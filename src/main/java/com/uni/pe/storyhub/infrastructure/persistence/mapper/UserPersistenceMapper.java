package com.uni.pe.storyhub.infrastructure.persistence.mapper;

import com.uni.pe.storyhub.domain.entity.User;
import com.uni.pe.storyhub.infrastructure.persistence.entity.UserEntity;

public class UserPersistenceMapper {

    public static User toDomain(UserEntity entity) {
        if (entity == null)
            return null;
        return User.builder()
                .idUsuario(entity.getIdUsuario())
                .nombreCompleto(entity.getNombreCompleto())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .imagenPerfil(entity.getImagenPerfil())
                .activo(entity.isActivo())
                .emailVerificado(entity.isEmailVerificado())
                .codigoVerificacion(entity.getCodigoVerificacion())
                .linkedin(entity.getLinkedin())
                .telegram(entity.getTelegram())
                .instagram(entity.getInstagram())
                .facebook(entity.getFacebook())
                .youtube(entity.getYoutube())
                .descripcion(entity.getDescripcion())
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .intentosFallidos(entity.getIntentosFallidos())
                .fechaBloqueo(entity.getFechaBloqueo())
                .usuarioCreacion(entity.getUsuarioCreacion())
                .usuarioActualizacion(entity.getUsuarioActualizacion())
                .build();
    }

    public static UserEntity toEntity(User domain) {
        if (domain == null)
            return null;
        return UserEntity.builder()
                .idUsuario(domain.getIdUsuario())
                .nombreCompleto(domain.getNombreCompleto())
                .username(domain.getUsername())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .imagenPerfil(domain.getImagenPerfil())
                .activo(domain.isActivo())
                .emailVerificado(domain.isEmailVerificado())
                .codigoVerificacion(domain.getCodigoVerificacion())
                .linkedin(domain.getLinkedin())
                .telegram(domain.getTelegram())
                .instagram(domain.getInstagram())
                .facebook(domain.getFacebook())
                .youtube(domain.getYoutube())
                .descripcion(domain.getDescripcion())
                .fechaCreacion(domain.getFechaCreacion())
                .fechaActualizacion(domain.getFechaActualizacion())
                .intentosFallidos(domain.getIntentosFallidos())
                .fechaBloqueo(domain.getFechaBloqueo())
                .usuarioCreacion(domain.getUsuarioCreacion())
                .usuarioActualizacion(domain.getUsuarioActualizacion())
                .build();
    }
}
