package com.uni.pe.storyhub.repository;

import com.uni.pe.storyhub.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class BlogRepository implements IBlogRepository{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean añadirBlog(Blog blogRequest) {
        try {
            String sql = "WITH new_blog AS (" +
                    "INSERT INTO blog (titulo, slug, breve_descripcion, img_banner, img_portada, descripcion_img_portada, contenido_blog, publicado, id_usuario) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "RETURNING id_blog" +
                    ")," +
                    "existing_tags AS (" +
                    "SELECT id_tag, nombre " +
                    "FROM tags " +
                    "WHERE nombre IN (" + buildInClause(blogRequest.getTags().size()) + ")" +
                    ")," +
                    "new_tags AS (" +
                    "INSERT INTO tags (nombre) " +
                    "SELECT tag.nombre " +
                    "FROM (VALUES " + buildValuesClause(blogRequest.getTags().size()) + ") AS tag(nombre) " +
                    "WHERE NOT EXISTS (" +
                    "SELECT 1 FROM existing_tags WHERE existing_tags.nombre = tag.nombre" +
                    ")" +
                    "RETURNING id_tag, nombre" +
                    ")" +
                    "INSERT INTO detalle_blog_tags (id_blog, id_tag) " +
                    "SELECT nb.id_blog, nt.id_tag " +
                    "FROM new_blog nb " +
                    "JOIN (" +
                    "SELECT * FROM existing_tags " +
                    "UNION ALL " +
                    "SELECT * FROM new_tags" +
                    ") AS nt ON true";

            List<Object> parameters = new ArrayList<>();
            parameters.add(blogRequest.getTitulo());
            parameters.add(blogRequest.getSlug());
            parameters.add(blogRequest.getBreve_descripcion());
            parameters.add(blogRequest.getImg_banner());
            parameters.add(blogRequest.getImg_portada());
            parameters.add(blogRequest.getDescripcion_img_portada());
            parameters.add(blogRequest.getContenido_blog());
            parameters.add(blogRequest.getPublicado());
            parameters.add(blogRequest.getUserIDResponse().getId_usuario());
            parameters.addAll(blogRequest.getTags().stream().map(Tags::getNombre).collect(Collectors.toList()));
            parameters.addAll(blogRequest.getTags().stream().map(Tags::getNombre).collect(Collectors.toList()));

            jdbcTemplate.update(sql, parameters.toArray());

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String buildInClause(int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append("?");
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    private String buildValuesClause(int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append("(?)");
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
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
                "b.breve_descripcion AS breve_descripcion, " +
                "b.img_banner AS img_banner, " +
                "b.slug AS slug, " +
                "TRIM(TO_CHAR(b.fecha_creacion, 'Month')) || ' ' || TO_CHAR(b.fecha_creacion, 'DD, YYYY') AS fecha_creacion " +
                "FROM usuario u " +
                "INNER JOIN blog b ON u.id_usuario = b.id_usuario " +
                "WHERE b.publicado = true " +
                "order by b.fecha_creacion ";

        return jdbcTemplate.query(SQL, (rs, rowNum) -> {
            BlogResponse blogResponse = new BlogResponse();
            User user = new User();

            user.setNombre_completo(rs.getString("user_nombre_completo"));
            user.setUsername(rs.getString("user_username"));
            user.setImagen_perfil(rs.getString("imagen_perfil"));

            blogResponse.setUser(user);
            blogResponse.setTitulo(rs.getString("titulo"));
            blogResponse.setImg_banner(rs.getString("img_banner"));
            blogResponse.setFecha_creacion(rs.getString("fecha_creacion"));
            blogResponse.setSlug(rs.getString("slug"));

            return blogResponse;
        });
    }

    @Override
    public BlogDetailResponse obtenerDetalleDelBlog(String slug) {
        String SQL = "select \n" +
                "\tu.nombre_completo,\n" +
                "\tu.imagen_perfil,\n" +
                "\tTO_CHAR(b.fecha_creacion, 'TMMon DD') AS fecha_creacion,\n" +
                "\tb.img_banner,\n" +
                "\tb.titulo,\n" +
                "\tb.contenido_blog,\n" +
                "\tb.img_portada,\n" +
                "\tb.descripcion_img_portada,\n" +
                "\tt.nombre\n" +
                "from usuario u \n" +
                "inner join blog b\n" +
                "on u.id_usuario = b.id_usuario \n" +
                "inner join detalle_blog_tags dbt \n" +
                "on b.id_blog = dbt.id_blog \n" +
                "inner join tags t \n" +
                "on dbt.id_tag = t.id_tag\n" +
                "where b.slug = ?";
        try {
            List<BlogDetailResponse> userProfileList = jdbcTemplate.query(SQL, new Object[]{slug}, new BlogRepository.BlogDetailRowMapper());

            if (userProfileList.isEmpty()) {
                // Si no se encontró ningún perfil, devolvemos null
                //System.out.println("user PROFILE EMPTY: "+ userProfileList);
                return null;
            } else {
                // Si se encontró al menos un perfil, devolvemos el primero
                //System.out.println("user PROFILE LIST: "+ userProfileList.get(0));
                return userProfileList.get(0);
            }
        } catch (EmptyResultDataAccessException e) {
            // Manejar el caso en el que no se encontró ningún resultado
            // Aquí puedes devolver un valor predeterminado, lanzar una excepción personalizada, etc.
            // Por ejemplo, podrías devolver null o un UserProfileResponse vacío
            System.out.println("e.getMessage: "+ e.getMessage());
            return null;
        }
    }

    private class BlogDetailRowMapper implements RowMapper<BlogDetailResponse> {
        public BlogDetailResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
            BlogDetailResponse blogDetailResponse = new BlogDetailResponse();
            blogDetailResponse.setNombre_completo(rs.getString("nombre_completo"));
            blogDetailResponse.setImagen_perfil(rs.getString("imagen_perfil"));
            blogDetailResponse.setFecha_creacion(rs.getString("fecha_creacion"));
            blogDetailResponse.setImg_banner(rs.getString("img_banner"));
            blogDetailResponse.setTitulo(rs.getString("titulo"));
            blogDetailResponse.setContenido_blog(rs.getString("contenido_blog"));
            blogDetailResponse.setImg_portada(rs.getString("img_portada"));
            blogDetailResponse.setDescripcion_img_portada(rs.getString("descripcion_img_portada"));

            // Si el título del blog es null, significa que el usuario no tiene blogs
            if (rs.getString("nombre") == null) {
                blogDetailResponse.setTag(new ArrayList<>()); // Establecer una lista vacía
            } else {
                // El usuario tiene al menos un blog, creamos una lista para almacenar los blogs
                List<Tags> tagsList = new ArrayList<>();
                do {
                    Tags tags = new Tags();
                    tags.setNombre(rs.getString("nombre"));

                    tagsList.add(tags);
                } while (rs.next());
                blogDetailResponse.setTag(tagsList);
            }

            return blogDetailResponse;
        }
    }

    @Override
    public boolean existeSlug(String slug) {
        String sql = "SELECT COUNT(*) FROM blog WHERE slug = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, slug);
        return count > 0;
    }

    @Override
    public boolean esPublico(String slug) {
        String sql = "SELECT publicado FROM blog WHERE slug = ?";
        Boolean publicado = jdbcTemplate.queryForObject(sql, Boolean.class, slug);
        return publicado != null && publicado;
    }
}
