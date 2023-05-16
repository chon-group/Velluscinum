# [Velluscinum](https://velluscinum.chon.group): A Middleware for Using Digital Assets in Multi-Agent Systems

Distributed Ledger Technologies (DLT) characteristics can contribute to several domains, such as Multi-agent Systems (MAS), facilitating the agreement between agents, managing trust relationships, and distributed scenarios. Some contributions to this integration are in the theoretical stage, and the few existing practical contributions have limitations and low performance. This work presents a MAS approach that can use digital assets as a factor of agreement in the relationship between cognitive agents using the Belief-Desire-Intention model. To validate the proposed methodology, we present the middleware Velluscinum that offers new internal actions to agents. The middleware was tested by adapting the Building-a-House classic example to cryptocurrency and agreements mediated by a distributed ledger.

## The built-in internal actions provided by the middleware are described below:
- .buildWallet(w) - generates a digital wallet and returns the belief +w(P,Q);
- .deployNFT(S,P,Q,I,M,b) - registers an asset and returns the belief +b(A);
- .transferNFT(S,P,Q,A,R,M,b) - transfer an asset and returns +b(T);
- .deployToken(S,P,Q,I,V,b) - creates V units from an asset, returns +b(C);
- .transferToken(S,P,Q,C,R,V,b) - transfer V units of C and returns +b(T);
- .stampTransaction(S,P,Q,T) - stamps a transaction (T);
- .tokenBalance}(S,P,Q,C,q) - check the wallet Q and return +q(C,V).

Where:
- b is a belief that represents a result of an operation in DLT;
- w is a belief that represents an agent's wallet;
- q is a belief that represents the balance of C in the agent's wallet.
- A is a literal that represents a divisible asset;
- C is a literal that represents a indivisible asset;
- P e Q are literals that represent the agent's key pair;
- R is a literal that represents the public key of a recipient agent;
- S is a literal that represents the address of a DLT node;
- T is a literal that represents a transaction performed in the DTL;
- V is a literal that represents the number of parts of a C;
- I is a key-value array that represents the immutable data of an asset;
- M is a key-value array representing asset or transaction metadata;


### Exemplo Simples
* Agent bob in project [firstExample.mas2j](https://sourceforge.net/p/chonos/velluscinum/ci/master/tree/examples/01-firstExample/)

```sh
/* Initial beliefs and rules */
bigchainDB("http://testchain.chon.group:9984/").
aliceKey("FNJPJdtuPQYsqHG6tuUjKjqv7SW84U4ipiyyLV2j6MEW").

/* Initial goals */
!start.

/* Plans */
+!start <-
	.print("Creating a Wallet");
	.buildWallet(myWallet);
	.wait(myWallet(PrivateKey,PublicKey));
	
	.print("Creating a NFT");
	?bigchainDB(Server);
	.deployNFT(Server,
			PrivateKey,PublicKey,
			"name:Meninas;author:Silva y Velázquez;place:Madrid;year:1656",
			"location:Madrid;value_eur:25000000;owner:Bob Agent",
			myNFT);

	.wait(myNFT(AssetID));
	.print("NFT registered: ",Server,"api/v1/transactions/",AssetID);

	.print("Tranfering the NFT");
	?aliceKey(AliceKey);
	.transferNFT(Server,
				PrivateKey,PublicKey,
				AssetID,
				AliceKey,
				"value_eur:30000000;owner:Alice;location:Rio de Janeiro",
				transactionTo(alice));
				
	.wait(transactionTo(alice,TransferID));
	.print("NFT transferred: ",Server,"api/v1/transactions/",TransferID).
```

### Outros Exemplos
* [Cozinheiro e Comilão](https://sourceforge.net/p/chonos/velluscinum/ci/master/tree/examples/02-cozinheiroEcomilao/)
* [Domestic Robot](https://sourceforge.net/p/chonos/velluscinum/ci/master/tree/examples/03-domestic-robot/)
