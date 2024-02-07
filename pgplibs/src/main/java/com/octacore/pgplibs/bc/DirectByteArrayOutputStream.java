package com.octacore.pgplibs.bc;

import java.io.ByteArrayOutputStream;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class DirectByteArrayOutputStream extends ByteArrayOutputStream {
    public DirectByteArrayOutputStream(int var1) {
        super(var1);
    }

    public byte[] getArray() {
        return this.buf;
    }

    protected void finalize() throws Throwable {
        super.finalize();
    }
}
