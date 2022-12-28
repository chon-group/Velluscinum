// Agent cozinheiro in project cozinheiroEcomilao.mas2j

/* Crenças Iniciais */
myPublicKey("MCowBQYDK2VwAyEAwoyIo/X+ka04yOS8hfRxyb+PuuodHVGFoI1BDFw2ekA=").
ultimoPedido(0).

/* Objetivos Iniciais */
!gerarCarteira.

/* Planos */
+!gerarCarteira: myPublicKey(PublK) & not protocolo(P)  <-
		.print("Gerando carteira digital");
		.send(banco,askOne,bancoPublicKey(BancoP),Replay);
		-+Replay;
		?bancoPublicKey(BancoP);
		gerarCarteira("http://testchain.chon.group:9984/","MC4CAQAwBQYDK2VwBCIEIF24sBVvj3ocOJsAYcpbtavUXzUzEWBxkz9hsJ94jCT+",PublK,BancoP);
		-Replay;
		?protocolo(NrProtocolo);
		?myWallet(NrCarteira);
		.print("Solicitando Abertura de conta");
		.send(banco,achieve,cadastrarContaBancaria(NrProtocolo,NrCarteira));
		.abolish(protocolo(_)[source(percept)]).
		
+contaBancaria(ok)[source(banco)] <-
	!aguardarPedidos.
		
+!aguardarPedidos: not preparandoPedido <-
	.print("Aguardando Pedidos");
	.wait(5000);
	!aguardarPedidos.

+!aguardarPedidos: preparandoPedido<-
	.wait(10000);
	!aguardarPedidos.

+!pedido(Product,Qtd,Pix)[source(Cliente)] <-
	.print("Recebi pedido... validando");
	.send(banco,askOne,operacao(Pix,Cliente,cozinheiro,Qtd),Reply);
	+Reply;
	?operacao(Registro,Cliente,cozinheiro,Qtd);
	+preparandoPedido;
	!prepararPedido(Product,Qtd,Pix,Registro,Cliente);
	+atendido(Pix);
	-preparandoPedido.

+!prepararPedido(Product,Qtd,Pix,Validacao,Cliente): Pix=Validacao & not atendido(Pix)<-
	?ultimoPedido(N);
	NrPedido=N+1;
	-+ultimoPedido(NrPedido);
	.print("Preparando o pedido Nr ",NrPedido);
	.wait(2000);
	.send(Cliente,tell,entregaComida(Product,Qtd)).
	
-!prepararPedido(Product,Qtd,Pix,Validacao,Cliente) <-
	.print("Pix inválido ou já atendido").
