package pl.coderslab.carrental.model.enum_package;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CarStatus {

    AVAILABLE("Available"),
    RENTED("Rented");

    private final String description;

    CarStatus(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

}
