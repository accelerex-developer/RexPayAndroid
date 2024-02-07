package com.octacore.pgplibs.storage;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @deprecated
 */
public class FileKeyStorage implements IKeyStoreStorage {
    private String a;

    public FileKeyStorage(String var1) {
        this.a = var1;
    }

    public String getFileName() {
        return this.a;
    }

    public InputStream getInputStream() throws IOException {
        File var1;
        return (var1 = new File(this.a)).exists() && var1.length() != 0L ? new FileInputStream(this.a) : null;
    }

    public OutputStream getOutputStream() throws IOException {
        return new FileOutputStream(this.a);
    }
}
