package com.uni.pe.storyhub.repository;

import com.uni.pe.storyhub.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BlogRepository implements IBlogRepository{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean añadirBlog(Blog blogRequest) {
        String titulo = blogRequest.getTitulo();
        String breveDescripcion = blogRequest.getBreve_descripcion();
        String imgBanner = blogRequest.getImg_banner();
        String imgPortada = blogRequest.getImg_portada();
        String descripcionImgPortada = blogRequest.getDescripcion_img_portada();
        String contenidoBlog = blogRequest.getContenido_blog();
        Boolean publicado = blogRequest.getPublicado();
        String slug = blogRequest.getSlug();
        Integer idUsusario = blogRequest.getUserIDResponse().getId_usuario();

        String sql = "INSERT INTO blog(titulo, slug, breve_descripcion, img_banner, img_portada, descripcion_img_portada, contenido_blog, publicado, id_usuario) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int rowsAffected = jdbcTemplate.update(sql, titulo, slug, breveDescripcion, imgBanner, imgPortada, descripcionImgPortada, contenidoBlog, publicado, idUsusario);
        return rowsAffected > 0;
    }

    @Override
    public boolean existeIdUsuario(Integer idUsuario) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE id_usuario = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, idUsuario);
        return count > 0;
    }

    @Override
    public boolean existeEmailDelUsuario(String email) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE email = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count > 0;
    }

    @Override
    public List<BlogDto> obtenerBlogsDelUsuario(String email) {
        String SQL = "select \n" +
                "\tb.id_blog,\n" +
                "\tb.img_portada,\n" +
                "\tb.titulo, \n" +
                "\tb.breve_descripcion, \n" +
                "\tb.publicado,\n" +
                "\tb.slug,\n" +
                "\tTO_CHAR(b.fecha_creacion, 'DD/MM/YYYY, HH24:MI') AS fecha_creacion\n" +
                "from blog b \n" +
                "inner join usuario u  \n" +
                "on b.id_usuario  =  u.id_usuario\n" +
                "where u.email = ?";
        return jdbcTemplate.query(SQL, new Object[] { email }, BeanPropertyRowMapper.newInstance(BlogDto.class));

    }

    @Override
    public List<BlogResponse> obtenerTodosLosBlogsCreados() {
        String SQL = "SELECT " +
                "u.nombre_completo AS user_nombre_completo, " +
                "u.username AS user_username, " +
                "u.imagen_perfil AS imagen_perfil, " +
                "b.titulo AS titulo, " +
                "b.descripcion AS descripcion, " +
                "b.img_banner AS img_banner, " +
                "TRIM(TO_CHAR(b.fecha_creacion, 'Month')) || ' ' || TO_CHAR(b.fecha_creacion, 'DD, YYYY') AS fecha_creacion " +
                "FROM usuario u " +
                "INNER JOIN blog b ON u.id_usuario = b.id_usuario " +
                "WHERE b.publicado = true";

        return jdbcTemplate.query(SQL, (rs, rowNum) -> {
            BlogResponse blogResponse = new BlogResponse();
            User user = new User();

            user.setNombre_completo(rs.getString("user_nombre_completo"));
            user.setUsername(rs.getString("user_username"));

            blogResponse.setUser(user);
            blogResponse.setImagen_perfil(rs.getString("imagen_perfil"));
            blogResponse.setTitulo(rs.getString("titulo"));
            blogResponse.setImg_banner(rs.getString("img_banner"));
            blogResponse.setFecha_creacion(rs.getString("fecha_creacion"));

            return blogResponse;
        });
    }
}
