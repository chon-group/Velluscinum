package group.chon.velluscinum;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * BigChainDB Asset and Transfer Builder for Jason Agents.
 *
 * @author Nilson Mori
 */
public class Asset {
    private JSONObject asset;
    private JSONObject metadata;
    private JSONObject transferMetadata;

    /**
     *  Generates an initial well-formatted non-fungible Token Object.
     *
     * @param key    Receives the key parameter from the key-value format.
     *
     * @param value  Receives the value parameter from the key-value format.
     */
    public void buildAsset(String key, String value){
        this.asset = new JSONObject();
        this.metadata = new JSONObject();
        addImmutableInformation(key,value);
    }

    /**
     * Adds at the Asset Object a key-value Immutable Information.
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
     * Adds at the Asset Object a key-value Additional (mutable) Information.
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
     *  Converts the Asset Object in a well-formatted String JSON.
     *
     * @return the String NFT Object.
     */
    public String assetToString(){
        return toJSONObject().toString();
    }

    /**
     *  Imports an Asset Object from a file.
     *
     * @param filePath Receives the file path.
     *
     * @return the Asset Object in JSON format.
     */
    public JSONObject importAssetFromFile(String filePath) {
        System.out.println(" Load Asset from file... "+filePath);
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

    /**
     * Generates an initial well-formatted Transfer Metadata Object.
     *
     * @param key Receives the key parameter from the key-value format.
     *
     * @param value Receives the value parameter from the key-value format.
     */
    public void buildTransfer(String key, String value){
        this.transferMetadata = new JSONObject();
        addTransferInformation(key,value);
    }

    /**
     * Adds at the Transfer Metadata Object a key-value Information.
     *
     * @param key Receives the key parameter from the key-value format.
     * @param value Receives the value parameter from the key-value format.
     *
     */
    public void addTransferInformation(String key, String value) {
        this.transferMetadata.put(key,value);
    }

    /**
     *  Converts the Transfer Metadata Object in a well-formatted String JSON.
     *
     * @return the String Transfer Metadata Object.
     */
    public String transferToString(){
        JSONArray jsonArrayMetadata = new JSONArray();
        jsonArrayMetadata.put(this.transferMetadata);

        JSONObject out = new JSONObject();
        out.put("metadata",jsonArrayMetadata);

        return out.toString();
    }

    /**
     *  Imports a Transfer Metadata Object from a file.
     *
     * @param filePath Receives the file path.
     *
     * @return the Transfer Metada Object in JSON format.
     */
    public JSONObject importTransferFromFile(String filePath) {
        System.out.println(" Load TransfInfo from file... "+filePath);

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

    /**
     * Creates a file with the Transfer Information
     *
     * @param filePath The path of the file that will create.
     */
    public void transferToFile(String filePath){
        toFile(filePath,transferToString());
    }

    /**
     * Creates a file with the Asset Data and MetaData
     *
     * @param filePath The path of the file that will create.
     */
    public void assetToFile(String filePath){
        toFile(filePath,assetToString());
    }
    private void toFile(String filePath, String data){
        byte[] byteData = data.getBytes();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            fos.write(byteData);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
