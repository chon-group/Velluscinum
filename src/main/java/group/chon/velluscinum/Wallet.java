package group.chon.velluscinum;

import com.bigchaindb.builders.BigchainDbConfigBuilder;
import com.bigchaindb.exceptions.TransactionNotFoundException;
import com.bigchaindb.model.Output;
import com.bigchaindb.model.Outputs;
import com.bigchaindb.model.Transaction;
import com.bigchaindb.util.KeyPairUtils;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * BigChainDB Wallet Manager for Jason Agents.
 *
 * @author Nilson Mori
 */
public class Wallet {
    private KeyManagement keyManagement = new KeyManagement();

    private void setConfig(String serverURL) {
        BigchainDbConfigBuilder
                .baseUrl(serverURL).setup();
    }

    /**
     * Shows the list of Assets in a Wallet.
     * @param url Receives a <b>URL:PORT</b> Address from a bigchainDB Server
     *               <br>&emsp;&emsp; (e.g., "http://testchain.chon.group:9984").
     * @param privateKey Receives a Base58 format <b>Private Key</b> from the wallet in the BigChainDB Network
     *                            <br>&emsp;&emsp; (e.g., "A4BAzNZdGBqkGHQP7gWovcYaGP3UELL6f4THhuHSQHCK").
     * @param publicKey Receives a Base58 format <b>Public Key</b> from the wallet in the BigChainDB Network
     *                           <br>&emsp;&emsp; (e.g., "arDLZkDEmZi5wWtebvZrC5KgSs2tX8ULLeBSFp8dTJR").
     * @return A WalletContent Object List.
     */
    public ArrayList<WalletContent> getMyTokens(String url,
            String privateKey,
            String publicKey){
        EdDSAPrivateKey bobPrivateKey = keyManagement.stringToEdDSAPrivateKey(privateKey,"base58");
        EdDSAPublicKey bobPublicKey = keyManagement.stringToEdDSAPublicKey(publicKey,"base58");

        return getMyTokens(url,bobPrivateKey,bobPublicKey,true);
    }
    private ArrayList<WalletContent> getMyTokens(
            String serverURL,
            EdDSAPrivateKey bobPrivateKey,
            EdDSAPublicKey bobPublicKey,
            Boolean isNeedMerge
            ){

        Outputs openOutputs = getOpenOutputs(serverURL,bobPublicKey);
        ArrayList<WalletContent> walletContents = new ArrayList<WalletContent>();
        ArrayList<String> tokens = new ArrayList<String>();

        Output output = null;
        Transaction transaction = null;
        for (int i = 0; i<openOutputs.getOutput().size(); i++){
            output = openOutputs.getOutput().get(i);
            transaction = getTransaction(serverURL,output.getTransactionId());
            if(transaction.getAsset().getId()!=null){
                tokens.add(transaction.getAsset().getId());
                walletContents.add(
                        new WalletContent(
                                transaction.getAsset().getId(),
                                transaction.getId(),
                                Long.parseLong(
                                        transaction.getOutputs().get(
                                                output.getOutputIndex()
                                        ).getAmount())
                        )
                );
            }
        }

        if(isNeedMerge){
            if(didNeedMerge(serverURL,bobPrivateKey,bobPublicKey,tokens)){
                return getMyTokens(serverURL,bobPrivateKey,bobPublicKey,false);
            }else{
                return walletContents;
            }
        }else{
            return walletContents;
        }


    }

    private boolean didNeedMerge(
            String url,
            EdDSAPrivateKey bobPrivateKey,
            EdDSAPublicKey bobPublicKey,
            ArrayList<String> tokens
    ){
        Collections.sort(tokens);
        ArrayList<String> listToMerge = new ArrayList<String>();
        String lastAsset = "null";
        for (int i=1; i<tokens.size(); i++){
            if(tokens.get(i-1).equals(tokens.get(i))){
                if(!lastAsset.equals(tokens.get(i))){
                    lastAsset = tokens.get(i);
                    listToMerge.add(tokens.get(i));
                }
            }
        }
        if(listToMerge.size()>0){
            mergeMultiplesTokens(url,bobPrivateKey,bobPublicKey,listToMerge);
            return true;
        }else{
            return false;
        }
    }


    private Outputs getOpenOutputs(String url, EdDSAPublicKey bobPublicKey){
        com.bigchaindb.builders.BigchainDbConfigBuilder.baseUrl(url).setup();
        try {
            return com.bigchaindb.api.OutputsApi.getUnspentOutputs(
                    KeyPairUtils.encodePublicKeyInBase58(bobPublicKey));
            //keyManagement.getAddressWallet(bobPublicKey));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private Transaction getTransaction(String url, String strTransaction){
        com.bigchaindb.builders.BigchainDbConfigBuilder.baseUrl(url).setup();
        try {
            return com.bigchaindb.api.TransactionsApi.getTransactionById(strTransaction);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TransactionNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void mergeMultiplesTokens(
            String url,
            EdDSAPrivateKey bobPrivateKey,
            EdDSAPublicKey bobPublicKey,
            ArrayList<String> listOfTokens
    ){
        BigchainDBDriver bigchainDBDriver = new BigchainDBDriver();
        Outputs listOfOutputs = getOpenOutputs(url,bobPublicKey);
        ArrayList<String> listOpenTransactionsFromTokenID = new ArrayList<String>();
        ArrayList<Integer> listOutputIndexFromTokenID = new ArrayList<Integer>();
        ArrayList<Long> listAmountFromTokenID = new ArrayList<Long>();

        Output output = null;
        String strTransaction = null;
        Transaction transaction = null;
        Integer outputINDEX = null;
        Long longAmount = 0L;

        for (int j=0; j<listOfTokens.size();j++){
            for (int i = 0; i<listOfOutputs.getOutput().size(); i++){
                output = listOfOutputs.getOutput().get(i);
                strTransaction = output.getTransactionId();
                transaction  = getTransaction(url,strTransaction);
                if(listOfTokens.get(j).equals(transaction.getAsset().getId())){
                    listOpenTransactionsFromTokenID.add(strTransaction);
                    outputINDEX = output.getOutputIndex();
                    listOutputIndexFromTokenID.add(outputINDEX);
                    longAmount = Long.parseLong(transaction.getOutputs().get(outputINDEX).getAmount());
                    listAmountFromTokenID.add(longAmount);
                }
            }

            if(listOpenTransactionsFromTokenID.size()>1){
                bigchainDBDriver.mergeFungibleToken(
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
