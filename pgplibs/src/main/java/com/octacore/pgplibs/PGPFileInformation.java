package com.octacore.pgplibs;

import java.util.List;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class PGPFileInformation {
    public static final int SIGNED = 1;
    public static final int ENCRYPTED = 2;
    public static final int SIGNED_AND_ENCRYPTED = 3;
    private int a;
    private List b;

    public PGPFileInformation() {
    }

    public int getAction() {
        return this.a;
    }

    public void setAction(int var1) {
        this.a = var1;
    }

    public List getFiles() {
        return this.b;
    }

    public void setFiles(List var1) {
        this.b = var1;
    }
}
