// Agent bob in project verifyTransaction
/* Initial goals */
!start.

/* Plans */
+!start <-
	.send(alice,askOne,bank(_),Reply);
	+Reply;
	.send(alice,askOne,account(_),Reply2);
	+Reply2;

	.velluscinum.buildWallet(myWallet);
	.wait(myWallet(P,Q));

	?bank(S);
	.velluscinum.deployToken(S,P,Q,"name:BobCoin;type:criptocurrency",1000,myCoin);
	.wait(myCoin(TokenID));

	?account(AliceWallet);
	!transferToken(S,P,Q,TokenID,AliceWallet,10);
.

+!transferToken(S,P,Q,Token,Recipient,Amount) <-
	.velluscinum.transferToken(S,P,Q,Token,Recipient,Amount,t);
	.wait(t(Transaction));
	.send(alice,achieve,verify(Transaction,Token,Amount));
	.wait(10000);
	.velluscinum.transferToken(S,P,Q,Token,Recipient,Amount,t2);
	.wait(t2(Transaction2));
	.send(alice,achieve,verify(Transaction2,Token,Amount+1));
.