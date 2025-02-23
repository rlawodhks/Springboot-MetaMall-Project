package shop.mtcoding.metamall.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Setter // DTO 만들면 삭제해야됨
@Getter
@Table(name = "user_tb")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String username;

    @JsonIgnore
    @Column(nullable = false, length = 60) // 60자 이상으로 하자, 1234 -> 단방향 암호화 60Byte
    private String password;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 10)
    private String role; // USER(고객), SELLER(판매자), ADMIN(관리자)

    @Column(nullable = false, length = 10) // length 값을 안정해주면 255로 자동 생성
    private Boolean status; // true 활성, false 비활성 계정

    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 권한 변경 (관리자)
    public void updateRole(String role) { // 권한을 바꾸는 메서드, setter로 직접 바꾸려 하지마라
        if (this.role.equals(role)) {
            // checkpoint 동일한 권한으로 변경할 수 없습니다.
        }
        this.role = role;
    }

    // 회원 탈퇴
    public void delete() {
        this.status = false;
    }


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public User(Long id, String username, String password, String email, String role, Boolean status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
