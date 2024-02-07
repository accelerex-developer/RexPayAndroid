package com.octacore.pgplibs.exceptions;

import com.octacore.pgplibs.PGPException;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class NonPGPDataException extends PGPException {
    private static final long serialVersionUID = -4989400714535054834L;

    public NonPGPDataException(String var1) {
        super(var1);
    }

    public NonPGPDataException(String var1, Exception var2) {
        super(var1, var2);
    }
}
