package group.chon.velluscinum.model;

import group.chon.velluscinum.core.BigchainDBDriver;
import org.json.JSONObject;

public class TokenContent {
    JSONObject token;

    public void loadToken(String bigChainDBServer, String assetID, String content){
        BigchainDBDriver driver = new BigchainDBDriver(bigChainDBServer);
        token = driver.getContentFromAsset(assetID,content);
    }

    public String getTokenContent(){
        String out = "[";
        for (int i=0; i<token.length();i++){
            out = out+"[\""+
                    token.names().getString(i)+"\",\""+
                    token.get(token.names().getString(i)).toString()+"\"]";
            if(i<token.length()-1){
                out = out+",";
            }
        }
        out = out+"]";
        return out;
    }

}