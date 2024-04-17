package pposonggil.usedStuff.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@DynamicInsert
public class Distance {
    @Id
    @GeneratedValue
    @Column(name = "distance_id")
    private Long id;

    private Long distance;
}
