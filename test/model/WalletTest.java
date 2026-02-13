package model;

import model.enums.TransactionType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WalletTest {
    private Wallet wallet;

    @BeforeEach
    public void setUp() {
        wallet = new Wallet();
    }

    @Test
    void testDeposit() {
        double valueToDeposit = 5.00;

        wallet.deposit(valueToDeposit);

        assertEquals(valueToDeposit, wallet.getBalanceUsd());
    }

    @Test
    void testDepositNegativeAmount() {
        double valueToDeposit = - 5.00;

        assertThrows(IllegalArgumentException.class, () ->wallet.deposit(valueToDeposit),  "The amount to deposit is expected to be a non-negative value");
    }

    @Test
    void testTransactionHistoryCorrect() {
        double valueToDeposit = 5.00;

        wallet.deposit(valueToDeposit);

        List<Transaction> history = wallet.getTransactionHistory();

        assertNotNull(history);
        assertEquals(1, history.size());

        Transaction toTest = history.get(0);

        assertEquals("deposit", toTest.type().getTransaction());
        assertEquals("USD", toTest.assetID());
        assertEquals(valueToDeposit, toTest.pricePerUnit());
        assertEquals(1.0, toTest.quantity());
        assertEquals("deposit", history.get(0).type().getTransaction());
    }

    @Test
    void testBuyNegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> wallet.buy("BTC", -5.0, 0.2), "Price is supposed to be a positive double");
    }

    @Test
    void testBuyNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> wallet.buy("BTC", 5.0, -0.2), "Amount is supposed to be a positive double");
    }

    @Test
    void testBuyAmountUsdGreaterThenBalance() {
        assertFalse(wallet.buy("BTC", 2.0, 5.02));
    }

    @Test
    void testBuyNoQuantityPossibleToBuy() {
        assertFalse(wallet.buy("BTC", 2.1, 1.98));
    }

    @Test
    void buyTwiceConsequentlyToIncreaseAmount() {
        wallet.deposit(200.00);
        wallet.buy("BTC", 2.0, 100);

        assertNotNull(wallet.getAssets());
        assertEquals(50.0 ,wallet.getAssets().get("BTC"));

        assertTrue(wallet.buy("BTC", 5.0, 60));

        assertEquals(62.0 ,wallet.getAssets().get("BTC"));
        assertEquals(40.00, wallet.getBalanceUsd());

        List<Transaction> history = wallet.getTransactionHistory();
        assertNotNull(history);
        assertEquals(3, history.size());
        assertEquals("deposit", history.get(0).type().getTransaction());
        assertEquals("buy", history.get(1).type().getTransaction());
        assertEquals("buy", history.get(2).type().getTransaction());
    }

    @Test
    void testSellNegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> wallet.sell("BTC", -1.0), "Sell price is supposed to be positive double!");
    }

    @Test
    void testSellNonPresentAsset() {
        assertFalse(wallet.sell("BTC", 100));

        wallet.deposit(100);
        wallet.buy("BTC", 100, 100);

        double balanceBeforeSelling = wallet.getBalanceUsd();

        assertTrue(wallet.sell("BTC", 100));

        assertEquals(balanceBeforeSelling + 100, wallet.getBalanceUsd());

        List<Transaction> history = wallet.getTransactionHistory();

        assertNotNull(history);
        assertEquals(3, history.size());

        assertEquals("USD", history.get(0).assetID());
        assertEquals("BTC", history.get(1).assetID());
        assertEquals("BTC", history.get(2).assetID());

        assertEquals("deposit", history.get(0).type().getTransaction());
        assertEquals("buy", history.get(1).type().getTransaction());
        assertEquals("sell", history.get(2).type().getTransaction());

        assertEquals(100.00, history.get(0).pricePerUnit());
        assertEquals(100.00, history.get(1).pricePerUnit());
        assertEquals(100.00, history.get(2).pricePerUnit());
    }

    @Test
    void testGetWalletSummaryWithNoTransactions() {
        Wallet wallet = new Wallet();
        wallet.deposit(1000.0);

        String summary = wallet.getWalletSummary();

        assertNotNull(summary);
        assertTrue(summary.contains("$1000"));
        assertTrue(summary.contains("WALLET SUMMARY:"));
    }

    @Test
    void testGetWalletSummaryWithTransactions() {
        Wallet wallet = new Wallet();
        wallet.deposit(1000.0);

        boolean btcResult = wallet.buy("BTC", 500, 0.1);
        boolean ethResult = wallet.buy("ETH", 200.0, 2.0);

        String summary = wallet.getWalletSummary();

        assertNotNull(summary);

        assertTrue(summary.contains("BTC"));
        assertTrue(summary.contains("ETH"));
        assertTrue(summary.contains("0.1") || summary.contains("0,1"));
    }


    @Test
    void testGetWalletSummaryEmptyPrices() {
        Wallet wallet = new Wallet();
        wallet.deposit(500.0);

        String summary = wallet.getWalletSummary();

        assertNotNull(summary);
        assertFalse(summary.isEmpty());
    }

    @Test
    void testGetWalletOverallSummaryWithProfit() {
        Wallet wallet = new Wallet();
        wallet.deposit(10000.0);

        wallet.buy("BTC", 0.1, 400.0);
        wallet.buy("ETH", 2.0, 200.0);

        Map<String, Double> prices = Map.of("BTC", 400.0, "ETH", 250.0);

        String summary = wallet.getWalletOverallSummary(prices);

        assertNotNull(summary);
        assertTrue(summary.contains("Profit"));

    }

    @Test
    void testGetWalletOverallSummaryWithLoss() {
        Wallet wallet = new Wallet();
        wallet.deposit(10000.0);

        wallet.buy("BTC", 0.1, 500.0);
        Map<String, Double> prices = Map.of("BTC", 400.0);

        String summary = wallet.getWalletOverallSummary(prices);

        assertNotNull(summary);
        assertTrue(summary.contains("Loss") || summary.contains("-"));
    }

    @Test
    void testGetWalletOverallSummaryEmptyWallet() {
        Wallet wallet = new Wallet();
        wallet.deposit(1000.0);

        Map<String, Double> prices = Map.of("BTC", 500.0);

        String summary = wallet.getWalletOverallSummary(prices);

        assertNotNull(summary);
        assertTrue(summary.contains("0") || summary.contains("0.00"));
    }
}

