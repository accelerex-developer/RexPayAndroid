package com.octacore.rexpay.crypto;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 06/02/2024
 **************************************************************************************************/
public enum CypherAlgorithm {
    NONE,
    TRIPLE_DES,
    CAST5,
    BLOWFISH,
    AES_128,
    AES_192,
    AES_256,
    TWOFISH,
    DES,
    SAFER,
    IDEA,
    CAMELLIA_128,
    CAMELLIA_192,
    CAMELLIA_256;

    private CypherAlgorithm() {
    }

    public final int intValue() {
        switch (this) {
            case AES_128:
                return 7;
            case AES_256:
                return 9;
            case AES_192:
                return 8;
            case BLOWFISH:
                return 4;
            case CAMELLIA_128:
                return 11;
            case CAMELLIA_192:
                return 12;
            case CAMELLIA_256:
                return 13;
            case CAST5:
                return 3;
            case DES:
                return 6;
            case IDEA:
                return 1;
            case SAFER:
                return 5;
            case TRIPLE_DES:
                return 2;
            case TWOFISH:
                return 10;
            default:
                return 0;
        }
    }
}
