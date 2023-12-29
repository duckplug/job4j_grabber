package grabber;

import utils.Post;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
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

    @Override
    public void save(Post post) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO post.post(title, description, created, link) VALUES(?, ?, ?, ?)")) {
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
                Post post = new Post();
                post.setId(rs.getInt(1));
                post.setTitle(rs.getString(2));
                post.setDescription(rs.getString(3));
                post.setLink(rs.getString(4));
                post.setCreated(rs.getTimestamp(5).toLocalDateTime());
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
            post.setId(rs.getInt(1));
            post.setTitle(rs.getString(2));
            post.setDescription(rs.getString(3));
            post.setLink(rs.getString(4));
            post.setCreated(rs.getTimestamp(5).toLocalDateTime());
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

    public static void main(String[] args) throws SQLException {
        Properties prop = new Properties();
        try (InputStream in = PsqlStore.class.getClassLoader()
                .getResourceAsStream("grabber.properties")) {
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PsqlStore ps = new PsqlStore(prop);
        LocalDateTime ldt = LocalDateTime.of(2024, 01, 12, 12, 12);
        Post post1 = new Post();
        post1.setLink("link_1");
        post1.setDescription("description_1");
        post1.setCreated(ldt);
        post1.setTitle("POST-1");

        Post post2 = new Post();
        post1.setLink("link_2");
        post1.setDescription("description_2");
        post1.setCreated(ldt);
        post1.setTitle("POST-2");

        ps.save(post1);
        ps.save(post2);

        List<Post> list =  ps.getAll();
        for (Post p : list) {
            System.out.println(p.toString());
        }
        System.out.println(ps.findById(1));
    }
}
