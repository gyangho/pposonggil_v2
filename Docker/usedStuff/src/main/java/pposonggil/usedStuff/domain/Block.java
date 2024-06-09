package pposonggil.usedStuff.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@DynamicInsert
public class Block extends BaseEntity{
    @Id
    @GeneratedValue
    @Column(name = "block_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "block_subject_id")
    private Member blockSubject;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "block_object_id")
    private Member blockObject;

    public void setBlockSubject(Member member) {
        this.blockSubject = member;
        member.getBlockSubjects().add(this);
    }

    public void setBlockObject(Member member) {
        this.blockObject = member;
        member.getBlockObjects().add(this);
    }

    public Block(Member blockSubject, Member blockObject) {
        this.blockSubject = blockSubject;
        this.blockObject = blockObject;
    }
}
