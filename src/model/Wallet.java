package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Wallet {
    private double deposit;
    private final List<Transaction> history;

    public Wallet() {
        this.deposit = 0.0;
        this.history = new ArrayList<>();
    }

    public void addTransaction(Transaction transaction) {
        history.add(transaction);
    }

    public List<Transaction> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public double getDeposit() {
        return deposit;
    }

    // synchronize
    public void deposit(double moneyToAdd) {
        deposit += moneyToAdd;
    }
}
