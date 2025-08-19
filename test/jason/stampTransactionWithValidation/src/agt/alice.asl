// Agent alice in project verifyTransaction

/* Initial beliefs and rules */
myPrivateKey("7neiPonWcQEXBoHooxMhTVvHQFAJGDdZYF1HuPTgH5ia").
account("C9q3KZzdBXaTyCpLUyizFXZQaPcZX3RjH8wLavRA91Lk").
bank("http://testchain.chon.group:9984/").

/* Plans */
+!verify(Transaction,Token,Amount) <-
	?myPrivateKey(P);
	?account(Q);
	?bank(S);
	.velluscinum.stampTransaction(S,P,Q,Transaction,all(asset(Token),amount(Amount)));
	.print("The Payment is confirmed! 🤑🤑🤑🤑🤑🤑");
.

-!verify(Transaction,Token,Amount) <-
	.print("The Payment is not confirmed!");
	.wait(5000);
	.stopMAS;
.