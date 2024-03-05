package group.chon.velluscinum.jasonStdLib;

import jason.asSemantics.*;
import jason.asSyntax.*;
import group.chon.velluscinum.*;
import group.chon.velluscinum.jasonStdLib.JasonUtil;

/**
 * 	.deployNFT(Server,MyPriv,MyPub,"asset:data","asset:metadata",nftBelief);
 */
public class deployNFT extends DefaultInternalAction {
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        JasonUtil util = new JasonUtil();
        String[] arrayArgs = util.toArray(args);

        if(args.length==6){
            while (util.isLocked());
            util.lock(true);
            Api api = new Api();
            api.setLog(ts.getAgArch().getAgName());
            String assetID = api.deploy(
                    arrayArgs[0].toString(),
                    arrayArgs[1].toString(),
                    arrayArgs[2].toString(),
                    arrayArgs[3].toString(),
                    arrayArgs[4].toString()
            );
            api = null;
            util.lock(false);
            if(assetID!=null){
//                Message m = new Message("tell",
//                        ts.getAgArch().getAgName(),
//                        ts.getAgArch().getAgName(),
//                        Literal.parseLiteral(util.newBelief(arrayArgs[args.length-1],assetID )));
//                ts.getAgArch().sendMsg(m);
                ts.getAg().getBB().add(Literal.parseLiteral(util.newBelief(arrayArgs[args.length-1],assetID )));
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