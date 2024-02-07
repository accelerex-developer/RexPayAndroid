package com.octacore.pgplibs.bc.kbx;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class KBXDataBlob {
    private int a;
    private byte b;
    public KBXBlobType BlobType;
    private int c;
    private short d;
    private short e;
    private KBXKey[] f;
    private short g;
    private byte[] h;
    private short i;
    private KBXUserID[] j;
    private short k;
    private int[] l;
    private int m;
    private byte[] n;
    public byte[] Blob;
    private byte[] o;
    private byte[] p;

    public KBXDataBlob() {
    }

    public static KBXDataBlob readFromStream(InputStream var0) throws IOException {
        KBXDataBlob var1 = new KBXDataBlob();
        int var2 = 0;
        var1.a = var0.read() << 24 | var0.read() << 16 | var0.read() << 8 | var0.read();
        var2 += 4;
        var1.b = (byte) var0.read();

        try {
            var1.BlobType = KBXBlobType.fromInt(var1.b);
        } catch (EOFException var4) {
            return null;
        }

        ++var2;
        if ((long) var1.a > 5242880L) {
            return null;
        } else {
            var0.read();
            ++var2;
            var0.read();
            var0.read();
            var2 += 2;
            var0.read();
            var0.read();
            var0.read();
            var0.read();
            var2 += 4;
            var1.c = var0.read() << 24 | var0.read() << 16 | var0.read() << 8 | var0.read();
            var2 += 4;
            var1.d = (short) (var0.read() << 8 | var0.read());
            var2 += 2;
            var1.e = (short) (var0.read() << 8 | var0.read());
            var2 += 2;
            var1.f = new KBXKey[var1.d];

            int var3;
            for (var3 = 0; var3 < var1.d; ++var3) {
                var1.f[var3] = KBXKey.create(var0, var1.e);
                var2 += var1.e;
            }

            var1.g = (short) (var0.read() << 8 | var0.read());
            var2 += 2;
            var1.h = new byte[var1.g];
            var0.read(var1.h, 0, var1.h.length);
            var2 += var1.h.length;
            var1.i = (short) (var0.read() << 8 | var0.read());
            var2 += 2;
            var0.read();
            var0.read();
            var2 += 2;
            var1.j = new KBXUserID[var1.i];

            for (var3 = 0; var3 < var1.i; ++var3) {
                var1.j[var3] = new KBXUserID(var0);
                var2 += 12;
            }

            var1.k = (short) (var0.read() << 8 | var0.read());
            var2 += 2;
            var0.read();
            var0.read();
            var2 += 2;
            var1.l = new int[var1.k];

            for (var3 = 0; var3 < var1.k; ++var3) {
                var1.l[var3] = var0.read() << 24 | var0.read() << 16 | var0.read() << 8 | var0.read();
                var2 += 4;
            }

            var0.read();
            ++var2;
            var0.read();
            ++var2;
            var0.read();
            var0.read();
            var2 += 2;
            var0.read();
            var0.read();
            var0.read();
            var0.read();
            var2 += 4;
            var0.read();
            var0.read();
            var0.read();
            var0.read();
            var2 += 4;
            var0.read();
            var0.read();
            var0.read();
            var0.read();
            var2 += 4;
            var1.m = var0.read() << 24 | var0.read() << 16 | var0.read() << 8 | var0.read();
            var2 += 4;
            var1.n = new byte[var1.m];
            if (var1.m > 0) {
                var0.read(var1.n, 0, var1.n.length);
                var2 += var1.n.length;
            }

            var1.Blob = new byte[var1.c];
            var0.read(var1.Blob, 0, var1.Blob.length);
            var2 += var1.Blob.length;
            var1.p = new byte[20];
            var1.o = new byte[var1.a - var2 - var1.p.length];
            if (var1.o.length > 0) {
                var0.read(var1.o, 0, var1.o.length);
                byte[] var10000 = var1.o;
            }

            var0.read(var1.p, 0, var1.p.length);
            return var1;
        }
    }
}
