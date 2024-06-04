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
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "EMAIL_UNIQUE",
                columnNames = {"email"}
        )
})
@Getter
@DynamicInsert
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String provider;
    private String profile_image;


    @ElementCollection(fetch=FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

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

    @ColumnDefault(value = "5")
    private Double ratingScore;

    private boolean isActivated;


}
