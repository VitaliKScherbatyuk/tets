/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 24.12.2023
 */

package scherbatyuk.network.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
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
public class User{

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
    @Lob
    private String imageData;
    private String password;
    private LocalDate createData;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Friends> friendsList;



    public User(User user) {
        this.id = user.id;
        this.name = user.name;
        this.email = user.email;
        this.age = user.age;
        this.country = user.country;
        this.city = user.city;
        this.hobby = user.hobby;
        this.imageData = user.imageData;
        this.password = user.password;
        this.createData = user.createData;
        this.role = user.role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(getId(), user.getId())
                && Objects.equals(getName(), user.getName())
                && Objects.equals(getEmail(), user.getEmail())
                && Objects.equals(getAge(), user.getAge())
                && Objects.equals(getCountry(), user.getCountry())
                && Objects.equals(getCity(), user.getCity())
                && Objects.equals(getHobby(), user.getHobby())
                && Objects.equals(getImageData(), user.getImageData())
                && Objects.equals(getPassword(), user.getPassword())
                && Objects.equals(getCreateData(), user.getCreateData())
                && getRole() == user.getRole()
                && Objects.equals(getFriendsList(), user.getFriendsList());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getEmail(), getAge(), getCountry(), getCity(), getHobby(),
                getImageData(), getPassword(), getCreateData(), getRole(), getFriendsList());
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
                ", imageData='" + imageData + '\'' +
                ", password='" + password + '\'' +
                ", createData=" + createData +
                ", role=" + role +
                ", friendsList=" + friendsList +
                '}';
    }
}