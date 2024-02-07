package com.octacore.pgplibs;

import com.octacore.pgplibs.bc.BaseLib;
import com.octacore.pgplibs.bc.BoolValue;
import com.octacore.pgplibs.bc.IOUtil;
import com.octacore.pgplibs.bc.PGPObjectFactory2;
import com.octacore.pgplibs.bc.PGPSignatureSubpacketGeneratorExtended;
import com.octacore.pgplibs.bc.ReflectionUtils;
import com.octacore.pgplibs.bc.UnknownKeyPacketsException;
import com.octacore.pgplibs.bc.kbx.KBXDataBlob;
import com.octacore.pgplibs.bc.kbx.KBXFirstBlob;
import com.octacore.pgplibs.events.IKeyStoreSaveListener;
import com.octacore.pgplibs.events.IKeyStoreSearchListener;
import com.octacore.pgplibs.exceptions.NoPrivateKeyFoundException;
import com.octacore.pgplibs.exceptions.NoPublicKeyFoundException;
import com.octacore.pgplibs.exceptions.NonPGPDataException;
import com.octacore.pgplibs.exceptions.WrongPasswordException;
import com.octacore.pgplibs.storage.AndroidContextFileKeyStorage;
import com.octacore.pgplibs.storage.FileKeyStorage;
import com.octacore.pgplibs.storage.IKeyStoreStorage;
import com.octacore.pgplibs.storage.InMemoryKeyStorage;

import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.nist.NISTNamedCurves;
import org.spongycastle.asn1.teletrust.TeleTrusTNamedCurves;
import org.spongycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.asn1.x9.X9ECPoint;
import org.spongycastle.bcpg.ArmoredInputStream;
import org.spongycastle.bcpg.ArmoredOutputStream;
import org.spongycastle.bcpg.BCPGKey;
import org.spongycastle.bcpg.ECDHPublicBCPGKey;
import org.spongycastle.bcpg.ECDSAPublicBCPGKey;
import org.spongycastle.bcpg.ExperimentalPacket;
import org.spongycastle.bcpg.PublicKeyPacket;
import org.spongycastle.bcpg.TrustPacket;
import org.spongycastle.bcpg.sig.NotationData;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.generators.ECKeyPairGenerator;
import org.spongycastle.crypto.generators.ElGamalKeyPairGenerator;
import org.spongycastle.crypto.params.ECKeyGenerationParameters;
import org.spongycastle.crypto.params.ECNamedDomainParameters;
import org.spongycastle.crypto.params.ElGamalKeyGenerationParameters;
import org.spongycastle.crypto.params.ElGamalParameters;
import org.spongycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.spongycastle.openpgp.PGPDataValidationException;
import org.spongycastle.openpgp.PGPEncryptedDataGenerator;
import org.spongycastle.openpgp.PGPEncryptedDataList;
import org.spongycastle.openpgp.PGPKdfParameters;
import org.spongycastle.openpgp.PGPKeyPair;
import org.spongycastle.openpgp.PGPKeyRingGenerator;
import org.spongycastle.openpgp.PGPLiteralData;
import org.spongycastle.openpgp.PGPLiteralDataGenerator;
import org.spongycastle.openpgp.PGPOnePassSignatureList;
import org.spongycastle.openpgp.PGPPBEEncryptedData;
import org.spongycastle.openpgp.PGPPrivateKey;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPPublicKeyRing;
import org.spongycastle.openpgp.PGPPublicKeyRingCollection;
import org.spongycastle.openpgp.PGPSecretKey;
import org.spongycastle.openpgp.PGPSecretKeyRing;
import org.spongycastle.openpgp.PGPSecretKeyRingCollection;
import org.spongycastle.openpgp.PGPSignature;
import org.spongycastle.openpgp.PGPSignatureGenerator;
import org.spongycastle.openpgp.PGPSignatureList;
import org.spongycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.spongycastle.openpgp.PGPSignatureSubpacketVector;
import org.spongycastle.openpgp.PGPUtil;
import org.spongycastle.openpgp.operator.PGPDigestCalculator;
import org.spongycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.spongycastle.openpgp.operator.bc.BcPBESecretKeyEncryptorBuilder;
import org.spongycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.spongycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.spongycastle.openpgp.operator.bc.BcPGPKeyConverter;
import org.spongycastle.openpgp.operator.bc.BcPGPKeyPair;
import org.spongycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcaPGPKeyPair;
import org.spongycastle.util.encoders.DecoderException;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 07/02/2024
 **************************************************************************************************/
public class KeyStore extends BaseLib implements Serializable {
    private static final long serialVersionUID = -47989515304466957L;
    protected static final int DEFAULT_BUFFER_SIZE = 1048576;
    private String b;
    private String c;
    private boolean d;
    private boolean e;
    private PGPPublicKeyRingCollection f;
    PGPSecretKeyRingCollection a;
    private Date g;
    private Date h;
    private KeyCertificationType i;
    private boolean j;
    private boolean k;
    private static final Logger l = Logger.getLogger(KeyStore.class.getName());
    private IKeyStoreStorage m;
    private boolean n;
    private boolean o;
    private boolean p;
    private static Pattern q = Pattern.compile("^(0x)?[A-Fa-f0-9]{6,8}$");
    private static Pattern r = Pattern.compile("^(0x)?[A-Fa-f0-9]{14,16}$");
    private static String s = "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE649286651ECE65381FFFFFFFFFFFFFFFF";
    private static String t = "2";
    private static String u = "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3DC2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F83655D23DCA3AD961C62F356208552BB9ED529077096966D670C354E4ABC9804F1746C08CA237327FFFFFFFFFFFFFFFF";
    private static String v = "2";
    private static String w = "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3DC2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F83655D23DCA3AD961C62F356208552BB9ED529077096966D670C354E4ABC9804F1746C08CA18217C32905E462E36CE3BE39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9DE2BCBF6955817183995497CEA956AE515D2261898FA051015728E5A8AACAA68FFFFFFFFFFFFFFFF";
    private static String x = "2";
    private static String y = "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3DC2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F83655D23DCA3AD961C62F356208552BB9ED529077096966D670C354E4ABC9804F1746C08CA18217C32905E462E36CE3BE39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9DE2BCBF6955817183995497CEA956AE515D2261898FA051015728E5A8AAAC42DAD33170D04507A33A85521ABDF1CBA64ECFB850458DBEF0A8AEA71575D060C7DB3970F85A6E1E4C7ABF5AE8CDB0933D71E8C94E04A25619DCEE3D2261AD2EE6BF12FFA06D98A0864D87602733EC86A64521F2B18177B200CBBE117577A615D6C770988C0BAD946E208E24FA074E5AB3143DB5BFCE0FD108E4B82D120A93AD2CAFFFFFFFFFFFFFFFF";
    private static String z = "2";
    private static String A = "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3DC2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F83655D23DCA3AD961C62F356208552BB9ED529077096966D670C354E4ABC9804F1746C08CA18217C32905E462E36CE3BE39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9DE2BCBF6955817183995497CEA956AE515D2261898FA051015728E5A8AAAC42DAD33170D04507A33A85521ABDF1CBA64ECFB850458DBEF0A8AEA71575D060C7DB3970F85A6E1E4C7ABF5AE8CDB0933D71E8C94E04A25619DCEE3D2261AD2EE6BF12FFA06D98A0864D87602733EC86A64521F2B18177B200CBBE117577A615D6C770988C0BAD946E208E24FA074E5AB3143DB5BFCE0FD108E4B82D120A92108011A723C12A787E6D788719A10BDBA5B2699C327186AF4E23C1A946834B6150BDA2583E9CA2AD44CE8DBBBC2DB04DE8EF92E8EFC141FBECAA6287C59474E6BC05D99B2964FA090C3A2233BA186515BE7ED1F612970CEE2D7AFB81BDD762170481CD0069127D5B05AA993B4EA988D8FDDC186FFB7DC90A6C08F4DF435C934063199FFFFFFFFFFFFFFFF";
    private static String B = "2";
    private static String C = "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3DC2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F83655D23DCA3AD961C62F356208552BB9ED529077096966D670C354E4ABC9804F1746C08CA18217C32905E462E36CE3BE39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9DE2BCBF6955817183995497CEA956AE515D2261898FA051015728E5A8AAAC42DAD33170D04507A33A85521ABDF1CBA64ECFB850458DBEF0A8AEA71575D060C7DB3970F85A6E1E4C7ABF5AE8CDB0933D71E8C94E04A25619DCEE3D2261AD2EE6BF12FFA06D98A0864D87602733EC86A64521F2B18177B200CBBE117577A615D6C770988C0BAD946E208E24FA074E5AB3143DB5BFCE0FD108E4B82D120A92108011A723C12A787E6D788719A10BDBA5B2699C327186AF4E23C1A946834B6150BDA2583E9CA2AD44CE8DBBBC2DB04DE8EF92E8EFC141FBECAA6287C59474E6BC05D99B2964FA090C3A2233BA186515BE7ED1F612970CEE2D7AFB81BDD762170481CD0069127D5B05AA993B4EA988D8FDDC186FFB7DC90A6C08F4DF435C93402849236C3FAB4D27C7026C1D4DCB2602646DEC9751E763DBA37BDF8FF9406AD9E530EE5DB382F413001AEB06A53ED9027D831179727B0865A8918DA3EDBEBCF9B14ED44CE6CBACED4BB1BDB7F1447E6CC254B332051512BD7AF426FB8F401378CD2BF5983CA01C64B92ECF032EA15D1721D03F482D7CE6E74FEF6D55E702F46980C82B5A84031900B1C9E59E7C97FBEC7E8F323A97A7E36CC88BE0F1D45B7FF585AC54BD407B22B4154AACC8F6D7EBF48E1D814CC5ED20F8037E0A79715EEF29BE32806A1D58BB7C5DA76F550AA3D8A1FBFF0EB19CCB1A313D55CDA56C9EC2EF29632387FE8D76E3C0468043E8F663F4860EE12BF2D5B0B7474D6E694F91E6DCC4024FFFFFFFFFFFFFFFF";
    private static String D = "2";
    private static String E = "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3DC2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F83655D23DCA3AD961C62F356208552BB9ED529077096966D670C354E4ABC9804F1746C08CA18217C32905E462E36CE3BE39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9DE2BCBF6955817183995497CEA956AE515D2261898FA051015728E5A8AAAC42DAD33170D04507A33A85521ABDF1CBA64ECFB850458DBEF0A8AEA71575D060C7DB3970F85A6E1E4C7ABF5AE8CDB0933D71E8C94E04A25619DCEE3D2261AD2EE6BF12FFA06D98A0864D87602733EC86A64521F2B18177B200CBBE117577A615D6C770988C0BAD946E208E24FA074E5AB3143DB5BFCE0FD108E4B82D120A92108011A723C12A787E6D788719A10BDBA5B2699C327186AF4E23C1A946834B6150BDA2583E9CA2AD44CE8DBBBC2DB04DE8EF92E8EFC141FBECAA6287C59474E6BC05D99B2964FA090C3A2233BA186515BE7ED1F612970CEE2D7AFB81BDD762170481CD0069127D5B05AA993B4EA988D8FDDC186FFB7DC90A6C08F4DF435C93402849236C3FAB4D27C7026C1D4DCB2602646DEC9751E763DBA37BDF8FF9406AD9E530EE5DB382F413001AEB06A53ED9027D831179727B0865A8918DA3EDBEBCF9B14ED44CE6CBACED4BB1BDB7F1447E6CC254B332051512BD7AF426FB8F401378CD2BF5983CA01C64B92ECF032EA15D1721D03F482D7CE6E74FEF6D55E702F46980C82B5A84031900B1C9E59E7C97FBEC7E8F323A97A7E36CC88BE0F1D45B7FF585AC54BD407B22B4154AACC8F6D7EBF48E1D814CC5ED20F8037E0A79715EEF29BE32806A1D58BB7C5DA76F550AA3D8A1FBFF0EB19CCB1A313D55CDA56C9EC2EF29632387FE8D76E3C0468043E8F663F4860EE12BF2D5B0B7474D6E694F91E6DBE115974A3926F12FEE5E438777CB6A932DF8CD8BEC4D073B931BA3BC832B68D9DD300741FA7BF8AFC47ED2576F6936BA424663AAB639C5AE4F5683423B4742BF1C978238F16CBE39D652DE3FDB8BEFC848AD922222E04A4037C0713EB57A81A23F0C73473FC646CEA306B4BCBC8862F8385DDFA9D4B7FA2C087E879683303ED5BDD3A062B3CF5B3A278A66D2A13F83F44F82DDF310EE074AB6A364597E899A0255DC164F31CC50846851DF9AB48195DED7EA1B1D510BD7EE74D73FAF36BC31ECFA268359046F4EB879F924009438B481C6CD7889A002ED5EE382BC9190DA6FC026E479558E4475677E9AA9E3050E2765694DFC81F56E880B96E7160C980DD98EDD3DFFFFFFFFFFFFFFFFF";
    private static String F = "2";
    private HashMap G;
    private HashMap H;
    private HashMap I;
    private String J;
    private byte K;
    private int L;
    private int M;
    private LinkedList N;
    private LinkedList O;

    public KeyStore() {
        this.d = true;
        this.e = true;
        this.i = KeyStore.KeyCertificationType.PositiveCertification;
        this.j = false;
        this.k = true;
        this.m = null;
        this.n = false;
        this.o = false;
        this.p = false;
        this.G = new HashMap();
        this.H = new HashMap();
        this.I = new HashMap();
        this.J = null;
        this.K = 120;
        this.L = 5;
        this.M = 3;
        this.N = new LinkedList();
        this.O = new LinkedList();

        try {
            this.m = new InMemoryKeyStorage();
            ArmoredOutputStream var4 = new ArmoredOutputStream(new ByteArrayOutputStream());
            this.J = (String)ReflectionUtils.getPrivateFieldvalue(var4, "version");
            this.f = new PGPPublicKeyRingCollection(Collections.EMPTY_LIST);
            this.a = new PGPSecretKeyRingCollection(Collections.EMPTY_LIST);
            this.g = this.h = new Date();
        } catch (Exception var3) {
            StackTraceElement[] var1 = var3.getStackTrace();

            for(int var2 = 0; var2 < var1.length; ++var2) {
                c(var1.toString());
            }

        }
    }

    public KeyStore(IKeyStoreStorage var1) throws IOException, PGPException {
        this((IKeyStoreStorage)var1, (String)null);
    }

    public KeyStore(IKeyStoreStorage var1, String var2) throws IOException, PGPException {
        this();
        this.m = var1;
        this.c = var2 == null ? "" : var2;
        InputStream var5 = null;

        try {
            if ((var5 = this.m.getInputStream()) != null) {
                this.a(var5, this.c);
                this.onLoadKeys();
            }
        } finally {
            IOUtil.closeStream(var5);
        }

    }

    /** @deprecated */
    public KeyStore(String var1, String var2) throws IOException, PGPException {
        this((IKeyStoreStorage)(new FileKeyStorage(var1)), var2);
        a("Opening KeyStore from {0}", var1);
        this.b = var1;
    }

    /** @deprecated */
    public static KeyStore openFile(String var0, String var1) throws PGPException, IOException {
        return new KeyStore(var0, var1);
    }

    public static KeyStore openInMemory() {
        return new KeyStore();
    }

    public void addSaveListener(IKeyStoreSaveListener var1) {
        this.N.add(var1);
    }

    public boolean removeSaveListener(IKeyStoreSaveListener var1) {
        return this.N.remove(var1);
    }

    public void addSearchListener(IKeyStoreSearchListener var1) {
        this.O.add(var1);
    }

    public boolean removeSearchListener(IKeyStoreSearchListener var1) {
        return this.O.remove(var1);
    }

    public void purge() throws PGPException {
        this.H.clear();
        this.I.clear();

        try {
            this.f = new PGPPublicKeyRingCollection(Collections.EMPTY_LIST);
            this.a = new PGPSecretKeyRingCollection(Collections.EMPTY_LIST);
        } catch (Exception var2) {
            throw new PGPException("unable to initialise: " + var2, var2);
        }

        this.G.clear();
        if (this.d) {
            this.save();
        }

    }

    public void loadFromStream(InputStream var1, String var2) throws IOException, PGPException {
        if (!((InputStream)var1).markSupported()) {
            var1 = new BufferedInputStream((InputStream)var1);
        }

        ((InputStream)var1).mark(1048576);
        InputStream var3;
        if ((var3 = PGPUtil.getDecoderStream((InputStream)var1)) instanceof ArmoredInputStream) {
            if (!((ArmoredInputStream)var3).isEndOfStream()) {
                ((InputStream)var1).reset();
                this.importKeyRing((InputStream)var1, var2);
            }
        } else {
            Object var4;
            if ((var4 = (new PGPObjectFactory2((InputStream)var1)).nextObject()) instanceof PGPPublicKeyRing) {
                ((InputStream)var1).reset();
                this.importKeyRing((InputStream)var1, var2);
            } else if (var4 instanceof PGPSecretKeyRing) {
                ((InputStream)var1).reset();
                this.importKeyRing((InputStream)var1, var2);
            } else if (var4 instanceof PGPLiteralData) {
                ((InputStream)var1).reset();
                this.a((InputStream)var1, var2);
            } else {
                if (!(var4 instanceof PGPEncryptedDataList)) {
                    throw new NonPGPDataException("The provided key storage does not contain valid key data.");
                }

                ((InputStream)var1).reset();
                this.a((InputStream)var1, var2);
            }
        }

        this.onLoadKeys();
    }

    private void a(InputStream var1, String var2) throws IOException, PGPException {
        try {
            var1 = PGPUtil.getDecoderStream(var1);
            Object var3;
            PGPObjectFactory2 var8;
            byte[] var10;
            if ((var3 = (var8 = new PGPObjectFactory2(var1)).nextObject()) instanceof PGPLiteralData) {
                PGPLiteralData var13;
                if ((var13 = (PGPLiteralData)var3) != null) {
                    byte[] var14 = a(var13);
                    this.f = new PGPPublicKeyRingCollection(var14, staticBCFactory.CreateKeyFingerPrintCalculator());
                    if ((var13 = (PGPLiteralData)var8.nextObject()) != null) {
                        var10 = a(var13);
                        this.a = new PGPSecretKeyRingCollection(var10, staticBCFactory.CreateKeyFingerPrintCalculator());
                    }
                }

            } else {
                if (var3 instanceof PGPEncryptedDataList) {
                    PGPEncryptedDataList var11;
                    PGPPBEEncryptedData var12 = (PGPPBEEncryptedData)(var11 = (PGPEncryptedDataList)var3).get(0);

                    try {
                        var1 = var12.getDataStream(staticBCFactory.CreatePBEDataDecryptorFactory(var2));
                    } catch (PGPDataValidationException var5) {
                        throw new WrongPasswordException("The specified password is wrong.", var5.getUnderlyingException());
                    }

                    PGPLiteralData var9;
                    byte[] var4 = a(var9 = (PGPLiteralData)(var8 = new PGPObjectFactory2(var1)).nextObject());
                    this.g = var9.getModificationTime();
                    var10 = a(var9 = (PGPLiteralData)var8.nextObject());
                    this.h = var9.getModificationTime();
                    if (!var12.isIntegrityProtected()) {
                        throw new PGPDataValidationException("no integrity protection found.");
                    }

                    if (!var12.verify()) {
                        throw new PGPDataValidationException("store failed integrity check.");
                    }

                    this.f = new PGPPublicKeyRingCollection(var4, staticBCFactory.CreateKeyFingerPrintCalculator());
                    this.a = new PGPSecretKeyRingCollection(var10, staticBCFactory.CreateKeyFingerPrintCalculator());
                }

            }
        } catch (DecoderException var6) {
            throw new PGPException(var6.getMessage(), var6);
        } catch (org.spongycastle.openpgp.PGPException var7) {
            throw IOUtil.newPGPException(var7);
        }
    }

    public void loadFromStream(InputStream var1) throws IOException, PGPException {
        this.loadFromStream(var1, "");
    }

    public void saveToStream(OutputStream var1) throws IOException {
        a(var1, "pubring.pkr", new Date(), this.f.getEncoded());
        a(var1, "secring.skr", new Date(), this.a.getEncoded());
    }

    public void saveToStream(OutputStream var1, String var2) throws IOException, PGPException {
        this.store(var1, var2);
    }

    public boolean isPartialMatchUserIds() {
        return this.k;
    }

    public void setPartialMatchUserIds(boolean var1) {
        this.k = var1;
    }

    public KeyCertificationType getDefaultKeyCertificationType() {
        return this.i;
    }

    public void setDefaultKeyCertificationType(KeyCertificationType var1) {
        this.i = var1;
    }

    public boolean getUsePrecomputedPrimes() {
        return this.n;
    }

    public void setUsePrecomputedPrimes(boolean var1) {
        this.n = var1;
    }

    public boolean isInMemory() {
        InputStream var1 = null;
        OutputStream var2 = null;
        boolean var5 = false;

        label62: {
            try {
                var5 = true;
                var1 = this.m.getInputStream();
                var2 = this.m.getOutputStream();
                var5 = false;
                break label62;
            } catch (IOException var6) {
                var5 = false;
            } finally {
                if (var5) {
                    IOUtil.closeStream(var1);
                    IOUtil.closeStream((OutputStream)null);
                }
            }

            IOUtil.closeStream(var1);
            IOUtil.closeStream((OutputStream)null);
            return var1 == null || var2 == null;
        }

        IOUtil.closeStream(var1);
        IOUtil.closeStream(var2);
        return var1 == null || var2 == null;
    }

    public String getAsciiVersionHeader() {
        return this.J;
    }

    public void setAsciiVersionHeader(String var1) {
        this.J = var1;
    }

    private void a(OutputStream var1) {
        if (var1 instanceof ArmoredOutputStream) {
            ((ArmoredOutputStream)var1).setHeader("Version", this.J);
        }

    }

    public PGPSecretKeyRingCollection getRawSecretKeys() {
        return this.a;
    }

    public PGPPublicKeyRingCollection getRawPublicKeys() {
        return this.f;
    }

    public static boolean isPasswordProtected(String var0) throws IOException {
        FileInputStream var1 = null;

        try {
            InputStream var4 = PGPUtil.getDecoderStream(var1 = new FileInputStream(var0));
            if ((new PGPObjectFactory2(var4)).nextObject() instanceof PGPEncryptedDataList) {
                return true;
            }
        } finally {
            IOUtil.closeStream(var1);
        }

        return false;
    }

    public static boolean isPasswordProtected(IKeyStoreStorage var0) throws IOException {
        InputStream var1 = null;

        try {
            InputStream var4 = PGPUtil.getDecoderStream(var1 = var0.getInputStream());
            if (!((new PGPObjectFactory2(var4)).nextObject() instanceof PGPEncryptedDataList)) {
                return false;
            }
        } finally {
            IOUtil.closeStream(var1);
        }

        return true;
    }

    public static boolean isPasswordProtected(InputStream var0) throws IOException {
        var0 = PGPUtil.getDecoderStream(var0);
        return (new PGPObjectFactory2(var0)).nextObject() instanceof PGPEncryptedDataList;
    }

    public static boolean checkPassword(IKeyStoreStorage var0, String var1) throws IOException {
        return b(var0.getInputStream(), var1);
    }

    /** @deprecated */
    public static boolean checkPassword(String var0, String var1) throws IOException {
        return b(new FileInputStream(var0), var1);
    }

    private static boolean b(InputStream var0, String var1) throws IOException {
        if (var1 == null) {
            var1 = "";
        }

        if (var0 == null) {
            return false;
        } else {
            try {
                InputStream var2 = PGPUtil.getDecoderStream(var0);
                Object var7;
                if (!((var7 = (new PGPObjectFactory2(var2)).nextObject()) instanceof PGPLiteralData)) {
                    PGPEncryptedDataList var8;
                    PGPPBEEncryptedData var9 = (PGPPBEEncryptedData)(var8 = (PGPEncryptedDataList)var7).get(0);

                    try {
                        var9.getDataStream(staticBCFactory.CreatePBEDataDecryptorFactory(var1));
                        return true;
                    } catch (org.spongycastle.openpgp.PGPException var5) {
                        if (var5 instanceof PGPDataValidationException) {
                            return false;
                        }

                        return false;
                    }
                }
            } finally {
                IOUtil.closeStream(var0);
            }

            return false;
        }
    }

    public String[] getUserIds() {
        return (String[])this.G.keySet().toArray(new String[this.G.keySet().size()]);
    }

    public String[] getKeyHexIds() {
        return (String[])this.H.keySet().toArray(new String[this.H.keySet().size()]);
    }

    public long getKeyIdForUserId(String var1) {
        if (null != this.G.get(var1)) {
            return (Long)((List)this.G.get(var1)).get(0);
        } else {
            long var2;
            if ((var2 = this.getKeyIdForKeyIdHex(var1)) > 0L) {
                return var2;
            } else {
                Collection var4;
                return (var4 = this.b(var1)).size() > 0 ? ((PGPPublicKeyRing)var4.iterator().next()).getPublicKey().getKeyID() : -1L;
            }
        }
    }

    public long getKeyIdForKeyIdHex(String var1) {
        var1 = d(var1);
        if (r.matcher(var1).matches()) {
            return Long.decode("0x" + var1);
        } else {
            return null != this.H.get(var1) ? (Long)((List)this.H.get(var1)).get(0) : -1L;
        }
    }

    public void addCertification(long var1, long var3, String var5, String var6) throws PGPException {
        PGPPublicKeyRing var9;
        PGPPublicKey var2 = (var9 = this.a(var1)).getPublicKey();

        PGPSecretKey var10;
        try {
            var10 = this.a.getSecretKey(var3);
        } catch (org.spongycastle.openpgp.PGPException var8) {
            throw IOUtil.newPGPException(var8);
        }

        try {
            PGPSignatureGenerator var4 = staticBCFactory.CreatePGPSignatureGenerator(var2.getAlgorithm(), 2);
            staticBCFactory.initSign(var4, 16, extractPrivateKey(var10, var5));
            PGPSignature var11 = var4.generateCertification(var6, var2);
            var2 = PGPPublicKey.addCertification(var2, var6, var11);
        } catch (Exception var7) {
            throw new PGPException("exception creating signature: " + var7, var7);
        }

        var9 = PGPPublicKeyRing.insertPublicKey(PGPPublicKeyRing.removePublicKey(var9, var2), var2);
        this.replacePublicKeyRing(var9);
    }

    public void signPublicKey(long var1, long var3, String var5) throws PGPException {
        PGPPublicKeyRing var8;
        PGPPublicKey var2 = (var8 = this.a(var1)).getPublicKey();

        PGPSecretKey var6;
        try {
            var6 = this.a.getSecretKey(var3);
        } catch (org.spongycastle.openpgp.PGPException var7) {
            throw IOUtil.newPGPException(var7);
        }

        if (var6 == null) {
            throw new NoPrivateKeyFoundException("No key found with Key Id: " + var3);
        } else {
            String var9 = "";
            Iterator var4;
            if ((var4 = var2.getUserIDs()).hasNext()) {
                var9 = (String)var4.next();
            }

            this.a(var8, var2, var9, var6, var5);
        }
    }

    public void signPublicKey(String var1, String var2, String var3) throws PGPException {
        PGPPublicKeyRing var4;
        PGPPublicKey var5 = (var4 = this.a(var1)).getPublicKey();
        PGPSecretKey var6 = this.findSecretKeyRing(var2).getSecretKey();
        this.a(var4, var5, var1, var6, var3);
    }

    public void signPublicKeyAsTrustedIntroducer(long var1, long var3, String var5) throws PGPException {
        PGPPublicKeyRing var8;
        PGPPublicKey var2 = (var8 = this.a(var1)).getPublicKey();

        PGPSecretKey var6;
        try {
            var6 = this.a.getSecretKey(var3);
        } catch (org.spongycastle.openpgp.PGPException var7) {
            throw IOUtil.newPGPException(var7);
        }

        if (var6 == null) {
            throw new NoPrivateKeyFoundException("No key found with Key Id: " + var3);
        } else {
            String var9 = "";
            Iterator var4;
            if ((var4 = var2.getUserIDs()).hasNext()) {
                var9 = (String)var4.next();
            }

            this.b(var8, var2, var9, var6, var5);
        }
    }

    public void signPublicKeyAsTrustedIntroducer(String var1, String var2, String var3) throws PGPException {
        PGPPublicKeyRing var4;
        PGPPublicKey var5 = (var4 = this.a(var1)).getPublicKey();
        PGPSecretKey var6 = this.findSecretKeyRing(var2).getSecretKey();
        this.b(var4, var5, var1, var6, var3);
    }

    public void setTrust(long var1, byte var3) throws PGPException, NoPublicKeyFoundException {
        PGPPublicKeyRing var4;
        PGPPublicKey var2 = (var4 = this.a(var1)).getPublicKey();
        TrustPacket var5 = new TrustPacket(var3);
        ReflectionUtils.setPrivateFieldvalue(var2, "trustPk", var5);
        var4 = PGPPublicKeyRing.insertPublicKey(PGPPublicKeyRing.removePublicKey(var4, var2), var2);
        this.replacePublicKeyRing(var4);
    }

    public void setTrust(String var1, byte var2) throws PGPException, NoPublicKeyFoundException {
        PGPPublicKeyRing var4;
        PGPPublicKey var3 = (var4 = this.a(var1)).getPublicKey();
        TrustPacket var5 = new TrustPacket(var2);
        ReflectionUtils.setPrivateFieldvalue(var3, "trustPk", var5);
        var4 = PGPPublicKeyRing.insertPublicKey(PGPPublicKeyRing.removePublicKey(var4, var3), var3);
        this.replacePublicKeyRing(var4);
    }

    public boolean isTrusted(String var1) throws PGPException {
        PGPPublicKey var2 = this.a(var1).getPublicKey();
        return this.a(var2.getKeyID(), 0);
    }

    public boolean isTrusted(long var1) throws PGPException {
        return this.a(var1, 0);
    }

    private boolean a(long var1, int var3) throws PGPException {
        if (var3 >= this.L) {
            return false;
        } else {
            PGPPublicKeyRing var4;
            PGPPublicKey var5 = (var4 = this.a(var1)).getPublicKey();
            int var2 = var3;
            KeyStore var12 = this;
            PGPPublicKeyRing var15;
            PGPPublicKey var6 = (var15 = this.a(var1)).getPublicKey();
            boolean var10000;
            if (((KeyPairInformation)this.I.get(new Long(var6.getKeyID()))).getTrust() >= 120) {
                var10000 = true;
            } else {
                Iterator var16 = var15.getPublicKey().getSignaturesOfType(16);

                while(true) {
                    if (!var16.hasNext()) {
                        var10000 = false;
                        break;
                    }

                    PGPSignature var7;
                    if ((var7 = (PGPSignature)var16.next()).getKeyID() != var6.getKeyID() && var12.a(var7.getKeyID(), var2 + 1)) {
                        var10000 = true;
                        break;
                    }
                }
            }

            if (var10000) {
                return true;
            } else {
                int var13 = 0;
                Iterator var14 = var4.getPublicKey().getSignaturesOfType(16);

                PGPSignature var17;
                while(var14.hasNext()) {
                    if ((var17 = (PGPSignature)var14.next()).getKeyID() != var5.getKeyID()) {
                        if (this.d(var17.getKeyID())) {
                            return true;
                        }

                        if (this.b(var17.getKeyID())) {
                            ++var13;
                        }

                        if (var13 >= this.getMarginalsNeeded()) {
                            return true;
                        }
                    }
                }

                var14 = var4.getPublicKey().getSignaturesOfType(18);

                while(var14.hasNext()) {
                    if ((var17 = (PGPSignature)var14.next()).getKeyID() != var5.getKeyID()) {
                        if (this.d(var17.getKeyID())) {
                            return true;
                        }

                        if (this.b(var17.getKeyID())) {
                            ++var13;
                        }

                        if (var13 >= this.getMarginalsNeeded()) {
                            return true;
                        }
                    }
                }

                var14 = var4.getPublicKey().getSignaturesOfType(19);

                while(var14.hasNext()) {
                    if ((var17 = (PGPSignature)var14.next()).getKeyID() != var5.getKeyID()) {
                        if (this.d(var17.getKeyID())) {
                            return true;
                        }

                        if (this.b(var17.getKeyID())) {
                            ++var13;
                        }

                        if (var13 >= this.getMarginalsNeeded()) {
                            return true;
                        }
                    }
                }

                return false;
            }
        }
    }

    private boolean b(long var1) throws PGPException {
        PGPPublicKeyRing var4;
        PGPPublicKey var2 = (var4 = this.a(var1)).getPublicKey();
        if (((KeyPairInformation)this.I.get(new Long(var2.getKeyID()))).getTrust() >= 60) {
            return true;
        } else {
            if (this.L > 1) {
                Iterator var5 = var4.getPublicKey().getSignaturesOfType(16);

                while(var5.hasNext()) {
                    PGPSignature var3;
                    if ((var3 = (PGPSignature)var5.next()).getKeyID() != var2.getKeyID() && this.d(var3.getKeyID())) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public boolean deleteKeyPair(String var1) throws PGPException {
        Collection var2 = this.b(var1);
        Collection var7 = this.getSecretKeyRingCollection(var1);
        long var4 = -1L;
        boolean var3 = false;
        Iterator var8;
        if ((var8 = var2.iterator()).hasNext()) {
            label34: {
                PGPPublicKeyRing var9 = (PGPPublicKeyRing)var8.next();

                try {
                    this.f = PGPPublicKeyRingCollection.removePublicKeyRing(this.f, var9);
                    this.b(var9);
                    this.I.remove(new Long(var9.getPublicKey().getKeyID()));
                    a("Deleted public key Id {0}", KeyPairInformation.keyId2Hex(var9.getPublicKey().getKeyID()));
                } catch (IllegalArgumentException var6) {
                    break label34;
                }

                var4 = var9.getPublicKey().getKeyID();
                var3 = true;
            }
        }

        PGPSecretKeyRing var10;
        if (var3) {
            if ((var10 = this.e(var4)) != null) {
                this.a = PGPSecretKeyRingCollection.removeSecretKeyRing(this.a, var10);
                this.a(var10);
                a("Deleted private key Id {0}", KeyPairInformation.keyId2Hex(var4));
            }
        } else if ((var8 = var7.iterator()).hasNext()) {
            var10 = (PGPSecretKeyRing)var8.next();
            this.a = PGPSecretKeyRingCollection.removeSecretKeyRing(this.a, var10);
            this.a(var10);
            a("Deleted private key Id {0}", KeyPairInformation.keyId2Hex(var10.getPublicKey().getKeyID()));
        }

        if (this.d) {
            this.save();
        }

        return var3;
    }

    public boolean deletePrivateKey(String var1) throws PGPException {
        Collection var3 = this.getSecretKeyRingCollection(var1);
        boolean var2 = false;
        Iterator var4;
        if ((var4 = var3.iterator()).hasNext()) {
            PGPSecretKeyRing var5 = (PGPSecretKeyRing)var4.next();
            this.a = PGPSecretKeyRingCollection.removeSecretKeyRing(this.a, var5);
            this.a(var5);
            a("Deleted private key Id {0}", KeyPairInformation.keyId2Hex(var5.getPublicKey().getKeyID()));
            var2 = true;
        }

        if (this.d) {
            this.save();
        }

        return var2;
    }

    public boolean deletePrivateKey(long var1) throws PGPException {
        PGPSecretKeyRing var3 = this.e(var1);
        boolean var2 = false;
        if (var3 != null) {
            this.a = PGPSecretKeyRingCollection.removeSecretKeyRing(this.a, var3);
            this.a(var3);
            a("Deleted private key Id {0}", KeyPairInformation.keyId2Hex(var3.getPublicKey().getKeyID()));
            var2 = true;
        }

        if (this.d) {
            this.save();
        }

        return var2;
    }

    public boolean deletePublicKey(String var1) throws PGPException {
        Collection var4 = this.b(var1);
        boolean var2 = false;
        Iterator var5;
        if ((var5 = var4.iterator()).hasNext()) {
            label24: {
                PGPPublicKeyRing var6 = (PGPPublicKeyRing)var5.next();

                try {
                    this.f = PGPPublicKeyRingCollection.removePublicKeyRing(this.f, var6);
                    this.b(var6);
                    this.I.remove(new Long(var6.getPublicKey().getKeyID()));
                    a("Deleted public key Id {0}", KeyPairInformation.keyId2Hex(var6.getPublicKey().getKeyID()));
                } catch (IllegalArgumentException var3) {
                    break label24;
                }

                var2 = true;
            }
        }

        if (this.d) {
            this.save();
        }

        return var2;
    }

    public boolean deletePublicKey(long var1) throws PGPException {
        PGPPublicKeyRing var4 = this.c(var1);
        boolean var2 = false;
        if (var4 != null) {
            try {
                this.f = PGPPublicKeyRingCollection.removePublicKeyRing(this.f, var4);
                this.b(var4);
                this.I.remove(new Long(var4.getPublicKey().getKeyID()));
                a("Deleted public key Id {0}", KeyPairInformation.keyId2Hex(var4.getPublicKey().getKeyID()));
            } catch (IllegalArgumentException var3) {
            }

            var2 = true;
        }

        if (this.d) {
            this.save();
        }

        return var2;
    }

    public void deleteKeyPair(long var1) throws PGPException {
        PGPSecretKeyRing var3 = this.e(var1);
        PGPPublicKeyRing var5 = this.c(var1);

        try {
            this.f = PGPPublicKeyRingCollection.removePublicKeyRing(this.f, var5);
            this.b(var5);
            this.I.remove(new Long(var5.getPublicKey().getKeyID()));
            a("Deleted public key Id {0}", KeyPairInformation.keyId2Hex(var5.getPublicKey().getKeyID()));
        } catch (IllegalArgumentException var4) {
        }

        if (var3 != null) {
            this.a = PGPSecretKeyRingCollection.removeSecretKeyRing(this.a, var3);
            this.a(var3);
            a("Deleted private key Id {0}", KeyPairInformation.keyId2Hex(var3.getPublicKey().getKeyID()));
        }

        if (this.d) {
            this.save();
        }

    }

    public boolean changePrivateKeyPassword(String var1, String var2, String var3) throws WrongPasswordException, PGPException {
        Iterator var6;
        if ((var6 = this.getSecretKeyRingCollection(var1).iterator()).hasNext()) {
            PGPSecretKeyRing var7;
            int var4 = (var7 = (PGPSecretKeyRing)var6.next()).getSecretKey().getKeyEncryptionAlgorithm();

            try {
                var7 = PGPSecretKeyRing.copyWithNewPassword(var7, staticBCFactory.CreatePBESecretKeyDecryptor(var2), staticBCFactory.CreatePBESecretKeyEncryptor(var3, var4));
            } catch (org.spongycastle.openpgp.PGPException var5) {
                if (var5.getMessage().startsWith("checksum mismatch at 0 of 2")) {
                    throw new WrongPasswordException(var5.getMessage(), var5.getUnderlyingException());
                }

                throw IOUtil.newPGPException(var5);
            }

            this.replaceSecretKeyRing(var7);
            return true;
        } else {
            return false;
        }
    }

    public void changePrivateKeyPassword(long var1, String var3, String var4) throws NoPrivateKeyFoundException, WrongPasswordException, PGPException {
        PGPSecretKeyRing var6;
        int var2 = (var6 = this.findSecretKeyRing(var1)).getSecretKey().getKeyEncryptionAlgorithm();

        try {
            var6 = PGPSecretKeyRing.copyWithNewPassword(var6, staticBCFactory.CreatePBESecretKeyDecryptor(var3), staticBCFactory.CreatePBESecretKeyEncryptor(var4, var2));
        } catch (org.spongycastle.openpgp.PGPException var5) {
            if (var5.getMessage().startsWith("checksum mismatch at 0 of 2")) {
                throw new WrongPasswordException(var5.getMessage(), var5.getUnderlyingException());
            }

            throw IOUtil.newPGPException(var5);
        }

        this.replaceSecretKeyRing(var6);
    }

    public void addUserId(long var1, String var3, String var4) throws NoPublicKeyFoundException, NoPrivateKeyFoundException, WrongPasswordException, PGPException {
        PGPPublicKeyRing var5;
        if ((var5 = this.c(var1)) == null) {
            throw new NoPublicKeyFoundException("No public key exists with key Id :" + String.valueOf(var1));
        } else {
            PGPPublicKey var6 = var5.getPublicKey();
            PGPSignatureGenerator var7 = staticBCFactory.CreatePGPSignatureGenerator(var6.getAlgorithm(), 2);
            PGPSecretKeyRing var8;
            if ((var8 = this.e(var6.getKeyID())) == null) {
                throw new NoPrivateKeyFoundException("No secret key found. You must have the secret key with key Id :" + String.valueOf(var1));
            } else {
                try {
                    staticBCFactory.initSign(var7, 19, BaseLib.extractPrivateKey(var8.getSecretKey(), var3));
                    PGPSignature var10 = var7.generateCertification(var4, var6);
                    PGPPublicKey var11 = PGPPublicKey.addCertification(var6, var4, var10);
                    var5 = PGPPublicKeyRing.insertPublicKey(PGPPublicKeyRing.removePublicKey(var5, var6), var11);
                } catch (Exception var9) {
                    throw new PGPException("creating signature for userId : " + var4, var9);
                }

                this.replacePublicKeyRing(var5);
            }
        }
    }

    public boolean deleteUserId(long var1, String var3) throws NoPublicKeyFoundException, PGPException {
        PGPPublicKeyRing var4;
        if ((var4 = this.c(var1)) == null) {
            throw new NoPublicKeyFoundException("No public key exists with key Id :" + String.valueOf(var1));
        } else {
            PGPPublicKey var2;
            PGPPublicKey var5;
            if ((var2 = PGPPublicKey.removeCertification(var5 = var4.getPublicKey(), var3)) != null) {
                var4 = PGPPublicKeyRing.insertPublicKey(PGPPublicKeyRing.removePublicKey(var4, var5), var2);
                this.replacePublicKeyRing(var4);
                return true;
            } else {
                return false;
            }
        }
    }

    public KeyPairInformation clearKeyExpirationTime(String var1, String var2) throws NoPublicKeyFoundException, NoPrivateKeyFoundException, PGPException {
        PGPPublicKeyRing var3 = this.a(var1);
        return this.a(var3, var3.getPublicKey().getKeyID(), var2, 0);
    }

    public KeyPairInformation clearKeyExpirationTime(long var1, String var3) throws NoPublicKeyFoundException, NoPrivateKeyFoundException, PGPException {
        PGPPublicKeyRing var4 = this.a(var1);
        return this.a(var4, var1, var3, 0);
    }

    public KeyPairInformation setKeyExpirationTime(String var1, String var2, int var3) throws NoPublicKeyFoundException, NoPrivateKeyFoundException, PGPException {
        PGPPublicKeyRing var4 = this.a(var1);
        return this.a(var4, var4.getPublicKey().getKeyID(), var2, var3);
    }

    public KeyPairInformation setKeyExpirationTime(long var1, String var3, int var4) throws NoPublicKeyFoundException, NoPrivateKeyFoundException, PGPException {
        PGPPublicKeyRing var5 = this.a(var1);
        return this.a(var5, var1, var3, var4);
    }

    private static PGPSignatureSubpacketGeneratorExtended a(PGPSignatureSubpacketVector var0, boolean var1) {
        PGPSignatureSubpacketGeneratorExtended var2 = new PGPSignatureSubpacketGeneratorExtended();
        if (var0.getFeatures() != null) {
            var2.setFeature(var0.getFeatures().isCritical(), var0.getFeatures().getData()[0]);
        }

        if (var0.getIssuerKeyID() != 0L) {
            var2.setIssuerKeyID(true, var0.getIssuerKeyID());
        }

        if (var0.getKeyFlags() > 0) {
            var2.setKeyFlags(false, var0.getKeyFlags());
        }

        if (var0.getNotationDataOccurences().length > 0) {
            for(int var3 = 0; var3 < var0.getNotationDataOccurences().length; ++var3) {
                NotationData var4 = var0.getNotationDataOccurences()[var3];
                var2.setNotationData(var4.isCritical(), var4.isHumanReadable(), var4.getNotationName(), var4.getNotationValue());
            }
        }

        if (var0.getPreferredCompressionAlgorithms() != null) {
            var2.setPreferredCompressionAlgorithms(false, var0.getPreferredCompressionAlgorithms());
        }

        if (var0.getPreferredHashAlgorithms() != null) {
            var2.setPreferredHashAlgorithms(false, var0.getPreferredHashAlgorithms());
        }

        if (var0.getPreferredSymmetricAlgorithms() != null) {
            var2.setPreferredSymmetricAlgorithms(false, var0.getPreferredSymmetricAlgorithms());
        }

        if (var0.getSignatureCreationTime() != null) {
            var2.setSignatureCreationTime(false, var0.getSignatureCreationTime());
        }

        if (var0.getSignatureExpirationTime() > 0L) {
            var2.setSignatureExpirationTime(false, var0.getSignatureExpirationTime());
        }

        if (var0.getSignerUserID() != null) {
            var2.setSignerUserID(false, var0.getSignerUserID());
        }

        if (var0.isPrimaryUserID()) {
            var2.setPrimaryUserID(false, true);
        }

        if (var1 && var0.getKeyExpirationTime() > 0L) {
            var2.setKeyExpirationTime(false, var0.getKeyExpirationTime());
        }

        return var2;
    }

    private KeyPairInformation a(PGPPublicKeyRing var1, long var2, String var4, int var5) throws NoPublicKeyFoundException, NoPrivateKeyFoundException, PGPException {
        PGPPublicKey var6 = var1.getPublicKey(var2);
        PGPSecretKeyRing var7;
        if ((var7 = this.e(var6.getKeyID())) == null) {
            throw new NoPrivateKeyFoundException("No secret key found. You must have the secret key with key Id :" + String.valueOf(var2));
        } else {
            Iterator var11 = var6.getSignatures();

            PGPSignature var3;
            PGPSignatureSubpacketVector var8;
            do {
                if (!var11.hasNext()) {
                    return null;
                }
            } while(!(var3 = (PGPSignature)var11.next()).hasSubpackets() || (var8 = var3.getHashedSubPackets()) == null || var8.getSubpacket(27) == null);

            PGPSignatureSubpacketGeneratorExtended var12 = a(var8, false);
            if (var5 > 0) {
                var12.setKeyExpirationTime(false, (long)(var5 * 60 * 60 * 24));
            }

            PGPSignatureSubpacketVector var13 = var12.generate();
            PGPSignatureGenerator var16 = staticBCFactory.CreatePGPSignatureGenerator(var3.getKeyAlgorithm(), var3.getHashAlgorithm());

            try {
                PGPPublicKey var9 = PGPPublicKey.removeCertification(var6, var3);
                staticBCFactory.initSign(var16, var3.getSignatureType(), BaseLib.extractPrivateKey(var7.getSecretKey(), var4));
                var16.setHashedSubpackets(var13);
                var16.setUnhashedSubpackets(var3.getUnhashedSubPackets());
                PGPSignature var15;
                if (var8.isPrimaryUserID()) {
                    String var14 = null;
                    if (var6.getUserIDs().hasNext()) {
                        var14 = (String)var6.getUserIDs().next();
                    }

                    var15 = var16.generateCertification(var14, var9);
                    var9 = PGPPublicKey.addCertification(var9, var14, var15);
                } else {
                    var15 = var16.generateCertification(var1.getPublicKey(), var9);
                    var9 = PGPPublicKey.addCertification(var9, var15);
                }

                var1 = PGPPublicKeyRing.insertPublicKey(PGPPublicKeyRing.removePublicKey(var1, var6), var9);
            } catch (Exception var10) {
                throw new PGPException("Error changing key expiration time for Key ID : " + var6.getKeyID(), var10);
            }

            this.replacePublicKeyRing(var1);
            return (KeyPairInformation)this.I.get(new Long(var1.getPublicKey().getKeyID()));
        }
    }

    public KeyPairInformation setKeyCertificationType(long var1, String var3, KeyCertificationType var4) throws NoPublicKeyFoundException, NoPrivateKeyFoundException, PGPException {
        PGPPublicKeyRing var5 = this.a(var1);
        return this.a(var5, var3, var4);
    }

    public KeyPairInformation setKeyCertificationType(String var1, String var2, KeyCertificationType var3) throws NoPublicKeyFoundException, NoPrivateKeyFoundException, PGPException {
        PGPPublicKeyRing var4 = this.a(var1);
        return this.a(var4, var2, var3);
    }

    private KeyPairInformation a(PGPPublicKeyRing var1, String var2, KeyCertificationType var3) throws NoPrivateKeyFoundException, PGPException {
        PGPPublicKey var4 = var1.getPublicKey();
        this.replacePublicKeyRing(var1);
        PGPSecretKeyRing var5;
        if ((var5 = this.e(var4.getKeyID())) == null) {
            throw new NoPrivateKeyFoundException("No secret key found. You must have the secret key with key Id :" + String.valueOf(var4.getKeyID()));
        } else {
            Iterator var6 = var4.getUserIDs();

            while(var6.hasNext()) {
                String var7 = (String)var6.next();
                Iterator var8 = var4.getSignaturesForID(var7);

                while(var8.hasNext()) {
                    PGPSignature var9;
                    if ((var9 = (PGPSignature)var8.next()).hasSubpackets()) {
                        PGPSignatureSubpacketVector var10 = var9.getHashedSubPackets();
                        PGPSignatureGenerator var11 = staticBCFactory.CreatePGPSignatureGenerator(var9.getKeyAlgorithm(), var9.getHashAlgorithm());

                        try {
                            PGPPublicKey var12 = PGPPublicKey.removeCertification(var4, var9);
                            staticBCFactory.initSign(var11, var3.getValue(), BaseLib.extractPrivateKey(var5.getSecretKey(), var2));
                            var11.setHashedSubpackets(var10);
                            var11.setUnhashedSubpackets(var9.getUnhashedSubPackets());
                            var9 = var11.generateCertification(var7, var4);
                            var12 = PGPPublicKey.addCertification(var12, var7, var9);
                            var1 = PGPPublicKeyRing.insertPublicKey(PGPPublicKeyRing.removePublicKey(var1, var4), var12);
                        } catch (Exception var13) {
                            throw new PGPException("Error changing key certification for Key ID : " + var4.getKeyID(), var13);
                        }
                    }
                }
            }

            this.replacePublicKeyRing(var1);
            return (KeyPairInformation)this.I.get(new Long(var4.getKeyID()));
        }
    }

    public boolean changeUserId(long var1, String var3, String var4, String var5) throws NoPublicKeyFoundException, NoPrivateKeyFoundException, WrongPasswordException, PGPException {
        PGPPublicKeyRing var6;
        if ((var6 = this.c(var1)) == null) {
            throw new NoPublicKeyFoundException("No public key exists with key Id :" + String.valueOf(var1));
        } else {
            PGPPublicKey var7 = var6.getPublicKey();
            PGPSignatureSubpacketVector var8 = null;
            PGPSignatureSubpacketVector var9 = null;
            Iterator var10 = var7.getSignaturesForID(var4);

            while(var10.hasNext()) {
                PGPSignature var11;
                if ((var11 = (PGPSignature)var10.next()).hasSubpackets()) {
                    var8 = var11.getHashedSubPackets();
                    var9 = var11.getUnhashedSubPackets();
                    if (var8 != null && var8.getSubpacket(27) != null) {
                        var8 = a(var8, true).generate();
                    }
                }
            }

            if ((var7 = PGPPublicKey.removeCertification(var7, var4)) != null) {
                PGPSignatureGenerator var17;
                try {
                    var17 = staticBCFactory.CreatePGPSignatureGenerator(var7.getAlgorithm(), 2);
                    if (var8 != null) {
                        var17.setHashedSubpackets(var8);
                    }

                    if (var9 != null) {
                        var17.setUnhashedSubpackets(var9);
                    }
                } catch (Exception var13) {
                    throw new PGPException("creating signature generator: " + var13, var13);
                }

                PGPSecretKeyRing var16;
                if ((var16 = this.e(var7.getKeyID())) == null) {
                    throw new NoPrivateKeyFoundException("No secret key found. You must have the secret key with key Id :" + String.valueOf(var1));
                } else {
                    try {
                        staticBCFactory.initSign(var17, 19, BaseLib.extractPrivateKey(var16.getSecretKey(), var3));
                        PGPSignature var14 = var17.generateCertification(var5, var7);
                        PGPPublicKey var15 = PGPPublicKey.addCertification(var7, var5, var14);
                        var6 = PGPPublicKeyRing.insertPublicKey(PGPPublicKeyRing.removePublicKey(var6, var7), var15);
                    } catch (Exception var12) {
                        throw new PGPException("creating signature for userId : " + var5, var12);
                    }

                    this.replacePublicKeyRing(var6);
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    public void changePrimaryUserId(long var1, String var3, String var4) throws NoPublicKeyFoundException, NoPrivateKeyFoundException, WrongPasswordException, PGPException {
        PGPPublicKeyRing var5;
        if ((var5 = this.c(var1)) == null) {
            throw new NoPublicKeyFoundException("No public key exists with key Id :" + String.valueOf(var1));
        } else {
            Iterator var7 = var5.getPublicKey().getUserIDs();
            String var6 = null;
            if (var7.hasNext()) {
                var6 = (String)var7.next();
            }

            if (var6 != null) {
                this.changeUserId(var1, var3, var6, var4);
            } else {
                this.addUserId(var1, var3, var4);
            }
        }
    }

    public KeyPairInformation generateEccKeyPair(EcCurve var1, String var2, String var3) throws PGPException {
        return this.generateEccKeyPair(var1, var2, var3, new CompressionAlgorithm[]{CompressionAlgorithm.ZIP, CompressionAlgorithm.ZLIB, CompressionAlgorithm.BZIP2, CompressionAlgorithm.UNCOMPRESSED}, new HashAlgorithm[]{HashAlgorithm.SHA512, HashAlgorithm.SHA384, HashAlgorithm.SHA256}, new CypherAlgorithm[]{CypherAlgorithm.AES_256, CypherAlgorithm.AES_192, CypherAlgorithm.AES_128}, 0L);
    }

    public KeyPairInformation generateEccKeyPair(EcCurve var1, String var2, String var3, long var4) throws PGPException {
        return this.generateEccKeyPair(var1, var2, var3, new CompressionAlgorithm[]{CompressionAlgorithm.ZIP, CompressionAlgorithm.ZLIB, CompressionAlgorithm.BZIP2, CompressionAlgorithm.UNCOMPRESSED}, new HashAlgorithm[]{HashAlgorithm.SHA512, HashAlgorithm.SHA384, HashAlgorithm.SHA256}, new CypherAlgorithm[]{CypherAlgorithm.AES_256, CypherAlgorithm.AES_192, CypherAlgorithm.AES_128}, var4);
    }

    public KeyPairInformation generateEccKeyPair(EcCurve var1, String var2, String var3, CompressionAlgorithm[] var4, HashAlgorithm[] var5, CypherAlgorithm[] var6) throws PGPException {
        return this.generateEccKeyPair(var1, var2, var3, var4, var5, var6, 0L);
    }

    public KeyPairInformation generateEccKeyPair(EcCurve var1, String var2, String var3, CompressionAlgorithm[] var4, HashAlgorithm[] var5, CypherAlgorithm[] var6, long var7) throws PGPException {
        a("Primary User Id is {0}", var2);
        a("EC Curve is {0} bits", var1.toString());
        PGPKeyRingGenerator var9;
        PGPSecretKeyRing var11 = (var9 = a(var1, var1, var2, var3, var4, var5, var6, var7, this.i.a)).generateSecretKeyRing();
        PGPPublicKeyRing var10 = var9.generatePublicKeyRing();
        this.a = PGPSecretKeyRingCollection.addSecretKeyRing(this.a, var11);
        this.f = PGPPublicKeyRingCollection.addPublicKeyRing(this.f, var10);
        this.a(var10);
        KeyPairInformation var12;
        (var12 = (KeyPairInformation)this.I.get(new Long(var10.getPublicKey().getKeyID()))).setPublicKeyRing(var10);
        var12.setPrivateKeyRing(var11);
        this.I.put(new Long(var11.getPublicKey().getKeyID()), var12);
        this.save();
        return var12;
    }

    public KeyPairInformation generateRsaKeyPair(int var1, String var2, String var3) throws PGPException {
        return this.generateKeyPair(var1, var2, KeyAlgorithm.RSA, var3, new CompressionAlgorithm[]{CompressionAlgorithm.ZIP, CompressionAlgorithm.UNCOMPRESSED, CompressionAlgorithm.ZLIB, CompressionAlgorithm.BZIP2}, new HashAlgorithm[]{HashAlgorithm.SHA256, HashAlgorithm.SHA384, HashAlgorithm.SHA512, HashAlgorithm.SHA1, HashAlgorithm.MD5, HashAlgorithm.SHA256}, new CypherAlgorithm[]{CypherAlgorithm.CAST5, CypherAlgorithm.TRIPLE_DES, CypherAlgorithm.AES_128, CypherAlgorithm.AES_192, CypherAlgorithm.AES_256, CypherAlgorithm.TWOFISH}, 0L);
    }

    public KeyPairInformation generateRsaKeyPair(int var1, String var2, String var3, int var4) throws PGPException {
        return this.generateKeyPair(var1, var2, KeyAlgorithm.RSA, var3, new CompressionAlgorithm[]{CompressionAlgorithm.ZIP, CompressionAlgorithm.UNCOMPRESSED, CompressionAlgorithm.ZLIB, CompressionAlgorithm.BZIP2}, new HashAlgorithm[]{HashAlgorithm.SHA256, HashAlgorithm.SHA384, HashAlgorithm.SHA512, HashAlgorithm.SHA1, HashAlgorithm.MD5, HashAlgorithm.SHA256}, new CypherAlgorithm[]{CypherAlgorithm.CAST5, CypherAlgorithm.TRIPLE_DES, CypherAlgorithm.AES_128, CypherAlgorithm.AES_192, CypherAlgorithm.AES_256, CypherAlgorithm.TWOFISH}, (long)var4);
    }

    public KeyPairInformation generateElGamalKeyPair(int var1, String var2, String var3) throws PGPException {
        return this.generateKeyPair(var1, var2, KeyAlgorithm.ELGAMAL, var3, new CompressionAlgorithm[]{CompressionAlgorithm.ZIP, CompressionAlgorithm.UNCOMPRESSED, CompressionAlgorithm.ZLIB, CompressionAlgorithm.BZIP2}, new HashAlgorithm[]{HashAlgorithm.SHA256, HashAlgorithm.SHA384, HashAlgorithm.SHA512, HashAlgorithm.SHA1, HashAlgorithm.MD5, HashAlgorithm.SHA256}, new CypherAlgorithm[]{CypherAlgorithm.CAST5, CypherAlgorithm.TRIPLE_DES, CypherAlgorithm.AES_128, CypherAlgorithm.AES_192, CypherAlgorithm.AES_256, CypherAlgorithm.TWOFISH});
    }

    public KeyPairInformation generateElGamalKeyPair(int var1, String var2, String var3, int var4) throws PGPException {
        return this.generateKeyPair(var1, var2, KeyAlgorithm.ELGAMAL, var3, new CompressionAlgorithm[]{CompressionAlgorithm.ZIP, CompressionAlgorithm.UNCOMPRESSED, CompressionAlgorithm.ZLIB, CompressionAlgorithm.BZIP2}, new HashAlgorithm[]{HashAlgorithm.SHA256, HashAlgorithm.SHA384, HashAlgorithm.SHA512, HashAlgorithm.SHA1, HashAlgorithm.MD5, HashAlgorithm.SHA256}, new CypherAlgorithm[]{CypherAlgorithm.CAST5, CypherAlgorithm.TRIPLE_DES, CypherAlgorithm.AES_128, CypherAlgorithm.AES_192, CypherAlgorithm.AES_256, CypherAlgorithm.TWOFISH}, (long)var4);
    }

    public KeyPairInformation generateKeyPair(int var1, String var2, String var3) throws PGPException {
        return this.generateKeyPair(var1, var2, KeyAlgorithm.RSA, var3, new CompressionAlgorithm[]{CompressionAlgorithm.ZIP, CompressionAlgorithm.UNCOMPRESSED}, new HashAlgorithm[]{HashAlgorithm.SHA1, HashAlgorithm.MD5, HashAlgorithm.SHA256}, new CypherAlgorithm[]{CypherAlgorithm.CAST5, CypherAlgorithm.TRIPLE_DES, CypherAlgorithm.AES_128, CypherAlgorithm.AES_192, CypherAlgorithm.AES_256});
    }

    static int a(HashAlgorithm var0) {
        if (HashAlgorithm.SHA256 == var0) {
            return 8;
        } else if (HashAlgorithm.SHA384 == var0) {
            return 9;
        } else if (HashAlgorithm.SHA512 == var0) {
            return 10;
        } else if (HashAlgorithm.SHA224 == var0) {
            return 11;
        } else if (HashAlgorithm.SHA1 == var0) {
            return 2;
        } else if (HashAlgorithm.MD5 == var0) {
            return 1;
        } else if (HashAlgorithm.RIPEMD160 == var0) {
            return 3;
        } else {
            return HashAlgorithm.MD2 == var0 ? 5 : -1;
        }
    }

    static int a(CompressionAlgorithm var0) {
        if (CompressionAlgorithm.ZLIB.equals(var0)) {
            return 2;
        } else if (CompressionAlgorithm.ZIP.equals(var0)) {
            return 1;
        } else if (CompressionAlgorithm.UNCOMPRESSED.equals(var0)) {
            return 0;
        } else {
            return CompressionAlgorithm.BZIP2.equals(var0) ? 3 : -1;
        }
    }

    static int a(CypherAlgorithm var0) {
        if (CypherAlgorithm.TRIPLE_DES == var0) {
            return 2;
        } else if (CypherAlgorithm.CAST5 == var0) {
            return 3;
        } else if (CypherAlgorithm.BLOWFISH == var0) {
            return 4;
        } else if (CypherAlgorithm.AES_128 == var0) {
            return 7;
        } else if (CypherAlgorithm.AES_192 == var0) {
            return 8;
        } else if (CypherAlgorithm.AES_256.equals(var0)) {
            return 9;
        } else if (CypherAlgorithm.TWOFISH.equals(var0)) {
            return 10;
        } else if (CypherAlgorithm.DES.equals(var0)) {
            return 6;
        } else if (CypherAlgorithm.SAFER.equals(var0)) {
            return 5;
        } else {
            return CypherAlgorithm.IDEA.equals(var0) ? 5 : -1;
        }
    }

    public KeyPairInformation generateKeyPair(int var1, String var2, KeyAlgorithm var3, String var4, CompressionAlgorithm[] var5, HashAlgorithm[] var6, CypherAlgorithm[] var7) throws PGPException {
        return this.generateKeyPair(var1, var2, var3, var4, var5, var6, var7, 0L);
    }

    public KeyPairInformation generateKeyPair(int var1, String var2, KeyAlgorithm var3, String var4, CompressionAlgorithm[] var5, HashAlgorithm[] var6, CypherAlgorithm[] var7, long var8) throws PGPException {
        PGPKeyRingGenerator var10;
        PGPSecretKeyRing var12 = (var10 = a(var1, var2, var3, var4, var5, var6, var7, var8)).generateSecretKeyRing();
        PGPPublicKeyRing var11 = var10.generatePublicKeyRing();
        this.a = PGPSecretKeyRingCollection.addSecretKeyRing(this.a, var12);
        this.f = PGPPublicKeyRingCollection.addPublicKeyRing(this.f, var11);
        this.a(var11);
        KeyPairInformation var13;
        (var13 = (KeyPairInformation)this.I.get(new Long(var11.getPublicKey().getKeyID()))).setPublicKeyRing(var11);
        var13.setPrivateKeyRing(var12);
        this.I.put(new Long(var12.getPublicKey().getKeyID()), var13);
        if (this.d) {
            this.save();
        }

        return var13;
    }

    static PGPKeyRingGenerator a(int var0, String var1, KeyAlgorithm var2, String var3, CompressionAlgorithm[] var4, HashAlgorithm[] var5, CypherAlgorithm[] var6, long var7) throws PGPException {
        ArrayList var9 = new ArrayList();

        int var11;
        for(int var10 = 0; var10 < var6.length; ++var10) {
            var11 = a(var6[var10]);
            var9.add(new Integer(var11));
        }

        int[] var41 = new int[var9.size()];

        for(var11 = 0; var11 < var41.length; ++var11) {
            var41[var11] = (Integer)var9.get(var11);
        }

        ArrayList var46 = new ArrayList();

        int var40;
        for(int var36 = 0; var36 < var5.length; ++var36) {
            var40 = a(var5[var36]);
            var46.add(new Integer(var40));
        }

        int[] var39 = new int[var46.size()];

        for(var40 = 0; var40 < var39.length; ++var40) {
            var39[var40] = (Integer)var46.get(var40);
        }

        var9 = new ArrayList();

        for(int var31 = 0; var31 < var4.length; ++var31) {
            var11 = a(var4[var31]);
            var9.add(new Integer(var11));
        }

        int[] var34 = new int[var9.size()];

        for(var11 = 0; var11 < var34.length; ++var11) {
            var34[var11] = (Integer)var9.get(var11);
        }

        PGPKeyRingGenerator var21;
        AsymmetricCipherKeyPair var43;
        if (KeyAlgorithm.EC != var2) {
            PGPSignatureSubpacketGenerator var30;
            PGPDigestCalculator var37;
            JcaPGPKeyPair var49;
            KeyPairGenerator var53;
            if (KeyAlgorithm.ELGAMAL == var2) {
                try {
                    var53 = KeyPairGenerator.getInstance("DSA", "SC");
                } catch (NoSuchProviderException var15) {
                    throw new PGPException("No Such provider: SC", var15);
                } catch (NoSuchAlgorithmException var16) {
                    throw new PGPException("No Such algorithm: DSA", var16);
                }

                var53.initialize(1024);
                KeyPair var32 = var53.generateKeyPair();
                ElGamalKeyPairGenerator var38 = new ElGamalKeyPairGenerator();
                BigInteger var47;
                ElGamalParameters var48;
                BigInteger var50;
                if (var0 == 8192) {
                    var47 = new BigInteger(F);
                    var50 = new BigInteger(E, 16);
                    var48 = new ElGamalParameters(var50, var47);
                    var38.init(new ElGamalKeyGenerationParameters(IOUtil.getSecureRandom(), var48));
                } else if (var0 == 6144) {
                    var47 = new BigInteger(D);
                    var50 = new BigInteger(C, 16);
                    var48 = new ElGamalParameters(var50, var47);
                    var38.init(new ElGamalKeyGenerationParameters(IOUtil.getSecureRandom(), var48));
                } else if (var0 == 4096) {
                    var47 = new BigInteger(B);
                    var50 = new BigInteger(A, 16);
                    var48 = new ElGamalParameters(var50, var47);
                    var38.init(new ElGamalKeyGenerationParameters(IOUtil.getSecureRandom(), var48));
                } else if (var0 == 3072) {
                    var47 = new BigInteger(z);
                    var50 = new BigInteger(y, 16);
                    var48 = new ElGamalParameters(var50, var47);
                    var38.init(new ElGamalKeyGenerationParameters(IOUtil.getSecureRandom(), var48));
                } else if (var0 == 2048) {
                    var47 = new BigInteger(x, 16);
                    var50 = new BigInteger(w, 16);
                    var48 = new ElGamalParameters(var50, var47);
                    var38.init(new ElGamalKeyGenerationParameters(IOUtil.getSecureRandom(), var48));
                } else if (var0 == 1536) {
                    var47 = new BigInteger(v, 16);
                    var50 = new BigInteger(u, 16);
                    var48 = new ElGamalParameters(var50, var47);
                    var38.init(new ElGamalKeyGenerationParameters(IOUtil.getSecureRandom(), var48));
                } else {
                    var47 = new BigInteger(t, 16);
                    var50 = new BigInteger(s, 16);
                    var48 = new ElGamalParameters(var50, var47);
                    var38.init(new ElGamalKeyGenerationParameters(IOUtil.getSecureRandom(), var48));
                }

                var43 = var38.generateKeyPair();

                try {
                    var49 = new JcaPGPKeyPair(17, var32, new Date());
                    BcPGPKeyPair var56 = new BcPGPKeyPair(16, var43, new Date());
                    (var30 = new PGPSignatureSubpacketGenerator()).setKeyExpirationTime(false, var7 * 24L * 60L * 60L);
                    var30.setKeyFlags(false, 3);
                    var30.setPreferredSymmetricAlgorithms(false, var41);
                    var30.setPreferredHashAlgorithms(false, var39);
                    var30.setPreferredCompressionAlgorithms(false, var34);
                    var30.setPrimaryUserID(false, true);
                    var37 = (new JcaPGPDigestCalculatorProviderBuilder()).build().get(2);
                    BcPGPContentSignerBuilder var55 = new BcPGPContentSignerBuilder(var49.getPublicKey().getAlgorithm(), 2);
                    if (var0 >= 2048) {
                        var55 = new BcPGPContentSignerBuilder(var49.getPublicKey().getAlgorithm(), 8);
                    }

                    a(var21 = new PGPKeyRingGenerator(19, var49, var1, var37, var30.generate(), (PGPSignatureSubpacketVector)null, var55, (new BcPBESecretKeyEncryptorBuilder(9, var37)).build(var3.toCharArray())), var56, var41, var39, var34, var7);
                    return var21;
                } catch (org.spongycastle.openpgp.PGPException var14) {
                    throw IOUtil.newPGPException(var14);
                }
            } else if (KeyAlgorithm.RSA == var2) {
                try {
                    var53 = KeyPairGenerator.getInstance("RSA", "SC");
                } catch (NoSuchProviderException var18) {
                    throw new PGPException("No Such provider: SC", var18);
                } catch (NoSuchAlgorithmException var19) {
                    throw new PGPException("No Such Algorithm: " + var19.getMessage(), var19);
                }

                SecureRandom var29 = new SecureRandom();
                var53.initialize(var0, var29);
                KeyPair var35 = var53.genKeyPair();
                KeyPair var44 = var53.genKeyPair();

                try {
                    var49 = new JcaPGPKeyPair(a(KeyAlgorithm.RSA), var35, new Date());
                    JcaPGPKeyPair var45 = new JcaPGPKeyPair(a(KeyAlgorithm.RSA), var44, new Date());
                    var30 = new PGPSignatureSubpacketGenerator();
                    if (var7 > 0L) {
                        var30.setKeyExpirationTime(false, var7 * 24L * 60L * 60L);
                    }

                    var30.setKeyFlags(false, 3);
                    var30.setPreferredSymmetricAlgorithms(false, var41);
                    var30.setPreferredHashAlgorithms(false, var39);
                    var30.setPreferredCompressionAlgorithms(false, var34);
                    var30.setPrimaryUserID(false, true);
                    var37 = (new JcaPGPDigestCalculatorProviderBuilder()).build().get(2);
                    PGPKeyRingGenerator var54;
                    a(var54 = new PGPKeyRingGenerator(19, var49, var1, var37, var30.generate(), (PGPSignatureSubpacketVector)null, new BcPGPContentSignerBuilder(var49.getPublicKey().getAlgorithm(), 2), (new BcPBESecretKeyEncryptorBuilder(9, var37)).build(var3.toCharArray())), var45, var41, var39, var34, var7);
                    return var54;
                } catch (org.spongycastle.openpgp.PGPException var17) {
                    throw IOUtil.newPGPException(var17);
                }
            } else {
                String var28 = "RSA, ELGAMAL, EC";
                String var22 = "keyAlgorighm";
                throw new PGPException("Wrong value for parameter " + var22 + ": " + var2 + ". Must be one of " + var28);
            }
        } else {
            ECKeyPairGenerator var51 = new ECKeyPairGenerator();
            X9ECParameters var24 = NISTNamedCurves.getByName("P-256");
            String var27 = "P-256";
            if (var0 > 256 && var0 < 521) {
                var24 = NISTNamedCurves.getByName("P-384");
                var27 = "P-384";
            } else if (var0 >= 521) {
                var24 = NISTNamedCurves.getByName("P-521");
                var27 = "P-521";
            }

            try {
                SecureRandom var42 = IOUtil.getSecureRandom();
                ECNamedDomainParameters var12 = new ECNamedDomainParameters(NISTNamedCurves.getOID(var27), var24.getCurve(), var24.getG(), var24.getN());
                var51.init(new ECKeyGenerationParameters(var12, var42));
                var43 = var51.generateKeyPair();
                AsymmetricCipherKeyPair var25 = var51.generateKeyPair();
                BcPGPKeyPair var33 = new BcPGPKeyPair(19, var43, new Date());
                BcPGPKeyPair var52 = new BcPGPKeyPair(18, var25, new Date());
                PGPSignatureSubpacketGenerator var20 = new PGPSignatureSubpacketGenerator();
                if (var7 > 0L) {
                    var20.setKeyExpirationTime(false, var7 * 24L * 60L * 60L);
                }

                var20.setKeyFlags(false, 3);
                var20.setPreferredSymmetricAlgorithms(false, var41);
                var20.setPreferredHashAlgorithms(false, var39);
                var20.setPreferredCompressionAlgorithms(false, var34);
                var20.setPrimaryUserID(false, true);
                PGPDigestCalculator var26 = (new BcPGPDigestCalculatorProvider()).get(2);
                a(var21 = new PGPKeyRingGenerator(KeyStore.KeyCertificationType.PositiveCertification.getValue(), var33, var1, var26, var20.generate(), (PGPSignatureSubpacketVector)null, new BcPGPContentSignerBuilder(var33.getPublicKey().getAlgorithm(), 10), (new BcPBESecretKeyEncryptorBuilder(9, var26)).build(var3.toCharArray())), var52, var41, var39, var34, var7);
                return var21;
            } catch (org.spongycastle.openpgp.PGPException var13) {
                throw IOUtil.newPGPException(var13);
            }
        }
    }

    private static PGPKeyRingGenerator a(EcCurve var0, EcCurve var1, String var2, String var3, CompressionAlgorithm[] var4, HashAlgorithm[] var5, CypherAlgorithm[] var6, long var7, int var9) throws PGPException {
        int[] var10 = new int[var6.length];

        for(int var11 = 0; var11 < var6.length; ++var11) {
            var10[var11] = var6[var11].intValue();
        }

        int[] var30 = new int[var5.length];

        for(int var26 = 0; var26 < var5.length; ++var26) {
            var30[var26] = var5[var26].intValue();
        }

        int[] var29 = new int[var4.length];

        for(int var24 = 0; var24 < var29.length; ++var24) {
            var29[var24] = var4[var24].intValue();
        }

        ECKeyPairGenerator var25 = new ECKeyPairGenerator();
        ASN1ObjectIdentifier var22 = b(var0);
        X9ECParameters var17 = a(var0);
        ASN1ObjectIdentifier var12 = b(var1);
        X9ECParameters var20 = a(var1);

        try {
            SecureRandom var13 = IOUtil.getSecureRandom();
            ECNamedDomainParameters var14 = new ECNamedDomainParameters(var22, var17.getCurve(), var17.getG(), var17.getN());
            var25.init(new ECKeyGenerationParameters(var14, var13));
            AsymmetricCipherKeyPair var31 = var25.generateKeyPair();
            ECNamedDomainParameters var15 = new ECNamedDomainParameters(var12, var20.getCurve(), var20.getG(), var20.getN());
            var25.init(new ECKeyGenerationParameters(var15, var13));
            AsymmetricCipherKeyPair var27 = var25.generateKeyPair();
            org.spongycastle.openpgp.PGPKeyPair var18 = a(19, var31, var22, var17);
            org.spongycastle.openpgp.PGPKeyPair var21 = a(18, var27, var12, var20);
            PGPSignatureSubpacketGenerator var23 = new PGPSignatureSubpacketGenerator();
            if (var7 > 0L) {
                var23.setKeyExpirationTime(false, var7 * 24L * 60L * 60L);
            }

            var23.setKeyFlags(false, 3);
            var23.setPreferredSymmetricAlgorithms(false, var10);
            var23.setPreferredHashAlgorithms(false, var30);
            var23.setPreferredCompressionAlgorithms(false, var29);
            var23.setPrimaryUserID(false, true);
            PGPDigestCalculator var28 = (new BcPGPDigestCalculatorProvider()).get(2);
            PGPKeyRingGenerator var19 = new PGPKeyRingGenerator(var9, var18, var2, var28, var23.generate(), (PGPSignatureSubpacketVector)null, new BcPGPContentSignerBuilder(var18.getPublicKey().getAlgorithm(), 10), (new BcPBESecretKeyEncryptorBuilder(9, var28)).build(var3.toCharArray()));
            if (var21 != null) {
                a(var19, var21, var10, var30, var29, var7);
            }

            return var19;
        } catch (org.spongycastle.openpgp.PGPException var16) {
            throw IOUtil.newPGPException(var16);
        }
    }

    private static org.spongycastle.openpgp.PGPKeyPair a(int var0, AsymmetricCipherKeyPair var1, ASN1ObjectIdentifier var2, X9ECParameters var3) throws PGPException {
        SubjectPublicKeyInfo var4;
        try {
            var4 = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(var1.getPublic());
        } catch (IOException var6) {
            throw new PGPException("Unable to encode key: " + var6.getMessage(), var6);
        }

        DEROctetString var10 = new DEROctetString(var4.getPublicKeyData().getBytes());
        X9ECPoint var9 = new X9ECPoint(var3.getCurve(), var10);
        Object var8;
        if (var0 == 18) {
            PGPKdfParameters var11 = new PGPKdfParameters(8, 7);
            var8 = new ECDHPublicBCPGKey(var2, var9.getPoint(), var11.getHashAlgorithm(), var11.getSymmetricWrapAlgorithm());
        } else {
            if (var0 != 19) {
                throw new PGPException("unknown EC algorithm");
            }

            var8 = new ECDSAPublicBCPGKey(var2, var9.getPoint());
        }

        try {
            PGPPublicKey var12 = new PGPPublicKey(new PublicKeyPacket(var0, new Date(), (BCPGKey)var8), new BcKeyFingerprintCalculator());
            PGPPrivateKey var7 = (new BcPGPKeyConverter()).getPGPPrivateKey(var12, var1.getPrivate());
            return new org.spongycastle.openpgp.PGPKeyPair(var12, var7);
        } catch (org.spongycastle.openpgp.PGPException var5) {
            throw new PGPException(var5.getMessage(), var5);
        }
    }

    private static X9ECParameters a(EcCurve var0) {
        switch (var0) {
            case P256:
                return NISTNamedCurves.getByName("P-256");
            case P384:
                return NISTNamedCurves.getByName("P-384");
            case P521:
                return NISTNamedCurves.getByName("P-521");
            case Brainpool256:
                return TeleTrusTNamedCurves.getByOID(TeleTrusTObjectIdentifiers.brainpoolP256r1);
            case Brainpool384:
                return TeleTrusTNamedCurves.getByOID(TeleTrusTObjectIdentifiers.brainpoolP384r1);
            case Brainpool512:
                return TeleTrusTNamedCurves.getByOID(TeleTrusTObjectIdentifiers.brainpoolP512r1);
            default:
                return NISTNamedCurves.getByName("P-256");
        }
    }

    private static ASN1ObjectIdentifier b(EcCurve var0) {
        switch (var0) {
            case P256:
                return NISTNamedCurves.getOID("P-256");
            case P384:
                return NISTNamedCurves.getOID("P-384");
            case P521:
                return NISTNamedCurves.getOID("P-521");
            case Brainpool256:
                return TeleTrusTObjectIdentifiers.brainpoolP256r1;
            case Brainpool384:
                return TeleTrusTObjectIdentifiers.brainpoolP384r1;
            case Brainpool512:
                return TeleTrusTObjectIdentifiers.brainpoolP512r1;
            default:
                return NISTNamedCurves.getOID("P-256");
        }
    }

    public void exportKeyRing(String var1, String var2) throws NoPublicKeyFoundException, IOException {
        this.exportKeyRing(var1, var2, true);
    }

    public void exportPubring(String var1) throws IOException {
        FileOutputStream var2 = null;
        boolean var4 = false;

        try {
            var4 = true;
            var2 = new FileOutputStream(var1);
            this.f.encode(var2);
            var4 = false;
        } finally {
            if (var4) {
                if (var2 != null) {
                    var2.close();
                }

            }
        }

        var2.close();
    }

    public void exportSecring(String var1) throws IOException {
        FileOutputStream var2 = null;
        boolean var4 = false;

        try {
            var4 = true;
            var2 = new FileOutputStream(var1);
            this.a.encode(var2);
            var4 = false;
        } finally {
            if (var4) {
                if (var2 != null) {
                    var2.close();
                }

            }
        }

        var2.close();
    }

    public void exportKeyRing(String var1, String var2, boolean var3) throws NoPublicKeyFoundException, IOException {
        FileOutputStream var4 = null;

        try {
            var4 = new FileOutputStream(var1);
            this.exportKeyRing((OutputStream)var4, var2, var3);
            return;
        } catch (Exception var7) {
        } finally {
            IOUtil.closeStream(var4);
            IOUtil.closeStream((OutputStream)null);
        }

        (new File(var1)).delete();
    }

    public void exportKeyRing(OutputStream var1, String var2, boolean var3) throws NoPublicKeyFoundException, IOException {
        Object var4 = null;

        try {
            if (var3) {
                var4 = var1;
                var1 = new ArmoredOutputStream((OutputStream)var1);
                this.a((OutputStream)var1);
            }

            try {
                this.findSecretKeyRing(var2).encode((OutputStream)var1);
                if (var3) {
                    IOUtil.closeStream((OutputStream)var1);
                }
            } catch (NoPrivateKeyFoundException var9) {
            }

            if (var3) {
                var1 = new ArmoredOutputStream((OutputStream)var4);
                this.a((OutputStream)var1);
            }

            this.a(var2).encode((OutputStream)var1);
        } catch (IOException var10) {
            throw var10;
        } finally {
            if (var3) {
                IOUtil.closeStream((OutputStream)var1);
            }

        }

    }

    public void exportKeyRing(OutputStream var1, long var2, boolean var4) throws NoPublicKeyFoundException, IOException {
        Object var5 = null;

        try {
            if (var4) {
                var5 = var1;
                var1 = new ArmoredOutputStream((OutputStream)var1);
                this.a((OutputStream)var1);
            }

            try {
                this.findSecretKeyRing(var2).encode((OutputStream)var1);
                if (var4) {
                    IOUtil.closeStream((OutputStream)var1);
                }
            } catch (NoPrivateKeyFoundException var10) {
            }

            if (var4) {
                var1 = new ArmoredOutputStream((OutputStream)var5);
                this.a((OutputStream)var1);
            }

            this.a(var2).encode((OutputStream)var1);
        } catch (IOException var11) {
            throw var11;
        } finally {
            if (var4) {
                IOUtil.closeStream((OutputStream)var1);
            }

        }

    }

    public void exportKeyRing(String var1, long var2, boolean var4) throws NoPublicKeyFoundException, IOException {
        Object var5 = null;
        Object var6 = null;

        try {
            var5 = new FileOutputStream(var1);
            if (var4) {
                var6 = var5;
                var5 = new ArmoredOutputStream((OutputStream)var5);
                this.a((OutputStream)var5);
            }

            try {
                this.findSecretKeyRing(var2).encode((OutputStream)var5);
                if (var4) {
                    IOUtil.closeStream((OutputStream)var5);
                }
            } catch (NoPrivateKeyFoundException var10) {
            }

            if (var4) {
                var5 = new ArmoredOutputStream((OutputStream)var6);
                this.a((OutputStream)var5);
            }

            this.a(var2).encode((OutputStream)var5);
        } catch (IOException var11) {
            throw var11;
        } finally {
            IOUtil.closeStream((OutputStream)var5);
            IOUtil.closeStream((OutputStream)var6);
        }

    }

    public void exportPublicKey(String var1, String var2, boolean var3) throws NoPublicKeyFoundException, IOException {
        IOUtil.exportPublicKeyRing(this.a(var2), var1, var3, this.J);
    }

    public void exportPublicKey(OutputStream var1, String var2, boolean var3) throws NoPublicKeyFoundException, IOException {
        IOUtil.exportPublicKeyRing(this.a(var2), var1, var3, this.J);
    }

    public void exportPublicKey(OutputStream var1, long var2, boolean var4) throws NoPublicKeyFoundException, IOException {
        IOUtil.exportPublicKeyRing(this.a(var2), var1, var4, this.J);
    }

    public void exportPublicKey(String var1, long var2, boolean var4) throws NoPublicKeyFoundException, IOException {
        IOUtil.exportPublicKeyRing(this.a(var2), var1, var4, this.J);
    }

    public void exportPrivateKey(String var1, String var2, boolean var3) throws NoPrivateKeyFoundException, IOException {
        IOUtil.exportPrivateKey(this.findSecretKeyRing(var2), var1, var3, this.J);
    }

    public void exportPrivateKey(OutputStream var1, String var2, boolean var3) throws NoPrivateKeyFoundException, IOException {
        IOUtil.exportPrivateKey(this.findSecretKeyRing(var2), var1, var3, this.J);
    }

    public void exportPrivateKey(OutputStream var1, long var2, boolean var4) throws NoPrivateKeyFoundException, IOException {
        IOUtil.exportPrivateKey(this.findSecretKeyRing(var2), var1, var4, this.J);
    }

    public void exportPrivateKey(String var1, long var2, boolean var4) throws NoPrivateKeyFoundException, IOException {
        IOUtil.exportPrivateKey(this.findSecretKeyRing(var2), var1, var4, this.J);
    }

    /** @deprecated */
    public void importPublickKey(String var1) throws IOException, PGPException {
        this.importPublicKey(var1);
    }

    public KeyPairInformation importPublicKey(KeyPairInformation var1) {
        try {
            this.a(var1.getRawPublicKeyRing());
            if (this.f.contains(var1.getKeyID())) {
                this.f = PGPPublicKeyRingCollection.removePublicKeyRing(this.f, var1.getRawPublicKeyRing());
            }

            this.f = PGPPublicKeyRingCollection.addPublicKeyRing(this.f, var1.getRawPublicKeyRing());
            KeyPairInformation var2;
            (var2 = this.a()).setPublicKeyRing(var1.getRawPublicKeyRing());
            this.I.put(new Long(var1.getKeyID()), var2);
            return var2;
        } catch (org.spongycastle.openpgp.PGPException var3) {
            return null;
        }
    }

    public KeyPairInformation[] importPublicKey(String var1) throws IOException, PGPException, NoPublicKeyFoundException {
        List var2 = this.loadKeyFile(var1);
        return this.a(var2);
    }

    public KeyPairInformation[] importPublicKey(InputStream var1) throws IOException, PGPException, NoPublicKeyFoundException {
        List var2 = loadKeyStream(var1);
        return this.a(var2);
    }

    private KeyPairInformation[] a(List var1) throws IOException, PGPException, NoPublicKeyFoundException {
        LinkedList var2 = new LinkedList();
        boolean var3 = false;

        for(int var4 = 0; var4 < var1.size(); ++var4) {
            Object var5;
            if ((var5 = var1.get(var4)) instanceof PGPPublicKeyRing) {
                var3 = true;
                PGPPublicKeyRing var8 = (PGPPublicKeyRing)var5;

                try {
                    if (this.f.contains(var8.getPublicKey().getKeyID())) {
                        this.f = PGPPublicKeyRingCollection.removePublicKeyRing(this.f, var8);
                    }
                } catch (org.spongycastle.openpgp.PGPException var7) {
                }

                this.f = PGPPublicKeyRingCollection.addPublicKeyRing(this.f, var8);
                this.a(var8);
                KeyPairInformation var6;
                (var6 = this.a()).setPublicKeyRing(var8);
                var2.add(var6);
                a("Imported public key {0}", var6.getKeyIDHex());
            }
        }

        if (!var3) {
            throw new NoPublicKeyFoundException("No public key was found in the supplied source.");
        } else {
            if (this.d) {
                this.save();
            }

            return (KeyPairInformation[])var2.toArray(new KeyPairInformation[var2.size()]);
        }
    }

    public KeyPairInformation[] importGnuPgKbx(String var1) throws IOException, PGPException {
        FileInputStream var5 = new FileInputStream("C:\\Users\\me\\AppData\\Roaming\\gnupg\\pubring.kbx");

        KeyPairInformation[] var2;
        try {
            var2 = this.importGnuPgKbx((InputStream)var5);
        } finally {
            IOUtil.closeStream(var5);
        }

        return var2;
    }

    public KeyPairInformation[] importGnuPgKbx(InputStream var1) throws IOException, PGPException {
        KeyStore var2 = new KeyStore();
        var1 = var1;
        new KBXFirstBlob(var1);

        KBXDataBlob var3;
        while((var3 = KBXDataBlob.readFromStream(var1)) != null) {
            var2.importKeyRing(var3.Blob);
        }

        this.importKeyStore(var2);
        return var2.getKeys();
    }

    public KeyPairInformation[] importKeyRing(byte[] var1) throws IOException, PGPException {
        ByteArrayInputStream var5 = new ByteArrayInputStream(var1);

        KeyPairInformation[] var2;
        try {
            var2 = this.importKeyRing((InputStream)var5, (String)null);
        } finally {
            IOUtil.closeStream(var5);
        }

        return var2;
    }

    public KeyPairInformation[] importKeyRing(String var1) throws IOException, PGPException {
        return this.importKeyRing((String)var1, (String)null);
    }

    public KeyPairInformation[] importKeyStore(KeyStore var1) throws IOException, PGPException {
        ByteArrayInputStream var2 = new ByteArrayInputStream(var1.f.getEncoded());
        ByteArrayInputStream var9 = new ByteArrayInputStream(var1.a.getEncoded());
        KeyPairInformation[] var11 = this.importKeyRing((InputStream)var2);
        KeyPairInformation[] var10 = this.importKeyRing((InputStream)var9);
        ArrayList var3 = new ArrayList(var11.length + var10.length);
        boolean[] var4;
        Arrays.fill(var4 = new boolean[var10.length], false);

        int var5;
        for(var5 = 0; var5 < var11.length; ++var5) {
            KeyPairInformation var6 = var11[var5];

            for(int var7 = 0; var7 < var10.length; ++var7) {
                KeyPairInformation var8 = var10[var7];
                if (var6.getKeyID() == var8.getKeyID()) {
                    var4[var7] = true;
                    var6.setPrivateKeyRing(var8.getRawPrivateKeyRing());
                    break;
                }
            }

            var3.add(var6);
        }

        for(var5 = 0; var5 < var4.length; ++var5) {
            if (!var4[var5]) {
                var3.add(var10[var5]);
            }
        }

        return (KeyPairInformation[])var3.toArray(new KeyPairInformation[var3.size()]);
    }

    public KeyPairInformation[] importKeyRing(String var1, String var2) throws IOException, PGPException {
        FileInputStream var3 = null;

        KeyPairInformation[] var6;
        try {
            var3 = new FileInputStream(var1);
            var6 = this.importKeyRing((InputStream)var3, var2);
        } finally {
            IOUtil.closeStream(var3);
        }

        return var6;
    }

    public KeyPairInformation importKeyRing(KeyPairInformation var1) {
        try {
            this.a(var1.getRawPublicKeyRing());
            if (this.f.contains(var1.getKeyID())) {
                this.f = PGPPublicKeyRingCollection.removePublicKeyRing(this.f, var1.getRawPublicKeyRing());
            }

            this.f = PGPPublicKeyRingCollection.addPublicKeyRing(this.f, var1.getRawPublicKeyRing());
            if (this.a.contains(var1.getKeyID())) {
                this.a = PGPSecretKeyRingCollection.removeSecretKeyRing(this.a, var1.getRawPrivateKeyRing());
            }

            this.a = PGPSecretKeyRingCollection.addSecretKeyRing(this.a, var1.getRawPrivateKeyRing());
            this.I.put(new Long(var1.getKeyID()), var1);
            a("Imported key {0}", var1.getKeyIDHex());
            return var1;
        } catch (org.spongycastle.openpgp.PGPException var2) {
            return null;
        }
    }

    public KeyPairInformation[] importKeyRing(InputStream var1) throws IOException, PGPException {
        return this.importKeyRing((InputStream)var1, (String)null);
    }

    public KeyPairInformation[] importKeyRing(InputStream var1, String var2) throws IOException, PGPException {
        c("Importing OpePGP key ring.");
        InputStream var3 = var1;
        if (!(var1 instanceof ArmoredInputStream)) {
            var3 = PGPUtil.getDecoderStream(BaseLib.cleanGnuPGBackupKeys(var1));
        }

        HashMap var8 = new HashMap();
        BoolValue var4 = new BoolValue(false);
        if (var3 instanceof ArmoredInputStream) {
            ArmoredInputStream var9 = (ArmoredInputStream)var3;

            while(!var9.isEndOfStream()) {
                List var5 = this.parseKeyStream(var9, var2, var4);

                for(int var6 = 0; var6 < var5.size(); ++var6) {
                    KeyPairInformation var7 = (KeyPairInformation)var5.get(var6);
                    var8.put(var7.getKeyIDHex(), var7);
                    a("Imported key {0}", var7.getKeyIDHex());
                }

                if (var4.isValue()) {
                    break;
                }
            }
        } else {
            List var10 = this.parseKeyStream(var3, var2, var4);

            for(int var11 = 0; var11 < var10.size(); ++var11) {
                KeyPairInformation var12 = (KeyPairInformation)var10.get(var11);
                var8.put(var12.getKeyIDHex(), var12);
                a("Imported key {0}", var12.getKeyIDHex());
            }
        }

        if (this.d) {
            this.save();
        }

        return (KeyPairInformation[])var8.values().toArray(new KeyPairInformation[var8.size()]);
    }

    public KeyPairInformation[] importPrivateKey(String var1) throws IOException, PGPException, NoPrivateKeyFoundException {
        return this.importPrivateKey((String)var1, (String)null);
    }

    public KeyPairInformation[] importPrivateKey(String var1, String var2) throws IOException, PGPException, NoPrivateKeyFoundException {
        List var3 = this.loadKeyFile(var1);
        return this.a(var3, var2);
    }

    public KeyPairInformation[] importPrivateKey(InputStream var1) throws IOException, PGPException, NoPrivateKeyFoundException {
        List var2 = loadKeyStream(var1);
        return this.a((List)var2, (String)null);
    }

    public KeyPairInformation[] importPrivateKey(InputStream var1, String var2) throws IOException, PGPException, NoPrivateKeyFoundException {
        List var3 = loadKeyStream(var1);
        return this.a(var3, var2);
    }

    private KeyPairInformation[] a(List var1, String var2) throws IOException, PGPException, NoPrivateKeyFoundException {
        LinkedList var3 = new LinkedList();
        boolean var4 = false;

        for(int var5 = 0; var5 < var1.size(); ++var5) {
            Object var6;
            if ((var6 = var1.get(var5)) instanceof PGPSecretKeyRing) {
                var4 = true;
                PGPSecretKeyRing var12 = (PGPSecretKeyRing)var6;
                if (var2 != null && !this.isKeyPasswordConfirmed(var12, var2)) {
                    throw new WrongPasswordException("Secret key password is incorrect: " + var2);
                }

                try {
                    if (this.a.contains(var12.getPublicKey().getKeyID())) {
                        this.a = PGPSecretKeyRingCollection.removeSecretKeyRing(this.a, var12);
                    }
                } catch (org.spongycastle.openpgp.PGPException var10) {
                }

                this.a = PGPSecretKeyRingCollection.addSecretKeyRing(this.a, var12);
                ByteArrayOutputStream var7 = new ByteArrayOutputStream(1048576);
                Iterator var8 = var12.getSecretKeys();

                while(var8.hasNext()) {
                    PGPPublicKey var9;
                    if ((var9 = ((PGPSecretKey)var8.next()).getPublicKey()) != null) {
                        var7.write(var9.getEncoded());
                    }
                }

                PGPPublicKeyRing var14 = staticBCFactory.CreatePGPPublicKeyRing(var7.toByteArray());

                try {
                    if (!this.f.contains(var12.getPublicKey().getKeyID())) {
                        this.f = PGPPublicKeyRingCollection.addPublicKeyRing(this.f, var14);
                        this.a(var14);
                    }
                } catch (org.spongycastle.openpgp.PGPException var11) {
                }

                Long var15 = new Long(var12.getPublicKey().getKeyID());
                KeyPairInformation var13;
                if ((var13 = (KeyPairInformation)this.I.get(var15)) == null) {
                    (var13 = this.a()).setPublicKeyRing(var14);
                    this.I.put(var15, var13);
                }

                var13.setPrivateKeyRing(var12);
                a("Imported private key {0}", var13.getKeyIDHex());
                var3.add(var13);
            }
        }

        if (!var4) {
            throw new NoPrivateKeyFoundException("No private key was found in the supplied source");
        } else {
            if (this.d) {
                this.save();
            }

            return (KeyPairInformation[])var3.toArray(new KeyPairInformation[var3.size()]);
        }
    }

    public String getKeystoreFileName() {
        if (this.m instanceof AndroidContextFileKeyStorage) {
            return ((AndroidContextFileKeyStorage)this.m).getFileName();
        } else {
            return this.m instanceof FileKeyStorage ? ((FileKeyStorage)this.m).getFileName() : this.b;
        }
    }

    public IKeyStoreStorage getStorage() {
        return this.m;
    }

    public String getKeystorePassword() {
        return this.c;
    }

    public void setKeystorePassword(String var1) {
        this.c = var1;

        try {
            this.save();
        } catch (Exception var2) {
        }
    }

    public KeyPairInformation[] listKeys() {
        KeyPairInformation[] var1 = this.getKeys();
        System.out.print(a("Type", 8));
        System.out.print(a("Bits", 10));
        System.out.print(a("Key ID", 9));
        System.out.print(a("Date", 11));
        System.out.println("User ID");

        for(int var2 = 0; var2 < var1.length; ++var2) {
            KeyPairInformation var3 = var1[var2];
            System.out.print(a(var3.getAlgorithm(), 8));
            System.out.print(a(String.valueOf(var3.getKeySize()), 10));
            System.out.print(a(var3.getKeyIDHex(), 9));
            StringBuffer var4;
            (var4 = new StringBuffer()).append(var3.getCreationTime().getYear()).append('/').append(var3.getCreationTime().getMonth()).append('/').append(var3.getCreationTime().getDate());
            System.out.print(a(var4.toString(), 11));

            for(int var5 = 0; var5 < var3.getUserIDs().length; ++var5) {
                System.out.print(var3.getUserIDs()[var5]);
            }

            System.out.println();
        }

        return var1;
    }

    public KeyPairInformation getKey(String var1) {
        KeyPairInformation var2;
        return (var2 = (KeyPairInformation)this.I.get(new Long(this.getKeyIdForUserId(var1)))) == null ? (KeyPairInformation)this.I.get(new Long(this.getKeyIdForKeyIdHex(var1))) : var2;
    }

    public KeyPairInformation getKey(long var1) {
        if (this.I.get(new Long(var1)) != null) {
            return (KeyPairInformation)this.I.get(new Long(var1));
        } else {
            try {
                PGPPublicKeyRing var4 = this.f.getPublicKeyRing(var1);
                return (KeyPairInformation)this.I.get(new Long(var4.getPublicKey().getKeyID()));
            } catch (org.spongycastle.openpgp.PGPException var3) {
                return null;
            }
        }
    }

    public KeyPairInformation[] getKeys() {
        return (KeyPairInformation[])this.I.values().toArray(new KeyPairInformation[this.I.size()]);
    }

    public KeyPairInformation[] getKeys(String var1) {
        LinkedList var2 = new LinkedList();
        Iterator var4 = this.b(var1).iterator();

        while(var4.hasNext()) {
            PGPPublicKeyRing var3 = (PGPPublicKeyRing)var4.next();
            var2.add(this.I.get(new Long(var3.getPublicKey().getKeyID())));
        }

        return (KeyPairInformation[])var2.toArray(new KeyPairInformation[var2.size()]);
    }

    private void a(PGPPublicKeyRing var1) {
        KeyPairInformation var2;
        if ((var2 = (KeyPairInformation)this.I.get(new Long(var1.getPublicKey().getKeyID()))) == null) {
            var2 = this.a();
            this.I.put(new Long(var1.getPublicKey().getKeyID()), var2);
        }

        var2.setPublicKeyRing(var1);
        Iterator var6 = var1.getPublicKeys();

        while(var6.hasNext()) {
            PGPPublicKey var7;
            String var3 = KeyPairInformation.keyId2Hex((var7 = (PGPPublicKey)var6.next()).getKeyID());
            if (this.H.get(var3) == null) {
                LinkedList var4;
                (var4 = new LinkedList()).add(new Long(var7.getKeyID()));
                this.H.put(var3, var4);
            } else {
                ((List)this.H.get(var3)).add(new Long(var7.getKeyID()));
            }

            Iterator var8 = var7.getUserIDs();

            while(var8.hasNext()) {
                var3 = (String)var8.next();
                if (this.G.get(var3) == null) {
                    LinkedList var5;
                    (var5 = new LinkedList()).add(new Long(var7.getKeyID()));
                    this.G.put(var3, var5);
                } else {
                    ((List)this.G.get(var3)).add(new Long(var7.getKeyID()));
                }
            }
        }

    }

    private void a(PGPPublicKey var1) {
        Iterator var2 = var1.getUserIDs();

        while(true) {
            String var3;
            List var4;
            do {
                if (!var2.hasNext()) {
                    String var6 = KeyPairInformation.keyId2Hex(var1.getKeyID());
                    List var7;
                    if ((var7 = (List)this.H.get(var6)) != null) {
                        for(int var8 = 0; var8 < var7.size(); ++var8) {
                            if ((Long)var7.get(var8) == var1.getKeyID()) {
                                var7.remove(var8);
                            }

                            if (var7.size() == 0) {
                                this.H.remove(var6);
                            }
                        }
                    }

                    return;
                }

                var3 = (String)var2.next();
            } while((var4 = (List)this.G.get(var3)) == null);

            for(int var5 = 0; var5 < var4.size(); ++var5) {
                if ((Long)var4.get(var5) == var1.getKeyID()) {
                    var4.remove(var5);
                }

                if (var4.size() == 0) {
                    this.G.remove(var3);
                }
            }
        }
    }

    private void b(PGPPublicKeyRing var1) {
        this.I.remove(new Long(var1.getPublicKey().getKeyID()));
        Iterator var3 = var1.getPublicKeys();

        while(var3.hasNext()) {
            PGPPublicKey var2 = (PGPPublicKey)var3.next();
            this.a(var2);
        }

    }

    private void a(PGPSecretKeyRing var1) {
        boolean var2 = false;
        Iterator var3 = var1.getSecretKeys();

        while(var3.hasNext()) {
            PGPSecretKey var4 = (PGPSecretKey)var3.next();

            try {
                if (!this.f.contains(var4.getKeyID())) {
                    var2 = true;
                    this.a(var4.getPublicKey());
                }
            } catch (org.spongycastle.openpgp.PGPException var5) {
            }
        }

        Long var6 = new Long(var1.getPublicKey().getKeyID());
        if (var2) {
            this.I.remove(var6);
        } else {
            ((KeyPairInformation)this.I.get(var6)).setPrivateKeyRing((PGPSecretKeyRing)null);
        }
    }

    protected void onLoadKeys() throws PGPException {
        this.H.clear();
        this.I.clear();
        this.G.clear();
        HashSet var1 = new HashSet();
        Iterator var2 = this.f.getKeyRings();

        KeyPairInformation var4;
        while(var2.hasNext()) {
            PGPPublicKeyRing var3 = (PGPPublicKeyRing)var2.next();
            (var4 = this.a()).setPublicKeyRing(var3);
            this.I.put(new Long(var3.getPublicKey().getKeyID()), var4);
            this.a(var4.getRawPublicKeyRing());
        }

        var2 = this.a.getKeyRings();

        while(var2.hasNext()) {
            PGPSecretKeyRing var11 = (PGPSecretKeyRing)var2.next();
            if (this.I.containsKey(new Long(var11.getPublicKey().getKeyID()))) {
                var4 = (KeyPairInformation)this.I.get(new Long(var11.getPublicKey().getKeyID()));
            } else {
                var4 = this.a();
                ArrayList var5 = new ArrayList();
                Iterator var6 = var11.getSecretKeys();

                while(var6.hasNext()) {
                    PGPSecretKey var7;
                    if ((var7 = (PGPSecretKey)var6.next()).getPublicKey() != null) {
                        var5.add(var7.getPublicKey());
                    }
                }

                ByteArrayOutputStream var12 = new ByteArrayOutputStream();

                for(int var13 = 0; var13 != var5.size(); ++var13) {
                    PGPPublicKey var8 = (PGPPublicKey)var5.get(var13);
                    var1.add(new Long(var8.getKeyID()));

                    try {
                        var8.encode(var12);
                    } catch (IOException var10) {
                        throw new PGPException(var10.getMessage(), var10);
                    }
                }

                PGPPublicKeyRing var14;
                try {
                    var14 = staticBCFactory.CreatePGPPublicKeyRing(var12.toByteArray());
                } catch (IOException var9) {
                    throw new PGPException(var9.getMessage(), var9);
                }

                var4.setPublicKeyRing(var14);
                this.a(var4.getRawPublicKeyRing());
            }

            var4.setPrivateKeyRing(var11);
            this.I.put(new Long(var11.getPublicKey().getKeyID()), var4);
        }

    }

    private static PGPPublicKey a(PGPPublicKey var0, long var1, int var3) {
        Iterator var5 = var0.getSignaturesOfType(16);

        while(var5.hasNext()) {
            PGPSignature var4;
            if ((var4 = (PGPSignature)var5.next()).getKeyID() == var1) {
                var0 = PGPPublicKey.removeCertification(var0, var4);
            }
        }

        return var0;
    }

    private void a(PGPPublicKeyRing var1, PGPPublicKey var2, String var3, PGPSecretKey var4, String var5) throws PGPException {
        long var10001 = var4.getKeyID();
        boolean var6 = true;
        long var9 = var10001;
        Iterator var13 = var2.getSignaturesOfType(16);

        boolean var10000;
        while(true) {
            if (var13.hasNext()) {
                if (((PGPSignature)var13.next()).getKeyID() != var9) {
                    continue;
                }

                var10000 = true;
                break;
            }

            var10000 = false;
            break;
        }

        if (!var10000) {
            if (!this.isKeyPasswordConfirmed(var4, var5)) {
                throw new WrongPasswordException("Secret key password is incorrect: " + var5);
            } else {
                try {
                    PGPSignatureGenerator var14 = staticBCFactory.CreatePGPSignatureGenerator(var4.getPublicKey().getAlgorithm(), 2);
                    staticBCFactory.initSign(var14, 16, BaseLib.extractPrivateKey(var4, var5.toCharArray()));
                    PGPSignature var12 = var14.generateCertification(var3, var2);
                    var2 = PGPPublicKey.addCertification(var2, var3, var12);
                } catch (Exception var11) {
                    throw new PGPException("exception creating signature: " + var11, var11);
                }

                var1 = PGPPublicKeyRing.insertPublicKey(PGPPublicKeyRing.removePublicKey(var1, var2), var2);
                this.replacePublicKeyRing(var1);
            }
        }
    }

    private void b(PGPPublicKeyRing var1, PGPPublicKey var2, String var3, PGPSecretKey var4, String var5) throws PGPException {
        long var10001 = var2.getKeyID();
        long var13 = var4.getKeyID();
        long var11 = var10001;
        PGPPublicKeyRing var6;
        PGPPublicKey var7 = (var6 = this.a(var11)).getPublicKey();
        Iterator var19 = var6.getPublicKey().getSignaturesOfType(16);

        boolean var10000;
        while(true) {
            if (var19.hasNext()) {
                PGPSignature var8;
                PGPSignatureSubpacketVector var9;
                if ((var8 = (PGPSignature)var19.next()).getKeyID() == var7.getKeyID() || !var8.hasSubpackets() || (var9 = var8.getHashedSubPackets()) == null || var9.getSubpacket(5) == null || var8.getKeyID() != var13) {
                    continue;
                }

                var10000 = true;
                break;
            }

            var10000 = false;
            break;
        }

        if (!var10000) {
            if (!this.isKeyPasswordConfirmed(var4, var5)) {
                throw new WrongPasswordException("Secret key password is incorrect: " + var5);
            } else {
                try {
                    PGPSignatureGenerator var20 = staticBCFactory.CreatePGPSignatureGenerator(var4.getPublicKey().getAlgorithm(), 2);
                    staticBCFactory.initSign(var20, 16, BaseLib.extractPrivateKey(var4, var5));
                    PGPSignatureSubpacketGenerator var16;
                    (var16 = new PGPSignatureSubpacketGenerator()).setSignatureCreationTime(false, new Date());
                    var16.setTrust(false, 1, 120);
                    var20.setHashedSubpackets(var16.generate());
                    PGPSignatureSubpacketGeneratorExtended var17;
                    (var17 = new PGPSignatureSubpacketGeneratorExtended()).setIssuerKeyID(false, var4.getKeyID());
                    var20.setUnhashedSubpackets(var17.generate());
                    PGPSignature var18 = var20.generateCertification(var3, var2);
                    var2 = PGPPublicKey.addCertification(a(var2, var4.getKeyID(), 16), var3, var18);
                } catch (org.spongycastle.openpgp.PGPException var15) {
                    throw IOUtil.newPGPException(var15);
                }

                var1 = PGPPublicKeyRing.insertPublicKey(PGPPublicKeyRing.removePublicKey(var1, var2), var2);
                this.replacePublicKeyRing(var1);
            }
        }
    }

    public boolean containsPublicKey(String var1) {
        return this.b(var1).size() > 0;
    }

    public boolean containsPrivateKey(String var1) {
        return this.getSecretKeyRingCollection(var1).size() > 0;
    }

    public boolean containsKey(String var1) {
        if (this.b(var1).size() > 0) {
            return true;
        } else if (this.getSecretKeyRingCollection(var1).size() > 0) {
            return true;
        } else if (q.matcher(var1).matches()) {
            var1 = d(var1);
            return this.H.containsKey(var1);
        } else {
            if (r.matcher(var1).matches()) {
                var1 = d(var1);
                long var5 = Long.decode("0x" + var1);

                try {
                    if (this.f.contains(var5) || this.a.contains(var5)) {
                        return true;
                    }
                } catch (Exception var7) {
                }
            }

            return false;
        }
    }

    public boolean containsKey(long var1) {
        if (this.c(var1) != null) {
            return true;
        } else {
            try {
                return this.e(var1) != null;
            } catch (NoPrivateKeyFoundException var4) {
                return false;
            }
        }
    }

    public boolean containsPrivateKey(long var1) {
        try {
            return this.e(var1) != null;
        } catch (NoPrivateKeyFoundException var3) {
            return false;
        }
    }

    public boolean containsPublicKey(long var1) {
        try {
            boolean var4 = this.f.getPublicKeyRing(var1) != null;
            return var4;
        } catch (org.spongycastle.openpgp.PGPException var3) {
            return false;
        }
    }

    public void setAutoSave(boolean var1) {
        this.d = var1;
    }

    public boolean isAutoSave() {
        return this.d;
    }

    public boolean isBackupOnSave() {
        return this.e;
    }

    public void setBackupOnSave(boolean var1) {
        this.e = var1;
    }

    public void save() throws PGPException {
        for(int var1 = 0; var1 < this.N.size(); ++var1) {
            ((IKeyStoreSaveListener)this.N.get(var1)).onSave(this);
        }

        OutputStream var7 = null;

        try {
            if ((var7 = this.m.getOutputStream()) != null) {
                if (this.c != null && !"".equals(this.c)) {
                    this.saveToStream(var7, this.c);
                } else {
                    this.saveToStream(var7);
                }
            }
        } catch (IOException var5) {
            throw new PGPException("exception saving key store", var5);
        } finally {
            IOUtil.closeStream(var7);
        }

    }

    protected void store(OutputStream var1, String var2) throws PGPException, IOException {
        PGPEncryptedDataGenerator var3 = staticBCFactory.CreatePGPEncryptedDataGenerator(9, true, IOUtil.getSecureRandom());

        try {
            var3.addMethod(staticBCFactory.CreatePBEKeyEncryptionMethodGenerator(var2));
            a(var1 = var3.open(var1, new byte[1024]), "pubring.gpg", this.g, this.f.getEncoded());
            a(var1, "secring.gpg", this.h, this.a.getEncoded());
            var1.close();
        } catch (org.spongycastle.openpgp.PGPException var4) {
            throw IOUtil.newPGPException(var4);
        }
    }

    private static byte[] a(PGPLiteralData var0) throws IOException {
        ByteArrayOutputStream var1 = new ByteArrayOutputStream();
        InputStream var3 = var0.getInputStream();

        int var2;
        while((var2 = var3.read()) >= 0) {
            var1.write(var2);
        }

        return var1.toByteArray();
    }

    private static void a(OutputStream var0, String var1, Date var2, byte[] var3) throws IOException {
        PGPLiteralDataGenerator var4;
        (var0 = (var4 = new PGPLiteralDataGenerator()).open(var0, 'b', var1, (long)var3.length, var2)).write(var3);
        var4.close();
        var0.close();
    }

    final PGPPublicKeyRing a(String var1) throws NoPublicKeyFoundException {
        Collection var2;
        if ((var2 = this.b(var1)).size() == 0 && this.O.size() > 0) {
            String var5 = "";
            String var3 = "";
            if (!q.matcher(var1).matches() && !r.matcher(var1).matches()) {
                var5 = var1;
            } else {
                var3 = var1;
            }

            for(int var4 = 0; var4 < this.O.size(); ++var4) {
                ((IKeyStoreSearchListener)this.O.get(var4)).onKeyNotFound(this, true, 0L, var3, var5);
            }

            var2 = this.b(var1);
        }

        if (var2.size() > 0) {
            return (PGPPublicKeyRing)var2.iterator().next();
        } else {
            throw new NoPublicKeyFoundException("No key found for userID: " + var1);
        }
    }

    final PGPPublicKeyRing a(long var1) throws NoPublicKeyFoundException {
        PGPPublicKeyRing var3;
        if ((var3 = this.c(var1)) == null && this.O.size() > 0) {
            for(int var4 = 0; var4 < this.O.size(); ++var4) {
                ((IKeyStoreSearchListener)this.O.get(var4)).onKeyNotFound(this, true, var1, KeyPairInformation.keyId2Hex(var1), "");
            }

            var3 = this.c(var1);
        }

        if (var3 == null) {
            throw new NoPublicKeyFoundException("no key found matching keyID: " + var1);
        } else {
            return var3;
        }
    }

    private Collection b(String var1) {
        try {
            ArrayList var2 = new ArrayList();
            HashSet var3 = new HashSet();
            Iterator var4 = this.f.getKeyRings(var1, this.k, !this.j);

            PGPPublicKeyRing var5;
            while(var4.hasNext()) {
                var5 = (PGPPublicKeyRing)var4.next();
                var3.add(new Long(var5.getPublicKey().getKeyID()));
                var2.add(var5);
            }

            var4 = this.a.getKeyRings(var1, this.k, !this.j);

            while(true) {
                PGPSecretKeyRing var11;
                do {
                    if (!var4.hasNext()) {
                        if (var2.size() == 0 && (q.matcher(var1).matches() || r.matcher(var1).matches())) {
                            String var10 = d(var1);
                            if ((var5 = this.c(this.getKeyIdForKeyIdHex(var10))) != null) {
                                var2.add(var5);
                            }
                        }

                        return var2;
                    }

                    var11 = (PGPSecretKeyRing)var4.next();
                } while(var3.contains(new Long(var11.getSecretKey().getKeyID())));

                ByteArrayOutputStream var6 = new ByteArrayOutputStream();
                Iterator var12 = var11.getSecretKeys();

                while(var12.hasNext()) {
                    PGPSecretKey var7 = (PGPSecretKey)var12.next();
                    var6.write(var7.getPublicKey().getEncoded());
                }

                var2.add(staticBCFactory.CreatePGPPublicKeyRing(var6.toByteArray()));
            }
        } catch (IOException var8) {
            throw new IllegalStateException("unexpected exception on extraction: " + var8);
        } catch (org.spongycastle.openpgp.PGPException var9) {
            throw new IllegalStateException("unexpected exception: " + var9);
        }
    }

    private PGPPublicKeyRing c(long var1) {
        try {
            if (this.f.contains(var1)) {
                return this.f.getPublicKeyRing(var1);
            } else if (!this.a.contains(var1)) {
                return null;
            } else {
                ByteArrayOutputStream var3 = new ByteArrayOutputStream();
                Iterator var6 = this.a.getSecretKeyRing(var1).getSecretKeys();

                while(var6.hasNext()) {
                    PGPSecretKey var2 = (PGPSecretKey)var6.next();
                    var3.write(var2.getPublicKey().getEncoded());
                }

                return staticBCFactory.CreatePGPPublicKeyRing(var3.toByteArray());
            }
        } catch (IOException var4) {
            throw new IllegalStateException("unexpected exception on extraction: " + var4);
        } catch (org.spongycastle.openpgp.PGPException var5) {
            throw new IllegalStateException("unexpected exception: " + var5);
        }
    }

    protected List loadKeyFile(String var1) throws FileNotFoundException, IOException, PGPException {
        FileInputStream var2 = null;

        List var5;
        try {
            var5 = loadKeyStream(var2 = new FileInputStream(var1));
        } finally {
            IOUtil.closeStream(var2);
        }

        return var5;
    }

    protected List loadKeysFromDecodedStream(InputStream var1, BoolValue var2) throws PGPException, IOException {
        LinkedList var3 = new LinkedList();
        PGPObjectFactory2 var6;
        (var6 = new PGPObjectFactory2(var1)).setLoadingKey(true);

        try {
            for(Object var4 = var6.nextObject(); var4 != null; var4 = var6.nextObject()) {
                if (var4 instanceof PGPPublicKeyRing) {
                    PGPPublicKeyRing var7 = (PGPPublicKeyRing)var4;
                    var3.add(var7);
                } else if (var4 instanceof PGPSecretKeyRing) {
                    PGPSecretKeyRing var8 = (PGPSecretKeyRing)var4;
                    var3.add(var8);
                } else if (!(var4 instanceof ExperimentalPacket) && !(var4 instanceof PGPOnePassSignatureList) && !(var4 instanceof PGPSignatureList)) {
                    throw new PGPException("Unexpected object found in stream: " + var4.getClass().getName());
                }
            }
        } catch (UnknownKeyPacketsException var5) {
            var2.setValue(true);
        }

        return var3;
    }

    protected List parseKeyStream(InputStream var1, String var2, BoolValue var3) throws PGPException, IOException {
        LinkedList var4 = new LinkedList();
        PGPObjectFactory2 var11;
        (var11 = new PGPObjectFactory2(var1)).setLoadingKey(true);

        try {
            for(Object var5 = var11.nextObject(); var5 != null; var5 = var11.nextObject()) {
                if (var5 instanceof PGPPublicKeyRing) {
                    PGPPublicKeyRing var13 = (PGPPublicKeyRing)var5;
                    if (this.f.contains(var13.getPublicKey().getKeyID())) {
                        this.f = PGPPublicKeyRingCollection.removePublicKeyRing(this.f, var13);
                    }

                    this.f = PGPPublicKeyRingCollection.addPublicKeyRing(this.f, var13);
                    this.a(var13);
                    Long var14 = new Long(var13.getPublicKey().getKeyID());
                    KeyPairInformation var16;
                    if ((var16 = (KeyPairInformation)this.I.get(var14)) == null) {
                        var16 = this.a();
                        this.I.put(var14, var16);
                    }

                    var16.setPublicKeyRing(var13);
                    var4.add(var16);
                } else if (!(var5 instanceof PGPSecretKeyRing)) {
                    if (!(var5 instanceof ExperimentalPacket) && !(var5 instanceof PGPOnePassSignatureList) && !(var5 instanceof PGPSignatureList) && !(var5 instanceof PGPEncryptedDataList)) {
                        throw new PGPException("Unexpected object found in stream: " + var5.getClass().getName());
                    }
                } else {
                    PGPSecretKeyRing var12 = (PGPSecretKeyRing)var5;
                    if (var2 != null && !this.isKeyPasswordConfirmed(var12, var2)) {
                        throw new WrongPasswordException("secret key password is incorrect");
                    }

                    if (this.a.contains(var12.getPublicKey().getKeyID())) {
                        this.a = PGPSecretKeyRingCollection.removeSecretKeyRing(this.a, var12);
                    }

                    this.a = PGPSecretKeyRingCollection.addSecretKeyRing(this.a, var12);
                    ByteArrayOutputStream var6 = new ByteArrayOutputStream(10240);
                    Iterator var7 = var12.getSecretKeys();

                    while(var7.hasNext()) {
                        PGPPublicKey var8;
                        if ((var8 = ((PGPSecretKey)var7.next()).getPublicKey()) != null) {
                            var6.write(var8.getEncoded());
                        }
                    }

                    PGPPublicKeyRing var15 = staticBCFactory.CreatePGPPublicKeyRing(var6.toByteArray());
                    if (!this.f.contains(var12.getPublicKey().getKeyID())) {
                        this.f = PGPPublicKeyRingCollection.addPublicKeyRing(this.f, var15);
                        this.a(var15);
                    }

                    KeyPairInformation var17;
                    if ((var17 = (KeyPairInformation)this.I.get(new Long(var12.getPublicKey().getKeyID()))) == null) {
                        (var17 = this.a()).setPublicKeyRing(var15);
                        this.I.put(new Long(var12.getPublicKey().getKeyID()), var17);
                    }

                    var17.setPrivateKeyRing(var12);
                    var4.add(var17);
                }
            }
        } catch (UnknownKeyPacketsException var9) {
            var3.setValue(true);
        } catch (org.spongycastle.openpgp.PGPException var10) {
            throw IOUtil.newPGPException(var10);
        }

        return var4;
    }

    private boolean d(long var1) throws PGPException {
        PGPPublicKeyRing var6;
        PGPPublicKey var2 = (var6 = this.a(var1)).getPublicKey();
        Iterator var7 = var6.getPublicKey().getSignaturesOfType(16);

        while(var7.hasNext()) {
            PGPSignature var3;
            if ((var3 = (PGPSignature)var7.next()).getKeyID() != var2.getKeyID() && var3.hasSubpackets() && var3.getHashedSubPackets().getSubpacket(5) != null) {
                try {
                    if (this.a.contains(var3.getKeyID())) {
                        return true;
                    }
                } catch (org.spongycastle.openpgp.PGPException var5) {
                    return false;
                }
            }
        }

        return false;
    }

    protected boolean isKeyPasswordConfirmed(PGPSecretKeyRing var1, String var2) {
        int var3 = 0;
        Iterator var6 = var1.getSecretKeys();

        while(var6.hasNext()) {
            PGPSecretKey var4 = (PGPSecretKey)var6.next();

            try {
                var4.extractPrivateKey(staticBCFactory.CreatePBESecretKeyDecryptor(var2));
                ++var3;
            } catch (Exception var5) {
            }
        }

        return var3 != 0;
    }

    protected boolean isKeyPasswordConfirmed(PGPSecretKey var1, String var2) {
        int var3 = 0;

        try {
            var1.extractPrivateKey(staticBCFactory.CreatePBESecretKeyDecryptor(var2));
            ++var3;
        } catch (Exception var4) {
        }

        return var3 != 0;
    }

    protected PGPSecretKeyRing findSecretKeyRing(String var1) throws NoPrivateKeyFoundException {
        Collection var2;
        if ((var2 = this.getSecretKeyRingCollection(var1)).size() == 0 && this.O.size() > 0) {
            String var5 = "";
            String var3 = "";
            if (!q.matcher(var1).matches() && !r.matcher(var1).matches()) {
                var5 = var1;
            } else {
                var3 = var1;
            }

            for(int var4 = 0; var4 < this.O.size(); ++var4) {
                ((IKeyStoreSearchListener)this.O.get(var4)).onKeyNotFound(this, false, 0L, var3, var5);
            }

            var2 = this.b(var1);
        }

        if (var2.size() > 0) {
            return (PGPSecretKeyRing)var2.iterator().next();
        } else {
            throw new NoPrivateKeyFoundException("No key found for userID: " + var1);
        }
    }

    protected PGPSecretKeyRing findSecretKeyRing(long var1) throws NoPrivateKeyFoundException {
        PGPSecretKeyRing var3;
        if ((var3 = this.e(var1)) == null && this.O.size() > 0) {
            for(int var4 = 0; var4 < this.O.size(); ++var4) {
                ((IKeyStoreSearchListener)this.O.get(var4)).onKeyNotFound(this, false, var1, KeyPairInformation.keyId2Hex(var1), "");
            }

            var3 = this.e(var1);
        }

        if (var3 == null) {
            throw new NoPrivateKeyFoundException("No key found matching keyID: " + var1);
        } else {
            return var3;
        }
    }

    protected Collection getSecretKeyRingCollection(String var1) {
        ArrayList var2 = new ArrayList();

        try {
            Iterator var3 = this.a.getKeyRings(var1, this.k, !this.j);

            PGPSecretKeyRing var4;
            while(var3.hasNext()) {
                var4 = (PGPSecretKeyRing)var3.next();
                var2.add(var4);
            }

            if (var2.size() == 0 && (q.matcher(var1).matches() || r.matcher(var1).matches())) {
                String var6 = d(var1);
                if ((var4 = this.e(this.getKeyIdForKeyIdHex(var6))) != null) {
                    var2.add(var4);
                }
            }
        } catch (org.spongycastle.openpgp.PGPException var5) {
        }

        return var2;
    }

    private PGPSecretKeyRing e(long var1) throws NoPrivateKeyFoundException {
        try {
            return this.a.getSecretKeyRing(var1);
        } catch (org.spongycastle.openpgp.PGPException var3) {
            throw new NoPrivateKeyFoundException(var3.getMessage(), var3);
        }
    }

    private static void a(PGPKeyRingGenerator var0, PGPKeyPair var1, int[] var2, int[] var3, int[] var4, long var5) throws PGPException {
        PGPSignatureSubpacketGenerator var7;
        (var7 = new PGPSignatureSubpacketGenerator()).setKeyFlags(false, 12);
        if (var5 > 0L) {
            var7.setKeyExpirationTime(false, var5 * 24L * 60L * 60L);
        }

        var7.setPreferredSymmetricAlgorithms(false, var2);
        var7.setPreferredHashAlgorithms(false, var3);
        var7.setPreferredCompressionAlgorithms(false, var4);

        try {
            var0.addSubKey(var1, var7.generate(), (PGPSignatureSubpacketVector)null);
        } catch (org.spongycastle.openpgp.PGPException var8) {
            throw IOUtil.newPGPException(var8);
        }
    }

    private static int a(KeyAlgorithm var0) {
        if (var0 == KeyAlgorithm.RSA) {
            return 1;
        } else if (var0 == KeyAlgorithm.EC) {
            return 18;
        } else if (var0 == KeyAlgorithm.ELGAMAL) {
            return 16;
        } else {
            throw new IllegalArgumentException("unknown key algorithm: " + var0);
        }
    }

    protected void replacePublicKeyRing(PGPPublicKeyRing var1) throws PGPException {
        try {
            boolean var2 = false;
            long var3 = var1.getPublicKey().getKeyID();
            KeyPairInformation var5 = this.a();
            if (this.f.contains(var3)) {
                var2 = true;
                var5.setPublicKeyRing(var1);
                this.I.put(new Long(var1.getPublicKey().getKeyID()), var5);
                this.f = PGPPublicKeyRingCollection.removePublicKeyRing(this.f, var1);
                this.f = PGPPublicKeyRingCollection.addPublicKeyRing(this.f, var1);
            }

            if (this.a.contains(var3)) {
                var2 = true;
                PGPSecretKeyRing var7 = PGPSecretKeyRing.replacePublicKeys(this.a.getSecretKeyRing(var3), var1);
                var5.setPublicKeyRing(var1);
                var5.setPrivateKeyRing(var7);
                this.I.put(new Long(var1.getPublicKey().getKeyID()), var5);
                this.a = PGPSecretKeyRingCollection.removeSecretKeyRing(this.a, var7);
                this.a = PGPSecretKeyRingCollection.addSecretKeyRing(this.a, var7);
            }

            if (!var2) {
                throw new IllegalStateException("unknown key ring in replace");
            }
        } catch (org.spongycastle.openpgp.PGPException var6) {
            throw new PGPException(var6.getMessage(), var6.getUnderlyingException());
        }

        if (this.d) {
            this.save();
        }

    }

    protected void replaceSecretKeyRing(PGPSecretKeyRing var1) throws PGPException {
        try {
            boolean var2 = false;
            long var3 = var1.getPublicKey().getKeyID();
            KeyPairInformation var5 = this.a();
            if (this.f.contains(var3)) {
                var2 = true;
                PGPPublicKeyRing var6 = this.f.getPublicKeyRing(var3);
                var5.setPublicKeyRing(var6);
            }

            if (this.a.contains(var3)) {
                var2 = true;
                var5.setPrivateKeyRing(var1);
                this.I.put(new Long(var1.getPublicKey().getKeyID()), var5);
                this.a = PGPSecretKeyRingCollection.removeSecretKeyRing(this.a, var1);
                this.a = PGPSecretKeyRingCollection.addSecretKeyRing(this.a, var1);
            }

            if (!var2) {
                throw new IllegalStateException("unknown key ring in replace");
            }
        } catch (org.spongycastle.openpgp.PGPException var7) {
            throw new PGPException(var7.getMessage(), var7.getUnderlyingException());
        }

        if (this.d) {
            this.save();
        }

    }

    protected int getEncAlgorithm(String var1) {
        if (var1 == null) {
            return 9;
        } else if (var1.equalsIgnoreCase("AES_256")) {
            return 9;
        } else if (var1.equalsIgnoreCase("AES_192")) {
            return 8;
        } else if (var1.equalsIgnoreCase("AES_128")) {
            return 7;
        } else if (var1.equalsIgnoreCase("TRIPLE_DES")) {
            return 2;
        } else if (var1.equalsIgnoreCase("TWOFISH")) {
            return 10;
        } else if (var1.equalsIgnoreCase("NULL")) {
            return 0;
        } else {
            throw new IllegalArgumentException("unknown symmetric encryption algorithm: " + var1);
        }
    }

    protected static String getKeyAlgorithm(int var0) {
        switch (var0) {
            case 1:
                return "RSA";
            case 2:
                return "RSA";
            case 3:
                return "RSA";
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            default:
                return "Unknown";
            case 16:
                return "DH/DSS";
            case 17:
                return "DH/DSS";
            case 18:
                return "EC";
            case 19:
                return "ECDSA";
            case 20:
                return "DH/DSS";
            case 21:
                return "DH/DSS";
        }
    }

    static String a(int var0) {
        switch (var0) {
            case 0:
                return "No compression";
            case 1:
                return "Zip";
            case 2:
                return "ZLib";
            case 3:
                return "BZip2";
            default:
                return "Unknown";
        }
    }

    static String b(int var0) {
        switch (var0) {
            case 1:
                return "MD 5";
            case 2:
                return "SHA1";
            case 3:
                return "RipeMD 160";
            case 4:
                return "Double SHA";
            case 5:
                return "MD 2";
            case 6:
                return "Tiger 192";
            case 7:
                return "Haval 5";
            case 8:
                return "SHA2 - 256";
            case 9:
                return "SHA2 - 384";
            case 10:
                return "SHA2 - 512";
            case 11:
                return "SHA2 - 224";
            default:
                return "Unknown";
        }
    }

    static String c(int var0) {
        switch (var0) {
            case 1:
                return "IDEA";
            case 2:
                return "3 DES";
            case 3:
                return "Cast 5";
            case 4:
                return "Blowfish";
            case 5:
                return "Safer";
            case 6:
                return "DES";
            case 7:
                return "AES 128";
            case 8:
                return "AES 192";
            case 9:
                return "AES 256";
            case 10:
                return "Twofish";
            default:
                return "Unknown";
        }
    }

    private static String a(String var0, int var1) {
        StringBuffer var2;
        (var2 = new StringBuffer()).append(var0);
        if (var0.length() < var1) {
            for(int var3 = 0; var3 < var1 - var0.length(); ++var3) {
                var2.append(' ');
            }
        }

        return var2.toString();
    }

    private static void c(String var0) {
        if (l.isLoggable(Level.FINE)) {
            l.fine(var0);
        }

    }

    private static void a(String var0, String var1) {
        if (l.isLoggable(Level.FINE)) {
            l.fine(MessageFormat.format(var0, var1));
        }

    }

    private static String d(String var0) {
        return (var0.startsWith("0x") ? var0.substring(2) : var0).toUpperCase();
    }

    public boolean isSkipLucasLehmerPrimeTest() {
        return this.o;
    }

    public void setSkipLucasLehmerPrimeTest(boolean var1) {
        this.o = var1;
    }

    public boolean isFastElGamalGeneration() {
        return this.p;
    }

    public void setFastElGamalGeneration(boolean var1) {
        this.p = var1;
    }

    public int getMaxTrustDepth() {
        return this.L;
    }

    public void setMaxTrustDepth(int var1) {
        this.L = var1;
    }

    public int getMarginalsNeeded() {
        return this.M;
    }

    public void setMarginalsNeeded(int var1) {
        this.M = var1;
    }

    public boolean isCaseSensitiveMatchUserIds() {
        return this.j;
    }

    public void setCaseSensitiveMatchUserIds(boolean var1) {
        this.j = var1;
    }

    public byte getMaxTrustedValue() {
        return this.K;
    }

    public void setMaxTrustedValue(byte var1) {
        this.K = var1;
    }

    private KeyPairInformation a() {
        return new KeyPairInformation(this.J, this.K);
    }

    public String getPassword() {
        return this.c;
    }

    public void setPassword(String var1) {
        this.c = var1;
    }

    public static enum KeyCertificationType {
        GenericCertification(16),
        PersonalCertification(17),
        CasualCertification(18),
        PositiveCertification(19);

        private int a;

        private KeyCertificationType(int var3) {
            this.a = var3;
        }

        public final int getValue() {
            return this.a;
        }
    }
}
