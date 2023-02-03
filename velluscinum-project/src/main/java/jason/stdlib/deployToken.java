package jason.stdlib;

import group.chon.velluscinum.Api;
import group.chon.velluscinum.Asset;
import group.chon.velluscinum.JasonUtil;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.Message;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;

/**
 * .buildToken(Server,PrK,PuK,"cryptocurrency:ChainChon",200,chainCoin);
 */
public class deployToken extends DefaultInternalAction {
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        JasonUtil util = new JasonUtil();
        Api api = new Api();
        if(args.length==6){
            String[] arrayArgs = util.toArray(args);
            Long amount = Long.parseLong(arrayArgs[4]);
            String tokenID = api.deploy(arrayArgs[0],arrayArgs[1],arrayArgs[2],arrayArgs[3],amount);
            if(tokenID!=null){
//                ts.getAg().getBB().add(
//                        Literal.parseLiteral(arrayArgs[args.length-1]+"(\""+tokenID+"\")[source(self)]"));
                Message m = new Message("tell",
                        ts.getAgArch().getAgName(),
                        ts.getAgArch().getAgName(),
                        Literal.parseLiteral(arrayArgs[args.length-1]+"(\""+tokenID+"\")"));
                ts.getAgArch().sendMsg(m);
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