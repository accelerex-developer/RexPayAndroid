package com.octacore.pgplibs.inspect;

import com.octacore.pgplibs.KeyPairInformation;

import java.util.Date;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class SignatureItem {
    private long a;
    private Date b;
    private String c = "";

    public SignatureItem(long var1, Date var3) {
        this.a = var1;
        this.b = var3;
    }

    public long getKeyId() {
        return this.a;
    }

    public String getKeyIdHex() {
        return KeyPairInformation.keyId2Hex(this.getKeyId());
    }

    public Date getSignatureTime() {
        return this.b;
    }

    public String getUserId() {
        return this.c;
    }

    public void setUserId(String var1) {
        this.c = var1;
    }
}
