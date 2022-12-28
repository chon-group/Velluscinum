package group.chon.velluscinum;

import com.bigchaindb.util.KeyPairUtils;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.KeyPairGenerator;

import java.io.*;
import java.security.KeyPair;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyManagement {
    Info driver = new Info();
    public KeyPair newKey() {
        System.out.print(driver.getDRIVERNAME()+" Key Generated.... ");
        KeyPairGenerator edDsaKpg = new KeyPairGenerator();
        KeyPair keyPair = edDsaKpg.generateKeyPair();
        System.out.println(KeyPairUtils.encodePublicKeyInBase58((EdDSAPublicKey) keyPair.getPublic()));
        return keyPair;
    }

    public String exportPrivateKeyToBase64(EdDSAPrivateKey edDSAPrivateKey){
        return Base64.getEncoder().encodeToString(edDSAPrivateKey.getEncoded());
    }

    public String exportPublicKeyToBase64(EdDSAPublicKey edDSAPublicKey){
        return Base64.getEncoder().encodeToString(edDSAPublicKey.getEncoded());
    }

    public void exportPrivateKeyToFile(EdDSAPrivateKey edDSAPrivateKey, String filePath) {
        keyToFile(Base64.getEncoder().encode(edDSAPrivateKey.getEncoded()), filePath);
    }

    public void exportPublicKeyToFile(EdDSAPublicKey edDSAPublicKey, String filePath) {
        keyToFile(Base64.getEncoder().encode(edDSAPublicKey.getEncoded()), filePath);
    }

    private void keyToFile(byte[] key, String filePath){
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            fos.write(key);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public EdDSAPrivateKey importPrivateKeyFromBase64(String strPrivateKeyBase64){
        byte[] KEY = Base64.getDecoder().decode(strPrivateKeyBase64);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(KEY);
        try {
            return (new EdDSAPrivateKey(pkcs8EncodedKeySpec));
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public EdDSAPublicKey importPublicKeyFromBase64(String strPublicKeyBase64){
        byte[] KEY = Base64.getDecoder().decode(strPublicKeyBase64);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(KEY);
        try {
            return (new EdDSAPublicKey(x509EncodedKeySpec));
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public EdDSAPublicKey importPublicKeyFromFile(String filePath) {
        try {
            FileInputStream is = new FileInputStream(filePath);
            byte[] KEY = Base64.getDecoder().decode(is.readAllBytes());
            is.close();
            return importPublicKeyFromBase64(Base64.getEncoder().encodeToString(KEY));
        } catch (IOException  e) {
            e.printStackTrace();
            return null;
        }
    }

    public EdDSAPrivateKey importPrivateKeyFromFile(String filePath) {
        try {
            FileInputStream is = new FileInputStream(filePath);
            byte[] KEY = Base64.getDecoder().decode(is.readAllBytes());
            is.close();
            return importPrivateKeyFromBase64(Base64.getEncoder().encodeToString(KEY));
        } catch (IOException  e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getAddressWallet(EdDSAPublicKey bobPublicKey){
        return KeyPairUtils.encodePublicKeyInBase58(bobPublicKey);
    }
}
