package com.uni.pe.storyhub.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "blog_vistas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogVista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idVista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_blog", nullable = false)
    private Blog blog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private User user;

    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @CreationTimestamp
    @Column(name = "fecha_vista", updatable = false)
    private LocalDateTime fechaVista;
}
