package pl.coderslab.carrental.model.enum_package;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentMethod {

    CARD("Card"),
    BANK_TRANSFER("Bank Transfer");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }
}
