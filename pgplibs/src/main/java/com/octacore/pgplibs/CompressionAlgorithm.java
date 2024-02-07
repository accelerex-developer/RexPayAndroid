package com.octacore.pgplibs;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public enum CompressionAlgorithm {
    ZLIB,
    ZIP,
    BZIP2,
    UNCOMPRESSED;

    private CompressionAlgorithm() {
    }

    public final int intValue() {
        switch (this) {
            case ZLIB:
                return 2;
            case ZIP:
                return 1;
            case BZIP2:
                return 3;
            case UNCOMPRESSED:
                return 0;
            default:
                return -1;
        }
    }
}
