package pposonggil.usedStuff.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicInsert
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class Lane {
    @Id
    @GeneratedValue
    @Column(name = "lane_id")
    private Long id;

    private String name;
    private String color;

    public static LaneBuilder builder() {
        return new LaneBuilder();
    }

    public static  Lane buildLane(String name, String color) {
        return Lane.builder()
                .name(name)
                .color(color)
                .build();
    }
}
