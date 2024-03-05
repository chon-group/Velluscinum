package group.chon.velluscinum.jasonStdLib;
import group.chon.velluscinum.core.WalletContent;
import jason.asSyntax.Term;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
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
}
