// plan to execute organisational goals

+!site_prepared      // the goal (introduced by the organisational obligation)
   <- prepareSite;   // simulation of the action (in GUI artifact)
   !requestPayment("SitePreparation"). /* new action */

+!floors_laid                 <- layFloors;
   !requestPayment("Floors").          /* new action */
+!walls_built                 <- buildWalls;
   !requestPayment("Walls").           /* new action */
+!roof_built                  <- buildRoof;
   !requestPayment("Roof").            /* new action */
+!windows_fitted              <- fitWindows.
+!doors_fitted                <- fitDoors;
   !requestPayment("WindowsDoors").    /* new action */
+!electrical_system_installed <- installElectricalSystem;
   !requestPayment("ElectricalSystem"). /* new action */
+!plumbing_installed          <- installPlumbing;
   !requestPayment("Plumbing").        /* new action */
+!exterior_painted            <- paintExterior;
   !requestPayment("Painting").        /* new action */
+!interior_painted            <- paintInterior.