// Agent bob in project firstExample.mas2j
/* Initial beliefs and rules */
bigchainDB("http://testchain.chon.group:9984/").
aliceKey("FNJPJdtuPQYsqHG6tuUjKjqv7SW84U4ipiyyLV2j6MEW").

/* Initial goals */
!start.

/* Plans */
+!start <-
	!buildWallet;
	!deployNFT;
	!transferNFT.


+!buildWallet <-
	.buildWallet(myWallet);
	?myWallet(PrivateKey,PublicKey);
	.print("Wallet address:",PublicKey).
		
+!deployNFT: bigchainDB(Server)<-
	?myWallet(MyPriv,MyPub);
	
	.deployNFT(Server,
			MyPriv,MyPub,
			"name:Meninas;author:Silva y Velázquez;place:Madrid;year:1656",
			"location:Madrid;value_eur:25000000;owner:Bob Agent",
			myNFT);
			
	?myNFT(NFT_ID);
	.print("NFT registered: ",Server,"api/v1/transactions/",NFT_ID).
			
	
+!transferNFT: bigchainDB(Server) & aliceKey(AliceKey)<-
	?myWallet(MyPriv,MyPub);
	?myNFT(AssetID);
	.transferNFT(Server,
				MyPriv,MyPub,
				AssetID,
				AliceKey,
				"value_eur:30000000;owner:Alice;location:Rio de Janeiro",
				transaction);
				
	?transaction(TransferID);
	.print("NFT transferred: ",Server,"api/v1/transactions/",TransferID).

