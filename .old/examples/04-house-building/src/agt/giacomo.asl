myWallet("BbwdGTSfQA368Cjemm9eiazryWKqNeQt9DGJ6L5s8DQZ","T2fReYrUcRFmgTyY91ZS7mKXmRAoQsTRboEeCvn7NDJ"). /* Auto generated GiacomoWallet - consult startMAS.sh*/
// Agent Giacomo, who wants to build a house

{ include("common.asl") }

/* Initial beliefs and rules */

// counts the number of tasks based on the observable properties of the auction artifacts
number_of_tasks(NS) :- .findall( S, task(S), L) & .length(L,NS).


/* Initial goals */

!have_a_house.


/* Plans */

+!have_a_house
   <- !contract; // hire the companies that will build the house
      !execute;  // (simulates) the execution of the construction
   .
-!have_a_house[error(E),error_msg(Msg),code(Cmd),code_src(Src),code_line(Line)]
   <- .print("Failed to build a house due to: ",Msg," (",E,"). Command: ",Cmd, " on ",Src,":", Line).


/* Plans for Contracting */

+!contract
   <- !create_auction_artifacts;
      !wait_for_bids.
//      !dispose_auction_artifacts.

+!create_auction_artifacts
   <-  !create_auction_artifact("SitePreparation", 2000); // 2000 is the maximum value I can pay for the task
       !create_auction_artifact("Floors",          1000);
       !create_auction_artifact("Walls",           1000);
       !create_auction_artifact("Roof",            2000);
       !create_auction_artifact("WindowsDoors",    2500);
       !create_auction_artifact("Plumbing",         500);
       !create_auction_artifact("ElectricalSystem", 500);
       !create_auction_artifact("Painting",        1200).

+!create_auction_artifact(Task,MaxPrice)
   <- .concat("auction_for_",Task,ArtName);
      makeArtifact(ArtName, "tools.AuctionArt", [Task, MaxPrice], ArtId);
      focus(ArtId);
      ?myWallet(P,Q);                                 /* new action */
      ?bigchaindbNode(S);                             /* new action */
      .concat("Contract of:",Task,I);                 /* new action */
      .concat("MaxPrice:",MaxPrice,M);                /* new action */
      .deployNFT(S,P,Q,I,M,registredContract(ArtId)); /* new action */
      .wait(registredContract(ArtId,ContractID)).     /* new action */

-!create_auction_artifact(Task,MaxPrice)[error_code(Code)]
   <- .print("Error creating artifact ", Code).

+!wait_for_bids
   <- println("Waiting bids for 5 seconds...");
      .wait(5000); // use an internal deadline of 5 seconds to close the auctions
      !show_winners.

+!show_winners
   <- for ( currentWinner(Ag)[artifact_id(ArtId)] ) {
         ?currentBid(Price)[artifact_id(ArtId)]; // check the current bid
         ?task(Task)[artifact_id(ArtId)];          // and the task it is for
         println("Winner of task ", Task," is ", Ag, " for ", Price);
         ?registredContract(ArtId,ContractID);       /* new action */
         ?myWallet(P,Q);                             /* new action */
         +bestBid(ArtId,Task,Price);                 /* new action */
         .send(Ag, achieve, 
            bestBid(ArtId,ContractID,Q,Price));      /* new action */ 
      }.

//+!dispose_auction_artifacts
//   <- for ( task(_)[artifact_id(ArtId)] ) {
//         stopFocus(ArtId)
//         //disposeArtifact(ArtId)
//      }.

/* Plans for managing the execution of the house construction */

+!execute
   <- println;
      println("*** Execution Phase ***");
      println;

      // create the group
      .my_name(Me);
      createWorkspace("ora4mas");
      joinWorkspace("ora4mas",WOrg);

      // NB.: we (have to) use the same id for OrgBoard and Workspace (ora4mas in this example)
      makeArtifact(ora4mas, "ora4mas.nopl.OrgBoard", ["src/org/house-os.xml"], OrgArtId)[wid(WOrg)];
      focus(OrgArtId);
      createGroup(hsh_group, house_group, GrArtId);
      debug(inspector_gui(on))[artifact_id(GrArtId)];
      adoptRole(house_owner)[artifact_id(GrArtId)];
      focus(GrArtId);

      !contract_winners("hsh_group"); // they will enter into the group

      // create the GUI artifact
      makeArtifact("housegui", "simulator.House");

      // create the scheme
      createScheme(bhsch, build_house_sch, SchArtId);
      debug(inspector_gui(on))[artifact_id(SchArtId)];
      focus(SchArtId);

      ?formationStatus(ok)[artifact_id(GrArtId)]; // see plan below to ensure we wait until it is well formed
      addScheme("bhsch")[artifact_id(GrArtId)];
      commitMission("management_of_house_building")[artifact_id(SchArtId)].

+!contract_winners(GroupName)
    : number_of_tasks(NS) &
      .findall( ArtId, currentWinner(A)[artifact_id(ArtId)] & A \== "no_winner", L) &
      .length(L, NS)
   <- for ( currentWinner(Ag)[artifact_id(ArtId)] ) {
        ?task(Task)[artifact_id(ArtId)];
        println("Contracting ",Ag," for ", Task);
        .wait(agreement(Company,ArtId,Price,TransactionID)); /* new action */
        .send(Ag, achieve, 
        contract(Task,GroupName,ArtId,TransactionID))    /* updated action */
      }.
+!contract_winners(_)
   <- println("** I didn't find enough builders!");
      .fail.

// plans to wait until the group is well formed
// makes this intention suspend until the group is believed to be well formed
+?formationStatus(ok)[artifact_id(G)]
   <- .wait({+formationStatus(ok)[artifact_id(G)]}).

+!house_built // I have an obligation towards the top-level goal of the scheme: finished!
   <- println("*** Finished ***").

/* new plans */
+!prepareContract(ContractID,CompanyPublicKey)[source(Company)]: 
         bigchaindbNode(S) & registredContract(ArtId,RegistredID) &
         myWallet(P,Q) & ContractID=RegistredID & 
         bestBid(ArtefactId,Task,Price) & ArtId=ArtefactId <- 

   .concat("ArtId:",ArtId,";Winner:",Company,";Price:",Price,M);
   .transferNFT(S,P,Q,ContractID,CompanyPublicKey,
                                M,agreement(Company,ArtId,Price));
   .wait(agreement(Company,ArtId,Price,ContractTransaction)).
   

+!payment(ArtID, CompanyPublicKey)[source(Company)]: 
        myWallet(P,Q) & bigchaindbNode(S) & jacamoCoin(C) &
        agreement(Company,ArtID,Price,TransactionID) & 
        registredContract(ArtID,ContractID) & bestBid(ArtID,Task,Price)<-

   .transferToken(S,P,Q,C,CompanyPublicKey,Price,paymentTransaction(ArtID));
   .wait(paymentTransaction(ArtID,PaymentTransaction));
   .send(Company, achieve, paymentProof(ArtID,PaymentTransaction)).
