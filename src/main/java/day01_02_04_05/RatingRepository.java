package day01_02_04_05;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class RatingRepository {

    private DataSource dataSource;

    public RatingRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertRating(long movieId, List<Integer> ratings) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO ratings (movie_id, rating) VALUES (?, ?)")) {
                for (int actual : ratings) {
                    if (actual < 1 || actual > 5) {
                        throw new IllegalArgumentException("Invalid rating.");
                    }
                    stmt.setLong(1, movieId);
                    stmt.setLong(2, actual);
                    stmt.executeUpdate();
                }
                connection.commit();
            } catch (IllegalArgumentException iae) {
                connection.rollback();
            }
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot connect", sqle);
        }
    }
}
