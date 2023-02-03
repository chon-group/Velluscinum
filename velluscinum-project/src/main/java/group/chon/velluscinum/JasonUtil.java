package group.chon.velluscinum;
import jason.asSyntax.Term;

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
}
