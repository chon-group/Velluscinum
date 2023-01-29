package group.chon.velluscinum;

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
    public static final String DRIVERNAME = "[Velluscinum]";
    /**
     * Main Method is used to allow command-line interface (CLI) execution<br>
     *
     * @param args <b>buildAsset</b> - [FILENAME] [Immutable:Data] [Additional:Information]
     *             <br>&emsp;&emsp; <i>Generates a file with Data and MetaData about an ASSET</i>
     *             <br>&emsp;&emsp;&emsp; <i>[Immutable:Data]</i> is mandatory. This argument receives a Colon Separated String
     *             (e.g., "key0:value0:key1:value1:key2:value2 ... keyN:valueN").
     *             <br>&emsp;&emsp;&emsp; <i>[Additional:Information]</i> is mandatory. This argument receives a Colon Separated String
     *             (e.g., "key0:value0:key1:value1:key2:value2 ... keyN:valueN").
     * @param args <b>buildTransfer</b> - [FILENAME] [Transfer:Information]
     *             <br>&emsp;&emsp;&emsp; <i>[Transfer:Information]</i> is mandatory. This argument receives a Colon Separated String
     *             (e.g., "key0:value0:key1:value1:key2:value2 ... keyN:valueN").
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
     * @param args <b>transferNFT</b> - [SERVER] [PRIVATE-KEY-FILE] [PUBLIC-KEY-FILE] [ASSET-ID] [METADATA] [DEST-PUBLIC-KEY-FILE]
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
            if(op.equals("buildAsset"))         {buildAsset(args);      }
            else if(op.equals("buildTransfer")) {buildTransfer(args);   }
            else if(op.equals("buildWallet"))   {buildWallet(args);     }
            else if(op.equals("deployNFT"))     {deployNFT(args);       }
            else if(op.equals("deployToken"))   {deployToken(args);     }
            else if(op.equals("transferNFT"))   {transferNFT(args);     }
            else if(op.equals("transferToken")) {transferToken(args);   }
            else if(op.equals("walletBalance")) {walletBalance(args);   }
            else                                {showManpage();         }
        } catch (Exception ex) {
            showManpage();
        }
        System.exit(0);
    }

    /**
     * Generates a file with Data and MetaData about an ASSET
     *
     * @param args [FILENAME] [Immutable:Data] [Additional:Information]
     *             <br> Additional Information is optional.
     */
    private static void buildAsset(String[] args){
        Asset asset = new Asset();
        if(args[2].length()!=0){
            String immutableStr = args[2];
            String[] immutableArgs = immutableStr.split(":");
            if((immutableArgs.length%2)==0){
                asset.buildAsset(immutableArgs[0],immutableArgs[1]);
                if(immutableArgs.length>2){
                    for(int i=2; i< immutableArgs.length; i+=2){
                        asset.addImmutableInformation(immutableArgs[i],immutableArgs[i+1]);
                    }
                }
            }else{
                showManpage();
            }
        }else{
            showManpage();
        }

        if(args.length>3){
            String additionalStr = args[3];
            String[] additionalArgs = additionalStr.split(":");
            if((additionalArgs.length%2)==0){
                for(int i=0; i<additionalArgs.length; i+=2){
                    asset.addAdditionalInformation(additionalArgs[i],additionalArgs[i+1]);
                }
            }else{
                showManpage();
            }
        }
        asset.assetToFile(args[1]+".asset");
    }

    private static void buildTransfer(String[] args){
        Asset transferInfo = new Asset();
        if(args[2].length()!=0){
            String transferInfoStr = args[2];
            String[] transferInfoArgs = transferInfoStr.split(":");
            if((transferInfoArgs.length%2)==0){
                transferInfo.buildTransfer(transferInfoArgs[0],
                                            transferInfoArgs[1]);
                if(transferInfoArgs.length>2){
                    for(int i=2; i< transferInfoArgs.length; i+=2){
                        transferInfo.addTransferAdditionalInformation(
                                transferInfoArgs[i],
                                transferInfoArgs[i+1]);
                    }
                }
            }else{
                showManpage();
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
    private static void buildWallet(String[] args){
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
     * Deploys a Non-Fungible-Token
     *
     * @param args [PRIVATE-KEY-FILE] [PUBLIC-KEY-FILE] [NFT-FILE]
     */
    private static void deployNFT(String[] args) {
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
    private static void deployToken(String[] args){
        BigchainDBDriver bigchainDBDriver = new BigchainDBDriver();
        KeyManagement keyManagement = new KeyManagement();
        Asset asset = new Asset();
        String strPrivateKey = keyManagement.importKeyFromFile(args[2]);
        //[SERVER] [PRIVATE-KEY-FILE] [PUBLIC-KEY-FILE] [TOKEN-FILE] [AMOUNT]
        bigchainDBDriver.deployToken(
                args[1],
                keyManagement.importKeyFromFile(args[2]),
                keyManagement.importKeyFromFile(args[3]),
                asset.importTransferFromFile(args[4]).toString(),
                args[5]
        );
    }

    /**
     * Transfers a Non-Fungible-Token
     *
     * @param args [SERVER] [PRIVATE-KEY-FILE] [PUBLIC-KEY-FILE] [ASSET-ID] [METADATA] [DEST-PUBLIC-KEY-FILE]
     */
    private static void transferNFT(String[] args){
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
    private static void transferToken(String[] args){
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
     * List the Assets in the Wallet
     * @param args [SERVER] [PRIVATE-KEY-FILE] [PUBLIC-KEY-FILE]
     */
    private static void walletBalance(String[] args){
        Wallet wallet = new Wallet();
        KeyManagement keyManagement = new KeyManagement();
        ArrayList<WalletContent> walletContents = wallet.getMyTokens(
                args[1],
                keyManagement.importKeyFromFile(args[2]),
                keyManagement.importKeyFromFile(args[3])
        );

        System.out.println("Wallet Balance of: "+keyManagement.importKeyFromFile(args[2]));
        System.out.println(" QTD \t Asset");
        for(int i=0; i<walletContents.size(); i++){
            System.out.println(walletContents.get(i).getAmount()+" \t "+walletContents.get(i).getToken());
        }

    }

    /**
     * Shows the ManPage of Velluscinum
     */
    private static void showManpage(){
        InputStream inputStream = Main.class.getResourceAsStream("/manpage.txt");
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