package pposonggil.usedStuff.domain.Route;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class PointInformation {
    private String name;
    private Double latitude;
    private Double longitude;
    private Long x;
    private Long y;

    protected PointInformation(){

    }

    public PointInformation(String name, Double latitude, Double longitude, Long x, Long y) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.x = x;
        this.y = y;
    }
}
