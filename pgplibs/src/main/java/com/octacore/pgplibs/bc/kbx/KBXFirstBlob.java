package com.octacore.pgplibs.bc.kbx;

import java.io.IOException;
import java.io.InputStream;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class KBXFirstBlob {
    public int Length;
    private byte[] a;

    public KBXFirstBlob(InputStream var1) throws IOException {
        this.Length = var1.read() << 24 | var1.read() << 16 | var1.read() << 8 | var1.read();
        var1.read();
        var1.read();
        var1.read();
        var1.read();
        this.a = new byte[4];
        var1.read(this.a, 0, this.a.length);
        var1.read();
        var1.read();
        var1.read();
        var1.read();
        var1.read();
        var1.read();
        var1.read();
        var1.read();
        var1.read();
        var1.read();
        var1.read();
        var1.read();
        var1.read();
        var1.read();
        var1.read();
        var1.read();
        var1.read();
        var1.read();
        var1.read();
        var1.read();
    }
}
