package com.octacore.pgplibs;

import com.octacore.pgplibs.bc.IOUtil;
import com.octacore.pgplibs.bc.PGPObjectFactory2;
import com.octacore.pgplibs.exceptions.NoPrivateKeyFoundException;
import com.octacore.pgplibs.exceptions.NoPublicKeyFoundException;
import com.octacore.pgplibs.exceptions.WrongPasswordException;
import com.octacore.pgplibs.exceptions.WrongPrivateKeyException;

import org.spongycastle.bcpg.ArmoredInputStream;
import org.spongycastle.openpgp.PGPKeyRingGenerator;
import org.spongycastle.openpgp.PGPPublicKeyRing;
import org.spongycastle.openpgp.PGPSecretKeyRing;
import org.spongycastle.openpgp.PGPUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class PGPKeyPair extends KeyPairInformation implements Serializable {
    private static final long serialVersionUID = 2538773604623463978L;

    private PGPKeyPair() {
    }

    public PGPKeyPair(String var1) throws NoPublicKeyFoundException {
        try {
            this.importKeyFile(new FileInputStream(var1));
        } catch (PGPException var2) {
            throw new NoPublicKeyFoundException("The specified file does not contain an OpenPGP key.", var2);
        } catch (IOException var3) {
            throw new NoPublicKeyFoundException("The specified file does not contain an OpenPGP key.", var3);
        }
    }

    public PGPKeyPair(String var1, String var2) throws NoPublicKeyFoundException, WrongPrivateKeyException {
        try {
            this.importKeyFile(new FileInputStream(var1));
            this.importKeyFile(new FileInputStream(var2));
            if (this.getRawPrivateKeyRing().getPublicKey().getKeyID() != this.getRawPublicKeyRing().getPublicKey().getKeyID()) {
                throw new WrongPrivateKeyException("The specified private key does not belong to the public key");
            }
        } catch (PGPException var3) {
            throw new NoPublicKeyFoundException("The specified file does not contain an OpenPGP key.", var3);
        } catch (IOException var4) {
            throw new NoPublicKeyFoundException("The specified file does not contain an OpenPGP key.", var4);
        }
    }

    public String getAsciiVersionHeader() {
        return "Version: " + this.asciiVersionHeader;
    }

    public void setAsciiVersionHeader(String var1) {
        this.asciiVersionHeader = var1;
    }

    public static PGPKeyPair generateKeyPair(int var0, String var1, KeyAlgorithm var2, String var3, CompressionAlgorithm[] var4, HashAlgorithm[] var5, CypherAlgorithm[] var6, long var7) throws PGPException {
        PGPKeyPair var9 = new PGPKeyPair();
        PGPKeyRingGenerator var10;
        PGPSecretKeyRing var12 = (var10 = KeyStore.a(var0, var1, var2, var3, var4, var5, var6, var7)).generateSecretKeyRing();
        PGPPublicKeyRing var11 = var10.generatePublicKeyRing();
        var9.setPublicKeyRing(var11);
        var9.setPrivateKeyRing(var12);
        return var9;
    }

    protected void importKeyFile(InputStream var1) throws IOException, PGPException {
        var1 = PGPUtil.getDecoderStream(var1);

        try {
            if (var1 instanceof ArmoredInputStream) {
                ArmoredInputStream var2 = (ArmoredInputStream) var1;

                while (!var2.isEndOfStream()) {
                    if (!this.parseKeyStream(var2)) {
                        return;
                    }
                }
            } else if (!this.parseKeyStream(var1)) {
                return;
            }
        } finally {
            var1.close();
        }

    }

    protected boolean parseKeyStream(InputStream var1) throws PGPException, IOException {
        PGPObjectFactory2 var3;
        for (Object var2 = (var3 = new PGPObjectFactory2(var1)).nextObject(); var2 != null; var2 = var3.nextObject()) {
            if (var2 instanceof PGPPublicKeyRing) {
                PGPPublicKeyRing var4 = (PGPPublicKeyRing) var2;
                this.setPublicKeyRing(var4);
            } else {
                if (!(var2 instanceof PGPSecretKeyRing)) {
                    throw new PGPException("Unexpected object found in stream: " + var2.getClass().getName());
                }

                PGPSecretKeyRing var5 = (PGPSecretKeyRing) var2;
                this.setPrivateKeyRing(var5);
                if (this.getRawPublicKeyRing() == null) {
                    this.setPublicKeyRing(new PGPPublicKeyRing(var5.getPublicKey().getEncoded(), this.bcFactory.CreateKeyFingerPrintCalculator()));
                }
            }
        }

        return true;
    }

    public void changePrivateKeyPassword(String var1, String var2) throws WrongPasswordException, NoPrivateKeyFoundException, PGPException {
        PGPSecretKeyRing var3;
        if ((var3 = this.getRawPrivateKeyRing()) == null) {
            throw new NoPrivateKeyFoundException("This key pair has no private key component.");
        } else {
            int var4 = var3.getSecretKey().getKeyEncryptionAlgorithm();

            try {
                var3 = PGPSecretKeyRing.copyWithNewPassword(var3, this.bcFactory.CreatePBESecretKeyDecryptor(var1 == null ? new char[0] : var1.toCharArray()), this.bcFactory.CreatePBESecretKeyEncryptor(var2, var4));
            } catch (org.spongycastle.openpgp.PGPException var5) {
                if (var5.getMessage().startsWith("checksum mismatch at 0 of 2")) {
                    throw new WrongPasswordException(var5.getMessage(), var5.getUnderlyingException());
                }

                throw IOUtil.newPGPException(var5);
            }

            this.setPrivateKeyRing(var3);
        }
    }
}
