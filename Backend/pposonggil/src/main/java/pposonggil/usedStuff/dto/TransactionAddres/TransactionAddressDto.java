package pposonggil.usedStuff.dto.TransactionAddres;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pposonggil.usedStuff.domain.TransactionAddress;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class TransactionAddressDto {
    private String name;
    private Double latitude;
    private Double longitude;
    private String street;

    public static TransactionAddressDto fromEntity(TransactionAddress transactionAddress) {
        return TransactionAddressDto.builder()
                .name(transactionAddress.getName())
                .latitude(transactionAddress.getLatitude())
                .longitude(transactionAddress.getLongitude())
                .street(transactionAddress.getStreet())
                .build();
    }

    public TransactionAddress toEntity() {
        return new TransactionAddress(this.name, this.latitude, this.longitude, this.street);
    }
}
