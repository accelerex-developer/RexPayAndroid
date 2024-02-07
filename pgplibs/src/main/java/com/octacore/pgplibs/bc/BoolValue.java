package com.octacore.pgplibs.bc;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class BoolValue {
    public boolean value = false;

    public BoolValue() {
        this.value = false;
    }

    public BoolValue(boolean var1) {
        this.value = var1;
    }

    public boolean isValue() {
        return this.value;
    }

    public void setValue(boolean var1) {
        this.value = var1;
    }
}
