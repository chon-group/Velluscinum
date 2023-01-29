// Agent banco in project cozinheiroEcomilao.mas2j
/* Crenças Iniciais */
bankWallet("Gq5mn9YzRCjPPrhL6JQpekbCc2Br637K23UQbYD6Y4Kq").
bankPrivateKey("GfYViAZvdMPQi8bZAMJsUEqDr273EvEx99eGymyVFsc1").
chainServer("http://testchain.chon.group:9984/").
/* Objetivos Iniciais */
!createCryptocurrency.



/* Planos */
+!createCryptocurrency: chainServer(Server)  
							& bankPrivateKey(PrK) 
							& bankWallet(PuK) <-
	.print("Criando Moeda Digital");
	buildToken(Server,PrK,PuK,"MyCoin",200);
	?bankCoin(ID);
	.broadcast(tell,bankCoin(ID)).


+!solicitacaoEmprestimo(NrProtocolo,NrCarteira,Valor)[source(Cliente)]: chainServer(URL)<-
	.print("Prezado Agente ",Cliente,", seja bem-vindo ao BanChain! - Aguarde equanto validamos sua carteira.");




// 	consultaUltimaTransacao(URL, NrCarteira);
// 	.wait(5000);
// 	!efetivaCadastro(NrProtocolo,NrCarteira,Cliente).

// +!efetivaCadastro(NrProtocolo,NrCarteira,Cliente):bigchainDBServer(URL) 
// & bancoPrivateKey(PrivateKey) & bancoPublicKey(PublicKey)
// & transacaoValida(Ativo,Transacao)& 
// Ativo==NrCarteira & Transacao==NrProtocolo <-
// 	.print("Carteira ",NrCarteira," validada com sucesso!");
// 	abrirConta(URL,PrivateKey,PublicKey,NrCarteira,10);
// 	.print("Depósito inicial realizado na Carteira ", NrCarteira);
// 	+conta(Cliente,NrCarteira);
// 	.send(Cliente,tell,contaBancaria(ok))
	.		

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
	
