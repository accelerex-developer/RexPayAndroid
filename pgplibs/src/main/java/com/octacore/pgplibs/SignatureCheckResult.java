package com.octacore.pgplibs;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public enum SignatureCheckResult {
    SignatureVerified,
    SignatureBroken,
    PublicKeyNotMatching,
    NoSignatureFound;

    private SignatureCheckResult() {
    }
}
