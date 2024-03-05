package group.chon.velluscinum.core;

import com.bigchaindb.model.Transaction;
import org.json.JSONObject;

public class NFT {
    BigchainDBDriver driver;

    public NFT(){
        /* ???? */
    }

    public void loadNFT(String bigChainDBServer, String assetID, String content){
        /* É NFT? */
        driver = new BigchainDBDriver(bigChainDBServer);
        System.out.println(driver.getContentFromAsset(assetID,content).toString());
    }


    public String getData(){
        return null;
    }

    public String getLastMetada(){
        return null;
    }
}
