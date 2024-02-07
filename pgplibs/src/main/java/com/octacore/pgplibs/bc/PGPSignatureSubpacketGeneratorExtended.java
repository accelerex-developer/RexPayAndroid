package com.octacore.pgplibs.bc;

import org.spongycastle.bcpg.SignatureSubpacket;
import org.spongycastle.bcpg.sig.EmbeddedSignature;
import org.spongycastle.bcpg.sig.Exportable;
import org.spongycastle.bcpg.sig.IssuerKeyID;
import org.spongycastle.bcpg.sig.KeyExpirationTime;
import org.spongycastle.bcpg.sig.KeyFlags;
import org.spongycastle.bcpg.sig.NotationData;
import org.spongycastle.bcpg.sig.PreferredAlgorithms;
import org.spongycastle.bcpg.sig.PrimaryUserID;
import org.spongycastle.bcpg.sig.Revocable;
import org.spongycastle.bcpg.sig.SignatureCreationTime;
import org.spongycastle.bcpg.sig.SignatureExpirationTime;
import org.spongycastle.bcpg.sig.SignerUserID;
import org.spongycastle.bcpg.sig.TrustSignature;
import org.spongycastle.openpgp.PGPSignature;
import org.spongycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.spongycastle.openpgp.PGPSignatureSubpacketVector;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class PGPSignatureSubpacketGeneratorExtended extends PGPSignatureSubpacketGenerator {
    private List a = new ArrayList();

    public PGPSignatureSubpacketGeneratorExtended() {
    }

    public void setRevocable(boolean var1, boolean var2) {
        this.a.add(new Revocable(var1, var2));
    }

    public void setExportable(boolean var1, boolean var2) {
        this.a.add(new Exportable(var1, var2));
    }

    public void setTrust(boolean var1, int var2, int var3) {
        this.a.add(new TrustSignature(var1, var2, var3));
    }

    public void setKeyExpirationTime(boolean var1, long var2) {
        this.a.add(new KeyExpirationTime(var1, var2));
    }

    public void setSignatureExpirationTime(boolean var1, long var2) {
        this.a.add(new SignatureExpirationTime(var1, var2));
    }

    public void setSignatureCreationTime(boolean var1, Date var2) {
        this.a.add(new SignatureCreationTime(var1, var2));
    }

    public void setPreferredHashAlgorithms(boolean var1, int[] var2) {
        this.a.add(new PreferredAlgorithms(21, var1, var2));
    }

    public void setPreferredSymmetricAlgorithms(boolean var1, int[] var2) {
        this.a.add(new PreferredAlgorithms(11, var1, var2));
    }

    public void setPreferredCompressionAlgorithms(boolean var1, int[] var2) {
        this.a.add(new PreferredAlgorithms(22, var1, var2));
    }

    public void setKeyFlags(boolean var1, int var2) {
        this.a.add(new KeyFlags(var1, var2));
    }

    public void setSignerUserID(boolean var1, String var2) {
        if (var2 == null) {
            throw new IllegalArgumentException("attempt to set null SignerUserID");
        } else {
            this.a.add(new SignerUserID(var1, var2));
        }
    }

    public void setEmbeddedSignature(boolean var1, PGPSignature var2) throws IOException {
        byte[] var3;
        byte[] var4;
        if ((var4 = var2.getEncoded()).length - 1 > 256) {
            var3 = new byte[var4.length - 3];
        } else {
            var3 = new byte[var4.length - 2];
        }

        System.arraycopy(var4, var4.length - var3.length, var3, 0, var3.length);
        this.a.add(new EmbeddedSignature(var1, false, var3));
    }

    public void setPrimaryUserID(boolean var1, boolean var2) {
        this.a.add(new PrimaryUserID(var1, var2));
    }

    public void setNotationData(boolean var1, boolean var2, String var3, String var4) {
        this.a.add(new NotationData(var1, var2, var3, var4));
    }

    public PGPSignatureSubpacketVector generate() {
        SignatureSubpacket[] var1 = (SignatureSubpacket[]) this.a.toArray(new SignatureSubpacket[this.a.size()]);
        Object var2 = null;

        try {
            Class var3 = PGPSignatureSubpacketVector.class;
            Constructor var4 = null;
            Constructor[] var10 = var3.getDeclaredConstructors();

            for (int var5 = 0; var5 < var10.length; ++var5) {
                Class[] var6;
                if ((var6 = var10[var5].getParameterTypes()).length == 1 && var6[0].isArray() && var6[0].getComponentType().equals(SignatureSubpacket.class)) {
                    var4 = var10[var5];
                    break;
                }
            }

            var4.setAccessible(true);
            var2 = var4.newInstance(var1);
        } catch (InvocationTargetException var7) {
        } catch (IllegalAccessException var8) {
        } catch (InstantiationException var9) {
        }

        return (PGPSignatureSubpacketVector) var2;
    }

    public void setRevocationReason(boolean var1, byte var2, String var3) {
        this.a.add(new RevocationReason(var1, var2, var3));
    }

    public void setRevocationKey(boolean var1, byte var2, byte[] var3) {
        this.a.add(new RevocationKey(var1, (byte) -128, var2, var3));
    }

    public void setIssuerKeyID(boolean var1, long var2) {
        this.a.add(new IssuerKeyID(var1, var2));
    }
}
