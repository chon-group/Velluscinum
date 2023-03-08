last_order_id(1). // initial belief

// plan to achieve the goal "order" for agent Ag
+!order(Product,Qtd)[source(Ag)] : true
  <- ?last_order_id(N);
     OrderId = N + 1;
     -+last_order_id(OrderId);
     !requestPayment(OrderId,Ag,Qtd);    
     .wait(orderPayed(OrderId));
     deliver(Product,Qtd);
     .send(Ag, tell, delivered(Product,Qtd,OrderId)).

+!requestPayment(OrderId,Ag,Amount):supermarketWallet(PrivateKey,PublicKey) & bigchaindbNode(DLTNode) <-
   .print("Requesting payment");
   .send(Ag, tell, payOrder(OrderId,Amount,PublicKey));
   .wait({+payment(OrderId,Transaction)});
   .print("Validating payment");
   .stampTransaction(DLTNode,PrivateKey,PublicKey,Transaction,confirmed(OrderId));
   +orderPayed(OrderId).

+cryptocurrency(Coin)[source(bank)]<-
   .print("Creating wallet");
   .buildWallet(supermarketWallet).