package day01_02_04_05;

import org.flywaydb.core.Flyway;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;


public class Main {

    public static void main(String[] args) {
        MariaDbDataSource dataSource = new MariaDbDataSource();
        try {
            dataSource.setUrl("jdbc:mariadb://localhost:3306/movies-actors?useUnicode=true");
            dataSource.setUser("root");
            dataSource.setPassword("diuska");
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot reach database!", sqle);
        }

        Flyway flyway = Flyway.configure().dataSource(dataSource).load();
        flyway.clean();
        flyway.migrate();

        ActorsRepository actorsRepository = new ActorsRepository(dataSource);
        actorsRepository.saveActor("Jack Doe");
        System.out.println(actorsRepository.findActorsWithPrefix("Jo"));

        MoviesRepository moviesRepository = new MoviesRepository(dataSource);
        moviesRepository.saveMovie("Titanic", LocalDate.of(1997, 12, 11));
        moviesRepository.saveMovie("Lord Of The Rings", LocalDate.of(2000, 12, 23));
        System.out.println(moviesRepository.findAllMovies());

        System.out.println(actorsRepository.saveActor("Johnny Depp"));
        System.out.println(actorsRepository.saveActor("Tom Hardy"));

        System.out.println(actorsRepository.findActorByName("Tom Hardy"));

        ActorsMoviesRepository actorsMoviesRepository = new ActorsMoviesRepository(dataSource);
        ActorsMoviesService actorsMoviesService = new ActorsMoviesService(actorsRepository, moviesRepository, actorsMoviesRepository);

        actorsMoviesService.insertMovieWithActors("Matrix", LocalDate.of(1999, 8, 5),
                List.of("Keanu Reeves", "Laurence Fishburne", "Carrie‑Anne Moss"));
        actorsMoviesService.insertMovieWithActors("John Wick", LocalDate.of(2014, 11, 13),
                List.of("Keanu Reeves", "Willem Dafoe", "Adrianne Palicki"));

        RatingRepository ratingRepository = new RatingRepository(dataSource);
        MoviesRatingsService moviesRatingsService = new MoviesRatingsService(moviesRepository, ratingRepository);

        moviesRatingsService.addRating("Titanic", 2, 4, 5);
        moviesRatingsService.addRating("Titanic", 3, 4, 5);
        moviesRatingsService.addRating("Matrix", 3, 4, 5, 5);
        moviesRatingsService.addRating("Matrix", 2, 4, 5, 6);
        moviesRatingsService.addRating("Lord Of The Rings", 5, 5, 4, 5);
    }
}