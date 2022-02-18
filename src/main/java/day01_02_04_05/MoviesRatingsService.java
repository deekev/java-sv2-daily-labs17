package day01_02_04_05;

import java.util.Arrays;
import java.util.Optional;

public class MoviesRatingsService {

    private MoviesRepository moviesRepository;
    private RatingRepository ratingRepository;

    public MoviesRatingsService(MoviesRepository moviesRepository, RatingRepository ratingRepository) {
        this.moviesRepository = moviesRepository;
        this.ratingRepository = ratingRepository;
    }

    public void addRating(String title, Integer ... ratings) {
        Optional<Movie> actual = moviesRepository.findMovieByTitle(title);
        if (actual.isPresent()) {
            ratingRepository.insertRating(actual.get().getId(), Arrays.asList(ratings));
        } else {
            throw new IllegalArgumentException("Cannot find movie: " + title);
        }
    }
}