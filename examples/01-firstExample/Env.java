// Environment code for project firstExample.mas2j
import jason.asSyntax.*;
import jason.environment.*;
import group.chon.velluscinum.*;
public class Env extends Environment {
	JasonBigchaindbDriver bigchaindb4Jason = new JasonBigchaindbDriver();
    	public boolean executeAction(String agName, Structure action) {    
		if(action.toString().substring(0,12).equals("createAsset(")){
			String server = action.getTerm(0).toString().replace("\"", "");
			String privKey = action.getTerm(1).toString().replace("\"", "");
			String publicKey  = action.getTerm(2).toString().replace("\"", "");				
			String asset = "{\n"
					+ "\"asset\":[{\n"
					+ "	\"Description\": \"My first Asset in BigChainDB\"\n"
					+ "}],\n"
					+ "  \"metadata\":[{\n"
					+ "	\"Hello\": \"World\"\n"
					+ "  }]\n"
					+ "}";
			String assetID = bigchaindb4Jason.newAsset(server,privKey,publicKey,asset);
			addPercept(agName, Literal.parseLiteral("assetID(\""+assetID+"\")"));
		}
		else if(action.toString().substring(0,14).equals("transferAsset(")){
			String server = action.getTerm(0).toString().replace("\"", "");
			String privateKey = action.getTerm(1).toString().replace("\"", "");
			String publicKey  = action.getTerm(2).toString().replace("\"", "");
			String assetID  = action.getTerm(3).toString().replace("\"", "");
			String aliceKey  = action.getTerm(4).toString().replace("\"", "");
			String metadata = "{\n"
				+ "  \"metadata\":[{\n"
				+ "	\"New Owner\": \"Alice\"\n"
				+ "  }]\n"
				+ "}";
			String transferID = bigchaindb4Jason.newTransfer(server,privateKey,publicKey,assetID,metadata,aliceKey);
			addPercept(agName, Literal.parseLiteral("transferID(\""+transferID+"\")"));
		}
        return true;
    }
}
