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
 * .buildWallet(walletBelief);
 */
public class buildWallet extends DefaultInternalAction {
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        JasonUtil util = new JasonUtil();
        if(args.length==1){
            String[] arrayArgs = util.toArray(args);
            while (util.isLocked());
            util.lock(true);
            Api api = new Api();
            api.setLog(ts.getAgArch().getAgName());
            String[] keyPair = api.buildWallet();
            api = null;
            util.lock(false);

//            Message m = new Message("tell",
//                    ts.getAgArch().getAgName(),
//                    ts.getAgArch().getAgName(),
//                    Literal.parseLiteral(util.newBelief(arrayArgs[0],keyPair[0],keyPair[1])));
//            ts.getAgArch().sendMsg(m);

            ts.getAg().getBB().add(Literal.parseLiteral(util.newBelief(arrayArgs[0],keyPair[0],keyPair[1])));
            return true;
        }else{
            ts.getAg().getLogger().info("[velluscinum.buildWallet] Input error");
            return false;
        }
        }
    }