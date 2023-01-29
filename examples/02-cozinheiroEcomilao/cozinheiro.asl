// Agent cozinheiro in project cozinheiroEcomilao.mas2j
/* Crenças Iniciais */
chainServer("http://testchain.chon.group:9984/").
ultimoPedido(0).

/* Objetivos Iniciais */
//!gerarCarteira.
!createWallet.


/* Planos */
+!createWallet <-
	.print("Gerando carteira digital");
	createWallet("base58").

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
