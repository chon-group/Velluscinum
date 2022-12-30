// Agent agent001 in project firstExample.mas2j

/* Initial beliefs and rules */
bigchain("http://testchain.chon.group:9984/").
privateKey("MC4CAQAwBQYDK2VwBCIEINrKHkh7bJlpSeGJyutdxrsa6qqtHVIbm6YXyQymTYK8").
publicKey("MCowBQYDK2VwAyEAjFVzFInLZCIpo94Ii5f74dtr/FcKQs8M0m9Z2JOAMVU=").


/* Initial goals */
!createAsset.

/* Plans */
+!createAsset: bigchain(Server) & privateKey(MyPriv) 
			& publicKey(MyPub)<-
			
	.print("Creating NFT in BigChainDB.");
	buildNFT("Description","My first NFT in BigChainDB");
	createAsset(Server,MyPriv,MyPub);
	?assetID(NFT)[source(percept)];
	.print("NFT registered: ",Server,"api/v1/transactions/",NFT);
	
	dataTransfer("New Owner", "Alice");
	transferAsset(Server,MyPriv,MyPub,NFT,"MCowBQYDK2VwAyEAEuN5rvkEHUqJcFr9bzh8qzbMellY9oHY32SkUoL0cL8=");
	?transferID(TransferID);
	.print("NFT transferred: ",Server,"api/v1/transactions/",TransferID).
	
