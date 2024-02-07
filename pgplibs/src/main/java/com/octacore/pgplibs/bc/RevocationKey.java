package com.octacore.pgplibs.bc;

import org.spongycastle.bcpg.SignatureSubpacket;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class RevocationKey extends SignatureSubpacket {
    public static final byte CLASS_DEFAULT = -128;
    public static final byte CLASS_SENSITIVE = 64;

    public RevocationKey(boolean var1, byte[] var2) {
        super(12, var1, false, var2);
    }

    public RevocationKey(boolean var1, byte var2, byte var3, byte[] var4) {
        boolean var10002 = var1;
        byte var10004 = var2;
        byte var10005 = var3;
        byte[] var6 = var4;
        var2 = var10005;
        byte var5 = var10004;
        (var4 = new byte[2 + var4.length])[0] = var5;
        var4[1] = var2;
        System.arraycopy(var6, 0, var4, 2, var6.length);
        super(12, var10002, false, var4);
    }

    public byte getSignatureClass() {
        return this.data[0];
    }

    public byte getAlgorithm() {
        return this.data[1];
    }

    public byte[] getFingerprint() {
        byte[] var1 = new byte[this.data.length - 2];
        System.arraycopy(this.data, 2, var1, 0, var1.length);
        return var1;
    }
}
