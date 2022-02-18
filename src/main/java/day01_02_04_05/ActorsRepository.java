package day01_02_04_05;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActorsRepository {

    private DataSource dataSource;

    public ActorsRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public long saveActor(String name) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("INSERT INTO actors (actor_name) VALUES (?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
            return executeAndGetGeneratedKey(stmt);
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot update: " + name, sqle);
        }
    }

    public List<String> findActorsWithPrefix(String prefix) {
        List<String> result = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT actor_name FROM actors WHERE actor_name LIKE ?")) {
            stmt.setString(1, prefix + "%");
            getActors(stmt, result);
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot query!", sqle);
        }
        return result;
    }

    public Optional<Actor> findActorByName(String name) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM actors WHERE actor_name = ?")) {
            stmt.setString(1, name);
            return getActor(stmt);
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot query!", sqle);
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

    private void getActors(PreparedStatement stmt, List<String> result) {
        try (ResultSet rs = stmt.executeQuery()) {
            while ((rs.next())) {
                String actorName = rs.getString("actor_name");
                result.add(actorName);
            }
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot find actor!", sqle);
        }
    }

    private Optional<Actor> getActor(PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            if ((rs.next())) {
                long id = rs.getLong("id");
                String actorName = rs.getString("actor_name");
                return Optional.of(new Actor(id, actorName));
            }
        }
        return Optional.empty();
    }
}