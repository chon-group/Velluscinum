// Agent comilao in project cozinheiroEcomilao.mas2j
/* Crenças Iniciais */
chainServer("http://testchain.chon.group:9984/").
estoqueComida(0).
energia(0).

/* Objetivos Iniciais */
//!curtir.
//!gerarCarteira.
!createWallet.

+!createWallet <-
	.print("Gerando carteira digital");
	createWallet("base58");
	.wait(2000);
	!pedirEmprestimo.

/* Planos */
+!pedirEmprestimo: publicKey(MyWallet) & privateKey(PrivK) & chainServer(Server)<-
	.print("Criando Conta Bancária e solicitando crédito");
	.my_name(Ag);

	.send(banco,askOne,bankWallet(BW),Replay);
	+Replay;
	?bankWallet(BankWallet);

	buildAsset("Agent Name",Ag,
			"ContextNet Address","5362fe5e-aaf1-43e6-9643-7ab094836ff4",
			"Public Wallet", MyWallet);
	deployAsset(Server,PrivK,MyWallet);
	?assetID(AccountID)[source(percept)];

	buildTransfer("Description","Solicitação de Empréstimo");
	deployTransfer(Server,PrivK,MyWallet,AccountID,BankWallet);
	?transferID(ProtocolID);
	.print("NFT transferred: ",Server,"api/v1/transactions/",ProtocolID);

	.print("Solicitando Abertura de conta");
	.send(banco,achieve,solicitacaoEmprestimo(ProtocolID,MyWallet,20));
	.print("cozinheiro OK").


+!gerarCarteira: myPublicKey(PublK) & not protocolo(P)  <-
		.print("Gerando carteira digital");
		.send(banco,askOne,bancoPublicKey(BancoP),Replay);
		-+Replay;
		?bancoPublicKey(BancoP);
		gerarCarteira("http://testchain.chon.group:9984/","5vNY4i324pxKD7rV9K3sztju96BZgTKn7CnGuxhiVMqn",PublK,BancoP);
		-Replay;
		?protocolo(NrProtocolo);
		?myWallet(NrCarteira);
		.print("Solicitando Abertura de conta");
		.send(banco,achieve,cadastrarContaBancaria(NrProtocolo,NrCarteira));
		.abolish(protocolo(_)[source(percept)]).
	
+!comer: sintoFome & estoqueComida(C) & C>3 & energia(E) & E<=10 <-
	-+estoqueComida(C-3);
	-+energia(E+3);
	.print("Comendo........ [Geladeira=",C-3,"] [Energia=",E+3,"]");
	.wait(1000);
	!comer.
	
+!comer: energia(E) & E>10	<-
	.print("Satisfeito...... [Energia=",E,"]");
	-sintoFome;
	.wait(2000);
	!curtir.

+!comer <-
	?estoqueComida(X);
	.print("Preciso pedir comida..... [Geladeira=",X,"]");
	.wait(2000);
	!pedirComida.

+!curtir: energia(E) & E>5 <-
	.print("Curtindo....");
	.wait(2000);
	-+energia(E-3);
	-sintoFome;
	!curtir.

+!curtir: energia(E) & E<=5<-
	?energia(X);
	.print("Sem Energia..... [Energia=",X,"]");
	+sintoFome;
	.wait(2000);
	!comer.

+!pedirComida: contaBancaria(ok)[source(banco)] <-
	.print("Pedindo Comida.....");
	.send(banco,achieve,pix(cozinheiro,5));
	.wait(10000);
	?operacao(CodPix,comilao,cozinheiro,5);
	.send(cozinheiro,achieve,pedido(lanche,5,CodPix));
	-operacao(CodPix,comilao,cozinheiro,5);
	.wait(5000);
	!comer.

+!pedirComida: not contaBancaria(ok)[source(banco)] <-
	.wait(5000);
	!gerarCarteira;
	.wait(20000);
	!pedirComida.

+entregaComida(Product,Qtd)[source(Entregador)] <-
	?estoqueComida(X);
	-+estoqueComida(X+Qtd);
	.print("Oba! Chegou comida!!!!");
	-entregaComida(Product,Qtd)[source(Entregador)].
	
+semDinheiro[source(banco)] <-
	.drop_desire(curtir);
	.drop_desire(pedirComida);
	.print("Ababou a festa :(");
	.wait(5000);
	.stopMAS.

