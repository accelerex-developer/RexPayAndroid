package com.octacore.pgplibs;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public enum HashAlgorithm {
    SHA1,
    SHA256,
    SHA384,
    SHA512,
    SHA224,
    MD5,
    RIPEMD160,
    MD2;

    private HashAlgorithm() {
    }

    public final int intValue() {
        switch (this) {
            case SHA1:
                return 2;
            case SHA256:
                return 8;
            case SHA384:
                return 9;
            case SHA512:
                return 10;
            case SHA224:
                return 11;
            case MD5:
                return 1;
            case RIPEMD160:
                return 3;
            case MD2:
                return 5;
            default:
                return -1;
        }
    }
}
