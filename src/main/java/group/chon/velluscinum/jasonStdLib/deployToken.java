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
 * .buildToken(Server,PrK,PuK,"cryptocurrency:ChainChon",200,coinBelief);
 */
public class deployToken extends DefaultInternalAction {
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        JasonUtil util = new JasonUtil();

        if(args.length==6){
            String[] arrayArgs = util.toArray(args);
            Long amount = Long.parseLong(arrayArgs[4]);
            while (util.isLocked());
            util.lock(true);
            Api api = new Api();
            api.setLog(ts.getAgArch().getAgName());
            String tokenID = api.deploy(arrayArgs[0],arrayArgs[1],arrayArgs[2],arrayArgs[3],amount);
            api = null;
            util.lock(false);
            if(tokenID!=null){
                Message m = new Message("tell",
                        "velluscinum",
                        ts.getAgArch().getAgName(),
                        Literal.parseLiteral(util.newBelief(arrayArgs[args.length-1],tokenID )));
                ts.getAgArch().sendMsg(m);
//                ts.getAg().getBB().add(Literal.parseLiteral(util.newBelief(arrayArgs[args.length-1],tokenID )));
                return true;
            }else{
                return false;
            }
        }else{
            ts.getAg().getLogger().info("Input error");
            return false;
        }
        }

    }