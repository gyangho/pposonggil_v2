package pposonggil.usedStuff.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter @Setter
@DynamicInsert
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String Name;
    private String nickname;
    private String phone;

    @ColumnDefault(value = "10")
    private Double rating;

    private Long transaction_count;
    private List<LocalDate> transaction_times;

}
