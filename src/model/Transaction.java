package model;

import model.enums.TransactionType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record Transaction(String assetID, double pricePerUnit, double quantity,
                          TransactionType type, LocalDateTime timestamp) {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String toString() {
        String time = timestamp.format(DATE_FORMATTER);
        double total = pricePerUnit * quantity;

        return switch (type) {
            case DEPOSIT -> String.format("[%s] DEPOSIT: +$%.2f", time, quantity);
            case BUY -> String
                    .format("[%s] BUY: %.6f %s @ $%.2f = $%.2f", time, quantity, assetID, pricePerUnit, total);
            case SELL -> String
                    .format("[%s] SELL: %.6f %s @ $%.2f = $%.2f", time, quantity, assetID, pricePerUnit, total);
        };
    }

    public String toCsv() {
        return String.format("%s,%s,%s,%.2f,%.6f,%.2f", timestamp.toString(), type.name(), assetID, pricePerUnit,
                quantity, pricePerUnit * quantity);
    }
}