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

/**
 * BigChainDB Driver for Jason Agents.
 *
 * @author Nilson Mori
 */
public class BigchainDBDriver {
    final private String DRIVERNAME = "[BigChainDBDriver]";
    private KeyManagement keyManagement = new KeyManagement();
    private TransferAdditionalInfo additionalInformation;
    private final Integer LAST = -1;

    private String getFieldOfTransactionFromAssetID(String serverURL, String assetID, String fieldMetadata, Integer idOfTransaction){
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

    private String getTransactionIDFromAsset(String serverURL, String assetID, Integer numberOfTransaction){
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

    private void setConfig(String serverURL) {
        BigchainDbConfigBuilder
                .baseUrl(serverURL).setup();
    }

    private void setConfig(String serverURL, String appID, String appKey) {
        BigchainDbConfigBuilder
                .baseUrl(serverURL)
                .addToken("app_id", appID)
                .addToken("app_key", appKey).setup();
    }

    /**
     * TransferNFT is the method that transfers a non-fungible token in the BigChainDB Network.
     *
     * @param strURL Receives a <b>URL:PORT</b> Address from a bigchainDB Server
     *               <br>&emsp;&emsp; (e.g., "http://testchain.chon.group:9984").
     * @param strSenderPrivateKey Receives a Base58 format <b>Private Key</b> from the sender wallet in the BigChainDB Network
     *                            <br>&emsp;&emsp; (e.g., "A4BAzNZdGBqkGHQP7gWovcYaGP3UELL6f4THhuHSQHCK").
     * @param strSenderPublicKey Receives a Base58 format <b>Public Key</b> from the sender wallet in the BigChainDB Network
     *                           <br>&emsp;&emsp; (e.g., "arDLZkDEmZi5wWtebvZrC5KgSs2tX8ULLeBSFp8dTJR").
     * @param strAssetId Receives the <b>ASSET-ID</b> from the non-fungible token that wants to transfer to another wallet
     *                  <br>&emsp;&emsp; (e.g., "095ee8b3a8599fabe61a09c1c046df7b8af3cb4f70b815222daad132988bd67d").
     * @param strNewMetadata Receives the <b>Metadata Array</b> about the transfer of the non-fungible token in JSON format
     *                      <br>&emsp;&emsp; (e.g., "{\"metadata\":[{\"new owner\":\"Agent Alice\",
     *                       <br>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp; \"location\":\"Rio de Janeiro\",
     *                       <br>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp; \"value_btc\":\"2100\",
     *                       <br>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp; \"value_eur\":\"30000000€\"
     *                       <br>&emsp;&emsp;&emsp;&emsp; }]}").
     * @param strRecipientPublicKey Receives a Base58 format <b>Public Key</b> from the receiver wallet in the BigChainDB Network
     *                              <br>&emsp;&emsp; (e.g, "FNJPJdtuPQYsqHG6tuUjKjqv7SW84U4ipiyyLV2j6MEW").
     *                              <br>
     *                              <br>
     * @return Return the <b>TRANSFER-ID</b> case every crypto-conditions are fulfilled in the transaction at BigChainDB Network
     *          <br>&emsp;&emsp; (e.g, "9019f938c175ad768f76b3ce4842a066a99198f68dc3078b0fab58349ece5896").
     * */
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

    private String newTransfer(EdDSAPrivateKey bobPrivateKey,
                               EdDSAPublicKey bobPublicKey,
                               String assetId,
                               JSONObject newMetadata,
                               EdDSAPublicKey alicePublicKey) {
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

    private String doTransfer(EdDSAPrivateKey bobPrivateKey,
                              EdDSAPublicKey bobPublicKey,
                              String assetId,
                              MetaData transferMetaData,
                              EdDSAPublicKey alicePublicKey) {
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

    /**
     * RegisterNFT is the method that create a non-fungible token in the BigChainDB Network.
     *
     * @param url Receives a <b>URL:PORT</b> Address from a BigChainDB Server
     *            <br>&emsp;&emsp; (e.g., "http://testchain.chon.group:9984").
     * @param privateKey Receives a Base58 format <b>Private Key</b> from the owner wallet in the BigChainDB Network
     *      *                            <br>&emsp;&emsp; (e.g., "A4BAzNZdGBqkGHQP7gWovcYaGP3UELL6f4THhuHSQHCK").
     * @param publicKey Receives a Base58 format <b>Public Key</b> from the owner wallet in the BigChainDB Network
     *      *                           <br>&emsp;&emsp; (e.g., "arDLZkDEmZi5wWtebvZrC5KgSs2tX8ULLeBSFp8dTJR").
     * @param nonFungibleToken Receives the <b>Asset/Metadata Array</b> of the non-fungible token in JSON format
     *                 <br>&emsp;&emsp; (e.g., "{\n" +
     *                 <br>&emsp;&emsp;"  \"asset\":[{\n" +
     *                 <br>&emsp;&emsp;&emsp;&emsp;"      \"name\":\"Meninas\",\n" +
     *                 <br>&emsp;&emsp;&emsp;&emsp;"      \"author\":\"Diego Rodríguez de Silva y Velázquez\",\n" +
     *                 <br>&emsp;&emsp;&emsp;&emsp;"      \"place\":\"Madrid\",\n" +
     *                 <br>&emsp;&emsp;&emsp;&emsp;"      \"year\":\"1656\"}],\n" +
     *                 <br>&emsp;&emsp;"  \"metadata\":[{\n" +
     *                 <br>&emsp;&emsp;&emsp;&emsp;"      \"location\":\"Madrid\",\n" +
     *                 <br>&emsp;&emsp;&emsp;&emsp;"      \"value_eur\":\"25000000€\",\n" +
     *                 <br>&emsp;&emsp;&emsp;&emsp;"      \"value_btc\":\"2200\",\n" +
     *                 <br>&emsp;&emsp;&emsp;&emsp;"      \"owner\":\"Agent Bob\"}]\n" +
     *                 <br>&emsp;&emsp;"}").
     *
     * @return Return the ASSET-ID case every crypto-conditions of CREATE action are fulfilled in BigChainDB Network
     *      *          <br>&emsp;&emsp; (e.g, "9019f938c175ad768f76b3ce4842a066a99198f68dc3078b0fab58349ece5896").
     */
    public String registerNFT(String url, String privateKey, String publicKey, String nonFungibleToken){
        setConfig(url);
        JSONObject asset = new JSONObject(nonFungibleToken);
        EdDSAPublicKey edDSAPublicKey = keyManagement.stringToEdDSAPublicKey(publicKey,"base58");
        EdDSAPrivateKey edDSAPrivateKey = keyManagement.stringToEdDSAPrivateKey(privateKey,"base58");
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
