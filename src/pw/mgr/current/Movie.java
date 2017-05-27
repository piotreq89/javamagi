package pw.mgr.current;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by piotrek on 2017-05-27.
 */
public class Movie {

    private String name ;
    private String location;
    private List<Movie> movieList = new ArrayList<>();

    public Movie(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public Movie() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Movie> getMovieList() {
        if(movieList.isEmpty()){
            movieList.add(new Movie("Film 1", "C:\\Users\\piotrek\\Desktop\\test\\magi_new9.mp4"));
            movieList.add(new Movie("Film 2", "C:\\Users\\piotrek\\Desktop\\test\\magi_new2.mp4"));
            movieList.add(new Movie("Film 3", "C:\\Users\\piotrek\\Desktop\\test\\magi_new3.mp4"));

        }
        return movieList;
    }

    public String getMovieLocationBaseOnName(String name){
        Optional<String> movieOptional = getMovieList().stream()
                .filter(m -> m.getName().equals(name))
                .findFirst()
                .map(Movie::getLocation);
        if(movieOptional.isPresent()){
            return movieOptional.get();
        }
        return "no movie ";
    }

    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Movie movie = (Movie) o;

        if (name != null ? !name.equals(movie.name) : movie.name != null) return false;
        return location != null ? location.equals(movie.location) : movie.location == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (location != null ? location.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "name='" + name + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
