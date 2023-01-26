/*
 * Copyright ChonGroup  http://chon.group
 * SPDX-License-Identifier: (Apache-2.0 AND CC-BY-4.0)
 * Code is Apache-2.0 and docs are CC-BY-4.0
 */
package group.chon.velluscinum;

import com.bigchaindb.util.Base58;
import com.bigchaindb.util.KeyPairUtils;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.KeyPairGenerator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class KeyManagement {
    final private String BASE64 = "base64";
    final private String BASE58 = "base58";
    final byte[] bytesSpecPrivate = {0x30,0x2e,0x02,0x01,0x00,0x30,0x05,0x06,0x03,0x2b,0x65,0x70,0x04,0x22,0x04,0x20};
    final byte[] bytesSpecPublic = {0x30,0x2a,0x30,0x05,0x06,0x03,0x2b,0x65,0x70,0x03,0x21,0x00};

    public KeyPair newKey() {
        KeyPairGenerator keyPairGenerator = new KeyPairGenerator();
        return keyPairGenerator.generateKeyPair();
    }

    public String[] newKeyPair(String base){
        KeyPair bobKeyPair 	= newKey();
        String[] strPair = {keyToString((EdDSAPrivateKey) bobKeyPair.getPrivate(),base),
                            keyToString((EdDSAPublicKey)  bobKeyPair.getPublic(),base)};
        return strPair;
    }

    public String keyToString(EdDSAPublicKey edDSAPublicKey, String outBase){
        if(outBase.equals(BASE58)){
            return Base58.encode(Arrays.copyOfRange(edDSAPublicKey.getEncoded(), 12, 44));
        }else if(outBase.equals(BASE64)){
            return Base64.getEncoder().encodeToString(edDSAPublicKey.getEncoded());
        }else{
            return  null;
        }
    }

    public String keyToString(EdDSAPrivateKey edDSAPrivateKey, String outBase){
        if(outBase.equals(BASE58)){
            return Base58.encode(Arrays.copyOfRange(edDSAPrivateKey.getEncoded(), 16, 48));
        }else if(outBase.equals(BASE64)){
            return Base64.getEncoder().encodeToString(edDSAPrivateKey.getEncoded());
        }else{
            return  null;
        }
    }

    public void keyToFile(EdDSAPublicKey edDSAPublicKey, String outBase,String filePath){
        if(outBase.equals(BASE58)){
            writeFile(keyToString(edDSAPublicKey,BASE58).getBytes(), filePath);
        }else if (outBase.equals(BASE64)) {
            writeFile(keyToString(edDSAPublicKey,BASE64).getBytes(),filePath);
        }
    }

    public void keyToFile(EdDSAPrivateKey edDSAPrivateKey, String outBase, String filePath){
        if(outBase.equals(BASE58)){
            writeFile(keyToString(edDSAPrivateKey,BASE58).getBytes(),filePath);
        }else if(outBase.equals(BASE64)){
            writeFile(keyToString(edDSAPrivateKey,BASE64).getBytes(),filePath);
        }
    }

    private void writeFile(byte[] key, String filePath){
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


    public EdDSAPrivateKey stringToEdDSAPrivateKey(String strPrivateKey, String base){
        if(base.equals(BASE58)){
            return stringToEdDSAPrivateKey(
                    Base64.getEncoder().encodeToString(
                            com.google.common.primitives.Bytes.concat(
                                    bytesSpecPrivate,
                                    org.bitcoinj.core.Base58.decode(strPrivateKey)
                            )
                    ),
                    BASE64
            );
        }else if(base.equals(BASE64)){
            byte[] KEY = Base64.getDecoder().decode(strPrivateKey);
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(KEY);
            try {
                return (new EdDSAPrivateKey(pkcs8EncodedKeySpec));
            } catch (InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        }else{
            return null;
        }
    }

    public EdDSAPublicKey stringToEdDSAPublicKey(String strPublicKey, String base){
        if(base.equals(BASE58)){
            return stringToEdDSAPublicKey(
                    Base64.getEncoder().encodeToString(
                            com.google.common.primitives.Bytes.concat(
                                    bytesSpecPublic,
                                    org.bitcoinj.core.Base58.decode(strPublicKey)
                            )
                    ),
                    BASE64
            );
        }else if(base.equals(BASE64)){
            byte[] KEY = Base64.getDecoder().decode(strPublicKey);
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(KEY);
            try {
                return (new EdDSAPublicKey(x509EncodedKeySpec));
            } catch (InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        }else {
            return null;
        }
    }

    public String importKeyFromFile(String filePath){
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getAddressWallet(EdDSAPublicKey bobPublicKey){
        return KeyPairUtils.encodePublicKeyInBase58(bobPublicKey);
    }
}
