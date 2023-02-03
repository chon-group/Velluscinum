package jason.stdlib;

import group.chon.velluscinum.Api;
import group.chon.velluscinum.JasonUtil;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;

/**
 *  .lockTransaction(Server,PrK,PuB,Transaction);
 */
public class stampTransaction extends DefaultInternalAction {
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        JasonUtil util = new JasonUtil();
        Api api = new Api();
        if(args.length==4){
            String[] arrayArgs = util.toArray(args);
            return api.stampTransaction(
                    arrayArgs[0],
                    arrayArgs[1],
                    arrayArgs[2],
                    arrayArgs[3]
            );
        }else{
            ts.getAg().getLogger().info("Input error");
            return false;
        }
        }

    }