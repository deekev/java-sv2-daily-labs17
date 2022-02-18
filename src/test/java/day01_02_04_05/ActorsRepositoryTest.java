package day01_02_04_05;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ActorsRepositoryTest {

    ActorsRepository actorsRepository;
    Flyway flyway;

    @BeforeEach
    void init() {
        MariaDbDataSource dataSource = new MariaDbDataSource();
        try {
            dataSource.setUrl("jdbc:mariadb://localhost:3306/movies-actors-test?useUnicode=true");
            dataSource.setUser("root");
            dataSource.setPassword("diuska");
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot reach database!", sqle);
        }

        flyway = Flyway.configure().dataSource(dataSource).load();
        flyway.clean();
        flyway.migrate();

        actorsRepository = new ActorsRepository(dataSource);
    }

    @Test
    void testInsert() {
        long id = actorsRepository.saveActor("John Doe");
        Optional<Actor> actor = actorsRepository.findActorByName("John Doe");

        assertEquals(id, actor.get().getId());
    }
}