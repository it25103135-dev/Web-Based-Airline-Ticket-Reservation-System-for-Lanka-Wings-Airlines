package com.lankawings.util;

public final class PaymentGateway {
    private PaymentGateway() {}

    public static GatewayResult authorize(String cardNumber, String expiry, String cvv) {
        if (!CardUtil.isValidCardNumber(cardNumber)) return new GatewayResult(false, "Card authorization failed: invalid card number.");
        if (!CardUtil.isValidExpiry(expiry)) return new GatewayResult(false, "Card authorization failed: invalid or expired card.");
        if (!CardUtil.isValidCvv(cvv)) return new GatewayResult(false, "Card authorization failed: invalid CVV.");
        return new GatewayResult(true, null);
    }

    public record GatewayResult(boolean approved, String message) {}
}
