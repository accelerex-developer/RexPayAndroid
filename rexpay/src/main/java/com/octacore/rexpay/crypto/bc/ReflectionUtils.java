package com.octacore.rexpay.crypto.bc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class ReflectionUtils {
    public ReflectionUtils() {
    }

    public static Object callPrivateStaticMethod(Class var0, String var1, Object[] var2) {
        Class[] var3;
        if (var2.length == 0) {
            var3 = new Class[0];
        } else {
            var3 = new Class[var2.length];

            for (int var4 = 0; var4 < var2.length; ++var4) {
                var3[var4] = var2[var4].getClass();
            }
        }

        Object var9 = null;

        try {
            Method var8;
            (var8 = var0.getDeclaredMethod(var1, var3)).setAccessible(true);
            var9 = var8.invoke((Object) null, var2);
        } catch (InvocationTargetException var5) {
        } catch (IllegalAccessException var6) {
        } catch (NoSuchMethodException var7) {
            throw new Error("No such method: " + var1);
        }

        return var9;
    }

    public static Object callPrivateMethod(Class var0, String var1, Object var2, Object[] var3) {
        Class[] var4;
        if (var3.length == 0) {
            var4 = new Class[0];
        } else {
            var4 = new Class[var3.length];

            for (int var5 = 0; var5 < var3.length; ++var5) {
                var4[var5] = var3[var5].getClass();
            }
        }

        Object var10 = null;

        try {
            Method var9;
            (var9 = var0.getDeclaredMethod(var1, var4)).setAccessible(true);
            var10 = var9.invoke(var2, var3);
        } catch (InvocationTargetException var6) {
        } catch (IllegalAccessException var7) {
        } catch (NoSuchMethodException var8) {
            throw new Error("No such method: " + var1);
        }

        return var10;
    }

    public static Object callPrivateConstrtuctor(Class var0, Object[] var1) {
        Class[] var2;
        if (var1.length == 0) {
            var2 = new Class[0];
        } else {
            var2 = new Class[var1.length];

            for (int var3 = 0; var3 < var1.length; ++var3) {
                var2[var3] = var1[var3].getClass();
            }
        }

        Object var9 = null;

        try {
            Constructor var8;
            (var8 = var0.getDeclaredConstructor(var2)).setAccessible(true);
            var9 = var8.newInstance(var1);
        } catch (InvocationTargetException var4) {
        } catch (IllegalAccessException var5) {
        } catch (InstantiationException var6) {
        } catch (NoSuchMethodException var7) {
            throw new Error("No such method: " + var0.getName());
        }

        return var9;
    }

    public static void setPrivateFieldvalue(Object var0, String var1, Object var2) {
        try {
            Field var7;
            (var7 = var0.getClass().getDeclaredField(var1)).setAccessible(true);
            var7.set(var0, var2);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException var4) {
            var4.printStackTrace();
        }
    }

    public static Object getPrivateFieldvalue(Object var0, String var1) {
        try {
            Field var6;
            (var6 = var0.getClass().getDeclaredField(var1)).setAccessible(true);
            return var6.get(var0);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException var3) {
            var3.printStackTrace();
        }

        return null;
    }
}
