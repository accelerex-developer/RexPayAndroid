package com.octacore.rexpay.crypto.bc;

import java.io.IOException;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class UnknownKeyPacketsException extends IOException {
    private static final long serialVersionUID = -277403260230069362L;

    public UnknownKeyPacketsException(String var1) {
        super(var1);
    }

    public UnknownKeyPacketsException(String var1, Exception var2) {
        super(var1);
    }
}
