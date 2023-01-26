package group.chon.velluscinum;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;

public class TransfAdditionalInfo {
    JSONObject metadata;

    public void newTransfInfo(String strKey, String strValue){
        this.metadata = new JSONObject();
        addAdditionalInformation(strKey,strValue);
    }

    public void addAdditionalInformation(String strKey, String strValue) {
        this.metadata.put(strKey,strValue);
    }

    public String toString(){
        JSONArray jsonArrayMetadata = new JSONArray();
        jsonArrayMetadata.put(this.metadata);

        JSONObject out = new JSONObject();
        out.put("metadata",jsonArrayMetadata);

        return out.toString();
    }

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
