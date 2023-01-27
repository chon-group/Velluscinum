package group.chon.velluscinum;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * BigChainDB NFT Manager for Jason Agents.
 *
 * @author Nilson Mori
 */
public class NonFungibleToken {
    private JSONObject asset;
    private JSONObject metadata;

    /**
     *  Generates an initial well-formatted non-fungible Token Object.
     *
     * @param key    Receives the key parameter from the key-value format.
     *
     * @param value  Receives the value parameter from the key-value format.
     */
    public void newNFT(String key, String value){
        this.asset = new JSONObject();
        this.metadata = new JSONObject();
        addImmutableInformation(key,value);
    }

    /**
     * Adds at the Non-Fungible Token Object a key-value Immutable Information.
     *
     * @param key Receives the key parameter from the key-value format.
     *
     * @param value Receives the value parameter from the key-value format.
     *
     */
    public void addImmutableInformation(String key, String value) {
        this.asset.put(key,value);
    }

    /**
     * Adds at the Non-Fungible Token Object a key-value Additional (mutable) Information.
     *
     * @param key Receives the key parameter from the key-value format.
     *
     * @param value Receives the value parameter from the key-value format.
     *
     */
    public void addAdditionalInformation(String key, String value) {
        this.metadata.put(key,value);
    }

    private JSONObject toJSONObject(){
        JSONArray jsonArrayAsset = new JSONArray();
        jsonArrayAsset.put(this.asset);

        JSONArray jsonArrayMetadata = new JSONArray();
        jsonArrayMetadata.put(this.metadata);

        JSONObject out = new JSONObject();
        out.put("asset",jsonArrayAsset);
        out.put("metadata",jsonArrayMetadata);
        return out;
    }

    /**
     *  Converts the NFT Object in a well-formatted String JSON.
     *
     * @return the String NFT Object.
     */
    public String toString(){
        return toJSONObject().toString();
    }

    /**
     *  Imports a NFT Object from a file.
     *
     * @param filePath Receives the file path.
     *
     * @return the NFT Object in JSON format.
     */
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
