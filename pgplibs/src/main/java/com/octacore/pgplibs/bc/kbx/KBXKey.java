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
public class KBXKey {
    private byte[] a;
    private byte[] b = new byte[0];
    public byte[] KeyID = new byte[0];

    public KBXKey() {
    }

    public static KBXKey create(InputStream var0, int var1) throws IOException {
        KBXKey var2;
        (var2 = new KBXKey()).a = new byte[20];
        var0.read(var2.a, 0, var2.a.length);
        var0.read();
        var0.read();
        var0.read();
        var0.read();
        var0.read();
        var0.read();
        var0.read();
        var0.read();
        if ((var1 -= 28) > 0) {
            var2.b = new byte[var1];
            var0.read(var2.b, 0, var2.b.length);
        }

        return var2;
    }
}
