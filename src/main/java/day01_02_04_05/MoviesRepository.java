package day01_02_04_05;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MoviesRepository {

    private DataSource dataSource;

    public MoviesRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public long saveMovie(String title, LocalDate releaseDate) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO movies (title, release_date) VALUES (?, ?)",
                     Statement.RETURN_GENERATED_KEYS)){
            stmt.setString(1, title);
            stmt.setDate(2, Date.valueOf(releaseDate));
            stmt.executeUpdate();
            return executeAndGetGeneratedKey(stmt);
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot insert data!", sqle);
        }
    }

    public List<Movie> findAllMovies() {
        List<Movie> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM movies");
             ResultSet rs = stmt.executeQuery()){
            fillMovieList(result, rs);
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot insert data!", sqle);
        }
        return result;
    }

    public Optional<Movie> findMovieByTitle(String title) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM movies WHERE title = ?")){
            stmt.setString(1, title);
            return getMovie(stmt);
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot insert data!", sqle);
        }
    }

    private Optional<Movie> getMovie(PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            if ((rs.next())) {
                long id = rs.getLong("id");
                String title = rs.getString("title");
                LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
                return Optional.of(new Movie(id, title, releaseDate));
            }
        }
        return Optional.empty();
    }

    private void fillMovieList(List<Movie> result, ResultSet rs) throws SQLException {
        while (rs.next()) {
            Long id = rs.getLong("id");
            String title = rs.getString("title");
            LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
            result.add(new Movie(id, title, releaseDate));
        }
    }

    private long executeAndGetGeneratedKey(PreparedStatement stmt) {
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getLong(1);
            } else {
                throw new SQLException("No key has generated");
            }
        } catch (SQLException sqle) {
            throw new IllegalArgumentException("Error by insert", sqle);
        }
    }
}