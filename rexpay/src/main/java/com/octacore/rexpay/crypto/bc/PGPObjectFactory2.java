package com.octacore.rexpay.crypto.bc;

import com.octacore.rexpay.crypto.BCFactory;

import org.bouncycastle.bcpg.BCPGInputStream;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPMarker;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPOnePassSignature;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class PGPObjectFactory2 extends PGPObjectFactory {
    private BCPGInputStream a;
    private BCFactory b;
    private boolean c;

    public PGPObjectFactory2(InputStream var1) {
        super(var1, (new BCFactory(false)).CreateKeyFingerPrintCalculator());
        this.b = new BCFactory(false);
        this.c = false;
        this.a = new BCPGInputStream(var1);
    }

    public PGPObjectFactory2(byte[] var1) {
        this((InputStream) (new ByteArrayInputStream(var1)));
    }

    public Object nextObject() throws IOException {
        try {
            ArrayList var1;
            switch (this.a.nextPacketTag()) {
                case -1:
                    return null;
                case 0:
                case 7:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                default:
                    break;
                case 1:
                case 3:
                    return new PGPEncryptedDataList(this.a);
                case 2:
                    var1 = new ArrayList();

                    while (this.a.nextPacketTag() == 2) {
                        try {
                            var1.add(ReflectionUtils.callPrivateConstrtuctor(PGPSignature.class, new Object[]{this.a}));
                        } catch (Exception var4) {
                            throw new IOException("can't create signature object: " + var4);
                        }
                    }

                    return new PGPSignatureList((PGPSignature[]) var1.toArray(new PGPSignature[var1.size()]));
                case 4:
                    var1 = new ArrayList();

                    while (this.a.nextPacketTag() == 4) {
                        try {
                            var1.add(ReflectionUtils.callPrivateConstrtuctor(PGPOnePassSignature.class, new Object[]{this.a}));
                        } catch (Exception var3) {
                            throw new IOException("can't create one pass signature object: " + var3);
                        }
                    }

                    return new PGPOnePassSignatureList((PGPOnePassSignature[]) var1.toArray(new PGPOnePassSignature[var1.size()]));
                case 5:
                    try {
                        return new PGPSecretKeyRing(this.a, this.b.CreateKeyFingerPrintCalculator());
                    } catch (PGPException var2) {
                        throw new IOException("can't create secret key object: " + var2);
                    }
                case 6:
                    return new PGPPublicKeyRing(this.a, this.b.CreateKeyFingerPrintCalculator());
                case 8:
                    return new PGPCompressedData(this.a);
                case 9:
                    return new PGP2xPBEEncryptedData(this.a);
                case 10:
                    return new PGPMarker(this.a);
                case 11:
                    return new PGPLiteralData(this.a);
                case 60:
                case 61:
                case 62:
                case 63:
                    return this.a.readPacket();
            }
        } catch (IOException var5) {
            if (this.c) {
                throw new UnknownKeyPacketsException(var5.getMessage());
            }

            throw var5;
        }

        if (this.c) {
            throw new UnknownKeyPacketsException("unknown object in stream " + this.a.nextPacketTag());
        } else {
            throw new IOException("unknown object in stream " + this.a.nextPacketTag());
        }
    }

    public boolean isLoadingKey() {
        return this.c;
    }

    public void setLoadingKey(boolean var1) {
        this.c = var1;
    }
}
