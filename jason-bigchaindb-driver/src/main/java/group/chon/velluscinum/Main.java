package group.chon.velluscinum;

import br.pro.turing.alfa.Test;
import br.pro.turing.alfa.Vellus;
import br.pro.turing.alfa.Wallet;
import br.pro.turing.alfa.WalletContent;
import com.bigchaindb.exceptions.TransactionNotFoundException;
import net.i2p.crypto.eddsa.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * BigChainDB Command-line interface (CLI).
 *
 * @author Nilson Mori
 *
 */
public class Main {
    /**
     * Main Method is used to allow command-line interface (CLI) execution<br>
     *
     * @param args  - Receive the following arguments sent through the command-line interface (CLI): <br>
     *       <ul>
     *         <li><b>createKeys</b> - <i>Used to create a keyPair</i>
     *              <br>&emsp;java -jar jason-bigchaindb-driver.jar createKeys [FILENAME]
     *                  <br>&emsp;&emsp; <i>example:</i>
     *                      <br>&emsp;&emsp;&emsp; java -jar jason-bigchaindb-driver.jar createKeys bob
     *                  <br>
     *                  <br>
     *         </li>
     *         <li><b>newNFT</b> - <i>Used to CREATE a Non-Fungible-Token</i>
     *              <br>&emsp; java -jar jason-bigchaindb-driver.jar newNFT [SERVER] [PRIVATE-KEY-FILE] [PUBLIC-KEY-FILE] [NFT-FILE]
     *                  <br>&emsp;&emsp; <i>example:</i>
     * 	                    <br>&emsp;&emsp;&emsp; java -jar jason-bigchaindb-driver.jar newNFT "<a href=http://testchain.chon.group target=_blank>http://testchain.chon.group:9984</a>" bob.private.key bob.public.key nft.json
     * 	            <br>
     * 	            <br>
     *         </li>
     *         <li><b>transferNFT</b> - <i>Used to TRANSFER a Non-Fungible-Token</i>
     *              <br>&emsp; java -jar jason-bigchaindb-driver.jar transferNFT [SERVER] [PRIVATE-KEY-FILE] [PUBLIC-KEY-FILE] [ASSET-ID] [METADATA] [DEST-PUBLIC-KEY-FILE]
     *                  <br>&emsp;&emsp; <i>example:</i>
     * 	                    <br>&emsp;&emsp;&emsp; java -jar jason-bigchaindb-driver.jar transferNFT "<a href=http://testchain.chon.group target=_blank>http://testchain.chon.group:9984</a>" bob.private.key bob.public.key "618b478c4e7941361b64a040e9bff271f6b543fe1896cd008c7369ee70489d2d" metadata.json alice.public.key
     * 	            <br>
     * 	            <br>
     *         </li>
     *       </ul>
     */
    public static void main(String[] args){
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
            String line = null;
            try {
                line = buffered.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            while(line != null){
                System.out.println(line);
                try {
                    line = buffered.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                buffered.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.exit(0);
        }
    }

    private static void createKeys(String[] args){
        String output = null;
        KeyManagement keyManagement = new KeyManagement();
        String[] strKeyPair = keyManagement.newKeyPair("base58");
        keyManagement.keyToFile(strKeyPair[0],args[1]+".private.key");
        keyManagement.keyToFile(strKeyPair[1],args[1]+".public.key");
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
        TransferAdditionalInfo transferAdditionalInfo = new TransferAdditionalInfo();
        KeyManagement keyManagement = new KeyManagement();
        //[SERVER] [PRIVATEKEY-FILE] [PUBLICKEY-FILE] [ASSET-ID] [METADATA] [PUBLIC_DEST_KEY-FILE]
        bigchainDBDriver.transferNFT(
                args[1],
                keyManagement.importKeyFromFile(args[2]),
                keyManagement.importKeyFromFile(args[3]),
                args[4],
                transferAdditionalInfo.importFromFile(args[5]).toString(),
                keyManagement.importKeyFromFile(args[6]));
        System.exit(0);
    }

    private static String test0() {
        //Creating an Asset
        Test test = new Test();
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
        TransferAdditionalInfo transferAdditionalInfo = new TransferAdditionalInfo();
        transferAdditionalInfo.newTransferInfo("New Owner", "Alice");

        BigchainDBDriver bigchaindb4Jason = new BigchainDBDriver();
        Test test = new Test();

        return bigchaindb4Jason.transferNFT(
                test.getDefaultServer(),
                test.getBobPrivateKey(),
                test.getBobPublicKey(),
                assetID,
                transferAdditionalInfo.toString(),
                test.getAlicePublickey());
    }

    private static String test2() throws Exception {
        //Creating a fungible token
        Test driver = new Test();
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
        Test driver = new Test();
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

        Test driver = new Test();
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