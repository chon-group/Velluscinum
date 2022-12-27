package group.chon.velluscinum;

import com.bigchaindb.util.KeyPairUtils;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.KeyPairGenerator;
import net.i2p.crypto.eddsa.math.Curve;
import net.i2p.crypto.eddsa.math.Field;
import net.i2p.crypto.eddsa.math.GroupElement;
import net.i2p.crypto.eddsa.math.ed25519.Ed25519LittleEndianEncoding;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
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

    public void exportPrivateKey(PrivateKey privateKey, String filePath) {
        FileOutputStream fos = null;
        EdDSAPrivateKey edDSAPrivateKey = (EdDSAPrivateKey) privateKey;
        try {
            fos = new FileOutputStream(filePath);
            byte[] privateKeyBase64 = Base64.getEncoder().encode(edDSAPrivateKey.getEncoded());
            fos.write(privateKeyBase64);
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

    public void exportPublicKey(PublicKey publicKey, String filePath) {
        FileOutputStream fos = null;
        EdDSAPublicKey edDSAPublicKey = (EdDSAPublicKey) publicKey;

        PublicKeyAttributes publicKeyAttributes = new PublicKeyAttributes(edDSAPublicKey.getA().getCurve().getField().getb(),
                edDSAPublicKey.getA().getCurve().getField().getQ().toByteArray(),
                edDSAPublicKey.getA().getCurve().getD().toByteArray(),
                edDSAPublicKey.getA().getCurve().getI().toByteArray(),
                edDSAPublicKey.getA().toByteArray());

        try {
            fos = new FileOutputStream(filePath);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(out);
            os.writeObject(publicKeyAttributes);
            byte[] publicKeyEncodedInBase64 = Base64.getEncoder().encode(out.toByteArray());
            fos.write(publicKeyEncodedInBase64);
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

    public EdDSAPublicKey importPublicKeyFromString(String publicKey) {
        String filePath = createTempFile(publicKey);
        EdDSAPublicKey outKey = importPublicKeyFromFile(filePath);
        deleteTempFile(filePath);
        return outKey;
    }

    public EdDSAPublicKey importPublicKeyFromFile(String filePath) {
        EdDSAPublicKey publicKey = null;
        PublicKeyAttributes publicKeyAttributes = null;
        FileInputStream is = null;
        try {
            is = new FileInputStream(filePath);
            byte[] publicKeyReadFromFile = is.readAllBytes();
            ByteArrayInputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(publicKeyReadFromFile));
            ObjectInputStream iss = new ObjectInputStream(in);
            publicKeyAttributes = (PublicKeyAttributes) iss.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (publicKeyAttributes != null) {
            EdDSAParameterSpec keySpecs = EdDSANamedCurveTable.getByName("Ed25519");
            Ed25519LittleEndianEncoding ed25519LittleEndianEncoding = new Ed25519LittleEndianEncoding();
            Field field1 = new Field(publicKeyAttributes.getB(), publicKeyAttributes.getQ(), ed25519LittleEndianEncoding);
            Curve curve1 = new Curve(field1, publicKeyAttributes.getD(), ed25519LittleEndianEncoding.decode(publicKeyAttributes.getI()));
            GroupElement groupElement = new GroupElement(curve1, publicKeyAttributes.getS());
            EdDSAPublicKeySpec pubKeySpec = new EdDSAPublicKeySpec(groupElement, keySpecs);
            publicKey = new EdDSAPublicKey(pubKeySpec);
        }

        return publicKey;
    }

    public EdDSAPrivateKey importPrivateKeyFromFile(String filePath) {
        EdDSAPrivateKey privateKey = null;
        byte[] privateKeyReadFromFile = null;
        FileInputStream is = null;
        try {
            is = new FileInputStream(filePath);
            privateKeyReadFromFile = is.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // closing resources
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (privateKeyReadFromFile != null) {
            byte[] privateKeyEncoded = Base64.getDecoder().decode(privateKeyReadFromFile);
            KeyPair keyPair = KeyPairUtils.decodeKeyPair(privateKeyEncoded);
            privateKey = (EdDSAPrivateKey) keyPair.getPrivate();
        }
        return privateKey;
    }

    public EdDSAPrivateKey importPrivateKeyFromString(String privateKey) {
        String privKeyPath = createTempFile(privateKey);
        EdDSAPrivateKey privKeyOut = importPrivateKeyFromFile(privKeyPath);
        deleteTempFile(privKeyPath);
        return privKeyOut;
    }

    private String createTempFile(String input) {
        String filePath = DigestUtils.md5Hex(input);
        try {
            FileWriter arq = new FileWriter(filePath);
            PrintWriter gravarArq = new PrintWriter(arq);
            gravarArq.printf(input);
            arq.flush();
            arq.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filePath;
    }

    private void deleteTempFile(String filePath) {
        File f = new File(filePath);
        f.delete();
    }


}
