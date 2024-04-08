package pposonggil.usedStuff.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@DynamicInsert
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @OneToMany(mappedBy = "member")
    private List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "reviewer")
    private List<Review> reviewerReviews = new ArrayList<>();

    @OneToMany(mappedBy = "reviewed")
    private List<Review> reviewedReviews = new ArrayList<>();

    @OneToMany(mappedBy = "reported")
    private List<Report> reportedReviews = new ArrayList<>();

    @OneToMany(mappedBy = "reporter")
    private List<Report> reporterReviews = new ArrayList<>();

    @OneToMany(mappedBy = "blocked")
    private List<Block> blockedBlocks = new ArrayList<>();

    @OneToMany(mappedBy = "blocker")
    private List<Block> blockerBlocks = new ArrayList<>();

    @OneToMany(mappedBy = "seller")
    private List<Distance> sellerDistances = new ArrayList<>();

    @OneToMany(mappedBy = "buyer")
    private List<Distance> buyerDistances = new ArrayList<>();

    private String Name;
    private String nickname;
    private String phone;

    @ColumnDefault(value = "10")
    private Double rating;

    private Long transaction_count;
    private List<LocalDate> transaction_times;
}
