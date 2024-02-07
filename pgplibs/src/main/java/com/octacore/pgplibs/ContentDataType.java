package com.octacore.pgplibs;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public enum ContentDataType {
    BINARY,
    TEXT,
    UTF8;

    private char a;

    private ContentDataType() {
    }

    public final char getCode() {
        return this.a;
    }

    static {
        BINARY.a = 'b';
        TEXT.a = 't';
        UTF8.a = 'u';
    }
}
