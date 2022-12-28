package group.chon.velluscinum;

import com.bigchaindb.builders.BigchainDbConfigBuilder;
import com.bigchaindb.builders.BigchainDbTransactionBuilder;
import com.bigchaindb.constants.Operations;
import com.bigchaindb.exceptions.TransactionNotFoundException;
import com.bigchaindb.model.*;
import com.bigchaindb.util.KeyPairUtils;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import java.io.IOException;
import java.util.*;

public class Vellus{
    Info driver = new Info();
    KeyManagement keyManagement = new KeyManagement();
    JasonBigchaindbDriver jasonBigchaindbDriver = new JasonBigchaindbDriver();
    public void setConfig(String serverURL) {
        jasonBigchaindbDriver.setConfig(serverURL);
    }
    public Outputs getOpenOutputs(EdDSAPublicKey bobPublicKey){
        try {
            return com.bigchaindb.api.OutputsApi.getUnspentOutputs(
                    keyManagement.getAddressFromPublicKey(bobPublicKey));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Transaction getTransaction(String strTransaction){
        try {
            return com.bigchaindb.api.TransactionsApi.getTransactionById(strTransaction);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TransactionNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public String createFungibleToken(EdDSAPrivateKey privateKey,
                            EdDSAPublicKey publicKey,
                            String tokenName,
                            Long longAmount) throws Exception {

        Map<String, String> cryptocurrency = new TreeMap<String, String>(){{
            put("cryptocurrency", tokenName);
        }};

        MetaData metaData = new MetaData();
        metaData.setMetaData("timestamp", Long.toString(System.currentTimeMillis()));

        try {
            ServerResponse.setLock(true);
            ServerResponse.setBoolWait(true);
            Transaction transaction = null;
            transaction = BigchainDbTransactionBuilder
                    .init()
                    .addOutput(String.valueOf(longAmount), publicKey)
                    .addAssets(cryptocurrency, TreeMap.class)
                    .addMetaData(metaData)
                    .operation(Operations.CREATE)
                    .buildAndSign(publicKey, privateKey)
                    .sendTransaction(ServerResponse.handleServerResponse());
            System.out.print(driver.getDRIVERNAME()+" Creating Token....... "+ transaction.getId()+" ");
            ServerResponse.waitDone();
            ServerResponse.setLock(false);
            return transaction.getId();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String transferToken(EdDSAPrivateKey bobPrivateKey,
                      EdDSAPublicKey bobPublicKey,
                      String strAssetID,
                      EdDSAPublicKey alicePublicKey,
                      Integer amount) throws TransactionNotFoundException, IOException {

        String owner = KeyPairUtils.encodePublicKeyInBase58(bobPublicKey);
        String strTransactionID = getLastTransaction(bobPrivateKey,bobPublicKey,strAssetID);
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
                            .addAssets(strAssetID, String.class)
                            .addOutput(amount.toString(),alicePublicKey)
                            .operation(Operations.TRANSFER)
                            .buildAndSign(bobPublicKey, bobPrivateKey)
                            .sendTransaction(ServerResponse.handleServerResponse());
                }else if(newBalance>0){
                    transferTransaction = BigchainDbTransactionBuilder
                            .init()
                            .addInput(null, spendFrom, bobPublicKey)
                            .addAssets(strAssetID, String.class)
                            .addOutput(String.valueOf(newBalance),bobPublicKey)
                            .addOutput(amount.toString(),alicePublicKey)
                            .operation(Operations.TRANSFER)
                            .buildAndSign(bobPublicKey, bobPrivateKey)
                            .sendTransaction(ServerResponse.handleServerResponse());
                }
                System.out.print(driver.getDRIVERNAME()+" Transferring Token... "+transferTransaction.getId()+" ");
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
            System.out.println(" Insufficient funds");
            return null;
        }
    }


    private String mergeFungibleToken(
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
            System.out.print(driver.getDRIVERNAME()+" Merging Tokens....... "+transferTransaction.getId()+" ");
            ServerResponse.waitDone();
            ServerResponse.setLock(false);
            return transferTransaction.getId();
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public String getLastTransaction(
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


    public void mergeMultiplesTokens(
            EdDSAPrivateKey bobPrivateKey,
            EdDSAPublicKey bobPublicKey,
            ArrayList<String> listOfTokens
    ){
        Outputs listOfOutputs = getOpenOutputs(bobPublicKey);
        ArrayList<String> listOpenTransactionsFromTokenID = new ArrayList<String>();
        ArrayList<Integer> listOutputIndexFromTokenID = new ArrayList<Integer>();
        ArrayList<Long> listAmountFromTokenID = new ArrayList<Long>();

        Output output = null;
        String strTransaction = null;
        Transaction transaction = null;
        Integer outputINDEX = null;

        for (int j=0; j<listOfTokens.size();j++){
            for (int i = 0; i<listOfOutputs.getOutput().size(); i++){
                output = listOfOutputs.getOutput().get(i);
                strTransaction = output.getTransactionId();
                transaction  = getTransaction(strTransaction);
                if(listOfTokens.get(j).equals(transaction.getAsset().getId())){
                    listOpenTransactionsFromTokenID.add(strTransaction);
                    outputINDEX = output.getOutputIndex();
                    listOutputIndexFromTokenID.add(outputINDEX);
                    listAmountFromTokenID.add(Long.parseLong(transaction.getOutputs().get(outputINDEX).getAmount()));
                }
            }

            if(listOpenTransactionsFromTokenID.size()>1){
                mergeFungibleToken(
                        bobPrivateKey,
                        bobPublicKey,
                        transaction.getAsset().getId(),
                        listOpenTransactionsFromTokenID,
                        listOutputIndexFromTokenID,
                        listAmountFromTokenID);
            }

            listOpenTransactionsFromTokenID.clear();
            listOutputIndexFromTokenID.clear();
            listAmountFromTokenID.clear();
        }

    }

}
