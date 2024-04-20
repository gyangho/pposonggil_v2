package pposonggil.usedStuff.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = {

        @UniqueConstraint(
                name = "NICKNAME_UNIQUE",
                columnNames = {"nickName"}
        ),
        @UniqueConstraint(
                name = "PHONE_UNIQUE",
                columnNames = {"phone"}
        )
})
@Getter
@DynamicInsert
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @OneToMany(mappedBy = "writer")
    private List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "chatMember")
    private List<ChatRoom> chatRooms = new ArrayList<>();

    @OneToMany(mappedBy = "sender")
    private List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "transactionMember")
    private List<TransactionInformation> transactionInformations = new ArrayList<>();

    @OneToMany(mappedBy = "reviewSubject")
    private List<Review> reviewSubjects = new ArrayList<>();

    @OneToMany(mappedBy = "reviewObject")
    private List<Review> reviewObjects = new ArrayList<>();

    @OneToMany(mappedBy = "reportSubject")
    private List<Report> reportSubjects = new ArrayList<>();

    @OneToMany(mappedBy = "reportObject")
    private List<Report> reportObjects = new ArrayList<>();

    @OneToMany(mappedBy = "blockSubject")
    private List<Block> blockSubjects = new ArrayList<>();

    @OneToMany(mappedBy = "blockObject")
    private List<Block> blockObjects = new ArrayList<>();

    private String name;
    private String nickName;
    private String phone;

    @ColumnDefault(value = "10")
    private Double ratingScore;

    private LocalDate createdAt;
    private boolean isActivated;

    public void setName(String name) {
        this.name = name;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
