// Environment code for project firstExample.mas2j

import jason.asSyntax.*;
import jason.environment.*;
import group.chon.velluscinum.*;

public class Env extends Environment {
	private NonFungibleToken 		nonFungibleToken 		= new NonFungibleToken();
	private BigchainDBDriver 		bigchaindb4Jason 		= new BigchainDBDriver();
	private TransfAdditionalInfo 	transfAdditionalInfo 	= new TransfAdditionalInfo();
	private KeyManagement 			keyManagement 			= new KeyManagement();
	
    public boolean executeAction(String agName, Structure action) {
		String[] args = getActionTermArray(action);

		if(action.toString().substring(0,11).equals("createAsset")){
			addPercept(agName, 
						Literal.parseLiteral(
							"assetID(\""+createAsset(args)+"\")")
						);
		}
		else if(action.toString().substring(0,13).equals("transferAsset")){		
			addPercept(agName,
						Literal.parseLiteral(
							"transferID(\""+transferNFT(args)+"\")")
						);
		}
		else if((action.toString().substring(0,8).equals("buildNFT"))){
			buildNFT(args);				
		}
		else if((action.toString().substring(0,11).equals("metadataNFT"))){
			metadataNFT(args);				
		}
		else if((action.toString().substring(0,16).equals("metadataTransfer"))){
			metadataTransfer(args);		
		}else if((action.toString().substring(0,12).equals("createWallet"))){
			String[] keyPair = createWallet(args);
			addPercept(agName,
						Literal.parseLiteral(
							"privateKey(\""+keyPair[0]+"\")")
						);
			addPercept(agName,
						Literal.parseLiteral(
							"publicKey(\""+keyPair[1]+"\")")
						);
		}
        return true;
    }
	
	private String createAsset(String[] args){
		return bigchaindb4Jason.registerNFT(
					args[0],
					args[1],
					args[2],
					this.nonFungibleToken.toString()
					);
			
	}
	
	private String transferNFT(String[] args){
		return bigchaindb4Jason.transferNFT(
					args[0],
					args[1],
					args[2],
					args[3],
					transfAdditionalInfo.toString(),
					args[4]);
	}
	
	private void buildNFT(String[] args){
		this.nonFungibleToken.newNFT(args[0],args[1]);
		if(args.length>2){
			for(int i=2; i<args.length; i+=2){
				this.nonFungibleToken.addImmutableInformation(args[i],args[i+1]);
			}
		}
	}
	
	private void metadataNFT(String[] args){
		for(int i=0; i<args.length; i+=2){
			this.nonFungibleToken.addAdditionalInformation(args[i],args[i+1]);	
		}
		
	}
	
	private void metadataTransfer(String[] args){
		this.transfAdditionalInfo.newTransfInfo(args[0],args[1]);
		if(args.length>2){
			for(int i=2; i<args.length; i+=2){
				this.transfAdditionalInfo.addAdditionalInformation(args[i],args[i+1]);
			}
		}
		
	}
	
	private String[] getActionTermArray(Structure action){
		Integer terms = action.getArity();
		String[] termArray = new String[terms];
		for(int i=0; i<terms; i++){
			termArray[i] = action.getTerm(i).toString().replace("\"", "");
		}
		return termArray;
	}
	
	private String[] createWallet(String[] args){
		return keyManagement.newKeyPair(args[0]);
	}
	
}
