package pposonggil.usedStuff.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@DynamicInsert
public class Block {
    @Id
    @GeneratedValue
    @Column(name = "block_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "block_subject_id")
    private Member blockSubject;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "block_object_id")
    private Member blockObject;

    private String blockType;
    private String content;
    private LocalDate createdAt;

    public void setBlockSubject(Member member) {
        this.blockSubject = member;
        member.getBlockSubjects().add(this);
    }

    public void setBlockObject(Member member) {
        this.blockObject = member;
        member.getBlockObjects().add(this);
    }

}
