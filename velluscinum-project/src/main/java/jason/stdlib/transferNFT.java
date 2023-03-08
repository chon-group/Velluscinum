package jason.stdlib;

import group.chon.velluscinum.Api;
import group.chon.velluscinum.JasonUtil;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.Message;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;

/**
 * 	.transferNFT(Server,
 * 				MyPriv,
 * 				MyPub,
 * 				AssetID,
 * 				AliceKey,
 * 				"value_eur:30000000;owner:Alice;location:Rio de Janeiro",
 * 				transactionBelief);
 */
public class transferNFT extends DefaultInternalAction {
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        JasonUtil util = new JasonUtil();
        Api api = new Api();
        String[] arrayArgs = util.toArray(args);
        String tranferID = null;
        if(args.length==6 || args.length==7) {
            while (util.isLocked());
            util.lock(true);
            tranferID = api.transfer(
                    arrayArgs[0],
                    arrayArgs[1],
                    arrayArgs[2],
                    arrayArgs[3],
                    arrayArgs[4],
                    arrayArgs[5]);
            util.lock(false);
        }else{
            ts.getAg().getLogger().info("Input error");
            return false;
        }

        if(args.length==6 && tranferID!=null) {
            return true;
        }else if(args.length==7 && tranferID!=null) {
            Message m = new Message("tell",
                    ts.getAgArch().getAgName(),
                    ts.getAgArch().getAgName(),
                    Literal.parseLiteral(util.newBelief(arrayArgs[args.length-1],tranferID)));
                    //Literal.parseLiteral(arrayArgs[args.length-1]+"(\""+tranferID+"\")"));
            ts.getAgArch().sendMsg(m);
            return true;
        }else{
                return false;
        }
    }
}