package group.chon.velluscinum;

import com.bigchaindb.builders.BigchainDbConfigBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;

public class Info {
    final private String DRIVERNAME = "[Velluscinum]";
    final private String MANUAL = "/manual.txt";
    final private String defaultServer = "http://testchain.chon.group:9984/";
    final private String bobPrivateKey = "MC4CAQAwBQYDK2VwBCIEIJKHX4YV2Mp6GeMcsU6TENzTEtpxlmiC+1CTViNofoRV";
    final private String bobPublicKey  = "rO0ABXNyACpncm91cC5jaG9uLnZlbGx1c2NpbnVtLlB1YmxpY0tleUF0dHJpYnV0ZXP72Aas3Pd5eQIABUkAAWJbAAFJdAACW0JbAAFRcQB+AAFbAAFkcQB+AAFbAAFzcQB+AAF4cAAAAQB1cgACW0Ks8xf4BghU4AIAAHhwAAAAILCgDkonG+7EeOQvrQYYQy+n1/s9mQBNKwvfwU+AJIMrdXEAfgADAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAdXEAfgADAAAAIKN4WRPKTet1q9hBQU0KcACY6Hl3eUDHjHP+byvubANSdXEAfgADAAAAIJOzWQkoABDFaOUm8Ya9rhRr/hxgbBkC0Mx6XzFaKmMx";
    final private String alicePrivateKey = "MC4CAQAwBQYDK2VwBCIEIF9O4UoI3Ku3VQE2i7uWtmoRHkecEDgRXsuL1W/PsY51";
    final private String alicePublickey = "rO0ABXNyACpncm91cC5jaG9uLnZlbGx1c2NpbnVtLlB1YmxpY0tleUF0dHJpYnV0ZXP72Aas3Pd5eQIABUkAAWJbAAFJdAACW0JbAAFRcQB+AAFbAAFkcQB+AAFbAAFzcQB+AAF4cAAAAQB1cgACW0Ks8xf4BghU4AIAAHhwAAAAILCgDkonG+7EeOQvrQYYQy+n1/s9mQBNKwvfwU+AJIMrdXEAfgADAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAdXEAfgADAAAAIKN4WRPKTet1q9hBQU0KcACY6Hl3eUDHjHP+byvubANSdXEAfgADAAAAIM+9A0FOYC5qO0ZbPhoc+uebhu+S0frrPCOtFUv8zo11";

    public String getDRIVERNAME() {
        return DRIVERNAME;
    }

    public InputStreamReader getMANUAL(){
        InputStream stream = Main.class.getResourceAsStream(MANUAL);
        InputStreamReader reader = new InputStreamReader(stream);
        return  reader;
    }

    public String getAlicePrivateKey() {
        return alicePrivateKey;
    }

    public String getAlicePublickey() {
        return alicePublickey;
    }

    public String getBobPrivateKey() {
        return bobPrivateKey;
    }

    public String getBobPublicKey() {
        return bobPublicKey;
    }

    public String getDefaultServer() {
        return defaultServer;
    }

}
