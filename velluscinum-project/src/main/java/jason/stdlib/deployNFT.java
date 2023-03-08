package jason.stdlib;

import jason.asSemantics.*;
import jason.asSyntax.*;
import group.chon.velluscinum.*;
import group.chon.velluscinum.JasonUtil;

/**
 * 	.deployNFT(Server,MyPriv,MyPub,"asset:data","asset:metadata",nftBelief);
 */
public class deployNFT extends DefaultInternalAction {
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        JasonUtil util = new JasonUtil();
        Api api = new Api();
        String[] arrayArgs = util.toArray(args);

        if(args.length==6){
            while (util.isLocked());
            util.lock(true);
            String assetID = api.deploy(
                    arrayArgs[0].toString(),
                    arrayArgs[1].toString(),
                    arrayArgs[2].toString(),
                    arrayArgs[3].toString(),
                    arrayArgs[4].toString()
            );
            util.lock(false);
            if(assetID!=null){
                Message m = new Message("tell",
                        ts.getAgArch().getAgName(),
                        ts.getAgArch().getAgName(),
                        Literal.parseLiteral(util.newBelief(arrayArgs[args.length-1],assetID )));
                        //Literal.parseLiteral(arrayArgs[args.length-1]+"(\""+assetID+"\")"));
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