package com.uni.pe.storyhub.repository;

import com.uni.pe.storyhub.model.ComentarioBlog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Repository
public class ComentarioRepository implements IComentarioRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String QUERY =
            "WITH RECURSIVE comentarios_activos AS (\n" +
                    "    -- Seleccionamos todos los comentarios que no están eliminados y no tienen parent_comentario_id\n" +
                    "    SELECT \n" +
                    "        id_comentario, \n" +
                    "        parent_comentario_id\n" +
                    "    FROM \n" +
                    "        comentarios \n" +
                    "    WHERE \n" +
                    "        is_deleted = false\n" +
                    "        AND parent_comentario_id IS NULL\n" +
                    "\n" +
                    "    UNION ALL\n" +
                    "\n" +
                    "    -- Recorremos recursivamente los comentarios que tienen un parent_comentario_id que ya está en la lista de comentarios activos\n" +
                    "    SELECT \n" +
                    "        c.id_comentario, \n" +
                    "        c.parent_comentario_id\n" +
                    "    FROM \n" +
                    "        comentarios c\n" +
                    "    INNER JOIN \n" +
                    "        comentarios_activos ca \n" +
                    "    ON \n" +
                    "        c.parent_comentario_id = ca.id_comentario\n" +
                    "    WHERE \n" +
                    "        c.is_deleted = false\n" +
                    ")\n" +
                    "SELECT \n" +
                    "    u.id_usuario,\n" +
                    "\tu.email,\n" +
                    "\tu.nombre_completo,\n" +
                    "\tu.imagen_perfil,\n" +
                    "\tu.username,\n" +
                    "\tc.created_at AS fecha_creacion,\n" +
                    "    c.comentario,\n" +
                    "    c.score,\n" +
                    "    c.reply_to,\n" +
                    "    c.parent_comentario_id,\n" +
                    "    c.id_comentario\n" +
                    "FROM \n" +
                    "    blog b \n" +
                    "INNER JOIN \n" +
                    "    comentarios c \n" +
                    "ON \n" +
                    "    b.id_blog = c.id_blog \n" +
                    "INNER JOIN \n" +
                    "    usuario u\n" +
                    "ON \n" +
                    "    c.id_usuario = u.id_usuario\n" +
                    "INNER JOIN \n" +
                    "    comentarios_activos ca \n" +
                    "ON \n" +
                    "    c.id_comentario = ca.id_comentario\n" +
                    "WHERE \n" +
                    "    b.id_blog = ?\n" +
                    "ORDER BY \n" +
                    "    c.id_comentario ASC";

    @Override
    public List<ComentarioBlog> obtenerComentariosBlog(int idBlog) {
        return jdbcTemplate.query(QUERY, new Object[]{idBlog}, new ComentarioBlogMapper());
    }

    private static class ComentarioBlogMapper implements RowMapper<ComentarioBlog> {
        @Override
        public ComentarioBlog mapRow(ResultSet rs, int rowNum) throws SQLException {
            ComentarioBlog comentario = new ComentarioBlog();
            comentario.setId_usuario(rs.getInt("id_usuario"));
            comentario.setEmail(rs.getString("email"));
            comentario.setNombre_completo(rs.getString("nombre_completo"));
            comentario.setImagen_perfil(rs.getString("imagen_perfil"));
            comentario.setUsername(rs.getString("username"));
            comentario.setFecha_creacion(rs.getTimestamp("fecha_creacion"));
            comentario.setComentario(rs.getString("comentario"));
            comentario.setScore(rs.getInt("score"));
            comentario.setReply_to(rs.getString("reply_to"));
            Integer parentId = rs.getInt("parent_comentario_id");
            comentario.setParent_comentario_id(parentId == 0 ? null : parentId);
            comentario.setReplies(new ArrayList<>());
            comentario.setId_comentario(rs.getInt("id_comentario"));
            return comentario;
        }
    }

    @Override
    public int publicarComentario(String comentario, Integer parent_comentario_id, String reply_to, int idUsuario, int idBlog) {

        // Obtener la hora actual en la zona horaria de Lima
        ZonedDateTime nowLima = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("America/Lima"));
        Timestamp timestampLima = Timestamp.valueOf(nowLima.toLocalDateTime());

        // Nueva consulta para insertar el comentario
        String SQL = "insert into comentarios (comentario, parent_comentario_id, reply_to, id_usuario, id_blog, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        // Asumiendo valores fijos para id_user, reply_to y score como en el ejemplo, o reemplazarlos con los valores adecuados
        return jdbcTemplate.update(SQL, comentario, parent_comentario_id, reply_to, idUsuario, idBlog, timestampLima);
    }

    @Override
    public int actualizarComentario(String comentarioEdit, int idComentario) {
        String sql = "UPDATE comentarios SET comentario = ? WHERE id_comentario = ?";
        return jdbcTemplate.update(sql, comentarioEdit, idComentario);
    }

    @Override
    public int eliminarComentario(int idComentario) {
        String sql = "UPDATE comentarios SET is_deleted = TRUE WHERE id_comentario = ?";
        return jdbcTemplate.update(sql, idComentario);
    }
}
