package group.chon.velluscinum.jasonStdLib;

import group.chon.velluscinum.Api;
import group.chon.velluscinum.jasonStdLib.JasonUtil;
import group.chon.velluscinum.core.WalletContent;
import jason.asSemantics.*;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;

import java.util.ArrayList;

/**
 * .tokenBalance(Server,MyPriv,MyPub,Coin,balanceBelief).
 */
public class tokenBalance extends DefaultInternalAction {
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        JasonUtil util = new JasonUtil();
        if(args.length==5){
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

            Long balance = 0L;
            for(int i=0; i<walletContents.size(); i++){
                if(walletContents.get(i).getToken().equals(arrayArgs[3])){
                    balance=walletContents.get(i).getAmount();
                    i=walletContents.size()+1;
                }
            }

            Message m = new Message("tell",
                    ts.getAgArch().getAgName(),
                    ts.getAgArch().getAgName(),
                    Literal.parseLiteral(util.newBelief(arrayArgs[4],arrayArgs[3],balance)));
                    //Literal.parseLiteral(arrayArgs[4]+"(\""+arrayArgs[3]+"\","+balance.toString()+")"));
            ts.getAgArch().sendMsg(m);
            return true;
        }else{
            return false;
        }
        }

    }