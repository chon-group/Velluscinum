package group.chon.velluscinum.model;

import group.chon.velluscinum.core.BigchainDBDriver;
import org.json.JSONArray;
import org.json.JSONObject;

public class TransactionContent {
    JSONObject transaction;

    public void loadTransaction(String bigChainDBServer, String transactionID){
        BigchainDBDriver driver = new BigchainDBDriver(bigChainDBServer);
        transaction = driver.getTransactionFromID(transactionID);
    }

    public String transactionToString(){
        return transaction.toString();
    }

    public String getAssetID(){
        return transaction.getJSONObject("asset").getString("id");
    }

    public JSONArray getOutputs(){
        return transaction.getJSONArray("outputs");
    }

    public JSONObject getOutputByPublicKey(String publicKey){
        for (int i = 0; i < getOutputs().length(); i++) {
            JSONObject output = (JSONObject) getOutputs().get(i);
            JSONArray publicKeys = output.getJSONArray("public_keys");
            for (int j = 0; j < publicKeys.length(); j++) {
                if(publicKeys.get(j).toString().equals(publicKey)){
                    return output;
                }
            }
        }
        return null;
    }

    public Long getOutputAmountByPublicKey(String publicKey){
        JSONObject output = getOutputByPublicKey(publicKey);
        return output.getLong("amount");
    }

    public JSONArray getInputs(){
        return transaction.getJSONArray("inputs");
    }

    public String getFirstOwnerBefore(){
            JSONObject input = (JSONObject) getInputs().get(0);
            JSONArray publicKeys = input.getJSONArray("owners_before");
            return publicKeys.get(0).toString();
    }
}

