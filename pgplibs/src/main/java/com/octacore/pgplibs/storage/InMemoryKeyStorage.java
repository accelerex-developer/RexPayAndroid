package com.octacore.pgplibs.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class InMemoryKeyStorage implements IKeyStoreStorage {
    public InMemoryKeyStorage() {
    }

    public InputStream getInputStream() throws IOException {
        return null;
    }

    public OutputStream getOutputStream() throws IOException {
        return null;
    }
}
