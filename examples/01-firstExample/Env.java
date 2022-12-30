// Environment code for project firstExample.mas2j
import jason.asSyntax.*;
import jason.environment.*;
import group.chon.velluscinum.*;
import group.chon.velluscinum.model.*;
public class Env extends Environment {
	NonFungibleToken nonFungibleToken = new NonFungibleToken();
	BigchainDBDriver bigchaindb4Jason = new BigchainDBDriver();
	TransfAdditionalInfo transfAdditionalInfo = new TransfAdditionalInfo();
	
    	public boolean executeAction(String agName, Structure action) {    
		if(action.toString().substring(0,12).equals("createAsset(")){
			String assetID = bigchaindb4Jason.registerNFT(
								action.getTerm(0).toString().replace("\"",""),
								action.getTerm(1).toString().replace("\"",""),
								action.getTerm(2).toString().replace("\"",""),
								nonFungibleToken.toString()
								);
			addPercept(agName, 
						Literal.parseLiteral("assetID(\""+assetID+"\")")
						);
		}
		else if(action.toString().substring(0,14).equals("transferAsset(")){		
			String transferID =  bigchaindb4Jason.transferNFT(
						action.getTerm(0).toString().replace("\"", ""),
						action.getTerm(1).toString().replace("\"", ""),
						action.getTerm(2).toString().replace("\"", ""),
						action.getTerm(3).toString().replace("\"", ""),
						transfAdditionalInfo.toString(),
						action.getTerm(4).toString().replace("\"", ""));					
			addPercept(agName,
						Literal.parseLiteral("transferID(\""+transferID+"\")")
						);
		}else if((action.toString().substring(0,9).equals("buildNFT("))){
			nonFungibleToken.newNFT(
				action.getTerm(0).toString().replace("\"", ""),
				action.getTerm(1).toString().replace("\"", ""));				
		}else if((action.toString().substring(0,13).equals("dataTransfer("))){
			transfAdditionalInfo.newTransfInfo(
				action.getTerm(0).toString().replace("\"", ""),
				action.getTerm(1).toString().replace("\"", ""));				
		}

        return true;
    }
}
