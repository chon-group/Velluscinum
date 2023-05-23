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
 * .buildWallet(walletBelief);
 */
public class buildWallet extends DefaultInternalAction {
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        JasonUtil util = new JasonUtil();
        Api api = new Api();
        if(args.length==1){
            String[] arrayArgs = util.toArray(args);
            while (util.isLocked());
            util.lock(true);
            String[] keyPair = api.buildWallet();
            util.lock(false);

            Message m = new Message("tell",
                    ts.getAgArch().getAgName(),
                    ts.getAgArch().getAgName(),
                    Literal.parseLiteral(util.newBelief(arrayArgs[0],keyPair[0],keyPair[1])));
                    //Literal.parseLiteral(arrayArgs[0]+"(\""+keyPair[0]+"\",\""+keyPair[1]+"\")"));
            ts.getAgArch().sendMsg(m);
            return true;
        }else{
            ts.getAg().getLogger().info("[.buildWallet] Input error");
            return false;
        }
        }

    }