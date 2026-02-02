package model;

import java.time.LocalDateTime;

public record Transaction(Asset asset,
                          double paidPrice,
                          double quantity,
                          TransactionType type,
                          LocalDateTime timestamp) {
}
