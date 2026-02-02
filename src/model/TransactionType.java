package model;

public enum TransactionType {
    BUY("buy", 2),
    SELL("sell", 1),
    DEPOSIT("deposit", 1);

    private final String transaction;
    private final int numArgs;

    private TransactionType(String transaction, int numArgs) {
        this.transaction = transaction;
        this.numArgs = numArgs;
    }

    public String getTransaction() {
        return transaction;
    }

    public int getArgsCount() {
        return numArgs;
    }
}
