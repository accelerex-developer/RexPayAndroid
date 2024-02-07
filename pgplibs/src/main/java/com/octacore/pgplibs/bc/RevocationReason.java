package com.octacore.pgplibs.bc;

import org.spongycastle.bcpg.SignatureSubpacket;
import org.spongycastle.util.Strings;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class RevocationReason extends SignatureSubpacket {
    public static final byte REASON_NO_REASON = 0;
    public static final byte REASON_KEY_SUPERSEDED = 1;
    public static final byte REASON_KEY_COMPROMISED = 2;
    public static final byte REASON_KEY_NO_LONGER_USED = 3;
    public static final byte REASON_USER_NO_LONGER_USED = 32;

    public RevocationReason(boolean var1, byte[] var2) {
        super(29, var1, false, var2);
    }

    public RevocationReason(boolean var1, byte var2, String var3) {
        boolean var10002 = var1;
        byte var4 = var2;
        byte[] var5 = Strings.toUTF8ByteArray(var3);
        byte[] var6;
        (var6 = new byte[1 + var5.length])[0] = var4;
        System.arraycopy(var5, 0, var6, 1, var5.length);
        super(29, var10002, false, var6);
    }

    public byte getRevocationReason() {
        return this.data[0];
    }

    public String getRevocationDescription() {
        if (this.data.length == 1) {
            return "";
        } else {
            byte[] var1 = new byte[this.data.length - 1];
            System.arraycopy(this.data, 1, var1, 0, var1.length);
            return Strings.fromUTF8ByteArray(var1);
        }
    }
}
