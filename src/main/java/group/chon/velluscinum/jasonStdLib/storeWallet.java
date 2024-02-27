package group.chon.velluscinum.jasonStdLib;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class storeWallet extends DefaultInternalAction {
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        Pattern pattern = Pattern.compile(args[0].toString() + "\\(\"([^\"]+)\",\"([^\"]+)\"\\)");
        Matcher matcher = pattern.matcher(ts.getAg().getBB().toString());
        if (!matcher.find()) { return false; }
            JasonUtil jasonUtil = new JasonUtil();
            jasonUtil.storePrivateKey(ts.getAgArch().getAgName(),matcher.group(1));
            jasonUtil.storePublicKey(ts.getAgArch().getAgName(), matcher.group(2));
            System.out.println("["+ts.getAgArch().getAgName()+"] Store Wallet... "+matcher.group(2));
        return true;
    }
}
