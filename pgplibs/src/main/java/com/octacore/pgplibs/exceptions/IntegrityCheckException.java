package com.octacore.pgplibs.exceptions;

import com.octacore.pgplibs.PGPException;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class IntegrityCheckException extends PGPException {
    private static final long serialVersionUID = 2934123828451758643L;

    public IntegrityCheckException(String var1) {
        super(var1);
    }
}
