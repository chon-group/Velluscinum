package group.chon.velluscinum.jasonStdLib;

import group.chon.velluscinum.Api;
import group.chon.velluscinum.jasonStdLib.JasonUtil;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.Message;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;

/**
 *  .lockTransaction(Server,PrK,PuB,Transaction);
 */
public class stampTransaction extends DefaultInternalAction {
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        JasonUtil util = new JasonUtil();
        if(args.length==4 || args.length ==5){
            String[] arrayArgs = util.toArray(args);
            while (util.isLocked());
            Api api = new Api();
            util.lock(true);
            api.setLog(ts.getAgArch().getAgName());
            String result = api.stampTransaction(
                    arrayArgs[0],
                    arrayArgs[1],
                    arrayArgs[2],
                    arrayArgs[3]
            );
            api = null;
            util.lock(false);
            if(result!=null) {
                if (args.length == 4) {
                    return true;
                } else if (args.length == 5) {
                    Message m = new Message("tell",
                            "velluscinum",
                            ts.getAgArch().getAgName(),
                            Literal.parseLiteral(util.newBelief(arrayArgs[args.length-1],result )));
                    ts.getAgArch().sendMsg(m);
//                    ts.getAg().getBB().add(Literal.parseLiteral(util.newBelief(arrayArgs[args.length-1],result )));
                    return true;
                }
            }
                return false;
        }else{
            ts.getAg().getLogger().info("Input error");
            return false;
        }
    }
}