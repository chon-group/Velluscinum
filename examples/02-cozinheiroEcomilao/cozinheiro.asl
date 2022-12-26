// Agent cozinheiro in project cozinheiroEcomilao.mas2j

/* Crenças Iniciais */
myPublicKey("rO0ABXNyACpncm91cC5jaG9uLnZlbGx1c2NpbnVtLlB1YmxpY0tleUF0dHJpYnV0ZXP72Aas3Pd5eQIABUkAAWJbAAFJdAACW0JbAAFRcQB+AAFbAAFkcQB+AAFbAAFzcQB+AAF4cAAAAQB1cgACW0Ks8xf4BghU4AIAAHhwAAAAILCgDkonG+7EeOQvrQYYQy+n1/s9mQBNKwvfwU+AJIMrdXEAfgADAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAdXEAfgADAAAAIKN4WRPKTet1q9hBQU0KcACY6Hl3eUDHjHP+byvubANSdXEAfgADAAAAIM+9A0FOYC5qO0ZbPhoc+uebhu+S0frrPCOtFUv8zo11").
ultimoPedido(0).

/* Objetivos Iniciais */
!gerarCarteira.

/* Planos */
+!gerarCarteira: myPublicKey(PublK) & not protocolo(P)  <-
		.print("Gerando carteira digital");
		.send(banco,askOne,bancoPublicKey(BancoP),Replay);
		-+Replay;
		?bancoPublicKey(BancoP);
		gerarCarteira("http://testchain.chon.group:9984/","MC4CAQAwBQYDK2VwBCIEIF9O4UoI3Ku3VQE2i7uWtmoRHkecEDgRXsuL1W/PsY51",PublK,BancoP);
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
