// Common code for the organisational part of the system

task_roles("SitePreparation",  [site_prep_contractor]).
task_roles("Floors",           [bricklayer]).
task_roles("Walls",            [bricklayer]).
task_roles("Roof",             [roofer]).
task_roles("WindowsDoors",     [window_fitter, door_fitter]).
task_roles("Plumbing",         [plumber]).
task_roles("ElectricalSystem", [electrician]).
task_roles("Painting",         [painter]).

+!contract(Task,GroupName,ArtId,TransactionID)
    : task_roles(Task,Roles) <- 

      !validateContract(Task,ArtId,TransactionID);
   
      !in_ora4mas;
      lookupArtifact(GroupName, GroupId);
      for ( .member( Role, Roles) ) {
         adoptRole(Role)[artifact_id(GroupId)];
         focus(GroupId);
      }.

-!contract(Service,GroupName)[error(E),error_msg(Msg),code(Cmd),code_src(Src),code_line(Line)]
   <- .print("Failed to sign the contract for ",Service,"/",GroupName,": ",Msg," (",E,"). command: ",Cmd, " on ",Src,":", Line).


+!in_ora4mas : in_ora4mas.
+!in_ora4mas : .intend(in_ora4mas)
   <- .wait({+in_ora4mas},100,_);
      !in_ora4mas.
@[atomic]
+!in_ora4mas
   <- joinWorkspace("ora4mas",_);
      +in_ora4mas.

{ include("$jacamoJar/templates/common-moise.asl") }
{ include("$jacamoJar/templates/org-obedient.asl") }

+!bestBid(ArtId,ContractID,W,Price)[source(Client)] <-
   !createWallet;
   +contract(Client,W,ArtId,ContractID,Price);
   .wait(publicwallet(Q));
   .send(Client, achieve, prepareContract(ContractID,Q)).

+!validateContract(Task,ArtId,TransactionID): myWallet(P,Q) & bigchaindbNode(S) & contract(Client,W,Artefact,ContractID,Price) & ArtId==Artefact <-
   .stampTransaction(S,P,Q,TransactionID);
   +agreement(ArtId,Task).

+!requestPayment(Service): myWallet(P,Q) & bigchaindbNode(S) 
      & agreement(ArtId,Task) & contract(Client,W,ArtId,ContractID,Price) & Service=Task <-
   +waitingPayment(ArtId,ContractID);
   .send(Client,achieve,payment(ArtId,Q));
   .wait({+payed(ArtId,T)}).

+!paymentProof(ArtID,PaymentTransaction): myWallet(P,Q) & bigchaindbNode(S) 
      & waitingPayment(ArtID,ContractID) & contract(Client,W,ArtID,ContractID,Price) <-
   .stampTransaction(S,P,Q,PaymentTransaction);
   .concat("Payment:OK;Value:",Price,M);
   .transferNFT(S,P,Q,ContractID,W,M,payed(ArtID));
   .wait(payed(ArtID,T));
   -waitingPayment(ArtID,ContractID).
    //.print("Tanks Agent ",Client).

+!createWallet: not publicwallet(Q) <-
   .buildWallet(myWallet);
   .wait(myWallet(P,Q));
   +publicwallet(Q).
   //.print("Creating a Wallet to receiver the contract",Q).

+!createWallet: publicwallet(Q) <- true.
