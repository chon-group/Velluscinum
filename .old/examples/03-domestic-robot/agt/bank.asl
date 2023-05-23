// Agent bank in project domestic-robot.mas2j
/* To verify transactions consult --> http://testchain.chon.group:9984/api/v1/transactions/TRANSACTION-ID-HERE */

bigchaindbNode("http://testchain.chon.group:9984/").   /* initial belief */

/* Initial goals */
!createCoin.

/* Plains */
+!createCoin: bigchaindbNode(DLTNode) <-
	.print("Creating the MAS2jCoin");
	.buildWallet(bankWallet);
	.wait(bankWallet(PrK,PuK));
	.deployToken(DLTNode,PrK,PuK,"cryptocurrency:MAS2jCoin",1000,mas2jCoin);
	.wait(mas2jCoin(Coin));
	.broadcast(tell,bigchaindbNode(DLTNode));
	.broadcast(tell,cryptocurrency(Coin)).
	
+!cashOut(ClientWallet,Amount)[source(Client)]: 
		mas2jCoin(Coin) & bankWallet(Prk,PuB) & bigchaindbNode(DLTNode) <-
	.print("Hello Agent ",Client,", wellcome to MAS2jBank!");
	.transferToken(DLTNode,Prk,PuB,Coin,ClientWallet,Amount,transactionTransfer).