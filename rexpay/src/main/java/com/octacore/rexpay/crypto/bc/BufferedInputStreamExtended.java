package com.octacore.rexpay.crypto.bc;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 06/02/2024
 **************************************************************************************************/
public class BufferedInputStreamExtended extends BufferedInputStream {
    BufferedInputStreamExtended(InputStream var1) {
        super(var1);
    }

    public synchronized int available() throws IOException {
        int var1;
        if ((var1 = super.available()) < 0) {
            var1 = Integer.MAX_VALUE;
        }

        return var1;
    }
}
