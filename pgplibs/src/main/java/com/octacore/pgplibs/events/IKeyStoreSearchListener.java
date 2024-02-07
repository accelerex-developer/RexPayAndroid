package com.octacore.pgplibs.events;

import com.octacore.pgplibs.KeyStore;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public interface IKeyStoreSearchListener {
    void onKeyNotFound(KeyStore var1, boolean var2, long var3, String var5, String var6);
}
