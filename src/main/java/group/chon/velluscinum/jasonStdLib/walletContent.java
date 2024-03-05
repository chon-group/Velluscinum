package group.chon.velluscinum.jasonStdLib;

import group.chon.velluscinum.Api;
import group.chon.velluscinum.core.WalletContent;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;

import java.util.ArrayList;

public class walletContent extends DefaultInternalAction {
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        JasonUtil util = new JasonUtil();
        if(args.length==4){
            String[] arrayArgs = util.toArray(args);

            while (util.isLocked());

            util.lock(true);
            Api api = new Api();
            api.setLog(ts.getAgArch().getAgName());
            ArrayList<WalletContent> walletContents = api.walletBalance(
                    arrayArgs[0],
                    arrayArgs[1],
                    arrayArgs[2]);
            api = null;
            util.lock(false);

            ts.getAg().getBB().add(Literal.parseLiteral(util.newBelief(arrayArgs[3],walletContents)));
            return true;
        }else{
            return false;
        }
    }
}
