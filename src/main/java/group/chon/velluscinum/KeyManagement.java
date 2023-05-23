package group.chon.velluscinum;

import com.bigchaindb.util.Base58;
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

/**
 *  BigChainDB Key Manager for Jason Agents.
 *
 * @author Nilson Mori
 */
public class KeyManagement {
    final private String BASE64 = "base64";
    final private String BASE58 = "base58";
    final byte[] bytesSpecPrivate = {0x30,0x2e,0x02,0x01,0x00,0x30,0x05,0x06,0x03,0x2b,0x65,0x70,0x04,0x22,0x04,0x20};
    final byte[] bytesSpecPublic = {0x30,0x2a,0x30,0x05,0x06,0x03,0x2b,0x65,0x70,0x03,0x21,0x00};

    /**
     * Generates a new ECDSA (Elliptic Curve Digital Signature Algorithm) Key Pair
     *
     * @return a ECDSA Key Pair Object
     *
     * */
    private KeyPair newKey() {
        KeyPairGenerator keyPairGenerator = new KeyPairGenerator();
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        System.out.println(Api.DRIVERNAME+" Build Wallet... "+keyToString((EdDSAPublicKey) keyPair.getPublic(),"base58"));
        return keyPair;
    }

    /**
     * Generates a new ECDSA (Elliptic Curve Digital Signature Algorithm) Key Pair
     * <br>
     * <br>
     *
     * @param outBase Defines the Key Pair return base format (e.g., "base58" OR "base64").
     * <br>
     * <br>
     *
     * @return a String Array with the ECDSA Key Pair in base64 or base58 format.
     * <br>&bull; The first element (return[0]) is the Private Key in the format defined in base parameter.
     * <br>&bull; The second element (return[1]) is the Public Key in the format defined in base parameter.
     */
    public String[] newKeyPair(String outBase){
        KeyPair bobKeyPair 	= newKey();
        String[] strPair = {keyToString((EdDSAPrivateKey) bobKeyPair.getPrivate(),outBase),
                            keyToString((EdDSAPublicKey)  bobKeyPair.getPublic(),outBase)};
        return strPair;
    }

    /**
     *  Converts an ECDSA Public Key Object in a String base format.
     *
     * @param edDSAPublicKey Receives an ECDSA Public Key Object.
     * @param outBase   Defines the output String format (e.g., "base58" OR "base64").
     *
     * @return  The Public Key in the String base format. The base is defined in the outBase parameter.
     */
    public String keyToString(EdDSAPublicKey edDSAPublicKey,
                              String outBase){
        if(outBase.equals(BASE58)){
            return Base58.encode(Arrays.copyOfRange(edDSAPublicKey.getEncoded(), 12, 44));
        }else if(outBase.equals(BASE64)){
            return Base64.getEncoder().encodeToString(edDSAPublicKey.getEncoded());
        }else{
            return  null;
        }
    }

    /**
     * Converts an ECDSA Private Key Object in a String base format.
     *
     * @param edDSAPrivateKey Receives an ECDSA Private Key Object.
     * @param outBase Defines the output String format (e.g., "base58" OR "base64").
     * @return The Public Key in the String base format. The base is defined in the outBase parameter.
     */
    public String keyToString(EdDSAPrivateKey edDSAPrivateKey,
                              String outBase){
        if(outBase.equals(BASE58)){
            return Base58.encode(Arrays.copyOfRange(edDSAPrivateKey.getEncoded(), 16, 48));
        }else if(outBase.equals(BASE64)){
            return Base64.getEncoder().encodeToString(edDSAPrivateKey.getEncoded());
        }else{
            return  null;
        }
    }

    /**
     *  Exports a String Base well-formatted Key to a file.
     *
     * @param keyInBaseString Receives a Key (Public OR Private) in String Base format.
     * @param filePath Receives the file path.
     */
    public void keyToFile(String keyInBaseString,
                          String filePath){
        byte[] byteKey = keyInBaseString.getBytes();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            fos.write(byteKey);
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


    /**
     *  Converts a String Base well-formatted Key in an ECDSA Private Key Object.
     *
     * @param strPrivateKey Receives a String Base well-formatted private key.
     *
     * @param base Defines the input String format (e.g., "base58" OR "base64").
     *
     * @return A ECDSA Private Key Object.
     */
    public EdDSAPrivateKey stringToEdDSAPrivateKey(String strPrivateKey,
                                                   String base){
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

    /**
     *  Converts a String Base well-formatted Key in an ECDSA Public Key Object.
     *
     * @param strPublicKey Receives a String Base well-formatted public key.
     *
     * @param base Defines the input String format (e.g., "base58" OR "base64").
     *
     * @return A ECDSA Public Key Object.
     */

        public EdDSAPublicKey stringToEdDSAPublicKey(String strPublicKey,
                                                     String base){
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

    /**
     *  Imports a String Base well-formatted Key from a file.
     *
     * @param filePath Receives the file Path.
     *
     * @return A Key (Public OR Private) in String Base format.
     */
    public String importKeyFromFile(String filePath){
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
