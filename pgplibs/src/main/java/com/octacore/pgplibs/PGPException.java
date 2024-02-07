package com.octacore.pgplibs;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class PGPException extends org.spongycastle.openpgp.PGPException {
    private static final long serialVersionUID = -7698669548427089832L;

    public PGPException(String var1) {
        super(var1);
    }

    public PGPException(String var1, Exception var2) {
        super(var1, var2);
    }
}
