package com.octacore.pgplibs;

import com.octacore.pgplibs.bc.BCFactory;
import com.octacore.pgplibs.bc.BaseLib;
import com.octacore.pgplibs.bc.DirectByteArrayOutputStream;
import com.octacore.pgplibs.bc.IOUtil;
import com.octacore.pgplibs.exceptions.NoPrivateKeyFoundException;

import org.spongycastle.bcpg.ArmoredOutputStream;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPPublicKeyRing;
import org.spongycastle.openpgp.PGPSecretKey;
import org.spongycastle.openpgp.PGPSecretKeyRing;
import org.spongycastle.openpgp.PGPSignature;
import org.spongycastle.util.encoders.Hex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class KeyPairInformation extends BaseLib implements Serializable {
    protected BCFactory bcFactory = new BCFactory(false);
    private static final long serialVersionUID = -4750279241178352641L;
    private PGPPublicKeyRing a;
    private PGPSecretKeyRing b;
    private long c;
    private String d;
    private String e;
    private String f;
    private String[] g;
    private int h;
    private String i;
    private Date j;
    private int k;
    private int l;
    private boolean m;
    private boolean n;
    private boolean o;
    private long[] p = new long[0];
    private byte q = 120;
    private List r = new ArrayList();
    private List s = new ArrayList();
    protected String asciiVersionHeader = null;

    KeyPairInformation() {
        new ArmoredOutputStream(new ByteArrayOutputStream());
    }

    KeyPairInformation(String var1, byte var2) {
        this.asciiVersionHeader = var1;
        this.q = var2;
    }

    public KeyPairInformation(byte[] var1) throws IOException {
        try {
            List var5 = BaseLib.loadKeyStream(new ByteArrayInputStream(var1));

            for (int var2 = 0; var2 < var5.size(); ++var2) {
                Object var3;
                if ((var3 = var5.get(var2)) instanceof PGPPublicKeyRing) {
                    PGPPublicKeyRing var6 = (PGPPublicKeyRing) var3;
                    this.setPublicKeyRing(var6);
                } else if (var3 instanceof PGPSecretKeyRing) {
                    PGPSecretKeyRing var7 = (PGPSecretKeyRing) var3;
                    this.setPrivateKeyRing(var7);
                }
            }

        } catch (PGPException var4) {
            throw new IOException(var4.getMessage());
        }
    }

    public void setPublicKeyRing(PGPPublicKeyRing var1) {
        this.a = var1;
        Iterator var3 = var1.getPublicKeys();

        PGPPublicKey var10000;
        while (true) {
            if (var3.hasNext()) {
                PGPPublicKey var4;
                if (!(var4 = (PGPPublicKey) var3.next()).isMasterKey()) {
                    continue;
                }

                var10000 = var4;
                break;
            }

            var10000 = var1.getPublicKey();
            break;
        }

        PGPPublicKey var2 = var10000;
        this.c = var2.getKeyID();
        this.d = keyId2Hex(var2.getKeyID());
        this.e = keyIdToLongHex(var2.getKeyID());
        this.f = new String(Hex.encode(var2.getFingerprint()));
        ArrayList var7 = new ArrayList();
        Iterator var8 = var2.getUserIDs();

        while (var8.hasNext()) {
            var7.add((String) var8.next());
        }

        this.g = (String[]) var7.toArray(new String[var7.size()]);
        this.j = var2.getCreationTime();
        this.h = var2.getBitStrength();
        this.i = KeyStore.getKeyAlgorithm(var2.getAlgorithm());
        this.k = var2.getValidDays();
        this.l = var2.getVersion();
        this.m = var2.isRevoked();
        this.n = var2.isEncryptionKey();
        this.o = BaseLib.isForVerification(var2);
        var8 = var1.getPublicKeys();

        while (var8.hasNext()) {
            PGPPublicKey var5;
            if (!(var5 = (PGPPublicKey) var8.next()).isMasterKey()) {
                SubKey var6 = new SubKey(this, var5);
                this.s.add(var6);
            }
        }

        this.a();
    }

    private void a() {
        ArrayList var1 = new ArrayList();
        Iterator var2 = this.a.getPublicKeys();

        while (var2.hasNext()) {
            PGPPublicKey var3;
            Iterator var4 = (var3 = (PGPPublicKey) var2.next()).getSignaturesOfType(16);

            PGPSignature var5;
            while (var4.hasNext()) {
                if ((var5 = (PGPSignature) var4.next()).getKeyID() != var3.getKeyID()) {
                    var1.add(var5);
                }
            }

            var4 = var3.getSignaturesOfType(18);

            while (var4.hasNext()) {
                if ((var5 = (PGPSignature) var4.next()).getKeyID() != var3.getKeyID()) {
                    var1.add(var5);
                }
            }

            var4 = var3.getSignaturesOfType(19);

            while (var4.hasNext()) {
                if ((var5 = (PGPSignature) var4.next()).getKeyID() != var3.getKeyID()) {
                    var1.add(var5);
                }
            }
        }

        this.p = new long[var1.size()];

        for (int var6 = 0; var6 < var1.size(); ++var6) {
            this.p[var6] = ((PGPSignature) var1.get(var6)).getKeyID();
        }

    }

    public void setPrivateKeyRing(PGPSecretKeyRing var1) {
        this.b = var1;
        if (var1 != null) {
            Iterator var3 = var1.getSecretKeys();

            while (var3.hasNext()) {
                PGPSecretKey var2;
                if (!(var2 = (PGPSecretKey) var3.next()).isMasterKey()) {
                    SubKey var4 = new SubKey(this, var2.getPublicKey());
                    this.r.add(var4);
                }
            }

        }
    }

    public SubKey[] getPublicSubKeys() {
        return (SubKey[]) this.s.toArray(new SubKey[this.s.size()]);
    }

    public SubKey[] getPrivateSubKeys() {
        return (SubKey[]) this.r.toArray(new SubKey[this.r.size()]);
    }

    public boolean isExpired() {
        if (this.k <= 0) {
            return false;
        } else {
            Calendar var1;
            (var1 = Calendar.getInstance()).setTime(this.j);
            var1.add(5, this.k);
            return var1.getTime().before(new Date());
        }
    }

    public boolean isExpiredOnDate(Date var1) {
        if (this.k <= 0) {
            return false;
        } else {
            Calendar var2;
            (var2 = Calendar.getInstance()).setTime(this.j);
            var2.add(5, this.k);
            return var2.getTime().before(var1);
        }
    }

    public boolean isRevoked() {
        return this.m;
    }

    public boolean isEncryptionKey() {
        return this.n;
    }

    public boolean isSigningKey() {
        return this.o;
    }

    public PGPPublicKeyRing getRawPublicKeyRing() {
        return this.a;
    }

    public byte[] getEncoded() {
        DirectByteArrayOutputStream var1 = null;
        boolean var6 = false;

        byte[] var2;
        label57:
        {
            label56:
            {
                byte[] var3;
                try {
                    var6 = true;
                    if (!this.hasPrivateKey()) {
                        var2 = this.a.getEncoded();
                        var6 = false;
                        break label57;
                    }

                    var2 = this.a.getEncoded();
                    var3 = this.b.getEncoded();
                    (var1 = new DirectByteArrayOutputStream(var2.length + var3.length)).write(var2);
                    var1.write(var3);
                    var2 = var1.toByteArray();
                    var6 = false;
                    break label56;
                } catch (IOException var7) {
                    var3 = new byte[0];
                    var6 = false;
                } finally {
                    if (var6) {
                        IOUtil.closeStream(var1);
                    }
                }

                IOUtil.closeStream(var1);
                return var3;
            }

            IOUtil.closeStream(var1);
            return var2;
        }

        IOUtil.closeStream((OutputStream) null);
        return var2;
    }

    public byte[] getPublicKeyEncoded() {
        try {
            return this.a.getEncoded();
        } catch (IOException var1) {
            return new byte[0];
        }
    }

    public byte[] getPrivateKeyEncoded() {
        try {
            return this.hasPrivateKey() ? this.b.getEncoded() : null;
        } catch (IOException var1) {
            return new byte[0];
        }
    }

    public PGPSecretKeyRing getRawPrivateKeyRing() {
        return this.b;
    }

    public boolean hasPrivateKey() {
        return this.b != null;
    }

    public static String keyIdToHex(long var0) {
        String var2;
        return (var2 = Long.toHexString(var0).toUpperCase()).substring(var2.length() - 8);
    }

    public static String keyId2Hex(long var0) {
        String var2;
        return (var2 = Long.toHexString(var0).toUpperCase()).substring(var2.length() - 8);
    }

    public static String keyIdToLongHex(long var0) {
        return Long.toHexString(var0).toUpperCase();
    }

    public long getKeyID() {
        return this.c;
    }

    public String getKeyIDHex() {
        return this.d;
    }

    public String getKeyIDLongHex() {
        return this.e;
    }

    public String getFingerprint() {
        return this.f;
    }

    public String getUserID() {
        return this.g.length > 0 ? this.g[0] : null;
    }

    public String[] getUserIDs() {
        return this.g;
    }

    public int getKeySize() {
        return this.h;
    }

    public String getAlgorithm() {
        return this.i;
    }

    public Date getCreationTime() {
        return this.j;
    }

    public int getValidDays() {
        return this.k;
    }

    public int getVersion() {
        return this.l;
    }

    public void exportPublicKey(String var1, boolean var2) throws IOException {
        IOUtil.exportPublicKeyRing(this.getRawPublicKeyRing(), var1, var2, this.asciiVersionHeader);
    }

    public void exportPublicKey(OutputStream var1, boolean var2) throws IOException {
        IOUtil.exportPublicKeyRing(this.getRawPublicKeyRing(), var1, var2, this.asciiVersionHeader);
    }

    public void exportPrivateKey(String var1, boolean var2) throws NoPrivateKeyFoundException, IOException {
        if (this.getRawPrivateKeyRing() == null) {
            throw new NoPrivateKeyFoundException("No private key was loaded in this KeyPair object");
        } else {
            IOUtil.exportPrivateKey(this.getRawPrivateKeyRing(), var1, var2, this.asciiVersionHeader);
        }
    }

    public void exportKeyRing(String var1, boolean var2) throws IOException {
        FileOutputStream var3 = null;

        try {
            var3 = new FileOutputStream(var1);
            this.exportKeyRing((OutputStream) var3, var2);
        } finally {
            IOUtil.closeStream(var3);
        }

    }

    public void exportKeyRing(OutputStream var1, boolean var2) throws IOException {
        Object var3 = var1;
        if (var2) {
            var3 = new ArmoredOutputStream(var1);
        }

        PGPSecretKeyRing var4;
        if ((var4 = this.getRawPrivateKeyRing()) != null) {
            var4.encode((OutputStream) var3);
            if (var2) {
                IOUtil.closeStream((OutputStream) var3);
            }
        }

        if (var2) {
            var3 = new ArmoredOutputStream(var1);
        }

        this.getRawPublicKeyRing().encode((OutputStream) var3);
        if (var2) {
            IOUtil.closeStream((OutputStream) var3);
        }

    }

    public void exportKeyRing(String var1) throws IOException {
        this.exportKeyRing(var1, true);
    }

    public boolean checkPassword(String var1) throws NoPrivateKeyFoundException {
        if (this.b == null) {
            throw new NoPrivateKeyFoundException("There is no private key in this key pair.");
        } else {
            if (var1 == null) {
                var1 = "";
            }

            try {
                this.b.getSecretKey().extractPrivateKey(this.bcFactory.CreatePBESecretKeyDecryptor(var1 == null ? new char[0] : var1.toCharArray()));
                return true;
            } catch (org.spongycastle.openpgp.PGPException var2) {
                if (var2.getMessage().toLowerCase().startsWith("checksum mismatch at 0 of 2")) {
                    return false;
                } else {
                    throw new NoPrivateKeyFoundException(var2.getMessage(), var2);
                }
            }
        }
    }

    public byte getTrust() {
        if (this.hasPrivateKey()) {
            return this.q;
        } else {
            byte[] var1;
            return (var1 = this.a.getPublicKey().getTrustData()) != null ? var1[0] : 0;
        }
    }

    public long[] getSignedWithKeyIds() {
        return this.p;
    }

    public class SubKey implements Serializable {
        private static final long serialVersionUID = -9122478708446314118L;
        private boolean a;
        private boolean b;
        private boolean c;
        private long d;
        private String e;
        private String f;
        private String[] g;
        private int h;
        private String i;
        private Date j;
        private int k;
        private int l;

        public SubKey(KeyPairInformation var1, PGPPublicKey var2) {
            this.d = var2.getKeyID();
            this.e = KeyPairInformation.keyId2Hex(var2.getKeyID());
            KeyPairInformation.keyIdToLongHex(var2.getKeyID());
            this.f = new String(Hex.encode(var2.getFingerprint()));
            ArrayList var4 = new ArrayList();
            Iterator var3 = var2.getUserIDs();

            while (var3.hasNext()) {
                var4.add((String) var3.next());
            }

            this.g = (String[]) var4.toArray(new String[var4.size()]);
            this.j = var2.getCreationTime();
            this.h = var2.getBitStrength();
            this.i = KeyStore.getKeyAlgorithm(var2.getAlgorithm());
            this.k = var2.getValidDays();
            this.l = var2.getVersion();
            this.c = var2.isRevoked();
            this.a = var2.isEncryptionKey();
            this.b = KeyPairInformation.isForVerification(var2);
        }

        public boolean isEncryptionKey() {
            return this.a;
        }

        public boolean isSigningKey() {
            return this.b;
        }

        public boolean isExpired() {
            if (this.k <= 0) {
                return false;
            } else {
                Calendar var1;
                (var1 = Calendar.getInstance()).setTime(this.j);
                var1.add(5, this.k);
                return var1.getTime().before(new Date());
            }
        }

        public boolean isExpiredOnDate(Date var1) {
            if (this.k <= 0) {
                return false;
            } else {
                Calendar var2;
                (var2 = Calendar.getInstance()).setTime(this.j);
                var2.add(5, this.k);
                return var2.getTime().before(var1);
            }
        }

        public boolean isRevoked() {
            return this.c;
        }

        public long getKeyID() {
            return this.d;
        }

        public String getKeyIDHex() {
            return this.e;
        }

        public String getFingerprint() {
            return this.f;
        }

        public String[] getUserIDs() {
            return this.g;
        }

        public int getKeySize() {
            return this.h;
        }

        public String getAlgorithm() {
            return this.i;
        }

        public Date getCreationTime() {
            return this.j;
        }

        public int getValidDays() {
            return this.k;
        }

        public int getVersion() {
            return this.l;
        }
    }
}
