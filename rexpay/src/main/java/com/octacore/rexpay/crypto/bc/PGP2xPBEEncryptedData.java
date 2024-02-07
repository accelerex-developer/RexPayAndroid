package com.octacore.rexpay.crypto.bc;

import org.bouncycastle.bcpg.BCPGInputStream;
import org.bouncycastle.openpgp.PGPDataValidationException;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class PGP2xPBEEncryptedData extends PGPEncryptedDataList {
    private byte[] a;
    private int b = 8;
    private BCPGInputStream c;
    private InputStream d;

    public PGP2xPBEEncryptedData(BCPGInputStream var1) throws IOException {
        super(var1);
        this.c = var1;
        byte[] var2 = new byte[this.b + 2];
        var1.read(var2, 0, this.b + 2);
        this.a = var2;
    }

    public InputStream getInputStream() {
        return this.c;
    }

    public InputStream getDataStream(char[] var1) throws PGPException {
        try {
            SecretKey var7 = BaseLib.makeKeyFromPassPhrase(1, var1, "BC");
            Cipher var2;
            (var2 = Cipher.getInstance("IDEA/CFB/NoPadding")).init(2, var7, new IvParameterSpec(new byte[var2.getBlockSize()]));
            byte[] var3 = var2.update(this.a);
            byte[] var4 = var2.doFinal();
            if (2 < var4.length) {
                throw new EOFException("unexpected end of stream.");
            } else {
                boolean var8 = var3[var3.length - 2] == var4[0] && var3[var3.length - 1] == var4[1];
                boolean var9 = var4[0] == 0 && var4[0] == 0;
                if (!var8 && !var9) {
                    throw new PGPDataValidationException("quick check failed.");
                } else {
                    var3 = new byte[var2.getBlockSize()];
                    System.arraycopy(this.a, this.a.length - var2.getBlockSize(), var3, 0, var2.getBlockSize());
                    (var2 = Cipher.getInstance("IDEA/CFB/NoPadding")).init(2, var7, new IvParameterSpec(var3));
                    this.d = new BCPGInputStream(new CipherInputStream(this.getInputStream(), var2));
                    return this.d;
                }
            }
        } catch (PGPException var5) {
            throw var5;
        } catch (Exception var6) {
            throw new PGPException("Exception creating cipher", var6);
        }
    }
}
