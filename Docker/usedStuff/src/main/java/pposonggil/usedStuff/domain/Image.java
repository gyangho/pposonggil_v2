package pposonggil.usedStuff.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class Image extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "picture_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board imageBoard;

    private String fileName;
    private String imageUrl;

    public void setImageBoard(Board board) {
        this.imageBoard = board;
        board.getImages().add(this);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Image(Board board) {
        this.imageBoard = board;
    }
}
