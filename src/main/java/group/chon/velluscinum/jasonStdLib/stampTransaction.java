package group.chon.velluscinum.jasonStdLib;

import group.chon.velluscinum.Api;
import group.chon.velluscinum.jasonStdLib.JasonUtil;
import group.chon.velluscinum.model.TransactionContent;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.Message;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *  .stampTransaction(Server,PrK,PuB,Transaction);
 *  .stampTransaction(Server,PrK,PuB,Transaction,all(asset(Token),amount(Amount),ownerBefore(Sender)));
 */
public class stampTransaction extends DefaultInternalAction {
    TransactionContent transactionContent = new TransactionContent();
    String agtPuB = new String();

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        JasonUtil util = new JasonUtil();
        String[] arrayArgs = util.toArray(args);
        int qtdArgs = arrayArgs.length;
        // has a verification to fulfill
        //.velluscinum.stampTransaction(S,P,Q,Transaction,all(asset(Token),amount(Amount),ownerBefore(Sender)));
        if(qtdArgs==5){
            while (util.isLocked());
            util.lock(true);
            agtPuB = arrayArgs[2];      //Q
            transactionContent.loadTransaction(arrayArgs[0],arrayArgs[3]); //S,Transaction
            JSONObject verification = util.parseVerification(arrayArgs[4]); //Validation... all(...)
            //ts.getAg().getLogger().severe("operations is "+verification.get("operation"));

            JSONArray fulfills = verification.getJSONArray("fulfill");
            for (int i = 0; i < fulfills.length(); i++) {
                JSONObject fulfill = fulfills.optJSONObject(i);
                if (fulfill == null) return false;
                for (String key : fulfill.keySet()) {
                    String value = fulfill.optString(key, "");
                    if(verification.get("operation").equals("all")){
                        if(!fulfillVerification(key,value)){
                            util.lock(false);
                            //ts.getAg().getLogger().severe("fulfill is --> " + key + " = " + value);
                            return false;
                        }
                    }
                }
            }
            util.lock(false);
            qtdArgs=4;
        }

        //.velluscinum.stampTransaction(S,P,Q,Transaction)
        if(qtdArgs==4){
            while (util.isLocked());
            Api api = new Api();
            util.lock(true);
            api.setLog(ts.getAgArch().getAgName());
            String result = api.stampTransaction(
                    arrayArgs[0],       //S
                    arrayArgs[1],       //P
                    arrayArgs[2],       //Q
                    arrayArgs[3]        //Transaction
            );
            api = null;
            util.lock(false);
            if(result!=null) {
                    return true;
            }
                return false;
        }else{
            ts.getAg().getLogger().severe("Input error");
            return false;
        }
    }

    //all(asset(Token),amount(Amount),ownerBefore(Sender))
    private boolean fulfillVerification(String fulfill, String value){
        if(fulfill.equals("asset")){
            return transactionContent.getAssetID().equals(value);
        } else if(fulfill.equals("amount")){
            return transactionContent.getOutputAmountByPublicKey(agtPuB).equals(Long.parseLong(value));
        } else if(fulfill.equals("ownerBefore")){
            return transactionContent.getFirstOwnerBefore().equals(value);
        } else {
            return false;
        }
    }
}