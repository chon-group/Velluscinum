# Um middleware para integração de Agentes BDI com o BigChainDB
## Metodologia
Uma integração entre agentes Jason BDI com o BigChainDB ocorre, de forma que, um agente possa criar ou transferir um ativo digital diretamente por ações internas. A Figura abaixo apresenta a metodologia proposta.

![alt text for screen readers](https://raw.githubusercontent.com/nilsonmori/velluscinum/master/paper/schema.png "shema.png")

Para possibilitar a interação direta dos agentes, através de ações internas é necessária a importação da biblioteca [velluscinum.jar](https://sourceforge.net/p/chonos/velluscinum/ci/master/tree/velluscinum-project/out/velluscinum.jar?format=raw), para o diretório /lib no projeto.

### Ações internas:
- .buildWallet(w) - gera uma carteira e retorna a crença +w(P,Q);
- .deployNFT(S,P,Q,I,M,b) - gera um NFT e retorna a crença +b(A);
- .transferNFT(S,P,Q,A,R,M,b) - transfere um NFT e retorna +b(T);
- .deployToken(S,P,Q,I,V,b) - cria V unidades de um token e retorna +b(C );
- .transferToken(S,P,Q,C,R,V,b) - transfere V unidades de C e retorna +b(T );
- .stampTransaction(S,P,Q,T) - carimba a transação (T );
- .tokenBalance(S,P,Q,C,q) - retorna +q(C,V).

Onde:
- Crenças:
-- b é uma crença que representa o resultado de uma operação na DLT;
-- w é uma crença que representa a carteira do agente;
-- q é uma crença que representa o saldo do token C na carteira do agente.
- Literais:
-- A é um literal que representa um NFT na DLT;
-- C é um literal que representa um Token na DLT;
-- P é um literal que representa a chave privada do agente;
-- Q é um literal que representa a chave pública do agente;
-- R é um literal que representa a chave pública de um agente destinatário;
-- S é um literal que representa o endereço de um nó da DLT;
-- T é um literal que representa uma transação realizada na DTL;
-- V é um literal que representa a quantidade de um Token na DTL;
- Arrays:
-- I é um array chave-valor (K1:V1;K2:V2;Kn:Vn) que representa os dados imutáveis de um ativo digital (ASSET);
-- M é um array chave-valor (k1:v1;k2:v2;kn:vn) que representa os metadados do ativo ou de uma transação (METADATA);


### Exemplo Simples
* Agent bob in project [firstExample.mas2j](https://sourceforge.net/p/chonos/velluscinum/ci/master/tree/examples/01-firstExample/)

```sh
// Agent bob in project firstExample.mas2j
/* Initial beliefs and rules */
bigchainDB("http://testchain.chon.group:9984/").
aliceKey("FNJPJdtuPQYsqHG6tuUjKjqv7SW84U4ipiyyLV2j6MEW").

/* Initial goal */
!start. 

/* Plans */
+!start: bigchainDB(Server) & aliceKey(AliceKey) <-
	.buildWallet(myWallet);
	
	?myWallet(MyPriv,MyPub);
	.deployNFT(Server,MyPriv,MyPub,
			"name:Meninas;author:Silva y Velázquez;place:Madrid;year:1656",
			"location:Madrid;value_eur:25000000;owner:Bob Agent",
			myNFT);	
			
	?myNFT(AssetID);
	.transferNFT(Server,MyPriv,MyPub,AssetID,AliceKey,
				"value_eur:30000000;owner:Alice;location:Rio de Janeiro",
				transaction);
```

### Outros Exemplos
* [Cozinheiro e Comilão](https://sourceforge.net/p/chonos/velluscinum/ci/master/tree/examples/02-cozinheiroEcomilao/)
* [Domestic Robot](https://sourceforge.net/p/chonos/velluscinum/ci/master/tree/examples/03-domestic-robot/)

# Referências
GMBH, BigchainDB. BigchainDB 2.0 The Blockchain Database. [S. l.: s. n.], 2018. Disponível em: https://www.bigchaindb.com/whitepaper/.
MCCONAGHY, Trent; MARQUES, Rodolphe; MÜLLER, Andreas; DE JONGHE, Dimitri; MCCONAGHY, Troy; MCMULLEN, Greg; HENDERSON, Ryan; BELLEMARE, Sylvain; GRANZOTTO, Alberto. Bigchaindb: a scalable blockchain database. white paper, BigChainDB, 2016.
