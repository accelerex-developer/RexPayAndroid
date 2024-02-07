package com.octacore.pgplibs.exceptions;

import com.octacore.pgplibs.PGPException;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class DetachedSignatureException extends PGPException {
    private static final long serialVersionUID = -4230922249681682790L;

    public DetachedSignatureException(String var1) {
        super(var1);
    }

    public DetachedSignatureException(String var1, Exception var2) {
        super(var1, var2);
    }
}
