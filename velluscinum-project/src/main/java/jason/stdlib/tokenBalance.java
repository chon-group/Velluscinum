package jason.stdlib;

import group.chon.velluscinum.Api;
import group.chon.velluscinum.JasonUtil;
import group.chon.velluscinum.WalletContent;
import jason.asSemantics.*;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;

import java.util.ArrayList;

/**
 * .tokenBalance(Server,MyPriv,MyPub,Coin,balance).
 */
public class tokenBalance extends DefaultInternalAction {
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        JasonUtil util = new JasonUtil();
        Api api = new Api();
        if(args.length==5){
            String[] arrayArgs = util.toArray(args);

            ArrayList<WalletContent> walletContents = api.walletBalance(
                    arrayArgs[0],
                    arrayArgs[1],
                    arrayArgs[2]);

            Long balance = 0L;
            for(int i=0; i<walletContents.size(); i++){
                System.out.println(walletContents.get(i).getAmount()+" -> "+walletContents.get(i).getToken());
                if(walletContents.get(i).getToken().equals(arrayArgs[3])){
                    balance=walletContents.get(i).getAmount();
                }

            }
            //ts.getAg().getBB().add(
            //        Literal.parseLiteral(arrayArgs[4]+"(\""+arrayArgs[3]+"\","+balance.toString()+")[source(self)]"));
            Message m = new Message("tell",
                    ts.getAgArch().getAgName(),
                    ts.getAgArch().getAgName(),
                    Literal.parseLiteral(arrayArgs[4]+"(\""+arrayArgs[3]+"\","+balance.toString()+")"));
            ts.getAgArch().sendMsg(m);

            return true;
        }else{
            return false;
        }
        }

    }