// Set of common plans for cartago

// the goal !focus_env_art is produced by the JaCaMo launcher for the agent to focus on initial artifacts
+!jcm::focus_env_art([],_).
+!jcm::focus_env_art(L,0)   <- .print("Error handling ",L," (to join a workspace or focus on an artifact)").

@lf_env_art[atomic]
+!jcm::focus_env_art([H|T],Try)
   <- !jcm::focus_env_art(H,Try);
      !jcm::focus_env_art(T,Try).

+!jcm::focus_env_art(art_env(W,"",_),Try)
   <- //.print("joining workspace ",W);
      .concat("/main/",W,FullW);
      joinWorkspace(FullW,_);
      .print("join workspace ",FullW,": done").
+!jcm::focus_env_art(art_env(W,A,NS),Try)
   <- .concat("/main/",W,FullW);
      .print("focusing on artifact ",A," (at workspace ",FullW,") using namespace ",NS);
      joinWorkspace(FullW,WId);
      lookupArtifact(A,AId)[wid(WId)];
      NS::focus(AId)[wid(WId)];
      .print("focus on ",A,": done");      
      .
-!jcm::focus_env_art(L,Try)
   <- .print("waiting a bit to join/focus -- ",L," -- try #",Try);
      .wait(200);
      !jcm::focus_env_art(L,Try-1).

// rules to make old code compatible with cartago 3.0
focused(WksName,ArtName[artifact_type(Type)],ArtId)[hide_in_mind_inspector] :-
   focusing(ArtId,ArtName,Type,_,WksName,_).
joined(WksName,WksId)[hide_in_mind_inspector] :-   
   joinedWsp(WksId,WksName,_).