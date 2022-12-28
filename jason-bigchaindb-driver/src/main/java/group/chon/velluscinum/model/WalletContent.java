package group.chon.velluscinum.model;
public class WalletContent {
    private String token;
    private String transaction;
    private Long amount;
    public WalletContent(String token, String transaction, Long amount){
        this.token = token;
        this.transaction = transaction;
        this.amount = amount;
    }
    public String getToken() {
        return token;
    }
    public String getTransaction() {
        return transaction;
    }
    public Long getAmount() {
        return amount;
    }
}
