package com.octacore.pgplibs.bc.kbx;

import java.io.EOFException;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public enum KBXBlobType {
    Empty,
    First,
    OpenPGP,
    X509;

    private KBXBlobType() {
    }

    public static KBXBlobType fromInt(int var0) throws EOFException {
        if (var0 == -1) {
            throw new EOFException();
        } else if (var0 == 0) {
            return Empty;
        } else if (var0 == 1) {
            return First;
        } else if (var0 == 2) {
            return OpenPGP;
        } else if (var0 == 3) {
            return X509;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
