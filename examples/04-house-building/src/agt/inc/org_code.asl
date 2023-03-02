// Common code for the organisational part of the system

task_roles("SitePreparation",  [site_prep_contractor]).
task_roles("Floors",           [bricklayer]).
task_roles("Walls",            [bricklayer]).
task_roles("Roof",             [roofer]).
task_roles("WindowsDoors",     [window_fitter, door_fitter]).
task_roles("Plumbing",         [plumber]).
task_roles("ElectricalSystem", [electrician]).
task_roles("Painting",         [painter]).

/* Velluscinum Added the TransactionID as input*/
+!contract(Task,GroupName,ArtId,TransactionID)
    : task_roles(Task,Roles)  <- 
   /*Velluscinum */
   !validateContract(Task,ArtId,TransactionID);

   !in_ora4mas;
      lookupArtifact(GroupName, GroupId);
      for ( .member( Role, Roles) ) {
         adoptRole(Role)[artifact_id(GroupId)];
         focus(GroupId);
      }.

-!contract(Service,GroupName)[error(E),error_msg(Msg),code(Cmd),code_src(Src),code_line(Line)]
   <- .print("Failed to sign the contract for ",Service,"/",GroupName,": ",Msg," (",E,"). command: ",Cmd, " on ",Src,":", Line).

+!bestBid(ArtId,ContractID,W,Price)[source(Client)]: myWallet(P,Q) <-
   .send(Client, achieve, prepareContract(ArtId,Q));
   +contract(Client,W,ArtId,ContractID,Price).

+!validateContract(Task,ArtId,TransactionID): myWallet(P,Q) & bigchaindbNode(S) & contract(Client,W,Artefact,ContractID,Price) & ArtId==Artefact <-
   .stampTransaction(S,P,Q,TransactionID);
   +agreement(ArtId,Task).

+!requestPayment(Service): myWallet(P,Q) & bigchaindbNode(S) 
      & agreement(ArtId,Task) & contract(Client,W,ArtId,ContractID,Price) & Service=Task <-
   
   .print("Requesting payment of ",Price," for task ",Task,", according to contract ",ContractID);
   .send(Client,achieve,payment(ArtId,Q)).

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
