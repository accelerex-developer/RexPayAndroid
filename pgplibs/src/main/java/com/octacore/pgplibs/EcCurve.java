package com.octacore.pgplibs;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public enum EcCurve {
    P256,
    P384,
    P521,
    Brainpool256,
    Brainpool384,
    Brainpool512;

    private EcCurve() {
    }
}
