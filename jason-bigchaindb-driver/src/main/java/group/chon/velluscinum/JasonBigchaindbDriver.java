package group.chon.velluscinum;

import com.bigchaindb.exceptions.TransactionNotFoundException;
import com.bigchaindb.model.*;
import com.bigchaindb.builders.*;
import com.bigchaindb.constants.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import org.json.JSONObject;

public class JasonBigchaindbDriver {
    Info driver = new Info();
    KeyManagement keyManagement = new KeyManagement();
    private final Integer LAST = -1;

    public String getFieldOfTransactionFromAssetID(String serverURL, String assetID, String fieldMetadata, Integer idOfTransaction){
        ServerResponse.setLock(true);
        String value = null;
        try {
            setConfig(serverURL);
            String transactionID = getTransactionIDFromAsset(assetID, idOfTransaction);
            JSONObject metadata = getMetadataFromTransactionID(transactionID);
            value = metadata.getJSONObject("metadata").getString(fieldMetadata);
        }catch(Exception ex) {

        }
        ServerResponse.setLock(false);
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

    public String getTransactionIDFromAsset(String serverURL, String assetID, Integer numberOfTransaction){
        ServerResponse.setLock(true);
        setConfig(serverURL);
        String resposta = getTransactionIDFromAsset(assetID, numberOfTransaction);
        ServerResponse.setLock(false);
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
        String transectionID = null;
        try {
            setConfig(strURL);
            EdDSAPrivateKey senderPrivateKey = keyManagement.importPrivateKeyFromString(strSenderPrivateKey);
            EdDSAPublicKey  senderPublicKey  = keyManagement.importPublicKeyFromString(strSenderPublicKey);
            EdDSAPublicKey  recipientPublicKey  = keyManagement.importPublicKeyFromString(strRecipientPublicKey);
            JSONObject newMetadata = new JSONObject(strNewMetadata);
            transectionID = newTransfer(senderPrivateKey, senderPublicKey, strAssetId, newMetadata, recipientPublicKey);
        }catch(Exception e) {
            e.printStackTrace();
            transectionID = null;
        }
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
        String lastTransection = getTransactionIDFromAsset(assetId, LAST);
        FulFill spendFrom = new FulFill();
        spendFrom.setTransactionId(lastTransection);
        spendFrom.setOutputIndex(0);
        Transaction transferTransaction;
        try {
            ServerResponse.setLock(true);
            ServerResponse.setBoolWait(true);
            transferTransaction = BigchainDbTransactionBuilder
                    .init()
                    .addMetaData(transferMetaData)
                    .addInput(null, spendFrom, bobPublicKey)
                    .addOutput("1", alicePublicKey)
                    .addAssets(assetId, String.class)
                    .operation(Operations.TRANSFER)
                    .buildAndSign(bobPublicKey, bobPrivateKey)
                    .sendTransaction(ServerResponse.handleServerResponse());

            System.out.print(driver.getDRIVERNAME()+" Transfer Asset... "+transferTransaction.getId()+" ");
            ServerResponse.waitDone();
            ServerResponse.setLock(false);
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
        setConfig(strURL);
        JSONObject asset = new JSONObject(strAsset);
        EdDSAPublicKey publicKey = keyManagement.importPublicKeyFromString(strPublicKey);
        EdDSAPrivateKey privateKey = keyManagement.importPrivateKeyFromString(strPrivateKey);
        String strAssetID = null;
        try {
            strAssetID = newAsset(asset, privateKey, publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        try {
            ServerResponse.setLock(true);
            ServerResponse.setBoolWait(true);
            //build and send CREATE transaction
            Transaction transaction = null;
            transaction = BigchainDbTransactionBuilder
                    .init()
                    .addAssets(assetData, TreeMap.class)
                    .addMetaData(metaData)
                    .operation(Operations.CREATE)
                    .buildAndSign(publicKey, privateKey)
                    .sendTransaction(ServerResponse.handleServerResponse());
            System.out.print(driver.getDRIVERNAME()+" Creating Asset... "+ transaction.getId()+" ");
            ServerResponse.waitDone();
            ServerResponse.setLock(false);
            return transaction.getId();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public JSONObject importAssetFromFile(String filePath) {
        System.out.println(driver.getDRIVERNAME()+" Load Asset from file... "+filePath);

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
