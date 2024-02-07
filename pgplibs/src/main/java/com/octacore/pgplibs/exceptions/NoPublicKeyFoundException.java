package com.octacore.pgplibs.exceptions;

import com.octacore.pgplibs.PGPException;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class NoPublicKeyFoundException extends PGPException {
    private static final long serialVersionUID = -4979530887461581921L;

    public NoPublicKeyFoundException(String var1) {
        super(var1);
    }

    public NoPublicKeyFoundException(String var1, Exception var2) {
        super(var1, var2);
    }
}
