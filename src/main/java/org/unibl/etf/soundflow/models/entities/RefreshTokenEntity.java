package org.unibl.etf.soundflow.models.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "refresh_token")
public class RefreshTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "token", nullable = false, length = 512, unique = true)
    private String token;

    @Column(name = "expiry", nullable = false)
    private Instant expiry;

    @Column(name = "revoked", nullable = false)
    @ColumnDefault("false")
    private Boolean revoked;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    @PrePersist
    public void prePersist() {
        if (this.revoked == null) {
            this.revoked = false;
        }
    }
}
