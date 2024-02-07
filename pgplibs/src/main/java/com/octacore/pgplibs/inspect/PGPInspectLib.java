package com.octacore.pgplibs.inspect;

import com.octacore.pgplibs.CypherAlgorithm;
import com.octacore.pgplibs.KeyStore;
import com.octacore.pgplibs.PGPException;
import com.octacore.pgplibs.bc.BaseLib;
import com.octacore.pgplibs.bc.IOUtil;
import com.octacore.pgplibs.bc.PGP2xPBEEncryptedData;
import com.octacore.pgplibs.bc.PGPObjectFactory2;
import com.octacore.pgplibs.exceptions.FileIsEncryptedException;
import com.octacore.pgplibs.exceptions.NonPGPDataException;
import com.octacore.pgplibs.exceptions.WrongPasswordException;
import com.octacore.pgplibs.exceptions.WrongPrivateKeyException;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

import org.spongycastle.openpgp.PGPCompressedData;
import org.spongycastle.openpgp.PGPEncryptedDataList;
import org.spongycastle.openpgp.PGPLiteralData;
import org.spongycastle.openpgp.PGPMarker;
import org.spongycastle.openpgp.PGPObjectFactory;
import org.spongycastle.openpgp.PGPOnePassSignature;
import org.spongycastle.openpgp.PGPOnePassSignatureList;
import org.spongycastle.openpgp.PGPPBEEncryptedData;
import org.spongycastle.openpgp.PGPPrivateKey;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPPublicKeyEncryptedData;
import org.spongycastle.openpgp.PGPSecretKeyRingCollection;
import org.spongycastle.openpgp.PGPSignature;
import org.spongycastle.openpgp.PGPSignatureList;
import org.spongycastle.openpgp.PGPUtil;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Iterator;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class PGPInspectLib extends BaseLib {
    public PGPInspectLib() {
    }

    public boolean isPublicKeyEncrypted(String var1) throws IOException, NonPGPDataException {
        FileInputStream var2 = null;

        boolean var5;
        try {
            var2 = new FileInputStream(var1);
            var5 = this.isPublicKeyEncrypted((InputStream) var2);
        } finally {
            IOUtil.closeStream(var2);
        }

        return var5;
    }

    public boolean isPublicKeyEncrypted(InputStream var1) throws IOException, NonPGPDataException {
        var1 = PGPUtil.getDecoderStream(var1);
        PGPObjectFactory2 var4 = new PGPObjectFactory2(var1);

        Object var2;
        try {
            var2 = var4.nextObject();
        } catch (IOException var3) {
            throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var3);
        }

        if (var2 instanceof PGPMarker) {
            var2 = var4.nextObject();
        }

        if (var2 instanceof PGPEncryptedDataList) {
            PGPEncryptedDataList var5;
            Iterator var6 = (var5 = (PGPEncryptedDataList) var2).getEncryptedDataObjects();

            while (var6.hasNext()) {
                if (var6.next() instanceof PGPPublicKeyEncryptedData) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isPBEEncrypted(String var1) throws IOException, NonPGPDataException {
        FileInputStream var2 = new FileInputStream(var1);
        return this.isPBEEncrypted((InputStream) var2);
    }

    public boolean isPBEEncrypted(InputStream var1) throws IOException, NonPGPDataException {
        var1 = PGPUtil.getDecoderStream(var1);
        PGPObjectFactory2 var4 = new PGPObjectFactory2(var1);

        Object var2;
        try {
            var2 = var4.nextObject();
        } catch (IOException var3) {
            throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var3);
        }

        if (var2 instanceof PGPMarker) {
            var2 = var4.nextObject();
        }

        if (var2 instanceof PGPEncryptedDataList) {
            PGPEncryptedDataList var5;
            Iterator var6 = (var5 = (PGPEncryptedDataList) var2).getEncryptedDataObjects();

            while (var6.hasNext()) {
                if (var6.next() instanceof PGPPBEEncryptedData) {
                    return true;
                }
            }
        } else if (var2 instanceof PGP2xPBEEncryptedData) {
            return true;
        }

        return false;
    }

    public long[] listEncryptionKeyIds(String var1) throws IOException, NonPGPDataException {
        InputStream var2 = null;

        long[] var5;
        try {
            var2 = BaseLib.readFileOrAsciiString(var1, "fileName");
            var5 = this.listEncryptionKeyIds(var2);
        } finally {
            IOUtil.closeStream(var2);
        }

        return var5;
    }

    public long[] listEncryptionKeyIds(InputStream var1) throws IOException, NonPGPDataException {
        var1 = PGPUtil.getDecoderStream(var1);
        PGPObjectFactory2 var5 = new PGPObjectFactory2(var1);

        Object var2;
        try {
            var2 = var5.nextObject();
        } catch (IOException var4) {
            throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var4);
        }

        if (var2 instanceof PGPMarker) {
            var2 = var5.nextObject();
        }

        ArrayList var6 = new ArrayList();
        if (var2 instanceof PGPEncryptedDataList) {
            PGPEncryptedDataList var7;
            Iterator var3 = (var7 = (PGPEncryptedDataList) var2).getEncryptedDataObjects();

            while (var3.hasNext()) {
                if ((var2 = var3.next()) instanceof PGPPublicKeyEncryptedData) {
                    PGPPublicKeyEncryptedData var9 = (PGPPublicKeyEncryptedData) var2;
                    var6.add(new Long(var9.getKeyID()));
                }
            }
        }

        long[] var10 = new long[var6.size()];

        for (int var8 = 0; var8 < var6.size(); ++var8) {
            var10[var8] = (Long) var6.get(var8);
        }

        return var10;
    }

    public SignatureItem[] listDetachedSignatureFile(String var1) throws IOException, NonPGPDataException {
        FileInputStream var2 = null;

        SignatureItem[] var5;
        try {
            var2 = new FileInputStream(var1);
            var5 = this.listDetachedSignatureStream(var2);
        } finally {
            IOUtil.closeStream(var2);
        }

        return var5;
    }

    public SignatureItem[] listDetachedSignatureStream(InputStream var1) throws IOException, NonPGPDataException {
        var1 = PGPUtil.getDecoderStream(var1);
        PGPObjectFactory2 var6 = new PGPObjectFactory2(var1);

        Object var7;
        try {
            var7 = var6.nextObject();
        } catch (IOException var5) {
            throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var5);
        }

        if (!(var7 instanceof PGPSignatureList)) {
            throw new NonPGPDataException("Unknown message format: " + var7.getClass().getName());
        } else {
            PGPSignatureList var8;
            SignatureItem[] var3 = new SignatureItem[(var8 = (PGPSignatureList) var7).size()];

            for (int var4 = 0; var4 < var8.size(); ++var4) {
                PGPSignature var2 = var8.get(var4);
                var3[var4] = a(var2);
            }

            return var3;
        }
    }

    public long[] listSigningKeyIds(String var1) throws PGPException, IOException {
        BufferedInputStream var2 = null;

        long[] var5;
        try {
            var2 = new BufferedInputStream(new FileInputStream(var1));
            var5 = this.listSigningKeyIds((InputStream) var2, (KeyStore) (new KeyStore()), (String) null);
        } finally {
            IOUtil.closeStream(var2);
        }

        return var5;
    }

    public long[] listSigningKeyIds(InputStream var1) throws PGPException, IOException {
        return this.listSigningKeyIds((InputStream) var1, (KeyStore) (new KeyStore()), (String) null);
    }

    public long[] listSigningKeyIds(String var1, String var2, String var3) throws PGPException, IOException {
        FileInputStream var4 = null;
        BufferedInputStream var5 = null;

        long[] var8;
        try {
            var4 = new FileInputStream(var2);
            var5 = new BufferedInputStream(new FileInputStream(var1));
            var8 = this.listSigningKeyIds((InputStream) var5, (InputStream) var4, var3);
        } finally {
            IOUtil.closeStream(var4);
            IOUtil.closeStream(var5);
        }

        return var8;
    }

    public boolean isSignedOnly(String var1) throws PGPException, IOException {
        FileInputStream var2 = new FileInputStream(var1);
        return this.isSignedOnly((InputStream) var2);
    }

    public boolean isSignedOnly(InputStream var1) throws PGPException, IOException {
        var1 = PGPUtil.getDecoderStream(var1);
        PGPObjectFactory2 var5 = new PGPObjectFactory2(var1);

        Object var2;
        try {
            var2 = var5.nextObject();
        } catch (IOException var4) {
            throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var4);
        }

        if (var2 instanceof PGPMarker) {
            var2 = var5.nextObject();
        }

        if (var2 instanceof PGPOnePassSignatureList) {
            return true;
        } else if (var2 instanceof PGPSignatureList) {
            return true;
        } else {
            if (var2 instanceof PGPCompressedData) {
                PGPCompressedData var6 = (PGPCompressedData) var2;

                BufferedInputStream var7;
                try {
                    var7 = new BufferedInputStream(var6.getDataStream());
                } catch (org.spongycastle.openpgp.PGPException var3) {
                    throw IOUtil.newPGPException(var3);
                }

                Object var8;
                if ((var8 = (new PGPObjectFactory2(var7)).nextObject()) instanceof PGPOnePassSignatureList) {
                    return true;
                }

                if (var8 instanceof PGPSignatureList) {
                    return true;
                }
            }

            return false;
        }
    }

    public CypherAlgorithm getCypherAlgorithmUsed(InputStream var1, String var2, String var3) throws PGPException, IOException {
        FileInputStream var4 = null;

        CypherAlgorithm var7;
        try {
            var4 = new FileInputStream(var2);
            var7 = this.getCypherAlgorithmUsed((InputStream) var1, (InputStream) var4, var3);
        } finally {
            IOUtil.closeStream(var4);
        }

        return var7;
    }

    public CypherAlgorithm getCypherAlgorithmUsed(String var1, String var2, String var3) throws PGPException, IOException {
        InputStream var4 = null;
        FileInputStream var5 = null;

        CypherAlgorithm var8;
        try {
            var5 = new FileInputStream(var2);
            var4 = readFileOrAsciiString(var1, "encryptedData");
            var8 = this.getCypherAlgorithmUsed((InputStream) var4, (InputStream) var5, var3);
        } finally {
            IOUtil.closeStream(var5);
            IOUtil.closeStream(var4);
        }

        return var8;
    }

    public CypherAlgorithm getCypherAlgorithmUsed(String var1, KeyStore var2, String var3) throws PGPException, IOException {
        InputStream var4 = null;

        CypherAlgorithm var7;
        try {
            var4 = readFileOrAsciiString(var1, "encryptedData");
            var7 = this.getCypherAlgorithmUsed(var4, var2, var3);
        } finally {
            IOUtil.closeStream(var4);
        }

        return var7;
    }

    public CypherAlgorithm getCypherAlgorithmUsed(InputStream var1, InputStream var2, String var3) throws PGPException, IOException {
        var1 = PGPUtil.getDecoderStream(var1);
        PGPObjectFactory2 var6 = new PGPObjectFactory2(var1);

        Object var4;
        try {
            var4 = var6.nextObject();
        } catch (IOException var5) {
            throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var5);
        }

        if (var4 instanceof PGPMarker) {
            var4 = var6.nextObject();
        }

        if (var4 instanceof PGPEncryptedDataList) {
            PGPEncryptedDataList var7 = (PGPEncryptedDataList) var4;
            return this.b(var7, (KeyStore) null, var2, var3);
        } else {
            return CypherAlgorithm.NONE;
        }
    }

    public CypherAlgorithm getCypherAlgorithmUsed(InputStream var1, KeyStore var2, String var3) throws PGPException, IOException {
        var1 = PGPUtil.getDecoderStream(var1);
        PGPObjectFactory2 var6 = new PGPObjectFactory2(var1);

        Object var4;
        try {
            var4 = var6.nextObject();
        } catch (IOException var5) {
            throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var5);
        }

        if (var4 instanceof PGPMarker) {
            var4 = var6.nextObject();
        }

        if (var4 instanceof PGPEncryptedDataList) {
            PGPEncryptedDataList var7 = (PGPEncryptedDataList) var4;
            return this.b(var7, var2, (InputStream) null, var3);
        } else {
            return CypherAlgorithm.NONE;
        }
    }

    public long[] listSigningKeyIds(InputStream var1, InputStream var2, String var3) throws PGPException, IOException {
        var1 = PGPUtil.getDecoderStream(var1);
        PGPObjectFactory2 var8 = new PGPObjectFactory2(var1);

        Object var4;
        try {
            var4 = var8.nextObject();
        } catch (IOException var5) {
            throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var5);
        }

        if (var4 instanceof PGPMarker) {
            var4 = var8.nextObject();
        }

        if (var4 instanceof PGPEncryptedDataList) {
            PGPEncryptedDataList var9 = (PGPEncryptedDataList) var4;

            try {
                if (var2 == null) {
                    throw new FileIsEncryptedException("The data is encrypted.");
                }

                return this.a((PGPEncryptedDataList) var9, (KeyStore) null, (InputStream) var2, (String) var3);
            } catch (SignatureException var7) {
            }
        } else if (var4 instanceof PGPCompressedData) {
            try {
                return a((PGPCompressedData) var4);
            } catch (SignatureException var6) {
            }
        } else {
            if (var4 instanceof PGPOnePassSignatureList) {
                return a((PGPOnePassSignatureList) var4);
            }

            if (var4 instanceof PGPSignatureList) {
                return a((PGPSignatureList) var4);
            }
        }

        return new long[0];
    }

    public long[] listSigningKeyIds(InputStream var1, KeyStore var2, String var3) throws PGPException, IOException {
        var1 = PGPUtil.getDecoderStream(var1);
        PGPObjectFactory2 var8 = new PGPObjectFactory2(var1);

        Object var4;
        try {
            var4 = var8.nextObject();
        } catch (IOException var5) {
            throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var5);
        }

        if (var4 instanceof PGPMarker) {
            var4 = var8.nextObject();
        }

        if (var4 instanceof PGPEncryptedDataList) {
            PGPEncryptedDataList var9 = (PGPEncryptedDataList) var4;

            try {
                return this.a((PGPEncryptedDataList) var9, (KeyStore) var2, (InputStream) null, (String) var3);
            } catch (SignatureException var7) {
            }
        } else if (var4 instanceof PGPCompressedData) {
            try {
                return a((PGPCompressedData) var4);
            } catch (SignatureException var6) {
            }
        } else {
            if (var4 instanceof PGPOnePassSignatureList) {
                return a((PGPOnePassSignatureList) var4);
            }

            if (var4 instanceof PGPSignatureList) {
                return a((PGPSignatureList) var4);
            }
        }

        return new long[0];
    }

    public SignatureItem[] listSignatures(String var1) throws PGPException, IOException {
        InputStream var2 = null;

        SignatureItem[] var5;
        try {
            var2 = BaseLib.readFileOrAsciiString(var1, "dataFileName");
            var5 = this.listSignatures((InputStream) var2, (KeyStore) null, (String) null);
        } finally {
            IOUtil.closeStream(var2);
        }

        return var5;
    }

    public SignatureItem[] listSignatures(InputStream var1, String var2, String var3) throws PGPException, IOException {
        FileInputStream var4 = null;

        SignatureItem[] var7;
        try {
            var4 = new FileInputStream(var2);
            var7 = this.listSignatures((InputStream) var1, (InputStream) var4, var3);
        } finally {
            IOUtil.closeStream(var4);
        }

        return var7;
    }

    public SignatureItem[] listSignatures(String var1, String var2, String var3) throws PGPException, IOException {
        FileInputStream var4 = null;
        InputStream var5 = null;

        SignatureItem[] var8;
        try {
            var4 = new FileInputStream(var2);
            var5 = readFileOrAsciiString(var1, "dataFileName");
            var8 = this.listSignatures((InputStream) var5, (InputStream) var4, var3);
        } finally {
            IOUtil.closeStream(var4);
            IOUtil.closeStream(var5);
        }

        return var8;
    }

    public SignatureItem[] listSignatures(InputStream var1) throws PGPException, IOException {
        return this.listSignatures((InputStream) var1, (KeyStore) null, (String) null);
    }

    public SignatureItem[] listSignatures(InputStream var1, InputStream var2, String var3) throws PGPException, IOException {
        var1 = PGPUtil.getDecoderStream(var1);
        PGPObjectFactory2 var8 = new PGPObjectFactory2(var1);

        Object var4;
        try {
            var4 = var8.nextObject();
        } catch (IOException var5) {
            throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var5);
        }

        if (var4 instanceof PGPMarker) {
            var4 = var8.nextObject();
        }

        if (var4 instanceof PGPEncryptedDataList) {
            PGPEncryptedDataList var9 = (PGPEncryptedDataList) var4;

            try {
                if (var2 == null) {
                    throw new FileIsEncryptedException("The data is encrypted.");
                }

                return this.c(var9, (KeyStore) null, var2, var3);
            } catch (SignatureException var7) {
            }
        } else if (var4 instanceof PGPCompressedData) {
            try {
                return this.b((PGPCompressedData) var4);
            } catch (SignatureException var6) {
            }
        } else {
            if (var4 instanceof PGPOnePassSignatureList) {
                return this.a((PGPObjectFactory) var8);
            }

            if (var4 instanceof PGPSignatureList) {
                return this.b((PGPSignatureList) var4);
            }
        }

        return new SignatureItem[0];
    }

    public SignatureItem[] listSignatures(InputStream var1, KeyStore var2, String var3) throws PGPException, IOException {
        var1 = PGPUtil.getDecoderStream(var1);
        PGPObjectFactory2 var8 = new PGPObjectFactory2(var1);

        Object var4;
        try {
            var4 = var8.nextObject();
        } catch (IOException var5) {
            throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var5);
        }

        if (var4 instanceof PGPMarker) {
            var4 = var8.nextObject();
        }

        if (var4 instanceof PGPEncryptedDataList) {
            PGPEncryptedDataList var9 = (PGPEncryptedDataList) var4;

            try {
                return this.c(var9, var2, (InputStream) null, var3);
            } catch (SignatureException var7) {
            }
        } else if (var4 instanceof PGPCompressedData) {
            try {
                return this.b((PGPCompressedData) var4);
            } catch (SignatureException var6) {
            }
        } else {
            if (var4 instanceof PGPOnePassSignatureList) {
                return this.a((PGPObjectFactory) var8);
            }

            if (var4 instanceof PGPSignatureList) {
                return this.b((PGPSignatureList) var4);
            }
        }

        return new SignatureItem[0];
    }

    public SignatureItem[] listSignatures(String var1, KeyStore var2, String var3) throws PGPException, IOException {
        InputStream var4 = null;

        SignatureItem[] var7;
        try {
            var4 = readFileOrAsciiString(var1, "encryptedData");
            var7 = this.listSignatures(var4, var2, var3);
        } finally {
            IOUtil.closeStream(var4);
        }

        return var7;
    }

    public ContentItem[] listOpenPGPFile(String var1, String var2, String var3) throws PGPException, IOException {
        FileInputStream var4 = null;
        BufferedInputStream var5 = null;

        ContentItem[] var8;
        try {
            var4 = new FileInputStream(var2);
            var5 = new BufferedInputStream(new FileInputStream(var1));
            var8 = this.listOpenPGPStream(var5, var4, var3);
        } finally {
            IOUtil.closeStream(var4);
            IOUtil.closeStream(var5);
        }

        return var8;
    }

    public ContentItem[] listOpenPGPFile(String var1) throws PGPException, IOException {
        BufferedInputStream var2 = null;

        ContentItem[] var5;
        try {
            var2 = new BufferedInputStream(new FileInputStream(var1));
            var5 = this.listOpenPGPStream(var2);
        } finally {
            IOUtil.closeStream((InputStream) null);
            IOUtil.closeStream(var2);
        }

        return var5;
    }

    public ContentItem[] listOpenPGPStream(InputStream var1, InputStream var2, String var3) throws PGPException, IOException {
        var1 = PGPUtil.getDecoderStream(var1);
        PGPObjectFactory2 var11 = new PGPObjectFactory2(var1);
        Object var4 = null;

        try {
            var4 = var11.nextObject();
        } catch (IOException var10) {
            throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var10);
        }

        if (var4 instanceof PGPMarker) {
            var4 = var11.nextObject();
        }

        ContentItem[] var5 = new ContentItem[0];
        if (var4 instanceof PGPEncryptedDataList) {
            PGPEncryptedDataList var12 = (PGPEncryptedDataList) var4;

            try {
                var5 = this.a(var12, false, (KeyStore) null, var2, var3, (InputStream) null);
            } catch (SignatureException var9) {
            }
        } else if (var4 instanceof PGPCompressedData) {
            try {
                var5 = this.a((PGPCompressedData) var4, false, (KeyStore) null, (InputStream) null);
            } catch (SignatureException var8) {
            }
        } else if (var4 instanceof PGPOnePassSignatureList) {
            try {
                var5 = this.a((PGPOnePassSignatureList) ((PGPOnePassSignatureList) var4), (PGPObjectFactory) var11, (KeyStore) null, (InputStream) null);
            } catch (SignatureException var7) {
            }
        } else if (var4 instanceof PGPSignatureList) {
            try {
                var5 = this.a((PGPSignatureList) ((PGPSignatureList) var4), (PGPObjectFactory) var11, (KeyStore) null, (InputStream) null);
            } catch (SignatureException var6) {
            }
        } else {
            if (!(var4 instanceof PGPLiteralData)) {
                throw new PGPException("Unknown message format: " + var4);
            }

            var5 = a((PGPLiteralData) var4);
        }

        return var5;
    }

    public ContentItem[] listOpenPGPStream(InputStream var1) throws PGPException, IOException {
        var1 = PGPUtil.getDecoderStream(var1);
        PGPObjectFactory2 var8 = new PGPObjectFactory2(var1);
        Object var2 = null;

        try {
            var2 = var8.nextObject();
        } catch (IOException var7) {
            throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var7);
        }

        if (var2 instanceof PGPMarker) {
            var2 = var8.nextObject();
        }

        ContentItem[] var3 = new ContentItem[0];
        if (var2 instanceof PGPEncryptedDataList) {
            throw new FileIsEncryptedException("The supplied data is encrypted. Use the overloaded version that accepts private decryption key instead.");
        } else {
            if (var2 instanceof PGPCompressedData) {
                try {
                    var3 = this.a((PGPCompressedData) var2, false, (KeyStore) null, (InputStream) null);
                } catch (SignatureException var6) {
                }
            } else if (var2 instanceof PGPOnePassSignatureList) {
                try {
                    var3 = this.a((PGPOnePassSignatureList) ((PGPOnePassSignatureList) var2), (PGPObjectFactory) var8, (KeyStore) null, (InputStream) null);
                } catch (SignatureException var5) {
                }
            } else if (var2 instanceof PGPSignatureList) {
                try {
                    var3 = this.a((PGPSignatureList) ((PGPSignatureList) var2), (PGPObjectFactory) var8, (KeyStore) null, (InputStream) null);
                } catch (SignatureException var4) {
                }
            } else {
                if (!(var2 instanceof PGPLiteralData)) {
                    throw new PGPException("Unknown message format: " + var2);
                }

                var3 = a((PGPLiteralData) var2);
            }

            return var3;
        }
    }

    public SignatureItem[] listRevocationCertificate(String var1) throws IOException, PGPException {
        FileInputStream var2 = null;

        SignatureItem[] var5;
        try {
            var2 = new FileInputStream(var1);
            var5 = this.listRevocationCertificate((InputStream) var2);
        } finally {
            IOUtil.closeStream(var2);
        }

        return var5;
    }

    public SignatureItem[] listRevocationCertificate(InputStream var1) throws IOException, PGPException {
        var1 = PGPUtil.getDecoderStream(var1);
        Object var5 = new PGPObjectFactory2(var1);

        Object var2;
        try {
            var2 = ((PGPObjectFactory2) var5).nextObject();
        } catch (IOException var4) {
            throw new NonPGPDataException("The supplied data is not a valid OpenPGP message", var4);
        }

        if (var2 instanceof PGPMarker) {
            var2 = ((PGPObjectFactory2) var5).nextObject();
        }

        if (var2 instanceof PGPSignatureList) {
            PGPSignatureList var6 = (PGPSignatureList) var2;
            ArrayList var7 = new ArrayList();

            for (int var3 = 0; var3 != var6.size(); ++var3) {
                if (var6.get(var3).getSignatureType() == 32) {
                    var7.add(a(var6.get(var3)));
                }
            }

            return (SignatureItem[]) var7.toArray(new SignatureItem[var7.size()]);
        } else {
            return !(var2 instanceof PGPSignature) || ((PGPSignature) (var5 = (PGPSignature) var2)).getSignatureType() != 32 && ((PGPSignature) var5).getSignatureType() != 40 ? new SignatureItem[0] : new SignatureItem[]{new SignatureItem(((PGPSignature) var5).getKeyID(), ((PGPSignature) var5).getCreationTime())};
        }
    }

    private ContentItem[] a(PGPEncryptedDataList var1, boolean var2, KeyStore var3, InputStream var4, String var5, InputStream var6) throws IOException, WrongPasswordException, WrongPrivateKeyException, PGPException, SignatureException {
        PGPPrivateKey var11 = null;
        PGPSecretKeyRingCollection var12;
        if (var4 != null) {
            var12 = createPGPSecretKeyRingCollection(var4);
        } else {
            var12 = var3.getRawSecretKeys();
        }

        PGPPublicKeyEncryptedData var13 = null;
        Iterator var8 = var1.getEncryptedDataObjects();

        while (var11 == null && var8.hasNext()) {
            Object var14;
            if ((var14 = var8.next()) instanceof PGPPublicKeyEncryptedData) {
                var13 = (PGPPublicKeyEncryptedData) var14;
                if ((var11 = this.getPrivateKey(var12, var13.getKeyID(), var5.toCharArray())) == null) {
                    continue;
                }
                break;
            }
        }

        if (var11 == null) {
            throw new WrongPrivateKeyException("secret key for message not found.");
        } else {
            InputStream var9;
            try {
                var9 = var13.getDataStream(staticBCFactory.CreatePublicKeyDataDecryptorFactory(var11));
            } catch (org.spongycastle.openpgp.PGPException var7) {
                throw IOUtil.newPGPException(var7);
            }

            Object var10;
            PGPObjectFactory2 var15;
            if ((var10 = (var15 = new PGPObjectFactory2(var9)).nextObject()) instanceof PGPCompressedData) {
                return this.a((PGPCompressedData) var10, false, (KeyStore) null, (InputStream) null);
            } else if (var10 instanceof PGPOnePassSignatureList) {
                return this.a((PGPOnePassSignatureList) ((PGPOnePassSignatureList) var10), (PGPObjectFactory) var15, (KeyStore) null, (InputStream) null);
            } else if (var10 instanceof PGPLiteralData) {
                return a((PGPLiteralData) var10);
            } else {
                throw new PGPException("Unknown message format: " + var10.getClass().getName());
            }
        }
    }

    private ContentItem[] a(PGPCompressedData var1, boolean var2, KeyStore var3, InputStream var4) throws PGPException, IOException, SignatureException {
        BufferedInputStream var11;
        try {
            var11 = new BufferedInputStream(var1.getDataStream());
        } catch (org.spongycastle.openpgp.PGPException var9) {
            throw IOUtil.newPGPException(var9);
        }

        ContentItem[] var12;
        try {
            PGPObjectFactory2 var5;
            Object var6;
            if ((var6 = (var5 = new PGPObjectFactory2(var11)).nextObject()) instanceof PGPLiteralData) {
                var12 = a((PGPLiteralData) var6);
                return var12;
            }

            if (!(var6 instanceof PGPOnePassSignatureList)) {
                if (!(var6 instanceof PGPSignatureList)) {
                    throw new PGPException("Unknown message format: " + var6.getClass().getName());
                }

                if (var2) {
                    var12 = this.a((PGPSignatureList) ((PGPSignatureList) var6), (PGPObjectFactory) var5, (KeyStore) var3, (InputStream) var4);
                    return var12;
                }

                var12 = this.a((PGPSignatureList) ((PGPSignatureList) var6), (PGPObjectFactory) var5, (KeyStore) null, (InputStream) null);
                return var12;
            }

            if (!var2) {
                var12 = this.a((PGPOnePassSignatureList) ((PGPOnePassSignatureList) var6), (PGPObjectFactory) var5, (KeyStore) null, (InputStream) null);
                return var12;
            }

            var12 = this.a((PGPOnePassSignatureList) ((PGPOnePassSignatureList) var6), (PGPObjectFactory) var5, (KeyStore) var3, (InputStream) var4);
        } finally {
            IOUtil.closeStream(var11);
        }

        return var12;
    }

    private static ContentItem[] a(PGPLiteralData var0) throws IOException {
        String var1;
        if ((var1 = var0.getFileName()).toUpperCase().endsWith(".TAR")) {
            TarInputStream var7 = null;

            try {
                var7 = new TarInputStream(var0.getInputStream());
                ArrayList var5 = new ArrayList();

                for (TarEntry var2 = var7.getNextEntry(); var2 != null; var2 = var7.getNextEntry()) {
                    var5.add(new ContentItem(var2.getName(), var2.getModTime(), var2.isDirectory()));
                }

                ContentItem[] var6 = (ContentItem[]) var5.toArray(new ContentItem[var5.size()]);
                return var6;
            } finally {
                IOUtil.closeStream(var7);
            }
        } else {
            return new ContentItem[]{new ContentItem(var1, var0.getModificationTime())};
        }
    }

    private ContentItem[] a(PGPOnePassSignatureList var1, PGPObjectFactory var2, KeyStore var3, InputStream var4) throws PGPException, IOException, SignatureException {
        PGPOnePassSignature var5 = null;
        PGPPublicKey var6 = null;
        if (var4 != null || var3 != null) {
            for (int var7 = 0; var7 != var1.size(); ++var7) {
                var5 = var1.get(var7);
                if (var4 != null) {
                    var6 = readPublicVerificationKey(var4, var5.getKeyID());
                } else {
                    var6 = readPublicVerificationKey(var3, var5.getKeyID());
                }

                if (var6 != null) {
                    break;
                }
            }

            if (var6 == null) {
                throw new PGPException("No public key could be found for signature.");
            }

            try {
                var5.init(staticBCFactory.CreatePGPContentVerifierBuilderProvider(), var6);
            } catch (org.spongycastle.openpgp.PGPException var8) {
                throw IOUtil.newPGPException(var8);
            }
        }

        Object var10;
        if ((var10 = var2.nextObject()) instanceof PGPLiteralData) {
            ContentItem[] var9 = a((PGPLiteralData) var10);
            return var9;
        } else {
            throw new PGPException("Unknown message format: " + var10.getClass().getName());
        }
    }

    private ContentItem[] a(PGPSignatureList var1, PGPObjectFactory var2, KeyStore var3, InputStream var4) throws PGPException, IOException, SignatureException {
        PGPSignature var5 = null;
        if (var4 != null || var3 != null) {
            PGPPublicKey var6 = null;

            for (int var7 = 0; var7 < var1.size(); ++var7) {
                if ((var5 = var1.get(var7)).getSignatureType() == 0 || var5.getSignatureType() == 1 || var5.getSignatureType() == 16) {
                    if (var4 != null) {
                        var6 = readPublicVerificationKey(var4, var5.getKeyID());
                    } else {
                        var6 = readPublicVerificationKey(var3, var5.getKeyID());
                    }

                    if (var6 != null) {
                        break;
                    }
                }
            }

            if (var6 == null) {
                throw new PGPException("No public key could be found for signature.");
            }

            try {
                var5.init(staticBCFactory.CreatePGPContentVerifierBuilderProvider(), var6);
            } catch (org.spongycastle.openpgp.PGPException var8) {
                throw IOUtil.newPGPException(var8);
            }
        }

        Object var9;
        if ((var9 = var2.nextObject()) instanceof PGPLiteralData) {
            ContentItem[] var10 = a((PGPLiteralData) var9);
            return var10;
        } else {
            throw new PGPException("Unknown message format: " + var9.getClass().getName());
        }
    }

    private long[] a(PGPEncryptedDataList var1, KeyStore var2, InputStream var3, String var4) throws IOException, WrongPasswordException, WrongPrivateKeyException, PGPException, SignatureException {
        PGPPrivateKey var5 = null;
        PGPSecretKeyRingCollection var11;
        if (var3 != null) {
            var11 = createPGPSecretKeyRingCollection(var3);
        } else {
            var11 = var2.getRawSecretKeys();
        }

        PGPPublicKeyEncryptedData var12 = null;
        Iterator var8 = var1.getEncryptedDataObjects();

        while (var5 == null && var8.hasNext()) {
            Object var6;
            if ((var6 = var8.next()) instanceof PGPPublicKeyEncryptedData) {
                var12 = (PGPPublicKeyEncryptedData) var6;
                if ((var5 = this.getPrivateKey(var11, var12.getKeyID(), var4.toCharArray())) == null) {
                    continue;
                }
                break;
            }
        }

        if (var5 == null) {
            throw new WrongPrivateKeyException("secret key for message not found.");
        } else {
            InputStream var9;
            try {
                var9 = var12.getDataStream(staticBCFactory.CreatePublicKeyDataDecryptorFactory(var5));
            } catch (org.spongycastle.openpgp.PGPException var7) {
                throw IOUtil.newPGPException(var7);
            }

            Object var10;
            if ((var10 = (new PGPObjectFactory2(var9)).nextObject()) instanceof PGPCompressedData) {
                return a((PGPCompressedData) var10);
            } else {
                return var10 instanceof PGPOnePassSignatureList ? a((PGPOnePassSignatureList) var10) : new long[0];
            }
        }
    }

    private CypherAlgorithm b(PGPEncryptedDataList var1, KeyStore var2, InputStream var3, String var4) throws IOException, WrongPasswordException, WrongPrivateKeyException, PGPException {
        PGPPrivateKey var5 = null;
        PGPSecretKeyRingCollection var10;
        if (var3 != null) {
            var10 = createPGPSecretKeyRingCollection(var3);
        } else {
            var10 = var2.getRawSecretKeys();
        }

        Iterator var11 = var1.getEncryptedDataObjects();

        while (var5 == null && var11.hasNext()) {
            Object var7;
            if (!((var7 = var11.next()) instanceof PGPPublicKeyEncryptedData)) {
                return CypherAlgorithm.NONE;
            }

            PGPPublicKeyEncryptedData var8 = (PGPPublicKeyEncryptedData) var7;
            if ((var5 = this.getPrivateKey(var10, var8.getKeyID(), var4.toCharArray())) != null) {
                int var9;
                try {
                    var9 = var8.getSymmetricAlgorithm(staticBCFactory.CreatePublicKeyDataDecryptorFactory(var5));
                } catch (org.spongycastle.openpgp.PGPException var6) {
                    throw IOUtil.newPGPException(var6);
                }

                return this.getSymmetricAlgorithm(var9);
            }
        }

        if (var5 == null) {
            throw new WrongPrivateKeyException("secret key for message not found.");
        } else {
            return CypherAlgorithm.NONE;
        }
    }

    private static long[] a(PGPCompressedData var0) throws PGPException, IOException, SignatureException {
        BufferedInputStream var4;
        try {
            var4 = new BufferedInputStream(var0.getDataStream());
        } catch (org.spongycastle.openpgp.PGPException var3) {
            throw IOUtil.newPGPException(var3);
        }

        long[] var1;
        int var2;
        Object var5;
        if ((var5 = (new PGPObjectFactory2(var4)).nextObject()) instanceof PGPOnePassSignatureList) {
            PGPOnePassSignatureList var7;
            var1 = new long[(var7 = (PGPOnePassSignatureList) var5).size()];

            for (var2 = 0; var2 != var7.size(); ++var2) {
                var1[var2] = var7.get(var2).getKeyID();
            }

            return var1;
        } else if (!(var5 instanceof PGPSignatureList)) {
            return new long[0];
        } else {
            PGPSignatureList var6;
            var1 = new long[(var6 = (PGPSignatureList) var5).size()];

            for (var2 = 0; var2 != var6.size(); ++var2) {
                var1[var2] = var6.get(var2).getKeyID();
            }

            return var1;
        }
    }

    private static long[] a(PGPOnePassSignatureList var0) {
        long[] var1 = new long[var0.size()];

        for (int var2 = 0; var2 != var0.size(); ++var2) {
            var1[var2] = var0.get(var2).getKeyID();
        }

        return var1;
    }

    private static long[] a(PGPSignatureList var0) {
        long[] var1 = new long[var0.size()];

        for (int var2 = 0; var2 != var0.size(); ++var2) {
            var1[var2] = var0.get(var2).getKeyID();
        }

        return var1;
    }

    private static SignatureItem a(PGPSignature var0) {
        SignatureItem var1 = new SignatureItem(var0.getKeyID(), var0.getCreationTime());
        if (var0.getHashedSubPackets() != null && var0.getHashedSubPackets().getSignerUserID() != null) {
            var1.setUserId(var0.getHashedSubPackets().getSignerUserID());
        }

        return var1;
    }

    private SignatureItem[] c(PGPEncryptedDataList var1, KeyStore var2, InputStream var3, String var4) throws IOException, WrongPasswordException, WrongPrivateKeyException, PGPException, SignatureException {
        PGPPrivateKey var5 = null;
        PGPSecretKeyRingCollection var11;
        if (var3 != null) {
            var11 = createPGPSecretKeyRingCollection(var3);
        } else {
            var11 = var2.getRawSecretKeys();
        }

        PGPPublicKeyEncryptedData var12 = null;
        Iterator var8 = var1.getEncryptedDataObjects();

        while (var5 == null && var8.hasNext()) {
            Object var6;
            if ((var6 = var8.next()) instanceof PGPPublicKeyEncryptedData) {
                var12 = (PGPPublicKeyEncryptedData) var6;
                if ((var5 = this.getPrivateKey(var11, var12.getKeyID(), var4.toCharArray())) == null) {
                    continue;
                }
                break;
            }
        }

        if (var5 == null) {
            throw new WrongPrivateKeyException("secret key for message not found.");
        } else {
            InputStream var9;
            try {
                var9 = var12.getDataStream(staticBCFactory.CreatePublicKeyDataDecryptorFactory(var5));
            } catch (org.spongycastle.openpgp.PGPException var7) {
                throw IOUtil.newPGPException(var7);
            }

            Object var10;
            PGPObjectFactory2 var13;
            if ((var10 = (var13 = new PGPObjectFactory2(var9)).nextObject()) instanceof PGPCompressedData) {
                return this.b((PGPCompressedData) var10);
            } else if (var10 instanceof PGPOnePassSignatureList) {
                return this.a((PGPObjectFactory) var13);
            } else {
                return var10 instanceof PGPSignatureList ? this.b((PGPSignatureList) var10) : new SignatureItem[0];
            }
        }
    }

    private SignatureItem[] b(PGPCompressedData var1) throws PGPException, IOException, SignatureException {
        BufferedInputStream var4;
        try {
            var4 = new BufferedInputStream(var1.getDataStream());
        } catch (org.spongycastle.openpgp.PGPException var3) {
            throw IOUtil.newPGPException(var3);
        }

        Object var2;
        PGPObjectFactory2 var5;
        if ((var2 = (var5 = new PGPObjectFactory2(var4)).nextObject()) instanceof PGPOnePassSignatureList) {
            return this.a((PGPObjectFactory) var5);
        } else {
            return var2 instanceof PGPSignatureList ? this.b((PGPSignatureList) var2) : new SignatureItem[0];
        }
    }

    private SignatureItem[] a(PGPObjectFactory var1) throws IOException {
        Object var2;
        if ((var2 = var1.nextObject()) instanceof PGPLiteralData) {
            InputStream var8 = ((PGPLiteralData) var2).getInputStream();
            byte[] var3 = new byte[1048576];

            try {
                while (true) {
                    if (var8.read(var3, 0, var3.length) >= 0) {
                        continue;
                    }
                }
            } finally {
                IOUtil.closeStream(var8);
            }
        }

        if ((var2 = var1.nextObject()) == null) {
            return new SignatureItem[0];
        } else {
            PGPSignatureList var9;
            SignatureItem[] var7 = new SignatureItem[(var9 = (PGPSignatureList) var2).size()];

            for (int var4 = 0; var4 != var9.size(); ++var4) {
                var7[var4] = a(var9.get(var4));
            }

            return var7;
        }
    }

    private SignatureItem[] b(PGPSignatureList var1) {
        SignatureItem[] var2 = new SignatureItem[var1.size()];

        for (int var3 = 0; var3 != var1.size(); ++var3) {
            var2[var3] = a(var1.get(var3));
        }

        return var2;
    }
}
