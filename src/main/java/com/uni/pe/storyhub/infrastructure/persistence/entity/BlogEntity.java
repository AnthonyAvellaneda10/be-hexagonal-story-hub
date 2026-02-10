package com.uni.pe.storyhub.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "blog")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idBlog;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(name = "breve_descripcion", length = 500)
    private String breveDescripcion;

    @Column(name = "img_banner", length = 300)
    private String imgBanner;

    @Column(name = "img_portada", length = 300)
    private String imgPortada;

    @Column(name = "descripcion_img_portada", length = 500)
    private String descripcionImgPortada;

    @Column(name = "contenido_blog", columnDefinition = "TEXT")
    private String contenidoBlog;

    @Builder.Default
    @Column(nullable = false)
    private boolean publicado = false;

    @Builder.Default
    @Column(nullable = false)
    private Integer likes = 0;

    @Builder.Default
    @Column(name = "vistas_count", nullable = false)
    private Integer vistasCount = 0;

    @Builder.Default
    @Column(name = "is_removed", nullable = false)
    private boolean removed = false;

    @Column(unique = true, nullable = false, length = 255)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private UserEntity author;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "detalle_blog_tags", joinColumns = @JoinColumn(name = "id_blog"), inverseJoinColumns = @JoinColumn(name = "id_tag"))
    private Set<TagEntity> tags = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommentEntity> comments = new HashSet<>();

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "usuario_creacion", length = 100)
    private String usuarioCreacion;

    @Column(name = "usuario_actualizacion", length = 100)
    private String usuarioActualizacion;
}
