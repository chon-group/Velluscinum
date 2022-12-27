package group.chon.velluscinum;

import com.bigchaindb.model.GenericCallback;
import okhttp3.Response;

public class ServerResponse {
    private static boolean boolWait = false;
    private static boolean lock	 = false;

    public static boolean isLock() {
        return lock;
    }

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

    public static boolean isBoolWait() {
        return boolWait;
    }

    public static void setBoolWait(boolean newWait) {
        boolWait = newWait;
    }

    public static void waitDone(){
        while(isBoolWait()){
            try {
                Thread.sleep(2000);
            }catch(Exception e) {}
        }
    }


    public static GenericCallback handleServerResponse() {
        //define callback methods to verify response from BigchainDBServer
        GenericCallback callback = new GenericCallback() {

            @Override
            public void transactionMalformed(Response response) {
                System.out.print("[malformed " + response.message()+"]");
                setBoolWait(false);
                onFailure();
            }

            @Override
            public void pushedSuccessfully(Response response) {
                System.out.print("[pushed]");
                setBoolWait(false);
                onSuccess(response);
            }

            @Override
            public void otherError(Response response) {
                System.out.print("[otherError" + response.message()+"]");
                setBoolWait(false);
                onFailure();
            }
        };

        return callback;
    }

    private static void onSuccess(Response response) {
        //TODO : Add your logic here with response from server
        System.out.println("[successfully]");
        setBoolWait(false);
    }

    private static void onFailure() {
        //TODO : Add your logic here
        System.out.println("[Transaction failed]");
        setBoolWait(false);
    }

}
