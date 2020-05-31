package net.scales.vas.models;

public class Transaction {

    private final String transactionId;

    public Transaction(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

}