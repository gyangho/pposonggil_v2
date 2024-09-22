package pposonggil.usedStuff.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class TransactionAddress {
    private String name;
    private Double latitude;
    private Double longitude;
    private String street;

    protected TransactionAddress() {

    }

    public TransactionAddress(String name, Double latitude, Double longitude, String street) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.street = street;
    }
}
