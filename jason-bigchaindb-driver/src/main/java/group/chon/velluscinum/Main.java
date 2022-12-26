package group.chon.velluscinum;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;

public class Main {
    public static void main(String[] args) throws IOException {
        JasonBigchaindbDriver bigchaindb4Jason = new JasonBigchaindbDriver();
        String op = null;
        String output = null;
        try {
            op = args[0];
            if(op.equals("createKeys")) {
                KeyPair bobKeyPair 	= bigchaindb4Jason.newKey();
                EdDSAPrivateKey bobPrivateKey = (EdDSAPrivateKey) bobKeyPair.getPrivate();
                EdDSAPublicKey bobPublicKey  = (EdDSAPublicKey)  bobKeyPair.getPublic();
                try{
                    output = args[1]+".";
                }catch(Exception ex){
                    output = "";
                }
                bigchaindb4Jason.exportPrivateKey(bobPrivateKey, output+"private.key");
                bigchaindb4Jason.exportPublicKey(bobPublicKey, output+"public.key");
            }else if(op.equals("newAsset")) {
                //[SERVER] [PRIVATEKEY-FILE] [PUBLICKEY-FILE] [ASSET]
                String			 serverURL	= args[1];
                bigchaindb4Jason.setConfig(serverURL);
                EdDSAPrivateKey  privateKey = bigchaindb4Jason.importPrivateKeyFromFile(args[2]);
                EdDSAPublicKey   publicKey  = bigchaindb4Jason.importPublicKeyFromFile(args[3]);
                JSONObject jSONAsset	= bigchaindb4Jason.importAssetFromFile(args[4]);
                bigchaindb4Jason.newAsset(jSONAsset, privateKey, publicKey);
                System.exit(0);
            }else if(op.equals("newTransfer")) {
                //[SERVER] [PRIVATEKEY-FILE] [PUBLICKEY-FILE] [ASSET-ID] [METADATA] [PUBLIC_DEST_KEY-FILE]
                String			 serverURL	= args[1];
                bigchaindb4Jason.setConfig(serverURL);
                EdDSAPrivateKey  privateKey = bigchaindb4Jason.importPrivateKeyFromFile(args[2]);
                EdDSAPublicKey   publicKey  = bigchaindb4Jason.importPublicKeyFromFile(args[3]);
                String 			 assetID = args[4];
                JSONObject		 jSONAsset	= bigchaindb4Jason.importAssetFromFile(args[5]);
                EdDSAPublicKey   destinatorPublicKey  = bigchaindb4Jason.importPublicKeyFromFile(args[6]);
                bigchaindb4Jason.newTransfer(privateKey, publicKey, assetID, jSONAsset, destinatorPublicKey);
                System.exit(0);
            }
        } catch (Exception ex) {
            InputStream stream = Main.class.getResourceAsStream("/manual.txt");
            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader buffered = new BufferedReader(reader);
            String line = buffered.readLine();
            while(line != null){
                System.out.println(line);
                line = buffered.readLine();
            }
            buffered.close();
            System.exit(0);
        }

    }
}