package model;

import model.enums.TransactionType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wallet {
    private static final double MINIMUM_AMOUNT = 0.0;
    private static final double EPSILON = 0.00000001;
    private static final int TO_PERCENTAGE = 100;
    private static final String DOLLAR_SIGN = "$";

    private static final String WALLET_SUMMARY_MESSAGE = "WALLET SUMMARY:" + System.lineSeparator();
    private static final String WALLET_OVERALL_SUMMARY_MESSAGE = "WALLET OVERALL SUMMARY:" + System.lineSeparator();

    private static final String BALANCE_MESSAGE = "BALANCE:" + System.lineSeparator();
    private static final String TRANSACTION_MESSAGE = "TRANSACTIONS:" + System.lineSeparator();

    private double balanceUsd;
    private final Map<String, Double> assets;
    private final List<Transaction> transactionHistory;

    public Wallet() {
        this.balanceUsd = MINIMUM_AMOUNT;
        this.assets = new HashMap<>();
        this.transactionHistory = new ArrayList<>();
    }

    public Wallet(Wallet other) {
        validateWallet(other);

        this.balanceUsd = other.getBalanceUsd();
        this.assets = other.getAssets();
        this.transactionHistory = other.getTransactionHistory();
    }

    public synchronized void deposit(double amount) {
        validatePositive(amount);
        balanceUsd += amount;
        transactionHistory.add(new Transaction("USD", 1.0, amount,
                TransactionType.DEPOSIT, LocalDateTime.now()));
    }

    public synchronized boolean buy(String assetId, double currentPrice, double amountUsd) {
        validateAssetId(assetId);
        validatePositive(currentPrice);
        validatePositive(amountUsd);

        if (amountUsd > balanceUsd) {
            return false;
        }

        double quantity = amountUsd / currentPrice;

        if (quantity < EPSILON) {
            return false;
        }

        Double existingQuantity = assets.get(assetId);

        if (existingQuantity == null) {
            assets.put(assetId, quantity);
        } else {
            assets.put(assetId, existingQuantity + quantity);
        }

        balanceUsd -= amountUsd;

        transactionHistory.add(new Transaction(assetId, currentPrice, quantity,
                TransactionType.BUY, LocalDateTime.now()));

        return true;
    }

    public synchronized boolean sell(String assetId, double currentPrice) {
        validateAssetId(assetId);
        validatePositive(currentPrice);

        Double quantity = assets.get(assetId);

        if (quantity == null || quantity < EPSILON) {
            return false;
        }

        double amountReceived = quantity * currentPrice;
        assets.remove(assetId);
        balanceUsd += amountReceived;

        transactionHistory.add(new Transaction(assetId, currentPrice, quantity,
                TransactionType.SELL, LocalDateTime.now()));

        return true;
    }

    public synchronized String getWalletSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(WALLET_SUMMARY_MESSAGE)
                .append(BALANCE_MESSAGE)
                .append(DOLLAR_SIGN)
                .append(balanceUsd)
                .append(System.lineSeparator());

        summary.append(TRANSACTION_MESSAGE).append(System.lineSeparator());

        for (Transaction transaction : transactionHistory) {
            summary.append(transaction.toString()).append(System.lineSeparator());
        }

        return summary.toString();
    }

    public synchronized String getWalletOverallSummary(Map<String, Double> currentPrices) {
        validateCurrentPrices(currentPrices);

        double totalInvested = calculateTotalInvested();
        double currentValue = calculateCurrentValue(currentPrices);
        double profit = currentValue - totalInvested;
        double returnPercentage = totalInvested > EPSILON ? (profit / totalInvested) * TO_PERCENTAGE : MINIMUM_AMOUNT;

        return buildString(currentPrices, totalInvested, currentValue, profit, returnPercentage);
    }

    private String buildString(Map<String, Double> currentPrices, double totalInvested,
                               double currentValue, double profit, double returnPercentage) {
        StringBuilder summary = new StringBuilder();
        summary.append(WALLET_OVERALL_SUMMARY_MESSAGE)
                .append(String.format("Total Invested: $%.2f%n", totalInvested))
                .append(String.format("Current Value: $%.2f%n", currentValue))
                .append(String.format("Profit/Loss: $%.2f%n", profit))
                .append(String.format("Return percentage: %.2f%n", returnPercentage))
                .append(System.lineSeparator());

        if (!assets.isEmpty()) {
            summary.append("Performance by Asset:%n".formatted());
            assets.forEach((assetId, quantity) -> {
                Double currentPrice = currentPrices.get(assetId);
                if (currentPrice != null) {
                    double assetProfit = calculateAssetProfit(assetId, currentPrice);
                    double assetInvested = getAssetInvested(assetId);
                    double assetReturn = assetInvested > EPSILON ? (assetProfit / assetInvested)
                            * TO_PERCENTAGE : MINIMUM_AMOUNT;

                    summary.append(String.format("  %s: Profit $%.2f PERCENTAGE PROFIT: %.2f%n",
                            assetId, assetProfit, assetReturn));
                }
            });
        }

        return summary.toString();
    }

    public double getBalanceUsd() {
        return balanceUsd;
    }

    public synchronized Map<String, Double> getAssets() {
        return new HashMap<>(assets);
    }

    public synchronized List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    private static void validatePositive(double value) {
        if (value <= EPSILON) {
            throw new IllegalArgumentException("Parameter 'value' passed should be positive!");
        }
    }

    private synchronized double calculateTotalInvested() {
        return assets.keySet().stream()
                .mapToDouble(this::getAssetInvested)
                .sum();
    }

    private synchronized double calculateCurrentValue(Map<String, Double> currentPrices) {
        double assetsValue = calculateAssetsValue(currentPrices);
        return balanceUsd + assetsValue;
    }

    private double calculateAssetsValue(Map<String, Double> currentPrices) {
        return assets.entrySet().stream()
                .mapToDouble(entry -> calculateAssetValue(entry.getKey(), entry.getValue(), currentPrices))
                .sum();
    }

    private double calculateAssetValue(String assetId, double quantity, Map<String, Double> currentPrices) {
        Double price = currentPrices.get(assetId);
        return price != null ? quantity * price : MINIMUM_AMOUNT;
    }

    private synchronized double calculateAssetProfit(String assetId, double currentPrice) {
        Double quantity = assets.get(assetId);
        if (quantity == null) {
            return MINIMUM_AMOUNT;
        }

        double totalInvestedInAsset = getAssetInvested(assetId);
        double currentValue = quantity * currentPrice;

        return currentValue - totalInvestedInAsset;
    }

    private synchronized double getAssetInvested(String assetId) {
        return transactionHistory.stream()
                .filter(transaction -> transaction.type() == TransactionType.BUY)
                .filter(transaction -> transaction.assetID().equals(assetId))
                .mapToDouble(transaction -> transaction.pricePerUnit() * transaction.quantity())
                .sum();
    }

    private static void validateWallet(Wallet wallet) {
        if (wallet == null) {
            throw new IllegalArgumentException("Parameter 'wallet' passed to construct an object is null!");
        }
    }

    private static void validateAssetId(String assetId) {
        if (assetId == null || assetId.isBlank()) {
            throw new IllegalArgumentException("Parameter 'assetId' is null or blank!");
        }
    }

    private static void validateCurrentPrices(Map<String, Double> currentPrices) {
        if (currentPrices == null || currentPrices.isEmpty()) {
            throw new IllegalArgumentException("Parameter 'currentPrices' passed to function does not contain data!");
        }
    }
}