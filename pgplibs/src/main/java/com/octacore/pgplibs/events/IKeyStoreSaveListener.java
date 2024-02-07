package com.octacore.pgplibs.events;

import com.octacore.pgplibs.KeyStore;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public interface IKeyStoreSaveListener {
    void onSave(KeyStore var1);
}
