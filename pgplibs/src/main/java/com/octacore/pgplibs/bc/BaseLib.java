package com.octacore.pgplibs.bc;

import com.octacore.pgplibs.CypherAlgorithm;
import com.octacore.pgplibs.KeyStore;
import com.octacore.pgplibs.PGPException;
import com.octacore.pgplibs.exceptions.NoPublicKeyFoundException;
import com.octacore.pgplibs.exceptions.WrongPasswordException;

import org.spongycastle.bcpg.ArmoredInputStream;
import org.spongycastle.bcpg.ExperimentalPacket;
import org.spongycastle.bcpg.S2K;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.openpgp.PGPOnePassSignatureList;
import org.spongycastle.openpgp.PGPPrivateKey;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPPublicKeyRing;
import org.spongycastle.openpgp.PGPPublicKeyRingCollection;
import org.spongycastle.openpgp.PGPSecretKey;
import org.spongycastle.openpgp.PGPSecretKeyRing;
import org.spongycastle.openpgp.PGPSecretKeyRingCollection;
import org.spongycastle.openpgp.PGPSignature;
import org.spongycastle.openpgp.PGPSignatureSubpacketVector;
import org.spongycastle.openpgp.PGPUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class BaseLib {
    protected static BCFactory staticBCFactory = new BCFactory(false);
    protected static final int DEFAULT_BUFFER_SIZE = 1048576;
    protected static final String NOT_A_VALID_OPENPGP_MESSAGE = "The supplied data is not a valid OpenPGP message";
    protected static final String UNKNOWN_MESSAGE_FORMAT = "Unknown message format: ";
    public static final String BOUNCY_CASTLE_PROVIDER = "SC";
    private static final String a = System.getProperty("line.separator");
    private static final int[] b = new int[]{19, 18, 17, 16};

    protected BaseLib() {
        if (Security.getProvider("SC") == null) {
            try {
                Security.insertProviderAt(new BouncyCastleProvider(), 1);
            } catch (NoClassDefFoundError var1) {
                throw new Error("Make sure you have all jar files required by Java OpenPGP Library in your classpath.");
            }
        }
    }

    public static List loadKeyStream(InputStream var0) throws FileNotFoundException, IOException, PGPException {
        Object var1 = new LinkedList();
        BoolValue var2 = new BoolValue();
        if ((var0 = PGPUtil.getDecoderStream(cleanGnuPGBackupKeys(var0))) instanceof ArmoredInputStream) {
            ArmoredInputStream var4 = (ArmoredInputStream)var0;

            while(!var4.isEndOfStream()) {
                List var3 = a((InputStream)var4, (BoolValue)var2);
                ((List)var1).addAll(var3);
                if (var2.isValue()) {
                    break;
                }
            }
        } else {
            var1 = a(var0, var2);
        }

        return (List)var1;
    }

    private static List a(InputStream var0, BoolValue var1) throws PGPException, IOException {
        LinkedList var2 = new LinkedList();
        PGPObjectFactory2 var5;
        (var5 = new PGPObjectFactory2(var0)).setLoadingKey(true);

        try {
            for(Object var3 = var5.nextObject(); var3 != null; var3 = var5.nextObject()) {
                if (var3 instanceof PGPPublicKeyRing) {
                    PGPPublicKeyRing var6 = (PGPPublicKeyRing)var3;
                    var2.add(var6);
                } else if (var3 instanceof PGPSecretKeyRing) {
                    PGPSecretKeyRing var7 = (PGPSecretKeyRing)var3;
                    var2.add(var7);
                } else if (!(var3 instanceof ExperimentalPacket) && !(var3 instanceof PGPOnePassSignatureList)) {
                    throw new PGPException("Unexpected object found in stream: " + var3.getClass().getName());
                }
            }
        } catch (UnknownKeyPacketsException var4) {
            var1.setValue(true);
        }

        return var2;
    }

    public static PGPPrivateKey extractPrivateKey(PGPSecretKey var0, String var1) throws WrongPasswordException, PGPException {
        return extractPrivateKey(var0, var1 == null ? new char[0] : var1.toCharArray());
    }

    protected static PGPPrivateKey extractPrivateKey(PGPSecretKey var0, char[] var1) throws WrongPasswordException, PGPException {
        if (var0 == null) {
            return null;
        } else {
            try {
                return var0.extractPrivateKey(staticBCFactory.CreatePBESecretKeyDecryptor(var1));
            } catch (org.spongycastle.openpgp.PGPException var2) {
                if (var2.getMessage().toLowerCase().startsWith("checksum mismatch at 0 of 2")) {
                    throw new WrongPasswordException(var2.getMessage(), var2.getUnderlyingException());
                } else {
                    throw IOUtil.newPGPException(var2);
                }
            }
        }
    }

    protected static SecretKey makeKeyFromPassPhrase(int var0, char[] var1, String var2) throws NoSuchProviderException, org.spongycastle.openpgp.PGPException {
        return makeKeyFromPassPhrase(var0, (S2K)null, var1, Security.getProvider(var2));
    }

    public static SecretKey makeKeyFromPassPhrase(int var0, S2K var1, char[] var2, Provider var3) throws org.spongycastle.openpgp.PGPException, NoSuchProviderException {
        int var4;
        String var17;
        switch (var0) {
            case 1:
                var4 = 128;
                var17 = "IDEA";
                break;
            case 2:
                var4 = 192;
                var17 = "DES_EDE";
                break;
            case 3:
                var4 = 128;
                var17 = "CAST5";
                break;
            case 4:
                var4 = 128;
                var17 = "Blowfish";
                break;
            case 5:
                var4 = 128;
                var17 = "SAFER";
                break;
            case 6:
                var4 = 64;
                var17 = "DES";
                break;
            case 7:
                var4 = 128;
                var17 = "AES";
                break;
            case 8:
                var4 = 192;
                var17 = "AES";
                break;
            case 9:
                var4 = 256;
                var17 = "AES";
                break;
            case 10:
                var4 = 256;
                var17 = "Twofish";
                break;
            default:
                throw new org.spongycastle.openpgp.PGPException("unknown symmetric algorithm: " + var0);
        }

        byte[] var5 = new byte[var2.length];

        for(int var6 = 0; var6 != var2.length; ++var6) {
            var5[var6] = (byte)var2[var6];
        }

        byte[] var19 = new byte[(var4 + 7) / 8];
        var4 = 0;

        int var20;
        for(int var7 = 0; var4 < var19.length; ++var7) {
            MessageDigest var18;
            byte[] var21;
            if (var1 != null) {
                String var10000;
                switch (var1.getHashAlgorithm()) {
                    case 1:
                        var10000 = "MD5";
                        break;
                    case 2:
                        var10000 = "SHA1";
                        break;
                    default:
                        throw new org.spongycastle.openpgp.PGPException("unknown hash algorithm: " + var1.getHashAlgorithm());
                }

                String var8 = var10000;

                try {
                    var18 = a(var8, var3);
                } catch (NoSuchAlgorithmException var16) {
                    throw new org.spongycastle.openpgp.PGPException("can't find S2K digest", var16);
                }

                for(var20 = 0; var20 != var7; ++var20) {
                    var18.update((byte)0);
                }

                var21 = var1.getIV();
                switch (var1.getType()) {
                    case 0:
                        var18.update(var5);
                        break;
                    case 1:
                        var18.update(var21);
                        var18.update(var5);
                        break;
                    case 2:
                    default:
                        throw new org.spongycastle.openpgp.PGPException("unknown S2K type: " + var1.getType());
                    case 3:
                        long var13 = var1.getIterationCount();
                        var18.update(var21);
                        var18.update(var5);
                        var13 -= (long)(var21.length + var5.length);

                        while(var13 > 0L) {
                            if (var13 < (long)var21.length) {
                                var18.update(var21, 0, (int)var13);
                                break;
                            }

                            var18.update(var21);
                            if ((var13 -= (long)var21.length) < (long)var5.length) {
                                var18.update(var5, 0, (int)var13);
                                var13 = 0L;
                            } else {
                                var18.update(var5);
                                var13 -= (long)var5.length;
                            }
                        }
                }
            } else {
                try {
                    var18 = a("MD5", var3);
                } catch (NoSuchAlgorithmException var15) {
                    throw new org.spongycastle.openpgp.PGPException("can't find MD5 digest", var15);
                }

                for(var20 = 0; var20 != var7; ++var20) {
                    var18.update((byte)0);
                }

                var18.update(var5);
            }

            if ((var21 = var18.digest()).length > var19.length - var4) {
                System.arraycopy(var21, 0, var19, var4, var19.length - var4);
            } else {
                System.arraycopy(var21, 0, var19, var4, var21.length);
            }

            var4 += var21.length;
        }

        for(var20 = 0; var20 != var5.length; ++var20) {
            var5[var20] = 0;
        }

        return new SecretKeySpec(var19, var17);
    }

    private static MessageDigest a(String var0, Provider var1) throws NoSuchAlgorithmException {
        try {
            return MessageDigest.getInstance(var0, var1);
        } catch (NoSuchAlgorithmException var2) {
            return MessageDigest.getInstance(var0);
        }
    }

    protected static PGPSecretKeyRingCollection createPGPSecretKeyRingCollection(InputStream var0) throws IOException, PGPException {
        if ((var0 = PGPUtil.getDecoderStream(cleanGnuPGBackupKeys(var0))) instanceof ArmoredInputStream) {
            ArmoredInputStream var3 = (ArmoredInputStream)var0;

            PGPSecretKeyRingCollection var1;
            do {
                if (var3.isEndOfStream()) {
                    try {
                        return new PGPSecretKeyRingCollection(new ArrayList());
                    } catch (org.spongycastle.openpgp.PGPException var2) {
                        throw IOUtil.newPGPException(var2);
                    }
                }
            } while((var1 = a(var3)).size() <= 0);

            return var1;
        } else {
            return a(var0);
        }
    }

    private static PGPSecretKeyRingCollection a(InputStream var0) throws IOException, PGPException {
        PGPObjectFactory2 var5 = new PGPObjectFactory2(var0);
        HashMap var2 = new HashMap();

        Object var1;
        while((var1 = var5.nextObject()) != null) {
            if (var1 instanceof PGPSecretKeyRing) {
                PGPSecretKeyRing var6 = (PGPSecretKeyRing)var1;
                Long var3 = new Long(var6.getPublicKey().getKeyID());
                var2.put(var3, var6);
            }
        }

        try {
            return new PGPSecretKeyRingCollection(var2.values());
        } catch (org.spongycastle.openpgp.PGPException var4) {
            throw IOUtil.newPGPException(var4);
        }
    }

    protected PGPPrivateKey getPrivateKey(PGPSecretKeyRingCollection var1, long var2, char[] var4) throws WrongPasswordException, PGPException {
        PGPSecretKey var7;
        try {
            var7 = var1.getSecretKey(var2);
        } catch (org.spongycastle.openpgp.PGPException var5) {
            throw IOUtil.newPGPException(var5);
        }

        if (var7 == null) {
            return null;
        } else {
            try {
                return var7.extractPrivateKey(staticBCFactory.CreatePBESecretKeyDecryptor(var4));
            } catch (org.spongycastle.openpgp.PGPException var6) {
                if (var6.getMessage().toLowerCase().startsWith("checksum mismatch at 0 of 2")) {
                    throw new WrongPasswordException(var6.getMessage(), var6.getUnderlyingException());
                } else {
                    throw IOUtil.newPGPException(var6);
                }
            }
        }
    }

    protected static PGPPublicKeyRingCollection createPGPPublicKeyRingCollection(InputStream var0) throws IOException, PGPException {
        if ((var0 = PGPUtil.getDecoderStream(cleanGnuPGBackupKeys(var0))) instanceof ArmoredInputStream) {
            ArmoredInputStream var3 = (ArmoredInputStream)var0;

            PGPPublicKeyRingCollection var1;
            do {
                if (var3.isEndOfStream()) {
                    try {
                        return new PGPPublicKeyRingCollection(new ArrayList());
                    } catch (org.spongycastle.openpgp.PGPException var2) {
                        throw IOUtil.newPGPException(var2);
                    }
                }
            } while((var1 = b(var3)).size() <= 0);

            return var1;
        } else {
            return b(var0);
        }
    }

    private static PGPPublicKeyRingCollection b(InputStream var0) throws IOException, PGPException {
        PGPObjectFactory2 var6 = new PGPObjectFactory2(var0);
        HashMap var2 = new HashMap();

        Object var1;
        try {
            while((var1 = var6.nextObject()) != null) {
                if (var1 instanceof PGPPublicKeyRing) {
                    PGPPublicKeyRing var7 = (PGPPublicKeyRing)var1;
                    Long var3 = new Long(var7.getPublicKey().getKeyID());
                    var2.put(var3, var7);
                }
            }
        } catch (IOException var5) {
            throw new NoPublicKeyFoundException(var5.getMessage(), var5);
        }

        try {
            return new PGPPublicKeyRingCollection(var2.values());
        } catch (org.spongycastle.openpgp.PGPException var4) {
            throw IOUtil.newPGPException(var4);
        }
    }

    public static InputStream readFileOrAsciiString(String var0, String var1) throws IOException {
        return IOUtil.readFileOrAsciiString(var0, var1);
    }

    protected static PGPPublicKey readPublicVerificationKey(KeyStore var0, long var1) throws IOException {
        return readPublicVerificationKey(var0.getRawPublicKeys(), var1);
    }

    protected static PGPPublicKey readPublicVerificationKey(InputStream var0, long var1) throws IOException, PGPException {
        return readPublicVerificationKey(createPGPPublicKeyRingCollection(var0), var1);
    }

    protected static PGPPublicKey readPublicVerificationKey(PGPPublicKeyRingCollection var0, long var1) throws IOException {
        PGPPublicKey var3 = null;
        Iterator var6 = var0.getKeyRings();

        while(var3 == null && var6.hasNext()) {
            Iterator var4 = ((PGPPublicKeyRing)var6.next()).getPublicKeys();

            while(var3 == null && var4.hasNext()) {
                PGPPublicKey var5;
                if (isForVerification(var5 = (PGPPublicKey)var4.next()) && var1 == var5.getKeyID()) {
                    var3 = var5;
                }
            }
        }

        return var3;
    }

    protected static boolean isForVerification(PGPPublicKey var0) {
        if (var0.getAlgorithm() != 16 && var0.getAlgorithm() != 20 && var0.getAlgorithm() != 21 && var0.getAlgorithm() != 2) {
            PGPPublicKey var10000 = var0;
            boolean var4 = true;
            var0 = var0;
            if (var10000.isMasterKey()) {
                for(int var1 = 0; var1 != b.length; ++var1) {
                    Iterator var2 = var0.getSignaturesOfType(b[var1]);

                    while(var2.hasNext()) {
                        if (!a((PGPSignature)var2.next(), 2)) {
                            return false;
                        }
                    }
                }
            } else {
                Iterator var5 = var0.getSignaturesOfType(24);

                while(var5.hasNext()) {
                    if (!a((PGPSignature)var5.next(), 2)) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private static boolean a(PGPSignature var0, int var1) {
        PGPSignatureSubpacketVector var2;
        return !var0.hasSubpackets() || !(var2 = var0.getHashedSubPackets()).hasSubpacket(27) || (var2.getKeyFlags() & var1) != 0;
    }

    protected static void pipeAll(InputStream var0, OutputStream var1) throws IOException {
        byte[] var2 = new byte[1048576];

        int var3;
        while((var3 = var0.read(var2)) > 0) {
            var1.write(var2, 0, var3);
        }

    }

    public static InputStream cleanGnuPGBackupKeys(InputStream var0) throws IOException {
        Object var3;
        if (var0.markSupported()) {
            var3 = var0;
        } else {
            var3 = new BufferedInputStreamExtended(var0);
        }

        ((InputStream)var3).mark(4096);
        if (!(PGPUtil.getDecoderStream((InputStream)var3) instanceof ArmoredInputStream)) {
            return (InputStream)var3;
        } else {
            ((InputStream)var3).reset();
            DirectByteArrayOutputStream var1 = new DirectByteArrayOutputStream(4096);
            pipeAll((InputStream)var3, var1);
            StringBuffer var4;
            int var5 = (var4 = new StringBuffer(new String(var1.getArray(), 0, var1.size(), "ASCII"))).indexOf("-----BEGIN PGP P");
            int var2 = var4.lastIndexOf("-----END PGP P");
            if (var5 >= 0) {
                var4 = new StringBuffer(var4.substring(var5, var4.indexOf("-----", var2 + 1) + 5));
            }

            replaceAll(var4, "\r\r\n", a);
            if (var4.indexOf("Comment") != -1 || var4.indexOf("Version") != -1) {
                replaceAll(var4, "\r\n\r\n", a);
            }

            replaceAll(var4, "\\n", a);
            BufferedReader var6 = new BufferedReader(new StringReader(var4.toString()));
            var4.setLength(0);

            String var7;
            while((var7 = var6.readLine()) != null) {
                if (!var7.trim().toLowerCase().startsWith("charset") && !var7.trim().toLowerCase().startsWith("comment")) {
                    var4.append(var7).append(a);
                }
            }

            if (var4.indexOf("-----BEGIN PGP PUBLIC KEY BLOCK-----") == 0 && var4.indexOf("Version:") == -1) {
                var4 = var4.replace(0, 36, "-----BEGIN PGP PUBLIC KEY BLOCK-----" + a + "Version:");
            }

            for(var5 = 0; var4.indexOf("Version", var5) != -1; ++var5) {
                var2 = var4.indexOf(a, var4.indexOf("Version", var5) + 1);
                var5 = var4.indexOf("Version", var5);
                if (!var4.substring(var2 + a.length(), var2 + 2 * a.length()).equals(a)) {
                    var4.insert(var2 + a.length(), a);
                }
            }

            return new ByteArrayInputStream(var4.toString().getBytes("ASCII"));
        }
    }

    public static void replaceAll(StringBuffer var0, String var1, String var2) {
        for(int var3 = var0.indexOf(var1); var3 != -1; var3 = var0.indexOf(var1, var3)) {
            var0.replace(var3, var3 + var1.length(), var2);
            var3 += var2.length();
        }

    }

    protected CypherAlgorithm getSymmetricAlgorithm(int var1) {
        if (var1 == 1) {
            return CypherAlgorithm.IDEA;
        } else if (var1 == 9) {
            return CypherAlgorithm.AES_256;
        } else if (var1 == 8) {
            return CypherAlgorithm.AES_192;
        } else if (var1 == 7) {
            return CypherAlgorithm.AES_128;
        } else if (var1 == 2) {
            return CypherAlgorithm.TRIPLE_DES;
        } else if (var1 == 10) {
            return CypherAlgorithm.TWOFISH;
        } else if (var1 == 4) {
            return CypherAlgorithm.BLOWFISH;
        } else if (var1 == 11) {
            return CypherAlgorithm.CAMELLIA_128;
        } else if (var1 == 12) {
            return CypherAlgorithm.CAMELLIA_192;
        } else if (var1 == 13) {
            return CypherAlgorithm.CAMELLIA_256;
        } else if (var1 == 0) {
            return CypherAlgorithm.NONE;
        } else {
            throw new IllegalArgumentException("unknown symmetric encryption algorithm: " + var1);
        }
    }
}
