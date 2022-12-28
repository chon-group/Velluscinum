// Agent banco in project cozinheiroEcomilao.mas2j
/* Crenças Iniciais */
bancoPublicKey("MCowBQYDK2VwAyEAgvaUUI4FSAb0pJhl+S/YAc2xA9uyT0wuxiy4DFt1X0g=").
bancoPrivateKey("MC4CAQAwBQYDK2VwBCIEIBYuAe4TXGqKTTMtaIGiV+p4m6S3z8DJyg2r0YqoTI7A").
bigchainDBServer("http://testchain.chon.group:9984/").

/* Objetivos Iniciais */

/* Planos */


+!cadastrarContaBancaria(NrProtocolo,NrCarteira)[source(Cliente)]: bigchainDBServer(URL)<-
	.print("Prezado Agente ",Cliente,", seja bem-vindo ao BanChain! - Aguarde equanto validamos sua carteira.");
	consultaUltimaTransacao(URL, NrCarteira);
	.wait(5000);
	!efetivaCadastro(NrProtocolo,NrCarteira,Cliente).

+!efetivaCadastro(NrProtocolo,NrCarteira,Cliente):bigchainDBServer(URL) 
& bancoPrivateKey(PrivateKey) & bancoPublicKey(PublicKey)
& transacaoValida(Ativo,Transacao)& 
Ativo==NrCarteira & Transacao==NrProtocolo <-
	.print("Carteira ",NrCarteira," validada com sucesso!");
	abrirConta(URL,PrivateKey,PublicKey,NrCarteira,10);
	.print("Depósito inicial realizado na Carteira ", NrCarteira);
	+conta(Cliente,NrCarteira);
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
	
