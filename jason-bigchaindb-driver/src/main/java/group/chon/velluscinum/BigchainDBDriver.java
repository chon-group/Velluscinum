package group.chon.velluscinum;

import com.bigchaindb.exceptions.TransactionNotFoundException;
import com.bigchaindb.model.*;
import com.bigchaindb.builders.*;
import com.bigchaindb.constants.*;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import org.json.JSONObject;

public class BigchainDBDriver {
    final private String DRIVERNAME = "[BigChainDBDriver]";
    private KeyManagement keyManagement = new KeyManagement();
    private TransfAdditionalInfo additionalInformation;
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

    public String transferNFT(String strURL,
                              String strSenderPrivateKey,
                              String strSenderPublicKey,
                              String strAssetId,
                              String strNewMetadata,
                              String strRecipientPublicKey) {

        try {
            setConfig(strURL);
            return newTransfer(
                    keyManagement.stringToEdDSAPrivateKey(strSenderPrivateKey,"base58"),
                    keyManagement.stringToEdDSAPublicKey(strSenderPublicKey,"base58"),
                    strAssetId,
                    new JSONObject(strNewMetadata),
                    keyManagement.stringToEdDSAPublicKey(strRecipientPublicKey,"base58"));
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String newTransfer(EdDSAPrivateKey bobPrivateKey, EdDSAPublicKey bobPublicKey, String assetId, JSONObject newMetadata, EdDSAPublicKey alicePublicKey) {
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

            System.out.print(DRIVERNAME+" Transfer Asset... "+transferTransaction.getId()+" ");
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

    public String registerNFT(String strURL, String strPrivateKey58, String strPublicKey58, String strNFT){
        setConfig(strURL);
        JSONObject asset = new JSONObject(strNFT);
        EdDSAPublicKey edDSAPublicKey = keyManagement.stringToEdDSAPublicKey(strPublicKey58,"base58");
        EdDSAPrivateKey edDSAPrivateKey = keyManagement.stringToEdDSAPrivateKey(strPrivateKey58,"base58");
        try {
            return newAsset(asset, edDSAPrivateKey, edDSAPublicKey);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String newAsset(JSONObject asset,
                            EdDSAPrivateKey edDSAPrivateKey,
                            EdDSAPublicKey edDSAPublicKey) throws Exception{

        JSONObject jsonAsset 	= new JSONObject(asset.getJSONArray("asset").get(0).toString());
        JSONObject jsonMeta 	= new JSONObject(asset.getJSONArray("metadata").get(0).toString());

        //Construindo Ativo
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
        return doCreate(assetData, metaData, edDSAPrivateKey, edDSAPublicKey);
    }

    private String doCreate(Map<String,
                            String> assetData,
                            MetaData metaData,
                            EdDSAPrivateKey edDSAPrivateKey,
                            EdDSAPublicKey edDSAPublicKey) throws Exception {
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
                    .buildAndSign(edDSAPublicKey, edDSAPrivateKey)
                    .sendTransaction(ServerResponse.handleServerResponse());
            System.out.print(DRIVERNAME+" Creating Asset... "+ transaction.getId()+" ");
            ServerResponse.waitDone();
            ServerResponse.setLock(false);
            return transaction.getId();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
