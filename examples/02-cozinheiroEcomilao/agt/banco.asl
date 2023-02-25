// Agent banco in project cozinheiroEcomilao.mas2j
/* Crenças Iniciais */
chainServer("http://testchain.chon.group:9984/").

/* Objetivos Iniciais */
!createCoin.

+!createCoin: chainServer(Server) <-
	.buildWallet(myWallet);
	?myWallet(PrK,PuK);
	.print("Criando Moeda Digital");
	.deployToken(Server,PrK,PuK,"name:ChainCoin",200,chainCoin);
	!publicaCoin.
	
+!publicaCoin: chainCoin(Coin) & myWallet(Prk,PuB) & chainServer(Server) <-
	.broadcast(tell,cryptocurrency(Coin));
	.broadcast(tell,bankWallet(PuB));
	.broadcast(tell,chainServer(Server)).
	

/* Planos */
+!solicitacaoEmprestimo(Protocolo,NrCarteira,Valor)[source(Cliente)]: 
		chainCoin(Coin) & myWallet(Prk,PuB) & chainServer(Server) <-
		
	.print("Prezado Agente ",Cliente,", seja bem-vindo ao BanChain! - Aguarde equanto validamos sua transação.");
	.stampTransaction(Server,Prk,PuB,Protocolo);
	.transferToken(Server,Prk,PuB,Coin,NrCarteira,Valor,transactionTransfer);
	.send(Cliente,tell,contaBancaria(ok)).
		

+!pix(Destino,Valor)[source(Origem)]: 
		bigchainDBServer(URL) & conta(Cliente,NrCarteira) & Cliente=Origem<-
	.print("Consultando Saldo da Carteira [",NrCarteira,"]");
	consultarSaldo(URL,NrCarteira);
	?saldo(NrCarteira,Saldo);
	.print("SALDO---> ",Saldo);
	!movimentar(Cliente,Saldo,NrCarteira,Destino,Valor).
	
+!movimentar(ClienteOrigem,Saldo,CarteiraOrigem,Destino,Valor)[source(self)]: 
		bigchainDBServer(URL) & bancoPrivateKey(PrivateKey) & bancoPublicKey(PublicKey) 
		& conta(ClienteDestino,CarteiraDestino) & Destino=ClienteDestino 
		& Saldo >= Valor <-	
	
	.random(NrRegistro);
	transferirPix(URL,PrivateKey,PublicKey,CarteiraOrigem,CarteiraDestino,Valor,NrRegistro);
	
	+operacao(NrRegistro,ClienteOrigem,ClienteDestino,Valor);
	.print("Pix realizado de [",ClienteOrigem,"] para [",ClienteDestino,
			"] valor [",Valor,"] código [",NrRegistro,"]");
	.send(ClienteOrigem,tell,operacao(NrRegistro,ClienteOrigem,ClienteDestino,Valor)).

+!movimentar(Cliente,Saldo,NrCarteira,Destino,Valor)[source(self)]: Saldo < Valor <-	
	.print("Saldo insuficiente...  saldo=",Saldo);
	.send(Cliente,tell,semDinheiro).
	
