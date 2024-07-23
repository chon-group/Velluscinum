package group.chon.velluscinum.jasonStdLib;

import group.chon.velluscinum.Api;
import group.chon.velluscinum.model.TokenContent;
import group.chon.velluscinum.model.WalletContent;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.Message;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;

import java.util.ArrayList;

/**
 *  .tokenInfo(Server,Token,all|data|metadata,informationBelief).
 */
public class tokenInfo extends DefaultInternalAction {
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        JasonUtil util = new JasonUtil();
        if(args.length==4){
            String[] arrayArgs = util.toArray(args);

            while (util.isLocked());
            util.lock(true);
            Api api = new Api();
            api.setLog(ts.getAgArch().getAgName());
            TokenContent tokenContent = api.getTokenData(
                    arrayArgs[0],
                    arrayArgs[1],
                    arrayArgs[2]
            );
            api = null;
            util.lock(false);
            Message m = new Message("tell",
                    "velluscinum",
                    ts.getAgArch().getAgName(),
                    Literal.parseLiteral(util.newBelief(arrayArgs[3],tokenContent)));
            ts.getAgArch().sendMsg(m);
//            ts.getAg().getBB().add(Literal.parseLiteral(util.newBelief(arrayArgs[3],tokenContent)));
            return true;
        }else{
            return false;
        }
    }
}
