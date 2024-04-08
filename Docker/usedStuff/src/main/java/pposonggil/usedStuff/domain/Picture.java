package pposonggil.usedStuff.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@DynamicInsert
public class Picture {
    @Id
    @GeneratedValue
    @Column(name = "picture_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board pictureBoard;

    private String name;

}
