package group.chon.velluscinum;

import com.bigchaindb.model.GenericCallback;
import okhttp3.Response;

/**
 * BigChainDB Driver Communication Manager
 *
 * @author Nilson Mori
 * @author BigChainDB
 */
public class ServerResponse {
    private static boolean boolWait = false;
    private static boolean lock	 = false;
    private static boolean result = false;

    /**
     *  Checks whether it is waiting for a transaction preparation.
     *
     * @return A boolean value (true OR false).
     */
    public static boolean isLock() {
        return lock;
    }

    /**
     *  Defines whether to wait for the transaction preparation.
     *
     * @param newLock A boolean value (true OR false).
     */
    public static void setLock(boolean newLock) {
        if(newLock){
            while(isLock()){
                try {
                    Thread.sleep(2000);
                }catch(Exception e) {}
            }
        }
        lock = newLock;
    }

    /**
     *  Checks whether it is waiting for the execution of a transaction on the BigChainDB Network.
     *
     * @return A boolean value (true OR false).
     */
    public static boolean isBoolWait() {
        return boolWait;
    }

    /**
     *  Defines whether to wait for a transaction to be carried out on the BigChainDB Network.
     *
     * @param wait A boolean value (true OR false)
     */
    public static void setBoolWait(boolean wait) {
        boolWait = wait;
    }

    /**
     * Waits for the transaction to be written to the BigChainDB network.
     */
    public static void waitDone(){
        while(isBoolWait()){
            try {
                Thread.sleep(2000);
            }catch(Exception e) {}
        }
    }


    /**
     *  Defines the callback methods to verify response from BigchainDB Server.
     *
     * @return callback
     */
    public static GenericCallback handleServerResponse() {
        GenericCallback callback = new GenericCallback() {

            @Override
            public void transactionMalformed(Response response) {
                ServerResponse.setResult(false);
                System.out.print("[malformed " + response.message()+"]");
                setBoolWait(false);
                onFailure();
            }

            @Override
            public void pushedSuccessfully(Response response) {
                ServerResponse.setResult(true);
                System.out.print("[pushed]");
                setBoolWait(false);
                onSuccess(response);
            }

            @Override
            public void otherError(Response response) {
                ServerResponse.setResult(false);
                System.out.print("[otherError" + response.message()+"]");
                setBoolWait(false);
                onFailure();
            }
        };

        return callback;
    }

    private static void onSuccess(Response response) {
        ServerResponse.setResult(true);
        System.out.println("[successfully]");
        setBoolWait(false);
    }

    private static void onFailure() {
        ServerResponse.setResult(false);
        System.out.println("[Transaction failed]");
        setBoolWait(false);
    }

    /**
     *  Defines if a transaction was completed deployed
     *
     * @param result boolean
     *
     */
    public static void setResult(boolean result) {
        ServerResponse.result = result;
    }

    /**
     * Verifies if a transaction was completed deployed
     *
      * @return boolean
     */
    public static boolean isResult() {
        return result;
    }
}
