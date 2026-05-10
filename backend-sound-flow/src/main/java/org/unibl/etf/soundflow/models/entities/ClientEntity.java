package org.unibl.etf.soundflow.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "client")
public class ClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 255)
    @NotNull
    @Column(name = "surname", nullable = false)
    private String surname;

    @Size(max = 255)
    @Column(name = "username", unique = true)
    private String username;

    @Size(min = 6, max = 320)
    @Column(name = "password")
    private String password;

    @Email
    @Size(min = 6, max = 320)
    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "auth_provider", nullable = false)
//    private AuthProvider authProvider;

    @OneToMany(mappedBy = "client")
    private List<SeparationJobEntity> jobs;

    @PrePersist
    public void prePersist() {
//        if (this.authProvider == null) {
//            this.authProvider = AuthProvider.LOCAL;
//        }
        if(this.isVerified == null) {
            this.isVerified = false;
        }
    }
}
