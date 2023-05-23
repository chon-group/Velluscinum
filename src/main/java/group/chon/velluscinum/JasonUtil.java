package group.chon.velluscinum;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;

import java.io.File;
import java.io.IOException;
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
        File file = new File("velluscinum.lock");
        if (file.exists()) {
            Random randI = new Random();
            try {
                Thread.sleep(1000+randI.nextInt(4000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return true;
        } else {
            return false;
        }
    }
}
