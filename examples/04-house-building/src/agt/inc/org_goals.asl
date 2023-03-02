// plan to execute organisational goals

+!site_prepared <-  // the goal (introduced by the organisational obligation)
   prepareSite;  // simulation of the action (in GUI artifact)
                                                            !requestPayment("SitePreparation").

+!floors_laid                   <- layFloors;               !requestPayment("Floors").
+!walls_built                   <- buildWalls;              !requestPayment("Walls").
+!roof_built                    <- buildRoof;               !requestPayment("Roof").
+!windows_fitted                <- fitWindows.
+!doors_fitted                  <- fitDoors;                !requestPayment("WindowsDoors").
+!electrical_system_installed   <- installElectricalSystem; !requestPayment("ElectricalSystem").
+!plumbing_installed            <- installPlumbing;         !requestPayment("Plumbing").
+!exterior_painted              <- paintExterior;           !requestPayment("Painting").
+!interior_painted              <- paintInterior.