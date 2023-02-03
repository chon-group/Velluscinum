# Um middleware para integração de Agentes BDI com o BigChainDB
_[@nilsonmori](https://github.com/nilsonmori/), [@souzavdj](https://github.com/souzavdj), [@igormcoelho](https://github.com/igormcoelho), [@profpantoja](https://github.com/profpantoja)_

### Resumo
Este trabalho, apresenta um middleware para interação de Sistemas Multiagentes com a BigChainDB, um banco de dados distribuído com características blockchain [McConaghy et al. 2016]. Assim como em Lebioda et al. (2019), cada agente possuiu uma chave pública e uma chave privada, entretanto, neste trabalho cada agente consegue assinar sua própria transação, através de seu par de chave assimétrica. Tal como Alaeddini et al. (2021), este trabalho utiliza a blockchain como uma base aberta e confiável para alimentar as crenças dos agentes, entretanto uma prova de conceito foi implementada e testada. Por fim, diferente de Papi et al. (2022) não é necessário a criação de uma instituição virtual, além disso, cada agente consegue interagir diretamente com os ativos digitais, através do próprio ambiente endógeno do SMA.

### Metodologia
Uma integração entre agentes Jason BDI com o BigChainDB ocorre, de forma que, um agente possa criar ou transferir um ativo digital diretamente por ações internas.
A Figura abaixo apresenta a metodologia proposta.

![alt text for screen readers](https://raw.githubusercontent.com/nilsonmori/velluscinum/master/paper/schema.png "shema.png")

Para possibilitar a interação direta dos agentes, através de ações internas é necessária a importação da biblioteca ( [velluscinum.jar](https://raw.githubusercontent.com/nilsonmori/velluscinum/master/velluscinum-project/out/velluscinum.jar) ), para o diretório /lib no projeto.

__Abaixo são descritas as ações internas__
* ___.buildWallet__(wallet)_ -  Gera um par de chaves e adiciona uma crença __wallet(P,Q)__ na mente do agente, onde: ___wallet___ é a crença definida na chamada da ação interna; ___P___ é um literal contendo a chave privada do agente; ___Q___ é a chave pública do agente.
* ___.deployNFT__(S,P,Q,"key:value","metadataKey:metadataValue",nft)_  - Gera um token não fungível e adiciona uma crença __nft(N)__ na mente do agente, onde: ___nft___ é a crença definida na chamada da ação interna; ___N___ é o identificador (ASSET-ID) do token gerado.
* ___.transferNFT__(S,P,Q,N,R,"metadaKey:metadaValue",transaction)_ - Transfere o token não fungível (__N__) para outra carteira (__R__) e adiciona uma crença __transaction(T)__ na mente do agente, onde: ___transaction___ é a crença definida na chamada da ação interna; ___T___ é o identificador (TRANSFER_ID) da transação realizada.
* ___.deployToken__(S,P,Q,"name:cryptocurrency",A,coin)_ - Cria __A__ unidades de um token fungível e adiciona uma crença __coin( C )__ na mente do agente, onde: ___coin___ é uma crença definida na chamada da ação interna; ___C___ é o identificador (ASSET-ID) do token gerado.
* ___.transferToken__(S,P,Q,C,W,A,transaction)_ - Transfere __A__ unidades do token __C__ para a carteira __W__ e adiciona __transaction(T)__ na mente do agente, onde: ___transaction___ é a crença definida na chamada da ação interna; ___T___ é o identificador (TRANSFER_ID) da transação realizada.
* ___.stampTransaction__(S,P,Q,T)_ - Carimba a transação (T);
* ___.tokenBalance__(S,P,Q,C,balance)_ - Consulta o saldo do token __C__ na carteira __Q__.

#### Exemplo Simples
* Agent agent001 in project [firstExample.mas2j](https://github.com/nilsonmori/velluscinum/tree/master/examples/01-firstExample)
```sh
// Agent bob in project firstExample.mas2j
/* Initial beliefs and rules */
bigchainDB("http://testchain.chon.group:9984/").
aliceKey("FNJPJdtuPQYsqHG6tuUjKjqv7SW84U4ipiyyLV2j6MEW").

/* Initial goals */
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
* [Cozinheiro e Comilão](https://github.com/nilsonmori/velluscinum/tree/master/examples/02-cozinheiroEcomilao)

### Referências
ALAEDDINI, Morteza; DUGDALE, Julie; REAIDY, Paul; MADIÈS, Philippe; GÜRCAN, Önder. An agent-oriented, blockchain-based design of the interbank money market trading system. Agents and multi-agent systems: Technologies and applications 2021. [S. l.]: Springer, 2021. p. 3–16.

GMBH, BigchainDB. BigchainDB 2.0 The Blockchain Database. [S. l.: s. n.], 2018. Disponível em: https://www.bigchaindb.com/whitepaper/.

LEBIODA, Andre; LACHENMAIER, Jens; BURKHARDT, Daniel. Control of cyber-physical production systems: A concept to increase the trustworthiness within multi-agent systems with distributed ledger technology. 2019.

MCCONAGHY, Trent; MARQUES, Rodolphe; MÜLLER, Andreas; DE JONGHE, Dimitri; MCCONAGHY, Troy; MCMULLEN, Greg; HENDERSON, Ryan; BELLEMARE, Sylvain; GRANZOTTO, Alberto. Bigchaindb: a scalable blockchain database. white paper, BigChainDB, 2016.

PAPI, Fernando Gomes; HÜBNER, Jomi Fred; DE BRITO, Maiquel. A Blockchain integration to support transactions of assets in multi-agent systems. Engineering Applications of Artificial Intelligence, v. 107, p. 104534, 2022.
