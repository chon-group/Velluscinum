{ include("tester_agent.asl") }
{ include("single.asl") }

/**
 * Test thermostat
 */
@[test]
+!test_thermostat
    <-

    //** INITIAL CONDITION - Let us say the temperature starts at 15 degrees **//
    -+temperature(15);

    //** REPEAT THE TEST FOR AN ARBITRARY NUMBER OF ITERATIONS **//
    for ( .range(I,1,20) ) {
        !heat_control;
        ?temperature(T);

        //** TEST CONDITION 1 - cooler is on? **//
        if (T > 10) {
            if (not cooling) {
                !force_failure("Cooler was supposed to be on");
            } else {
                !force_pass;
            }

            //** EMULATE AGENT IS ACTING UPON THE ENVIRONMENT - Let us say the temperatura dropped 1 degree **//
            -+temperature(T-1);

        //** TEST CONDITION 2 - cooler is off? **//
        } else {
            if (cooling) {
                !force_failure("Cooler was supposed to be off ");
            } else {
                !force_pass;
            }

            //** EMULATE ENVIRONMENT HAS ARBITRARILY CHANGED - Let us say the temperatura raised 1 or 2 degrees **//
            .random(X);
            if (X <= 0.2) {
                C = math.ceil(X*10);
                -+temperature( T + C );
            }

        }
    }
.
