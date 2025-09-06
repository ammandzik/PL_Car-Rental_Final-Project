package pl.coderslab.carrental.model.enum_package;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentStatus {

    APPROVED("Approved"),
    REJECTED("Rejected"),
    AWAITING("Awaiting"),
    FUNDS_BEING_REFUNDED("Funds being refunded"),
    CANCELLED("Cancelled");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }
}
