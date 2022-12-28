# Um middleware para integração de Agentes BDI com o BigChainDB
_[@nilsonmori](https://github.com/nilsonmori/), [@souzavdj](https://github.com/souzavdj), [@igormcoelho](https://github.com/igormcoelho), [@profpantoja](https://github.com/profpantoja)_

### Resumo
Este trabalho, apresenta um middleware para interação de Sistemas Multiagentes com a BigChainDB, um banco de dados distribuído com características blockchain [McConaghy et al. 2016]. Assim como em Lebioda et al. (2019), cada agente possuiu uma chave pública e uma chave privada, entretanto, neste trabalho cada agente consegue assinar sua própria transação, através de seu par de chave assimétrica. Tal como Alaeddini et al. (2021), este trabalho utiliza a blockchain como uma base aberta e confiável para alimentar as crenças dos agentes, entretanto uma prova de conceito foi implementada e testada. Por fim, diferente de Papi et al. (2022) não é necessário a criação de uma instituição virtual, além disso, cada agente consegue interagir diretamente com os ativos digitais, através do próprio ambiente endógeno do SMA.

### Metodologia
Este trabalho apresenta uma integração entre agentes Jason BDI com o BigChainDB, possibilitando que um agente possa criar ou transferir um ativo digital diretamente por ações implementadas no ambiente simulado do Sistema Multiagente. A Figura abaixo apresenta a metodologia proposta.

![alt text for screen readers](https://raw.githubusercontent.com/nilsonmori/velluscinum/master/paper/schema.png "Text to show on mouseover")

Para possibilitar a interação direta dos agentes, através do ambiente simulado, foi necessária a implementação de uma biblioteca ( [jason-bigchaindb-driver.jar](https://raw.githubusercontent.com/nilsonmori/velluscinum/master/jason-bigchaindb-driver/out/artifacts/jason_bigchaindb_driver_jar/jason-bigchaindb-driver.jar) ), disponível em com métodos públicos a serem utilizados pelo desenvolvedor, durante a programação da função no ambiente simulado.

__Abaixo são descritos os principais métodos:__
* _String __newAsset__(String uRL, String privKey, String pubKey, String asset)_
   + este método recebe quatro parâmetros em String, realiza uma transação do tipo CREATE na blockchain e retorna o identificador do ativo (ASSETID).
* _String __newTransfer__(String uRL, String privKey, String pubKey, String assetId, String metaData, String recipientPubKey)_
   + este método recebe seis parâmetros em String, realiza uma transação do tipo TRANSFER na blockchain e retorna o identificador da transção (TRANSACTIONID). 
* _String __getTransactionIDFromAsset__(String URL, String assetId, Integer numberOfTransaction)_ 
   + este método recebe dois parâmetros, realiza uma busca pelas transações de um determinado ativo e retorna o identificador da transação.
* _String __getFieldOfTransactionFromAssetID__(String URL, String assetId, String fieldInMetadata, Integer numberOfTransaction)_
   + este método recebe quatro parâmetros de entrada, realiza uma busca pelas transações de um determinado ativo e retorna o valor de determinado campo do METADATA.

#### Exemplo Simples
* Agent agent001 in project [firstExample.mas2j](https://github.com/nilsonmori/velluscinum/tree/master/examples/01-firstExample)
```sh
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
	.wait(1000);
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
	
```

* Environment code for project [firstExample.mas2j](https://github.com/nilsonmori/velluscinum/tree/master/examples/01-firstExample)
```sh
import jason.asSyntax.*;
import jason.environment.*;
import group.chon.velluscinum.*;
public class Env extends Environment {
	JasonBigchaindbDriver bigchaindb4Jason = new JasonBigchaindbDriver();
	public boolean executeAction(String agName, Structure action) {    
		String server = action.getTerm(0).toString().replace("\"", "");
		String privateKey = action.getTerm(1).toString().replace("\"", "");
		String publicKey  = action.getTerm(2).toString().replace("\"", "");
		if(action.toString().substring(0,12).equals("createAsset(")){
			String asset = "{\n"
				+ "\"asset\":[{\n"
				+ "	\"Description\": \"My first Asset in BigChainDB\"\n"
				+ "}],\n"
				+ "  \"metadata\":[{\n"
				+ "	\"Hello\": \"World\"\n"
				+ "  }]\n"
				+ "}";
			String assetID = bigchaindb4Jason.newAsset(server,privateKey,publicKey,asset);
			addPercept(agName, Literal.parseLiteral("assetID(\""+assetID+"\")"));
		} else if(action.toString().substring(0,14).equals("transferAsset(")){
			String assetID  = action.getTerm(3).toString().replace("\"", "");
			String aliceKey  = action.getTerm(4).toString().replace("\"", "");
			String metadata = "{\n"
				+ "  \"metadata\":[{\n"
				+ "	\"New Owner\": \"Alice\"\n"
				+ "  }]\n"
				+ "}";
			String transferID = bigchaindb4Jason.newTransfer(server,privateKey,publicKey,assetID,metadata,aliceKey);
			addPercept(agName, Literal.parseLiteral("transferID(\""+transferID+"\")"));
		}
	return true;
}
}
```

### Outros Exemplos
* [Cozinheiro e Comilão](https://github.com/nilsonmori/velluscinum/tree/master/examples/02-cozinheiroEcomilao)

### Referências
ALAEDDINI, Morteza; DUGDALE, Julie; REAIDY, Paul; MADIÈS, Philippe; GÜRCAN, Önder. An agent-oriented, blockchain-based design of the interbank money market trading system. Agents and multi-agent systems: Technologies and applications 2021. [S. l.]: Springer, 2021. p. 3–16.

GMBH, BigchainDB. BigchainDB 2.0 The Blockchain Database. [S. l.: s. n.], 2018. Disponível em: https://www.bigchaindb.com/whitepaper/.

LEBIODA, Andre; LACHENMAIER, Jens; BURKHARDT, Daniel. Control of cyber-physical production systems: A concept to increase the trustworthiness within multi-agent systems with distributed ledger technology. 2019.

MCCONAGHY, Trent; MARQUES, Rodolphe; MÜLLER, Andreas; DE JONGHE, Dimitri; MCCONAGHY, Troy; MCMULLEN, Greg; HENDERSON, Ryan; BELLEMARE, Sylvain; GRANZOTTO, Alberto. Bigchaindb: a scalable blockchain database. white paper, BigChainDB, 2016.

PAPI, Fernando Gomes; HÜBNER, Jomi Fred; DE BRITO, Maiquel. A Blockchain integration to support transactions of assets in multi-agent systems. Engineering Applications of Artificial Intelligence, v. 107, p. 104534, 2022.
