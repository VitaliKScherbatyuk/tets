/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 24.12.2023
 */

package scherbatyuk.network.domain;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * creating a user to work with the database. We use lombok:
 * with arguments in the constructor, without them
 * from hashCode and equals and the constructor. We generate setters and getters
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class User {

    private static final String SEQ_NAME = "user_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_NAME)
    @SequenceGenerator(name = SEQ_NAME, sequenceName = SEQ_NAME, allocationSize = 1)
    private Integer id;

    private String name;
    private String email;
    private Integer age;
    private String country;
    private String city;
    private String hobby;
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;
    private String password;
    private LocalDate createData;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @OneToOne
    @JoinColumn(name = "geo_location_id")
    private GeoLocationResponse geoLocationResponse;

    public User(User user) {
        this.id = user.id;
        this.name = user.name;
        this.email = user.email;
        this.age = user.age;
        this.country = user.country;
        this.city = user.city;
        this.hobby = user.hobby;
        this.image = user.image;
        this.password = user.password;
        this.createData = user.createData;
        this.role = user.role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(getId(), user.getId()) && Objects.equals(getName(),
                user.getName()) && Objects.equals(getEmail(), user.getEmail()) && Objects.equals(getAge(),
                user.getAge()) && Objects.equals(getCountry(), user.getCountry()) && Objects.equals(getCity(),
                user.getCity()) && Objects.equals(getHobby(), user.getHobby()) && Objects.equals(getPassword(),
                user.getPassword()) && Objects.equals(getCreateData(),
                user.getCreateData()) && getRole() == user.getRole();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getEmail(), getAge(), getCountry(), getCity(), getHobby(),
                getPassword(), getCreateData(), getRole());
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", hobby='" + hobby + '\'' +
                ", password='" + password + '\'' +
                ", createData=" + createData +
                ", role=" + role +
                ", geoLocationResponse=" + geoLocationResponse +
                '}';
    }
}