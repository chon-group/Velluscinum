package group.chon.velluscinum;

import com.bigchaindb.exceptions.TransactionNotFoundException;
import com.bigchaindb.model.*;
import com.bigchaindb.util.KeyPairUtils;
import com.bigchaindb.builders.*;
import com.bigchaindb.constants.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;
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
import okhttp3.Response;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;

public class JasonBigchaindbDriver {
    private final Integer LAST = -1;
    final String DRIVERNAME = "[Velluscinum]";
    private boolean boolWait = false;
    private boolean lock	 = false;


    private boolean isLock() {
        return lock;
    }

    private void setLock(boolean lock) {
        if(lock){
            while(isLock()){
                try {
                    Thread.sleep(2000);
                }catch(Exception e) {}
            }
        }
        this.lock = lock;
    }

    public String getFieldOfTransactionFromAssetID(String serverURL, String assetID, String fieldMetadata, Integer idOfTransaction){
        setLock(true);
        String value = null;
        try {
            setConfig(serverURL);
            String transactionID = getTransactionIDFromAsset(assetID, idOfTransaction);
            JSONObject metadata = getMetadataFromTransactionID(transactionID);
            value = metadata.getJSONObject("metadata").getString(fieldMetadata);
        }catch(Exception ex) {

        }
        setLock(false);
        return value;
    }

    private JSONObject getMetadataFromTransactionID(String transactionID){
        Transaction T = new Transaction();
        T = getTransactionFromID(transactionID);
        JSONObject j = new JSONObject(T.toString());
        return j;
    }

    private Transaction getTransactionFromID(String transactionID) {
        Transaction T = new Transaction();
        try {
            T = com.bigchaindb.api.TransactionsApi.getTransactionById(transactionID);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            T = null;
        } catch (TransactionNotFoundException e) {
            throw new RuntimeException(e);
        }
        return T;
    }

// 	public JSONObject getTransactionFromAsset(String assetID, Integer numberOfTransaction) {
//		Transactions Tr = getTransactionsFromAsset(assetID);
//		if(numberOfTransaction==-1) {
//			numberOfTransaction = Tr.getTransactions().size()-1;
//		}
//       	return new JSONObject(Tr.getTransactions().get(numberOfTransaction).toString());
//	}

    public String getTransactionIDFromAsset(String serverURL, String assetID, Integer numberOfTransaction){
        setLock(true);
        setConfig(serverURL);
        String resposta = getTransactionIDFromAsset(assetID, numberOfTransaction);
        setLock(false);
        return resposta;
    }

    private String getTransactionIDFromAsset(String assetID, Integer numberOfTransaction) {
        Transactions Tr = getTransactionsFromAsset(assetID);
        Integer totalTranfers = Tr.getTransactions().size();
        String strTransactionID = null;
        if(totalTranfers==0){
            strTransactionID = assetID;
        }else {
            if(numberOfTransaction==LAST) {
                numberOfTransaction = totalTranfers-1;
            }
            strTransactionID = Tr.getTransactions().get(numberOfTransaction).getId().replace("\"", "");
        }
        return strTransactionID;
    }

//	public Integer getNumberOfTransactionsFromAsset(String assetID){
//		Transactions Tr = getTransactionsFromAsset(assetID);
//		return Tr.getTransactions().size();
//	}

    private Transactions getTransactionsFromAsset(String assetID){
        Transactions T = new Transactions();
        try {
            T = com.bigchaindb.api.TransactionsApi.getTransactionsByAssetId(assetID, com.bigchaindb.constants.Operations.TRANSFER);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return T;
    }

    public void setConfig(String serverURL) {
        BigchainDbConfigBuilder
                .baseUrl(serverURL).setup();
    }

    public void setConfig(String serverURL, String appID, String appKey) {
        BigchainDbConfigBuilder
                .baseUrl(serverURL)
                .addToken("app_id", appID)
                .addToken("app_key", appKey).setup();
    }

    public String newTransfer(String strURL, String strSenderPrivateKey, String strSenderPublicKey, String strAssetId, String strNewMetadata, String strRecipientPublicKey) {
        setLock(true);
        String transectionID = null;

        try {
            setConfig(strURL);
            EdDSAPrivateKey senderPrivateKey = importPrivateKeyFromString(strSenderPrivateKey);
            EdDSAPublicKey  senderPublicKey  = importPublicKeyFromString(strSenderPublicKey);
            EdDSAPublicKey  recipientPublicKey  = importPublicKeyFromString(strRecipientPublicKey);
            JSONObject newMetadata = new JSONObject(strNewMetadata);
            transectionID = newTransfer(senderPrivateKey, senderPublicKey, strAssetId, newMetadata, recipientPublicKey);
        }catch(Exception e) {
            e.printStackTrace();
            transectionID = null;
        }
        setLock(false);
        return transectionID;


    }

    public String newTransfer(EdDSAPrivateKey bobPrivateKey, EdDSAPublicKey bobPublicKey, String assetId, JSONObject newMetadata, EdDSAPublicKey alicePublicKey) {

        JSONObject jsonMetadata 	= new JSONObject(newMetadata.getJSONArray("metadata").get(0).toString());
        MetaData metaData = new MetaData();
        String field = null;

        for(int i = 0; i<jsonMetadata.length(); i++){
            field = jsonMetadata.names().getString(i);
            metaData.setMetaData(field, jsonMetadata.get(field).toString());
        }
        metaData.setMetaData("timestamp", Long.toString(System.currentTimeMillis()));

        try {

            return  doTransfer(bobPrivateKey, bobPublicKey, assetId, metaData, alicePublicKey);
        }catch (Exception e) {
            //System.out.println("null");
            return null;
            // TODO: handle exception
        }

    }

    private String doTransfer(EdDSAPrivateKey bobPrivateKey, EdDSAPublicKey bobPublicKey, String assetId, MetaData transferMetaData, EdDSAPublicKey alicePublicKey) {
        setBoolWait(true);
        String lastTransection = getTransactionIDFromAsset(assetId, LAST);
        FulFill spendFrom = new FulFill();
        spendFrom.setTransactionId(lastTransection);
        spendFrom.setOutputIndex(0);
        Transaction transferTransaction;
        try {
            transferTransaction = BigchainDbTransactionBuilder
                    .init()
                    .addMetaData(transferMetaData)
                    .addInput(null, spendFrom, bobPublicKey)
                    .addOutput("1", alicePublicKey)
                    .addAssets(assetId, String.class)
                    .operation(Operations.TRANSFER)
                    .buildAndSign(bobPublicKey, bobPrivateKey)
                    .sendTransaction(handleServerResponse());

            System.out.print(DRIVERNAME+" Transfer Asset... "+transferTransaction.getId()+" ");
            while(isBoolWait()) {
                Thread.sleep(2000);
            }

            return transferTransaction.getId();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public String newAsset(String strURL, String strPrivateKey, String strPublicKey, String strAsset){
        setLock(true);
        setConfig(strURL);
        JSONObject asset = new JSONObject(strAsset);
        EdDSAPublicKey publicKey = importPublicKeyFromString(strPublicKey);
        EdDSAPrivateKey privateKey = importPrivateKeyFromString(strPrivateKey);
        String strAssetID = null;
        try {
            strAssetID = newAsset(asset, privateKey, publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setLock(false);
        return strAssetID;
    }

    public String newAsset(JSONObject asset, EdDSAPrivateKey privateKey, EdDSAPublicKey publicKey) throws Exception{
        JSONObject jsonAsset 	= new JSONObject(asset.getJSONArray("asset").get(0).toString());
        JSONObject jsonMeta 	= new JSONObject(asset.getJSONArray("metadata").get(0).toString());

        //Construindo Ativo
        @SuppressWarnings("serial")
        Map<String, String> assetData = new TreeMap<String, String>(){{
            String field = null;
            for(int i = 0; i<jsonAsset.length(); i++){
                field = jsonAsset.names().getString(i);
                put(field, jsonAsset.get(field).toString());
            }
        }};

        //Construindo Metadata relativo ao Ativo
        MetaData metaData = new MetaData();
        String field = null;
        for(int i = 0; i<jsonMeta.length(); i++){
            field = jsonMeta.names().getString(i);
            metaData.setMetaData(field, jsonMeta.get(field).toString());
        }
        metaData.setMetaData("timestamp", Long.toString(System.currentTimeMillis()));

        return doCreate(assetData, metaData, privateKey, publicKey);

    }

    private String doCreate(Map<String, String> assetData, MetaData metaData, EdDSAPrivateKey privateKey, EdDSAPublicKey publicKey) throws Exception {
        setBoolWait(true);
        try {
            //build and send CREATE transaction
            Transaction transaction = null;

            transaction = BigchainDbTransactionBuilder
                    .init()
                    .addAssets(assetData, TreeMap.class)
                    .addMetaData(metaData)
                    .operation(Operations.CREATE)
                    .buildAndSign(publicKey, privateKey)
                    .sendTransaction(handleServerResponse());

            System.out.print(DRIVERNAME+" Creating Asset... "+ transaction.getId()+" ");
            while(isBoolWait()) {
                Thread.sleep(2000);
            }

            return transaction.getId();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    private GenericCallback handleServerResponse() {
        //define callback methods to verify response from BigchainDBServer
        GenericCallback callback = new GenericCallback() {

            @Override
            public void transactionMalformed(Response response) {
                System.out.print("[malformed " + response.message()+"]");
                setBoolWait(false);
                onFailure();
            }

            @Override
            public void pushedSuccessfully(Response response) {
                System.out.print("[pushed]");
                setBoolWait(false);
                onSuccess(response);
            }

            @Override
            public void otherError(Response response) {
                System.out.print("[otherError" + response.message()+"]");
                setBoolWait(false);
                onFailure();
            }
        };

        return callback;
    }

    private boolean isBoolWait() {
        return boolWait;
    }

    private void setBoolWait(boolean boolWait) {
        this.boolWait = boolWait;
    }

    private void onSuccess(Response response) {
        //TODO : Add your logic here with response from server
        System.out.println("[successfully]");
        setBoolWait(false);
    }

    private void onFailure() {
        //TODO : Add your logic here
        System.out.println("[Transaction failed]");
        setBoolWait(false);
    }

    public KeyPair newKey() {
        System.out.print(DRIVERNAME+" Key Generated.... ");
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
        System.out.print(DRIVERNAME+" Load PublicKey... ");
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
            System.out.println(KeyPairUtils.encodePublicKeyInBase58(publicKey));
        }

        return publicKey;
    }

    public EdDSAPrivateKey importPrivateKeyFromFile(String filePath) {
        System.out.print(DRIVERNAME+" Load PrivateKey... ");
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
            System.out.println(privateKeyEncoded);
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


    public JSONObject importAssetFromFile(String filePath) {
        System.out.println(DRIVERNAME+" Load Asset from file... "+filePath);

        byte[] assetReadFromFile = null;
        JSONObject assetJSON = null;
        FileInputStream is = null;

        try {
            is = new FileInputStream(filePath);
            assetReadFromFile = is.readAllBytes();
            if (assetReadFromFile != null) {
                String strAsset = new String(assetReadFromFile);
                assetJSON = new JSONObject(strAsset);
            }
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
        return assetJSON;
    }
}
