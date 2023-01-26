package group.chon.velluscinum;

import com.bigchaindb.model.Output;
import com.bigchaindb.model.Outputs;
import com.bigchaindb.model.Transaction;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import java.util.ArrayList;
import java.util.Collections;

public class Wallet {
    KeyManagement keyManagement = new KeyManagement();
    Vellus vellus = new Vellus();

    public void setConfig(String serverURL) {
        vellus.setConfig(serverURL);
    }

    public ArrayList<WalletContent> getMyTokens(
            EdDSAPrivateKey bobPrivateKey,
            EdDSAPublicKey bobPublicKey
    ){
        return getMyTokens(bobPrivateKey,bobPublicKey,true);
    }
    private ArrayList<WalletContent> getMyTokens(
            EdDSAPrivateKey bobPrivateKey,
            EdDSAPublicKey bobPublicKey,
            Boolean isNeedMerge
            ){

        Outputs openOutputs = vellus.getOpenOutputs(bobPublicKey);
        ArrayList<WalletContent> walletContents = new ArrayList<WalletContent>();
        ArrayList<String> tokens = new ArrayList<String>();

        Output output = null;
        Transaction transaction = null;
        for (int i = 0; i<openOutputs.getOutput().size(); i++){
            output = openOutputs.getOutput().get(i);
            transaction = vellus.getTransaction(output.getTransactionId());
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
            if(didNeedMerge(bobPrivateKey,bobPublicKey,tokens)){
                return getMyTokens(bobPrivateKey,bobPublicKey,false);
            }else{
                return walletContents;
            }
        }else{
            return walletContents;
        }


    }

    private boolean didNeedMerge(
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
            vellus.mergeMultiplesTokens(bobPrivateKey,bobPublicKey,listToMerge);
            return true;
        }else{
            return false;
        }
    }
}
