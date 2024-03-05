package group.chon.velluscinum.model;

/**
 * WalletContent Object
 *
 * @author Nilson Mori
 */
public class WalletContent {
    private String token;
    private String transaction;
    private Long amount;
    private boolean nft;

    /**
     * WalletContent Object
     *
     * @param token Receives the ASSET-ID.
     * @param transaction Receives the TRANSACTION-ID.
     * @param amount Receives the amount about the Transaction.
     */
    public WalletContent(String token, String transaction, Long amount, boolean isNFT){
        this.token = token;
        this.transaction = transaction;
        this.amount = amount;
        this.nft = isNFT;
    }

    /**
     * WalletContent Token getter
     *
     * @return a ASSET-ID
     */
    public String getToken() {
        return token;
    }

    /**
     * WalletContent Transaction getter
     *
     * @return a TRANSACTION-ID
     */
    public String getTransaction() {
        return transaction;
    }

    /**
     *  WalletContent Amount getter
     *
     * @return the amount from the transaction.
     */
    public Long getAmount(){ return amount; }

    public String getAmountAsString(){
        if(isNft()){
            return "---";
        }
        return amount.toString();
    }

    public boolean isNft(){ return nft; }

    public String getType(){
        if(isNft()){
            return "nft";
        }else{
            return "token";
        }
    }
}
