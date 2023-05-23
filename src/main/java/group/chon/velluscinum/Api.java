package group.chon.velluscinum;

import com.bigchaindb.api.TransactionsApi;
import com.bigchaindb.model.Transaction;
import jason.asSyntax.Literal;

import java.io.*;
import java.util.ArrayList;

/**
 * BigChainDB Command-line interface (CLI) and API.
 *
 * @author Nilson Mori
 *
 */
public class Api {
    public static final String DRIVERNAME = "[Velluscinum]";

    /**
     * Main Method is used to allow command-line interface (CLI) execution<br>
     *
     * @param args <b>buildAsset</b> - [FILENAME] [Immutable:Data] [Additional:Information]
     *             <br>&emsp;&emsp; <i>Generates a file with Data and MetaData about an ASSET</i>
     *             <br>&emsp;&emsp;&emsp; <i>[Immutable:Data]</i> is mandatory. This argument receives a Colon and Semicolon Separated String
     *             (e.g., "key0:value0;key1:value1;key2:value2; ... ;keyN:valueN").
     *             <br>&emsp;&emsp;&emsp; <i>[Additional:Information]</i> is mandatory. This argument receives a Colon and Semicolon Separated String
     *             (e.g., "key0:value0;key1:value1;key2:value2; ... ;keyN:valueN").
     * @param args <b>buildTransfer</b> - [FILENAME] [Transfer:Information]
     *             <br>&emsp;&emsp;&emsp; <i>[Transfer:Information]</i> is mandatory. This argument receives a Colon and Semicolon Separated String
     *             (e.g., "key0:value0;key1:value1;key2:value2; ... ;keyN:valueN").
     * @param args <b>buildWallet</b> [FILENAME]
     *             <br>&emsp;&emsp; <i>Creates two files with an ECDSA keyset in Base58</i>.
     *             <br>
     *             <br>
     * @param args <b>deployNFT</b> [SERVER] [PRIVATE-KEY-FILE] [PUBLIC-KEY-FILE] [NFT-FILE]
     *             <br>&emsp;&emsp; <i>Used to CREATE a Non-Fungible-Token</i>
     * @param args <b>deployToken</b> [SERVER] [PRIVATE-KEY-FILE] [PUBLIC-KEY-FILE] [TOKEN-FILE] [AMOUNT]
     *             <br>&emsp;&emsp; <i>Used to CREATE a Token</i>
     *             <br>
     *             <br>
     * @param args <b>transferNFT</b> - [SERVER] [PRIVATE-KEY-FILE] [PUBLIC-KEY-FILE] [ASSET-ID] [METADATA-FILE] [DEST-PUBLIC-KEY-FILE]
     *             <br> <i>Used to TRANSFER a Non-Fungible-Token</i>
     * @param args <b>transferToken</b> - [SERVER] [PRIVATE-KEY-FILE] [PUBLIC-KEY-FILE] [ASSET-ID] [DEST-PUBLIC-KEY-FILE] [AMOUNT]
     *             <br>&emsp;&emsp; Transfer an amount of Token.
     *             <br>
     *             <br>
     * @param args <b>walletBalance</b> - [SERVER] [PRIVATE-KEY-FILE] [PUBLIC-KEY-FILE]
     *             <br>&emsp;&emsp; Shows the list of Assets in the Wallet.
     */
    public static void main(String[] args) {
        String op = null;
        try {
            op = args[0];
            if(op.equals("buildAsset")) buildAsset_CLI(args);
            else if(op.equals("buildTransfer")) buildTransfer(args);
            else if(op.equals("buildWallet")) buildWallet_CLI(args);
            else if(op.equals("deployNFT")) deployNFT_CLI(args);
            else if(op.equals("deployToken")) deployToken_CLI(args);
            else if(op.equals("transferNFT")) transferNFT_CLI(args);
            else if(op.equals("transferToken")) transferToken_CLI(args);
            else if(op.equals("walletBalance")) walletBalance_CLI(args);
            else showManpage();
        } catch (Exception ex) {
            showManpage();
        }
        System.exit(0);
    }


    /**
     * Deploys a Token in the BigChainDB.
     *
     * @param url Receives a <b>URL:PORT</b> Address from a bigchainDB Server
     *             <br>&emsp;&emsp; (e.g., "http://testchain.chon.group:9984").
     *
     * @param privateKey Receives a Base58 format <b>Private Key</b> from the wallet in the BigChainDB Network
     *                <br>&emsp;&emsp; (e.g., "A4BAzNZdGBqkGHQP7gWovcYaGP3UELL6f4THhuHSQHCK").
     *
     * @param publicKey Receives a Base58 format <b>Public Key</b> from the wallet in the BigChainDB Network
     *                        <br>&emsp;&emsp; (e.g., "arDLZkDEmZi5wWtebvZrC5KgSs2tX8ULLeBSFp8dTJR").
     *
     * @param asset Receives a Colon and Semicolon Separated String Immutable Information about the ASSET
     *      <br>(e.g., "key0:value0;key1:value1;key2:value2; ... ;keyN:valueN").
     *
     * @param amount Receives the Amount of the Token (maximum value is 9×10¹⁸).
     *
     * @return Return the <b>ASSET-ID</b> case every crypto-conditions are fulfilled in the transaction at BigChainDB Network
     *       <br>&emsp;&emsp; (e.g, "9019f938c175ad768f76b3ce4842a066a99198f68dc3078b0fab58349ece5896").
     */
    public String deploy(String url,
                                String privateKey,
                                String publicKey,
                                String asset,
                             //   String metadata,
                                Long amount
    ){
        Asset token = new Asset();
        String[] pairKeyValueString = asset.split(";");
        for(int i=0; i< pairKeyValueString.length;i++){
            String[] key_value=pairKeyValueString[i].split(":");
            if(key_value.length==2){
                if(i==0){
                    token.buildAsset(key_value[0],key_value[1]);
                }else{
                    token.addImmutableInformation(key_value[0],key_value[1]);
                }
            }
        }

        BigchainDBDriver bigchainDBDriver = new BigchainDBDriver();
        return bigchainDBDriver.deployToken(
                url,
                privateKey,
                publicKey,
                token.assetToString(),
                amount
        );
    }

    /**
     *  Deploys a NFT in the BigChainBD Network
     *
     * @param url Receives a <b>URL:PORT</b> Address from a bigchainDB Server
     *             <br>&emsp;&emsp; (e.g., "http://testchain.chon.group:9984").
     *
     * @param privateKey Receives a Base58 format <b>Private Key</b> from the wallet in the BigChainDB Network
     *                <br>&emsp;&emsp; (e.g., "A4BAzNZdGBqkGHQP7gWovcYaGP3UELL6f4THhuHSQHCK").
     *
     * @param publicKey Receives a Base58 format <b>Public Key</b> from the wallet in the BigChainDB Network
     *                        <br>&emsp;&emsp; (e.g., "arDLZkDEmZi5wWtebvZrC5KgSs2tX8ULLeBSFp8dTJR").
     *
     * @param asset Receives a Colon and Semicolon Separated String Immutable Information about the ASSET
     *      <br>(e.g., "key0:value0;key1:value1;key2:value2; ... ;keyN:valueN").
     *
     * @param metadata Receives a Colon and Semicolon Separated String from Metadata Information about the ASSET
     *      <br>(e.g., "key0:value0;key1:value1;key2:value2; ... ;keyN:valueN").
     *
     * @return Return the <b>ASSET-ID</b> case every crypto-conditions are fulfilled in the transaction at BigChainDB Network
     *       <br>&emsp;&emsp; (e.g, "9019f938c175ad768f76b3ce4842a066a99198f68dc3078b0fab58349ece5896").
     */
    public String deploy(String url,
                                String privateKey,
                                String publicKey,
                                String asset,
                                String metadata){

        Asset NFT = new Asset();
        String[] pairKeyValueString = asset.split(";");
        for(int i=0; i< pairKeyValueString.length;i++){
            String[] key_value=pairKeyValueString[i].split(":");
            if(key_value.length==2){
                if(i==0){
                    NFT.buildAsset(key_value[0],key_value[1]);
                }else{
                    NFT.addImmutableInformation(key_value[0],key_value[1]);
                }
            }
        }

        String[] pairKeyValueAdditionalArgs = metadata.split(";");
        for (int i=0; i< pairKeyValueAdditionalArgs.length; i++){
            String[] key_value = pairKeyValueAdditionalArgs[i].split(":");
            if(key_value.length==2){
                NFT.addAdditionalInformation(key_value[0],key_value[1]);
            }
        }

        BigchainDBDriver bigchainDBDriver = new BigchainDBDriver();
        return bigchainDBDriver.deployNFT(url,
                privateKey,
                publicKey,
                NFT.assetToString());
    }

    /**
     * Transfer a NFT in the BigChainDB Network.
     *
     * @param url Receives a <b>URL:PORT</b> Address from a bigchainDB Server
     *             <br>&emsp;&emsp; (e.g., "http://testchain.chon.group:9984").
     *
     * @param senderPrivateKey Receives a Base58 format <b>Private Key</b> from the sender wallet in the BigChainDB Network
     *                <br>&emsp;&emsp; (e.g., "A4BAzNZdGBqkGHQP7gWovcYaGP3UELL6f4THhuHSQHCK").
     *
     * @param senderPublicKey Receives a Base58 format <b>Public Key</b> from the sender wallet in the BigChainDB Network
     *                        <br>&emsp;&emsp; (e.g., "arDLZkDEmZi5wWtebvZrC5KgSs2tX8ULLeBSFp8dTJR").
     *
     * @param assetID Receives the <b>ASSET-ID</b> from the Asset that wants to transfer to another wallet
     *                  <br>&emsp;&emsp; (e.g., "095ee8b3a8599fabe61a09c1c046df7b8af3cb4f70b815222daad132988bd67d").
     *
     * @param recipientPublicKey Receives a Base58 format <b>Public Key</b> from the receiver wallet in the BigChainDB Network
     *          <br>&emsp;&emsp; (e.g, "FNJPJdtuPQYsqHG6tuUjKjqv7SW84U4ipiyyLV2j6MEW").
     *
     * @param metadata Receives a Colon and Semicolon Separated String from Metadata Information about the ASSET
     *          <br>&emsp;&emsp; (e.g., "key0:value0;key1:value1;key2:value2; ... ;keyN:valueN").
     *
     * @return Return the <b>TRANSFER-ID</b> case every crypto-conditions are fulfilled in the transaction at BigChainDB Network
     *              <br>&emsp;&emsp; (e.g, "9019f938c175ad768f76b3ce4842a066a99198f68dc3078b0fab58349ece5896").
     */
    public String transfer(String url,
                                  String senderPrivateKey,
                                  String senderPublicKey,
                                  String assetID,
                                  String recipientPublicKey,
                                  String metadata
                                       ){

        Asset transferInfo = new Asset();

        String[] pairKeyValueTransfer = metadata.split(";");
        for(int i=0; i< pairKeyValueTransfer.length; i++){
            String[] key_value = pairKeyValueTransfer[i].split(":");
            if(key_value.length==2){
                if(i==0){
                    transferInfo.buildTransfer(key_value[0],key_value[1]);
                }else{
                    transferInfo.addTransferInformation(key_value[0],key_value[1]);
                }
            }else{
                showManpage();
            }
        }

        BigchainDBDriver bigchainDBDriver = new BigchainDBDriver();
        return bigchainDBDriver.transferNFT(
                url,
                senderPrivateKey,
                senderPublicKey,
                assetID,
                transferInfo.transferToString(),
                recipientPublicKey);
    }

    /**
     *  Transfers a token in the BigChainDB Network.
     *
     * @param url Receives a <b>URL:PORT</b> Address from a bigchainDB Server
     *               <br>&emsp;&emsp; (e.g., "http://testchain.chon.group:9984")
     * @param senderPrivateKey Receives a Base58 format <b>Private Key</b> from the sender wallet in the BigChainDB Network
     *                            <br>&emsp;&emsp; (e.g., "A4BAzNZdGBqkGHQP7gWovcYaGP3UELL6f4THhuHSQHCK").
     * @param senderPublicKey Receives a Base58 format <b>Public Key</b> from the sender wallet in the BigChainDB Network
     *                           <br>&emsp;&emsp; (e.g., "arDLZkDEmZi5wWtebvZrC5KgSs2tX8ULLeBSFp8dTJR").
     * @param assetID    Receives the <b>ASSET-ID</b> from the token that wants to transfer to another wallet
     *                  <br>&emsp;&emsp; (e.g., "095ee8b3a8599fabe61a09c1c046df7b8af3cb4f70b815222daad132988bd67d").
     * @param recipientPublicKey    Receives a Base58 format <b>Public Key</b> from the receiver wallet in the BigChainDB Network
     *                              <br>&emsp;&emsp; (e.g, "FNJPJdtuPQYsqHG6tuUjKjqv7SW84U4ipiyyLV2j6MEW").
     * @param amount Receives the number of tokens that will transfer.
     *
     * @return Return the <b>TRANSFER-ID</b> case every crypto-conditions are fulfilled in the transaction at BigChainDB Network
     *          <br>&emsp;&emsp; (e.g, "9019f938c175ad768f76b3ce4842a066a99198f68dc3078b0fab58349ece5896").
     */
    public String transfer(String url,
                                  String senderPrivateKey,
                                  String senderPublicKey,
                                  String assetID,
                                  String recipientPublicKey,
                                  Integer amount){
        BigchainDBDriver bigchainDBDriver = new BigchainDBDriver();
        return bigchainDBDriver.transferToken(url,senderPrivateKey,senderPublicKey,assetID,recipientPublicKey,amount);
    }

    /**
     * Stamps a transaction
     * @param url   Receives a <b>URL:PORT</b> Address from a bigchainDB Server
     *               <br>&emsp;&emsp; (e.g., "http://testchain.chon.group:9984")
     * @param privateKey Receives a Base58 format <b>Private Key</b> from the sender wallet in the BigChainDB Network
     *              <br>&emsp;&emsp; (e.g., "A4BAzNZdGBqkGHQP7gWovcYaGP3UELL6f4THhuHSQHCK").
     * @param publicKey Receives a Base58 format <b>Public Key</b> from the sender wallet in the BigChainDB Network
     *              <br>&emsp;&emsp; (e.g., "arDLZkDEmZi5wWtebvZrC5KgSs2tX8ULLeBSFp8dTJR").
     * @param transactionID    Receives the <b>TRANSFER-ID</b> from the token that wants to transfer to another wallet
     *              <br>&emsp;&emsp; (e.g., "095ee8b3a8599fabe61a09c1c046df7b8af3cb4f70b815222daad132988bd67d").
     *
     * @return Not null if the transaction was successfully stamped.
     */
    public String stampTransaction(String url,
                                   String privateKey,
                                   String publicKey,
                                   String transactionID){
        BigchainDBDriver bigchainDBDriver = new BigchainDBDriver();
        try{
            Transaction transaction = TransactionsApi.getTransactionById(transactionID);
            return bigchainDBDriver.stampTransaction(url,privateKey,publicKey,transaction);
           /* if(bigchainDBDriver.stampTransaction(url,privateKey,publicKey,transaction)!=null){
                return true;
            }else{
                return false;
            }*/
        }catch (Exception ex){
            System.out.println(ex);
            return null;
        }



    }



    /**
     * Lists content of the Wallet
     *
     * @param url Receives a <b>URL:PORT</b> Address from a bigchainDB Server
     *               <br>&emsp;&emsp; (e.g., "http://testchain.chon.group:9984")
     * @param privateKey Receives a Base58 format <b>Private Key</b> from the wallet in the BigChainDB Network
     *                            <br>&emsp;&emsp; (e.g., "A4BAzNZdGBqkGHQP7gWovcYaGP3UELL6f4THhuHSQHCK").
     * @param publicKey Receives a Base58 format <b>Public Key</b> from the wallet in the BigChainDB Network
     *                           <br>&emsp;&emsp; (e.g., "arDLZkDEmZi5wWtebvZrC5KgSs2tX8ULLeBSFp8dTJR").
     *
     * @return a ArrayList of WalletContent.
     *  @see WalletContent
     *
     */
    public ArrayList<WalletContent> walletBalance(String url,
                                                         String privateKey,
                                                         String publicKey){
        Wallet wallet = new Wallet();
        ArrayList<WalletContent> result = wallet.getMyTokens(url,privateKey,publicKey);
        return result;
    }



    /**
     * Generates a file with Data and MetaData about an ASSET
     *
     * @param args [FILENAME] [Immutable:Data] [Additional:Information]
     *             <br> Additional Information is optional.
     */
    private static void buildAsset_CLI(String[] args){
        Asset asset = new Asset();
        if(args[2].length()!=0){
            String immutableStr = args[2];
            String[] pairKeyValueString = immutableStr.split(";");
            for(int i=0; i< pairKeyValueString.length;i++){
                String[] key_value=pairKeyValueString[i].split(":");
                if(key_value.length==2){
                    if(i==0){
                        asset.buildAsset(key_value[0],key_value[1]);
                    }else{
                        asset.addImmutableInformation(key_value[0],key_value[1]);
                    }
                }else{
                    showManpage();
                }
            }
        }else{
            showManpage();
        }

        if(args.length>3){
            String additionalStr = args[3];
            String[] pairKeyValueAdditionalArgs = additionalStr.split(";");
            for (int i=0; i< pairKeyValueAdditionalArgs.length; i++){
                String[] key_value = pairKeyValueAdditionalArgs[i].split(":");
                if(key_value.length==2){
                        asset.addAdditionalInformation(key_value[0],key_value[1]);
                }else{
                    showManpage();
                }
            }
        }
        asset.assetToFile(args[1]+".asset");
    }

    /**
     * Generates a file with Metadata about the TRANSFER
     *
     * @param args [FILENAME] [Additional:Information]
     */
    private static void buildTransfer(String[] args){
        Asset transferInfo = new Asset();
        if(args[2].length()!=0){
            String transferInfoStr = args[2];
            String[] pairKeyValueTransfer = transferInfoStr.split(";");
            for(int i=0; i< pairKeyValueTransfer.length; i++){
                String[] key_value = pairKeyValueTransfer[i].split(":");
                if(key_value.length==2){
                    if(i==0){
                        transferInfo.buildTransfer(key_value[0],key_value[1]);
                    }else{
                        transferInfo.addTransferInformation(key_value[0],key_value[1]);
                    }
                }else{
                    showManpage();
                }
            }
        }else{
            showManpage();
        }
        transferInfo.transferToFile(args[1]+".transfer");
    }
    /**
     * Creates two files with an ECDSA keyset in Base58
     *
     * @param args [FILENAME]
     */
    private static void buildWallet_CLI(String[] args){
        if(args[1].length()!=0){
            KeyManagement keyManagement = new KeyManagement();
            String[] strKeyPair = keyManagement.newKeyPair("base58");
            keyManagement.keyToFile(strKeyPair[0],args[1]+".privkey");
            keyManagement.keyToFile(strKeyPair[1],args[1]+".publkey");
        }else{
            showManpage();
        }

    }

    /**
     *  Creates a ECDSA Pair Keys in String Base58.
     *
     * @return
     *      <br>&bull; The first element (return[0]) is the Private Key in the format defined in base parameter.
     *      <br>&bull; The second element (return[1]) is the Public Key in the format defined in base parameter.
     */
    public static String[] buildWallet(){
        KeyManagement keyManagement = new KeyManagement();
        return keyManagement.newKeyPair("base58");
    }

    /**
     * Deploys a Non-Fungible-Token
     *
     * @param args [PRIVATE-KEY-FILE] [PUBLIC-KEY-FILE] [NFT-FILE]
     */
    private static void deployNFT_CLI(String[] args) {
        BigchainDBDriver bigchainDBDriver = new BigchainDBDriver();
        KeyManagement keyManagement = new KeyManagement();
        Asset asset = new Asset();
        String strPrivateKey = keyManagement.importKeyFromFile(args[2]);
        //[SERVER] [PRIVATEKEY-FILE] [PUBLICKEY-FILE] [NFT-FILE]
        bigchainDBDriver.deployNFT(
                args[1],
                keyManagement.importKeyFromFile(args[2]),
                keyManagement.importKeyFromFile(args[3]),
                asset.importAssetFromFile(args[4]).toString());
    }

    /**
     * Creates a Token
     *
     * @param args [SERVER] [PRIVATE-KEY-FILE] [PUBLIC-KEY-FILE] [TOKEN-FILE] [AMOUNT]
     */
    private static void deployToken_CLI(String[] args){
        BigchainDBDriver bigchainDBDriver = new BigchainDBDriver();
        KeyManagement keyManagement = new KeyManagement();
        Asset asset = new Asset();
        String strPrivateKey = keyManagement.importKeyFromFile(args[2]);
        //[SERVER] [PRIVATE-KEY-FILE] [PUBLIC-KEY-FILE] [TOKEN-FILE] [AMOUNT]
        String tokenId = bigchainDBDriver.deployToken(
                args[1],
                keyManagement.importKeyFromFile(args[2]),
                keyManagement.importKeyFromFile(args[3]),
                asset.importTransferFromFile(args[4]).toString(),
                Long.parseLong(args[5])
        );
        keyManagement.keyToFile(tokenId,args[4]+"Id");
    }

    /**
     * Transfers a Non-Fungible-Token
     *
     * @param args [SERVER] [PRIVATE-KEY-FILE] [PUBLIC-KEY-FILE] [ASSET-ID] [METADATA] [DEST-PUBLIC-KEY-FILE]
     */
    private static void transferNFT_CLI(String[] args){
        BigchainDBDriver bigchainDBDriver = new BigchainDBDriver();
        Asset transferAsset = new Asset();
        KeyManagement keyManagement = new KeyManagement();
        //[SERVER] [PRIVATEKEY-FILE] [PUBLICKEY-FILE] [ASSET-ID] [METADATA] [PUBLIC_DEST_KEY-FILE]
        bigchainDBDriver.transferNFT(
                args[1],
                keyManagement.importKeyFromFile(args[2]),
                keyManagement.importKeyFromFile(args[3]),
                args[4],
                transferAsset.importTransferFromFile(args[5]).toString(),
                keyManagement.importKeyFromFile(args[6]));
    }

    /**
     * Transfer an amount of Token
     *
     * @param args [SERVER] [PRIVATE-KEY-FILE] [PUBLIC-KEY-FILE] [ASSET-ID] [DEST-PUBLIC-KEY-FILE] [AMOUNT]
     */
    private static void transferToken_CLI(String[] args){
        BigchainDBDriver bigchainDBDriver = new BigchainDBDriver();
        Asset transferAsset = new Asset();
        KeyManagement keyManagement = new KeyManagement();
        //[SERVER] [PRIVATE-KEY-FILE] [PUBLIC-KEY-FILE] [ASSET-ID] [DEST-PUBLIC-KEY-FILE] [AMOUNT]
        bigchainDBDriver.transferToken(
                args[1],
                keyManagement.importKeyFromFile(args[2]),
                keyManagement.importKeyFromFile(args[3]),
                args[4],
                keyManagement.importKeyFromFile(args[5]),
                Integer.parseInt(args[6]));
    }


    /**
     * Wallet balance via command-line interface
     *
     * @param args [SERVER] [PRIVATE-KEY-FILE] [PUBLIC-KEY-FILE]
     */
    private static void walletBalance_CLI(String[] args){
        KeyManagement keyManagement = new KeyManagement();

        Wallet wallet = new Wallet();
        ArrayList<WalletContent> walletContents = wallet.getMyTokens(
                args[1],
                keyManagement.importKeyFromFile(args[2]),
                keyManagement.importKeyFromFile(args[3]));

        System.out.println("Wallet Balance of: "+keyManagement.importKeyFromFile(args[3]));
        System.out.println(" QTD \t Asset");
        for(int i=0; i<walletContents.size(); i++){
            System.out.println(walletContents.get(i).getAmount()+" \t "+walletContents.get(i).getToken());
        }
    }

    /**
     * Shows the ManPage of Velluscinum
     */
    private static void showManpage(){
        InputStream inputStream = Api.class.getResourceAsStream("/manpage.txt");
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
        System.exit(1);
    }

}