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
public class KBXUserID {
    public static final int Size = 12;

    public KBXUserID(InputStream var1) throws IOException {
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
