{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }

/* application domain goals */
+!wsecs[scheme(S)]  <- .print("writing sections for scheme ",S,"...").

/* organisational plans for obligations (this agent does not use the obedient plans) */

// only commits to mColaborator!
+obligation(Ag,Norm,committed(Ag,mColaborator,Scheme),DeadLine)[workspace(_,W)] : .my_name(Ag)
   <- commitMission(mColaborator)[artifact_name(Scheme),wid(W)].
+obligation(Ag,Norm,committed(Ag,_,Scheme),DeadLine).

// obligation to achieve a goal
+obligation(Ag,Norm,done(Scheme,Goal,Ag),Deadline)[artifact_id(ArtId)]
    : .my_name(Ag)
   <- //.print(" ---> working to achieve ",Goal," in scheme ",Scheme);
      !Goal[scheme(Scheme)];
      //.print(" <--- done");
      goalAchieved(Goal)[artifact_id(ArtId)].
