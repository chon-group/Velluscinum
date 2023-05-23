package group.chon.velluscinum;

import com.bigchaindb.exceptions.TransactionNotFoundException;
import com.bigchaindb.model.*;
import com.bigchaindb.builders.*;
import com.bigchaindb.constants.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.bigchaindb.util.KeyPairUtils;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import org.json.JSONObject;

/**
 * BigChainDB Driver for Jason Agents.
 *
 * @author Nilson Mori
 */
public class BigchainDBDriver {
    //final private String DRIVERNAME = "[BigChainDBDriver]";
    private KeyManagement keyManagement = new KeyManagement();
    private final Integer LAST = -1;



    /**
     *  Creates a Fungible Token.
     *
     * @param url Receives a <b>URL:PORT</b> Address from a bigchainDB Server
     *            <br>&emsp;&emsp; (e.g., "http://testchain.chon.group:9984").
     * @param privateKey Receives a Base58 format <b>Private Key</b> from the owner wallet in the BigChainDB Network
     *                   <br>&emsp;&emsp; (e.g., "A4BAzNZdGBqkGHQP7gWovcYaGP3UELL6f4THhuHSQHCK").
     * @param publicKey Receives a Base58 format <b>Public Key</b> from the owner wallet in the BigChainDB Network
     *                  <br>&emsp;&emsp; (e.g., "arDLZkDEmZi5wWtebvZrC5KgSs2tX8ULLeBSFp8dTJR").
     * @param tokenName Receives the Name of Token (e.g., "MyCoin").
     * @param amount    Receives the Amount of the Token (maximum value is 9×10¹⁸).
     *                  <br>
     *                  <br>
     * @return Return the ASSET-ID case every crypto-conditions of CREATE action are fulfilled in BigChainDB Network
     *  <br>&emsp;&emsp; (e.g, "9019f938c175ad768f76b3ce4842a066a99198f68dc3078b0fab58349ece5896").
     */
    public String deployToken(String url,
                              String privateKey,
                              String publicKey,
                              String tokenName,
                              Long amount) {

        setConfig(url);
        EdDSAPrivateKey edDSAPrivateKey = keyManagement.stringToEdDSAPrivateKey(privateKey,"base58");
        EdDSAPublicKey edDSAPublicKey = keyManagement.stringToEdDSAPublicKey(publicKey,"base58");


        JSONObject asset = new JSONObject(tokenName);
        JSONObject jsonAsset 	= new JSONObject(asset.getJSONArray("asset").get(0).toString());
        //JSONObject jsonMeta 	= new JSONObject(asset.getJSONArray("metadata").get(0).toString());

       Map<String, String> assetData = new TreeMap<String, String>(){{
            String field = null;
            for(int i = 0; i<jsonAsset.length(); i++){
                field = jsonAsset.names().getString(i);
                put(field, jsonAsset.get(field).toString());
            }
        }};

        try {
            ServerResponse.setLock(true);
            ServerResponse.setBoolWait(true);
            Transaction transaction = null;
            transaction = BigchainDbTransactionBuilder
                    .init()
                    .addOutput(String.valueOf(amount), edDSAPublicKey)
                    .addAssets(assetData, TreeMap.class)
                    .operation(Operations.CREATE)
                    .buildAndSign(edDSAPublicKey, edDSAPrivateKey)
                    .sendTransaction(ServerResponse.handleServerResponse());
            System.out.print(Api.DRIVERNAME+" Creating Token... "+ transaction.getId()+" ");
            ServerResponse.waitDone();
            ServerResponse.setLock(false);
            if(ServerResponse.isResult()){
                return transaction.getId();
            }
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *  Transfers a token in the BigChainDB Network.
     *
     * @param url Receives a <b>URL:PORT</b> Address from a bigchainDB Server
     *               <br>&emsp;&emsp; (e.g., "http://testchain.chon.group:9984")
     * @param senderPrivateKey Receives a Base58 format <b>Private Key</b> from the sender wallet in the BigChainDB Network
     *                            <br>&emsp;&emsp; (e.g., "A4BAzNZdGBqkGHQP7gWovcYaGP3UELL6f4THhuHSQHCK").
     * @param senderPublicKey Receives a Base58 format <b>Public Key</b> from the sender wallet in the BigChainDB Network
     *                           <br>&emsp;&emsp; (e.g., "arDLZkDEmZi5wWtebvZrC5KgSs2tX8ULLeBSFp8dTJR").
     * @param tokenID    Receives the <b>ASSET-ID</b> from the token that wants to transfer to another wallet
     *                  <br>&emsp;&emsp; (e.g., "095ee8b3a8599fabe61a09c1c046df7b8af3cb4f70b815222daad132988bd67d").
     * @param recipientPublicKey    Receives a Base58 format <b>Public Key</b> from the receiver wallet in the BigChainDB Network
     *                              <br>&emsp;&emsp; (e.g, "FNJPJdtuPQYsqHG6tuUjKjqv7SW84U4ipiyyLV2j6MEW").
     * @param amount Receives the number of tokens that will transfer.
     *
     * @return Return the <b>TRANSFER-ID</b> case every crypto-conditions are fulfilled in the transaction at BigChainDB Network
     *          <br>&emsp;&emsp; (e.g, "9019f938c175ad768f76b3ce4842a066a99198f68dc3078b0fab58349ece5896").
     */
    public String transferToken(String url,
                                String senderPrivateKey,
                                String senderPublicKey,
                                String tokenID,
                                String recipientPublicKey,
                                Integer amount) {
        setConfig(url);
        EdDSAPrivateKey bobPrivateKey = keyManagement.stringToEdDSAPrivateKey(senderPrivateKey,"base58");
        EdDSAPublicKey bobPublicKey = keyManagement.stringToEdDSAPublicKey(senderPublicKey, "base58");
        EdDSAPublicKey alicePublicKey = keyManagement.stringToEdDSAPublicKey(recipientPublicKey, "base58");

        String owner = KeyPairUtils.encodePublicKeyInBase58(bobPublicKey);
        String strTransactionID = null;
        try{
            strTransactionID = getLastTransaction(bobPrivateKey,bobPublicKey,tokenID);
        }catch (Exception exception){
            System.out.println("Erro ao encontrar a ultima transação do ativo");
        }

        Long balance = 0L;
        Integer outputID = 0;
        Transaction T;
        try {
            T = com.bigchaindb.api.TransactionsApi.getTransactionById(strTransactionID);
            for (int i=0; i<T.getOutputs().size(); i++){
                if(owner.equals(T.getOutputs().get(i).getPublicKeys().get(0))){
                    outputID = i;
                    balance = Long.parseLong(T.getOutputs().get(i).getAmount());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransactionNotFoundException e) {
            throw new RuntimeException(e);
        }


        if(Integer.toUnsignedLong(amount)<=balance){
            Long newBalance = balance-Integer.toUnsignedLong(amount);
            FulFill spendFrom = new FulFill();
            spendFrom.setTransactionId(strTransactionID);
            spendFrom.setOutputIndex(outputID);
            Transaction transferTransaction = null;
            try {
                ServerResponse.setLock(true);
                ServerResponse.setBoolWait(true);
                if(newBalance==0){
                    transferTransaction = BigchainDbTransactionBuilder
                            .init()
                            .addInput(null, spendFrom, bobPublicKey)
                            .addAssets(tokenID, String.class)
                            .addOutput(amount.toString(),alicePublicKey)
                            .operation(Operations.TRANSFER)
                            .buildAndSign(bobPublicKey, bobPrivateKey)
                            .sendTransaction(ServerResponse.handleServerResponse());
                }else if(newBalance>0){
                    transferTransaction = BigchainDbTransactionBuilder
                            .init()
                            .addInput(null, spendFrom, bobPublicKey)
                            .addAssets(tokenID, String.class)
                            .addOutput(String.valueOf(newBalance),bobPublicKey)
                            .addOutput(amount.toString(),alicePublicKey)
                            .operation(Operations.TRANSFER)
                            .buildAndSign(bobPublicKey, bobPrivateKey)
                            .sendTransaction(ServerResponse.handleServerResponse());
                }
                System.out.print(Api.DRIVERNAME+" Transfer Token... "+transferTransaction.getId()+" ");
                ServerResponse.waitDone();
                ServerResponse.setLock(false);
                return transferTransaction.getId();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }else{
            System.out.println(Api.DRIVERNAME+" Transfer Token... "+strTransactionID+" [malformed] [insufficient funds]");
            return null;
        }
    }

    /**
     * Creates a non-fungible token in the BigChainDB Network.
     *
     * @param url Receives a <b>URL:PORT</b> Address from a BigChainDB Server
     *            <br>&emsp;&emsp; (e.g., "http://testchain.chon.group:9984").
     * @param privateKey Receives a Base58 format <b>Private Key</b> from the owner wallet in the BigChainDB Network
     *                   <br>&emsp;&emsp; (e.g., "A4BAzNZdGBqkGHQP7gWovcYaGP3UELL6f4THhuHSQHCK").
     * @param publicKey Receives a Base58 format <b>Public Key</b> from the owner wallet in the BigChainDB Network
     *                  <br>&emsp;&emsp; (e.g., "arDLZkDEmZi5wWtebvZrC5KgSs2tX8ULLeBSFp8dTJR").
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
    public String deployNFT(String url,
                            String privateKey,
                            String publicKey,
                            String nonFungibleToken){
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

    /**
     * Transfers a non-fungible token in the BigChainDB Network.
     *
     * @param url Receives a <b>URL:PORT</b> Address from a bigchainDB Server
     *               <br>&emsp;&emsp; (e.g., "http://testchain.chon.group:9984").
     * @param senderPrivateKey Receives a Base58 format <b>Private Key</b> from the sender wallet in the BigChainDB Network
     *                            <br>&emsp;&emsp; (e.g., "A4BAzNZdGBqkGHQP7gWovcYaGP3UELL6f4THhuHSQHCK").
     * @param senderPublicKey Receives a Base58 format <b>Public Key</b> from the sender wallet in the BigChainDB Network
     *                           <br>&emsp;&emsp; (e.g., "arDLZkDEmZi5wWtebvZrC5KgSs2tX8ULLeBSFp8dTJR").
     * @param nftID Receives the <b>ASSET-ID</b> from the non-fungible token that wants to transfer to another wallet
     *                  <br>&emsp;&emsp; (e.g., "095ee8b3a8599fabe61a09c1c046df7b8af3cb4f70b815222daad132988bd67d").
     * @param transferMetadata Receives the <b>Metadata Array</b> about the transfer of the non-fungible token in JSON format
     *                      <br>&emsp;&emsp; (e.g., "{\"metadata\":[{\"new owner\":\"Agent Alice\",
     *                       <br>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp; \"location\":\"Rio de Janeiro\",
     *                       <br>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp; \"value_btc\":\"2100\",
     *                       <br>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp; \"value_eur\":\"30000000€\"
     *                       <br>&emsp;&emsp;&emsp;&emsp; }]}").
     * @param recipientPublicKey Receives a Base58 format <b>Public Key</b> from the receiver wallet in the BigChainDB Network
     *                              <br>&emsp;&emsp; (e.g, "FNJPJdtuPQYsqHG6tuUjKjqv7SW84U4ipiyyLV2j6MEW").
     *                              <br>
     *                              <br>
     * @return Return the <b>TRANSFER-ID</b> case every crypto-conditions are fulfilled in the transaction at BigChainDB Network
     *          <br>&emsp;&emsp; (e.g, "9019f938c175ad768f76b3ce4842a066a99198f68dc3078b0fab58349ece5896").
     * */
    public String transferNFT(String url,
                              String senderPrivateKey,
                              String senderPublicKey,
                              String nftID,
                              String transferMetadata,
                              String recipientPublicKey) {
        try {
            setConfig(url);
            return newTransfer(
                    keyManagement.stringToEdDSAPrivateKey(senderPrivateKey,"base58"),
                    keyManagement.stringToEdDSAPublicKey(senderPublicKey,"base58"),
                    nftID,
                    new JSONObject(transferMetadata),
                    keyManagement.stringToEdDSAPublicKey(recipientPublicKey,"base58"));
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Stamps a transaction
     *
     * @param url   Receives a <b>URL:PORT</b> Address from a bigchainDB Server
     *               <br>&emsp;&emsp; (e.g., "http://testchain.chon.group:9984")
     * @param privateKey Receives a Base58 format <b>Private Key</b> from the sender wallet in the BigChainDB Network
     *              <br>&emsp;&emsp; (e.g., "A4BAzNZdGBqkGHQP7gWovcYaGP3UELL6f4THhuHSQHCK").
     * @param publicKey Receives a Base58 format <b>Public Key</b> from the sender wallet in the BigChainDB Network
     *              <br>&emsp;&emsp; (e.g., "arDLZkDEmZi5wWtebvZrC5KgSs2tX8ULLeBSFp8dTJR").
     * @param transaction    Receives a Transaction Object.
     *
     * @return Return the <b>TRANSFER-ID</b> case every crypto-conditions are fulfilled in the transaction at BigChainDB Network
     *          <br>&emsp;&emsp; (e.g, "9019f938c175ad768f76b3ce4842a066a99198f68dc3078b0fab58349ece5896").
     */
    public String stampTransaction(String url,
                                   String privateKey,
                                   String publicKey,
                                   Transaction transaction){

        Input input = transaction.getInputs().get(0);
        if(publicKey.equals(input.getOwnersBefore().get(0))){
            return null;
        }else{
            try {

                List<Output> outputs = transaction.getOutputs();
                String tokenID = transaction.getAsset().getId();
                Integer outputIndex = 0;
                Long amount = 0L;

                for(int i=0; i<transaction.getOutputs().size(); i++){
                    List<String> publicKeysOutput =  outputs.get(i).getPublicKeys();
                    for (int j=0; j<publicKeysOutput.size(); j++){
                        String outputOwner = publicKeysOutput.get(j);
                        if(outputOwner.equals(publicKey)){
                            outputIndex = i;
                            amount = Long.parseLong(outputs.get(i).getAmount());
                        }
                    }
                }
                KeyManagement keyManagement = new KeyManagement();
                EdDSAPublicKey edDSAPublicKey = keyManagement.stringToEdDSAPublicKey(publicKey,"base58");
                EdDSAPrivateKey edDSAPrivateKey = keyManagement.stringToEdDSAPrivateKey(privateKey,"base58");


                FulFill spendFrom = new FulFill();
                spendFrom.setTransactionId(transaction.getId());
                spendFrom.setOutputIndex(outputIndex);

                ServerResponse.setLock(true);
                ServerResponse.setBoolWait(true);
                Transaction transferTransaction = BigchainDbTransactionBuilder
                        .init()
                        .addInput(null, spendFrom, edDSAPublicKey)
                        .addAssets(tokenID, String.class)
                        .addOutput(amount.toString(),edDSAPublicKey)
                        .operation(Operations.TRANSFER)
                        .buildAndSign(edDSAPublicKey,edDSAPrivateKey)
                        .sendTransaction(ServerResponse.handleServerResponse());

                System.out.print(Api.DRIVERNAME + " Stamp Transaction... " + transferTransaction.getId() + " ");
                ServerResponse.waitDone();
                ServerResponse.setLock(false);
                if (ServerResponse.isResult()) {
                    return transferTransaction.getId();
                } else {
                    return null;
                }
            }catch (Exception ex){
                return null;
            }

        }
    }

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
        //metaData.setMetaData("timestamp", Long.toString(System.currentTimeMillis()));

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

            System.out.print(Api.DRIVERNAME+" Transfer Asset... "+transferTransaction.getId()+" ");
            ServerResponse.waitDone();
            ServerResponse.setLock(false);
            if(ServerResponse.isResult()){
                return transferTransaction.getId();
            }else{
                return null;
            }

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
        //metaData.setMetaData("timestamp", Long.toString(System.currentTimeMillis()));
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
            System.out.print(Api.DRIVERNAME+" Creating Asset... "+ transaction.getId()+" ");
            ServerResponse.waitDone();
            ServerResponse.setLock(false);
            if(ServerResponse.isResult()){
                return transaction.getId();
            }else{
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getLastTransaction(
            EdDSAPrivateKey bobPrivateKey,
            EdDSAPublicKey bobPublicKey,
            String strTokenID) throws IOException, TransactionNotFoundException {

        String bobKeyEncoded = KeyPairUtils.encodePublicKeyInBase58(bobPublicKey);
        Outputs outputs = com.bigchaindb.api.OutputsApi.getUnspentOutputs(bobKeyEncoded);
        Integer totalOutputs = outputs.getOutput().size();

        ArrayList<String> listOpenTransactionsFromTokenID = new ArrayList<String>();
        ArrayList<Integer> listOutputIndexFromTokenID = new ArrayList<Integer>();
        ArrayList<Long> listAmountFromTokenID = new ArrayList<Long>();


        for (int i = 0; i<totalOutputs; i++){
            Output output = outputs.getOutput().get(i);
            String strTransaction = output.getTransactionId();
            Transaction transaction  = com.bigchaindb.api.TransactionsApi.getTransactionById(strTransaction);
            String strAssetID = transaction.getAsset().getId();
            if(strTokenID.equals(strAssetID)){
                listOpenTransactionsFromTokenID.add(strTransaction);
                Integer outputINDEX = output.getOutputIndex();
                Long outputAmount = Long.parseLong(transaction.getOutputs().get(outputINDEX).getAmount());
                listOutputIndexFromTokenID.add(outputINDEX);
                listAmountFromTokenID.add(outputAmount);
            }
        }

        Integer opens = listOpenTransactionsFromTokenID.size();
        if(opens==0){
            return strTokenID;
        }else if(opens==1){
            return listOpenTransactionsFromTokenID.get(0);
        }else if(opens>1){
            return mergeFungibleToken(
                    bobPrivateKey,
                    bobPublicKey,
                    strTokenID,
                    listOpenTransactionsFromTokenID,
                    listOutputIndexFromTokenID,
                    listAmountFromTokenID);
        }else{
            return null;
        }
    }

    /**
     * Merges a list of opens OUTPUT from the same ASSET in a unique transaction.
     *
     * @param bobPrivateKey Receives a Base58 private key from the owner.
     * @param bobPublicKey Receives a Base58 public key from the owner.
     * @param assetID Receives the ID from the ASSET that will be merged.
     * @param listOfTransactions List of Transactions with open OUTPUT about the ASSET.
     * @param listOfOutputIndexes List of OUTPUT-INDEX from open OUTPUT in the transaction.
     * @param listOfAmounts List of Amounts about the open OUTPUT in the transaction.
     *
     * @return The TRANSACTION-ID that unified all the OUTPUTS, generating a single OUTPUT with the sum of the amounts.
     */
    public String mergeFungibleToken(
            EdDSAPrivateKey bobPrivateKey,
            EdDSAPublicKey bobPublicKey,
            String assetID,
            ArrayList<String> listOfTransactions,
            ArrayList<Integer> listOfOutputIndexes,
            ArrayList<Long> listOfAmounts){

        ServerResponse.setLock(true);
        ServerResponse.setBoolWait(true);
        Integer totalInputs = listOfTransactions.size();
        Long totalAmount = 0L;
        FulFill[] spendsFrom = new FulFill[totalInputs];
        for(int i=0; i<totalInputs; i++){
            spendsFrom[i] = new FulFill();
            spendsFrom[i].setTransactionId(listOfTransactions.get(i));
            spendsFrom[i].setOutputIndex(listOfOutputIndexes.get(i));
            totalAmount = totalAmount+listOfAmounts.get(i);
        }
        Transaction transferTransaction = null;
        try{
            transferTransaction = BigchainDbTransactionBuilder
                    .init()
                    .addInputs(null,spendsFrom,bobPublicKey)
                    .addAssets(assetID, String.class)
                    .addOutput(String.valueOf(totalAmount),bobPublicKey)
                    .operation(Operations.TRANSFER)
                    .buildAndSign(bobPublicKey, bobPrivateKey)
                    .sendTransaction(ServerResponse.handleServerResponse());
            System.out.print(Api.DRIVERNAME+" Merging Tokens... "+transferTransaction.getId()+" ");
            ServerResponse.waitDone();
            ServerResponse.setLock(false);
            return transferTransaction.getId();
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }


}
