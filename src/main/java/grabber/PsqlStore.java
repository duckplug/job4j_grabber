package grabber;

import utils.Post;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {

    private Connection connection;

    public PsqlStore(Properties config) throws SQLException {
        try {
            Class.forName(config.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        connection = DriverManager.getConnection(
                config.getProperty("url"),
                config.getProperty("username"),
                config.getProperty("password"));
    }

    private static Post postCreater(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setId(rs.getInt(1));
        post.setTitle(rs.getString(2));
        post.setDescription(rs.getString(3));
        post.setLink(rs.getString(4));
        post.setCreated(rs.getTimestamp(5).toLocalDateTime());
        return post;
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO post.post(title, description, created, link) VALUES(?, ?, ?, ?)"
                        + "ON CONFLICT(link) DO NOTHING")) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(post.getCreated()));
            ps.setString(4, post.getLink());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList();
        /*
        Для вызова таблицы не из шимы public,
        необходимо указывать имя_шимы.имя_таблицы
        post - имя таблицы и шимы.
         */
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM post.post")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Post post = postCreater(rs);
                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Post findById(int id) {
        Post post = new Post();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM post.post WHERE id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            rs.next();
            post = postCreater(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }
}