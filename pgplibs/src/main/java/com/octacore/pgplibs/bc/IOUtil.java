package com.octacore.pgplibs.bc;

import com.octacore.pgplibs.PGPException;

import org.spongycastle.bcpg.ArmoredOutputStream;
import org.spongycastle.openpgp.PGPPublicKeyRing;
import org.spongycastle.openpgp.PGPSecretKeyRing;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class IOUtil {
    public IOUtil() {
    }

    public static void closeStream(InputStream var0) {
        if (var0 != null) {
            try {
                var0.close();
                return;
            } catch (IOException var1) {
            }
        }

    }

    public static void closeStream(OutputStream var0) {
        if (var0 != null) {
            try {
                var0.flush();
                var0.close();
                return;
            } catch (IOException var1) {
            }
        }

    }

    public static InputStream readFileOrAsciiString(String var0, String var1) throws IOException {
        if (var0 != null && !"".equals(var0.trim())) {
            label95:
            {
                if (var0.indexOf("BEGIN") == -1 || var0.indexOf("PGP") == -1) {
                    if (var0.length() < 256) {
                        break label95;
                    }

                    var1 = var0;
                    boolean var10000;
                    if (var0 != null && !"".equals(var0.trim())) {
                        int var2 = 0;

                        while (true) {
                            if (var2 >= var1.length()) {
                                var10000 = true;
                                break;
                            }

                            char var3;
                            if (((var3 = var1.charAt(var2)) < 'A' || var3 > 'Z') && (var3 < 'a' || var3 > 'z') && (var3 < '0' || var3 > '9') && var3 != '+' && var3 != '/' && var3 != '\r' && var3 != '\n') {
                                var10000 = false;
                                break;
                            }

                            ++var2;
                        }
                    } else {
                        var10000 = false;
                    }

                    if (!var10000) {
                        break label95;
                    }
                }

                return new ByteArrayInputStream(var0.getBytes("ASCII"));
            }

            if ((new File(var0)).exists()) {
                return new BufferedInputStream(new FileInputStream(var0));
            } else {
                throw new FileNotFoundException("File not found at the specified location: " + var0);
            }
        } else {
            throw new IllegalArgumentException(var1);
        }
    }

    public static void exportPublicKeyRing(PGPPublicKeyRing var0, String var1, boolean var2, String var3) throws IOException {
        FileOutputStream var4 = null;

        try {
            var4 = new FileOutputStream(var1);
            exportPublicKeyRing(var0, (OutputStream) var4, var2, var3);
        } finally {
            closeStream((OutputStream) var4);
        }

    }

    public static void exportPublicKeyRing(PGPPublicKeyRing var0, OutputStream var1, boolean var2, String var3) throws IOException {
        try {
            if (var2) {
                ((ArmoredOutputStream) (var1 = new ArmoredOutputStream((OutputStream) var1))).setHeader("Version", var3);
            }

            var0.encode((OutputStream) var1);
        } catch (IOException var7) {
            throw var7;
        } finally {
            if (var2) {
                closeStream((OutputStream) var1);
            }

        }

    }

    public static void exportPrivateKey(PGPSecretKeyRing var0, String var1, boolean var2, String var3) throws IOException {
        Object var4 = null;
        Object var5 = null;

        try {
            var4 = new FileOutputStream(var1);
            if (var2) {
                var5 = var4;
                ((ArmoredOutputStream) (var4 = new ArmoredOutputStream((OutputStream) var4))).setHeader("Version", var3);
            }

            var0.encode((OutputStream) var4);
        } catch (IOException var8) {
            throw var8;
        } finally {
            closeStream((OutputStream) var4);
            closeStream((OutputStream) var5);
        }

    }

    public static void exportPrivateKey(PGPSecretKeyRing var0, OutputStream var1, boolean var2, String var3) throws IOException {
        try {
            if (var2) {
                ((ArmoredOutputStream) (var1 = new ArmoredOutputStream((OutputStream) var1))).setHeader("Version", var3);
            }

            var0.encode((OutputStream) var1);
        } catch (IOException var7) {
            throw var7;
        } finally {
            if (var2) {
                closeStream((OutputStream) var1);
            }

        }

    }

    public static SecureRandom getSecureRandom() {
        try {
            return SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException var0) {
            return new SecureRandom();
        }
    }

    public static PGPException newPGPException(org.spongycastle.openpgp.PGPException var0) {
        return var0 instanceof PGPException ? (PGPException) var0 : new PGPException(var0.getMessage(), var0.getUnderlyingException());
    }
}
