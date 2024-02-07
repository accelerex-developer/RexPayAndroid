package com.octacore.pgplibs;

import com.octacore.pgplibs.bc.BCFactory;
import com.octacore.pgplibs.bc.BaseLib;
import com.octacore.pgplibs.bc.IOUtil;
import com.octacore.pgplibs.bc.PGPObjectFactory2;
import com.octacore.pgplibs.bc.PGPSignatureSubpacketGeneratorExtended;
import com.octacore.pgplibs.bc.ReflectionUtils;
import com.octacore.pgplibs.bc.RevocationKey;

import org.spongycastle.bcpg.ArmoredInputStream;
import org.spongycastle.bcpg.ArmoredOutputStream;
import org.spongycastle.bcpg.SignatureSubpacket;
import org.spongycastle.bcpg.sig.IssuerKeyID;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPPublicKeyRing;
import org.spongycastle.openpgp.PGPSecretKeyRing;
import org.spongycastle.openpgp.PGPSignature;
import org.spongycastle.openpgp.PGPSignatureGenerator;
import org.spongycastle.openpgp.PGPSignatureList;
import org.spongycastle.openpgp.PGPUtil;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.encoders.Hex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class RevocationLib extends BaseLib {
    private BCFactory a = new BCFactory(true);
    public static final byte REASON_NO_REASON = 0;
    public static final byte REASON_KEY_SUPERSEDED = 1;
    public static final byte REASON_KEY_COMPROMISED = 2;
    public static final byte REASON_KEY_NO_LONGER_USED = 3;
    public static final byte REASON_USER_NO_LONGER_USED = 32;
    private Logger b = Logger.getLogger(RevocationLib.class.getName());
    private String c = null;

    public RevocationLib() {
        ArmoredOutputStream var1 = new ArmoredOutputStream(new ByteArrayOutputStream());
        this.c = (String) ReflectionUtils.getPrivateFieldvalue(var1, "version");
    }

    public String getAsciiVersionHeader() {
        return "Version: " + this.c;
    }

    public void setAsciiVersionHeader(String var1) {
        this.c = var1;
    }

    public String createRevocationCertificateText(String var1, String var2, byte var3, String var4) throws PGPException, IOException {
        PGPSecretKeyRing var5 = c(var1);
        return this.a(var5, var2, var3, var4);
    }

    public String createRevocationCertificateText(KeyStore var1, long var2, String var4, byte var5, String var6) throws PGPException, IOException {
        PGPSecretKeyRing var7 = var1.findSecretKeyRing(var2);
        return this.a(var7, var4, var5, var6);
    }

    public String createRevocationCertificateText(KeyStore var1, String var2, String var3, byte var4, String var5) throws PGPException, IOException {
        PGPSecretKeyRing var6 = var1.findSecretKeyRing(var2);
        return this.a(var6, var3, var4, var5);
    }

    public void createRevocationCertificateInFile(String var1, String var2, byte var3, String var4, String var5) throws PGPException, IOException {
        var1 = this.createRevocationCertificateText(var1, var2, var3, var4);
        FileOutputStream var8 = null;

        try {
            (var8 = new FileOutputStream(var5)).write(var1.getBytes("US-ASCII"));
        } finally {
            IOUtil.closeStream(var8);
        }

    }

    public void createRevocationCertificateInFile(KeyStore var1, long var2, String var4, byte var5, String var6, String var7) throws PGPException, IOException {
        String var10 = this.createRevocationCertificateText(var1, var2, var4, var5, var6);
        FileOutputStream var11 = null;

        try {
            (var11 = new FileOutputStream(var7)).write(var10.getBytes("US-ASCII"));
        } finally {
            IOUtil.closeStream(var11);
        }

    }

    public void createRevocationCertificateInFile(KeyStore var1, String var2, String var3, byte var4, String var5, String var6) throws PGPException, IOException {
        String var9 = this.createRevocationCertificateText(var1, var2, var3, var4, var5);
        FileOutputStream var10 = null;

        try {
            (var10 = new FileOutputStream(var6)).write(var9.getBytes("US-ASCII"));
        } finally {
            IOUtil.closeStream(var10);
        }

    }

    public void assignDesignatedRevoker(String var1, String var2, String var3, String var4) throws PGPException, IOException {
        PGPPublicKeyRing var5 = a(var1);
        PGPSecretKeyRing var7 = c(var2);
        PGPPublicKeyRing var8 = a(var4);
        boolean var6 = b(var1);
        IOUtil.exportPublicKeyRing(this.a(var5, var7, var3, var8), var1, var6, this.c);
    }

    public void assignDesignatedRevoker(KeyStore var1, long var2, String var4, long var5) throws PGPException, IOException {
        PGPPublicKeyRing var7 = var1.a(var2);
        PGPSecretKeyRing var8 = var1.findSecretKeyRing(var2);
        PGPPublicKeyRing var3 = var1.a(var5);
        var7 = this.a(var7, var8, var4, var3);
        var1.replacePublicKeyRing(var7);
    }

    public void assignDesignatedRevoker(KeyStore var1, String var2, String var3, String var4) throws PGPException, IOException {
        PGPPublicKeyRing var5 = var1.a(var2);
        PGPSecretKeyRing var6 = var1.findSecretKeyRing(var2);
        PGPPublicKeyRing var7 = var1.a(var4);
        var5 = this.a(var5, var6, var3, var7);
        var1.replacePublicKeyRing(var5);
    }

    public void revokeKeyWithRevocationCertificateText(String var1, String var2) throws IOException, PGPException {
        PGPPublicKeyRing var3 = a(var1);
        boolean var4 = b(var1);
        ByteArrayInputStream var5 = null;

        try {
            var5 = new ByteArrayInputStream(var2.getBytes("US-ASCII"));
            var3 = a((PGPPublicKeyRing) var3, var5);
        } finally {
            IOUtil.closeStream(var5);
        }

        IOUtil.exportPublicKeyRing(var3, var1, var4, this.c);
    }

    public void revokeKeyWithRevocationCertificateText(KeyStore var1, String var2) throws PGPException {
        ByteArrayInputStream var3 = null;

        PGPPublicKeyRing var11;
        try {
            long var5 = a((InputStream) (var3 = new ByteArrayInputStream(var2.getBytes("US-ASCII"))));
            var11 = a((PGPPublicKeyRing) var1.a(var5), var3);
        } catch (IOException var9) {
            throw new PGPException(var9.getMessage(), var9);
        } finally {
            IOUtil.closeStream(var3);
        }

        if (var11 != null) {
            var1.replacePublicKeyRing(var11);
        }

    }

    public void revokeKeyWithRevocationCertificateFile(String var1, String var2) throws IOException, PGPException {
        PGPPublicKeyRing var3 = a(var1);
        boolean var4 = b(var1);
        FileInputStream var5 = null;

        try {
            var5 = new FileInputStream(var2);
            var3 = a((PGPPublicKeyRing) var3, var5);
        } finally {
            IOUtil.closeStream(var5);
        }

        IOUtil.exportPublicKeyRing(var3, var1, var4, this.c);
    }

    public void revokeKeyWithRevocationCertificateFile(KeyStore var1, String var2) throws IOException, PGPException {
        FileInputStream var3 = null;

        PGPPublicKeyRing var6;
        try {
            var3 = new FileInputStream(var2);
            var6 = a((KeyStore) var1, var3);
        } finally {
            IOUtil.closeStream(var3);
        }

        if (var6 != null) {
            var1.replacePublicKeyRing(var6);
        }

    }

    public void revokeKey(KeyStore var1, long var2, String var4, byte var5, String var6) throws PGPException {
        PGPPublicKeyRing var7 = var1.a(var2);
        PGPSecretKeyRing var8 = var1.findSecretKeyRing(var2);

        try {
            var7 = this.a(var7, var7.getPublicKey(var2), var8, var4, var5, var6);
        } catch (org.spongycastle.openpgp.PGPException var9) {
            throw IOUtil.newPGPException(var9);
        }

        var1.replacePublicKeyRing(var7);
    }

    public void revokeKey(KeyStore var1, String var2, String var3, byte var4, String var5) throws PGPException {
        PGPPublicKeyRing var6 = var1.a(var2);
        PGPSecretKeyRing var7 = var1.findSecretKeyRing(var2);
        var6 = this.a(var6, var6.getPublicKey(), var7, var3, var4, var5);
        var1.replacePublicKeyRing(var6);
    }

    public void revokeKey(String var1, String var2, String var3, byte var4, String var5) throws IOException, PGPException {
        PGPPublicKeyRing var6 = a(var1);
        PGPSecretKeyRing var8 = c(var2);
        boolean var7 = b(var1);
        IOUtil.exportPublicKeyRing(this.a(var6, var6.getPublicKey(), var8, var3, var4, var5), var1, var7, this.c);
    }

    public void revokeUserIdSignature(String var1, String var2, String var3, String var4, byte var5, String var6) throws IOException, PGPException {
        PGPPublicKeyRing var9 = a(var1);
        PGPSecretKeyRing var8 = c(var2);
        boolean var7 = b(var1);
        IOUtil.exportPublicKeyRing(this.a(var9, var8, var3, var5, var6), var1, var7, this.c);
    }

    public void revokeUserIdSignature(KeyStore var1, long var2, String var4, String var5, byte var6, String var7) throws PGPException {
        PGPPublicKeyRing var9 = var1.a(var2);
        PGPSecretKeyRing var8 = var1.findSecretKeyRing(var2);
        var9 = this.a(var9, var8, var5, var6, var7);
        var1.replacePublicKeyRing(var9);
    }

    public void revokeUserIdSignature(KeyStore var1, String var2, String var3, byte var4, String var5) throws PGPException {
        PGPPublicKeyRing var6 = var1.a(var2);
        PGPSecretKeyRing var7 = var1.findSecretKeyRing(var2);
        var6 = this.a(var6, var7, var3, var4, var5);
        var1.replacePublicKeyRing(var6);
    }

    public void revokeKeyWithDesignatedRevoker(String var1, String var2, String var3, byte var4, String var5) throws PGPException, IOException {
        PGPPublicKeyRing var6 = a(var1);
        boolean var7 = b(var1);
        PGPSecretKeyRing var8 = c(var2);
        IOUtil.exportPublicKeyRing(this.b(var6, var8, var3, var4, var5), var1, var7, this.c);
    }

    public void revokeKeyWithDesignatedRevoker(KeyStore var1, long var2, long var4, String var6, byte var7, String var8) throws PGPException {
        PGPPublicKeyRing var9 = var1.a(var2);
        PGPSecretKeyRing var3 = var1.findSecretKeyRing(var4);
        var9 = this.b(var9, var3, var6, var7, var8);
        var1.replacePublicKeyRing(var9);
    }

    public void revokeKeyWithDesignatedRevoker(KeyStore var1, String var2, String var3, String var4, byte var5, String var6) throws PGPException {
        PGPPublicKeyRing var7 = var1.a(var2);
        PGPSecretKeyRing var8 = var1.findSecretKeyRing(var3);
        var7 = this.b(var7, var8, var4, var5, var6);
        var1.replacePublicKeyRing(var7);
    }

    private static PGPPublicKeyRing a(String var0) throws PGPException, IOException {
        FileInputStream var1 = null;

        PGPPublicKeyRing var4;
        try {
            var4 = (PGPPublicKeyRing) PGPLibs.createPGPPublicKeyRingCollection(var1 = new FileInputStream(var0)).getKeyRings().next();
        } finally {
            IOUtil.closeStream(var1);
        }

        return var4;
    }

    private static boolean b(String var0) throws IOException {
        FileInputStream var1 = null;

        boolean var4;
        try {
            var4 = PGPUtil.getDecoderStream(var1 = new FileInputStream(var0)) instanceof ArmoredInputStream;
        } finally {
            IOUtil.closeStream(var1);
        }

        return var4;
    }

    private static PGPSecretKeyRing c(String var0) throws IOException, PGPException {
        FileInputStream var1 = null;

        PGPSecretKeyRing var4;
        try {
            var4 = (PGPSecretKeyRing) PGPLibs.createPGPSecretKeyRingCollection(var1 = new FileInputStream(var0)).getKeyRings().next();
        } finally {
            IOUtil.closeStream(var1);
        }

        return var4;
    }

    private PGPPublicKeyRing a(PGPPublicKeyRing var1, PGPSecretKeyRing var2, String var3, PGPPublicKeyRing var4) throws PGPException {
        PGPSignatureSubpacketGeneratorExtended var5;
        (var5 = new PGPSignatureSubpacketGeneratorExtended()).setSignatureCreationTime(false, new Date());
        var5.setRevocable(false, false);
        var5.setRevocationKey(false, (byte) var4.getPublicKey().getAlgorithm(), var4.getPublicKey().getFingerprint());
        PGPSignatureSubpacketGeneratorExtended var9;
        (var9 = new PGPSignatureSubpacketGeneratorExtended()).setIssuerKeyID(false, var1.getPublicKey().getKeyID());

        PGPSignature var8;
        try {
            PGPSignatureGenerator var6;
            (var6 = new PGPSignatureGenerator(this.a.CreatePGPContentSignerBuilder(var2.getPublicKey().getAlgorithm(), 2))).init(31, BaseLib.extractPrivateKey(var2.getSecretKey(), var3));
            var6.setHashedSubpackets(var5.generate());
            var6.setUnhashedSubpackets(var9.generate());
            var8 = var6.generateCertification(var1.getPublicKey());
        } catch (org.spongycastle.openpgp.PGPException var7) {
            throw IOUtil.newPGPException(var7);
        }

        PGPPublicKey var10 = PGPPublicKey.addCertification(var1.getPublicKey(), var8);
        return PGPPublicKeyRing.insertPublicKey(PGPPublicKeyRing.removePublicKey(var1, var1.getPublicKey()), var10);
    }

    private PGPPublicKeyRing a(PGPPublicKeyRing var1, PGPPublicKey var2, PGPSecretKeyRing var3, String var4, byte var5, String var6) throws PGPException {
        PGPSignatureSubpacketGeneratorExtended var7;
        (var7 = new PGPSignatureSubpacketGeneratorExtended()).setSignatureCreationTime(false, new Date());
        var7.setRevocationReason(false, var5, var6);
        PGPSignatureSubpacketGeneratorExtended var10;
        (var10 = new PGPSignatureSubpacketGeneratorExtended()).setIssuerKeyID(false, var3.getSecretKey().getKeyID());

        PGPSignature var9;
        try {
            PGPSignatureGenerator var11 = new PGPSignatureGenerator(this.a.CreatePGPContentSignerBuilder(var2.getAlgorithm(), 2));
            if (var2.isMasterKey()) {
                var11.init(32, BaseLib.extractPrivateKey(var3.getSecretKey(), var4));
            } else {
                var11.init(40, BaseLib.extractPrivateKey(var3.getSecretKey(), var4));
            }

            var11.setHashedSubpackets(var7.generate());
            var11.setUnhashedSubpackets(var10.generate());
            var9 = var11.generateCertification(var2);
        } catch (org.spongycastle.openpgp.PGPException var8) {
            throw IOUtil.newPGPException(var8);
        }

        var2 = PGPPublicKey.addCertification(var2, var9);
        return PGPPublicKeyRing.insertPublicKey(PGPPublicKeyRing.removePublicKey(var1, var2), var2);
    }

    private String a(PGPSecretKeyRing var1, String var2, byte var3, String var4) throws PGPException, IOException {
        PGPSignatureSubpacketGeneratorExtended var5;
        (var5 = new PGPSignatureSubpacketGeneratorExtended()).setSignatureCreationTime(false, new Date());
        var5.setRevocationReason(false, var3, var4);
        PGPSignatureSubpacketGeneratorExtended var12;
        (var12 = new PGPSignatureSubpacketGeneratorExtended()).setIssuerKeyID(false, var1.getPublicKey().getKeyID());

        PGPSignature var11;
        try {
            PGPSignatureGenerator var14;
            (var14 = new PGPSignatureGenerator(this.a.CreatePGPContentSignerBuilder(var1.getPublicKey().getAlgorithm(), 2))).init(32, BaseLib.extractPrivateKey(var1.getSecretKey(), var2));
            var14.setHashedSubpackets(var5.generate());
            var14.setUnhashedSubpackets(var12.generate());
            var11 = var14.generateCertification(var1.getPublicKey());
            var4 = KeyPairInformation.keyId2Hex(var1.getPublicKey().getKeyID());
            String var13 = "Created revocation certificate for key {0}";
            if (this.b.isLoggable(Level.FINE)) {
                this.b.fine(MessageFormat.format(var13, var4));
            }
        } catch (org.spongycastle.openpgp.PGPException var9) {
            throw IOUtil.newPGPException(var9);
        }

        ByteArrayOutputStream var15 = null;
        ArmoredOutputStream var10 = null;

        try {
            var15 = new ByteArrayOutputStream();
            var10 = new ArmoredOutputStream(var15);
            var11.encode(var10);
        } finally {
            IOUtil.closeStream(var10);
            IOUtil.closeStream(var15);
        }

        return var15.toString("US-ASCII").replaceFirst("-----BEGIN PGP SIGNATURE-----", "-----BEGIN PGP PUBLIC KEY BLOCK-----").replaceFirst("-----END PGP SIGNATURE-----", "-----END PGP PUBLIC KEY BLOCK-----");
    }

    private static PGPPublicKeyRing a(PGPPublicKeyRing var0, InputStream var1) throws IOException, PGPException {
        var1 = PGPUtil.getDecoderStream(var1);
        PGPObjectFactory2 var7 = new PGPObjectFactory2(var1);
        PGPSignature var2 = null;
        boolean var3 = false;

        Object var4;
        while ((var4 = var7.nextObject()) != null) {
            if (var4 instanceof PGPSignatureList) {
                PGPSignatureList var8 = (PGPSignatureList) var4;

                for (int var5 = 0; var5 < var8.size(); ++var5) {
                    SignatureSubpacket var6;
                    if ((var2 = var8.get(var5)).getSignatureType() == 32) {
                        if ((var6 = var2.getUnhashedSubPackets().getSubpacket(16)) != null && (new IssuerKeyID(var6.isCritical(), false, var6.getData())).getKeyID() == var0.getPublicKey().getKeyID()) {
                            var3 = true;
                        }
                    } else if (var2.getSignatureType() == 40 && (var6 = var2.getUnhashedSubPackets().getSubpacket(16)) != null && (new IssuerKeyID(var6.isCritical(), false, var6.getData())).getKeyID() == var0.getPublicKey().getKeyID()) {
                        var3 = true;
                    }

                    if (var3) {
                        break;
                    }
                }
            }

            if (var3) {
                break;
            }
        }

        if (!var3) {
            throw new PGPException("The supplied revocation certificate has no issuer key id subpacket for key with id: " + Long.toHexString(var0.getPublicKey().getKeyID()));
        } else {
            PGPPublicKey var9 = PGPPublicKey.addCertification(var0.getPublicKey(), var2);
            return PGPPublicKeyRing.insertPublicKey(PGPPublicKeyRing.removePublicKey(var0, var0.getPublicKey()), var9);
        }
    }

    private static PGPPublicKeyRing a(KeyStore var0, InputStream var1) throws IOException, PGPException {
        var1 = PGPUtil.getDecoderStream(var1);
        PGPObjectFactory2 var7 = new PGPObjectFactory2(var1);
        PGPSignature var2 = null;
        PGPPublicKeyRing var3 = null;

        Object var4;
        while ((var4 = var7.nextObject()) != null) {
            if (var4 instanceof PGPSignatureList) {
                PGPSignatureList var8 = (PGPSignatureList) var4;

                for (int var5 = 0; var5 < var8.size(); ++var5) {
                    SignatureSubpacket var6;
                    IssuerKeyID var10;
                    if ((var2 = var8.get(var5)).getSignatureType() == 32) {
                        if ((var6 = var2.getUnhashedSubPackets().getSubpacket(16)) != null) {
                            var10 = new IssuerKeyID(var6.isCritical(), false, var6.getData());
                            if (var0.containsKey(var10.getKeyID())) {
                                var3 = var0.a(var10.getKeyID());
                            }
                        }
                    } else if (var2.getSignatureType() == 40 && (var6 = var2.getUnhashedSubPackets().getSubpacket(16)) != null) {
                        var10 = new IssuerKeyID(var6.isCritical(), false, var6.getData());
                        if (var0.containsKey(var10.getKeyID())) {
                            var3 = var0.a(var10.getKeyID());
                        }
                    }

                    if (var3 != null) {
                        break;
                    }
                }
            }

            if (var3 != null) {
                break;
            }
        }

        if (var3 == null) {
            throw new PGPException("No key was found in the KeyStore matching the revocation certificate Issuer ID");
        } else {
            PGPPublicKey var9 = PGPPublicKey.addCertification(var3.getPublicKey(), var2);
            return PGPPublicKeyRing.insertPublicKey(PGPPublicKeyRing.removePublicKey(var3, var3.getPublicKey()), var9);
        }
    }

    private static long a(InputStream var0) throws IOException, PGPException {
        var0 = PGPUtil.getDecoderStream(var0);
        PGPObjectFactory2 var4 = new PGPObjectFactory2(var0);

        while (true) {
            Object var1;
            do {
                if ((var1 = var4.nextObject()) == null) {
                    return -1L;
                }
            } while (!(var1 instanceof PGPSignatureList));

            PGPSignatureList var2 = (PGPSignatureList) var1;

            for (int var3 = 0; var3 < var2.size(); ++var3) {
                PGPSignature var5;
                SignatureSubpacket var6;
                if ((var5 = var2.get(var3)).getSignatureType() == 32) {
                    if ((var6 = var5.getUnhashedSubPackets().getSubpacket(16)) != null) {
                        return (new IssuerKeyID(var6.isCritical(), false, var6.getData())).getKeyID();
                    }
                } else if (var5.getSignatureType() == 40 && (var6 = var5.getUnhashedSubPackets().getSubpacket(16)) != null) {
                    return (new IssuerKeyID(var6.isCritical(), false, var6.getData())).getKeyID();
                }
            }
        }
    }

    private PGPPublicKeyRing a(PGPPublicKeyRing var1, PGPSecretKeyRing var2, String var3, byte var4, String var5) throws PGPException {
        PGPSignatureSubpacketGeneratorExtended var6;
        (var6 = new PGPSignatureSubpacketGeneratorExtended()).setSignatureCreationTime(false, new Date());
        var6.setRevocationReason(false, var4, var5);
        PGPSignatureSubpacketGeneratorExtended var9;
        (var9 = new PGPSignatureSubpacketGeneratorExtended()).setIssuerKeyID(false, var1.getPublicKey().getKeyID());

        PGPSignature var8;
        try {
            PGPSignatureGenerator var10;
            (var10 = new PGPSignatureGenerator(this.a.CreatePGPContentSignerBuilder(var1.getPublicKey().getAlgorithm(), 2))).init(48, BaseLib.extractPrivateKey(var2.getSecretKey(), var3.toCharArray()));
            var10.setHashedSubpackets(var6.generate());
            var10.setUnhashedSubpackets(var9.generate());
            var8 = var10.generateCertification((String) var1.getPublicKey().getUserIDs().next(), var1.getPublicKey());
        } catch (org.spongycastle.openpgp.PGPException var7) {
            throw IOUtil.newPGPException(var7);
        }

        PGPPublicKey var11 = PGPPublicKey.addCertification(var1.getPublicKey(), (String) var1.getPublicKey().getUserIDs().next(), var8);
        return PGPPublicKeyRing.insertPublicKey(PGPPublicKeyRing.removePublicKey(var1, var11), var11);
    }

    private PGPPublicKeyRing b(PGPPublicKeyRing var1, PGPSecretKeyRing var2, String var3, byte var4, String var5) throws PGPException {
        boolean var6 = false;
        Iterator var7 = var1.getPublicKey().getSignaturesOfType(31);

        while (var7.hasNext()) {
            PGPSignature var8;
            SignatureSubpacket var9;
            if ((var9 = (var8 = (PGPSignature) var7.next()).getHashedSubPackets().getSubpacket(12)) != null && Arrays.areEqual((new RevocationKey(var9.isCritical(), var9.getData())).getFingerprint(), var2.getPublicKey().getFingerprint())) {
                var6 = true;
            }

            SignatureSubpacket var12;
            if ((var12 = var8.getUnhashedSubPackets().getSubpacket(16)) != null && (new IssuerKeyID(var12.isCritical(), false, var12.getData())).getKeyID() == var1.getPublicKey().getKeyID()) {
                var6 = var6;
            }

            if (var6) {
                break;
            }
        }

        if (!var6) {
            throw new PGPException("Target key has no designated revoker signature with fingerprint: " + new String(Hex.encode(var2.getPublicKey().getFingerprint())));
        } else {
            PGPSignatureSubpacketGeneratorExtended var13;
            (var13 = new PGPSignatureSubpacketGeneratorExtended()).setSignatureCreationTime(false, new Date());
            var13.setRevocationReason(false, var4, var5);
            PGPSignatureSubpacketGeneratorExtended var14;
            (var14 = new PGPSignatureSubpacketGeneratorExtended()).setIssuerKeyID(false, var2.getPublicKey().getKeyID());

            PGPSignature var15;
            try {
                PGPSignatureGenerator var10;
                (var10 = new PGPSignatureGenerator(this.a.CreatePGPContentSignerBuilder(var2.getPublicKey().getAlgorithm(), 2))).init(32, BaseLib.extractPrivateKey(var2.getSecretKey(), var3));
                var10.setHashedSubpackets(var13.generate());
                var10.setUnhashedSubpackets(var14.generate());
                var15 = var10.generateCertification(var1.getPublicKey());
            } catch (org.spongycastle.openpgp.PGPException var11) {
                throw IOUtil.newPGPException(var11);
            }

            PGPPublicKey var16 = PGPPublicKey.addCertification(var1.getPublicKey(), var15);
            return PGPPublicKeyRing.insertPublicKey(PGPPublicKeyRing.removePublicKey(var1, var1.getPublicKey()), var16);
        }
    }
}
