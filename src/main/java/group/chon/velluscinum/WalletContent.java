package group.chon.velluscinum;

/**
 * WalletContent Object
 *
 * @author Nilson Mori
 */
public class WalletContent {
    private String token;
    private String transaction;
    private Long amount;

    /**
     * WalletContent Object
     *
     * @param token Receives the ASSET-ID.
     * @param transaction Receives the TRANSACTION-ID.
     * @param amount Receives the amount about the Transaction.
     */
    public WalletContent(String token, String transaction, Long amount){
        this.token = token;
        this.transaction = transaction;
        this.amount = amount;
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
    public Long getAmount() {
        return amount;
    }
}
