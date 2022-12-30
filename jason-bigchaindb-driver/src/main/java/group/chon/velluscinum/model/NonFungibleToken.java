package group.chon.velluscinum.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;

public class NonFungibleToken {
    private JSONObject asset;
    private JSONObject metadata;

    public void newNFT(String strKey, String strValue){
        this.asset = new JSONObject();
        this.metadata = new JSONObject();
        addImmutableInformation(strKey,strValue);
    }

    public void addImmutableInformation(String strKey, String strValue) {
        this.asset.put(strKey,strValue);
    }

    public void addAdditionalInformation(String strKey, String strValue) {
        this.metadata.put(strKey,strValue);
    }

    public JSONObject toJSONObject(){
        JSONArray jsonArrayAsset = new JSONArray();
        jsonArrayAsset.put(this.asset);

        JSONArray jsonArrayMetadata = new JSONArray();
        jsonArrayMetadata.put(this.metadata);

        JSONObject out = new JSONObject();
        out.put("asset",jsonArrayAsset);
        out.put("metadata",jsonArrayMetadata);
        return out;
    }

    public String toString(){
        return toJSONObject().toString();
    }


    public JSONObject importFromFile(String filePath) {
        System.out.println(" Load NFT from file... "+filePath);

        byte[] assetReadFromFile = null;
        JSONObject assetJSON = null;
        FileInputStream is = null;
        try {
            is = new FileInputStream(filePath);
            assetReadFromFile = is.readAllBytes();
            if (assetReadFromFile != null) {
                assetJSON = new JSONObject(new String(assetReadFromFile));
            }
            is.close();
            return assetJSON;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
