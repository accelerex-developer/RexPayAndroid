package com.octacore.pgplibs;

import com.octacore.pgplibs.bc.BCFactory;
import com.octacore.pgplibs.bc.BaseLib;
import com.octacore.pgplibs.bc.DirectByteArrayOutputStream;
import com.octacore.pgplibs.bc.IOUtil;
import com.octacore.pgplibs.bc.PGPObjectFactory2;
import com.octacore.pgplibs.exceptions.NonPGPDataException;
import com.octacore.pgplibs.exceptions.WrongPasswordException;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.BCPGOutputStream;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPMarker;
import org.bouncycastle.openpgp.PGPOnePassSignature;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.PGPV3SignatureGenerator;
import org.bouncycastle.openpgp.operator.PGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PGPLibs extends BaseLib {
    private BCFactory a = new BCFactory(false);
    private HashAlgorithm b;
    private CypherAlgorithm c;
    private CompressionAlgorithm d;
    private boolean e;
    private boolean f;
    private Logger g;
    private String h;
    private ContentDataType i;
    private Level j;
    private boolean k;
    private boolean l;
    private boolean m;
    private boolean n;

    public static String getVersion() {
        return "1.4.10";
    }

    public PGPLibs() {
        this.b = HashAlgorithm.SHA1;
        this.c = CypherAlgorithm.AES_256;
        this.d = CompressionAlgorithm.ZIP;
        this.e = false;
        this.f = false;
        this.g = Logger.getLogger(PGPLibs.class.getName());
        this.h = null;
        this.i = ContentDataType.BINARY;
        this.j = Level.FINE;
        this.k = false;
        this.l = true;
        this.m = false;
        this.n = true;
        this.h = "Octacore OpenPGP Library for Android";
    }

    public boolean isIntegrityProtectArchives() {
        return this.n;
    }

    public void setIntegrityProtectArchives(boolean var1) {
        this.n = var1;
    }

    public boolean isOverrideKeyAlgorithmPreferences() {
        return this.m;
    }

    public void setOverrideKeyAlgorithmPreferences(boolean var1) {
        this.m = var1;
    }

    public ContentDataType getContentType() {
        return this.i;
    }

    public void setContentType(ContentDataType var1) {
        this.i = var1;
        this.a("Content type set to {0}", this.i.toString());
    }

    public void setPgp2Compatible(boolean var1) {
        this.k = var1;
    }

    public Level getDebugLevel() {
        return this.j;
    }

    public void setDebuglevel(Level var1) {
        this.j = var1;
    }

    public boolean isUseExpiredKeys() {
        return this.e;
    }

    public void setUseExpiredKeys(boolean var1) {
        this.e = var1;
    }

    public boolean isUseRevokedKeys() {
        return this.f;
    }

    public void setUseRevokedKeys(boolean var1) {
        this.f = var1;
    }

    public void setHash(HashAlgorithm var1) {
        this.b = var1;
        this.a("Preferred hash set to {0}", this.b.toString());
    }

    public CypherAlgorithm getCypher() {
        return this.c;
    }

    public String getAsciiCommentHeader() {
        return "";
    }

    public String getAsciiVersionHeader() {
        return "Version: " + this.h;
    }

    public void setAsciiVersionHeader(String var1) {
        this.h = var1;
    }

    private void a(OutputStream var1) {
        if (var1 instanceof ArmoredOutputStream) {
            ((ArmoredOutputStream)var1).setHeader("Version", this.h);
        }
    }

    public void setCypher(CypherAlgorithm var1) {
        this.c = var1;
        this.a("Preferred cypher set to {0}", this.c.toString());
    }

    public CompressionAlgorithm getCompression() {
        return this.d;
    }

    public void setCompression(CompressionAlgorithm var1) {
        this.d = var1;
        this.a("Preferred compression set to {0}", this.d.toString());
    }

    public boolean detachedVerifyStream(InputStream var1, InputStream var2, InputStream var3) throws PGPException, IOException {
        var2 = PGPUtil.getDecoderStream(var2);
        PGPObjectFactory2 var9 = new PGPObjectFactory2(var2);

        Object var10;
        try {
            var10 = var9.nextObject();
        } catch (IOException var7) {
            throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var7);
        }

        if (!(var10 instanceof PGPSignatureList)) {
            throw new PGPException("Unknown message format: " + var10.getClass().getName());
        } else {
            PGPSignatureList var11 = (PGPSignatureList)var10;
            PGPSignature var4 = null;
            PGPPublicKey var5 = null;

            for(int var6 = 0; var6 < var11.size(); ++var6) {
                var4 = var11.get(var6);
                if ((var5 = readPublicVerificationKey(var3, var4.getKeyID())) != null) {
                    break;
                }
            }

            if (var5 == null) {
                return false;
            } else {
                try {
                    var4.init(this.a.CreatePGPContentVerifierBuilderProvider(), var5);
                    byte[] var13 = new byte[1048576];

                    int var12;
                    while((var12 = var1.read(var13, 0, var13.length)) > 0) {
                        var4.update(var13, 0, var12);
                    }

                    return var4.verify();
                } catch (PGPException var8) {
                    throw IOUtil.newPGPException(var8);
                }
            }
        }
    }

    public boolean detachedVerifyStream(InputStream var1, InputStream var2, KeyStore var3) throws PGPException, IOException {
        var2 = PGPUtil.getDecoderStream(var2);
        PGPObjectFactory2 var9 = new PGPObjectFactory2(var2);

        Object var10;
        try {
            var10 = var9.nextObject();
        } catch (IOException var7) {
            throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var7);
        }

        if (!(var10 instanceof PGPSignatureList)) {
            throw new PGPException("Unknown message format: " + var10.getClass().getName());
        } else {
            this.a("Detached signature found");
            PGPSignatureList var11 = (PGPSignatureList)var10;
            PGPSignature var4 = null;
            PGPPublicKey var5 = null;

            for(int var6 = 0; var6 < var11.size(); ++var6) {
                var4 = var11.get(var6);
                this.a("Detached signature for key ID {0}", KeyPairInformation.keyId2Hex(var4.getKeyID()));
                if ((var5 = readPublicVerificationKey(var3, var4.getKeyID())) != null) {
                    break;
                }
            }

            if (var5 == null) {
                this.a("No matching public key found");
                return false;
            } else {
                try {
                    var4.init(this.a.CreatePGPContentVerifierBuilderProvider(), var5);
                    byte[] var13 = new byte[1048576];

                    int var12;
                    while((var12 = var1.read(var13, 0, var13.length)) > 0) {
                        var4.update(var13, 0, var12);
                    }

                    if (var4.verify()) {
                        this.a("Signature verified");
                        return true;
                    } else {
                        this.a("Signature cannot be verified. Probably is tampered.");
                        return false;
                    }
                } catch (PGPException var8) {
                    throw IOUtil.newPGPException(var8);
                }
            }
        }
    }

    public SignatureCheckResult detachedVerify(String var1, String var2, String var3) throws PGPException, IOException {
        InputStream var4 = null;
        InputStream var5 = null;
        InputStream var6 = null;

        SignatureCheckResult var9;
        try {
            var4 = readFileOrAsciiString(var1, "message");
            var5 = readFileOrAsciiString(var3, "publicKeyFile");
            var6 = readFileOrAsciiString(var2, "detachedSignature");
            var9 = this.detachedVerify(var4, var6, var5);
        } finally {
            IOUtil.closeStream(var4);
            IOUtil.closeStream(var5);
            IOUtil.closeStream(var6);
        }

        return var9;
    }

    public SignatureCheckResult detachedVerify(String var1, String var2, KeyStore var3) throws PGPException, IOException {
        InputStream var4 = null;
        InputStream var5 = null;

        SignatureCheckResult var8;
        try {
            var4 = readFileOrAsciiString(var1, "message");
            var5 = readFileOrAsciiString(var2, "detachedSignature");
            var8 = this.detachedVerify(var4, var5, var3);
        } finally {
            IOUtil.closeStream(var4);
            IOUtil.closeStream(var5);
        }

        return var8;
    }

    public SignatureCheckResult detachedVerify(InputStream var1, InputStream var2, InputStream var3) throws PGPException, IOException {
        var2 = PGPUtil.getDecoderStream(var2);
        PGPObjectFactory2 var9 = new PGPObjectFactory2(var2);

        Object var10;
        try {
            var10 = var9.nextObject();
        } catch (IOException var7) {
            throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var7);
        }

        if (!(var10 instanceof PGPSignatureList)) {
            throw new PGPException("Unknown message format: " + var10.getClass().getName());
        } else {
            PGPSignatureList var11 = (PGPSignatureList)var10;
            PGPSignature var4 = null;
            PGPPublicKey var5 = null;

            for(int var6 = 0; var6 < var11.size(); ++var6) {
                var4 = var11.get(var6);
                if ((var5 = readPublicVerificationKey(var3, var4.getKeyID())) != null) {
                    break;
                }
            }

            if (var5 == null) {
                return SignatureCheckResult.PublicKeyNotMatching;
            } else {
                try {
                    var4.init(this.a.CreatePGPContentVerifierBuilderProvider(), var5);
                    byte[] var13 = new byte[1048576];

                    int var12;
                    while((var12 = var1.read(var13, 0, var13.length)) > 0) {
                        var4.update(var13, 0, var12);
                    }

                    return var4.verify() ? SignatureCheckResult.SignatureVerified : SignatureCheckResult.SignatureBroken;
                } catch (PGPException var8) {
                    throw IOUtil.newPGPException(var8);
                }
            }
        }
    }

    public SignatureCheckResult detachedVerify(InputStream var1, InputStream var2, KeyStore var3) throws PGPException, IOException {
        var2 = PGPUtil.getDecoderStream(var2);
        PGPObjectFactory2 var9 = new PGPObjectFactory2(var2);

        Object var10;
        try {
            var10 = var9.nextObject();
        } catch (IOException var7) {
            throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var7);
        }

        if (!(var10 instanceof PGPSignatureList)) {
            throw new PGPException("Unknown message format: " + var10.getClass().getName());
        } else {
            this.a("Detached signature found");
            PGPSignatureList var11 = (PGPSignatureList)var10;
            PGPSignature var4 = null;
            PGPPublicKey var5 = null;

            for(int var6 = 0; var6 < var11.size(); ++var6) {
                var4 = var11.get(var6);
                this.a("Detached signature for key ID {0}", KeyPairInformation.keyId2Hex(var4.getKeyID()));
                if ((var5 = readPublicVerificationKey(var3, var4.getKeyID())) != null) {
                    break;
                }
            }

            if (var5 == null) {
                this.a("No matching public key found");
                return SignatureCheckResult.PublicKeyNotMatching;
            } else {
                try {
                    var4.init(this.a.CreatePGPContentVerifierBuilderProvider(), var5);
                    byte[] var13 = new byte[1048576];

                    int var12;
                    while((var12 = var1.read(var13, 0, var13.length)) > 0) {
                        var4.update(var13, 0, var12);
                    }

                    if (var4.verify()) {
                        this.a("Signature verified");
                        return SignatureCheckResult.SignatureVerified;
                    } else {
                        this.a("Signature verified failed. Probably it was tampered.");
                        return SignatureCheckResult.SignatureBroken;
                    }
                } catch (PGPException var8) {
                    throw IOUtil.newPGPException(var8);
                }
            }
        }
    }

    public String detachedSignString(String var1, String var2, String var3) throws PGPException, IOException {
        FileInputStream var4 = null;
        InputStream var5 = null;
        DirectByteArrayOutputStream var6 = null;

        try {
            var4 = new FileInputStream(var1);
            var5 = readFileOrAsciiString(var2, "privateKeyFileName");
            var6 = new DirectByteArrayOutputStream(1048576);
            this.detachedSignStream(var4, var5, var3, var6, true);
            var1 = new String(var6.getArray(), 0, var6.size(), "UTF-8");
        } catch (PGPException var10) {
            throw var10;
        } catch (IOException var11) {
            throw var11;
        } finally {
            IOUtil.closeStream(var4);
            IOUtil.closeStream(var5);
            IOUtil.closeStream(var6);
        }

        return var1;
    }

    public void detachedSignFile(String var1, String var2, String var3, String var4, boolean var5) throws PGPException, IOException {
        FileInputStream var6 = null;
        InputStream var7 = null;
        FileOutputStream var8 = null;
        boolean var9 = false;
        boolean var13 = false;

        try {
            var13 = true;
            var6 = new FileInputStream(var1);
            var7 = readFileOrAsciiString(var2, "privateKeyFileName");
            var8 = new FileOutputStream(var4);
            this.detachedSignStream(var6, var7, var3, var8, var5);
            var13 = false;
        } catch (PGPException var14) {
            var9 = true;
            throw var14;
        } catch (IOException var15) {
            var9 = true;
            throw var15;
        } finally {
            if (var13) {
                IOUtil.closeStream(var6);
                IOUtil.closeStream(var7);
                IOUtil.closeStream(var8);
                File var17;
                if (var9 && (var17 = new File(var4)).exists()) {
                    var17.delete();
                }

            }
        }

        IOUtil.closeStream(var6);
        IOUtil.closeStream(var7);
        IOUtil.closeStream(var8);
    }

    public void detachedSignFile(String var1, KeyStore var2, String var3, String var4, String var5, boolean var6) throws PGPException, IOException {
        FileInputStream var7 = null;
        FileOutputStream var8 = null;
        boolean var9 = false;
        boolean var13 = false;

        try {
            var13 = true;
            var7 = new FileInputStream(var1);
            var8 = new FileOutputStream(var5);
            this.detachedSignStream(var7, var2, var3, var4, var8, var6);
            var13 = false;
        } catch (PGPException var14) {
            var9 = true;
            throw var14;
        } catch (IOException var15) {
            var9 = true;
            throw var15;
        } finally {
            if (var13) {
                IOUtil.closeStream(var7);
                IOUtil.closeStream(var8);
                File var17;
                if (var9 && (var17 = new File(var5)).exists()) {
                    var17.delete();
                }

            }
        }

        IOUtil.closeStream(var7);
        IOUtil.closeStream(var8);
    }

    public void detachedSignFile(String var1, KeyStore var2, long var3, String var5, String var6, boolean var7) throws PGPException, IOException {
        FileInputStream var8 = null;
        FileOutputStream var9 = null;
        boolean var10 = false;
        boolean var14 = false;

        try {
            var14 = true;
            var8 = new FileInputStream(var1);
            var9 = new FileOutputStream(var6);
            this.detachedSignStream(var8, var2, var3, var5, var9, var7);
            var14 = false;
        } catch (PGPException var15) {
            var10 = true;
            throw var15;
        } catch (IOException var16) {
            var10 = true;
            throw var16;
        } finally {
            if (var14) {
                IOUtil.closeStream(var8);
                IOUtil.closeStream(var9);
                File var18;
                if (var10 && (var18 = new File(var6)).exists()) {
                    var18.delete();
                }

            }
        }

        IOUtil.closeStream(var8);
        IOUtil.closeStream(var9);
    }

    public void detachedSignStream(InputStream var1, InputStream var2, String var3, OutputStream var4, boolean var5) throws PGPException, IOException {
        if (var5) {
            var4 = new ArmoredOutputStream((OutputStream)var4);
            this.a((OutputStream)var4);
        }

        try {
            PGPSecretKey var9;
            PGPPrivateKey var6 = extractPrivateKey(var9 = this.b(var2), var3);
            int var11 = KeyStore.a(this.b);
            if (this.k) {
                var11 = 1;
            }

            this.a("Signing with private key {0}", KeyPairInformation.keyId2Hex(var6.getKeyID()));
            PGPV3SignatureGenerator var12;
            (var12 = new PGPV3SignatureGenerator(this.a.CreatePGPContentSignerBuilder(var9.getPublicKey().getAlgorithm(), var11))).init(0, var6);
            BCPGOutputStream var10 = new BCPGOutputStream((OutputStream)var4);
            byte[] var13 = new byte[1048576];

            int var7;
            while((var7 = var1.read(var13, 0, var13.length)) > 0) {
                var12.update(var13, 0, var7);
            }

            IOUtil.closeStream(var1);
            var12.generate().encode(var10);
            IOUtil.closeStream(var10);
            ((OutputStream)var4).flush();
            if (var5) {
                IOUtil.closeStream((OutputStream)var4);
            }

        } catch (PGPException var8) {
            throw IOUtil.newPGPException(var8);
        }
    }

    public void detachedSignStream(InputStream var1, KeyStore var2, long var3, String var5, OutputStream var6, boolean var7) throws PGPException, IOException {
        InputStream var8 = d(var2, var3);
        this.detachedSignStream(var1, var8, var5, var6, var7);
    }

    public void detachedSignStream(InputStream var1, KeyStore var2, String var3, String var4, OutputStream var5, boolean var6) throws PGPException, IOException {
        InputStream var7 = b(var2, var3);
        this.detachedSignStream(var1, var7, var4, var5, var6);
    }

    public void clearSignFile(String var1, String var2, String var3, HashAlgorithm var4, String var5) throws PGPException, IOException, WrongPasswordException {
        this.a("Clear text signing file {0}", var1);
        this.a("Output file is {0}", var5);
        BufferedInputStream var6 = null;
        FileInputStream var7 = null;
        BufferedOutputStream var8 = null;
        boolean var9 = false;
        int var10;
        if ((var10 = KeyStore.a(var4)) < 0) {
            throw new InvalidParameterException("Wrong value for parameter 'hashingAlgorithm': " + var4 + ". Must be one of: SHA256, SHA384, SHA512, SHA224, SHA1, MD5, RIPEMD160, MD2");
        } else {
            boolean var14 = false;

            try {
                var14 = true;
                var6 = new BufferedInputStream(new FileInputStream(var1), 1048576);
                var7 = new FileInputStream(var2);
                var8 = new BufferedOutputStream(new FileOutputStream(var5), 1048576);
                PGPLibs.NamelessClass_1 var18 = new NamelessClass_1();
                PGPSecretKey var19 = this.b((InputStream)var7);
                var18.a(var6, var19, var3, var10, var8);
                var14 = false;
            } catch (PGPException var15) {
                var9 = true;
                throw IOUtil.newPGPException(var15);
            } catch (IOException var16) {
                var9 = true;
                throw var16;
            } finally {
                if (var14) {
                    IOUtil.closeStream(var6);
                    IOUtil.closeStream(var7);
                    IOUtil.closeStream(var8);
                    File var20;
                    if (var9 && (var20 = new File(var5)).exists()) {
                        var20.delete();
                    }

                }
            }

            IOUtil.closeStream(var6);
            IOUtil.closeStream(var7);
            IOUtil.closeStream(var8);
        }
    }

    public void clearSignFileVersion3(String var1, String var2, String var3, HashAlgorithm var4, String var5) throws PGPException, IOException, WrongPasswordException {
        BufferedInputStream var6 = null;
        FileInputStream var7 = null;
        BufferedOutputStream var8 = null;
        boolean var9 = false;
        int var10;
        if ((var10 = KeyStore.a(var4)) < 0) {
            throw new InvalidParameterException("Wrong value for parameter 'hashingAlgorithm': " + var4 + ". Must be one of: SHA256, SHA384, SHA512, SHA224, SHA1, MD5, RIPEMD160, MD2");
        } else {
            boolean var14 = false;

            try {
                var14 = true;
                var6 = new BufferedInputStream(new FileInputStream(var1), 1048576);
                var7 = new FileInputStream(var2);
                var8 = new BufferedOutputStream(new FileOutputStream(var5), 1048576);
                NamelessClass_1 var18 = new NamelessClass_1();
                PGPSecretKey var19 = this.b((InputStream)var7);
                var18.b(var6, var19, var3, var10, var8);
                var14 = false;
            } catch (PGPException var15) {
                var9 = true;
                throw IOUtil.newPGPException(var15);
            } catch (IOException var16) {
                var9 = true;
                throw var16;
            } finally {
                if (var14) {
                    IOUtil.closeStream(var6);
                    IOUtil.closeStream(var7);
                    IOUtil.closeStream(var8);
                    File var20;
                    if (var9 && (var20 = new File(var5)).exists()) {
                        var20.delete();
                    }

                }
            }

            IOUtil.closeStream(var6);
            IOUtil.closeStream(var7);
            IOUtil.closeStream(var8);
        }
    }

    public String clearSignString(String var1, String var2, String var3, HashAlgorithm var4) throws PGPException, IOException, WrongPasswordException {
        int var5;
        if ((var5 = KeyStore.a(var4)) < 0) {
            throw new InvalidParameterException("Wrong value for parameter 'hashingAlgorithm': " + var4 + ". Must be one of: SHA256, SHA384, SHA512, SHA224, SHA1, MD5, RIPEMD160, MD2");
        } else {
            FileInputStream var12 = null;

            try {
                var12 = new FileInputStream(var2);
                NamelessClass_1 var11 = new NamelessClass_1();
                PGPSecretKey var6 = this.b((InputStream)var12);
                var1 = var11.a(var1, var6, var3, var5);
            } catch (PGPException var9) {
                throw IOUtil.newPGPException(var9);
            } finally {
                IOUtil.closeStream(var12);
            }

            return var1;
        }
    }

    public String clearSignStringVersion3(String var1, String var2, String var3, HashAlgorithm var4) throws PGPException, IOException, WrongPasswordException {
        int var5;
        if ((var5 = KeyStore.a(var4)) < 0) {
            throw new InvalidParameterException("Wrong value for parameter 'hashingAlgorithm': " + var4 + ". Must be one of: SHA256, SHA384, SHA512, SHA224, SHA1, MD5, RIPEMD160, MD2");
        } else {
            FileInputStream var12 = null;

            try {
                var12 = new FileInputStream(var2);
                NamelessClass_1 var11 = new NamelessClass_1();
                PGPSecretKey var6 = this.b((InputStream)var12);
                var1 = var11.b(var1, var6, var3, var5);
            } catch (PGPException var9) {
                throw IOUtil.newPGPException(var9);
            } finally {
                IOUtil.closeStream(var12);
            }

            return var1;
        }
    }

    public void signFile(KeyStore var1, String var2, String var3, String var4, String var5) throws PGPException, WrongPasswordException, IOException {
        this.signFile(var1, var2, var1.getKeyIdForKeyIdHex(var3), var4, var5);
    }

    public void signFile(KeyStore var1, String var2, long var3, String var5, String var6) throws PGPException, WrongPasswordException, IOException {
        FileInputStream var7 = null;
        FileOutputStream var8 = null;
        boolean var9 = false;
        boolean var14 = false;

        try {
            var14 = true;
            File var10 = new File(var2);
            var7 = new FileInputStream(var2);
            var8 = new FileOutputStream(var6);
            PGPSecretKey var18 = var1.a.getSecretKey(var3);
            this.a(var7, var10.getName(), var18, var5, var8, false);
            var14 = false;
        } catch (PGPException var15) {
            var9 = true;
            throw IOUtil.newPGPException(var15);
        } catch (IOException var16) {
            var9 = true;
            throw var16;
        } finally {
            if (var14) {
                IOUtil.closeStream(var7);
                IOUtil.closeStream(var8);
                File var19;
                if (var9 && (var19 = new File(var6)).exists()) {
                    var19.delete();
                }

            }
        }

        IOUtil.closeStream(var7);
        IOUtil.closeStream(var8);
    }

    public void signFile(String var1, KeyStore var2, String var3, String var4, String var5, boolean var6) throws PGPException, WrongPasswordException, IOException {
        BufferedInputStream var7 = null;
        InputStream var8 = null;
        FileOutputStream var9 = null;
        boolean var10 = false;
        boolean var14 = false;

        try {
            var14 = true;
            var8 = b(var2, var3);
            var9 = new FileOutputStream(var5);
            PGPSecretKey var18 = this.b(var8);
            File var20 = new File(var1);
            var7 = new BufferedInputStream(new FileInputStream(var1));
            this.a(var7, var20.getName(), var18, var4, var9, var6);
            var14 = false;
        } catch (PGPException var15) {
            var10 = true;
            throw IOUtil.newPGPException(var15);
        } catch (IOException var16) {
            var10 = true;
            throw var16;
        } finally {
            if (var14) {
                IOUtil.closeStream(var8);
                IOUtil.closeStream(var7);
                IOUtil.closeStream(var9);
                File var19;
                if (var10 && (var19 = new File(var5)).exists()) {
                    var19.delete();
                }

            }
        }

        IOUtil.closeStream(var8);
        IOUtil.closeStream(var7);
        IOUtil.closeStream(var9);
    }

    public void signFile(String var1, KeyStore var2, long var3, String var5, String var6, boolean var7) throws PGPException, WrongPasswordException, IOException {
        BufferedInputStream var8 = null;
        InputStream var9 = null;
        FileOutputStream var10 = null;
        boolean var11 = false;
        boolean var15 = false;

        try {
            var15 = true;
            var9 = d(var2, var3);
            var10 = new FileOutputStream(var6);
            PGPSecretKey var19 = this.b(var9);
            File var21 = new File(var1);
            var8 = new BufferedInputStream(new FileInputStream(var1));
            this.a(var8, var21.getName(), var19, var5, var10, var7);
            var15 = false;
        } catch (PGPException var16) {
            var11 = true;
            throw IOUtil.newPGPException(var16);
        } catch (IOException var17) {
            var11 = true;
            throw var17;
        } finally {
            if (var15) {
                IOUtil.closeStream(var9);
                IOUtil.closeStream(var8);
                IOUtil.closeStream(var10);
                File var20;
                if (var11 && (var20 = new File(var6)).exists()) {
                    var20.delete();
                }

            }
        }

        IOUtil.closeStream(var9);
        IOUtil.closeStream(var8);
        IOUtil.closeStream(var10);
    }

    public void signFile(String var1, String var2, String var3, String var4, boolean var5) throws IOException, PGPException, WrongPasswordException {
        FileInputStream var6 = null;
        BufferedOutputStream var7 = null;
        boolean var8 = false;
        boolean var12 = false;

        try {
            var12 = true;
            var6 = new FileInputStream(var2);
            var7 = new BufferedOutputStream(new FileOutputStream(var4), 1048576);
            this.signFile(var1, (InputStream)var6, var3, (OutputStream)var7, var5);
            var12 = false;
        } catch (PGPException var13) {
            var8 = true;
            throw IOUtil.newPGPException(var13);
        } catch (IOException var14) {
            var8 = true;
            throw var14;
        } finally {
            if (var12) {
                IOUtil.closeStream(var6);
                IOUtil.closeStream(var7);
                File var16;
                if (var8 && (var16 = new File(var4)).exists()) {
                    var16.delete();
                }

            }
        }

        IOUtil.closeStream(var6);
        IOUtil.closeStream(var7);
    }

    public void signFile(String var1, InputStream var2, String var3, OutputStream var4, boolean var5) throws IOException, PGPException, WrongPasswordException {
        PGPSecretKey var11 = this.b(var2);
        BufferedInputStream var6 = null;

        try {
            var6 = new BufferedInputStream(new FileInputStream(var1));
            this.a(var6, (new File(var1)).getName(), var11, var3, var4, var5);
        } catch (PGPException var9) {
            throw IOUtil.newPGPException(var9);
        } finally {
            IOUtil.closeStream(var6);
            IOUtil.closeStream(var4);
        }

    }

    public String signString(String var1, String var2, String var3) throws PGPException, IOException {
        InputStream var4 = null;

        try {
            var4 = readFileOrAsciiString(var2, "privateKeyFile");
            var1 = this.signString(var1, var4, var3, "UTF-8");
        } finally {
            IOUtil.closeStream(var4);
        }

        return var1;
    }

    public String signString(String var1, String var2, String var3, String var4) throws PGPException, IOException {
        InputStream var5 = null;

        try {
            var5 = readFileOrAsciiString(var2, "privateKeyFile");
            var1 = this.signString(var1, var5, var3, var4);
        } finally {
            IOUtil.closeStream(var5);
        }

        return var1;
    }

    public String signString(String var1, InputStream var2, String var3, String var4) throws PGPException, IOException {
        ByteArrayInputStream var5 = null;

        try {
            DirectByteArrayOutputStream var6 = new DirectByteArrayOutputStream(1048576);
            var5 = new ByteArrayInputStream(var1.getBytes(var4));
            this.signStream(var5, "message.txt", var2, var3, var6, true);
            var1 = new String(var6.getArray(), 0, var6.size(), "UTF-8");
        } finally {
            IOUtil.closeStream(var5);
        }

        return var1;
    }

    public String signString(String var1, InputStream var2, String var3) throws PGPException, IOException {
        return this.signString(var1, var2, var3, "UTF-8");
    }

    public String signString(String var1, KeyStore var2, String var3, String var4, String var5) throws PGPException, IOException {
        ByteArrayInputStream var6 = null;

        try {
            DirectByteArrayOutputStream var7 = new DirectByteArrayOutputStream(1048576);
            var6 = new ByteArrayInputStream(var1.getBytes(var5));
            this.signStream(var6, "message.txt", var2, var3, var4, var7, true);
            var1 = new String(var7.getArray(), 0, var7.size(), "UTF-8");
        } finally {
            IOUtil.closeStream(var6);
        }

        return var1;
    }

    public String signString(String var1, KeyStore var2, long var3, String var5, String var6) throws PGPException, IOException {
        ByteArrayInputStream var7 = null;

        try {
            DirectByteArrayOutputStream var8 = new DirectByteArrayOutputStream(1048576);
            var7 = new ByteArrayInputStream(var1.getBytes(var6));
            this.signStream(var7, "message.txt", var2, var3, var5, var8, true);
            var1 = new String(var8.getArray(), 0, var8.size(), "UTF-8");
        } finally {
            IOUtil.closeStream(var7);
        }

        return var1;
    }

    public void signStream(InputStream var1, String var2, InputStream var3, String var4, OutputStream var5, boolean var6) throws IOException, PGPException, WrongPasswordException {
        PGPSecretKey var11 = this.b(var3);

        try {
            this.a(var1, var2, var11, var4, var5, var6);
        } catch (PGPException var9) {
            throw IOUtil.newPGPException(var9);
        } finally {
            IOUtil.closeStream(var1);
            IOUtil.closeStream(var5);
        }

    }

    public void signStream(InputStream var1, String var2, KeyStore var3, String var4, String var5, OutputStream var6, boolean var7) throws IOException, PGPException, WrongPasswordException {
        InputStream var8 = null;

        try {
            var8 = b(var3, var4);
            PGPSecretKey var13 = this.b(var8);
            this.a(var1, var2, var13, var5, var6, var7);
        } catch (PGPException var11) {
            throw IOUtil.newPGPException(var11);
        } finally {
            IOUtil.closeStream(var1);
            IOUtil.closeStream(var6);
            IOUtil.closeStream(var8);
        }

    }

    public void signStream(InputStream var1, String var2, KeyStore var3, long var4, String var6, OutputStream var7, boolean var8) throws IOException, PGPException, WrongPasswordException {
        InputStream var9 = null;

        try {
            var9 = d(var3, var4);
            PGPSecretKey var14 = this.b(var9);
            this.a(var1, var2, var14, var6, var7, var8);
        } catch (PGPException var12) {
            throw IOUtil.newPGPException(var12);
        } finally {
            IOUtil.closeStream(var1);
            IOUtil.closeStream(var7);
            IOUtil.closeStream(var9);
        }

    }

    public void signFileVersion3(String var1, String var2, String var3, String var4, boolean var5) throws IOException, PGPException, WrongPasswordException {
        BufferedInputStream var6 = null;
        InputStream var7 = null;
        BufferedOutputStream var8 = null;
        boolean var9 = false;
        boolean var13 = false;

        try {
            var13 = true;
            var6 = new BufferedInputStream(new FileInputStream(var1), 1048576);
            var7 = readFileOrAsciiString(var2, "privateKeyFileName");
            var8 = new BufferedOutputStream(new FileOutputStream(var4), 1048576);
            this.signStreamVersion3(var6, (new File(var1)).getName(), var7, var3, var8, var5);
            var13 = false;
        } catch (PGPException var14) {
            var9 = true;
            throw IOUtil.newPGPException(var14);
        } catch (IOException var15) {
            var9 = true;
            throw var15;
        } finally {
            if (var13) {
                IOUtil.closeStream(var6);
                IOUtil.closeStream(var7);
                IOUtil.closeStream(var8);
                File var17;
                if (var9 && (var17 = new File(var4)).exists()) {
                    var17.delete();
                }

            }
        }

        IOUtil.closeStream(var6);
        IOUtil.closeStream(var7);
        IOUtil.closeStream(var8);
    }

    public void signStreamVersion3(InputStream var1, String var2, InputStream var3, String var4, OutputStream var5, boolean var6) throws IOException, PGPException, WrongPasswordException {
        if (var6) {
            var5 = new ArmoredOutputStream((OutputStream)var5);
            this.a((OutputStream)var5);
        }

        PGPSecretKey var14 = this.b(var3);
        this.b(var14.getPublicKey());
        this.c(var14.getPublicKey());
        PGPPrivateKey var17 = extractPrivateKey(var14, var4);
        int var7 = this.e(var14.getPublicKey());
        PGPContentSignerBuilder var20 = this.a.CreatePGPContentSignerBuilder(var14.getPublicKey().getAlgorithm(), var7);
        PGPSignatureGenerator var21 = new PGPSignatureGenerator(var20);

        try {
            var21.init(0, var17);
        } catch (PGPException var12) {
            throw IOUtil.newPGPException(var12);
        }

        Iterator var15;
        if ((var15 = var14.getPublicKey().getUserIDs()).hasNext()) {
            PGPSignatureSubpacketGenerator var18;
            (var18 = new PGPSignatureSubpacketGenerator()).setSignerUserID(false, (String)var15.next());
            var21.setHashedSubpackets(var18.generate());
        }

        PGPCompressedDataGenerator var19 = new PGPCompressedDataGenerator(2);
        BCPGOutputStream var16 = new BCPGOutputStream(var19.open((OutputStream)var5));
        DirectByteArrayOutputStream var8 = new DirectByteArrayOutputStream(1048576);
        byte[] var10 = new byte[1048576];

        int var9;
        while((var9 = var1.read(var10)) >= 0) {
            var8.write(var10, 0, var9);
            var21.update(var10, 0, var9);
        }

        try {
            var21.generate().encode(var16);
        } catch (PGPException var11) {
            throw IOUtil.newPGPException(var11);
        }

        this.a("Signing content with file name label {0}. (version 3, old style signature)", var2);
        OutputStream var13;
        PGPLiteralDataGenerator var22;
        (var13 = (var22 = new PGPLiteralDataGenerator()).open(var16, this.getContentType().getCode(), var2, (long)var8.size(), new Date())).write(var8.getArray(), 0, var8.size());
        var22.close();
        var19.close();
        IOUtil.closeStream(var1);
        IOUtil.closeStream(var8);
        IOUtil.closeStream(var13);
        IOUtil.closeStream(var16);
        ((OutputStream)var5).flush();
        if (var6) {
            IOUtil.closeStream((OutputStream)var5);
        }

    }

    public void signAndEncryptFile(String var1, String var2, String var3, String var4, String var5, boolean var6) throws PGPException, WrongPasswordException, IOException {
        this.signAndEncryptFile(var1, var2, var3, var4, var5, var6, this.n);
    }

    public void signAndEncryptFile(String var1, String var2, String var3, String var4, String var5, boolean var6, boolean var7) throws PGPException, WrongPasswordException, IOException {
        this.a("Signing file {0}", var1);
        InputStream var8 = null;
        BufferedOutputStream var9 = null;
        InputStream var10 = null;
        BufferedInputStream var11 = null;
        boolean var12 = false;
        boolean var16 = false;

        try {
            var16 = true;
            var8 = readFileOrAsciiString(var2, "privateKeyFileName");
            var9 = new BufferedOutputStream(new FileOutputStream(var5), 1048576);
            var11 = new BufferedInputStream(new FileInputStream(var1), 1048576);
            var10 = readFileOrAsciiString(var4, "publicKeyFile");
            this.signAndEncryptStream(var11, (new File(var1)).getName(), var8, var3, (InputStream)var10, var9, var6, var7);
            var16 = false;
        } catch (PGPException var17) {
            var12 = true;
            throw IOUtil.newPGPException(var17);
        } catch (IOException var18) {
            var12 = true;
            throw var18;
        } finally {
            if (var16) {
                IOUtil.closeStream(var8);
                IOUtil.closeStream(var9);
                IOUtil.closeStream(var10);
                IOUtil.closeStream(var11);
                File var20;
                if (var12 && (var20 = new File(var5)).exists()) {
                    var20.delete();
                }

            }
        }

        IOUtil.closeStream(var8);
        IOUtil.closeStream(var9);
        IOUtil.closeStream(var10);
        IOUtil.closeStream(var11);
    }

    public void signAndEncryptFile(String var1, String var2, String var3, String[] var4, String var5, boolean var6, boolean var7) throws PGPException, WrongPasswordException, IOException {
        this.a("Signing file {0}", var1);
        InputStream var8 = null;
        BufferedOutputStream var9 = null;
        InputStream[] var10 = new InputStream[var4.length];
        BufferedInputStream var11 = null;
        boolean var12 = false;
        boolean var16 = false;

        int var20;
        try {
            var16 = true;
            var8 = readFileOrAsciiString(var2, "privateKeyFileName");
            var9 = new BufferedOutputStream(new FileOutputStream(var5), 1048576);
            var11 = new BufferedInputStream(new FileInputStream(var1), 1048576);
            var20 = 0;

            while(true) {
                if (var20 >= var4.length) {
                    this.signAndEncryptStream(var11, (new File(var1)).getName(), var8, var3, (InputStream[])var10, var9, var6, var7);
                    var16 = false;
                    break;
                }

                var10[var20] = readFileOrAsciiString(var4[var20], "publicKeyFiles: " + var20);
                ++var20;
            }
        } catch (PGPException var17) {
            var12 = true;
            throw IOUtil.newPGPException(var17);
        } catch (IOException var18) {
            var12 = true;
            throw var18;
        } finally {
            if (var16) {
                IOUtil.closeStream(var8);
                IOUtil.closeStream(var9);

                for(var20 = 0; var20 < var10.length; ++var20) {
                    IOUtil.closeStream(var10[var20]);
                }

                IOUtil.closeStream(var11);
                File var21;
                if (var12 && (var21 = new File(var5)).exists()) {
                    var21.delete();
                }

            }
        }

        IOUtil.closeStream(var8);
        IOUtil.closeStream(var9);

        for(var20 = 0; var20 < var10.length; ++var20) {
            IOUtil.closeStream(var10[var20]);
        }

        IOUtil.closeStream(var11);
    }

    public void signAndEncryptFile(String var1, InputStream var2, String var3, InputStream var4, OutputStream var5, boolean var6, boolean var7) throws IOException, PGPException, WrongPasswordException {
        BufferedInputStream var8 = null;
        File var9 = new File(var1);
        this.a("Signing file {0}", var1);

        try {
            var8 = new BufferedInputStream(new FileInputStream(var9));
            this.signAndEncryptStream(var8, var9.getName(), var2, var3, (InputStream)var4, var5, var6, var7);
        } finally {
            IOUtil.closeStream(var8);
            IOUtil.closeStream(var5);
        }

    }

    public void signAndEncryptStream(InputStream var1, String var2, InputStream var3, String var4, InputStream var5, OutputStream var6, boolean var7, boolean var8) throws IOException, PGPException, WrongPasswordException, NoPublicKeyFoundException, NoPrivateKeyFoundException {
        if (this.k) {
            InputStream[] var10005 = new InputStream[]{(InputStream)var5};
            new Date();
            this.a(var1, var2, (InputStream)var3, var4, var10005, (OutputStream)var6, var7);
        } else {
            if (!(var6 instanceof BufferedOutputStream)) {
                var6 = new BufferedOutputStream((OutputStream)var6, 1048576);
            }

            if (!((InputStream)var5).markSupported()) {
                var5 = new BufferedInputStream((InputStream)var5);
            }

            if (!((InputStream)var3).markSupported()) {
                var3 = new BufferedInputStream((InputStream)var3);
            }

            ((InputStream)var5).mark(1048576);
            ((InputStream)var3).mark(1048576);
            PGPPublicKey var9 = this.a((InputStream)var5);
            PGPSecretKey var10 = this.b((InputStream)var3);
            if (var9.getVersion() == 3) {
                ((InputStream)var5).reset();
                ((InputStream)var3).reset();
                this.a("Swithcing to version 3 signatures");
                this.signAndEncryptStreamVersion3(var1, var2, (InputStream)var3, var4, (InputStream)var5, (OutputStream)var6, var7, var8);
            } else {
                var3 = null;

                try {
                    if (var7) {
                        var3 = var6;
                        var6 = new ArmoredOutputStream((OutputStream)var6);
                        this.a((OutputStream)var6);
                    }

                    int var22 = this.d(var9);
                    this.a("Encrypting with cipher {0}", KeyStore.c(var22));
                    JcePGPDataEncryptorBuilder var11;
                    (var11 = new JcePGPDataEncryptorBuilder(var22)).setSecureRandom(a());
                    var11.setWithIntegrityPacket(var8);
                    PGPEncryptedDataGenerator var23;
                    (var23 = new PGPEncryptedDataGenerator(this.a.CreatePGPDataEncryptorBuilder(var22, var8, a()))).addMethod(this.a.CreatePublicKeyKeyEncryptionMethodGenerator(var9));
                    OutputStream var24 = var23.open((OutputStream)var6, new byte[65536]);
                    PGPPrivateKey var19 = extractPrivateKey(var10, var4);
                    int var29 = this.e(var10.getPublicKey());
                    this.a("Signing with hash {0}", KeyStore.b(var29));
                    PGPSignatureGenerator var30;
                    (var30 = new PGPSignatureGenerator(this.a.CreatePGPContentSignerBuilder(var10.getPublicKey().getAlgorithm(), var29))).init(this.getContentType().getCode(), var19);
                    Iterator var20 = var10.getPublicKey().getUserIDs();

                    while(true) {
                        if (!var20.hasNext()) {
                            int var27 = this.f(var9);
                            PGPCompressedDataGenerator var31 = new PGPCompressedDataGenerator(var27);
                            OutputStream var21 = null;
                            PGPLiteralDataGenerator var25 = new PGPLiteralDataGenerator();
                            OutputStream var28;
                            if (var27 == 0) {
                                this.a("No compression.");
                                var30.generateOnePassVersion(false).encode(var24);
                                var28 = var25.open(var24, this.getContentType().getCode(), var2, new Date(), new byte[1048576]);
                            } else {
                                this.a("Compression is {0}", KeyStore.a(var27));
                                var21 = var31.open(var24, new byte[65536]);
                                var30.generateOnePassVersion(false).encode(var21);
                                var28 = var25.open(var21, this.getContentType().getCode(), var2, new Date(), new byte[1048576]);
                            }

                            this.a("Signing stream content with internal file name label: {0}", var2);
                            byte[] var18 = new byte[65536];

                            int var13;
                            while((var13 = var1.read(var18, 0, var18.length)) != -1) {
                                var28.write(var18, 0, var13);
                                var30.update(var18, 0, var13);
                            }

                            IOUtil.closeStream(var28);
                            var25.close();
                            if (var21 == null) {
                                var30.generate().encode(var24);
                            } else {
                                var30.generate().encode(var21);
                            }

                            IOUtil.closeStream(var21);
                            var31.close();
                            IOUtil.closeStream(var24);
                            var23.close();
                            IOUtil.closeStream(var1);
                            if (var7) {
                                ((OutputStream)var6).close();
                            }
                            break;
                        }

                        String var26 = (String)var20.next();
                        PGPSignatureSubpacketGenerator var12 = new PGPSignatureSubpacketGenerator();
                        this.a("Signing with User ID {0}", var26);
                        var12.setSignerUserID(false, var26);
                        var30.setHashedSubpackets(var12.generate());
                    }
                } catch (PGPException var16) {
                    throw IOUtil.newPGPException(var16);
                } finally {
                    if (var7) {
                        IOUtil.closeStream((OutputStream)var6);
                        ((OutputStream)var3).flush();
                    } else {
                        ((OutputStream)var6).flush();
                    }

                }

            }
        }
    }

    public void signAndEncryptStream(InputStream var1, String var2, InputStream var3, String var4, InputStream[] var5, OutputStream var6, boolean var7, boolean var8) throws IOException, PGPException, WrongPasswordException, NoPublicKeyFoundException, NoPrivateKeyFoundException {
        if (this.k) {
            new Date();
            this.a(var1, var2, var3, var4, var5, (OutputStream)var6, var7);
        } else {
            try {
                if (var7) {
                    var6 = new ArmoredOutputStream((OutputStream)var6);
                    this.a((OutputStream)var6);
                }

                int var9 = KeyStore.a(this.c);
                PGPEncryptedDataGenerator var23 = new PGPEncryptedDataGenerator(this.a.CreatePGPDataEncryptorBuilder(var9, var8, a()));

                for(var9 = 0; var9 < var5.length; ++var9) {
                    PGPPublicKey var10 = this.a(var5[var9]);
                    this.a("Encrypting with public key {0}", KeyPairInformation.keyId2Hex(var10.getKeyID()));
                    var23.addMethod(this.a.CreatePublicKeyKeyEncryptionMethodGenerator(var10));
                }

                OutputStream var24 = var23.open((OutputStream)var6, new byte[65536]);
                PGPSecretKey var25;
                PGPPrivateKey var15 = extractPrivateKey(var25 = this.b(var3), var4);
                int var18 = this.e(var25.getPublicKey());
                this.a("Signing with hash {0}", KeyStore.b(var18));
                PGPSignatureGenerator var19;
                (var19 = new PGPSignatureGenerator(this.a.CreatePGPContentSignerBuilder(var25.getPublicKey().getAlgorithm(), var18))).init(this.getContentType().getCode(), var15);
                Iterator var16 = var25.getPublicKey().getUserIDs();

                while(var16.hasNext()) {
                    String var20 = (String)var16.next();
                    PGPSignatureSubpacketGenerator var26;
                    (var26 = new PGPSignatureSubpacketGenerator()).setSignerUserID(false, var20);
                    var19.setHashedSubpackets(var26.generate());
                }

                int var21 = KeyStore.a(this.d);
                PGPCompressedDataGenerator var27 = new PGPCompressedDataGenerator(var21);
                OutputStream var17 = null;
                PGPLiteralDataGenerator var11 = new PGPLiteralDataGenerator();
                OutputStream var22;
                if (var21 == 0) {
                    this.a("No Compression.");
                    var19.generateOnePassVersion(false).encode(var24);
                    var22 = var11.open(var24, this.getContentType().getCode(), var2, new Date(), new byte[1048576]);
                } else {
                    this.a("Compression is {0}", KeyStore.a(var21));
                    var17 = var27.open(var24, new byte[65536]);
                    var19.generateOnePassVersion(false).encode(var17);
                    var22 = var11.open(var17, this.getContentType().getCode(), var2, new Date(), new byte[1048576]);
                }

                this.a("Signing stream content with internal file name label: {0}", var2);
                byte[] var14 = new byte[65536];

                int var12;
                while((var12 = var1.read(var14, 0, var14.length)) != -1) {
                    var22.write(var14, 0, var12);
                    var19.update(var14, 0, var12);
                }

                IOUtil.closeStream(var22);
                var11.close();
                if (var17 == null) {
                    var19.generate().encode(var24);
                } else {
                    var19.generate().encode(var17);
                }

                IOUtil.closeStream(var17);
                var27.close();
                IOUtil.closeStream(var24);
                var23.close();
                IOUtil.closeStream(var1);
                if (var7) {
                    ((OutputStream)var6).close();
                }

            } catch (PGPException var13) {
                throw IOUtil.newPGPException(var13);
            }
        }
    }

    public void signAndEncryptFile(String var1, KeyStore var2, String var3, String var4, String var5, String var6, boolean var7, boolean var8) throws IOException, PGPException {
        InputStream var9 = null;
        InputStream var10 = null;
        BufferedInputStream var11 = null;
        BufferedOutputStream var12 = null;
        boolean var13 = false;
        this.a("Signing and encrypting file {0}", var1);
        this.a("Output file is {0}", (new File(var6)).getAbsolutePath());
        boolean var17 = false;

        File var21;
        try {
            var17 = true;
            var9 = a(var2, var5);
            var10 = b(var2, var3);
            var21 = new File(var1);
            var11 = new BufferedInputStream(new FileInputStream(var1), 1048576);
            var12 = new BufferedOutputStream(new FileOutputStream(new File(var6)), 1048576);
            this.signAndEncryptStream(var11, var21.getName(), var10, var4, (InputStream)var9, var12, var7, var8);
            var17 = false;
        } catch (PGPException var18) {
            var13 = true;
            throw IOUtil.newPGPException(var18);
        } catch (IOException var19) {
            var13 = true;
            throw var19;
        } finally {
            if (var17) {
                IOUtil.closeStream(var11);
                IOUtil.closeStream(var12);
                IOUtil.closeStream(var10);
                IOUtil.closeStream(var9);
                if (var13 && (var21 = new File(var6)).exists()) {
                    var21.delete();
                }

            }
        }

        IOUtil.closeStream(var11);
        IOUtil.closeStream(var12);
        IOUtil.closeStream(var10);
        IOUtil.closeStream(var9);
    }

    public void signAndEncryptFile(String var1, KeyStore var2, String var3, String var4, String[] var5, String var6, boolean var7, boolean var8) throws IOException, PGPException {
        InputStream[] var9 = new InputStream[var5.length];
        InputStream var10 = null;
        BufferedInputStream var11 = null;
        BufferedOutputStream var12 = null;
        boolean var13 = false;
        this.a("Signing and encrypting file {0}", var1);
        this.a("Output file is {0}", (new File(var6)).getAbsolutePath());
        boolean var18 = false;

        int var14;
        try {
            var18 = true;
            var14 = 0;

            while(true) {
                if (var14 >= var9.length) {
                    var10 = b(var2, var3);
                    File var24 = new File(var1);
                    var11 = new BufferedInputStream(new FileInputStream(var1), 1048576);
                    var12 = new BufferedOutputStream(new FileOutputStream(var6), 1048576);
                    this.signAndEncryptStream(var11, var24.getName(), var10, var4, (InputStream[])var9, var12, var7, var8);
                    var18 = false;
                    break;
                }

                var9[var14] = a(var2, var5[var14]);
                ++var14;
            }
        } catch (PGPException var19) {
            var13 = true;
            throw IOUtil.newPGPException(var19);
        } catch (IOException var20) {
            var13 = true;
            throw var20;
        } finally {
            if (var18) {
                IOUtil.closeStream(var11);
                IOUtil.closeStream(var12);
                IOUtil.closeStream(var10);

                for(int var22 = 0; var22 < var9.length; ++var22) {
                    IOUtil.closeStream(var9[var22]);
                }

                File var23;
                if (var13 && (var23 = new File(var6)).exists()) {
                    var23.delete();
                }

            }
        }

        IOUtil.closeStream(var11);
        IOUtil.closeStream(var12);
        IOUtil.closeStream(var10);

        for(var14 = 0; var14 < var9.length; ++var14) {
            IOUtil.closeStream(var9[var14]);
        }

    }

    public void signAndEncryptFile(String var1, KeyStore var2, long var3, String var5, long[] var6, String var7, boolean var8, boolean var9) throws IOException, PGPException {
        InputStream[] var10 = new InputStream[var6.length];
        InputStream var11 = null;
        BufferedInputStream var12 = null;
        BufferedOutputStream var13 = null;
        boolean var14 = false;
        this.a("Signing and encrypting file {0}", var1);
        this.a("Output file is {0}", (new File(var7)).getAbsolutePath());
        boolean var19 = false;

        int var15;
        try {
            var19 = true;
            var15 = 0;

            while(true) {
                if (var15 >= var10.length) {
                    var11 = d(var2, var3);
                    File var25 = new File(var1);
                    var12 = new BufferedInputStream(new FileInputStream(var1), 1048576);
                    var13 = new BufferedOutputStream(new FileOutputStream(var7), 1048576);
                    this.signAndEncryptStream(var12, var25.getName(), var11, var5, (InputStream[])var10, var13, var8, var9);
                    var19 = false;
                    break;
                }

                var10[var15] = c(var2, var6[var15]);
                ++var15;
            }
        } catch (PGPException var20) {
            var14 = true;
            throw IOUtil.newPGPException(var20);
        } catch (IOException var21) {
            var14 = true;
            throw var21;
        } finally {
            if (var19) {
                IOUtil.closeStream(var12);
                IOUtil.closeStream(var13);
                IOUtil.closeStream(var11);

                for(int var23 = 0; var23 < var10.length; ++var23) {
                    IOUtil.closeStream(var10[var23]);
                }

                File var24;
                if (var14 && (var24 = new File(var7)).exists()) {
                    var24.delete();
                }

            }
        }

        IOUtil.closeStream(var12);
        IOUtil.closeStream(var13);
        IOUtil.closeStream(var11);

        for(var15 = 0; var15 < var10.length; ++var15) {
            IOUtil.closeStream(var10[var15]);
        }

    }

    public void signAndEncryptFile(String var1, KeyStore var2, long var3, String var5, long var6, String var8, boolean var9, boolean var10) throws IOException, PGPException {
        InputStream var11 = null;
        InputStream var12 = null;
        BufferedInputStream var13 = null;
        BufferedOutputStream var14 = null;
        this.a("Signing and encrypting file {0}", var1);
        this.a("Output file is {0}", (new File(var8)).getAbsolutePath());
        boolean var15 = false;
        boolean var19 = false;

        File var23;
        try {
            var19 = true;
            var11 = c(var2, var6);
            var12 = d(var2, var3);
            var23 = new File(var1);
            var13 = new BufferedInputStream(new FileInputStream(var1), 1048576);
            var14 = new BufferedOutputStream(new FileOutputStream(var8), 1048576);
            this.signAndEncryptStream(var13, var23.getName(), var12, var5, (InputStream)var11, var14, var9, var10);
            var19 = false;
        } catch (PGPException var20) {
            var15 = true;
            throw IOUtil.newPGPException(var20);
        } catch (IOException var21) {
            var15 = true;
            throw var21;
        } finally {
            if (var19) {
                IOUtil.closeStream(var13);
                IOUtil.closeStream(var14);
                IOUtil.closeStream(var12);
                IOUtil.closeStream(var11);
                if (var15 && (var23 = new File(var8)).exists()) {
                    var23.delete();
                }

            }
        }

        IOUtil.closeStream(var13);
        IOUtil.closeStream(var14);
        IOUtil.closeStream(var12);
        IOUtil.closeStream(var11);
    }

    public void signAndEncryptStream(InputStream var1, String var2, KeyStore var3, String var4, String var5, String var6, OutputStream var7, boolean var8, boolean var9) throws IOException, PGPException {
        try {
            InputStream var12 = a(var3, var6);
            InputStream var11 = b(var3, var4);
            this.signAndEncryptStream(var1, var2, var11, var5, var12, var7, var8, var9);
        } catch (PGPException var10) {
            throw IOUtil.newPGPException(var10);
        }
    }

    public void signAndEncryptStream(InputStream var1, String var2, KeyStore var3, String var4, String var5, String[] var6, OutputStream var7, boolean var8, boolean var9) throws IOException, PGPException {
        InputStream[] var10 = new InputStream[var6.length];

        try {
            InputStream var13 = b(var3, var4);

            for(int var11 = 0; var11 < var6.length; ++var11) {
                var10[var11] = a(var3, var6[var11]);
            }

            this.signAndEncryptStream(var1, var2, var13, var5, var10, var7, var8, var9);
        } catch (PGPException var12) {
            throw IOUtil.newPGPException(var12);
        }
    }

    public void signAndEncryptStream(InputStream var1, String var2, KeyStore var3, String var4, String var5, long[] var6, OutputStream var7, boolean var8, boolean var9) throws IOException, PGPException {
        InputStream[] var10 = new InputStream[var6.length];

        try {
            InputStream var13 = b(var3, var4);

            for(int var11 = 0; var11 < var6.length; ++var11) {
                var10[var11] = c(var3, var6[var11]);
            }

            this.signAndEncryptStream(var1, var2, var13, var5, var10, var7, var8, var9);
        } catch (PGPException var12) {
            throw IOUtil.newPGPException(var12);
        }
    }

    public void signAndEncryptFileVersion3(String var1, String var2, String var3, String var4, String var5, boolean var6) throws PGPException, IOException {
        BufferedInputStream var7 = null;
        InputStream var8 = null;
        InputStream var9 = null;
        BufferedOutputStream var10 = null;
        boolean var11 = false;
        this.a("Signing and encrypting file {0}", var1);
        this.a("Output file is {0}", (new File(var5)).getAbsolutePath());
        boolean var15 = false;

        try {
            var15 = true;
            var7 = new BufferedInputStream(new FileInputStream(var1), 1048576);
            var8 = readFileOrAsciiString(var2, "privateKeyFileName");
            var10 = new BufferedOutputStream(new FileOutputStream(var5), 1048576);
            var9 = readFileOrAsciiString(var4, "publicKeyFile");
            this.signAndEncryptStreamVersion3(var7, (new File(var1)).getName(), var8, var3, var9, var10, var6, this.n);
            var15 = false;
        } catch (PGPException var16) {
            var11 = true;
            throw IOUtil.newPGPException(var16);
        } catch (IOException var17) {
            var11 = true;
            throw var17;
        } finally {
            if (var15) {
                IOUtil.closeStream(var7);
                IOUtil.closeStream(var8);
                IOUtil.closeStream(var10);
                IOUtil.closeStream(var9);
                File var19;
                if (var11 && (var19 = new File(var5)).exists()) {
                    var19.delete();
                }

            }
        }

        IOUtil.closeStream(var7);
        IOUtil.closeStream(var8);
        IOUtil.closeStream(var10);
        IOUtil.closeStream(var9);
    }

    public void signAndEncryptFileVersion3(String var1, String var2, String var3, String var4, String var5, boolean var6, boolean var7) throws PGPException, IOException {
        BufferedInputStream var8 = null;
        InputStream var9 = null;
        InputStream var10 = null;
        BufferedOutputStream var11 = null;
        this.a("Signing and encrypting file {0}", var1);
        this.a("Output file is {0}", (new File(var5)).getAbsolutePath());
        boolean var12 = false;
        boolean var16 = false;

        try {
            var16 = true;
            var8 = new BufferedInputStream(new FileInputStream(var1), 1048576);
            var9 = readFileOrAsciiString(var2, "privateKeyFileName");
            var11 = new BufferedOutputStream(new FileOutputStream(var5), 1048576);
            var10 = readFileOrAsciiString(var4, "publicKeyFile");
            this.signAndEncryptStreamVersion3(var8, (new File(var1)).getName(), var9, var3, var10, var11, var6, var7);
            var16 = false;
        } catch (PGPException var17) {
            var12 = true;
            throw IOUtil.newPGPException(var17);
        } catch (IOException var18) {
            var12 = true;
            throw var18;
        } finally {
            if (var16) {
                IOUtil.closeStream(var8);
                IOUtil.closeStream(var9);
                IOUtil.closeStream(var11);
                IOUtil.closeStream(var10);
                File var20;
                if (var12 && (var20 = new File(var5)).exists()) {
                    var20.delete();
                }

            }
        }

        IOUtil.closeStream(var8);
        IOUtil.closeStream(var9);
        IOUtil.closeStream(var11);
        IOUtil.closeStream(var10);
    }

    public void signAndEncryptStreamVersion3(InputStream var1, String var2, InputStream var3, String var4, InputStream var5, OutputStream var6, boolean var7) throws IOException, PGPException, WrongPasswordException {
        this.signAndEncryptStreamVersion3(var1, var2, var3, var4, var5, var6, var7);
    }

    public void signAndEncryptStreamVersion3(InputStream var1, String var2, KeyStore var3, String var4, String var5, String var6, OutputStream var7, boolean var8) throws IOException, PGPException, WrongPasswordException {
        InputStream var9 = a(var3, var6);
        InputStream var10 = b(var3, var4);
        this.signAndEncryptStreamVersion3(var1, var2, var10, var5, var9, var7, var8);
    }

    public void signAndEncryptStreamVersion3(InputStream var1, String var2, KeyStore var3, long var4, String var6, long var7, OutputStream var9, boolean var10) throws IOException, PGPException, WrongPasswordException {
        InputStream var11 = c(var3, var7);
        InputStream var12 = d(var3, var4);
        this.signAndEncryptStreamVersion3(var1, var2, var12, var6, var11, var9, var10);
    }

    public void signAndEncryptStreamVersion3(InputStream var1, String var2, InputStream var3, String var4, InputStream var5, OutputStream var6, boolean var7, boolean var8) throws IOException, PGPException, WrongPasswordException {
        if (!(var6 instanceof BufferedOutputStream)) {
            var6 = new BufferedOutputStream((OutputStream)var6, 1048576);
        }

        PGPPublicKey var25 = this.a(var5);
        PGPSecretKey var18 = this.b(var3);
        if (var7) {
            var6 = new ArmoredOutputStream((OutputStream)var6);
            this.a((OutputStream)var6);
        }

        int var9 = this.d(var25);
        this.a("Encrypting with cypher {0}", KeyStore.c(var9));
        PGPEncryptedDataGenerator var28 = new PGPEncryptedDataGenerator(this.a.CreatePGPDataEncryptorBuilder(var9, var8, a()));

        OutputStream var29;
        try {
            var28.addMethod(this.a.CreatePublicKeyKeyEncryptionMethodGenerator(var25));
            var29 = var28.open((OutputStream)var6, new byte[65536]);
        } catch (PGPException var16) {
            throw IOUtil.newPGPException(var16);
        }

        int var27 = this.f(var25);
        PGPCompressedDataGenerator var10 = new PGPCompressedDataGenerator(var27);
        PGPPrivateKey var21 = extractPrivateKey(var18, var4);
        int var11 = this.e(var18.getPublicKey());
        this.a("Signing with hash {0}", KeyStore.c(var11));
        PGPSignatureGenerator var30 = new PGPSignatureGenerator(this.a.CreatePGPContentSignerBuilder(var18.getPublicKey().getAlgorithm(), var11));

        try {
            var30.init(0, var21);
        } catch (PGPException var15) {
            throw IOUtil.newPGPException(var15);
        }

        Iterator var19;
        if ((var19 = var18.getPublicKey().getUserIDs()).hasNext()) {
            String var20 = (String)var19.next();
            PGPSignatureSubpacketGenerator var23;
            (var23 = new PGPSignatureSubpacketGenerator()).setSignerUserID(false, var20);
            var30.setHashedSubpackets(var23.generate());
        }

        OutputStream var22 = null;
        DirectByteArrayOutputStream var12 = new DirectByteArrayOutputStream(1048576);
        PGPLiteralDataGenerator var13 = new PGPLiteralDataGenerator();

        OutputStream var26;
        try {
            byte[] var14 = new byte[1048576];

            while(true) {
                int var24;
                if ((var24 = var1.read(var14)) < 0) {
                    this.a("Signing data with OpenPGP version 3 signature; internal file name {0}", var2);
                    if (var27 == 0) {
                        this.a("No Compression.");
                        var30.generate().encode(var29);
                        var26 = var13.open(var29, 'b', var2, new Date(), new byte[1048576]);
                    } else {
                        this.a("Compression is {0}", KeyStore.a(var27));
                        var22 = var10.open(var29, new byte[65536]);
                        var30.generate().encode(var22);
                        var26 = var13.open(var22, 'b', var2, new Date(), new byte[1048576]);
                    }

                    var26.write(var12.getArray(), 0, var12.size());
                    break;
                }

                var12.write(var14, 0, var24);
                var30.update(var14, 0, var24);
            }
        } catch (PGPException var17) {
            throw IOUtil.newPGPException(var17);
        }

        IOUtil.closeStream(var26);
        var13.close();
        IOUtil.closeStream(var22);
        var10.close();
        IOUtil.closeStream(var29);
        var28.close();
        IOUtil.closeStream(var12);
        IOUtil.closeStream(var1);
        ((OutputStream)var6).flush();
        if (var7) {
            IOUtil.closeStream((OutputStream)var6);
        }

    }

    public String signAndEncryptString(String var1, String var2, String var3, String var4) throws PGPException, IOException {
        return this.signAndEncryptString(var1, var2, var3, var4, "UTF-8");
    }

    public String signAndEncryptStringVersion3(String var1, String var2, String var3, String var4) throws PGPException, IOException {
        return this.signAndEncryptStringVersion3(var1, var2, var3, var4, "UTF-8");
    }

    public String signAndEncryptString(String var1, String var2, String var3, String var4, String var5) throws PGPException, IOException {
        ByteArrayInputStream var6 = null;
        InputStream var7 = null;
        InputStream var8 = null;

        try {
            var7 = BaseLib.readFileOrAsciiString(var2, "privateKeyFileName");
            var8 = BaseLib.readFileOrAsciiString(var4, "publicKeyFileName");
            DirectByteArrayOutputStream var12 = new DirectByteArrayOutputStream(1048576);
            var6 = new ByteArrayInputStream(var1.getBytes(var5));
            ContentDataType var11 = this.getContentType();
            this.setContentType(ContentDataType.TEXT);
            this.signAndEncryptStream(var6, "", var7, var3, (InputStream)var8, var12, true, this.n);
            this.setContentType(var11);
            var1 = new String(var12.getArray(), 0, var12.size(), "UTF-8");
        } finally {
            IOUtil.closeStream(var6);
            IOUtil.closeStream(var7);
            IOUtil.closeStream(var8);
        }

        return var1;
    }

    public String signAndEncryptStringVersion3(String var1, String var2, String var3, String var4, String var5) throws PGPException, IOException {
        ByteArrayInputStream var6 = null;
        FileInputStream var7 = null;
        FileInputStream var8 = null;

        try {
            var7 = new FileInputStream(var2);
            var8 = new FileInputStream(var4);
            DirectByteArrayOutputStream var12 = new DirectByteArrayOutputStream(1048576);
            var6 = new ByteArrayInputStream(var1.getBytes(var5));
            ContentDataType var11 = this.getContentType();
            this.setContentType(ContentDataType.TEXT);
            this.signAndEncryptStreamVersion3(var6, "", var7, var3, var8, var12, true, false);
            this.setContentType(var11);
            var1 = new String(var12.getArray(), 0, var12.size(), "UTF-8");
        } finally {
            IOUtil.closeStream(var6);
            IOUtil.closeStream(var7);
            IOUtil.closeStream(var8);
        }

        return var1;
    }

    public String signAndEncryptString(String var1, KeyStore var2, String var3, String var4, String var5, String var6) throws PGPException, IOException {
        ByteArrayInputStream var7 = null;
        InputStream var8 = null;
        InputStream var9 = null;

        try {
            var8 = b(var2, var3);
            var9 = a(var2, var5);
            DirectByteArrayOutputStream var13 = new DirectByteArrayOutputStream(1048576);
            var7 = new ByteArrayInputStream(var1.getBytes(var6));
            ContentDataType var12 = this.getContentType();
            this.setContentType(ContentDataType.TEXT);
            this.signAndEncryptStream(var7, "", var8, var4, (InputStream)var9, var13, true, this.n);
            this.setContentType(var12);
            var1 = new String(var13.getArray(), 0, var13.size(), "UTF-8");
        } finally {
            IOUtil.closeStream(var7);
            IOUtil.closeStream(var8);
            IOUtil.closeStream(var9);
        }

        return var1;
    }

    public String signAndEncryptString(String var1, KeyStore var2, String var3, String var4, String var5) throws PGPException, IOException {
        return this.signAndEncryptString(var1, var2, var3, var4, var5, "UTF-8");
    }

    public String signAndEncryptString(String var1, KeyStore var2, long var3, String var5, long var6, String var8) throws PGPException, IOException {
        ByteArrayInputStream var9 = null;
        InputStream var10 = null;
        InputStream var11 = null;

        try {
            var10 = d(var2, var3);
            var11 = c(var2, var6);
            DirectByteArrayOutputStream var15 = new DirectByteArrayOutputStream(1048576);
            var9 = new ByteArrayInputStream(var1.getBytes(var8));
            ContentDataType var14 = this.getContentType();
            this.setContentType(ContentDataType.TEXT);
            this.signAndEncryptStream(var9, "", var10, var5, (InputStream)var11, var15, true, this.n);
            this.setContentType(var14);
            var1 = new String(var15.getArray(), 0, var15.size(), "UTF-8");
        } finally {
            IOUtil.closeStream(var9);
            IOUtil.closeStream(var10);
            IOUtil.closeStream(var11);
        }

        return var1;
    }

    public String signAndEncryptString(String var1, KeyStore var2, long var3, String var5, long var6) throws PGPException, IOException {
        return this.signAndEncryptString(var1, var2, var3, var5, var6, "UTF-8");
    }

    public String decryptStream(InputStream var1, InputStream var2, String var3, OutputStream var4) throws PGPException, IOException {
        var1 = PGPUtil.getDecoderStream(var1);
        PGPObjectFactory2 var8 = new PGPObjectFactory2(var1);

        Object var5;
        try {
            var5 = var8.nextObject();
        } catch (IOException var7) {
            throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var7);
        }

        if (var5 instanceof PGPMarker) {
            var5 = var8.nextObject();
        }

        a var6 = new a(this, (byte)0);
        String var10;
        if (var5 instanceof PGPEncryptedDataList) {
            PGPEncryptedDataList var9 = (PGPEncryptedDataList)var5;
            var10 = this.a(var9, false, var6, (KeyStore)null, var2, var3, (InputStream)null, var4);
        } else if (var5 instanceof PGPCompressedData) {
            var10 = this.a((PGPCompressedData)var5, false, var6, (KeyStore)null, (InputStream)null, var4);
        } else if (var5 instanceof PGPOnePassSignatureList) {
            this.a("Found signature");
            var10 = this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var5), var8, (KeyStore)null, (InputStream)null, var4, var6);
        } else if (var5 instanceof PGPSignatureList) {
            this.a("Found signature version 3");
            var10 = this.a((PGPSignatureList)((PGPSignatureList)var5), var8, (KeyStore)null, (InputStream)null, var4, var6);
        } else {
            if (!(var5 instanceof PGPLiteralData)) {
                throw new NonPGPDataException("Unknown message format: " + var5);
            }

            var10 = this.a((PGPLiteralData)((PGPLiteralData)var5), (PGPOnePassSignature)null, (OutputStream)var4);
        }

        return var10;
    }

    public String[] decryptStreamTo(InputStream var1, InputStream var2, String var3, String var4) throws PGPException, IOException {
        var1 = PGPUtil.getDecoderStream(var1);
        PGPObjectFactory2 var8 = new PGPObjectFactory2(var1);

        Object var5;
        try {
            var5 = var8.nextObject();
        } catch (IOException var7) {
            throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var7);
        }

        if (var5 instanceof PGPMarker) {
            var5 = var8.nextObject();
        }

        a var6 = new a(this, (byte)0);
        String[] var10;
        if (var5 instanceof PGPEncryptedDataList) {
            PGPEncryptedDataList var9 = (PGPEncryptedDataList)var5;
            var10 = this.a(var9, false, var6, (KeyStore)null, var2, var3, (InputStream)null, var4, (String)null);
        } else if (var5 instanceof PGPCompressedData) {
            var10 = this.a((PGPCompressedData)((PGPCompressedData)var5), false, var6, (KeyStore)null, (InputStream)null, (String)var4, (String)null);
        } else if (var5 instanceof PGPOnePassSignatureList) {
            this.a("Found signature");
            var10 = this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var5), var8, (KeyStore)null, (InputStream)null, var4, (String)null, var6);
        } else if (var5 instanceof PGPSignatureList) {
            this.a("Found signature version 3");
            var10 = this.a((PGPSignatureList)((PGPSignatureList)var5), var8, (KeyStore)null, (InputStream)null, var4, (String)null, var6);
        } else {
            if (!(var5 instanceof PGPLiteralData)) {
                throw new NonPGPDataException("Unknown message format: " + var5);
            }

            var10 = this.a((PGPLiteralData)var5, (PGPOnePassSignature)null, var4, (String)null);
        }

        return var10;
    }

    public String[] decryptStreamTo(InputStream var1, KeyStore var2, String var3, String var4) throws PGPException, IOException {
        this.a("Decrypting stream to folder {0}", (new File(var4)).getAbsolutePath());
        var1 = PGPUtil.getDecoderStream(var1);
        PGPObjectFactory2 var8 = new PGPObjectFactory2(var1);

        Object var5;
        try {
            var5 = var8.nextObject();
        } catch (IOException var7) {
            throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var7);
        }

        if (var5 instanceof PGPMarker) {
            var5 = var8.nextObject();
        }

        a var6 = new a(this, (byte)0);
        String[] var10;
        if (var5 instanceof PGPEncryptedDataList) {
            PGPEncryptedDataList var9 = (PGPEncryptedDataList)var5;
            var10 = this.a(var9, false, var6, var2, (InputStream)null, var3, (InputStream)null, var4, (String)null);
        } else if (var5 instanceof PGPCompressedData) {
            var10 = this.a((PGPCompressedData)((PGPCompressedData)var5), false, var6, (KeyStore)null, (InputStream)null, (String)var4, (String)null);
        } else if (var5 instanceof PGPOnePassSignatureList) {
            this.a("Found signature");
            var10 = this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var5), var8, (KeyStore)null, (InputStream)null, var4, (String)null, var6);
        } else if (var5 instanceof PGPSignatureList) {
            this.a("Found signature version 3");
            var10 = this.a((PGPSignatureList)((PGPSignatureList)var5), var8, (KeyStore)null, (InputStream)null, var4, (String)null, var6);
        } else {
            if (!(var5 instanceof PGPLiteralData)) {
                throw new NonPGPDataException("Unknown message format: " + var5);
            }

            var10 = this.a((PGPLiteralData)var5, (PGPOnePassSignature)null, var4, (String)null);
        }

        return var10;
    }

    public String decryptStream(InputStream var1, KeyStore var2, String var3, OutputStream var4) throws PGPException, IOException {
        var1 = PGPUtil.getDecoderStream(var1);
        PGPObjectFactory2 var8 = new PGPObjectFactory2(var1);

        Object var5;
        try {
            var5 = var8.nextObject();
        } catch (IOException var7) {
            throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var7);
        }

        if (var5 instanceof PGPMarker) {
            var5 = var8.nextObject();
        }

        a var6 = new a(this, (byte)0);
        String var10;
        if (var5 instanceof PGPEncryptedDataList) {
            PGPEncryptedDataList var9 = (PGPEncryptedDataList)var5;
            var10 = this.a(var9, false, var6, var2, (InputStream)null, var3, (InputStream)null, var4);
        } else if (var5 instanceof PGPCompressedData) {
            var10 = this.a((PGPCompressedData)var5, false, var6, (KeyStore)null, (InputStream)null, var4);
        } else if (var5 instanceof PGPLiteralData) {
            var10 = this.a((PGPLiteralData)((PGPLiteralData)var5), (PGPOnePassSignature)null, (OutputStream)var4);
        } else if (var5 instanceof PGPOnePassSignatureList) {
            this.a("Found signature");
            var10 = this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var5), var8, (KeyStore)null, (InputStream)null, var4, var6);
        } else {
            if (!(var5 instanceof PGPSignatureList)) {
                throw new NonPGPDataException("Unknown message format: " + var5);
            }

            this.a("Found signature version 3");
            var10 = this.a((PGPSignatureList)((PGPSignatureList)var5), var8, (KeyStore)null, (InputStream)null, var4, var6);
        }

        return var10;
    }

    public String decryptFile(String var1, KeyStore var2, String var3, String var4) throws PGPException, IOException {
        FileInputStream var5 = null;
        FileOutputStream var6 = null;
        this.a("Decrypting file {0}", var1);
        this.a("Decrypting to {0}", (new File(var4)).getAbsolutePath());
        boolean var7 = false;
        boolean var11 = false;

        try {
            var11 = true;
            var5 = new FileInputStream(var1);
            var6 = new FileOutputStream(var4);
            var1 = this.decryptStream(var5, (KeyStore)var2, var3, var6);
            var11 = false;
        } catch (PGPException var12) {
            var7 = true;
            throw var12;
        } catch (IOException var13) {
            var7 = true;
            throw var13;
        } finally {
            if (var11) {
                IOUtil.closeStream(var5);
                IOUtil.closeStream(var6);
                File var15;
                if (var7 && (var15 = new File(var4)).exists()) {
                    var15.delete();
                }

            }
        }

        IOUtil.closeStream(var5);
        IOUtil.closeStream(var6);
        return var1;
    }

    public String decryptFile(String var1, String var2, String var3, String var4) throws PGPException, IOException {
        this.a("Decrypting file {0}", var1);
        FileInputStream var5 = null;

        try {
            var5 = new FileInputStream(var2);
            var1 = this.decryptFile(var1, (InputStream)var5, var3, var4);
        } finally {
            IOUtil.closeStream(var5);
        }

        return var1;
    }

    public String decryptFilePBE(String var1, String var2, String var3) throws PGPException, IOException {
        this.a("Decrypting password encrypted file {0}", var1);
        this.a("Decrypting to {0}", (new File(var3)).getAbsolutePath());
        BufferedInputStream var4 = null;
        BufferedOutputStream var5 = null;
        boolean var6 = false;
        boolean var13 = false;

        try {
            var13 = true;
            var4 = new BufferedInputStream(new FileInputStream(var1));
            var5 = new BufferedOutputStream(new FileOutputStream(var3));
            InputStream var18 = PGPUtil.getDecoderStream(var4);
            PGPObjectFactory2 var19 = new PGPObjectFactory2(var18);

            Object var7;
            try {
                var7 = var19.nextObject();
            } catch (IOException var14) {
                throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var14);
            }

            if (var7 instanceof PGPMarker) {
                var7 = var19.nextObject();
            }

            a var8 = new a(this, (byte)0);
            if (var7 instanceof PGPEncryptedDataList) {
                PGPEncryptedDataList var20 = (PGPEncryptedDataList)var7;
                var1 = this.a((PGPEncryptedDataList)var20, false, var8, (String)var2, (KeyStore)null, (InputStream)null, (OutputStream)var5);
            } else if (var7 instanceof PGPCompressedData) {
                var1 = this.a((PGPCompressedData)var7, false, var8, (KeyStore)null, (InputStream)null, var5);
            } else if (var7 instanceof PGPOnePassSignatureList) {
                this.a("Found signature");
                var1 = this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var7), var19, (KeyStore)null, (InputStream)null, var5, var8);
            } else if (var7 instanceof PGPSignatureList) {
                this.a("Found signature version 3");
                var1 = this.a((PGPSignatureList)((PGPSignatureList)var7), var19, (KeyStore)null, (InputStream)null, var5, var8);
            } else {
                if (!(var7 instanceof PGPLiteralData)) {
                    throw new NonPGPDataException("Unknown message format: " + var7);
                }

                this.a("Literal data packet only");
                var1 = this.a((PGPLiteralData)((PGPLiteralData)var7), (PGPOnePassSignature)null, (OutputStream)var5);
            }

            var1 = var1;
            var13 = false;
        } catch (PGPException var15) {
            var6 = true;
            throw IOUtil.newPGPException(var15);
        } catch (IOException var16) {
            var6 = true;
            throw var16;
        } finally {
            if (var13) {
                IOUtil.closeStream(var4);
                IOUtil.closeStream(var5);
                File var21;
                if (var6 && (var21 = new File(var3)).exists()) {
                    var21.delete();
                }

            }
        }

        IOUtil.closeStream(var4);
        IOUtil.closeStream(var5);
        return var1;
    }

    public String[] decryptFileTo(String var1, String var2, String var3, String var4) throws PGPException, IOException {
        this.a("Decrypting file {0}", var1);
        FileInputStream var5 = null;
        FileInputStream var6 = null;

        String[] var9;
        try {
            var5 = new FileInputStream(var1);
            var6 = new FileInputStream(var2);
            var9 = this.decryptStreamTo(var5, (InputStream)var6, var3, var4);
        } finally {
            IOUtil.closeStream(var5);
            IOUtil.closeStream(var6);
        }

        return var9;
    }

    public String[] decryptFileTo(String var1, KeyStore var2, String var3, String var4) throws PGPException, IOException {
        this.a("Decrypting file {0}", var1);
        FileInputStream var5 = null;

        String[] var8;
        try {
            var5 = new FileInputStream(var1);
            var8 = this.decryptStreamTo(var5, (KeyStore)var2, var3, var4);
        } finally {
            IOUtil.closeStream(var5);
        }

        return var8;
    }

    public String decryptFile(String var1, InputStream var2, String var3, String var4) throws PGPException, IOException {
        BufferedInputStream var5 = null;
        BufferedOutputStream var6 = null;
        this.a("Decrypting file {0}", var1);
        this.a("Decrypting to {0}", (new File(var4)).getAbsolutePath());
        boolean var7 = false;
        boolean var11 = false;

        try {
            var11 = true;
            var5 = new BufferedInputStream(new FileInputStream(var1), 1048576);
            var6 = new BufferedOutputStream(new FileOutputStream(var4), 1048576);
            var1 = this.decryptStream(var5, (InputStream)var2, var3, var6);
            var11 = false;
        } catch (PGPException var12) {
            var7 = true;
            throw var12;
        } catch (IOException var13) {
            var7 = true;
            throw var13;
        } finally {
            if (var11) {
                IOUtil.closeStream(var5);
                IOUtil.closeStream(var6);
                File var15;
                if (var7 && (var15 = new File(var4)).exists()) {
                    var15.delete();
                }

            }
        }

        IOUtil.closeStream(var5);
        IOUtil.closeStream(var6);
        return var1;
    }

    public String decryptString(String var1, String var2, String var3) throws IOException, PGPException {
        return this.decryptString(var1, var2, var3, "UTF-8");
    }

    public String decryptString(String var1, String var2, String var3, String var4) throws IOException, PGPException {
        InputStream var5 = null;

        try {
            var5 = BaseLib.readFileOrAsciiString(var2, "privateKeyFileName");
            var1 = this.decryptString(var1, var5, var3, var4);
        } finally {
            IOUtil.closeStream(var5);
        }

        return var1;
    }

    public String decryptString(String var1, InputStream var2, String var3) throws IOException, PGPException {
        return this.decryptString(var1, var2, var3, "UTF-8");
    }

    public String decryptString(String var1, KeyStore var2, String var3) throws IOException, PGPException {
        return this.decryptString(var1, var2, var3, "UTF-8");
    }

    public String decryptString(String var1, KeyStore var2, String var3, String var4) throws IOException, PGPException {
        ByteArrayInputStream var5 = null;
        DirectByteArrayOutputStream var6 = null;

        try {
            var5 = new ByteArrayInputStream(var1.getBytes("ASCII"));
            var6 = new DirectByteArrayOutputStream(1048576);
            this.decryptStream(var5, (KeyStore)var2, var3, var6);
            var1 = new String(var6.getArray(), 0, var6.size(), var4);
        } finally {
            IOUtil.closeStream(var5);
            IOUtil.closeStream(var6);
        }

        return var1;
    }

    public String decryptString(String var1, InputStream var2, String var3, String var4) throws IOException, PGPException {
        ByteArrayInputStream var5 = null;
        DirectByteArrayOutputStream var6 = null;

        try {
            var5 = new ByteArrayInputStream(var1.getBytes("ASCII"));
            var6 = new DirectByteArrayOutputStream(1048576);
            this.decryptStream(var5, (InputStream)var2, var3, var6);
            var1 = new String(var6.getArray(), 0, var6.size(), var4);
        } finally {
            IOUtil.closeStream(var5);
            IOUtil.closeStream(var6);
        }

        return var1;
    }

    public String decryptStringPBE(String var1, String var2) throws IOException, PGPException {
        return this.decryptStringPBE(var1, var2, "UTF-8");
    }

    public String decryptStringPBE(String var1, String var2, String var3) throws IOException, PGPException {
        ByteArrayInputStream var4 = null;
        DirectByteArrayOutputStream var5 = null;

        try {
            var4 = new ByteArrayInputStream(var1.getBytes("ASCII"));
            var5 = new DirectByteArrayOutputStream(1048576);
            this.decryptStreamPBE(var4, var2, var5);
            var1 = new String(var5.getArray(), 0, var5.size(), var3);
        } finally {
            IOUtil.closeStream(var4);
            IOUtil.closeStream(var5);
        }

        return var1;
    }

    public String decryptStreamPBE(InputStream var1, String var2, OutputStream var3) throws PGPException, IOException {
        var1 = PGPUtil.getDecoderStream(var1);
        PGPObjectFactory2 var7 = new PGPObjectFactory2(var1);

        Object var4;
        try {
            var4 = var7.nextObject();
        } catch (IOException var6) {
            throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var6);
        }

        if (var4 instanceof PGPMarker) {
            var4 = var7.nextObject();
        }

        a var5 = new a(this, (byte)0);
        String var9;
        if (var4 instanceof PGPEncryptedDataList) {
            PGPEncryptedDataList var8 = (PGPEncryptedDataList)var4;
            var9 = this.a((PGPEncryptedDataList)var8, false, var5, (String)var2, (KeyStore)null, (InputStream)null, (OutputStream)var3);
        } else if (var4 instanceof PGPCompressedData) {
            var9 = this.a((PGPCompressedData)var4, false, var5, (KeyStore)null, (InputStream)null, var3);
        } else if (var4 instanceof PGPOnePassSignatureList) {
            var9 = this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var4), var7, (KeyStore)null, (InputStream)null, var3, var5);
        } else if (var4 instanceof PGPSignatureList) {
            var9 = this.a((PGPSignatureList)((PGPSignatureList)var4), var7, (KeyStore)null, (InputStream)null, var3, var5);
        } else {
            if (!(var4 instanceof PGPLiteralData)) {
                throw new NonPGPDataException("Unknown message format: " + var4);
            }

            var9 = this.a((PGPLiteralData)((PGPLiteralData)var4), (PGPOnePassSignature)null, (OutputStream)var3);
        }

        return var9;
    }

    public String encryptString(String var1, String var2) throws PGPException, IOException {
        return this.encryptString(var1, var2, "UTF-8");
    }

    public String encryptString(String var1, String var2, String var3) throws PGPException, IOException {
        FileInputStream var4 = null;

        try {
            var4 = new FileInputStream(var2);
            var1 = this.encryptString(var1, (InputStream)var4, var3);
        } finally {
            IOUtil.closeStream(var4);
        }

        return var1;
    }

    public String encryptString(String var1, String[] var2, String var3) throws PGPException, IOException {
        LinkedList var4 = new LinkedList();

        for(int var6 = 0; var6 < var2.length; ++var6) {
            InputStream var5 = readFileOrAsciiString(var2[var6], "publicKeyFileNames :" + var6);
            var4.add(var5);
        }

        InputStream[] var12 = (InputStream[])var4.toArray(new InputStream[var4.size()]);
        ByteArrayInputStream var9 = null;
        DirectByteArrayOutputStream var11 = null;

        try {
            var11 = new DirectByteArrayOutputStream(1048576);
            var9 = new ByteArrayInputStream(var1.getBytes(var3));
            this.encryptStream(var9, "message.txt", (InputStream[])var12, var11, true, this.n);
            var1 = new String(var11.getArray(), 0, var11.size(), "UTF-8");
        } finally {
            IOUtil.closeStream(var9);
            IOUtil.closeStream(var11);

            for(int var10 = 0; var10 < var12.length; ++var10) {
                IOUtil.closeStream(var12[var10]);
            }

        }

        return var1;
    }

    public String encryptString(String var1, KeyStore var2, String var3) throws PGPException, IOException {
        return this.encryptString(var1, var2, var3, "UTF-8");
    }

    public String encryptString(String var1, KeyStore var2, String var3, String var4) throws PGPException, IOException {
        InputStream var5 = null;

        try {
            var5 = a(var2, var3);
            var1 = this.encryptString(var1, var5, var4);
        } finally {
            IOUtil.closeStream(var5);
        }

        return var1;
    }

    public String encryptString(String var1, KeyStore var2, long var3) throws PGPException, IOException {
        return this.encryptString(var1, var2, var3, "UTF-8");
    }

    public String encryptString(String var1, KeyStore var2, long var3, String var5) throws PGPException, IOException {
        InputStream var6 = null;

        try {
            var6 = c(var2, var3);
            var1 = this.encryptString(var1, var6, var5);
        } finally {
            IOUtil.closeStream(var6);
        }

        return var1;
    }

    public String encryptString(String var1, KeyStore var2, long[] var3, String var4) throws PGPException, IOException {
        ByteArrayInputStream var5 = null;

        try {
            DirectByteArrayOutputStream var6 = new DirectByteArrayOutputStream(1048576);
            var5 = new ByteArrayInputStream(var1.getBytes(var4));
            this.encryptStream(var5, "message.txt", var2, (long[])var3, var6, true, this.n);
            var1 = new String(var6.getArray(), 0, var6.size(), "UTF-8");
        } finally {
            IOUtil.closeStream(var5);
        }

        return var1;
    }

    public String encryptString(String var1, KeyStore var2, String[] var3, String var4) throws PGPException, IOException {
        ByteArrayInputStream var5 = null;

        try {
            DirectByteArrayOutputStream var6 = new DirectByteArrayOutputStream(1048576);
            var5 = new ByteArrayInputStream(var1.getBytes(var4));
            this.encryptStream(var5, "message.txt", var2, (String[])var3, var6, true, this.n);
            var1 = new String(var6.getArray(), 0, var6.size(), "UTF-8");
        } finally {
            IOUtil.closeStream(var5);
        }

        return var1;
    }

    public String encryptString(String var1, InputStream var2) throws PGPException, IOException {
        return this.encryptString(var1, var2, "UTF-8");
    }

    public String encryptString(String var1, InputStream var2, String var3) throws PGPException, IOException {
        ByteArrayInputStream var4 = null;

        try {
            DirectByteArrayOutputStream var5 = new DirectByteArrayOutputStream(1048576);
            var4 = new ByteArrayInputStream(var1.getBytes(var3));
            this.encryptStream(var4, "", (InputStream)var2, var5, true, this.n);
            var1 = new String(var5.getArray(), 0, var5.size(), "UTF-8");
        } finally {
            IOUtil.closeStream(var4);
        }

        return var1;
    }

    public String encryptStringPBE(String var1, String var2) throws PGPException, IOException {
        return this.encryptStringPBE(var1, var2, "UTF-8");
    }

    public String encryptStringPBE(String var1, String var2, String var3) throws PGPException, IOException {
        ByteArrayInputStream var4 = null;

        try {
            DirectByteArrayOutputStream var5 = new DirectByteArrayOutputStream(1048576);
            var4 = new ByteArrayInputStream(var1.getBytes(var3));
            this.encryptStreamPBE(var4, "", var2, var5, true, this.n);
            var1 = new String(var5.getArray(), 0, var5.size(), "UTF-8");
        } finally {
            IOUtil.closeStream(var4);
        }

        return var1;
    }

    public void encryptStream(InputStream var1, String var2, long var3, String var5, OutputStream var6, boolean var7) throws PGPException, IOException {
        this.encryptStream(var1, var2, var3, var5, var6, var7, this.n);
    }

    public void encryptStream(InputStream var1, String var2, long var3, String var5, OutputStream var6, boolean var7, boolean var8) throws PGPException, IOException {
        FileInputStream var9 = null;

        try {
            var9 = new FileInputStream(new File(var5));
            this.encryptStream(var1, var2, var3, (InputStream)var9, var6, var7, var8);
        } finally {
            IOUtil.closeStream(var9);
        }

    }

    public void encryptStream(InputStream var1, String var2, KeyStore var3, String var4, OutputStream var5, boolean var6, boolean var7) throws PGPException, IOException {
        InputStream var10 = a(var3, var4);

        try {
            this.encryptStream(var1, var2, var10, var5, var6, var7);
        } finally {
            IOUtil.closeStream(var10);
            IOUtil.closeStream(var1);
        }

    }

    public void encryptStream(InputStream var1, String var2, KeyStore var3, String[] var4, OutputStream var5, boolean var6, boolean var7) throws PGPException, IOException {
        InputStream[] var8 = new InputStream[var4.length];

        int var9;
        for(var9 = 0; var9 < var4.length; ++var9) {
            var8[var9] = a(var3, var4[var9]);
        }

        boolean var11 = false;

        try {
            var11 = true;
            this.encryptStream(var1, var2, var8, var5, var6, var7);
            var11 = false;
        } finally {
            if (var11) {
                for(int var13 = 0; var13 < var4.length; ++var13) {
                    IOUtil.closeStream(var8[var13]);
                }

                IOUtil.closeStream(var1);
            }
        }

        for(var9 = 0; var9 < var4.length; ++var9) {
            IOUtil.closeStream(var8[var9]);
        }

        IOUtil.closeStream(var1);
    }

    public void encryptStream(InputStream var1, String var2, KeyStore var3, long var4, OutputStream var6, boolean var7, boolean var8) throws PGPException, IOException {
        ByteArrayInputStream var9 = null;
        DirectByteArrayOutputStream var10 = new DirectByteArrayOutputStream(1048576);
        PGPPublicKeyRing var13 = var3.a(var4);

        try {
            var13.encode(var10);
            var9 = new ByteArrayInputStream(var10.getArray(), 0, var10.size());
            DirectByteArrayOutputStream var14 = new DirectByteArrayOutputStream(1048576);
            byte[] var5 = new byte[1048576];

            int var15;
            while((var15 = var1.read(var5)) > 0) {
                var14.write(var5, 0, var15);
            }

            this.encryptStream(new ByteArrayInputStream(var14.getArray(), 0, var14.size()), var2, (long)var14.size(), (InputStream)var9, var6, var7, var8);
        } finally {
            IOUtil.closeStream(var9);
            IOUtil.closeStream(var1);
        }
    }

    public void encryptStream(InputStream var1, String var2, KeyStore var3, long[] var4, OutputStream var5, boolean var6, boolean var7) throws PGPException, IOException {
        InputStream[] var8 = new InputStream[var4.length];

        int var9;
        for(var9 = 0; var9 < var4.length; ++var9) {
            PGPPublicKeyRing var10 = var3.a(var4[var9]);
            var8[var9] = new ByteArrayInputStream(var10.getEncoded());
        }

        boolean var12 = false;

        try {
            var12 = true;
            DirectByteArrayOutputStream var16 = new DirectByteArrayOutputStream(1048576);
            byte[] var14 = new byte[1048576];

            while(true) {
                int var17;
                if ((var17 = var1.read(var14)) <= 0) {
                    this.encryptStream(new ByteArrayInputStream(var16.getArray(), 0, var16.size()), var2, (InputStream[])var8, var5, var6, var7);
                    var12 = false;
                    break;
                }

                var16.write(var14, 0, var17);
            }
        } finally {
            if (var12) {
                for(int var15 = 0; var15 < var4.length; ++var15) {
                    IOUtil.closeStream(var8[var15]);
                }

                IOUtil.closeStream(var1);
            }
        }

        for(var9 = 0; var9 < var4.length; ++var9) {
            IOUtil.closeStream(var8[var9]);
        }

        IOUtil.closeStream(var1);
    }

    public void encryptStream(InputStream var1, String var2, long var3, InputStream var5, OutputStream var6, boolean var7, boolean var8) throws PGPException, IOException {
        PGPPublicKey var11 = this.a(var5);
        InputStream var10001 = var1;
        PGPPublicKey[] var10003 = new PGPPublicKey[]{var11};
        boolean var9 = false;
        PGPPublicKey[] var4 = var10003;
        String var12 = var2;
        InputStream var10 = var10001;
        this.a(var10, var12, var4, var6, new Date(), var7, var8, false);
    }

    public void encryptStream(InputStream var1, String var2, InputStream var3, OutputStream var4, boolean var5, boolean var6) throws PGPException, IOException {
        try {
            PGPPublicKey var9 = this.a(var3);
            this.a(var1, var2, new PGPPublicKey[]{var9}, var4, new Date(), var5, var6, false);
        } finally {
            IOUtil.closeStream(var1);
        }

    }

    public void encryptStream(InputStream var1, String var2, InputStream[] var3, OutputStream var4, boolean var5, boolean var6) throws PGPException, IOException {
        try {
            PGPPublicKey[] var7 = new PGPPublicKey[var3.length];

            for(int var8 = 0; var8 < var3.length; ++var8) {
                var7[var8] = this.a(var3[var8]);
            }

            this.a(var1, var2, var7, var4, new Date(), var5, var6, false);
        } finally {
            IOUtil.closeStream(var1);
        }
    }

    public void encryptStream(InputStream var1, String var2, PGPKeyPair var3, OutputStream var4, boolean var5, boolean var6) throws PGPException, IOException {
        try {
            PGPPublicKey var9 = this.a(var3.getRawPublicKeyRing());
            this.a(var1, var2, new PGPPublicKey[]{var9}, var4, new Date(), var5, var6, false);
        } finally {
            IOUtil.closeStream(var1);
        }

    }

    public void encryptStreamPBE(InputStream var1, String var2, String var3, OutputStream var4, boolean var5, boolean var6) throws PGPException, IOException {
        try {
            this.a(var1, var2, var3, var4, var5, var6);
        } finally {
            IOUtil.closeStream(var1);
        }

    }

    public void encryptFilePBE(String var1, String var2, String var3, String var4, boolean var5, boolean var6) throws PGPException, IOException {
        File var7 = new File(var1);
        BufferedInputStream var8 = null;
        FileInputStream var9 = null;
        BufferedOutputStream var10 = null;
        this.a("Password encrypting file {0}", var1);
        this.a("Encrypting to {0}", (new File(var4)).getAbsolutePath());
        boolean var11 = false;
        boolean var27 = false;

        try {
            var27 = true;
            var8 = new BufferedInputStream(new FileInputStream(var1), 1048576);
            var10 = new BufferedOutputStream(new FileOutputStream(var4), 1048576);
            var9 = new FileInputStream(var2);
            PGPPublicKey var40 = this.a((InputStream)var9);
            String var10002 = var7.getName();
            long var10003 = var7.length();
            boolean var13 = var6;
            boolean var12 = var5;
            Object var46 = var10;
            String var45 = var3;
            PGPPublicKey var44 = var40;
            long var18 = var10003;
            var3 = var10002;
            BufferedInputStream var42 = var8;
            PGPLibs var41 = this;
            if (!(var10 instanceof BufferedOutputStream)) {
                var46 = new BufferedOutputStream(var10, 1048576);
            }

            try {
                Object var14 = null;
                if (var12) {
                    var14 = var46;
                    var46 = new ArmoredOutputStream((OutputStream)var46);
                    var41.a((OutputStream)var46);
                }

                OutputStream var15 = null;

                try {
                    int var16 = var41.f(var44);
                    PGPCompressedDataGenerator var17 = new PGPCompressedDataGenerator(var16);
                    int var20 = var41.d(var44);
                    PGPEncryptedDataGenerator var47;
                    (var47 = new PGPEncryptedDataGenerator(var41.a.CreatePGPDataEncryptorBuilder(var20, var13, a()))).addMethod(var41.a.CreatePublicKeyKeyEncryptionMethodGenerator(var44));
                    var41.a("Encrypting with key {0} ", KeyPairInformation.keyId2Hex(var44.getKeyID()));
                    var47.addMethod(var41.a.CreatePBEKeyEncryptionMethodGenerator(var45));
                    var41.a("Encrypting with password");
                    var15 = var47.open((OutputStream)var46, new byte[1048576]);
                    if (var16 == 0) {
                        a(var15, 'b', var42, var3, var18, new Date());
                    } else {
                        a(var17.open(var15), 'b', var42, var3, var18, new Date());
                    }

                    var15.close();
                } catch (PGPException var34) {
                    throw IOUtil.newPGPException(var34);
                } finally {
                    IOUtil.closeStream((OutputStream)null);
                    IOUtil.closeStream(var15);
                    IOUtil.closeStream((OutputStream)var46);
                    if (var12) {
                        IOUtil.closeStream((OutputStream)var14);
                    }

                }

                var27 = false;
            } catch (IOException var36) {
                throw new PGPException(var36.getMessage(), var36);
            }
        } catch (PGPException var37) {
            var11 = true;
            throw var37;
        } catch (IOException var38) {
            var11 = true;
            throw var38;
        } finally {
            if (var27) {
                IOUtil.closeStream(var9);
                IOUtil.closeStream(var8);
                IOUtil.closeStream(var10);
                File var43;
                if (var11 && (var43 = new File(var4)).exists()) {
                    var43.delete();
                }

            }
        }

        IOUtil.closeStream(var9);
        IOUtil.closeStream(var8);
        IOUtil.closeStream(var10);
    }

    public void encryptFilePBE(String var1, String var2, String var3, boolean var4, boolean var5) throws PGPException, IOException {
        File var6 = new File(var1);
        BufferedInputStream var7 = null;
        BufferedOutputStream var8 = null;
        this.a("Password encrypting file {0}", var1);
        this.a("Encrypting to {0}", (new File(var3)).getAbsolutePath());
        boolean var9 = false;
        boolean var13 = false;

        try {
            var13 = true;
            var7 = new BufferedInputStream(new FileInputStream(var1), 1048576);
            var8 = new BufferedOutputStream(new FileOutputStream(var3), 1048576);
            this.a(var7, var6.getName(), var2, var8, var4, var5);
            var13 = false;
        } catch (PGPException var14) {
            var9 = true;
            throw var14;
        } catch (IOException var15) {
            var9 = true;
            throw var15;
        } finally {
            if (var13) {
                IOUtil.closeStream((InputStream)null);
                IOUtil.closeStream(var7);
                IOUtil.closeStream(var8);
                File var17;
                if (var9 && (var17 = new File(var3)).exists()) {
                    var17.delete();
                }

            }
        }

        IOUtil.closeStream((InputStream)null);
        IOUtil.closeStream(var7);
        IOUtil.closeStream(var8);
    }

    public void encryptFile(String var1, String var2, String var3, boolean var4) throws PGPException, IOException {
        this.encryptFile(var1, var2, var3, var4, this.n);
    }

    public void encryptFile(String var1, String var2, String var3, boolean var4, boolean var5) throws PGPException, IOException {
        FileInputStream var6 = null;

        try {
            var6 = new FileInputStream(var2);
            this.encryptFile(var1, (InputStream)var6, var3, var4, var5);
        } finally {
            IOUtil.closeStream(var6);
        }

    }

    public void encryptFile(String var1, String[] var2, String var3, boolean var4, boolean var5) throws PGPException, IOException {
        this.a("Encrypting file {0}", var1);
        this.a("Encrypting to {0}", (new File(var3)).getAbsolutePath());
        LinkedList var6 = new LinkedList();
        FileInputStream var7 = null;

        for(int var8 = 0; var8 < var2.length; ++var8) {
            try {
                var7 = new FileInputStream(var2[var8]);
                PGPPublicKey var9 = this.a((InputStream)var7);
                var6.add(var9);
            } finally {
                IOUtil.closeStream(var7);
            }
        }

        PGPPublicKey[] var27 = (PGPPublicKey[])var6.toArray(new PGPPublicKey[var6.size()]);
        FileInputStream var28 = null;
        FileOutputStream var24 = null;
        boolean var26 = false;
        boolean var14 = false;

        try {
            var14 = true;
            File var23 = new File(var1);
            var28 = new FileInputStream(var23);
            var24 = new FileOutputStream(var3);
            this.a(var28, var23.getName(), var27, var24, new Date(var23.lastModified()), var4, var5, false);
            var14 = false;
        } catch (PGPException var19) {
            var26 = true;
            throw IOUtil.newPGPException(var19);
        } catch (IOException var20) {
            var26 = true;
            throw var20;
        } finally {
            if (var14) {
                IOUtil.closeStream(var28);
                IOUtil.closeStream(var24);
                File var25;
                if (var26 && (var25 = new File(var3)).exists()) {
                    var25.delete();
                }

            }
        }

        IOUtil.closeStream(var28);
        IOUtil.closeStream(var24);
    }

    public void encryptFile(String var1, KeyStore var2, String[] var3, String var4, boolean var5, boolean var6) throws PGPException, IOException {
        this.a("Encrypting file {0}", var1);
        this.a("Encrypting to {0}", (new File(var4)).getAbsolutePath());
        LinkedList var7 = new LinkedList();
        InputStream var8 = null;

        for(int var9 = 0; var9 < var3.length; ++var9) {
            try {
                var8 = a(var2, var3[var9]);
                PGPPublicKey var10 = this.a(var8);
                var7.add(var10);
            } finally {
                IOUtil.closeStream(var8);
            }
        }

        PGPPublicKey[] var28 = (PGPPublicKey[])var7.toArray(new PGPPublicKey[var7.size()]);
        FileInputStream var29 = null;
        FileOutputStream var25 = null;
        boolean var26 = false;
        boolean var15 = false;

        try {
            var15 = true;
            File var24 = new File(var1);
            var29 = new FileInputStream(var24);
            var25 = new FileOutputStream(var4);
            this.a(var29, var24.getName(), var28, var25, new Date(var24.lastModified()), var5, var6, false);
            var15 = false;
        } catch (PGPException var20) {
            var26 = true;
            throw IOUtil.newPGPException(var20);
        } catch (IOException var21) {
            var26 = true;
            throw var21;
        } finally {
            if (var15) {
                IOUtil.closeStream(var29);
                IOUtil.closeStream(var25);
                File var27;
                if (var26 && (var27 = new File(var4)).exists()) {
                    var27.delete();
                }

            }
        }

        IOUtil.closeStream(var29);
        IOUtil.closeStream(var25);
    }

    public void encryptFile(String var1, KeyStore var2, long[] var3, String var4, boolean var5, boolean var6) throws PGPException, IOException {
        this.a("Encrypting file {0}", var1);
        this.a("Encrypting to {0}", (new File(var4)).getAbsolutePath());
        LinkedList var7 = new LinkedList();
        InputStream var8 = null;

        for(int var9 = 0; var9 < var3.length; ++var9) {
            try {
                var8 = c(var2, var3[var9]);
                PGPPublicKey var10 = this.a(var8);
                var7.add(var10);
            } finally {
                IOUtil.closeStream(var8);
            }
        }

        PGPPublicKey[] var28 = (PGPPublicKey[])var7.toArray(new PGPPublicKey[var7.size()]);
        FileInputStream var29 = null;
        FileOutputStream var25 = null;
        boolean var26 = false;
        boolean var15 = false;

        try {
            var15 = true;
            File var24 = new File(var1);
            var29 = new FileInputStream(var24);
            var25 = new FileOutputStream(var4);
            this.a(var29, var24.getName(), var28, var25, new Date(var24.lastModified()), var5, var6, false);
            var15 = false;
        } catch (PGPException var20) {
            var26 = true;
            throw IOUtil.newPGPException(var20);
        } catch (IOException var21) {
            var26 = true;
            throw var21;
        } finally {
            if (var15) {
                IOUtil.closeStream(var29);
                IOUtil.closeStream(var25);
                File var27;
                if (var26 && (var27 = new File(var4)).exists()) {
                    var27.delete();
                }

            }
        }

        IOUtil.closeStream(var29);
        IOUtil.closeStream(var25);
    }

    public void encryptFiles(String[] var1, String var2, String var3, boolean var4, boolean var5) throws PGPException, IOException {
        this.encryptFiles(var1, new String[]{var2}, var3, var4, var5);
    }

    public void encryptFiles(String[] var1, String[] var2, String var3, boolean var4, boolean var5) throws PGPException, IOException {
        this.a("Encrypting multiple files");
        this.a("Encrypting to {0}", (new File(var3)).getAbsolutePath());
        if (var1.length == 0) {
            throw new IllegalArgumentException("Please specify at least one file to be encrypted.");
        } else {
            LinkedList var6 = new LinkedList();
            FileInputStream var7 = null;

            for(int var8 = 0; var8 < var2.length; ++var8) {
                try {
                    var7 = new FileInputStream(var2[var8]);
                    PGPPublicKey var9 = this.a((InputStream)var7);
                    var6.add(var9);
                } finally {
                    IOUtil.closeStream(var7);
                }
            }

            PGPPublicKey[] var30 = (PGPPublicKey[])var6.toArray(new PGPPublicKey[var6.size()]);
            File var31 = null;
            boolean var25 = false;
            String var27 = var1[0];
            if (var1.length > 1) {
                var31 = a(var1);
                var25 = true;
                var27 = var31.getAbsolutePath();
            }

            BufferedInputStream var24 = null;
            BufferedOutputStream var29 = null;
            boolean var10 = false;
            boolean var15 = false;

            try {
                var15 = true;
                File var28 = new File(var27);
                var24 = new BufferedInputStream(new FileInputStream(var28), 1048576);
                var29 = new BufferedOutputStream(new FileOutputStream(var3), 1048576);
                this.a(var24, var28.getName(), var30, var29, new Date(var28.lastModified()), var4, var5, var25);
                var15 = false;
            } catch (PGPException var20) {
                var10 = true;
                throw var20;
            } catch (IOException var21) {
                var10 = true;
                throw var21;
            } finally {
                if (var15) {
                    IOUtil.closeStream(var24);
                    IOUtil.closeStream(var29);
                    if (var31 != null) {
                        var31.delete();
                    }

                    File var26;
                    if (var10 && (var26 = new File(var3)).exists()) {
                        var26.delete();
                    }

                }
            }

            IOUtil.closeStream(var24);
            IOUtil.closeStream(var29);
            if (var31 != null) {
                var31.delete();
            }

        }
    }

    public void encryptFiles(String[] var1, KeyStore var2, String[] var3, String var4, boolean var5, boolean var6) throws PGPException, IOException {
        this.a("Encrypting multiple files");
        this.a("Encrypting to {0}", (new File(var4)).getAbsolutePath());
        if (var1.length == 0) {
            throw new IllegalArgumentException("please specify at least one file name to be encrypted.");
        } else {
            LinkedList var7 = new LinkedList();
            InputStream var8 = null;

            for(int var9 = 0; var9 < var3.length; ++var9) {
                try {
                    var8 = a(var2, var3[var9]);
                    PGPPublicKey var10 = this.a(var8);
                    var7.add(var10);
                } finally {
                    IOUtil.closeStream(var8);
                }
            }

            PGPPublicKey[] var31 = (PGPPublicKey[])var7.toArray(new PGPPublicKey[var7.size()]);
            File var32 = null;
            boolean var25 = false;
            String var27 = var1[0];
            if (var1.length > 1) {
                var32 = a(var1);
                var25 = true;
                var27 = var32.getAbsolutePath();
            }

            BufferedInputStream var24 = null;
            BufferedOutputStream var29 = null;
            boolean var30 = false;
            boolean var15 = false;

            try {
                var15 = true;
                File var28 = new File(var27);
                var24 = new BufferedInputStream(new FileInputStream(var28), 1048576);
                var29 = new BufferedOutputStream(new FileOutputStream(var4), 1048576);
                this.a(var24, var28.getName(), var31, var29, new Date(var28.lastModified()), var5, var6, var25);
                var15 = false;
            } catch (PGPException var20) {
                var30 = true;
                throw IOUtil.newPGPException(var20);
            } catch (IOException var21) {
                var30 = true;
                throw var21;
            } finally {
                if (var15) {
                    IOUtil.closeStream(var24);
                    IOUtil.closeStream(var29);
                    if (var32 != null) {
                        var32.delete();
                    }

                    File var26;
                    if (var30 && (var26 = new File(var4)).exists()) {
                        var26.delete();
                    }

                }
            }

            IOUtil.closeStream(var24);
            IOUtil.closeStream(var29);
            if (var32 != null) {
                var32.delete();
            }

        }
    }

    public void encryptFiles(String[] var1, KeyStore var2, long[] var3, String var4, boolean var5, boolean var6) throws PGPException, IOException {
        this.a("Encrypting multiple files");
        this.a("Encrypting to {0}", (new File(var4)).getAbsolutePath());
        if (var1.length == 0) {
            throw new IllegalArgumentException("please specify at least one file name to be encrypted.");
        } else {
            LinkedList var7 = new LinkedList();
            InputStream var8 = null;

            for(int var9 = 0; var9 < var3.length; ++var9) {
                try {
                    var8 = c(var2, var3[var9]);
                    PGPPublicKey var10 = this.a(var8);
                    var7.add(var10);
                } finally {
                    IOUtil.closeStream(var8);
                }
            }

            PGPPublicKey[] var31 = (PGPPublicKey[])var7.toArray(new PGPPublicKey[var7.size()]);
            File var32 = null;
            boolean var25 = false;
            String var27 = var1[0];
            if (var1.length > 1) {
                var32 = a(var1);
                var25 = true;
                var27 = var32.getAbsolutePath();
            }

            BufferedInputStream var24 = null;
            BufferedOutputStream var29 = null;
            boolean var30 = false;
            boolean var15 = false;

            try {
                var15 = true;
                File var28 = new File(var27);
                var24 = new BufferedInputStream(new FileInputStream(var28), 1048576);
                var29 = new BufferedOutputStream(new FileOutputStream(var4), 1048576);
                this.a(var24, var28.getName(), var31, var29, new Date(var28.lastModified()), var5, var6, var25);
                var15 = false;
            } catch (PGPException var20) {
                var30 = true;
                throw IOUtil.newPGPException(var20);
            } catch (IOException var21) {
                var30 = true;
                throw var21;
            } finally {
                if (var15) {
                    IOUtil.closeStream(var24);
                    IOUtil.closeStream(var29);
                    if (var32 != null) {
                        var32.delete();
                    }

                    File var26;
                    if (var30 && (var26 = new File(var4)).exists()) {
                        var26.delete();
                    }

                }
            }

            IOUtil.closeStream(var24);
            IOUtil.closeStream(var29);
            if (var32 != null) {
                var32.delete();
            }

        }
    }

    public void encryptFile(String var1, InputStream var2, String var3, boolean var4, boolean var5) throws PGPException, IOException {
        this.a("Encrypting file {0}", var1);
        this.a("Encrypting to {0}", (new File(var3)).getAbsolutePath());
        BufferedInputStream var6 = null;
        BufferedOutputStream var7 = null;
        boolean var8 = false;
        boolean var12 = false;

        try {
            var12 = true;
            File var16 = new File(var1);
            var6 = new BufferedInputStream(new FileInputStream(var16), 1048576);
            var7 = new BufferedOutputStream(new FileOutputStream(var3), 1048576);
            PGPPublicKey var17 = this.a(var2);
            this.a(var6, var16.getName(), new PGPPublicKey[]{var17}, var7, new Date(var16.lastModified()), var4, var5, false);
            var12 = false;
        } catch (PGPException var13) {
            var8 = true;
            throw var13;
        } catch (IOException var14) {
            var8 = true;
            throw var14;
        } finally {
            if (var12) {
                IOUtil.closeStream(var6);
                IOUtil.closeStream(var7);
                File var18;
                if (var8 && (var18 = new File(var3)).exists()) {
                    var18.delete();
                }

            }
        }

        IOUtil.closeStream(var6);
        IOUtil.closeStream(var7);
    }

    /** @deprecated */
    public int encryptFileByUserId(KeyStore var1, String var2, String var3, String var4) {
        try {
            this.encryptFile(var2, var1, var3, var4);
            return 0;
        } catch (Exception var5) {
            return 1;
        }
    }

    public void encryptFile(String var1, KeyStore var2, String var3, String var4, boolean var5, boolean var6) throws PGPException, IOException {
        this.a("Encrypting file {0}", var1);
        this.a("Encrypting to {0}", (new File(var4)).getAbsolutePath());
        InputStream var17 = a(var2, var3);
        FileInputStream var19 = null;
        FileOutputStream var7 = null;
        boolean var8 = false;
        boolean var13 = false;

        try {
            var13 = true;
            File var9 = new File(var1);
            var19 = new FileInputStream(var1);
            var7 = new FileOutputStream(var4);
            this.encryptStream(var19, var9.getName(), (InputStream)var17, var7, var5, var6);
            var13 = false;
        } catch (PGPException var14) {
            var8 = true;
            throw IOUtil.newPGPException(var14);
        } catch (IOException var15) {
            var8 = true;
            throw var15;
        } finally {
            if (var13) {
                IOUtil.closeStream(var19);
                IOUtil.closeStream(var7);
                File var18;
                if (var8 && (var18 = new File(var4)).exists()) {
                    var18.delete();
                }

            }
        }

        IOUtil.closeStream(var19);
        IOUtil.closeStream(var7);
    }

    public void encryptFile(String var1, KeyStore var2, String var3, String var4) throws PGPException, IOException {
        this.encryptFile(var1, var2, var3, var4, false, this.n);
    }

    /** @deprecated */
    public int encryptFileByKeyId(KeyStore var1, String var2, String var3, String var4) {
        try {
            this.encryptFile(var2, var1, Long.decode(var3), var4);
            return 1;
        } catch (Exception var5) {
            return 0;
        }
    }

    public void encryptFile(String var1, KeyStore var2, long var3, String var5, boolean var6, boolean var7) throws PGPException, IOException {
        BufferedInputStream var8 = null;
        BufferedOutputStream var9 = null;
        InputStream var10 = null;
        this.a("Encrypting file {0}", (new File(var1)).getAbsolutePath());
        this.a("Encrypting to {0}", (new File(var5)).getAbsolutePath());
        boolean var11 = false;
        boolean var16 = false;

        try {
            var16 = true;
            File var12 = new File(var1);
            var8 = new BufferedInputStream(new FileInputStream(var1), 1048576);
            var9 = new BufferedOutputStream(new FileOutputStream(var5), 1048576);
            var10 = c(var2, var3);
            PGPPublicKey var20 = this.a(var10);
            this.a(var8, var12.getName(), new PGPPublicKey[]{var20}, var9, new Date(var12.lastModified()), var6, var7, false);
            var16 = false;
        } catch (PGPException var17) {
            var11 = true;
            throw IOUtil.newPGPException(var17);
        } catch (IOException var18) {
            var11 = true;
            throw var18;
        } finally {
            if (var16) {
                IOUtil.closeStream(var8);
                IOUtil.closeStream(var9);
                IOUtil.closeStream(var10);
                File var21;
                if (var11 && (var21 = new File(var5)).exists()) {
                    var21.delete();
                }

            }
        }

        IOUtil.closeStream(var8);
        IOUtil.closeStream(var9);
        IOUtil.closeStream(var10);
    }

    public void encryptFile(String var1, KeyStore var2, long var3, String var5) throws PGPException, IOException {
        this.encryptFile(var1, var2, var3, var5, false, this.n);
    }

    /** @deprecated */
    public boolean verifyFile(String var1, String var2, String var3) throws PGPException, FileIsEncryptedException, IOException {
        BufferedInputStream var4 = null;
        InputStream var5 = null;
        BufferedOutputStream var6 = null;
        this.a("Signature verification of file {0}", var1);
        this.a("Extracting to {0}", (new File(var3)).getAbsolutePath());
        boolean var7 = false;
        boolean var11 = false;

        boolean var15;
        try {
            var11 = true;
            var4 = new BufferedInputStream(new FileInputStream(var1), 1048576);
            var5 = readFileOrAsciiString(var2, "publicKeyFileName");
            var6 = new BufferedOutputStream(new FileOutputStream(var3), 1048576);
            var15 = this.verifyStream(var4, (InputStream)var5, var6);
            var11 = false;
        } catch (PGPException var12) {
            var7 = true;
            throw var12;
        } catch (IOException var13) {
            var7 = true;
            throw var13;
        } finally {
            if (var11) {
                IOUtil.closeStream(var4);
                IOUtil.closeStream(var5);
                IOUtil.closeStream(var6);
                File var16;
                if (var7 && (var16 = new File(var3)).exists()) {
                    var16.delete();
                }

            }
        }

        IOUtil.closeStream(var4);
        IOUtil.closeStream(var5);
        IOUtil.closeStream(var6);
        return var15;
    }

    /** @deprecated */
    public boolean verifyFile(String var1, KeyStore var2, String var3) throws PGPException, FileIsEncryptedException, IOException {
        BufferedInputStream var4 = null;
        FileOutputStream var5 = null;
        this.a("Signature verification of file {0}", var1);
        this.a("Extracting to {0}", (new File(var3)).getAbsolutePath());
        boolean var6 = false;
        boolean var10 = false;

        boolean var14;
        try {
            var10 = true;
            var4 = new BufferedInputStream(new FileInputStream(var1));
            var5 = new FileOutputStream(var3);
            var14 = this.verifyStream(var4, (KeyStore)var2, var5);
            var10 = false;
        } catch (PGPException var11) {
            var6 = true;
            throw var11;
        } catch (IOException var12) {
            var6 = true;
            throw var12;
        } finally {
            if (var10) {
                IOUtil.closeStream(var4);
                IOUtil.closeStream(var5);
                File var15;
                if (var6 && (var15 = new File(var3)).exists()) {
                    var15.delete();
                }

            }
        }

        IOUtil.closeStream(var4);
        IOUtil.closeStream(var5);
        return var14;
    }

    /** @deprecated */
    public boolean verifyFile(String var1, String var2) throws PGPException, FileIsEncryptedException, IOException {
        this.a("Signature verification of file {0}", var1);
        FileInputStream var3 = null;
        InputStream var4 = null;

        boolean var7;
        try {
            var3 = new FileInputStream(var1);
            var4 = readFileOrAsciiString(var2, "publicKeyFileName");
            var7 = this.verifyStream(var3, var4);
        } finally {
            IOUtil.closeStream(var3);
            IOUtil.closeStream(var4);
        }

        return var7;
    }

    /** @deprecated */
    public boolean verifyFile(InputStream var1, InputStream var2) throws PGPException, IOException {
        return this.verifyStream(var1, var2);
    }

    public SignatureCheckResult verifyWithoutExtracting(InputStream var1, InputStream var2) throws PGPException, FileIsEncryptedException, IOException {
        ArmoredInputStream var3;
        if ((var1 = PGPUtil.getDecoderStream(var1)) instanceof ArmoredInputStream && (var3 = (ArmoredInputStream)var1).isClearText()) {
            return (new NamelessClass_1()).b(var3, (KeyStore)null, var2, new DummyStream());
        } else {
            PGPObjectFactory2 var7 = new PGPObjectFactory2(var1);

            Object var6;
            try {
                var6 = var7.nextObject();
            } catch (IOException var5) {
                throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var5);
            }

            if (var6 instanceof PGPMarker) {
                var6 = var7.nextObject();
            }

            a var4 = new a(this, (byte)0);
            if (var6 instanceof PGPEncryptedDataList) {
                throw new FileIsEncryptedException("This file is encrypted. Use the methods <decryptAndVerifySignature> or <decrypt> to open it.");
            } else {
                if (var6 instanceof PGPCompressedData) {
                    this.a((PGPCompressedData)var6, true, var4, (KeyStore)null, var2, new DummyStream());
                } else if (var6 instanceof PGPOnePassSignatureList) {
                    this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var6), var7, (KeyStore)null, var2, new DummyStream(), var4);
                } else if (var6 instanceof PGPSignatureList) {
                    this.a((PGPSignatureList)((PGPSignatureList)var6), var7, (KeyStore)null, var2, new DummyStream(), var4);
                } else {
                    if (!(var6 instanceof PGPLiteralData)) {
                        throw new NonPGPDataException("Unknown message format: " + var6.getClass());
                    }

                    this.a((PGPLiteralData)((PGPLiteralData)var6), (PGPOnePassSignature)null, (OutputStream)(new DummyStream()));
                }

                return var4.a;
            }
        }
    }

    public SignatureCheckResult verifyWithoutExtracting(InputStream var1, KeyStore var2) throws PGPException, FileIsEncryptedException, IOException {
        var1 = PGPUtil.getDecoderStream(var1);
        DummyStream var3 = new DummyStream();
        ArmoredInputStream var4;
        if (var1 instanceof ArmoredInputStream && (var4 = (ArmoredInputStream)var1).isClearText()) {
            return (new NamelessClass_1()).b(var4, var2, (InputStream)null, var3);
        } else {
            PGPObjectFactory2 var8 = new PGPObjectFactory2(var1);

            Object var7;
            try {
                var7 = var8.nextObject();
            } catch (IOException var6) {
                throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var6);
            }

            if (var7 instanceof PGPMarker) {
                var7 = var8.nextObject();
            }

            a var5 = new a(this, (byte)0);
            if (var7 instanceof PGPEncryptedDataList) {
                throw new FileIsEncryptedException("This file is encrypted. Use <decryptAndVerify> or <decrypt> to open it.");
            } else {
                if (var7 instanceof PGPCompressedData) {
                    this.a((PGPCompressedData)var7, true, var5, var2, (InputStream)null, var3);
                } else if (var7 instanceof PGPOnePassSignatureList) {
                    this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var7), var8, var2, (InputStream)null, var3, var5);
                } else if (var7 instanceof PGPSignatureList) {
                    this.a((PGPSignatureList)((PGPSignatureList)var7), var8, var2, (InputStream)null, var3, var5);
                } else {
                    if (!(var7 instanceof PGPLiteralData)) {
                        throw new NonPGPDataException("Unknown message format: " + var7.getClass());
                    }

                    this.a((PGPLiteralData)((PGPLiteralData)var7), (PGPOnePassSignature)null, (OutputStream)var3);
                }

                return var5.a;
            }
        }
    }

    public SignatureCheckResult verifyWithoutExtracting(String var1, String var2) throws IOException, PGPException, FileIsEncryptedException {
        InputStream var3 = null;
        InputStream var4 = null;
        DummyStream var5 = new DummyStream();

        SignatureCheckResult var8;
        try {
            var3 = readFileOrAsciiString(var1, "message");
            var4 = readFileOrAsciiString(var2, "publicKeyFile");
            var8 = this.verifyAndExtract((InputStream)var3, (InputStream)var4, (OutputStream)var5);
        } finally {
            IOUtil.closeStream(var3);
            IOUtil.closeStream(var4);
            IOUtil.closeStream(var5);
        }

        return var8;
    }

    public SignatureCheckResult verifyWithoutExtracting(String var1, KeyStore var2) throws IOException, PGPException, FileIsEncryptedException {
        InputStream var3 = null;
        DummyStream var4 = new DummyStream();

        SignatureCheckResult var7;
        try {
            var3 = readFileOrAsciiString(var1, "message");
            var7 = this.verifyAndExtract((InputStream)var3, (KeyStore)var2, (OutputStream)var4);
        } finally {
            IOUtil.closeStream(var3);
            IOUtil.closeStream(var4);
        }

        return var7;
    }

    public SignatureCheckResult verifyWithoutExtracting(File var1, File var2) throws PGPException, FileIsEncryptedException, IOException {
        return this.verifyWithoutExtracting(var1.getAbsolutePath(), var2.getAbsolutePath());
    }

    public SignatureCheckResult verifyWithoutExtracting(File var1, KeyStore var2) throws PGPException, FileIsEncryptedException, IOException {
        return this.verifyWithoutExtracting(var1.getAbsolutePath(), var2);
    }

    public SignatureCheckResult verifyAndExtract(InputStream var1, InputStream var2, OutputStream var3) throws PGPException, FileIsEncryptedException, IOException {
        ArmoredInputStream var4;
        if ((var1 = PGPUtil.getDecoderStream(var1)) instanceof ArmoredInputStream && (var4 = (ArmoredInputStream)var1).isClearText()) {
            return (new NamelessClass_1()).b(var4, (KeyStore)null, var2, var3);
        } else {
            PGPObjectFactory2 var8 = new PGPObjectFactory2(var1);

            Object var7;
            try {
                var7 = var8.nextObject();
            } catch (IOException var6) {
                throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var6);
            }

            if (var7 instanceof PGPMarker) {
                var7 = var8.nextObject();
            }

            a var5 = new a(this, (byte)0);
            if (var7 instanceof PGPEncryptedDataList) {
                throw new FileIsEncryptedException("This file is encrypted. Use the methods <decryptAndVerifySignature> or <decrypt> to open it.");
            } else {
                if (var7 instanceof PGPCompressedData) {
                    this.a((PGPCompressedData)var7, true, var5, (KeyStore)null, var2, var3);
                } else if (var7 instanceof PGPOnePassSignatureList) {
                    this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var7), var8, (KeyStore)null, var2, var3, var5);
                } else if (var7 instanceof PGPSignatureList) {
                    this.a((PGPSignatureList)((PGPSignatureList)var7), var8, (KeyStore)null, var2, var3, var5);
                } else {
                    if (!(var7 instanceof PGPLiteralData)) {
                        throw new NonPGPDataException("Unknown message format: " + var7.getClass());
                    }

                    this.a((PGPLiteralData)((PGPLiteralData)var7), (PGPOnePassSignature)null, (OutputStream)var3);
                }

                return var5.a;
            }
        }
    }

    public SignatureCheckResult verifyAndExtract(InputStream var1, KeyStore var2, OutputStream var3) throws PGPException, FileIsEncryptedException, IOException {
        ArmoredInputStream var4;
        if ((var1 = PGPUtil.getDecoderStream(var1)) instanceof ArmoredInputStream && (var4 = (ArmoredInputStream)var1).isClearText()) {
            return (new NamelessClass_1()).b(var4, var2, (InputStream)null, var3);
        } else {
            PGPObjectFactory2 var8 = new PGPObjectFactory2(var1);

            Object var7;
            try {
                var7 = var8.nextObject();
            } catch (IOException var6) {
                throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var6);
            }

            if (var7 instanceof PGPMarker) {
                var7 = var8.nextObject();
            }

            a var5 = new a(this, (byte)0);
            if (var7 instanceof PGPEncryptedDataList) {
                throw new FileIsEncryptedException("This file is encrypted. Use <decryptAndVerify> or <decrypt> to open it.");
            } else {
                if (var7 instanceof PGPCompressedData) {
                    this.a((PGPCompressedData)var7, true, var5, var2, (InputStream)null, var3);
                } else if (var7 instanceof PGPOnePassSignatureList) {
                    this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var7), var8, var2, (InputStream)null, var3, var5);
                } else if (var7 instanceof PGPSignatureList) {
                    this.a((PGPSignatureList)((PGPSignatureList)var7), var8, var2, (InputStream)null, var3, var5);
                } else {
                    if (!(var7 instanceof PGPLiteralData)) {
                        throw new NonPGPDataException("Unknown message format: " + var7.getClass());
                    }

                    this.a((PGPLiteralData)((PGPLiteralData)var7), (PGPOnePassSignature)null, (OutputStream)var3);
                }

                return var5.a;
            }
        }
    }

    public SignatureCheckResult verifyAndExtract(String var1, String var2, StringBuffer var3, String var4) throws IOException, PGPException, FileIsEncryptedException {
        if (var3 == null) {
            throw new IllegalArgumentException("The decryptedString parameter cannot be null");
        } else {
            InputStream var5 = null;
            InputStream var6 = null;
            DirectByteArrayOutputStream var7 = null;

            SignatureCheckResult var10;
            try {
                var5 = readFileOrAsciiString(var1, "message");
                var6 = readFileOrAsciiString(var2, "publicKeyFile");
                var7 = new DirectByteArrayOutputStream(1048576);
                var10 = this.verifyAndExtract((InputStream)var5, (InputStream)var6, (OutputStream)var7);
                var3.setLength(0);
                var3.append(new String(var7.getArray(), 0, var7.size(), var4));
                var10 = var10;
            } finally {
                IOUtil.closeStream(var5);
                IOUtil.closeStream(var6);
                IOUtil.closeStream(var7);
            }

            return var10;
        }
    }

    public SignatureCheckResult verifyAndExtract(String var1, String var2, StringBuffer var3) throws IOException, PGPException, FileIsEncryptedException {
        return this.verifyAndExtract(var1, var2, var3, "ASCII");
    }

    public SignatureCheckResult verifyAndExtract(String var1, KeyStore var2, StringBuffer var3, String var4) throws IOException, PGPException, FileIsEncryptedException {
        if (var3 == null) {
            throw new IllegalArgumentException("The decryptedString parameter cannot be null");
        } else {
            InputStream var5 = null;
            DirectByteArrayOutputStream var6 = null;

            SignatureCheckResult var9;
            try {
                var5 = readFileOrAsciiString(var1, "message");
                var6 = new DirectByteArrayOutputStream(1048576);
                var9 = this.verifyAndExtract((InputStream)var5, (KeyStore)var2, (OutputStream)var6);
                var3.setLength(0);
                var3.append(new String(var6.getArray(), 0, var6.size(), var4));
                var9 = var9;
            } finally {
                IOUtil.closeStream(var5);
                IOUtil.closeStream(var6);
            }

            return var9;
        }
    }

    public SignatureCheckResult verifyAndExtract(String var1, KeyStore var2, StringBuffer var3) throws IOException, PGPException, FileIsEncryptedException {
        return this.verifyAndExtract(var1, var2, var3);
    }

    public SignatureCheckResult verifyAndExtract(String var1, String var2, String var3) throws PGPException, FileIsEncryptedException, IOException {
        BufferedInputStream var4 = null;
        InputStream var5 = null;
        BufferedOutputStream var6 = null;
        this.a("Signature verification of file {0}", var1);
        this.a("Extracting to {0}", (new File(var3)).getAbsolutePath());
        boolean var7 = false;
        boolean var11 = false;

        SignatureCheckResult var15;
        try {
            var11 = true;
            var4 = new BufferedInputStream(new FileInputStream(var1), 1048576);
            var5 = readFileOrAsciiString(var2, "publicKeyFileName");
            var6 = new BufferedOutputStream(new FileOutputStream(var3), 1048576);
            var15 = this.verifyAndExtract((InputStream)var4, (InputStream)var5, (OutputStream)var6);
            var11 = false;
        } catch (PGPException var12) {
            var7 = true;
            throw var12;
        } catch (IOException var13) {
            var7 = true;
            throw var13;
        } finally {
            if (var11) {
                IOUtil.closeStream(var4);
                IOUtil.closeStream(var5);
                IOUtil.closeStream(var6);
                File var16;
                if (var7 && (var16 = new File(var3)).exists()) {
                    var16.delete();
                }

            }
        }

        IOUtil.closeStream(var4);
        IOUtil.closeStream(var5);
        IOUtil.closeStream(var6);
        return var15;
    }

    public SignatureCheckResult verifyAndExtract(File var1, File var2, File var3) throws PGPException, FileIsEncryptedException, IOException {
        return this.verifyAndExtract(var1.getAbsolutePath(), var2.getAbsolutePath(), var3.getAbsolutePath());
    }

    public SignatureCheckResult verifyAndExtract(File var1, KeyStore var2, File var3) throws PGPException, FileIsEncryptedException, IOException {
        return this.verifyAndExtract(var1.getAbsolutePath(), var2, var3.getAbsolutePath());
    }

    public SignatureCheckResult verifyAndExtract(String var1, KeyStore var2, String var3) throws PGPException, FileIsEncryptedException, IOException {
        BufferedInputStream var4 = null;
        FileOutputStream var5 = null;
        this.a("Signature verification of file {0}", var1);
        this.a("Extracting to {0}", (new File(var3)).getAbsolutePath());
        boolean var6 = false;
        boolean var10 = false;

        SignatureCheckResult var14;
        try {
            var10 = true;
            var4 = new BufferedInputStream(new FileInputStream(var1));
            var5 = new FileOutputStream(var3);
            var14 = this.verifyAndExtract((InputStream)var4, (KeyStore)var2, (OutputStream)var5);
            var10 = false;
        } catch (PGPException var11) {
            var6 = true;
            throw var11;
        } catch (IOException var12) {
            var6 = true;
            throw var12;
        } finally {
            if (var10) {
                IOUtil.closeStream(var4);
                IOUtil.closeStream(var5);
                File var15;
                if (var6 && (var15 = new File(var3)).exists()) {
                    var15.delete();
                }

            }
        }

        IOUtil.closeStream(var4);
        IOUtil.closeStream(var5);
        return var14;
    }

    /** @deprecated */
    public boolean verifyStream(InputStream var1, InputStream var2) throws PGPException, FileIsEncryptedException, IOException {
        ArmoredInputStream var3;
        if ((var1 = PGPUtil.getDecoderStream(var1)) instanceof ArmoredInputStream && (var3 = (ArmoredInputStream)var1).isClearText()) {
            NamelessClass_1 var5 = new NamelessClass_1();

            try {
                return var5.a(var3, (KeyStore)null, var2, new DummyStream());
            } catch (SignatureException var4) {
                return false;
            }
        } else {
            return this.verifyStream(var1, (InputStream)var2, new DummyStream());
        }
    }

    /** @deprecated */
    public boolean verifyStream(InputStream var1, InputStream var2, OutputStream var3) throws PGPException, FileIsEncryptedException, IOException {
        ArmoredInputStream var4;
        if ((var1 = PGPUtil.getDecoderStream(var1)) instanceof ArmoredInputStream && (var4 = (ArmoredInputStream)var1).isClearText()) {
            NamelessClass_1 var9 = new NamelessClass_1();

            try {
                return var9.a(var4, (KeyStore)null, var2, var3);
            } catch (SignatureException var6) {
                return false;
            }
        } else {
            PGPObjectFactory2 var10 = new PGPObjectFactory2(var1);

            Object var8;
            try {
                var8 = var10.nextObject();
            } catch (IOException var7) {
                throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var7);
            }

            if (var8 instanceof PGPMarker) {
                var8 = var10.nextObject();
            }

            a var5 = new a(this, (byte)0);
            if (var8 instanceof PGPEncryptedDataList) {
                throw new FileIsEncryptedException("This file is encrypted. Use <decryptAndVerify> or <decrypt> to open it.");
            } else {
                if (var8 instanceof PGPCompressedData) {
                    this.a((PGPCompressedData)var8, true, var5, (KeyStore)null, var2, var3);
                } else if (var8 instanceof PGPOnePassSignatureList) {
                    this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var8), var10, (KeyStore)null, var2, var3, var5);
                } else if (var8 instanceof PGPSignatureList) {
                    this.a((PGPSignatureList)((PGPSignatureList)var8), var10, (KeyStore)null, var2, var3, var5);
                } else {
                    if (!(var8 instanceof PGPLiteralData)) {
                        throw new PGPException("Unknown message format: " + var8.getClass());
                    }

                    this.a((PGPLiteralData)((PGPLiteralData)var8), (PGPOnePassSignature)null, (OutputStream)var3);
                }

                return var5.a == SignatureCheckResult.SignatureVerified;
            }
        }
    }

    /** @deprecated */
    public boolean verifyStream(InputStream var1, KeyStore var2, OutputStream var3) throws PGPException, FileIsEncryptedException, IOException {
        ArmoredInputStream var4;
        if ((var1 = PGPUtil.getDecoderStream(var1)) instanceof ArmoredInputStream && (var4 = (ArmoredInputStream)var1).isClearText()) {
            NamelessClass_1 var8 = new NamelessClass_1();

            try {
                return var8.a(var4, var2, (InputStream)null, var3);
            } catch (SignatureException var6) {
                return false;
            }
        } else {
            Object var7;
            PGPObjectFactory2 var9;
            if ((var7 = (var9 = new PGPObjectFactory2(var1)).nextObject()) instanceof PGPMarker) {
                var7 = var9.nextObject();
            }

            a var5 = new a(this, (byte)0);
            if (var7 instanceof PGPEncryptedDataList) {
                throw new FileIsEncryptedException("This file is encrypted. Use <decryptAndVerify> or <decrypt> to open it.");
            } else {
                if (var7 instanceof PGPCompressedData) {
                    this.a((PGPCompressedData)var7, true, var5, var2, (InputStream)null, var3);
                } else if (var7 instanceof PGPOnePassSignatureList) {
                    this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var7), var9, var2, (InputStream)null, var3, var5);
                } else if (var7 instanceof PGPSignatureList) {
                    this.a((PGPSignatureList)((PGPSignatureList)var7), var9, var2, (InputStream)null, var3, var5);
                } else {
                    if (!(var7 instanceof PGPLiteralData)) {
                        throw new NonPGPDataException("Unknown message format: " + var7.getClass().getName());
                    }

                    this.a((PGPLiteralData)((PGPLiteralData)var7), (PGPOnePassSignature)null, (OutputStream)var3);
                }

                return var5.a == SignatureCheckResult.SignatureVerified;
            }
        }
    }

    /** @deprecated */
    public boolean verifyString(String var1, String var2, StringBuffer var3) throws IOException, PGPException, FileIsEncryptedException {
        return this.verifyString(var1, var2, var3, "UTF-8");
    }

    /** @deprecated */
    public boolean verifyString(String var1, String var2, StringBuffer var3, String var4) throws IOException, PGPException, FileIsEncryptedException {
        if (var3 == null) {
            throw new IllegalArgumentException("The decryptedString parameter cannot be null");
        } else {
            ByteArrayInputStream var5 = null;
            InputStream var6 = null;
            DirectByteArrayOutputStream var7 = null;

            boolean var10;
            try {
                var5 = new ByteArrayInputStream(var1.getBytes(var4));
                var6 = readFileOrAsciiString(var2, "publicKeyFileName");
                var7 = new DirectByteArrayOutputStream(1048576);
                var10 = this.verifyStream(var5, (InputStream)var6, var7);
                var3.setLength(0);
                var3.append(new String(var7.getArray(), 0, var7.size(), var4));
            } finally {
                IOUtil.closeStream(var5);
                IOUtil.closeStream(var6);
                IOUtil.closeStream(var7);
            }

            return var10;
        }
    }

    /** @deprecated */
    public boolean decryptAndVerifyString(String var1, String var2, String var3, String var4, StringBuffer var5) throws IOException, PGPException, FileIsEncryptedException {
        return this.decryptAndVerifyString(var1, var2, var3, var4, var5, "UTF-8");
    }

    /** @deprecated */
    public boolean decryptAndVerifyString(String var1, String var2, String var3, String var4, StringBuffer var5, String var6) throws IOException, PGPException {
        if (var5 == null) {
            throw new IllegalArgumentException("The decryptedString parameter cannot be null");
        } else {
            ByteArrayInputStream var7 = null;
            InputStream var8 = null;
            InputStream var9 = null;
            DirectByteArrayOutputStream var10 = null;

            boolean var13;
            try {
                var7 = new ByteArrayInputStream(var1.getBytes("ASCII"));
                var8 = readFileOrAsciiString(var2, "privateKeyFileName");
                var9 = readFileOrAsciiString(var4, "publicKeyFileName");
                var10 = new DirectByteArrayOutputStream(1048576);
                var13 = this.decryptAndVerifyStream(var7, var8, var3, var9, var10);
                var5.setLength(0);
                var5.append(new String(var10.getArray(), 0, var10.size(), var6));
            } finally {
                IOUtil.closeStream(var7);
                IOUtil.closeStream(var8);
                IOUtil.closeStream(var9);
                IOUtil.closeStream(var10);
            }

            return var13;
        }
    }

    public SignatureCheckResult decryptAndVerify(String var1, String var2, String var3, String var4, StringBuffer var5) throws IOException, PGPException, FileIsEncryptedException {
        return this.decryptAndVerify(var1, var2, var3, var4, var5, "UTF-8");
    }

    public SignatureCheckResult decryptAndVerify(String var1, String var2, String var3, String var4, StringBuffer var5, String var6) throws IOException, PGPException {
        if (var5 == null) {
            throw new IllegalArgumentException("The decryptedString parameter cannot be null");
        } else {
            ByteArrayInputStream var7 = null;
            InputStream var8 = null;
            InputStream var9 = null;
            DirectByteArrayOutputStream var10 = null;

            SignatureCheckResult var13;
            try {
                var7 = new ByteArrayInputStream(var1.getBytes("ASCII"));
                var8 = readFileOrAsciiString(var2, "privateKeyFileName");
                var9 = readFileOrAsciiString(var4, "publicKeyFileName");
                var10 = new DirectByteArrayOutputStream(1048576);
                var13 = this.decryptAndVerify((InputStream)var7, (InputStream)var8, var3, (InputStream)var9, (OutputStream)var10);
                var5.setLength(0);
                var5.append(new String(var10.getArray(), 0, var10.size(), var6));
                var13 = var13;
            } finally {
                IOUtil.closeStream(var7);
                IOUtil.closeStream(var8);
                IOUtil.closeStream(var9);
                IOUtil.closeStream(var10);
            }

            return var13;
        }
    }

    /** @deprecated */
    public boolean decryptAndVerifyFile(String var1, KeyStore var2, String var3, String var4) throws PGPException, IOException {
        this.a("Decrypting and verifying file {0}", (new File(var1)).getAbsolutePath());
        this.a("Extracting to {0}", (new File(var4)).getAbsolutePath());
        FileInputStream var5 = null;
        FileOutputStream var6 = null;
        InputStream var7 = null;
        boolean var8 = false;
        boolean var14 = false;

        boolean var20;
        try {
            var14 = true;
            var5 = new FileInputStream(var1);
            var6 = new FileOutputStream(var4);
            var7 = PGPUtil.getDecoderStream(var5);
            Object var9;
            PGPObjectFactory2 var18;
            if ((var9 = (var18 = new PGPObjectFactory2(var7)).nextObject()) instanceof PGPMarker) {
                var9 = var18.nextObject();
            }

            a var10 = new a(this, (byte)0);
            if (var9 instanceof PGPEncryptedDataList) {
                PGPEncryptedDataList var19 = (PGPEncryptedDataList)var9;
                this.a(var19, true, var10, var2, (InputStream)null, var3, (InputStream)null, var6);
            } else {
                if (!(var9 instanceof PGPCompressedData)) {
                    if (var9 == null) {
                        throw new NonPGPDataException("The supplied data is not a valid OpenPGP message");
                    }

                    throw new PGPException("Unknown message format: " + var9);
                }

                this.a((PGPCompressedData)var9, true, var10, var2, (InputStream)null, var6);
            }

            var20 = var10.a == SignatureCheckResult.SignatureVerified;
            var14 = false;
        } catch (PGPException var15) {
            var8 = true;
            throw var15;
        } catch (IOException var16) {
            var8 = true;
            throw var16;
        } finally {
            if (var14) {
                IOUtil.closeStream(var5);
                IOUtil.closeStream(var7);
                IOUtil.closeStream(var6);
                File var21;
                if (var8 && (var21 = new File(var4)).exists()) {
                    var21.delete();
                }

            }
        }

        IOUtil.closeStream(var5);
        IOUtil.closeStream(var7);
        IOUtil.closeStream(var6);
        return var20;
    }

    public SignatureCheckResult decryptAndVerify(String var1, String var2, String var3, String var4, String var5) throws PGPException, IOException {
        this.a("Decrypting and signature verifying file {0}", var1);
        this.a("Extracting to {0}", (new File(var5)).getAbsolutePath());
        FileInputStream var6 = null;
        InputStream var7 = null;
        InputStream var8 = null;
        FileOutputStream var9 = null;
        boolean var10 = false;
        boolean var14 = false;

        SignatureCheckResult var18;
        try {
            var14 = true;
            var6 = new FileInputStream(var1);
            var7 = readFileOrAsciiString(var2, "privateKeyFileName");
            var8 = readFileOrAsciiString(var4, "publicKeyFile");
            var9 = new FileOutputStream(var5);
            var18 = this.decryptAndVerify((InputStream)var6, (InputStream)var7, var3, (InputStream)var8, (OutputStream)var9);
            var14 = false;
        } catch (PGPException var15) {
            var10 = true;
            throw var15;
        } catch (IOException var16) {
            var10 = true;
            throw var16;
        } finally {
            if (var14) {
                IOUtil.closeStream(var6);
                IOUtil.closeStream(var7);
                IOUtil.closeStream(var8);
                IOUtil.closeStream(var9);
                File var19;
                if (var10 && (var19 = new File(var5)).exists()) {
                    var19.delete();
                }

            }
        }

        IOUtil.closeStream(var6);
        IOUtil.closeStream(var7);
        IOUtil.closeStream(var8);
        IOUtil.closeStream(var9);
        return var18;
    }

    public SignatureCheckResult decryptAndVerify(String var1, KeyStore var2, String var3, String var4) throws PGPException, IOException {
        this.a("Decrypting and verifying file {0}", (new File(var1)).getAbsolutePath());
        this.a("Extracting to {0}", (new File(var4)).getAbsolutePath());
        FileInputStream var5 = null;
        FileOutputStream var6 = null;
        boolean var7 = false;
        boolean var11 = false;

        SignatureCheckResult var15;
        try {
            var11 = true;
            var5 = new FileInputStream(var1);
            var6 = new FileOutputStream(var4);
            var15 = this.decryptAndVerify((InputStream)var5, var2, var3, (OutputStream)var6);
            var11 = false;
        } catch (PGPException var12) {
            var7 = true;
            throw var12;
        } catch (IOException var13) {
            var7 = true;
            throw var13;
        } finally {
            if (var11) {
                IOUtil.closeStream(var5);
                IOUtil.closeStream((InputStream)null);
                IOUtil.closeStream(var6);
                File var16;
                if (var7 && (var16 = new File(var4)).exists()) {
                    var16.delete();
                }

            }
        }

        IOUtil.closeStream(var5);
        IOUtil.closeStream((InputStream)null);
        IOUtil.closeStream(var6);
        return var15;
    }

    /** @deprecated */
    public boolean decryptAndVerifyFileTo(String var1, KeyStore var2, String var3, String var4) throws PGPException, IOException {
        FileInputStream var5 = null;
        InputStream var6 = null;
        this.a("Decrypting and verifying file {0}", (new File(var1)).getAbsolutePath());
        this.a("Extracting to {0}", (new File(var4)).getAbsolutePath());

        boolean var12;
        try {
            var6 = PGPUtil.getDecoderStream(var5 = new FileInputStream(var1));
            PGPObjectFactory2 var7;
            Object var8;
            if ((var8 = (var7 = new PGPObjectFactory2(var6)).nextObject()) instanceof PGPMarker) {
                var8 = var7.nextObject();
            }

            a var9 = new a(this, (byte)0);
            if (var8 instanceof PGPEncryptedDataList) {
                PGPEncryptedDataList var13 = (PGPEncryptedDataList)var8;
                this.a(var13, true, var9, var2, (InputStream)null, var3, (InputStream)null, var4, var1);
            } else if (var8 instanceof PGPCompressedData) {
                this.a((PGPCompressedData)((PGPCompressedData)var8), true, var9, (KeyStore)var2, (InputStream)null, (String)var4, (String)var1);
            } else if (var8 instanceof PGPOnePassSignatureList) {
                this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var8), var7, var2, (InputStream)null, var4, var1, var9);
            } else if (var8 instanceof PGPSignatureList) {
                this.a((PGPSignatureList)((PGPSignatureList)var8), var7, var2, (InputStream)null, var4, var1, var9);
            } else {
                if (!(var8 instanceof PGPLiteralData)) {
                    throw new NonPGPDataException("The supplied data is not a valid OpenPGP message");
                }

                this.a((PGPLiteralData)var8, (PGPOnePassSignature)null, var4, var1);
            }

            var12 = var9.a == SignatureCheckResult.SignatureVerified;
        } finally {
            IOUtil.closeStream(var5);
            IOUtil.closeStream(var6);
        }

        return var12;
    }

    public SignatureCheckResult decryptAndVerifyTo(String var1, KeyStore var2, String var3, String var4) throws PGPException, IOException {
        BufferedInputStream var5 = null;
        this.a("Decrypting and verifying file {0}", (new File(var1)).getAbsolutePath());
        this.a("Extracting to {0}", (new File(var4)).getAbsolutePath());

        SignatureCheckResult var8;
        try {
            var5 = new BufferedInputStream(new FileInputStream(var1));
            var8 = this.decryptAndVerifyTo((InputStream)var5, var2, var3, var4);
        } finally {
            IOUtil.closeStream(var5);
        }

        return var8;
    }

    /** @deprecated */
    public boolean decryptAndVerifyStream(InputStream var1, KeyStore var2, String var3, OutputStream var4) throws PGPException, IOException {
        InputStream var5 = null;

        try {
            if (!((var5 = PGPUtil.getDecoderStream((InputStream)var1)) instanceof ArmoredInputStream) || !((ArmoredInputStream)(var1 = (ArmoredInputStream)var5)).isClearText()) {
                PGPObjectFactory2 var16 = new PGPObjectFactory2(var5);

                Object var19;
                try {
                    var19 = var16.nextObject();
                } catch (IOException var12) {
                    throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var12);
                }

                if (var19 instanceof PGPMarker) {
                    this.a("Skipping PGP marker.");
                    var19 = var16.nextObject();
                }

                a var7 = new a(this, (byte)0);
                if (var19 instanceof PGPEncryptedDataList) {
                    PGPEncryptedDataList var20 = (PGPEncryptedDataList)var19;
                    this.a(var20, true, var7, var2, (InputStream)null, var3, (InputStream)null, var4);
                } else if (var19 instanceof PGPCompressedData) {
                    this.a((PGPCompressedData)var19, true, var7, var2, (InputStream)null, var4);
                } else if (var19 instanceof PGPOnePassSignatureList) {
                    this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var19), var16, var2, (InputStream)null, var4, var7);
                } else if (var19 instanceof PGPSignatureList) {
                    this.a((PGPSignatureList)((PGPSignatureList)var19), var16, var2, (InputStream)null, var4, var7);
                } else {
                    if (!(var19 instanceof PGPLiteralData)) {
                        throw new NonPGPDataException("Unknown message format: " + var19);
                    }

                    this.a((PGPLiteralData)((PGPLiteralData)var19), (PGPOnePassSignature)null, (OutputStream)var4);
                }

                boolean var17 = var7.a == SignatureCheckResult.SignatureVerified;
                return var17;
            }

            this.a("Clear text signed data found");
            NamelessClass_1 var6 = new NamelessClass_1();

            try {
                boolean var18 = var6.a((ArmoredInputStream)var1, var2, (InputStream)null, var4);
                return var18;
            } catch (SignatureException var13) {
                this.a("Signature exception: " + var13);
            }
        } catch (PGPException var14) {
            throw IOUtil.newPGPException(var14);
        } finally {
            IOUtil.closeStream(var5);
        }

        return false;
    }

    public SignatureCheckResult decryptAndVerify(InputStream var1, KeyStore var2, String var3, OutputStream var4) throws PGPException, IOException {
        InputStream var5 = null;

        SignatureCheckResult var16;
        try {
            ArmoredInputStream var14;
            if ((var5 = PGPUtil.getDecoderStream(var1)) instanceof ArmoredInputStream && (var14 = (ArmoredInputStream)var5).isClearText()) {
                this.a("Clear text signed data found");
                SignatureCheckResult var18 = (new NamelessClass_1()).b(var14, var2, (InputStream)null, var4);
                return var18;
            }

            PGPObjectFactory2 var15 = new PGPObjectFactory2(var5);

            Object var6;
            try {
                var6 = var15.nextObject();
            } catch (IOException var11) {
                throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var11);
            }

            if (var6 instanceof PGPMarker) {
                this.a("Skipping PGP marker.");
                var6 = var15.nextObject();
            }

            a var7 = new a(this, (byte)0);
            if (var6 instanceof PGPEncryptedDataList) {
                PGPEncryptedDataList var17 = (PGPEncryptedDataList)var6;
                this.a(var17, true, var7, var2, (InputStream)null, var3, (InputStream)null, var4);
            } else if (var6 instanceof PGPCompressedData) {
                this.a((PGPCompressedData)var6, true, var7, var2, (InputStream)null, var4);
            } else if (var6 instanceof PGPOnePassSignatureList) {
                this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var6), var15, var2, (InputStream)null, var4, var7);
            } else if (var6 instanceof PGPSignatureList) {
                this.a((PGPSignatureList)((PGPSignatureList)var6), var15, var2, (InputStream)null, var4, var7);
            } else {
                if (!(var6 instanceof PGPLiteralData)) {
                    throw new NonPGPDataException("Unknown message format: " + var6);
                }

                this.a((PGPLiteralData)((PGPLiteralData)var6), (PGPOnePassSignature)null, (OutputStream)var4);
            }

            var16 = var7.a;
        } catch (PGPException var12) {
            throw IOUtil.newPGPException(var12);
        } finally {
            IOUtil.closeStream(var5);
        }

        return var16;
    }

    /** @deprecated */
    public boolean decryptAndVerifyFile(String var1, String var2, String var3, String var4, String var5) throws PGPException, IOException {
        this.a("Decrypting and signature verifying file {0}", var1);
        this.a("Extracting to {0}", (new File(var5)).getAbsolutePath());
        FileInputStream var6 = null;
        InputStream var7 = null;
        InputStream var8 = null;
        FileOutputStream var9 = null;
        boolean var10 = false;
        boolean var14 = false;

        boolean var18;
        try {
            var14 = true;
            var6 = new FileInputStream(var1);
            var7 = readFileOrAsciiString(var2, "privateKeyFileName");
            var8 = readFileOrAsciiString(var4, "publicKeyFile");
            var9 = new FileOutputStream(var5);
            var18 = this.decryptAndVerifyStream(var6, var7, var3, var8, var9);
            var14 = false;
        } catch (PGPException var15) {
            var10 = true;
            throw var15;
        } catch (IOException var16) {
            var10 = true;
            throw var16;
        } finally {
            if (var14) {
                IOUtil.closeStream(var6);
                IOUtil.closeStream(var7);
                IOUtil.closeStream(var8);
                IOUtil.closeStream(var9);
                File var19;
                if (var10 && (var19 = new File(var5)).exists()) {
                    var19.delete();
                }

            }
        }

        IOUtil.closeStream(var6);
        IOUtil.closeStream(var7);
        IOUtil.closeStream(var8);
        IOUtil.closeStream(var9);
        return var18;
    }

    /** @deprecated */
    public boolean decryptAndVerifyFileTo(String var1, String var2, String var3, String var4, String var5) throws PGPException, IOException {
        this.a("Decrypting and signature verifying file {0}", var1);
        this.a("Extracting to {0}", (new File(var5)).getAbsolutePath());
        FileInputStream var6 = null;
        FileInputStream var7 = null;
        FileInputStream var8 = null;

        boolean var11;
        try {
            var6 = new FileInputStream(var1);
            var7 = new FileInputStream(var2);
            var8 = new FileInputStream(var4);
            var11 = this.decryptAndVerifyStreamTo(var6, var7, var3, var8, var5);
        } finally {
            IOUtil.closeStream(var6);
            IOUtil.closeStream(var7);
            IOUtil.closeStream(var8);
        }

        return var11;
    }

    public SignatureCheckResult decryptAndVerifyTo(String var1, String var2, String var3, String var4, String var5) throws PGPException, IOException {
        this.a("Decrypting and signature verifying file {0}", var1);
        this.a("Extracting to {0}", (new File(var5)).getAbsolutePath());
        FileInputStream var6 = null;
        FileInputStream var7 = null;
        FileInputStream var8 = null;

        SignatureCheckResult var11;
        try {
            var6 = new FileInputStream(var1);
            var7 = new FileInputStream(var2);
            var8 = new FileInputStream(var4);
            var11 = this.decryptAndVerifyTo((InputStream)var6, (InputStream)var7, var3, (InputStream)var8, var5);
        } finally {
            IOUtil.closeStream(var6);
            IOUtil.closeStream(var7);
            IOUtil.closeStream(var8);
        }

        return var11;
    }

    /** @deprecated */
    public boolean decryptAndVerifyStream(InputStream var1, InputStream var2, String var3, InputStream var4, OutputStream var5) throws PGPException, IOException {
        ArmoredInputStream var6;
        if ((var1 = PGPUtil.getDecoderStream(var1)) instanceof ArmoredInputStream && (var6 = (ArmoredInputStream)var1).isClearText()) {
            this.a("Clear text signed data found");
            NamelessClass_1 var20 = new NamelessClass_1();

            try {
                return var20.a(var6, (KeyStore)null, var4, var5);
            } catch (SignatureException var13) {
                this.a("Signature exception: " + var13);
                return false;
            }
        } else {
            boolean var17;
            try {
                PGPObjectFactory2 var18 = new PGPObjectFactory2(var1);

                Object var7;
                try {
                    var7 = var18.nextObject();
                } catch (IOException var14) {
                    throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var14);
                }

                if (var7 instanceof PGPMarker) {
                    this.a("Skipping marker packet.");
                    var7 = var18.nextObject();
                }

                a var8 = new a(this, (byte)0);
                if (var7 instanceof PGPEncryptedDataList) {
                    PGPEncryptedDataList var19 = (PGPEncryptedDataList)var7;
                    this.a(var19, true, var8, (KeyStore)null, var2, var3, var4, var5);
                } else if (var7 instanceof PGPCompressedData) {
                    this.a((PGPCompressedData)var7, true, var8, (KeyStore)null, var4, var5);
                } else if (var7 instanceof PGPOnePassSignatureList) {
                    this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var7), var18, (KeyStore)null, var4, var5, var8);
                } else if (var7 instanceof PGPSignatureList) {
                    this.a((PGPSignatureList)((PGPSignatureList)var7), var18, (KeyStore)null, var4, var5, var8);
                } else {
                    if (!(var7 instanceof PGPLiteralData)) {
                        throw new NonPGPDataException("Unknown message format: " + var7);
                    }

                    this.a((PGPLiteralData)((PGPLiteralData)var7), (PGPOnePassSignature)null, (OutputStream)var5);
                }

                var17 = var8.a == SignatureCheckResult.SignatureVerified;
            } catch (PGPException var15) {
                this.a("PGPException " + var15);
                throw IOUtil.newPGPException(var15);
            } finally {
                IOUtil.closeStream(var1);
            }

            return var17;
        }
    }

    public SignatureCheckResult decryptAndVerify(InputStream var1, String var2, String var3, String var4, OutputStream var5) throws PGPException, IOException {
        InputStream var6 = null;
        InputStream var7 = null;

        SignatureCheckResult var10;
        try {
            var6 = readFileOrAsciiString(var2, "privateKeyFile");
            var7 = readFileOrAsciiString(var4, "publicKeyFile");
            var10 = this.decryptAndVerify(var1, var6, var3, var7, var5);
        } finally {
            IOUtil.closeStream(var6);
            IOUtil.closeStream(var7);
        }

        return var10;
    }

    public SignatureCheckResult decryptAndVerify(InputStream var1, InputStream var2, String var3, InputStream var4, OutputStream var5) throws PGPException, IOException {
        ArmoredInputStream var6;
        if ((var1 = PGPUtil.getDecoderStream(var1)) instanceof ArmoredInputStream && (var6 = (ArmoredInputStream)var1).isClearText()) {
            this.a("Clear text signed data found");

            class NamelessClass_1 {
                public NamelessClass_1() {
                }

                public boolean a(ArmoredInputStream var1, KeyStore var2, InputStream var3, OutputStream var4) throws PGPException, IOException, SignatureException {
                    DirectByteArrayOutputStream var5 = new DirectByteArrayOutputStream(1048576);
                    ByteArrayOutputStream var6 = new ByteArrayOutputStream();
                    int var7 = this.a((ByteArrayOutputStream)var6, (InputStream)var1);
                    byte[] var8 = a();
                    byte[] var9 = var6.toByteArray();
                    var5.write(var9, 0, this.a(var9));
                    var5.write(var8);
                    if (var7 != -1 && var1.isClearText()) {
                        while(var7 != -1 && var1.isClearText()) {
                            var7 = this.a(var6, var7, var1);
                            var9 = var6.toByteArray();
                            var5.write(var9, 0, this.a(var9));
                            var5.write(var8);
                        }
                    }

                    var5.close();
                    PGPSignatureList var12 = (PGPSignatureList)(new PGPObjectFactory2(var1)).nextObject();
                    PGPSignature var18 = null;
                    PGPPublicKey var17 = null;

                    for(int var19 = 0; var19 != var12.size(); ++var19) {
                        var18 = var12.get(var19);
                        if (var3 != null) {
                            var17 = PGPLibs.readPublicVerificationKey(var3, var18.getKeyID());
                        } else {
                            var17 = PGPLibs.readPublicVerificationKey(var2, var18.getKeyID());
                        }

                        if (var17 != null) {
                            PGPLibs.this.a("Signed with key {0}", KeyPairInformation.keyId2Hex(var18.getKeyID()));
                            break;
                        }

                        PGPLibs.this.a("Signed with unknown key {0}", KeyPairInformation.keyId2Hex(var18.getKeyID()));
                    }

                    if (var17 == null) {
                        throw new PGPException("No public key could be found for signature.");
                    } else {
                        try {
                            var18.init(PGPLibs.this.a.CreatePGPContentVerifierBuilderProvider(), var17);
                        } catch (PGPException var10) {
                            throw IOUtil.newPGPException(var10);
                        }

                        ByteArrayInputStream var20 = new ByteArrayInputStream(var5.getArray(), 0, var5.size());
                        var7 = this.a((ByteArrayOutputStream)var6, (InputStream)var20);
                        this.a(var18, var6.toByteArray());
                        if (var7 != -1) {
                            do {
                                var7 = this.a(var6, var7, var20);
                                var18.update((byte)13);
                                var18.update((byte)10);
                                this.a(var18, var6.toByteArray());
                            } while(var7 != -1);
                        }

                        boolean var13 = false;

                        try {
                            if (var18.verify()) {
                                var13 = true;
                            }
                        } catch (PGPException var11) {
                            throw IOUtil.newPGPException(var11);
                        }

                        ByteArrayInputStream var14 = new ByteArrayInputStream(var5.getArray(), 0, var5.size());
                        byte[] var16 = new byte[1048576];

                        int var15;
                        while((var15 = var14.read(var16)) > 0) {
                            var4.write(var16, 0, var15);
                        }

                        return var13;
                    }
                }

                public SignatureCheckResult b(ArmoredInputStream var1, KeyStore var2, InputStream var3, OutputStream var4) throws PGPException, IOException {
                    SignatureCheckResult var10000 = SignatureCheckResult.NoSignatureFound;
                    DirectByteArrayOutputStream var5 = new DirectByteArrayOutputStream(1048576);
                    ByteArrayOutputStream var6 = new ByteArrayOutputStream();
                    int var7 = this.a((ByteArrayOutputStream)var6, (InputStream)var1);
                    byte[] var8 = a();
                    byte[] var9 = var6.toByteArray();
                    var5.write(var9, 0, this.a(var9));
                    var5.write(var8);
                    if (var7 != -1 && var1.isClearText()) {
                        while(var7 != -1 && var1.isClearText()) {
                            var7 = this.a(var6, var7, var1);
                            var9 = var6.toByteArray();
                            var5.write(var9, 0, this.a(var9));
                            var5.write(var8);
                        }
                    }

                    var5.close();
                    PGPSignatureList var11 = (PGPSignatureList)(new PGPObjectFactory2(var1)).nextObject();
                    PGPSignature var17 = null;
                    PGPPublicKey var16 = null;

                    for(int var18 = 0; var18 != var11.size(); ++var18) {
                        var17 = var11.get(var18);
                        if (var3 != null) {
                            var16 = PGPLibs.readPublicVerificationKey(var3, var17.getKeyID());
                        } else {
                            var16 = PGPLibs.readPublicVerificationKey(var2, var17.getKeyID());
                        }

                        if (var16 != null) {
                            PGPLibs.this.a("Signed with key {0}", KeyPairInformation.keyId2Hex(var17.getKeyID()));
                            break;
                        }

                        PGPLibs.this.a("Signed with unknown key {0}", KeyPairInformation.keyId2Hex(var17.getKeyID()));
                    }

                    if (var16 == null) {
                        var10000 = SignatureCheckResult.PublicKeyNotMatching;
                    }

                    PGPLibs.this.a.initVerify(var17, var16);
                    ByteArrayInputStream var19 = new ByteArrayInputStream(var5.getArray(), 0, var5.size());
                    var7 = this.a((ByteArrayOutputStream)var6, (InputStream)var19);
                    this.a(var17, var6.toByteArray());
                    if (var7 != -1) {
                        do {
                            var7 = this.a(var6, var7, var19);
                            var17.update((byte)13);
                            var17.update((byte)10);
                            this.a(var17, var6.toByteArray());
                        } while(var7 != -1);
                    }

                    SignatureCheckResult var12;
                    try {
                        if (var17.verify()) {
                            var12 = SignatureCheckResult.SignatureVerified;
                        } else {
                            var12 = SignatureCheckResult.SignatureBroken;
                        }
                    } catch (PGPException var10) {
                        throw IOUtil.newPGPException(var10);
                    }

                    ByteArrayInputStream var13 = new ByteArrayInputStream(var5.getArray(), 0, var5.size());
                    byte[] var15 = new byte[1048576];

                    int var14;
                    while((var14 = var13.read(var15)) > 0) {
                        var4.write(var15, 0, var14);
                    }

                    return var12;
                }

                public String a(String var1, PGPSecretKey var2, String var3, int var4) throws IOException, PGPException {
                    ByteArrayInputStream var6 = new ByteArrayInputStream(var1.getBytes("UTF-8"));
                    DirectByteArrayOutputStream var5 = new DirectByteArrayOutputStream(1048576);
                    this.a(var6, var2, var3, var4, var5);
                    return new String(var5.getArray(), 0, var5.size());
                }

                public String b(String var1, PGPSecretKey var2, String var3, int var4) throws IOException, PGPException {
                    ByteArrayInputStream var6 = new ByteArrayInputStream(var1.getBytes("UTF-8"));
                    DirectByteArrayOutputStream var5 = new DirectByteArrayOutputStream(1048576);
                    this.b(var6, var2, var3, var4, var5);
                    return new String(var5.getArray(), 0, var5.size());
                }

                public void a(InputStream var1, PGPSecretKey var2, String var3, int var4, OutputStream var5) throws IOException, PGPException, WrongPasswordException {
                    PGPPrivateKey var13 = BaseLib.extractPrivateKey(var2, var3);

                    PGPSignatureGenerator var6;
                    try {
                        (var6 = new PGPSignatureGenerator(PGPLib.this.a.CreatePGPContentSignerBuilder(var2.getPublicKey().getAlgorithm(), var4))).init(1, var13);
                    } catch (PGPException var8) {
                        throw IOUtil.newPGPException(var8);
                    }

                    PGPSignatureSubpacketGenerator var14 = new PGPSignatureSubpacketGenerator();
                    Iterator var10;
                    if ((var10 = var2.getPublicKey().getUserIDs()).hasNext()) {
                        String var11 = (String)var10.next();
                        PGPLib.this.a("Signing for User Id {0}", var11);
                        var14.setSignerUserID(false, var11);
                        var6.setHashedSubpackets(var14.generate());
                    }

                    ArmoredOutputStream var12 = new ArmoredOutputStream(var5);
                    PGPLib.this.a((OutputStream)var12);
                    var12.beginClearText(var4);
                    ByteArrayOutputStream var15 = new ByteArrayOutputStream();
                    var4 = this.a(var15, var1);
                    int var16 = 1;
                    this.a(var12, (PGPSignatureGenerator)var6, var15.toByteArray());
                    if (var4 != -1) {
                        do {
                            ++var16;
                            var4 = this.a(var15, var4, var1);
                            var6.update((byte)13);
                            var6.update((byte)10);
                            this.a(var12, (PGPSignatureGenerator)var6, var15.toByteArray());
                        } while(var4 != -1);
                    }

                    if (var16 == 1 && !a(var15.toByteArray()[var15.toByteArray().length - 1])) {
                        var12.write(13);
                        var12.write(10);
                    }

                    var12.endClearText();
                    BCPGOutputStream var9 = new BCPGOutputStream(var12);

                    try {
                        var6.generate().encode(var9);
                    } catch (PGPException var7) {
                        throw IOUtil.newPGPException(var7);
                    }

                    var12.close();
                }

                public void b(InputStream var1, PGPSecretKey var2, String var3, int var4, OutputStream var5) throws IOException, PGPException {
                    PGPPrivateKey var13 = BaseLib.extractPrivateKey(var2, var3);

                    PGPV3SignatureGenerator var6;
                    try {
                        (var6 = new PGPV3SignatureGenerator(PGPLib.this.a.CreatePGPContentSignerBuilder(var2.getPublicKey().getAlgorithm(), var4))).init(1, var13);
                    } catch (PGPException var8) {
                        throw IOUtil.newPGPException(var8);
                    }

                    PGPSignatureSubpacketGenerator var14 = new PGPSignatureSubpacketGenerator();
                    Iterator var10;
                    if ((var10 = var2.getPublicKey().getUserIDs()).hasNext()) {
                        String var11 = (String)var10.next();
                        PGPLib.this.a("Signing for User Id {0}", var11);
                        var14.setSignerUserID(false, var11);
                    }

                    ArmoredOutputStream var12 = new ArmoredOutputStream(var5);
                    PGPLib.this.a((OutputStream)var12);
                    var12.beginClearText(var4);
                    ByteArrayOutputStream var15 = new ByteArrayOutputStream();
                    var4 = this.a(var15, var1);
                    this.a(var12, (PGPV3SignatureGenerator)var6, var15.toByteArray());
                    if (var4 != -1) {
                        do {
                            var4 = this.a(var15, var4, var1);
                            var6.update((byte)13);
                            var6.update((byte)10);
                            this.a(var12, (PGPV3SignatureGenerator)var6, var15.toByteArray());
                        } while(var4 != -1);
                    }

                    var12.endClearText();
                    BCPGOutputStream var9 = new BCPGOutputStream(var12);

                    try {
                        var6.generate().encode(var9);
                    } catch (PGPException var7) {
                        throw IOUtil.newPGPException(var7);
                    }

                    var12.close();
                }

                private static byte[] a() {
                    String var0;
                    byte[] var1 = new byte[(var0 = System.getProperty("line.separator")).length()];

                    for(int var2 = 0; var2 != var1.length; ++var2) {
                        var1[var2] = (byte)var0.charAt(var2);
                    }

                    return var1;
                }

                private int a(byte[] var1) {
                    int var2;
                    for(var2 = var1.length - 1; var2 >= 0 && a(var1[var2]); --var2) {
                    }

                    return var2 + 1;
                }

                private void a(OutputStream var1, PGPSignatureGenerator var2, byte[] var3) throws PGPException, IOException {
                    int var4;
                    if ((var4 = this.b(var3)) > 0) {
                        var2.update(var3, 0, var4);
                    }

                    var1.write(var3, 0, var3.length);
                }

                private void a(OutputStream var1, PGPV3SignatureGenerator var2, byte[] var3) throws PGPException, IOException {
                    int var4;
                    if ((var4 = this.b(var3)) > 0) {
                        var2.update(var3, 0, var4);
                    }

                    var1.write(var3, 0, var3.length);
                }

                private void a(PGPSignature var1, byte[] var2) throws IOException {
                    int var3;
                    if ((var3 = this.b(var2)) > 0) {
                        var1.update(var2, 0, var3);
                    }

                }

                private int b(byte[] var1) {
                    int var2;
                    byte var3;
                    for(var2 = var1.length - 1; var2 >= 0 && ((var3 = var1[var2]) == 13 || var3 == 10 || var3 == 9 || var3 == 32); --var2) {
                    }

                    return var2 + 1;
                }

                private static boolean a(byte var0) {
                    return var0 == 13 || var0 == 10;
                }

                private int a(ByteArrayOutputStream var1, InputStream var2) throws IOException {
                    var1.reset();
                    int var3 = -1;

                    int var4;
                    while((var4 = var2.read()) >= 0) {
                        var1.write(var4);
                        if (var4 == 13 || var4 == 10) {
                            var3 = b(var1, var4, var2);
                            break;
                        }
                    }

                    return var3;
                }

                private int a(ByteArrayOutputStream var1, int var2, InputStream var3) throws IOException {
                    var1.reset();
                    int var4 = var2;

                    do {
                        var1.write(var4);
                        if (var4 == 13 || var4 == 10) {
                            var2 = b(var1, var4, var3);
                            break;
                        }
                    } while((var4 = var3.read()) >= 0);

                    if (var4 < 0) {
                        var2 = -1;
                    }

                    return var2;
                }

                private static int b(ByteArrayOutputStream var0, int var1, InputStream var2) throws IOException {
                    int var3 = var2.read();
                    if (var1 == 13 && var3 == 10) {
                        var0.write(var3);
                        var3 = var2.read();
                    }

                    return var3;
                }
            }

            return (new NamelessClass_1()).b(var6, (KeyStore)null, var4, var5);
        } else {
            SignatureCheckResult var15;
            try {
                PGPObjectFactory2 var16 = new PGPObjectFactory2(var1);

                Object var7;
                try {
                    var7 = var16.nextObject();
                } catch (IOException var12) {
                    throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var12);
                }

                if (var7 instanceof PGPMarker) {
                    this.a("Skipping marker packet.");
                    var7 = var16.nextObject();
                }

                a var8 = new a(this, (byte)0);
                if (var7 instanceof PGPEncryptedDataList) {
                    PGPEncryptedDataList var17 = (PGPEncryptedDataList)var7;
                    this.a(var17, true, var8, (KeyStore)null, var2, var3, var4, var5);
                } else if (var7 instanceof PGPCompressedData) {
                    this.a((PGPCompressedData)var7, true, var8, (KeyStore)null, var4, var5);
                } else if (var7 instanceof PGPOnePassSignatureList) {
                    this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var7), var16, (KeyStore)null, var4, var5, var8);
                } else if (var7 instanceof PGPSignatureList) {
                    this.a((PGPSignatureList)((PGPSignatureList)var7), var16, (KeyStore)null, var4, var5, var8);
                } else {
                    if (!(var7 instanceof PGPLiteralData)) {
                        throw new NonPGPDataException("Unknown message format: " + var7);
                    }

                    this.a((PGPLiteralData)((PGPLiteralData)var7), (PGPOnePassSignature)null, (OutputStream)var5);
                }

                var15 = var8.a;
            } catch (PGPException var13) {
                this.a("PGPException " + var13);
                throw IOUtil.newPGPException(var13);
            } finally {
                IOUtil.closeStream(var1);
            }

            return var15;
        }
    }

    /** @deprecated */
    public boolean decryptAndVerifyStreamTo(InputStream var1, InputStream var2, String var3, InputStream var4, String var5) throws PGPException, IOException {
        this.a("Decrypting and signature verifying of stream data to {0}", (new File(var5)).getAbsolutePath());
        InputStream var6 = null;

        boolean var17;
        try {
            var6 = PGPUtil.getDecoderStream(var1);
            PGPObjectFactory2 var15 = new PGPObjectFactory2(var6);

            Object var7;
            try {
                var7 = var15.nextObject();
            } catch (IOException var12) {
                throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var12);
            }

            if (var7 instanceof PGPMarker) {
                this.a("Skipping marker packet.");
                var7 = var15.nextObject();
            }

            a var8 = new a(this, (byte)0);
            if (var7 instanceof PGPEncryptedDataList) {
                PGPEncryptedDataList var16 = (PGPEncryptedDataList)var7;
                this.a(var16, true, var8, (KeyStore)null, var2, var3, var4, var5, (String)null);
            } else if (var7 instanceof PGPCompressedData) {
                this.a((PGPCompressedData)((PGPCompressedData)var7), true, var8, (KeyStore)null, (InputStream)var4, (String)var5, (String)null);
            } else if (var7 instanceof PGPOnePassSignatureList) {
                this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var7), var15, (KeyStore)null, var4, var5, (String)null, var8);
            } else if (var7 instanceof PGPSignatureList) {
                this.a((PGPSignatureList)((PGPSignatureList)var7), var15, (KeyStore)null, var4, var5, (String)null, var8);
            } else {
                if (!(var7 instanceof PGPLiteralData)) {
                    throw new NonPGPDataException("Unknown message format: " + var7);
                }

                this.a((PGPLiteralData)var7, (PGPOnePassSignature)null, var5, (String)null);
            }

            var17 = var8.a == SignatureCheckResult.SignatureVerified;
        } catch (PGPException var13) {
            throw IOUtil.newPGPException(var13);
        } finally {
            IOUtil.closeStream(var6);
        }

        return var17;
    }

    public SignatureCheckResult decryptAndVerifyTo(InputStream var1, InputStream var2, String var3, InputStream var4, String var5) throws PGPException, IOException {
        this.a("Decrypting and signature verifying of stream data to {0}", (new File(var5)).getAbsolutePath());
        InputStream var6 = null;

        SignatureCheckResult var17;
        try {
            var6 = PGPUtil.getDecoderStream(var1);
            PGPObjectFactory2 var15 = new PGPObjectFactory2(var6);

            Object var7;
            try {
                var7 = var15.nextObject();
            } catch (IOException var12) {
                throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var12);
            }

            if (var7 instanceof PGPMarker) {
                this.a("Skipping marker packet.");
                var7 = var15.nextObject();
            }

            a var8 = new a(this, (byte)0);
            if (var7 instanceof PGPEncryptedDataList) {
                PGPEncryptedDataList var16 = (PGPEncryptedDataList)var7;
                this.a(var16, true, var8, (KeyStore)null, var2, var3, var4, var5, (String)null);
            } else if (var7 instanceof PGPCompressedData) {
                this.a((PGPCompressedData)((PGPCompressedData)var7), true, var8, (KeyStore)null, (InputStream)var4, (String)var5, (String)null);
            } else if (var7 instanceof PGPOnePassSignatureList) {
                this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var7), var15, (KeyStore)null, var4, var5, (String)null, var8);
            } else if (var7 instanceof PGPSignatureList) {
                this.a((PGPSignatureList)((PGPSignatureList)var7), var15, (KeyStore)null, var4, var5, (String)null, var8);
            } else {
                if (!(var7 instanceof PGPLiteralData)) {
                    throw new NonPGPDataException("Unknown message format: " + var7);
                }

                this.a((PGPLiteralData)var7, (PGPOnePassSignature)null, var5, (String)null);
            }

            var17 = var8.a;
        } catch (PGPException var13) {
            throw IOUtil.newPGPException(var13);
        } finally {
            IOUtil.closeStream(var6);
        }

        return var17;
    }

    /** @deprecated */
    public boolean decryptAndVerifyStreamTo(InputStream var1, KeyStore var2, String var3, String var4) throws PGPException, IOException {
        this.a("Decrypting and signature verifying of stream data to {0}", (new File(var4)).getAbsolutePath());
        InputStream var5 = null;

        boolean var14;
        try {
            var5 = PGPUtil.getDecoderStream(var1);
            Object var6;
            PGPObjectFactory2 var12;
            if ((var6 = (var12 = new PGPObjectFactory2(var5)).nextObject()) instanceof PGPMarker) {
                var6 = var12.nextObject();
            }

            a var7 = new a(this, (byte)0);
            if (var6 instanceof PGPEncryptedDataList) {
                PGPEncryptedDataList var13 = (PGPEncryptedDataList)var6;
                this.a(var13, true, var7, var2, (InputStream)null, var3, (InputStream)null, var4, (String)null);
            } else {
                if (!(var6 instanceof PGPCompressedData)) {
                    if (var6 == null) {
                        throw new NonPGPDataException("The supplied data is not a valid OpenPGP message");
                    }

                    throw new PGPException("Unknown message format: " + var6);
                }

                this.a((PGPCompressedData)((PGPCompressedData)var6), true, var7, (KeyStore)var2, (InputStream)null, (String)var4, (String)null);
            }

            var14 = var7.a == SignatureCheckResult.SignatureVerified;
        } catch (PGPException var10) {
            throw IOUtil.newPGPException(var10);
        } finally {
            IOUtil.closeStream(var5);
        }

        return var14;
    }

    public SignatureCheckResult decryptAndVerifyTo(InputStream var1, KeyStore var2, String var3, String var4) throws PGPException, IOException {
        this.a("Decrypting and signature verifying of stream data to {0}", (new File(var4)).getAbsolutePath());
        InputStream var5 = null;

        SignatureCheckResult var14;
        try {
            var5 = PGPUtil.getDecoderStream(var1);
            Object var6;
            PGPObjectFactory2 var12;
            if ((var6 = (var12 = new PGPObjectFactory2(var5)).nextObject()) instanceof PGPMarker) {
                var6 = var12.nextObject();
            }

            a var7 = new a(this, (byte)0);
            if (var6 instanceof PGPEncryptedDataList) {
                PGPEncryptedDataList var13 = (PGPEncryptedDataList)var6;
                this.a(var13, true, var7, var2, (InputStream)null, var3, (InputStream)null, var4, (String)null);
            } else {
                if (!(var6 instanceof PGPCompressedData)) {
                    if (var6 == null) {
                        throw new NonPGPDataException("The supplied data is not a valid OpenPGP message");
                    }

                    throw new PGPException("Unknown message format: " + var6);
                }

                this.a((PGPCompressedData)((PGPCompressedData)var6), true, var7, (KeyStore)var2, (InputStream)null, (String)var4, (String)null);
            }

            var14 = var7.a;
        } catch (PGPException var10) {
            throw IOUtil.newPGPException(var10);
        } finally {
            IOUtil.closeStream(var5);
        }

        return var14;
    }

    private void a(InputStream var1, String var2, PGPPublicKey[] var3, OutputStream var4, Date var5, boolean var6, boolean var7, boolean var8) throws PGPException, IOException {
        try {
            if (var6) {
                var4 = new ArmoredOutputStream((OutputStream)var4);
                this.a((OutputStream)var4);
            }

            int var9 = KeyStore.a(this.d);
            int var10 = KeyStore.a(this.c);
            if (var3.length == 1) {
                var9 = this.f(var3[0]);
                var10 = this.d(var3[0]);
                this.a("Encrypting with cypher {0}", KeyStore.c(var10));
                this.a("Compression is {0}", KeyStore.a(var9));
            }

            if (var8) {
                BCPGOutputStream var26;
                a(var26 = new BCPGOutputStream((OutputStream)var4));
                var26.flush();
            }

            PGPEncryptedDataGenerator var27 = new PGPEncryptedDataGenerator(this.a.CreatePGPDataEncryptorBuilder(var10, var7, a()));

            OutputStream var23;
            try {
                int var24 = 0;

                while(true) {
                    if (var24 >= var3.length) {
                        var23 = var27.open((OutputStream)var4, new byte[1048576]);
                        break;
                    }

                    this.a("Encrypting with key {0} ", KeyPairInformation.keyId2Hex(var3[var24].getKeyID()));
                    var27.addMethod(this.a.CreatePublicKeyKeyEncryptionMethodGenerator(var3[var24]));
                    ++var24;
                }
            } catch (PGPException var21) {
                throw IOUtil.newPGPException(var21);
            }

            try {
                PGPCompressedDataGenerator var25 = new PGPCompressedDataGenerator(var9);
                PGPLiteralDataGenerator var28 = new PGPLiteralDataGenerator();
                OutputStream var29 = null;

                try {
                    if (var9 == 0) {
                        var29 = var28.open(var23, 'b', var2, var5, new byte[1048576]);
                        pipeAll(var1, var29);
                    } else {
                        var29 = var28.open(var25.open(var23), 'b', var2, var5, new byte[1048576]);
                        pipeAll(var1, var29);
                    }
                } finally {
                    var28.close();
                    IOUtil.closeStream(var29);
                    IOUtil.closeStream(var1);
                    var25.close();
                }
            } finally {
                IOUtil.closeStream(var23);
                if (var6) {
                    IOUtil.closeStream((OutputStream)var4);
                }

            }

        } catch (IOException var22) {
            throw var22;
        }
    }

    private void a(InputStream var1, String var2, String var3, OutputStream var4, boolean var5, boolean var6) throws PGPException {
        try {
            Object var7 = null;
            if (var5) {
                var7 = var4;
                var4 = new ArmoredOutputStream((OutputStream)var4);
                this.a((OutputStream)var4);
            }

            int var17 = KeyStore.a(this.d);
            int var8 = KeyStore.a(this.c);
            PGPEncryptedDataGenerator var18 = new PGPEncryptedDataGenerator(this.a.CreatePGPDataEncryptorBuilder(var8, var6, a()));

            OutputStream var16;
            try {
                var18.addMethod(this.a.CreatePBEKeyEncryptionMethodGenerator(var3));
                this.a("Encrypting with password");
                var16 = var18.open((OutputStream)var4, new byte[1048576]);
            } catch (PGPException var13) {
                throw IOUtil.newPGPException(var13);
            }

            try {
                PGPCompressedDataGenerator var19 = new PGPCompressedDataGenerator(var17);
                PGPLiteralDataGenerator var20 = new PGPLiteralDataGenerator();
                OutputStream var15;
                if (var17 == 0) {
                    var15 = var20.open(var16, 'b', var2, new Date(), new byte[1048576]);
                    pipeAll(var1, var15);
                } else {
                    var15 = var20.open(var19.open(var16), 'b', var2, new Date(), new byte[1048576]);
                    pipeAll(var1, var15);
                }

                IOUtil.closeStream(var15);
                var19.close();
            } finally {
                IOUtil.closeStream(var1);
                IOUtil.closeStream(var16);
                IOUtil.closeStream((OutputStream)var4);
                IOUtil.closeStream((OutputStream)var7);
            }

        } catch (IOException var14) {
            throw new PGPException(var14.getMessage(), var14);
        }
    }

    private String a(PGPLiteralData var1, PGPSignature var2, OutputStream var3) throws IOException {
        String var4 = var1.getFileName();
        this.a("Found literal data packet");
        this.a("Decrypted file original name is {0}", var4);

        try {
            InputStream var9 = var1.getInputStream();
            byte[] var5 = new byte[1048576];

            int var6;
            while((var6 = var9.read(var5, 0, var5.length)) >= 0) {
                if (var2 != null) {
                    var2.update(var5, 0, var6);
                }

                if (var3 != null) {
                    var3.write(var5, 0, var6);
                }
            }

            IOUtil.closeStream(var9);
        } finally {
            IOUtil.closeStream(var3);
        }

        return var4;
    }

    private String[] a(PGPLiteralData var1, PGPSignature var2, String var3, String var4) throws IOException {
        this.a("Found literal data packet");
        String var5;
        if ((var5 = var1.getFileName()).toUpperCase().endsWith(".TAR") && this.l) {
            this.a("Found multiple file archive");
            return (new TarInputStream(var1.getInputStream())).extractAll(var3);
        } else {
            FileOutputStream var6 = null;

            try {
                if (var5 == null || "".equals(var5)) {
                    if (var4 != null && !"".equals(var4)) {
                        if ((var5 = (new File(var4)).getName()).lastIndexOf(".") > 0) {
                            var5 = var5.substring(0, var5.lastIndexOf("."));
                        }
                    } else {
                        var5 = "output";
                    }
                }

                var6 = new FileOutputStream(var3 + File.separator + var5);
                this.a("Extracting to {0}", var3 + File.separator + var5);
                InputStream var9 = var1.getInputStream();

                int var11;
                for(byte[] var10 = new byte[1048576]; (var11 = var9.read(var10, 0, var10.length)) >= 0; var6.write(var10, 0, var11)) {
                    if (var2 != null) {
                        var2.update(var10, 0, var11);
                    }
                }

                IOUtil.closeStream(var9);
            } finally {
                IOUtil.closeStream(var6);
            }

            return new String[]{var5};
        }
    }

    private String a(PGPLiteralData var1, PGPOnePassSignature var2, OutputStream var3) throws IOException {
        String var4 = var1.getFileName();
        InputStream var9 = var1.getInputStream();
        this.a("Found literal data packet");
        this.a("Decrypted file original name is {0}", var4);
        byte[] var5 = new byte[1048576];

        try {
            int var6;
            while((var6 = var9.read(var5, 0, var5.length)) >= 0) {
                if (var2 != null) {
                    var2.update(var5, 0, var6);
                }

                if (var3 != null) {
                    var3.write(var5, 0, var6);
                }
            }

            IOUtil.closeStream(var9);
        } finally {
            IOUtil.closeStream(var3);
        }

        return var4;
    }

    private String[] a(PGPLiteralData var1, PGPOnePassSignature var2, String var3, String var4) throws IOException {
        this.a("Found literal data packet");
        String var5;
        if ((var5 = var1.getFileName()).toUpperCase().endsWith(".TAR") && this.l) {
            this.a("Found multiple file archive");
            return (new TarInputStream(var1.getInputStream())).extractAll(var3);
        } else {
            InputStream var6 = var1.getInputStream();
            this.a("Decrypted file original name is {0}", var1.getFileName());
            if (var5 == null || "".equals(var5)) {
                if (var4 != null && !"".equals(var4)) {
                    if ((var5 = (new File(var4)).getName()).lastIndexOf(".") > 0) {
                        var5 = var5.substring(0, var5.lastIndexOf("."));
                    }
                } else {
                    var5 = "output";
                }
            }

            this.a("Extracting to {0}", var3 + File.separator + var5);
            FileOutputStream var9 = new FileOutputStream(var3 + File.separator + var5);
            byte[] var10 = new byte[1048576];

            try {
                int var11;
                for(; (var11 = var6.read(var10, 0, var10.length)) >= 0; var9.write(var10, 0, var11)) {
                    if (var2 != null) {
                        var2.update(var10, 0, var11);
                    }
                }

                IOUtil.closeStream(var6);
            } finally {
                IOUtil.closeStream(var9);
            }

            return new String[]{var5};
        }
    }

    private String a(PGPCompressedData var1, boolean var2, a var3, KeyStore var4, InputStream var5, OutputStream var6) throws PGPException, IOException {
        this.a("Decrypted data compression algorithm is {0}", KeyStore.a(var1.getAlgorithm()));

        BufferedInputStream var13;
        try {
            var13 = new BufferedInputStream(var1.getDataStream());
        } catch (PGPException var11) {
            throw IOUtil.newPGPException(var11);
        }

        String var14;
        try {
            PGPObjectFactory2 var7;
            Object var8;
            if ((var8 = (var7 = new PGPObjectFactory2(var13)).nextObject()) instanceof PGPLiteralData) {
                var14 = this.a((PGPLiteralData)((PGPLiteralData)var8), (PGPOnePassSignature)null, (OutputStream)var6);
                return var14;
            }

            if (!(var8 instanceof PGPOnePassSignatureList)) {
                if (!(var8 instanceof PGPSignatureList)) {
                    throw new PGPException("Unknown message format: " + var8.getClass().getName());
                }

                if (var2) {
                    var14 = this.a((PGPSignatureList)((PGPSignatureList)var8), var7, var4, var5, var6, var3);
                    return var14;
                }

                var14 = this.a((PGPSignatureList)((PGPSignatureList)var8), var7, (KeyStore)null, (InputStream)null, var6, var3);
                return var14;
            }

            if (var2) {
                var14 = this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var8), var7, var4, var5, var6, var3);
                return var14;
            }

            var14 = this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var8), var7, (KeyStore)null, (InputStream)null, var6, var3);
        } finally {
            IOUtil.closeStream(var13);
        }

        return var14;
    }

    private String[] a(PGPCompressedData var1, boolean var2, a var3, KeyStore var4, InputStream var5, String var6, String var7) throws PGPException, IOException {
        this.a("Decrypted data compression algorithm is {0}", KeyStore.a(var1.getAlgorithm()));

        BufferedInputStream var14;
        try {
            var14 = new BufferedInputStream(var1.getDataStream());
        } catch (PGPException var12) {
            throw IOUtil.newPGPException(var12);
        }

        String[] var15;
        try {
            PGPObjectFactory2 var8;
            Object var9;
            if ((var9 = (var8 = new PGPObjectFactory2(var14)).nextObject()) instanceof PGPLiteralData) {
                var15 = this.a((PGPLiteralData)var9, (PGPOnePassSignature)null, var6, var7);
                return var15;
            }

            if (var9 instanceof PGPOnePassSignatureList) {
                if (var2) {
                    var15 = this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var9), var8, var4, var5, var6, var7, var3);
                    return var15;
                }

                var15 = this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var9), var8, (KeyStore)null, (InputStream)null, var6, var7, var3);
                return var15;
            }

            if (!(var9 instanceof PGPSignatureList)) {
                throw new PGPException("Unknown message format: " + var9.getClass().getName());
            }

            if (!var2) {
                var15 = this.a((PGPSignatureList)((PGPSignatureList)var9), var8, (KeyStore)null, (InputStream)null, var6, var7, var3);
                return var15;
            }

            var15 = this.a((PGPSignatureList)((PGPSignatureList)var9), var8, var4, var5, var6, var7, var3);
        } finally {
            IOUtil.closeStream(var14);
        }

        return var15;
    }

    private String a(PGPOnePassSignatureList var1, PGPObjectFactory var2, KeyStore var3, InputStream var4, OutputStream var5, a var6) throws PGPException, IOException {
        this.a("Found signature");
        PGPOnePassSignature var7 = null;
        PGPPublicKey var8 = null;
        if (var4 != null || var3 != null) {
            for(int var9 = 0; var9 != var1.size(); ++var9) {
                var7 = var1.get(var9);
                if (var4 != null) {
                    var8 = readPublicVerificationKey(var4, var7.getKeyID());
                } else {
                    var8 = readPublicVerificationKey(var3, var7.getKeyID());
                }

                if (var8 != null) {
                    this.a("Message signed with Key Id {0}", KeyPairInformation.keyId2Hex(var8.getKeyID()));
                    break;
                }

                this.a("Message signed with Unknown Key Id {0}", KeyPairInformation.keyId2Hex(var7.getKeyID()));
            }

            if (var8 != null) {
                this.a.initVerify(var7, var8);
            }
        }

        Object var14;
        if ((var14 = var2.nextObject()) instanceof PGPLiteralData) {
            String var11 = this.a((PGPLiteralData)var14, var8 != null ? var7 : null, var5);
            if (var4 != null || var3 != null) {
                if (var8 == null) {
                    this.a("The signature of the message does not correspond to the provided public key.");
                    var6.a = SignatureCheckResult.PublicKeyNotMatching;
                } else {
                    Object var12;
                    if ((var12 = var2.nextObject()) != null && var7 != null) {
                        PGPSignatureList var13 = (PGPSignatureList)var12;

                        try {
                            if (!var7.verify(var13.get(0))) {
                                this.a("The signature of the message did not passed verification.");
                                var6.a = SignatureCheckResult.SignatureBroken;
                            } else {
                                var6.a = SignatureCheckResult.SignatureVerified;
                            }
                        } catch (PGPException var10) {
                            throw IOUtil.newPGPException(var10);
                        }
                    }
                }
            }

            return var11;
        } else {
            throw new PGPException("Unknown message format: " + var14.getClass().getName());
        }
    }

    private String[] a(PGPOnePassSignatureList var1, PGPObjectFactory var2, KeyStore var3, InputStream var4, String var5, String var6, a var7) throws PGPException, IOException {
        this.a("Found signature");
        PGPOnePassSignature var8 = null;
        PGPPublicKey var9 = null;
        if (var4 != null || var3 != null) {
            for(int var10 = 0; var10 != var1.size(); ++var10) {
                var8 = var1.get(var10);
                if (var4 != null) {
                    var9 = readPublicVerificationKey(var4, var8.getKeyID());
                } else {
                    var9 = readPublicVerificationKey(var3, var8.getKeyID());
                }

                if (var9 != null) {
                    this.a("The message is signed with Key Id {0}", KeyPairInformation.keyId2Hex(var9.getKeyID()));
                    break;
                }

                this.a("The message is signed with Unknown Key Id {0}", KeyPairInformation.keyId2Hex(var8.getKeyID()));
            }

            if (var9 != null) {
                this.a.initVerify(var8, var9);
            }
        }

        Object var12;
        if ((var12 = var2.nextObject()) instanceof PGPLiteralData) {
            String[] var13 = this.a((PGPLiteralData)var12, var9 != null ? var8 : null, var5, var6);
            if (var4 != null || var3 != null) {
                if (var9 == null) {
                    this.a("The signature of the message does not correspond to the provided public key.");
                    var7.a = SignatureCheckResult.PublicKeyNotMatching;
                } else {
                    Object var14;
                    if ((var14 = var2.nextObject()) != null && var8 != null) {
                        PGPSignatureList var15 = (PGPSignatureList)var14;

                        try {
                            if (!var8.verify(var15.get(0))) {
                                this.a("The signature of the message did not passed verification.");
                                var7.a = SignatureCheckResult.SignatureBroken;
                            } else {
                                this.a("The signature of the message passed verification.");
                                var7.a = SignatureCheckResult.SignatureVerified;
                            }
                        } catch (PGPException var11) {
                            throw IOUtil.newPGPException(var11);
                        }
                    }
                }
            }

            return var13;
        } else {
            throw new PGPException("Unknown message format: " + var12.getClass().getName());
        }
    }

    private String a(PGPSignatureList var1, PGPObjectFactory var2, KeyStore var3, InputStream var4, OutputStream var5, a var6) throws PGPException, IOException {
        this.a("Found signature version 3");
        PGPSignature var7 = null;
        PGPPublicKey var8 = null;
        if (var4 != null || var3 != null) {
            for(int var9 = 0; var9 < var1.size(); ++var9) {
                if ((var7 = var1.get(var9)).getSignatureType() == 0 || var7.getSignatureType() == 1 || var7.getSignatureType() == 16) {
                    if (var4 != null) {
                        var8 = readPublicVerificationKey(var4, var7.getKeyID());
                    } else {
                        var8 = readPublicVerificationKey(var3, var7.getKeyID());
                    }

                    if (var8 != null) {
                        this.a("The message is signed with Key Id {0}", KeyPairInformation.keyId2Hex(var8.getKeyID()));
                        break;
                    }

                    this.a("The message is signed with Unknown Key Id {0}", KeyPairInformation.keyId2Hex(var7.getKeyID()));
                }
            }

            if (var8 != null) {
                this.a.initVerify(var7, var8);
            }
        }

        Object var12;
        if ((var12 = var2.nextObject()) instanceof PGPLiteralData) {
            String var11 = this.a((PGPLiteralData)var12, var8 != null ? var7 : null, var5);
            if (var4 != null || var3 != null) {
                if (var8 == null) {
                    this.a("The signature of the message does not correspond to the provided public key.");
                    var6.a = SignatureCheckResult.PublicKeyNotMatching;
                } else {
                    try {
                        if (!var7.verify()) {
                            this.a("The signature of the message did not passed verification.");
                            var6.a = SignatureCheckResult.SignatureBroken;
                        } else {
                            var6.a = SignatureCheckResult.SignatureVerified;
                        }
                    } catch (PGPException var10) {
                        throw IOUtil.newPGPException(var10);
                    }
                }
            }

            return var11;
        } else if (var12 == null) {
            throw new DetachedSignatureException("This is a detached signature file");
        } else {
            throw new PGPException("Unknown message format: " + var12.getClass().getName());
        }
    }

    private String[] a(PGPSignatureList var1, PGPObjectFactory var2, KeyStore var3, InputStream var4, String var5, String var6, a var7) throws PGPException, IOException {
        PGPSignature var8 = null;
        PGPPublicKey var9 = null;
        if (var4 != null || var3 != null) {
            for(int var10 = 0; var10 < var1.size(); ++var10) {
                if ((var8 = var1.get(var10)).getSignatureType() == 0 || var8.getSignatureType() == 1 || var8.getSignatureType() == 16) {
                    if (var4 != null) {
                        var9 = readPublicVerificationKey(var4, var8.getKeyID());
                    } else {
                        var9 = readPublicVerificationKey(var3, var8.getKeyID());
                    }

                    if (var9 != null) {
                        this.a("Message signed with Key Id {0}", KeyPairInformation.keyId2Hex(var9.getKeyID()));
                        break;
                    }

                    this.a("Message signed with Unknown Key Id {0}", KeyPairInformation.keyId2Hex(var8.getKeyID()));
                }
            }

            if (var9 != null) {
                this.a.initVerify(var8, var9);
            }
        }

        Object var12;
        if ((var12 = var2.nextObject()) instanceof PGPLiteralData) {
            String[] var13 = this.a((PGPLiteralData)var12, var9 != null ? var8 : null, var5, var6);
            if (var4 != null || var3 != null) {
                if (var9 == null) {
                    this.a("The signature of the message does not correspond to the provided public key.");
                    var7.a = SignatureCheckResult.PublicKeyNotMatching;
                }

                try {
                    if (!var8.verify()) {
                        this.a("The signature of the message did not passed verification.");
                        var7.a = SignatureCheckResult.SignatureBroken;
                    } else {
                        this.a("The signature of the message passed verification.");
                        var7.a = SignatureCheckResult.SignatureVerified;
                    }
                } catch (PGPException var11) {
                    throw IOUtil.newPGPException(var11);
                }
            }

            return var13;
        } else if (var12 == null) {
            throw new DetachedSignatureException("This is a detached signature file");
        } else {
            throw new PGPException("Unknown message format: " + var12.getClass().getName());
        }
    }

    private String a(PGPEncryptedDataList var1, boolean var2, a var3, String var4, KeyStore var5, InputStream var6, OutputStream var7) throws IOException, WrongPasswordException, PGPException {
        WrongPasswordException var16 = null;
        InputStream var18 = null;
        PGPPBEEncryptedData var19 = null;
        if (var1 instanceof PGP2xPBEEncryptedData) {
            try {
                this.a("Password encrypted data packet found");
                var18 = ((PGP2xPBEEncryptedData)var1).getDataStream(var4.toCharArray());
            } catch (PGPDataValidationException var10) {
                throw new WrongPasswordException("The supplied password is incorrect.", var10.getUnderlyingException());
            } catch (PGPException var11) {
                throw IOUtil.newPGPException(var11);
            }
        } else {
            Iterator var14 = var1.getEncryptedDataObjects();

            label72:
            while(true) {
                Object var8;
                do {
                    if (!var14.hasNext()) {
                        break label72;
                    }
                } while(!((var8 = var14.next()) instanceof PGPPBEEncryptedData));

                this.a("Password encrypted data packet found");
                var19 = (PGPPBEEncryptedData)var8;

                try {
                    var18 = var19.getDataStream(this.a.CreatePBEDataDecryptorFactory(var4));
                    break;
                } catch (PGPDataValidationException var12) {
                    var16 = new WrongPasswordException("The supplied password is incorrect.", var12.getUnderlyingException());
                } catch (PGPException var13) {
                    throw IOUtil.newPGPException(var13);
                }
            }
        }

        if (var18 == null && var16 != null) {
            throw var16;
        } else if (var18 == null) {
            throw new FileIsEncryptedException("The file is encrypted with an OpenPGP key.");
        } else {
            String var15;
            Object var17;
            PGPObjectFactory2 var20;
            if ((var17 = (var20 = new PGPObjectFactory2(var18)).nextObject()) instanceof PGPCompressedData) {
                var15 = this.a((PGPCompressedData)var17, false, var3, (KeyStore)null, (InputStream)null, var7);
            } else if (var17 instanceof PGPOnePassSignatureList) {
                var15 = this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var17), var20, (KeyStore)null, (InputStream)null, var7, var3);
            } else if (var17 instanceof PGPSignatureList) {
                var15 = this.a((PGPSignatureList)((PGPSignatureList)var17), var20, (KeyStore)null, (InputStream)null, var7, var3);
            } else {
                if (!(var17 instanceof PGPLiteralData)) {
                    throw new PGPException("Unknown message format: " + var17.getClass().getName());
                }

                var15 = this.a((PGPLiteralData)((PGPLiteralData)var17), (PGPOnePassSignature)null, (OutputStream)var7);
            }

            if (var19 != null && var19.isIntegrityProtected()) {
                try {
                    if (!var19.verify()) {
                        this.a("Integrity check failed!");
                        throw new IntegrityCheckException("The encrypted data is corrupted!");
                    }
                } catch (PGPException var9) {
                }
            }

            return var15;
        }
    }

    private String a(PGPEncryptedDataList var1, boolean var2, a var3, KeyStore var4, InputStream var5, String var6, InputStream var7, OutputStream var8) throws IOException, PGPException, WrongPrivateKeyException, WrongPasswordException, FileIsPBEEncryptedException, IntegrityCheckException, DetachedSignatureException {
        PGPSecretKeyRingCollection var9;
        if (var5 != null) {
            var9 = createPGPSecretKeyRingCollection(var5);
        } else {
            var9 = var4.a;
        }

        WrongPasswordException var10 = null;
        PGPPrivateKey var11 = null;
        PGPPublicKeyEncryptedData var12 = null;
        String[] var13;
        a(var13 = new String[var1.size()], "Password encrypted");

        for(int var14 = 0; var14 < var1.size(); ++var14) {
            Object var15;
            if ((var15 = var1.get(var14)) instanceof PGPPublicKeyEncryptedData) {
                var12 = (PGPPublicKeyEncryptedData)var15;
                var13[var14] = KeyPairInformation.keyId2Hex(var12.getKeyID());
                this.a("Public key encrypted data packet found");
                this.a("Encrypted with key {0}", "0".equals(var13[var14]) ? "wildcard" : var13[var14]);

                try {
                    if ((var11 = this.getPrivateKey(var9, var12.getKeyID(), var6.toCharArray())) != null && var4 != null) {
                        var11 = this.getPrivateKey(var9, var12.getKeyID(), var6.toCharArray());
                    }
                } catch (WrongPasswordException var18) {
                    var11 = null;
                    var10 = var18;
                }

                if (var11 != null) {
                    break;
                }
            }
        }

        if (var11 == null && var10 != null) {
            throw var10;
        } else if (var12 == null) {
            this.a("This file is encrypted with a password.");
            throw new FileIsPBEEncryptedException("This file is encrypted with a password.");
        } else if (var11 == null) {
            if (var5 != null) {
                if (var9 != null && var9.size() == 0) {
                    throw new WrongPrivateKeyException("Decryption of data encrypted using KEY-ID(s) : " + b(var13, ",") + " failed, The provided key is not a valid OpenPGP private key.");
                } else {
                    String var22 = "";
                    Iterator var24;
                    if ((var24 = var9.getKeyRings()).hasNext()) {
                        var22 = KeyPairInformation.keyId2Hex(((PGPSecretKeyRing)var24.next()).getSecretKey().getKeyID());
                    }

                    throw new WrongPrivateKeyException("Decryption of data encrypted using KEY-ID(s) : " + b(var13, ",") + " failed, using incorrect private KEY-ID :" + var22);
                }
            } else {
                throw new WrongPrivateKeyException("Decryption of data encrypted using KEY-ID(s) : " + b(var13, ",") + " failed, no matching key was found in the KeyStore.");
            }
        } else {
            InputStream var21;
            try {
                var21 = var12.getDataStream(this.a.CreatePublicKeyDataDecryptorFactory(var11));
            } catch (PGPException var17) {
                throw IOUtil.newPGPException(var17);
            }

            Object var19;
            PGPObjectFactory2 var20;
            String var23;
            if ((var19 = (var20 = new PGPObjectFactory2(var21)).nextObject()) instanceof PGPCompressedData) {
                var23 = this.a((PGPCompressedData)var19, var2, var3, var4, var7, var8);
            } else if (var19 instanceof PGPOnePassSignatureList) {
                this.a("Signature found!");
                if (var2) {
                    var23 = this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var19), var20, var4, var7, var8, var3);
                } else {
                    var23 = this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var19), var20, (KeyStore)null, (InputStream)null, var8, var3);
                }
            } else if (var19 instanceof PGPSignatureList) {
                this.a("Signature found (version 3, old style)");
                if (var2) {
                    var23 = this.a((PGPSignatureList)((PGPSignatureList)var19), var20, var4, var7, var8, var3);
                } else {
                    var23 = this.a((PGPSignatureList)((PGPSignatureList)var19), var20, (KeyStore)null, (InputStream)null, var8, var3);
                }
            } else {
                if (!(var19 instanceof PGPLiteralData)) {
                    throw new NonPGPDataException("Unknown message format: " + var19.getClass().getName());
                }

                var23 = this.a((PGPLiteralData)((PGPLiteralData)var19), (PGPOnePassSignature)null, (OutputStream)var8);
            }

            if (var12.isIntegrityProtected()) {
                try {
                    if (!var12.verify()) {
                        this.a("Integrity check failed!");
                        throw new IntegrityCheckException("The encrypted data is corrupted!");
                    }
                } catch (PGPException var16) {
                }
            }

            return var23;
        }
    }

    private String[] a(PGPEncryptedDataList var1, boolean var2, a var3, KeyStore var4, InputStream var5, String var6, InputStream var7, String var8, String var9) throws IOException, WrongPasswordException, WrongPrivateKeyException, PGPException {
        PGPPrivateKey var10 = null;
        PGPSecretKeyRingCollection var11;
        if (var5 != null) {
            var11 = createPGPSecretKeyRingCollection(var5);
        } else {
            var11 = var4.a;
        }

        WrongPasswordException var12 = null;
        PGPPublicKeyEncryptedData var13 = null;
        String[] var14;
        a(var14 = new String[var1.size()], "Password encrypted");

        for(int var15 = 0; var15 < var1.size(); ++var15) {
            Object var16;
            if ((var16 = var1.get(var15)) instanceof PGPPublicKeyEncryptedData) {
                var13 = (PGPPublicKeyEncryptedData)var16;
                var14[var15] = KeyPairInformation.keyId2Hex(var13.getKeyID());
                this.a("Encrypted with key ID {0}", "0".equals(var14[var15]) ? "wildcard" : var14[var15]);

                try {
                    if ((var10 = this.getPrivateKey(var11, var13.getKeyID(), var6.toCharArray())) != null && var4 != null) {
                        var10 = this.getPrivateKey(var11, var13.getKeyID(), var6.toCharArray());
                    }
                } catch (WrongPasswordException var19) {
                    var10 = null;
                    var12 = var19;
                }

                if (var10 != null) {
                    break;
                }
            }
        }

        if (var10 == null && var12 != null) {
            throw var12;
        } else if (var13 == null) {
            this.a("This file is encrypted with a password.");
            throw new FileIsPBEEncryptedException("This file is encrypted with a password.");
        } else if (var10 == null) {
            if (var5 != null) {
                if (var11 != null && var11.size() == 0) {
                    throw new WrongPrivateKeyException("Decryption of data encrypted using KEY-ID(s) : " + b(var14, ",") + " failed, The provided key is not a valid OpenPGP private key.");
                } else {
                    String var23 = "";
                    Iterator var25;
                    if ((var25 = var11.getKeyRings()).hasNext()) {
                        var23 = KeyPairInformation.keyId2Hex(((PGPSecretKeyRing)var25.next()).getSecretKey().getKeyID());
                    }

                    throw new WrongPrivateKeyException("Decryption of data encrypted using KEY-ID(s) : " + b(var14, ",") + " failed, using incorrect private KEY-ID :" + var23);
                }
            } else {
                throw new WrongPrivateKeyException("Decryption of data encrypted using KEY-ID(s) : " + b(var14, ",") + " failed, no matching key was found in the KeyStore.");
            }
        } else {
            InputStream var22;
            try {
                var22 = var13.getDataStream(this.a.CreatePublicKeyDataDecryptorFactory(var10));
            } catch (PGPException var18) {
                throw IOUtil.newPGPException(var18);
            }

            Object var20;
            PGPObjectFactory2 var21;
            String[] var24;
            if ((var20 = (var21 = new PGPObjectFactory2(var22)).nextObject()) instanceof PGPCompressedData) {
                var24 = this.a((PGPCompressedData)var20, var2, var3, var4, var7, var8, var9);
            } else if (var20 instanceof PGPOnePassSignatureList) {
                if (var2) {
                    var24 = this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var20), var21, var4, var7, var8, var9, var3);
                } else {
                    var24 = this.a((PGPOnePassSignatureList)((PGPOnePassSignatureList)var20), var21, (KeyStore)null, (InputStream)null, var8, var9, var3);
                }
            } else if (var20 instanceof PGPSignatureList) {
                if (var2) {
                    var24 = this.a((PGPSignatureList)((PGPSignatureList)var20), var21, var4, var7, var8, var9, var3);
                } else {
                    var24 = this.a((PGPSignatureList)((PGPSignatureList)var20), var21, (KeyStore)null, (InputStream)null, var8, var9, var3);
                }
            } else {
                if (!(var20 instanceof PGPLiteralData)) {
                    throw new PGPException("Unknown message format: " + var20.getClass().getName());
                }

                var24 = this.a((PGPLiteralData)var20, (PGPOnePassSignature)null, var8, var9);
            }

            if (var13.isIntegrityProtected()) {
                try {
                    if (!var13.verify()) {
                        this.a("Integrity check failed!");
                        throw new IntegrityCheckException("The encrypted data is corrupted!");
                    }
                } catch (PGPException var17) {
                }
            }

            return var24;
        }
    }

    private static String a(PGPPublicKey var0) {
        Iterator var1;
        return (var1 = var0.getUserIDs()).hasNext() ? (String)var1.next() : "";
    }

    private PGPPublicKey a(InputStream var1) throws IOException, NoPublicKeyFoundException, PGPException {
        PGPPublicKeyRingCollection var6 = createPGPPublicKeyRingCollection(var1);
        PGPException var2 = null;
        PGPPublicKey var3 = null;
        Iterator var7 = var6.getKeyRings();

        while(var3 == null && var7.hasNext()) {
            PGPPublicKeyRing var4 = (PGPPublicKeyRing)var7.next();

            try {
                var3 = this.a(var4);
            } catch (PGPException var5) {
                var2 = var5;
            }

            if (var3 != null) {
                break;
            }
        }

        if (var3 == null) {
            if (var2 != null) {
                throw var2;
            } else {
                throw new NoPublicKeyFoundException("Can't find encryption key in key ring.");
            }
        } else {
            return var3;
        }
    }

    private PGPPublicKey a(PGPPublicKeyRing var1) throws IOException, NoPublicKeyFoundException, PGPException {
        PGPPublicKey var2 = null;
        Object var3 = null;
        Iterator var4 = var1.getPublicKeys();

        while(true) {
            while(true) {
                PGPPublicKey var5;
                do {
                    if (!var4.hasNext()) {
                        if (var2 == null) {
                            if (var3 != null) {
                                throw var3;
                            }

                            throw new NoPublicKeyFoundException("Can't find encryption key in key ring.");
                        }

                        return var2;
                    }
                } while(!(var5 = (PGPPublicKey)var4.next()).isEncryptionKey());

                if (var5.isRevoked() && !this.f) {
                    this.a("The key {0} is revoked", KeyPairInformation.keyIdToHex(var5.getKeyID()));
                    var3 = new KeyIsRevokedException("The key with Key Id:" + KeyPairInformation.keyIdToHex(var5.getKeyID()) + " [" + a(var5) + "] is revoked. See PGPLib.setUseRevokedKeys for more information.");
                } else {
                    boolean var10000;
                    label81: {
                        if (var5 != null && !this.e && var5.getValidDays() > 0) {
                            Calendar var6;
                            (var6 = Calendar.getInstance()).setTime(var5.getCreationTime());
                            var6.add(5, var5.getValidDays());
                            if (var6.getTime().before(new Date())) {
                                var10000 = true;
                                break label81;
                            }
                        }

                        var10000 = false;
                    }

                    if (var10000 && !this.e) {
                        this.a("The key {0} is expired", KeyPairInformation.keyIdToHex(var5.getKeyID()));
                        var3 = new KeyIsExpiredException("The key with Key Id:" + KeyPairInformation.keyIdToHex(var5.getKeyID()) + " [" + a(var5) + "] has expired. See PGPLib.setUseExpiredKeys for more information.");
                    } else {
                        Iterator var8 = var5.getSignatures();

                        while(var8.hasNext()) {
                            PGPSignature var7;
                            if ((var7 = (PGPSignature)var8.next()).getKeyID() == var1.getPublicKey().getKeyID() && var7.getHashedSubPackets() != null && var7.getHashedSubPackets().hasSubpacket(27) && ((var7.getHashedSubPackets().getKeyFlags() & 4) == 4 || (var7.getHashedSubPackets().getKeyFlags() & 8) == 8)) {
                                var2 = var5;
                                break;
                            }
                        }

                        if (var2 == null || var5.getBitStrength() > var2.getBitStrength() || var2.isMasterKey() && var5.getBitStrength() == var2.getBitStrength()) {
                            var2 = var5;
                        }
                    }
                }
            }
        }
    }

    private void b(PGPPublicKey var1) throws KeyIsExpiredException {
        if (var1 != null) {
            if (!this.e) {
                if (var1.getValidDays() > 0) {
                    Calendar var2;
                    (var2 = Calendar.getInstance()).setTime(var1.getCreationTime());
                    var2.add(5, var1.getValidDays());
                    if (var2.getTime().before(new Date())) {
                        String var4 = "";
                        Iterator var3;
                        if ((var3 = var1.getUserIDs()).hasNext()) {
                            var4 = (String)var3.next();
                        }

                        this.a("The key {0} is expired", KeyPairInformation.keyId2Hex(var1.getKeyID()));
                        throw new KeyIsExpiredException("The key with Id:" + KeyPairInformation.keyId2Hex(var1.getKeyID()) + " [" + var4 + "] has expired. See PGPLib.setUseExpiredKeys for more information.");
                    }
                }
            }
        }
    }

    private void c(PGPPublicKey var1) throws KeyIsRevokedException {
        if (var1 != null) {
            if (!this.f) {
                if (var1.isRevoked()) {
                    String var2 = "";
                    Iterator var3;
                    if ((var3 = var1.getUserIDs()).hasNext()) {
                        var2 = (String)var3.next();
                    }

                    this.a("The key {0} is revoked", KeyPairInformation.keyId2Hex(var1.getKeyID()));
                    throw new KeyIsRevokedException("The key with Id:" + var1.getKeyID() + " [" + var2 + "] is revoked. See PGPLib.setUseRevokedKeys for more information.");
                }
            }
        }
    }

    private static void a(OutputStream var0, char var1, InputStream var2, String var3, long var4, Date var6) throws IOException {
        PGPLiteralDataGenerator var12 = null;
        OutputStream var7 = null;
        boolean var9 = false;

        try {
            var9 = true;
            var7 = (var12 = new PGPLiteralDataGenerator()).open(var0, 'b', var3, var4, var6);
            byte[] var11 = new byte[1048576];

            while(true) {
                int var13;
                if ((var13 = var2.read(var11)) <= 0) {
                    var9 = false;
                    break;
                }

                var7.write(var11, 0, var13);
            }
        } finally {
            if (var9) {
                if (var12 != null) {
                    var12.close();
                }

                IOUtil.closeStream(var7);
                IOUtil.closeStream(var2);
            }
        }

        var12.close();
        IOUtil.closeStream(var7);
        IOUtil.closeStream(var2);
    }

    private PGPSecretKey b(InputStream var1) throws NoPrivateKeyFoundException, IOException, PGPException {
        PGPSecretKeyRingCollection var5 = createPGPSecretKeyRingCollection(var1);
        PGPSecretKey var2 = null;
        Iterator var6 = var5.getKeyRings();

        while(var2 == null && var6.hasNext()) {
            Iterator var3 = ((PGPSecretKeyRing)var6.next()).getSecretKeys();

            while(var2 == null && var3.hasNext()) {
                PGPSecretKey var4;
                if ((var4 = (PGPSecretKey)var3.next()).isSigningKey()) {
                    var2 = var4;
                }
            }
        }

        if (var2 == null) {
            throw new NoPrivateKeyFoundException("Can't find signing key in key ring.");
        } else {
            this.b(var2.getPublicKey());
            this.c(var2.getPublicKey());
            return var2;
        }
    }

    private void a(InputStream var1, String var2, PGPSecretKey var3, String var4, OutputStream var5, boolean var6) throws PGPException, WrongPasswordException, IOException {
        if (!(var5 instanceof BufferedOutputStream)) {
            var5 = new BufferedOutputStream((OutputStream)var5, 1048576);
        }

        if (var3.getPublicKey() != null && var3.getPublicKey().getVersion() == 3) {
            this.a("Switching to version 3 signatures");
            this.signStreamVersion3(var1, var2, new ByteArrayInputStream(var3.getEncoded()), var4, (OutputStream)var5, var6);
        }

        Object var7 = null;
        if (var6) {
            this.a("Output is ASCII armored");
            var7 = var5;
            var5 = new ArmoredOutputStream((OutputStream)var5);
            this.a((OutputStream)var5);
        }

        PGPPrivateKey var17 = extractPrivateKey(var3, var4);
        int var20 = KeyStore.a(this.b);
        this.a("Signature with hash algorithm {0}", KeyStore.b(var20));
        PGPSignatureGenerator var21 = new PGPSignatureGenerator(this.a.CreatePGPContentSignerBuilder(var3.getPublicKey().getAlgorithm(), var20));

        try {
            var21.init(0, var17);
        } catch (PGPException var13) {
            throw IOUtil.newPGPException(var13);
        }

        Iterator var18;
        if ((var18 = var3.getPublicKey().getUserIDs()).hasNext()) {
            PGPSignatureSubpacketGenerator var8 = new PGPSignatureSubpacketGenerator();
            var4 = (String)var18.next();
            this.a("Signing for user Id {0}", var4);
            var8.setSignerUserID(false, var4);
            var21.setHashedSubpackets(var8.generate());
        }

        int var22 = this.f(var3.getPublicKey());
        PGPCompressedDataGenerator var19 = new PGPCompressedDataGenerator(var22);
        BCPGOutputStream var16;
        if (var22 == 0) {
            this.a("No Compression");
            var16 = new BCPGOutputStream((OutputStream)var5);
        } else {
            this.a("Compression algorithm is {0}", KeyStore.a(var22));
            var16 = new BCPGOutputStream(var19.open((OutputStream)var5));
        }

        try {
            var21.generateOnePassVersion(false).encode(var16);
        } catch (PGPException var12) {
            throw IOUtil.newPGPException(var12);
        }

        DirectByteArrayOutputStream var23 = new DirectByteArrayOutputStream(1048576);
        byte[] var10 = new byte[1048576];

        int var9;
        while((var9 = var1.read(var10)) >= 0) {
            var23.write(var10, 0, var9);
            var21.update(var10, 0, var9);
        }

        PGPLiteralDataGenerator var14;
        OutputStream var15;
        (var15 = (var14 = new PGPLiteralDataGenerator()).open(var16, 'b', var2, (long)var23.size(), new Date())).write(var23.getArray(), 0, var23.size());

        try {
            var21.generate().encode(var16);
        } catch (PGPException var11) {
            throw IOUtil.newPGPException(var11);
        }

        var14.close();
        var19.close();
        IOUtil.closeStream(var15);
        IOUtil.closeStream(var16);
        IOUtil.closeStream((OutputStream)var5);
        IOUtil.closeStream((OutputStream)var7);
        IOUtil.closeStream(var23);
    }

    private void a(InputStream var1, String var2, InputStream var3, String var4, InputStream[] var5, OutputStream var6, boolean var7) throws PGPException, IOException {
        if (var7) {
            this.a("Output is ASCII armored");
            var6 = new ArmoredOutputStream((OutputStream)var6);
            this.a((OutputStream)var6);
        }

        DirectByteArrayOutputStream var8 = new DirectByteArrayOutputStream(1048576);
        BaseLib.pipeAll(var1, var8);
        PGPEncryptedDataGenerator var12 = this.a.CreatePGPEncryptedDataGenerator(1, false, IOUtil.getSecureRandom());
        PGPPublicKey[] var9 = new PGPPublicKey[var5.length];

        for(int var10 = 0; var10 < var5.length; ++var10) {
            var9[var10] = this.a(var5[var10]);
            var12.addMethod(this.a.CreatePublicKeyKeyEncryptionMethodGenerator(var9[var10]));
            this.a("Ecrypting for key Id {0}", String.valueOf(var9[var10].getKeyID()));
        }

        PGPLiteralDataGenerator var18 = new PGPLiteralDataGenerator(true);
        DirectByteArrayOutputStream var17 = new DirectByteArrayOutputStream(1048576);
        OutputStream var14 = var18.open(var17, this.i.getCode(), var2, (long)var8.size(), new Date());
        var8.writeTo(var14);
        DirectByteArrayOutputStream var15 = new DirectByteArrayOutputStream(1048576);
        this.detachedSignStream(new ByteArrayInputStream(var8.getArray(), 0, var8.size()), var3, var4, var15, false);
        byte[] var16 = var15.toByteArray();
        (var15 = new DirectByteArrayOutputStream(1048576)).write((byte)(var16[0] | 1));
        var15.write(0);
        var15.write(var16[1]);
        var15.write(var16, 2, var16.length - 2);

        try {
            OutputStream var13 = var12.open((OutputStream)var6, (long)(var15.size() + var17.size()));
            var15.writeTo(var13);
            var17.writeTo(var13);
        } catch (PGPException var11) {
            throw IOUtil.newPGPException(var11);
        }

        ((OutputStream)var6).flush();
        if (var7) {
            IOUtil.closeStream((OutputStream)var6);
        }

    }

    private static InputStream a(KeyStore var0, String var1) throws PGPException {
        PGPPublicKeyRing var3 = var0.a(var1);

        try {
            return new ByteArrayInputStream(var3.getEncoded());
        } catch (IOException var2) {
            throw new PGPException(var2.getMessage(), var2);
        }
    }

    private static InputStream c(KeyStore var0, long var1) throws IOException, NoPublicKeyFoundException {
        PGPPublicKeyRing var3 = var0.a(var1);
        return new ByteArrayInputStream(var3.getEncoded());
    }

    private static InputStream b(KeyStore var0, String var1) throws IOException, PGPException {
        PGPSecretKeyRing var2 = var0.findSecretKeyRing(var1);
        return new ByteArrayInputStream(var2.getEncoded());
    }

    private static InputStream d(KeyStore var0, long var1) throws IOException, PGPException {
        PGPSecretKey var4;
        try {
            var4 = var0.a.getSecretKey(var1);
        } catch (PGPException var3) {
            throw IOUtil.newPGPException(var3);
        }

        if (var4 != null) {
            return new ByteArrayInputStream(var4.getEncoded());
        } else {
            throw new NoPrivateKeyFoundException("No private key was found with KeyId : " + var1);
        }
    }

    private int d(PGPPublicKey var1) {
        int var2 = KeyStore.a(this.c);
        int var3 = 0;
        Iterator var4 = var1.getSignatures();

        while(var3 == 0 && var4.hasNext()) {
            PGPSignature var5;
            if ((var5 = (PGPSignature)var4.next()).getHashedSubPackets() != null && var5.getHashedSubPackets().getPreferredSymmetricAlgorithms() != null) {
                int[] var7 = var5.getHashedSubPackets().getPreferredSymmetricAlgorithms();

                for(int var6 = 0; var6 < var7.length; ++var6) {
                    var3 = var7[var6];
                    if (var2 == var3) {
                        break;
                    }
                }
            }
        }

        if (var3 == 0) {
            if (var1.getVersion() == 3) {
                var3 = 1;
            } else if (var1.getAlgorithm() == 18) {
                var3 = 9;
            } else if (var1.getCreationTime().getYear() < 2002) {
                var3 = 3;
            } else {
                var3 = var2;
            }
        }

        this.a("Cypher: {0}", KeyStore.c(var3));
        return var3;
    }

    private int e(PGPPublicKey var1) {
        int var2 = KeyStore.a(this.b);
        int var3 = -1;
        Iterator var6 = var1.getSignatures();

        while(var3 == -1 && var6.hasNext()) {
            PGPSignature var4;
            if ((var4 = (PGPSignature)var6.next()).getHashedSubPackets() != null && var4.getHashedSubPackets().getPreferredHashAlgorithms() != null) {
                int[] var7 = var4.getHashedSubPackets().getPreferredHashAlgorithms();

                for(int var5 = 0; var5 < var7.length; ++var5) {
                    var3 = var7[var5];
                    if (var2 == var3) {
                        break;
                    }
                }
            }
        }

        if (var3 == -1) {
            var3 = var2;
        }

        this.a("Hash: {0}", KeyStore.b(var3));
        return var3;
    }

    private int f(PGPPublicKey var1) {
        int var2 = KeyStore.a(this.d);
        int var3 = -1;
        Iterator var6 = var1.getSignatures();

        while(var3 == -1 && var6.hasNext()) {
            PGPSignature var4;
            if ((var4 = (PGPSignature)var6.next()).getHashedSubPackets() != null && var4.getHashedSubPackets().getPreferredCompressionAlgorithms() != null) {
                int[] var7 = var4.getHashedSubPackets().getPreferredCompressionAlgorithms();

                for(int var5 = 0; var5 < var7.length; ++var5) {
                    var3 = var7[var5];
                    if (var2 == var3) {
                        break;
                    }
                }
            }
        }

        if (var3 == -1) {
            var3 = var2;
        }

        this.a("Compression: {0}", KeyStore.a(var3));
        return var3;
    }

    private static SecureRandom a() {
        try {
            return SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException var0) {
            return new SecureRandom();
        }
    }

    private static void a(BCPGOutputStream var0) {
        Class var1 = BCPGOutputStream.class;
        byte[] var2 = new byte[]{80, 71, 80};
        Method[] var7 = var1.getDeclaredMethods();

        for(int var3 = 0; var3 < var7.length; ++var3) {
            if (var7[var3].getName().endsWith("writePacket") && var7[var3].getParameterTypes().length == 3) {
                Method var8;
                (var8 = var7[var3]).setAccessible(true);

                try {
                    var8.invoke(var0, new Integer(10), var2, new Boolean(true));
                    return;
                } catch (InvocationTargetException var5) {
                    return;
                } catch (IllegalAccessException var6) {
                    return;
                }
            }
        }

        throw new Error("No such method: writeMarkerPacket");
    }

    private static File a(String[] var0) throws PGPException {
        try {
            File var1 = File.createTempFile("tmpTarBCPG", ".tar");
            FileOutputStream var2 = new FileOutputStream(var1);
            TarOutputStream var5 = new TarOutputStream(var2);

            for(int var3 = 0; var3 < var0.length; ++var3) {
                var5.writeFileEntry(new TarEntry(new File(var0[var3]), ""));
            }

            var5.close();
            return var1;
        } catch (IOException var4) {
            throw new PGPException(var4.getMessage(), var4);
        }
    }

    private void a(String var1) {
        if (this.g.isLoggable(Level.FINE)) {
            this.g.fine(var1);
        }

    }

    private void a(String var1, String var2) {
        if (this.g.isLoggable(Level.FINE)) {
            this.g.fine(MessageFormat.format(var1, var2));
        }

    }

    private static void a(String[] var0, String var1) {
        for(int var2 = 0; var2 < var0.length; ++var2) {
            var0[var2] = var1;
        }

    }

    private static String b(String[] var0, String var1) {
        int var2;
        if ((var2 = var0.length) == 0) {
            return "";
        } else {
            StringBuffer var3;
            (var3 = new StringBuffer()).append(var0[0]);

            for(int var4 = 1; var4 < var2; ++var4) {
                var3.append(var1).append(var0[var4]);
            }

            return var3.toString();
        }
    }

    static {
        String var0 = "Your 30 day evaluation version of DidiSoft OpenPGP Libary for Android has expired.";

        try {
            Date var1 = (new SimpleDateFormat("MM/dd/yyyy")).parse("10/01/2022");
            Date var2 = new Date();
            if (var1.getTime() < var2.getTime()) {
                throw new RuntimeException(var0);
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }
    }

    class a {
        public SignatureCheckResult a;

        private a(PGPLib var1) {
            this.a = SignatureCheckResult.NoSignatureFound;
        }
    }
}