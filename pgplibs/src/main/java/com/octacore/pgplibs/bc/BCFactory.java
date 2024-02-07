package com.octacore.pgplibs.bc;

import org.spongycastle.openpgp.PGPEncryptedDataGenerator;
import org.spongycastle.openpgp.PGPException;
import org.spongycastle.openpgp.PGPOnePassSignature;
import org.spongycastle.openpgp.PGPPrivateKey;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPPublicKeyRing;
import org.spongycastle.openpgp.PGPSignature;
import org.spongycastle.openpgp.PGPSignatureGenerator;
import org.spongycastle.openpgp.PGPV3SignatureGenerator;
import org.spongycastle.openpgp.operator.KeyFingerPrintCalculator;
import org.spongycastle.openpgp.operator.PBEDataDecryptorFactory;
import org.spongycastle.openpgp.operator.PBEKeyEncryptionMethodGenerator;
import org.spongycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.spongycastle.openpgp.operator.PBESecretKeyEncryptor;
import org.spongycastle.openpgp.operator.PGPContentSignerBuilder;
import org.spongycastle.openpgp.operator.PGPContentVerifierBuilderProvider;
import org.spongycastle.openpgp.operator.PGPDataEncryptorBuilder;
import org.spongycastle.openpgp.operator.PublicKeyDataDecryptorFactory;
import org.spongycastle.openpgp.operator.PublicKeyKeyEncryptionMethodGenerator;
import org.spongycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.spongycastle.openpgp.operator.bc.BcPBEDataDecryptorFactory;
import org.spongycastle.openpgp.operator.bc.BcPBEKeyEncryptionMethodGenerator;
import org.spongycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.spongycastle.openpgp.operator.bc.BcPBESecretKeyEncryptorBuilder;
import org.spongycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.spongycastle.openpgp.operator.bc.BcPGPContentVerifierBuilderProvider;
import org.spongycastle.openpgp.operator.bc.BcPGPDataEncryptorBuilder;
import org.spongycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.spongycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;
import org.spongycastle.openpgp.operator.bc.BcPublicKeyKeyEncryptionMethodGenerator;
import org.spongycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.spongycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;
import org.spongycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcePBEDataDecryptorFactoryBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcePBEKeyEncryptionMethodGenerator;
import org.spongycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;

import java.io.IOException;
import java.security.SecureRandom;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class BCFactory {
    private boolean a = false;

    public BCFactory(boolean var1) {
        this.a = var1;
    }

    public PGPPublicKeyRing CreatePGPPublicKeyRing(byte[] var1) throws IOException {
        return new PGPPublicKeyRing(var1, this.CreateKeyFingerPrintCalculator());
    }

    public KeyFingerPrintCalculator CreateKeyFingerPrintCalculator() {
        return (KeyFingerPrintCalculator) (this.a ? new JcaKeyFingerprintCalculator() : new BcKeyFingerprintCalculator());
    }

    public PBESecretKeyDecryptor CreatePBESecretKeyDecryptor(char[] var1) throws PGPException {
        return this.a ? (new JcePBESecretKeyDecryptorBuilder()).setProvider("SC").build(var1) : (new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider())).build(var1);
    }

    public PBESecretKeyDecryptor CreatePBESecretKeyDecryptor(String var1) throws PGPException {
        return this.a ? (new JcePBESecretKeyDecryptorBuilder()).setProvider("SC").build(var1 == null ? "".toCharArray() : var1.toCharArray()) : (new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider())).build(var1 == null ? "".toCharArray() : var1.toCharArray());
    }

    public PBESecretKeyEncryptor CreatePBESecretKeyEncryptor(String var1, int var2) throws PGPException {
        return this.a ? (new JcePBESecretKeyEncryptorBuilder(var2)).setProvider("SC").build(var1 == null ? "".toCharArray() : var1.toCharArray()) : (new BcPBESecretKeyEncryptorBuilder(var2)).build(var1 == null ? "".toCharArray() : var1.toCharArray());
    }

    public PBEDataDecryptorFactory CreatePBEDataDecryptorFactory(String var1) throws PGPException {
        return (PBEDataDecryptorFactory) (this.a ? (new JcePBEDataDecryptorFactoryBuilder((new JcaPGPDigestCalculatorProviderBuilder()).setProvider("SC").build())).build(var1 == null ? new char[0] : var1.toCharArray()) : new BcPBEDataDecryptorFactory(var1 == null ? new char[0] : var1.toCharArray(), new BcPGPDigestCalculatorProvider()));
    }

    public PGPSignatureGenerator CreatePGPSignatureGenerator(int var1, int var2) {
        return new PGPSignatureGenerator(this.CreatePGPContentSignerBuilder(var1, var2));
    }

    public PGPV3SignatureGenerator CreatePGPV3SignatureGenerator(int var1, int var2) {
        return new PGPV3SignatureGenerator(this.CreatePGPContentSignerBuilder(var1, var2));
    }

    public PGPContentSignerBuilder CreatePGPContentSignerBuilder(int var1, int var2) {
        return (PGPContentSignerBuilder) (this.a ? new JcaPGPContentSignerBuilder(var1, var2) : new BcPGPContentSignerBuilder(var1, var2));
    }

    public void initSign(PGPSignatureGenerator var1, int var2, PGPPrivateKey var3) throws PGPException {
        try {
            var1.init(var2, var3);
        } catch (PGPException var4) {
            throw IOUtil.newPGPException(var4);
        }
    }

    public void initSign(PGPV3SignatureGenerator var1, int var2, PGPPrivateKey var3) throws PGPException {
        try {
            var1.init(var2, var3);
        } catch (PGPException var4) {
            throw IOUtil.newPGPException(var4);
        }
    }

    public PublicKeyKeyEncryptionMethodGenerator CreatePublicKeyKeyEncryptionMethodGenerator(PGPPublicKey var1) {
        return (PublicKeyKeyEncryptionMethodGenerator) (this.a ? new JcePublicKeyKeyEncryptionMethodGenerator(var1) : new BcPublicKeyKeyEncryptionMethodGenerator(var1));
    }

    public void initVerify(PGPSignature var1, PGPPublicKey var2) throws PGPException {
        try {
            var1.init(this.CreatePGPContentVerifierBuilderProvider(), var2);
        } catch (PGPException var3) {
            throw IOUtil.newPGPException(var3);
        }
    }

    public void initVerify(PGPOnePassSignature var1, PGPPublicKey var2) throws PGPException {
        try {
            var1.init(this.CreatePGPContentVerifierBuilderProvider(), var2);
        } catch (PGPException var3) {
            throw IOUtil.newPGPException(var3);
        }
    }

    public PGPContentVerifierBuilderProvider CreatePGPContentVerifierBuilderProvider() {
        return (PGPContentVerifierBuilderProvider) (this.a ? (new JcaPGPContentVerifierBuilderProvider()).setProvider("SC") : new BcPGPContentVerifierBuilderProvider());
    }

    public PBEKeyEncryptionMethodGenerator CreatePBEKeyEncryptionMethodGenerator(String var1) {
        return (PBEKeyEncryptionMethodGenerator) (this.a ? new JcePBEKeyEncryptionMethodGenerator(var1 == null ? new char[0] : var1.toCharArray()) : new BcPBEKeyEncryptionMethodGenerator(var1 == null ? new char[0] : var1.toCharArray()));
    }

    public PBEKeyEncryptionMethodGenerator CreatePBEKeyEncryptionMethodGenerator(char[] var1) {
        return (PBEKeyEncryptionMethodGenerator) (this.a ? new JcePBEKeyEncryptionMethodGenerator(var1) : new BcPBEKeyEncryptionMethodGenerator(var1));
    }

    public PGPEncryptedDataGenerator CreatePGPEncryptedDataGenerator(int var1, boolean var2, SecureRandom var3, boolean var4) {
        return new PGPEncryptedDataGenerator(this.CreatePGPDataEncryptorBuilder(var1, var2, var3), var4);
    }

    public PGPEncryptedDataGenerator CreatePGPEncryptedDataGenerator(int var1, boolean var2, SecureRandom var3) {
        return new PGPEncryptedDataGenerator(this.CreatePGPDataEncryptorBuilder(var1, var2, var3));
    }

    public PGPDataEncryptorBuilder CreatePGPDataEncryptorBuilder(int var1, boolean var2, SecureRandom var3) {
        if (this.a) {
            JcePGPDataEncryptorBuilder var5;
            (var5 = new JcePGPDataEncryptorBuilder(var1)).setSecureRandom(var3);
            var5.setWithIntegrityPacket(var2);
            var5.setProvider("SC");
            return var5;
        } else {
            BcPGPDataEncryptorBuilder var4;
            (var4 = new BcPGPDataEncryptorBuilder(var1)).setSecureRandom(var3);
            var4.setWithIntegrityPacket(var2);
            return var4;
        }
    }

    public PublicKeyDataDecryptorFactory CreatePublicKeyDataDecryptorFactory(PGPPrivateKey var1) {
        return (PublicKeyDataDecryptorFactory) (this.a ? (new JcePublicKeyDataDecryptorFactoryBuilder()).setProvider("SC").build(var1) : new BcPublicKeyDataDecryptorFactory(var1));
    }
}
