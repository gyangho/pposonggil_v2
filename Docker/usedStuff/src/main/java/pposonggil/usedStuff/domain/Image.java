package pposonggil.usedStuff.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@DynamicInsert
public class Image {
    @Id
    @GeneratedValue
    @Column(name = "picture_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board imageBoard;

    private String imageUrl;
    private LocalDate createdAt;

    public void setImageBoard(Board board) {
        this.imageBoard = board;
        board.getImages().add(this);
    }
}
