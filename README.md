# Um middleware para integração de Agentes BDI com o BigChainDB
_[@nilsonmori](https://github.com/nilsonmori/), [@souzavdj](https://github.com/souzavdj), [@igormcoelho](https://github.com/igormcoelho), [@profpantoja](https://github.com/profpantoja)_

### Resumo
Este trabalho, apresenta um middleware para interação de Sistemas Multiagentes com a BigChainDB, um banco de dados distribuído com características blockchain [McConaghy et al. 2016]. Assim como em Lebioda et al. (2019), cada agente possuiu uma chave pública e uma chave privada, entretanto, neste trabalho cada agente consegue assinar sua própria transação, através de seu par de chave assimétrica. Tal como Alaeddini et al. (2021), este trabalho utiliza a blockchain como uma base aberta e confiável para alimentar as crenças dos agentes, entretanto uma prova de conceito foi implementada e testada. Por fim, diferente de Papi et al. (2022) não é necessário a criação de uma instituição virtual, além disso, cada agente consegue interagir diretamente com os ativos digitais, através do próprio ambiente endógeno do SMA.

### Metodologia
Este trabalho apresenta uma integração entre agentes Jason BDI com o BigChainDB, possibilitando que um agente possa criar ou transferir um ativo digital diretamente por ações implementadas no ambiente simulado do Sistema Multiagente. A Figura abaixo apresenta a metodologia proposta.

![alt text for screen readers](https://raw.githubusercontent.com/nilsonmori/velluscinum/master/paper/schema.png "Text to show on mouseover")

Para possibilitar a interação direta dos agentes, através do ambiente simulado, foi necessária a implementação de uma biblioteca ( [jason-bigchaindb-driver.jar](https://raw.githubusercontent.com/nilsonmori/velluscinum/master/jason-bigchaindb-driver/out/jason-bigchaindb-driver.jar) ), disponível em com métodos públicos a serem utilizados pelo desenvolvedor, durante a programação da função no ambiente simulado.

__Abaixo são descritos os principais métodos:__
* _String __registerNFT__(String url, String privateKey, String publicKey, String nonFungibleToken)_ 
   + este método recebe quatro parâmetros em String, realiza uma transação do tipo CREATE no Servidor BigChainDB e retorna o identificador do ativo (ASSETID).
* _String __transferNFT__(String url, String senderPrivateKey, String senderPublicKey, String nftID, String transferMetadata, String recipientPublicKey)_
   + este método recebe seis parâmetros em String, realiza uma transação do tipo TRANSFER na blockchain e retorna o identificador da transção (TRANSACTIONID). 

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
+!start <-
	createWallet("base58");
	!deployNFT;
	!tranferNFT.

+!deployNFT: bigchainDB(Server) & privateKey(MyPriv) & publicKey(MyPub) <-
	buildNFT("name","Meninas",
		"author","Diego Rodríguez de Silva y Velázquez",
		"place","Madrid", "year","1656");
			 
	metadataNFT("location","Madrid", "value_eur","25000000€",
                "value_btc","2200", "owner","Agent Bob");

	createAsset(Server,MyPriv,MyPub);		

+!tranferNFT: assetID(NFT) & aliceKey(AK) & bigchainDB(Server) 
	& privateKey(MyPriv) & publicKey(MyPub)<-
				
	metadataTransfer("value_eur","30000000€","value_btc","2100",
		"location","Rio de Janeiro","owner","Agent Alice");
	
	transferAsset(Server,MyPriv,MyPub,NFT,AK);
	
```

* Environment code for project [firstExample.mas2j](https://github.com/nilsonmori/velluscinum/tree/master/examples/01-firstExample)
```sh

// Environment code for project firstExample.mas2j

import jason.asSyntax.*;
import jason.environment.*;
import group.chon.velluscinum.*;

public class Env extends Environment {
private NonFungibleToken 	nonFungibleToken 	= new NonFungibleToken();
private BigchainDBDriver 	bigchaindb4Jason 	= new BigchainDBDriver();
private TransferAdditionalInfo 	transferAdditionalInfo 	= new TransferAdditionalInfo();
private KeyManagement		keyManagement 		= new KeyManagement();
	
public boolean executeAction(String agName, Structure action) {
	String[] args = getActionTermArray(action);

	if(action.toString().substring(0,11).equals("createAsset")){
		addPercept(agName,Literal.parseLiteral("assetID(\""+createAsset(args)+"\")"));
	}else if(action.toString().substring(0,13).equals("transferAsset")){		
		addPercept(agName,Literal.parseLiteral("transferID(\""+transferNFT(args)+"\")"));
	}else if((action.toString().substring(0,8).equals("buildNFT"))){
		buildNFT(args);				
	}else if((action.toString().substring(0,11).equals("metadataNFT"))){
		metadataNFT(args);				
	}else if((action.toString().substring(0,16).equals("metadataTransfer"))){
		metadataTransfer(args);		
	}else if((action.toString().substring(0,12).equals("createWallet"))){
		String[] keyPair = createWallet(args);
		addPercept(agName,Literal.parseLiteral("privateKey(\""+keyPair[0]+"\")"));
		addPercept(agName,Literal.parseLiteral("publicKey(\""+keyPair[1]+"\")"));
	}
	return true;
}

private String createAsset(String[] args){
	return bigchaindb4Jason.registerNFT(args[0],args[1],args[2],this.nonFungibleToken.toString());
}

private String transferNFT(String[] args){
	return bigchaindb4Jason.transferNFT(args[0],args[1],args[2],args[3],transferAdditionalInfo.toString(),args[4]);
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
	this.transferAdditionalInfo.newTransfInfo(args[0],args[1]);
	if(args.length>2){
		for(int i=2; i<args.length; i+=2){
			this.transferAdditionalInfo.addAdditionalInformation(args[i],args[i+1]);
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

```

### Outros Exemplos
* [Cozinheiro e Comilão](https://github.com/nilsonmori/velluscinum/tree/master/examples/02-cozinheiroEcomilao)

### Referências
ALAEDDINI, Morteza; DUGDALE, Julie; REAIDY, Paul; MADIÈS, Philippe; GÜRCAN, Önder. An agent-oriented, blockchain-based design of the interbank money market trading system. Agents and multi-agent systems: Technologies and applications 2021. [S. l.]: Springer, 2021. p. 3–16.

GMBH, BigchainDB. BigchainDB 2.0 The Blockchain Database. [S. l.: s. n.], 2018. Disponível em: https://www.bigchaindb.com/whitepaper/.

LEBIODA, Andre; LACHENMAIER, Jens; BURKHARDT, Daniel. Control of cyber-physical production systems: A concept to increase the trustworthiness within multi-agent systems with distributed ledger technology. 2019.

MCCONAGHY, Trent; MARQUES, Rodolphe; MÜLLER, Andreas; DE JONGHE, Dimitri; MCCONAGHY, Troy; MCMULLEN, Greg; HENDERSON, Ryan; BELLEMARE, Sylvain; GRANZOTTO, Alberto. Bigchaindb: a scalable blockchain database. white paper, BigChainDB, 2016.

PAPI, Fernando Gomes; HÜBNER, Jomi Fred; DE BRITO, Maiquel. A Blockchain integration to support transactions of assets in multi-agent systems. Engineering Applications of Artificial Intelligence, v. 107, p. 104534, 2022.
