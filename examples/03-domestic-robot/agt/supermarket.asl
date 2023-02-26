last_order_id(1). // initial belief

// plan to achieve the goal "order" for agent Ag
+!order(Product,Qtd,Transaction)[source(Ag)] <-
    !verifyPayment(Transaction);   
    ?last_order_id(N);
     OrderId = N + 1;
     -+last_order_id(OrderId);
     .print("Delivering the Beer!!!");
     deliver(Product,Qtd);
     .send(Ag, tell, delivered(Product,Qtd,OrderId)).

+cryptocurrency(Coin)[source(bank)]<-
   .print("Creating wallet");
   .buildWallet(supermarketWallet);
   ?supermarketWallet(PrivateKey,PublicKey);
   .broadcast(tell,supermarketWallet(PublicKey)).

+!verifyPayment(T): supermarketWallet(PrivateKey,PublicKey) 
  & bigchaindbNode(Server) <-
  .print("Validating the payment");
  .stampTransaction(Server,PrivateKey,PublicKey,T).