package group.chon.velluscinum;

import com.bigchaindb.builders.BigchainDbConfigBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;

public class Info {
    final private String DRIVERNAME = "[Velluscinum]";
    final private String MANUAL = "/manual.txt";
    final private String defaultServer = "http://testchain.chon.group:9984/";
    final private String bobPrivateKey = "MC4CAQAwBQYDK2VwBCIEINrKHkh7bJlpSeGJyutdxrsa6qqtHVIbm6YXyQymTYK8";
    final private String bobPublicKey  = "MCowBQYDK2VwAyEAjFVzFInLZCIpo94Ii5f74dtr/FcKQs8M0m9Z2JOAMVU=";
    final private String alicePrivateKey = "MC4CAQAwBQYDK2VwBCIEIANd+6AE0IetQ1vaQC88DhYuEs9miXjvv2LkXSxR4j3Z";
    final private String alicePublickey = "MCowBQYDK2VwAyEAEuN5rvkEHUqJcFr9bzh8qzbMellY9oHY32SkUoL0cL8=";

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
