// Agent cozinheiro in project cozinheiroEcomilao.mas2j
/* Crenças Iniciais */
ultimoPedido(0).

/* Objetivos Iniciais */
!createWallet.


/* Planos */
+!createWallet <-
	.print("Gerando carteira digital");
	.buildWallet(myWallet);
	?myWallet(Priv,Pub);
	+cozinheiroWallet(Pub);
	!aguardarPedidos.
		
+!aguardarPedidos: not preparandoPedido <-
	.print("Aguardando Pedidos");
	.wait(5000);
	!aguardarPedidos.

+!aguardarPedidos: preparandoPedido<-
	.wait(10000);
	!aguardarPedidos.

+!pedido(Product,Qtd,Pix)[source(Cliente)]: cryptocurrency(Coin) 
			& chainServer(Server) & myWallet(MyPriv,MyPub) <-
	.print("Recebi pedido... validando");
	.stampTransaction(Server,MyPriv,MyPub,Pix);
	+preparandoPedido;
	!prepararPedido(Product,Qtd,Pix,Registro,Cliente);
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
