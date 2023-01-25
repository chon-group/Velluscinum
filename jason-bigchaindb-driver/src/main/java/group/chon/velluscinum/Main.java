package group.chon.velluscinum;

import com.bigchaindb.exceptions.TransactionNotFoundException;
import com.bigchaindb.util.Base58;
import com.bigchaindb.util.KeyPairUtils;
import group.chon.velluscinum.model.NonFungibleToken;
import group.chon.velluscinum.model.TransfAdditionalInfo;
import group.chon.velluscinum.model.WalletContent;
import group.chon.velluscinum.test.Info;
import net.i2p.crypto.eddsa.*;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Base64;

public class Main {
    public static void main(String[] args) throws Exception {
        String op = null;
        try {
            op = args[0];
            if(op.equals("createKeys")) {createKeys(args);}
            else if(op.equals("newNFT")) {newNFT(args);}
            else if(op.equals("transferNFT")) {transferNFT(args);}
        } catch (Exception ex) {
            InputStream inputStream = Main.class.getResourceAsStream("/manual.txt");
            InputStreamReader inputStreamReaderMANUAL = new InputStreamReader(inputStream);
            BufferedReader buffered = new BufferedReader(inputStreamReaderMANUAL);
            String line = buffered.readLine();
            while(line != null){
                System.out.println(line);
                line = buffered.readLine();
            }
            buffered.close();
            test4();
            System.exit(0);
        }
    }

    private static void createKeys(String[] args){
        String output = null;
        KeyManagement keyManagement = new KeyManagement();
        KeyPair bobKeyPair 	= keyManagement.newKey();
        EdDSAPrivateKey bobPrivateKey = (EdDSAPrivateKey) bobKeyPair.getPrivate();
        EdDSAPublicKey bobPublicKey  = (EdDSAPublicKey)  bobKeyPair.getPublic();
        keyManagement.keyToFile(bobPrivateKey,"base58",args[1]+".private.key");
        keyManagement.keyToFile(bobPublicKey,"base58",args[1]+".public.key");
        System.exit(0);
    }

    private static void newNFT(String[] args) throws Exception {
        BigchainDBDriver bigchainDBDriver = new BigchainDBDriver();
        KeyManagement keyManagement = new KeyManagement();
        NonFungibleToken nonFungibleToken = new NonFungibleToken();
        String strPrivateKey = keyManagement.importKeyFromFile(args[2]);
        //[SERVER] [PRIVATEKEY-FILE] [PUBLICKEY-FILE] [NFT-FILE]
        bigchainDBDriver.registerNFT(
                args[1],
                keyManagement.importKeyFromFile(args[2]),
                keyManagement.importKeyFromFile(args[3]),
                nonFungibleToken.importFromFile(args[4]).toString());
        System.exit(0);
    }

    private static void transferNFT(String[] args){
        BigchainDBDriver bigchainDBDriver = new BigchainDBDriver();
        TransfAdditionalInfo transfAdditionalInfo = new TransfAdditionalInfo();
        KeyManagement keyManagement = new KeyManagement();
        //[SERVER] [PRIVATEKEY-FILE] [PUBLICKEY-FILE] [ASSET-ID] [METADATA] [PUBLIC_DEST_KEY-FILE]
        bigchainDBDriver.transferNFT(
                args[1],
                keyManagement.importKeyFromFile(args[2]),
                keyManagement.importKeyFromFile(args[3]),
                args[4],
                transfAdditionalInfo.importFromFile(args[5]).toString(),
                keyManagement.importKeyFromFile(args[6]));
        System.exit(0);
    }

    private static String test0() {
        //Creating an Asset
        Info test = new Info();
        NonFungibleToken nonFungibleToken = new NonFungibleToken();
        nonFungibleToken.newNFT("Description","My first NFT in BigChainDB");
        nonFungibleToken.addImmutableInformation("Another Info", "This is Immutable");
        nonFungibleToken.addAdditionalInformation("CurrentOwner","Bob");

        BigchainDBDriver bigchaindb4Jason = new BigchainDBDriver();
        KeyManagement keyManagement = new KeyManagement();

        return bigchaindb4Jason.registerNFT(
                test.getDefaultServer(),
                test.getBobPrivateKey(),
                test.getBobPublicKey(),
                nonFungibleToken.toString()
                );
    }

    private static String test1(){
        String assetID = test0();
        //Transferring an Asset
        TransfAdditionalInfo transfAdditionalInfo = new TransfAdditionalInfo();
        transfAdditionalInfo.newTransfInfo("New Owner", "Alice");

        BigchainDBDriver bigchaindb4Jason = new BigchainDBDriver();
        Info test = new Info();

        return bigchaindb4Jason.transferNFT(
                test.getDefaultServer(),
                test.getBobPrivateKey(),
                test.getBobPublicKey(),
                assetID,
                transfAdditionalInfo.toString(),
                test.getAlicePublickey());
    }

    private static String test2() throws Exception {
        //Creating a fungible token
        Info driver = new Info();
        KeyManagement keyManagement = new KeyManagement();
        EdDSAPublicKey bankPublicKey = keyManagement.stringToEdDSAPublicKey(driver.getBobPublicKey(), "base58");
        EdDSAPrivateKey bankPrivateKey = keyManagement.stringToEdDSAPrivateKey(driver.getBobPrivateKey(), "base58");
        Vellus vellus = new Vellus();
        vellus.setConfig(driver.getDefaultServer());
        String idMoeda = vellus.createFungibleToken(bankPrivateKey,bankPublicKey,"FungibleToken", 100L);
        return  idMoeda;
    }

    private static String test3() throws Exception {
        String fungibleToken =  test2();
        Info driver = new Info();
        KeyManagement keyManagement = new KeyManagement();
        EdDSAPublicKey bankPublicKey = keyManagement.stringToEdDSAPublicKey(driver.getBobPublicKey(),"base58");
        EdDSAPrivateKey bankPrivateKey = keyManagement.stringToEdDSAPrivateKey(driver.getBobPrivateKey(),"base58");
        EdDSAPrivateKey  clientPrivateKey = keyManagement.stringToEdDSAPrivateKey(driver.getAlicePrivateKey(),"base58");
        EdDSAPublicKey  clientPublicKey = keyManagement.stringToEdDSAPublicKey(driver.getAlicePublickey(),"base58");
        Vellus vellus = new Vellus();
        vellus.setConfig(driver.getDefaultServer());

        //bank to client
        vellus.transferToken(bankPrivateKey,bankPublicKey,fungibleToken,clientPublicKey,1);
        vellus.transferToken(bankPrivateKey,bankPublicKey,fungibleToken,clientPublicKey,2);
        vellus.transferToken(bankPrivateKey,bankPublicKey,fungibleToken,clientPublicKey,3);
        vellus.transferToken(bankPrivateKey,bankPublicKey,fungibleToken,clientPublicKey,4);

        //client to bank
        vellus.transferToken(clientPrivateKey,clientPublicKey,fungibleToken,bankPublicKey,5);
        vellus.transferToken(clientPrivateKey,clientPublicKey,fungibleToken,bankPublicKey,5);


        return null;
    }

    private static ArrayList<WalletContent> test4() throws TransactionNotFoundException, IOException {

        Info driver = new Info();
        KeyManagement keyManagement = new KeyManagement();
        Wallet wallet = new Wallet();

        EdDSAPrivateKey bobPrivateKey = keyManagement.stringToEdDSAPrivateKey(driver.getBobPrivateKey(),"base58");
        EdDSAPublicKey bobPublicKey = keyManagement.stringToEdDSAPublicKey(driver.getBobPublicKey(),"base58");

        wallet.setConfig(driver.getDefaultServer());
        ArrayList<WalletContent> walletContents= wallet.getMyTokens(bobPrivateKey,bobPublicKey);

        for(int i =0; i<walletContents.size(); i++){
            System.out.println(walletContents.get(i).getToken()+
                    " "+walletContents.get(i).getAmount()+
                    " "+walletContents.get(i).getTransaction());
        }
        return walletContents;
    }
}