package day01_02_04_05;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ActorsMoviesRepository {

    private DataSource dataSource;

    public ActorsMoviesRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertActorAndMovieId(long actorId, long movieId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("INSERT INTO actors_movies (actor_id, movie_id) VALUES (?, ?)")) {
            stmt.setLong(1, actorId);
            stmt.setLong(2, movieId);
            stmt.executeUpdate();
        } catch (SQLException sqle) {
                 throw new IllegalStateException("Cannot insert row.", sqle);
        }
    }
}
