// Agent cozinheiro in project cozinheiroEcomilao.mas2j
/* Crenças Iniciais */
ultimoPedido(0).

/* Objetivos Iniciais */
!createWallet.

/* Planos */
+!createWallet <-
	.print("Gerando carteira digital");
	.buildWallet(myWallet);
	.wait(myWallet(Priv,Pub));
	+cozinheiroWallet(Pub).

+!pedido(Product,Qtd,Pix)[source(Cliente)]: cryptocurrency(Coin) 
			& chainServer(Server) & myWallet(MyPriv,MyPub) <-
	?ultimoPedido(N);
	NrPedido=N+1;
	-+ultimoPedido(NrPedido);
	.print("Recebi pedido",NrPedido," ...validando");
	.stampTransaction(Server,MyPriv,MyPub,Pix,pagamento(NrPedido));
	+preparandoPedido;
	!prepararPedido(NrPedido,Product,Qtd,Cliente);
	-preparandoPedido.

+!prepararPedido(NrPedido,Product,Qtd,Cliente) <-
	.print("Preparando o pedido Nr ",NrPedido);
	.wait(1000);
	.send(Cliente,tell,entregaComida(Product,Qtd)).