package pposonggil.usedStuff.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import pposonggil.usedStuff.domain.Route.Path;
//import pposonggil.usedStuff.domain.Route.RouteRequest;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

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
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class Member extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Builder.Default
    @OneToMany(mappedBy = "writer")
    private List<Board> boards = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "requester")
    private List<ChatRoom> chatRooms = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "routeRequester")
    private List<Path> paths = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "tradeSubject")
    private List<Trade> tradeSubjects = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "tradeObject")
    private List<Trade> tradeObjects = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "sender")
    private List<Message> messages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "reviewSubject")
    private List<Review> reviewSubjects = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "reviewObject")
    private List<Review> reviewObjects = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "reportSubject")
    private List<Report> reportSubjects = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "reportObject")
    private List<Report> reportObjects = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "blockSubject")
    private List<Block> blockSubjects = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "blockObject")
    private List<Block> blockObjects = new ArrayList<>();

    private String name;
    private String nickName;
    private String phone;

    @ColumnDefault(value = "5")
    private Double ratingScore;

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

    public static MemberBuilder builder(String nickName) {
        if (nickName == null)
            throw new IllegalArgumentException("필수 파라미터 누락");
        return new MemberBuilder()
                .nickName(nickName);
    }

    public static Member buildMember(String name, String nickName, String phone) {
        return Member.builder(nickName)
                .name(name)
                .phone(phone)
                .ratingScore(5.0)
                .isActivated(true)
                .build();
    }
}
