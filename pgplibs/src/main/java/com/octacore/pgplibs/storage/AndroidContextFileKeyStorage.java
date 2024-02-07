package com.octacore.pgplibs.storage;

import android.content.Context;

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
public class AndroidContextFileKeyStorage implements IKeyStoreStorage {
    private String a;
    private Context b;
    private int c;

    public AndroidContextFileKeyStorage(Context var1, String var2) {
        this.c = 0;
        this.a = var2;
        this.b = var1;
    }

    public AndroidContextFileKeyStorage(Context var1, String var2, int var3) {
        this(var1, var2);
        this.c = var3;
    }

    public String getFileName() {
        return this.a;
    }

    public InputStream getInputStream() throws IOException {
        return this.b.getFileStreamPath(this.a).exists() ? this.b.openFileInput(this.a) : null;
    }

    public OutputStream getOutputStream() throws IOException {
        return this.b.openFileOutput(this.a, this.c);
    }
}
