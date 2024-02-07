package com.octacore.pgplibs.inspect;

import java.io.Serializable;
import java.util.Date;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class ContentItem implements Serializable {
    private static final long serialVersionUID = 590263891205390633L;
    private String a;
    private Date b;
    private boolean c;

    ContentItem(String var1, Date var2) {
        this(var1, var2, false);
    }

    ContentItem(String var1, Date var2, boolean var3) {
        this.a = var1;
        this.b = var2;
        this.c = var3;
    }

    public String getFileName() {
        return this.a;
    }

    public Date getModificationDate() {
        return this.b;
    }

    public boolean isDirectory() {
        return this.c;
    }
}
