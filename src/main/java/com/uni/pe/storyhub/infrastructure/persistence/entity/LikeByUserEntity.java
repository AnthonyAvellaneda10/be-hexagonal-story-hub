package com.uni.pe.storyhub.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "likesbyuser", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "id_usuario", "id_blog" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeByUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idLikebyuser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_blog", nullable = false)
    private BlogEntity blog;

    @Column(name = "is_like", nullable = false)
    private boolean isLike;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
