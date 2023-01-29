// Agent bob in project firstExample.mas2j
/* Initial beliefs and rules */
bigchainDB("http://testchain.chon.group:9984/").
aliceKey("FNJPJdtuPQYsqHG6tuUjKjqv7SW84U4ipiyyLV2j6MEW").

/* Initial goals */
!start.

/* Plans */
+!start <-
	createWallet("base58");
	!deployNFT;
	!tranferNFT.

+!deployNFT: bigchainDB(Server) & privateKey(MyPriv) & publicKey(MyPub) <-
	buildNFT("name","Meninas",
			 "author","Diego Rodríguez de Silva y Velázquez",
			 "place","Madrid",
			 "year","1656");
			 
	metadataNFT("location","Madrid",
                "value_eur","25000000€",
                "value_btc","2200",
				"owner","Agent Bob");

	createAsset(Server,MyPriv,MyPub);	
	?assetID(NFT)[source(percept)];
	.print("NFT registered: ",Server,"api/v1/transactions/",NFT).
	
+!tranferNFT: assetID(NFT) & aliceKey(AK) 
				& bigchainDB(Server) & privateKey(MyPriv) 
				& publicKey(MyPub)<-
				
	metadataTransfer("value_eur","30000000€",
                 "value_btc","2100",
				 "location","Rio de Janeiro",
				 "owner","Agent Alice");
	
	transferAsset(Server,MyPriv,MyPub,NFT,AK);
	?transferID(TransferID);
	.print("NFT transferred: ",Server,"api/v1/transactions/",TransferID).
