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

@Repository
public class ComentarioRepository implements IComentarioRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String QUERY =
            "select \n" +
                    "\tu.id_usuario,\n" +
                    "\tu.email,\n" +
                    "\tu.nombre_completo,\n" +
                    "\tu.imagen_perfil,\n" +
                    "\tTO_CHAR(c.created_at, 'Mon DD, YYYY - HH12:MI am') AS fecha_creacion,\n" +
                    "    c.comentario,\n" +
                    "    c.score,\n" +
                    "    c.reply_to,\n" +
                    "    c.parent_comentario_id,\n" +
                    "    c.id_comentario\n" +
                    "from comentarios c\n" +
                    "inner join blog b ON c.id_blog = b.id_blog\n" +
                    "inner join usuario u on c.id_usuario = u.id_usuario\n" +
                    "where b.id_blog = ? and c.is_deleted = false\n" +
                    "order by c.id_comentario asc";

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
            comentario.setFecha_creacion(rs.getString("fecha_creacion"));
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
        // Nueva consulta para insertar el comentario
        String SQL = "insert into comentarios (comentario, parent_comentario_id, reply_to, id_usuario, id_blog) " +
                "VALUES (?, ?, ?, ?, ?)";

        // Asumiendo valores fijos para id_user, reply_to y score como en el ejemplo, o reemplazarlos con los valores adecuados
        return jdbcTemplate.update(SQL, comentario, parent_comentario_id, reply_to, idUsuario, idBlog);
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
