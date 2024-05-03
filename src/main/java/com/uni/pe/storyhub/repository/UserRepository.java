package com.uni.pe.storyhub.repository;

import com.uni.pe.storyhub.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository implements IUserRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean existeCorreo(String email) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE email = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count > 0;
    }

    @Override
    public boolean existeNombreUsuario(String username) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE username = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count > 0;
    }

    @Override
    public boolean registrarUsuario(String nombre_completo, String username, String correo, String contraseña, String imagen_perfil) {
        String sql = "INSERT INTO usuario(nombre_completo, username, email, contraseña, imagen_perfil) VALUES (?, ?, ?, ?, ?)";
        int rowsAffected = jdbcTemplate.update(sql, nombre_completo, username, correo, contraseña, imagen_perfil);
        return rowsAffected > 0;
    }

    @Override
    public String obtenerContraseñaPorEmail(String email) {
        String sql = "SELECT contraseña FROM usuario WHERE email = ?";
        return jdbcTemplate.queryForObject(sql, String.class, email);
    }

    @Override
    public UserResponse obtenerDataDelUsuarioPorEmail(String email) {
        String sql = "SELECT id_usuario, email, imagen_perfil FROM usuario WHERE email = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            UserResponse user = new UserResponse();
            user.setId_usuario(rs.getInt("id_usuario"));
            user.setEmail(rs.getString("email"));
            user.setImagen_perfil(rs.getString("imagen_perfil"));
            return user;
        }, email);
    }

    @Override
    public int actualizarFotoDePerfil(UpdatePhotoUser updatePhotoUser) {
        String sql = "UPDATE usuario SET imagen_perfil = ? WHERE email = ?";
        return jdbcTemplate.update(sql, updatePhotoUser.getImagen_perfil(), updatePhotoUser.getEmail());
    }

    @Override
    public int actualizarPerfil(UpdateProfileUser updateProfileUser) {
        String nombreCompleto = updateProfileUser.getNombre_completo();
        String username = updateProfileUser.getUsername();
        String descripcion = updateProfileUser.getDescripcion();
        String linkedin = updateProfileUser.getLinkedin();
        String telegram = updateProfileUser.getTelegram();
        String youtube = updateProfileUser.getYoutube();
        String instagram = updateProfileUser.getInstagram();
        String facebook = updateProfileUser.getFacebook();

        String email = updateProfileUser.getEmail();

        String sql = "UPDATE usuario \n" +
                "SET \n" +
                "\tnombre_completo = ?, \n" +
                "\tusername = ?, \n" +
                "\tdescripcion = ?, \n" +
                "\tlinkedin = ?, \n" +
                "\ttelegram = ?, \n" +
                "\tyoutube = ?, \n" +
                "\tinstagram = ?, \n" +
                "\tfacebook = ? \n" +
                "WHERE email = ?";
        return jdbcTemplate.update(sql, nombreCompleto, username, descripcion, linkedin, telegram, youtube, instagram, facebook, email);
    }

    /*@Override
    public List<UserProfileResponse> getUserProfile(String username) {
        String sql = "select DISTINCT \n" +
                "\tu.nombre_completo,\n" +
                "\tu.email,\n" +
                "\tu.descripcion,\n" +
                "\t'Se unió el ' || TO_CHAR(b.fecha_creacion, 'DD \"de\" TMMon \"del\" YYYY') AS fecha_creacion,\n" +
                "\tu.linkedin,\n" +
                "\tu.twitter,\n" +
                "\tu.instagram,\n" +
                "\tb.titulo,\n" +
                "\tb.breve_descripcion,\n" +
                "\tu.imagen_perfil,\n" +
                "\tu.nombre_completo,\n" +
                "\tTO_CHAR(b.fecha_creacion, 'TMMon DD, YYYY') AS fecha_creacion\n" +
                "from usuario u \n" +
                "inner join blog b \n" +
                "on u.id_usuario  =  b.id_usuario\n" +
                "where u.username = ? and b.publicado = true";

        System.out.println("username 2" + username);
        System.out.println("sql : " + sql);

        return jdbcTemplate.query(sql, new Object[]{username}, new BeanPropertyRowMapper<>(UserProfileResponse.class));
    }*/


    // Método para obtener el perfil de usuario con sus blogs
    @Override
    public UserProfileBlogResponse getUserProfile(String username) {
        String sql = "select DISTINCT \n" +
                "\tu.nombre_completo,\n" +
                "\tu.username,\n" +
                "\tu.email,\n" +
                "\tu.descripcion,\n" +
                "\t'Se unió el ' || TO_CHAR(u.fecha_creacion, 'DD \"de\" TMMon \"del\" YYYY') AS joined,\n" +
                "\tu.linkedin,\n" +
                "\tu.facebook,\n" +
                "\tu.instagram,\n" +
                "\tb.titulo,\n" +
                "\tb.slug,\n" +
                "\tb.img_banner,\n" +
                "\tb.breve_descripcion,\n" +
                "\tu.imagen_perfil,\n" +
                "\tTO_CHAR(b.fecha_creacion, 'TMMon DD, YYYY') AS fecha_creacion\n" +
                "from (select * from blog where publicado = true) b\n" +
                "right join usuario u\n" +
                "on u.id_usuario  =  b.id_usuario\n" +
                "where u.username = ?";

        try {
            List<UserProfileBlogResponse> userProfileList = jdbcTemplate.query(sql, new Object[]{username}, new UserProfileRowMapper());

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

    private class UserProfileRowMapper implements RowMapper<UserProfileBlogResponse> {
        public UserProfileBlogResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserProfileBlogResponse userProfile = new UserProfileBlogResponse();
            userProfile.setNombre_completo(rs.getString("nombre_completo"));
            userProfile.setUsername(rs.getString("username"));
            userProfile.setEmail(rs.getString("email"));
            userProfile.setDescripcion(rs.getString("descripcion"));
            userProfile.setFecha_creacion(rs.getString("joined"));
            userProfile.setLinkedin(rs.getString("linkedin"));
            userProfile.setFacebook(rs.getString("facebook"));
            userProfile.setInstagram(rs.getString("instagram"));
            userProfile.setImagen_perfil(rs.getString("imagen_perfil"));

            // Si el título del blog es null, significa que el usuario no tiene blogs
            if (rs.getString("titulo") == null) {
                userProfile.setBlog(new ArrayList<>()); // Establecer una lista vacía
            } else {
                // El usuario tiene al menos un blog, creamos una lista para almacenar los blogs
                List<BlogResponse> blogList = new ArrayList<>();
                do {
                    BlogResponse blog = new BlogResponse();
                    blog.setTitulo(rs.getString("titulo"));
                    blog.setBreve_descripcion(rs.getString("breve_descripcion"));
                    blog.setFecha_creacion(rs.getString("fecha_creacion"));
                    blog.setSlug(rs.getString("slug"));
                    blog.setImg_banner(rs.getString("img_banner"));
                    // No es necesario establecer el objeto User ya que este solo se usa cuando hay blogs
                    // Crear un objeto User y establecer los valores correspondientes
                    User user = new User();
                    user.setNombre_completo(rs.getString("nombre_completo"));
                    user.setImagen_perfil(rs.getString("imagen_perfil"));
                    blog.setUser(user);

                    blogList.add(blog);
                } while (rs.next());
                userProfile.setBlog(blogList);
            }

            return userProfile;
        }
    }

    @Override
    public GetUserProfileResponse obtenerPerfilDelUsuarioPorEmail(String email) {

        String sql = "SELECT imagen_perfil, email, nombre_completo, username, descripcion, linkedin, telegram, youtube, instagram, facebook FROM usuario WHERE email = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{email}, (rs, rowNum) ->
                new GetUserProfileResponse(
                        rs.getString("imagen_perfil"),
                        rs.getString("email"),
                        rs.getString("nombre_completo"),
                        rs.getString("username"),
                        rs.getString("descripcion"),
                        rs.getString("linkedin"),
                        rs.getString("telegram"),
                        rs.getString("youtube"),
                        rs.getString("instagram"),
                        rs.getString("facebook")
                ));
    }
}
