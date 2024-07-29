package com.uni.pe.storyhub.repository;

import com.uni.pe.storyhub.model.*;
import com.uni.pe.storyhub.utils.Utilidades;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class BlogRepository implements IBlogRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private static final int MAX_IMG_BANNER = 350;
    private static final int MAX_IMG_PORTADA = 350;

    @Override
    public boolean añadirBlog(Blog blogRequest) {
        try {
            // Validar la longitud de los campos antes de la inserción
            if (blogRequest.getImg_banner().length() > MAX_IMG_BANNER) {
                throw new DataIntegrityViolationException("El nombre de la imagen del banner excede el límite permitido");
            }

            if (blogRequest.getImg_portada().length() > MAX_IMG_PORTADA) {
                throw new DataIntegrityViolationException("El nombre de la imagen de la portada excede el límite permitido");
            }


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
        } catch (DataIntegrityViolationException e) {
            // Manejar la excepción de clave duplicada (nombre de usuario duplicado)
            throw e;
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
    public boolean existeIdBlog(Integer idBlog) {
        String sql = "SELECT COUNT(*) FROM blog WHERE id_blog = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, idBlog);
        return count > 0;
    }

    @Override
    public boolean blogEliminado(Integer idBlog) {
        String sql = "SELECT COUNT(*) FROM blog WHERE id_blog = ? and is_removed = true";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, idBlog);
        return count > 0;
    }

    @Override
    public int blogEliminadoBySlug(String slug) {
        String sql = "SELECT COUNT(*) FROM blog WHERE slug = ? and publicado = true and is_removed = false";
        return jdbcTemplate.queryForObject(sql, Integer.class, slug);
    }

    @Override
    public boolean existeEmailDelUsuario(String email) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE email = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count > 0;
    }

    @Override
    public Page<BlogDto> obtenerBlogsDelUsuario(String email, Pageable pageable) {
        String SQL = "select \n" +
                "\tb.id_blog,\n" +
                "\tb.img_portada,\n" +
                "\tb.titulo, \n" +
                "\tb.breve_descripcion, \n" +
                "\tb.publicado,\n" +
                "\tb.slug,\n" +
                "\tb.contenido_blog,\n" +
                "\tTO_CHAR(b.fecha_creacion, 'DD/MM/YYYY, HH24:MI') AS fecha_creacion\n" +
                "from blog b \n" +
                "inner join usuario u  \n" +
                "on b.id_usuario  =  u.id_usuario\n" +
                "where u.email = ? AND b.is_removed = FALSE";

        List<BlogDto> blogs = jdbcTemplate.query(SQL, new Object[]{email}, BeanPropertyRowMapper.newInstance(BlogDto.class));
        int total = blogs.size();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), total);
        List<BlogDto> pagedBlogs = blogs.subList(start, end);

        return new PageImpl<>(pagedBlogs, pageable, total);
    }

    @Override
    public List<Tags> obtenerTags() {
        String SQL = "select id_tag, nombre from tags t";
        return jdbcTemplate.query(SQL, BeanPropertyRowMapper.newInstance(Tags.class));
    }

    @Override
    public Page<BlogResponse> obtenerTodosLosBlogsCreados(Pageable pageable) {
        String sql = "SELECT\n" +
                "    u.nombre_completo,\n" +
                "    u.username,\n" +
                "    u.imagen_perfil,\n" +
                "    b.id_blog,\n" +
                "    b.titulo,\n" +
                "    b.breve_descripcion,\n" +
                "    b.img_banner,\n" +
                "    b.slug,\n" +
                "    TRIM(TO_CHAR(b.fecha_creacion, 'Month')) || ' ' || TO_CHAR(b.fecha_creacion, 'DD, YYYY') AS fecha_creacion,\n" +
                "    t.nombre\n" +
                "FROM\n" +
                "    usuario u\n" +
                "INNER JOIN blog b ON u.id_usuario = b.id_usuario\n" +
                "INNER JOIN (\n" +
                "    SELECT\n" +
                "        dbt.id_blog,\n" +
                "        t.id_tag,\n" +
                "        t.nombre,\n" +
                "        ROW_NUMBER() OVER(PARTITION BY dbt.id_blog ORDER BY t.id_tag) AS row_num\n" +
                "    FROM\n" +
                "        detalle_blog_tags dbt\n" +
                "    INNER JOIN tags t ON dbt.id_tag = t.id_tag\n" +
                ") t ON b.id_blog = t.id_blog\n" +
                "WHERE\n" +
                "    b.publicado = TRUE\n" +
                "    AND t.row_num <= 2\n" +
                "    AND b.is_removed = FALSE\n" +
                "ORDER BY\n" +
                "    b.fecha_creacion";

        List<BlogResponse> blogList = new ArrayList<>();
        Map<Integer, BlogResponse> blogMap = new LinkedHashMap<>();

        jdbcTemplate.query(sql, (ResultSet rs) -> {
            int idBlog = rs.getInt("id_blog");
            if (!blogMap.containsKey(idBlog)) {
                BlogResponse blogResponse = new BlogResponse();
                blogResponse.setTitulo(rs.getString("titulo"));
                blogResponse.setBreve_descripcion(rs.getString("breve_descripcion"));
                blogResponse.setImg_banner(rs.getString("img_banner"));
                blogResponse.setFecha_creacion(rs.getString("fecha_creacion"));
                blogResponse.setSlug(rs.getString("slug"));

                User user = new User();
                user.setNombre_completo(rs.getString("nombre_completo"));
                user.setUsername(rs.getString("username"));
                user.setImagen_perfil(rs.getString("imagen_perfil"));
                blogResponse.setUser(user);

                List<Tags> tagsList = new ArrayList<>();
                Tags tag = new Tags();
                tag.setNombre(rs.getString("nombre"));
                tagsList.add(tag);
                blogResponse.setTags(tagsList);

                blogMap.put(idBlog, blogResponse);
            } else {
                Tags tag = new Tags();
                tag.setNombre(rs.getString("nombre"));
                blogMap.get(idBlog).getTags().add(tag);
            }
        });

        blogList.addAll(blogMap.values());

        int total = blogList.size();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), total);
        List<BlogResponse> pagedBlogs = blogList.subList(start, end);

        return new PageImpl<>(pagedBlogs, pageable, total);
    }

    @Override
    public BlogDetailResponse obtenerDetalleDelBlog(String slug, Integer idBlog, Integer idUsuario) {
        String SQL = "select \n" +
                "\tb.id_blog,\n" +
                "\tu.email,\n" +
                "\tu.nombre_completo,\n" +
                "\tu.imagen_perfil,\n" +
                "\tTO_CHAR(b.fecha_creacion, 'TMMon DD') AS fecha_creacion,\n" +
                "\tb.img_banner,\n" +
                "\tb.titulo,\n" +
                "\tb.contenido_blog,\n" +
                "\tb.img_portada,\n" +
                "\tb.vistas,\n" +
                "\tb.descripcion_img_portada,\n" +
                "\t(select count(*) from likesbyuser where id_blog = ? and is_like = true) as likes,\n" +
                "\t(select count(*) > 0 from likesbyuser where id_blog = ? and id_usuario = ? and is_like = true) as likedByUser,\n" +
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
            List<BlogDetailResponse> userProfileList = jdbcTemplate.query(SQL, new Object[]{idBlog, idBlog, idUsuario, slug}, new BlogDetailRowMapper());

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
            System.out.println("e.getMessage: " + e.getMessage());
            return null;
        }
    }

    private class BlogDetailRowMapper implements RowMapper<BlogDetailResponse> {
        public BlogDetailResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
            BlogDetailResponse blogDetailResponse = new BlogDetailResponse();
            blogDetailResponse.setId_blog(rs.getInt("id_blog"));
            blogDetailResponse.setEmail(rs.getString("email"));
            blogDetailResponse.setNombre_completo(rs.getString("nombre_completo"));
            blogDetailResponse.setImagen_perfil(rs.getString("imagen_perfil"));
            blogDetailResponse.setFecha_creacion(rs.getString("fecha_creacion"));
            blogDetailResponse.setImg_banner(rs.getString("img_banner"));
            blogDetailResponse.setTitulo(rs.getString("titulo"));
            blogDetailResponse.setContenido_blog(rs.getString("contenido_blog"));
            blogDetailResponse.setImg_portada(rs.getString("img_portada"));
            blogDetailResponse.setVistas(rs.getInt("vistas"));
            blogDetailResponse.setLikes(rs.getInt("likes"));
            blogDetailResponse.setDescripcion_img_portada(rs.getString("descripcion_img_portada"));
            blogDetailResponse.setLikedByUser(rs.getBoolean("likedByUser"));

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
    public int obtenerIdBlogBySlug(String slug) {
        String sql = "SELECT id_blog FROM blog WHERE slug = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, slug);
    }

    @Override
    public boolean existeLike(Integer idUsuario, Integer idBlog) {
        String sql = "SELECT is_like FROM likesbyuser WHERE id_usuario = ? AND id_blog = ?";
        try {
            Boolean isLike = jdbcTemplate.queryForObject(sql, new Object[]{idUsuario, idBlog}, Boolean.class);
            return isLike != null && isLike;
        } catch (EmptyResultDataAccessException e) {
            return false; // No hay resultado, consideramos que el like no existe
        }
    }

    @Override
    public int verificarSiDioLike(Integer idUsuario, Integer idBlog) {
        String SQL = "SELECT COUNT(*) FROM likesbyuser WHERE id_usuario = ? AND id_blog = ?";
        return jdbcTemplate.queryForObject(SQL, new Object[]{idUsuario, idBlog}, Integer.class);
    }

    @Override
    public int darLikePorPrimeraVez(Integer idUsuario, Integer idBlog) {
        String SQL = "INSERT INTO likesbyuser (id_usuario, id_blog, is_like) VALUES (?, ?, true)";
        return jdbcTemplate.update(SQL, idUsuario, idBlog);
    }

    @Override
    public boolean verEstadoDeLike(Integer userId, Integer blogId) {
        String sql = "SELECT is_like FROM likesbyuser WHERE id_usuario = ? AND id_blog = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{userId, blogId}, Boolean.class);
    }

    @Override
    public int agregarLike(Integer idUsuario, Integer idBlog) {
        String SQL = "UPDATE likesbyuser SET is_like = TRUE WHERE id_usuario = ? AND id_blog = ?";
        return jdbcTemplate.update(SQL, idUsuario, idBlog);
    }

    @Override
    public int quitarLike(Integer idUsuario, Integer idBlog) {
        String SQL = "UPDATE likesbyuser SET is_like = FALSE WHERE id_usuario = ? AND id_blog = ?";
        return jdbcTemplate.update(SQL, idUsuario, idBlog);
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

    @Override
    public int actualizarVistasDelBlog(String slug) {
        String SQL = "UPDATE blog SET vistas = vistas + 1 WHERE slug = ?";
        try {
            return jdbcTemplate.update(SQL, slug);
        } catch (DataAccessException e) {
            return 0; // Indicar que no se pudo actualizar
        }
    }

    @Override
    public int editarBlog(Integer idBlog, String breveDescripcion, String contenidoBlog, Boolean publicado) {
        String sql = "UPDATE blog SET breve_descripcion = ?, contenido_blog = ?, publicado = ? WHERE id_blog = ?";
        return jdbcTemplate.update(sql, breveDescripcion, contenidoBlog, publicado, idBlog);
    }

    @Override
    public int eliminarBlog(Integer idBlog) {
        String sql = "UPDATE blog SET is_removed = TRUE WHERE id_blog = ?";
        return jdbcTemplate.update(sql, idBlog);
    }
}
