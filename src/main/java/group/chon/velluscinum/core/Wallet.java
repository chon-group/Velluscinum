package group.chon.velluscinum.core;

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
            }else{
                /*
                    In this case is an Asset without transaction
                    TO DO
                 */
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
            BigchainDBDriver bigchainDBDriver = new BigchainDBDriver();
            for (int i=0;i< listToMerge.size();i++){
                bigchainDBDriver.joinTokens(bobPrivateKey,bobPublicKey, listToMerge.get(i));
            }
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
}
