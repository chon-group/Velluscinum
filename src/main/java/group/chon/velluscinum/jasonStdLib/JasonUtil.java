package group.chon.velluscinum.jasonStdLib;
import group.chon.velluscinum.model.TokenContent;
import group.chon.velluscinum.model.WalletContent;
import jason.asSyntax.Term;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Jason Utilities
 */
public class JasonUtil {
    /**
     * Convert a Term Array in a String Array.
     * @param args Term Array
     * @return String Array
     */
    public String[] toArray(Term[] args){
        String[] termArray = new String[args.length];
        for (int i=0; i<args.length; i++){
            termArray[i]=args[i].toString().replace("\"", "");
        }
        return termArray;
    }

    public String newBelief(String strBelief, String strTransaction){
        String last = strBelief.substring(strBelief.length()-1,strBelief.length());
        if(last.equals(")")){
            return strBelief.substring(0,strBelief.length()-1)+",\""+strTransaction+"\")";
        }else{
            return strBelief+"(\""+strTransaction+"\")";
        }
    }

    public String newBelief(String strBelief, TokenContent token){
            return strBelief+"("+token.getTokenContent()+")";
    }

    public String newBelief(String strBelief, String strArg1, String strArg2){
        return newBelief(strBelief,strArg1+"\",\""+strArg2);
    }

    public String newBelief(String strBelief, String strArg1, Long longBalance){
        String last = strBelief.substring(strBelief.length()-1,strBelief.length());
        if(last.equals(")")){
            return strBelief.substring(0,strBelief.length()-1)+",\""+strArg1+"\","+longBalance.toString()+")";
        }else{
            return strBelief+"(\""+strArg1+"\","+longBalance.toString()+")";
        }
    }

    public String newBelief(String strBelief, ArrayList<WalletContent> content){

        String onlyOneString = "[";
        for (int i=0; i<content.size();i++){
            onlyOneString = onlyOneString+"["+content.get(i).getType()+
                    ",\""+content.get(i).getToken()+
                    "\","+content.get(i).getAmount()+"]";
            if(i<content.size()-1){
                onlyOneString = onlyOneString+",";
            }
        }
        onlyOneString = onlyOneString+"]";

        String last = strBelief.substring(strBelief.length()-1,strBelief.length());
        if(last.equals(")")){
            return strBelief.substring(0,strBelief.length()-1)+onlyOneString+")";
        }else{
            return strBelief+"("+onlyOneString+")";
        }
    }
    public void lock(boolean lock){
        try {
            File file = new File("velluscinum.lock");
            if (lock) {
                file.createNewFile();
            } else {
                file.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isLocked(){
        Random randI = new Random();
        try {
            Thread.sleep(500+randI.nextInt(3000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        File file = new File("velluscinum.lock");
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }
    
    public void storePrivateKey(String agName, String content) throws IOException {
        storeKey(agName+".privateKey",content);
    }
    public void storePublicKey(String agName, String content) throws IOException {
        storeKey(agName+".publicKey",content);
    }

    private void storeKey(String fileName, String content) throws IOException{
        try(FileWriter writer = new FileWriter(fileName)){
            writer.write(content);
        }
    }

    public JSONObject parseVerification(String expr) {
        System.out.println("Expressao recebida... "+expr);

        JSONObject root = new JSONObject();

        // Extrair operação
        int firstParen = expr.indexOf("(");
        String operation = expr.substring(0, firstParen).trim();
        root.put("operation", operation);

        // Conteúdo interno
        String inner = expr.substring(firstParen + 1, expr.lastIndexOf(")"));
        String[] parts = inner.split("\\),");

        JSONObject verificationObj = new JSONObject();
        for (String part : parts) {
            part = part.replace(")", "");
            int p1 = part.indexOf("(");
            String key = part.substring(0, p1).trim();
            String value = part.substring(p1 + 1).trim();
            verificationObj.put(key, value);
        }

        // Coloca dentro de um array
        JSONArray verificationArr = new JSONArray();
        verificationArr.put(verificationObj);

        root.put("fulfill", verificationArr);

        return root;
    }

}
