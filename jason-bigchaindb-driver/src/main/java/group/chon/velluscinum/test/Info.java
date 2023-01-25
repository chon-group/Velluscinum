package group.chon.velluscinum.test;

import group.chon.velluscinum.Main;

import java.io.InputStream;
import java.io.InputStreamReader;

public class Info {
    final private String defaultServer = "http://testchain.chon.group:9984/";
    final private String bobPrivateKey = "A4BAzNZdGBqkGHQP7gWovcYaGP3UELL6f4THhuHSQHCK";
    final private String bobPublicKey  = "arDLZkDEmZi5wWtebvZrC5KgSs2tX8ULLeBSFp8dTJR";
    final private String alicePrivateKey = "7ZmTjitG3mhsgY6qon2HLozdX9S6kEuvBbFKdfgJahGj";
    final private String alicePublickey = "FNJPJdtuPQYsqHG6tuUjKjqv7SW84U4ipiyyLV2j6MEW";

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
