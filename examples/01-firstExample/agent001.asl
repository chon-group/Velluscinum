// Agent agent001 in project firstExample.mas2j

/* Initial beliefs and rules */
bigchainServer("http://testchain.chon.group:9984/").
myprivateKey("MC4CAQAwBQYDK2VwBCIEIJKHX4YV2Mp6GeMcsU6TENzTEtpxlmiC+1CTViNofoRV").
mypublicKey("rO0ABXNyACpncm91cC5jaG9uLnZlbGx1c2NpbnVtLlB1YmxpY0tleUF0dHJpYnV0ZXP72Aas3Pd5eQIABUkAAWJbAAFJdAACW0JbAAFRcQB+AAFbAAFkcQB+AAFbAAFzcQB+AAF4cAAAAQB1cgACW0Ks8xf4BghU4AIAAHhwAAAAILCgDkonG+7EeOQvrQYYQy+n1/s9mQBNKwvfwU+AJIMrdXEAfgADAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAdXEAfgADAAAAIKN4WRPKTet1q9hBQU0KcACY6Hl3eUDHjHP+byvubANSdXEAfgADAAAAIJOzWQkoABDFaOUm8Ya9rhRr/hxgbBkC0Mx6XzFaKmMx").
alicePublicKey("rO0ABXNyACpncm91cC5jaG9uLnZlbGx1c2NpbnVtLlB1YmxpY0tleUF0dHJpYnV0ZXP72Aas3Pd5eQIABUkAAWJbAAFJdAACW0JbAAFRcQB+AAFbAAFkcQB+AAFbAAFzcQB+AAF4cAAAAQB1cgACW0Ks8xf4BghU4AIAAHhwAAAAILCgDkonG+7EeOQvrQYYQy+n1/s9mQBNKwvfwU+AJIMrdXEAfgADAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAdXEAfgADAAAAIKN4WRPKTet1q9hBQU0KcACY6Hl3eUDHjHP+byvubANSdXEAfgADAAAAIHCvVao6XhNLLHTPkwUE3a5iT6cEfCVfQ9UKi7enbKC3").

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
	
