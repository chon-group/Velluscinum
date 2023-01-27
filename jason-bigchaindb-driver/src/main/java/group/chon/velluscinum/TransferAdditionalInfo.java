package group.chon.velluscinum;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * BigChainDB TRANSFER metadata information manager for Jason Agents.
 *
 * @author Nilson Mori
 */
public class TransferAdditionalInfo {
    JSONObject metadata;

    /**
     * Generates an initial well-formatted Transfer Metadata Object.
     *
     * @param key Receives the key parameter from the key-value format.
     *
     * @param value Receives the value parameter from the key-value format.
     */
    public void newTransferInfo(String key, String value){
        this.metadata = new JSONObject();
        addAdditionalInformation(key,value);
    }

    /**
     * Adds at the Transfer Metadata Object a key-value Information.
     *
     * @param key Receives the key parameter from the key-value format.
     * @param value Receives the value parameter from the key-value format.
     *
     */
    public void addAdditionalInformation(String key, String value) {
        this.metadata.put(key,value);
    }

    /**
     *  Converts the Transfer Metadata Object in a well-formatted String JSON.
     *
     * @return the String Transfer Metadata Object.
     */
    public String toString(){
        JSONArray jsonArrayMetadata = new JSONArray();
        jsonArrayMetadata.put(this.metadata);

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
    public JSONObject importFromFile(String filePath) {
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


}
