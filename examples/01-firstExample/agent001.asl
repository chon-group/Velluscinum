// Agent agent001 in project firstExample.mas2j

/* Initial beliefs and rules */
bigchainServer("http://testchain.chon.group:9984/").
myprivateKey("MC4CAQAwBQYDK2VwBCIEINrKHkh7bJlpSeGJyutdxrsa6qqtHVIbm6YXyQymTYK8").
mypublicKey("MCowBQYDK2VwAyEAjFVzFInLZCIpo94Ii5f74dtr/FcKQs8M0m9Z2JOAMVU=").
alicePublicKey("MCowBQYDK2VwAyEAEuN5rvkEHUqJcFr9bzh8qzbMellY9oHY32SkUoL0cL8=").

/* Initial goals */
!start.

/* Plans */
+!start <-
	.print("Creating an Asset in BigChainDB.");
	.wait(2000);
	?bigchainServer(URL);
	?myprivateKey(PrivKey);
	?mypublicKey(PublKey);
	createAsset(URL,PrivKey,PublKey);
	.wait(5000);
	?assetID(AssetID)[source(percept)];
	.print("Asset registered on Blockchain!");
	.print(URL,"api/v1/transactions/",AssetID);
	?alicePublicKey(AliceKey);
	transferAsset(URL,PrivKey,PublKey,AssetID,AliceKey);
	.wait(5000);
	?transferID(TransferID);
	.print("Alice is the Asset Owner!");
	.print(URL,"api/v1/transactions/",TransferID).
	
