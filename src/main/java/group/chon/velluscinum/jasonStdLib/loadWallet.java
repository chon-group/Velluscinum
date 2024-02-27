package group.chon.velluscinum.jasonStdLib;

import group.chon.velluscinum.Api;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.Message;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class loadWallet extends DefaultInternalAction {
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        JasonUtil jasonUtil = new JasonUtil();
        if(existsKey(ts.getAgArch().getAgName())){
            Message m = new Message("tell",
                    ts.getAgArch().getAgName(),
                    ts.getAgArch().getAgName(),
                    Literal.parseLiteral(
                            jasonUtil.newBelief(args[0].toString(),
                                    getKey(ts.getAgArch().getAgName()+".privateKey"),
                                    getKey(ts.getAgArch().getAgName()+".publicKey")))
                    );
            System.out.println("["+ts.getAgArch().getAgName()+"] Load Wallet.... "+getKey(ts.getAgArch().getAgName()+".publicKey"));
            ts.getAgArch().sendMsg(m);
        }else{
            while (jasonUtil.isLocked());
            jasonUtil.lock(true);
            Api api = new Api();
            api.setLog(ts.getAgArch().getAgName());
            String[] keyPair = api.buildWallet();
            api = null;
            jasonUtil.lock(false);

            jasonUtil.storePrivateKey(ts.getAgArch().getAgName(),keyPair[0]);
            jasonUtil.storePublicKey(ts.getAgArch().getAgName(),keyPair[1]);

            Message m = new Message("tell",
                    ts.getAgArch().getAgName(),
                    ts.getAgArch().getAgName(),
                    Literal.parseLiteral(jasonUtil.newBelief(args[0].toString(),keyPair[0],keyPair[1])));
            ts.getAgArch().sendMsg(m);
        }
        return true;
    }

    private boolean existsKey(String agName){
        File privateKey = new File(agName+".privateKey");
        File publicKey = new File(agName+".publicKey");
        return publicKey.exists() && privateKey.exists();
    }

    private String getKey(String nomeArquivo) throws IOException {
        return new String(Files.readAllBytes(Paths.get(nomeArquivo)));
    }
}
