import java.io.*;
import java.util.*;

class Movie {
    String name;
    double imdbRating;
    int releaseYear;
    int popularity; // watch time popularity or simulated popularity

    // Constructor
    public Movie(String name, double imdbRating, int releaseYear, int popularity) {
        this.name = name;
        this.imdbRating = imdbRating;
        this.releaseYear = releaseYear;
        this.popularity = popularity;
    }

    @Override
    public String toString() {
        return String.format("%-45s | Rating: %.1f | Year: %d | Popularity: %d",
                name, imdbRating, releaseYear, popularity);
    }
}

class MovieRecommendationSystem {

    // quickSort algorithm
    public static void quickSort(List<Movie> movies, int low, int high, String parameter) {
        if (low < high) {
            int pi = partition(movies, low, high, parameter);
            quickSort(movies, low, pi - 1, parameter);
            quickSort(movies, pi + 1, high, parameter);
        }
    }
    private static int partition(List<Movie> movies, int low, int high, String parameter) {
        Movie pivot = movies.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (compare(movies.get(j), pivot, parameter)) {
                i++;
                Collections.swap(movies, i, j);
            }
        }
        Collections.swap(movies, i + 1, high);
        return i + 1;
    }

    private static boolean compare(Movie m1, Movie m2, String parameter) {
        switch (parameter.toLowerCase()) {
            case "rating":
                return m1.imdbRating > m2.imdbRating;
            case "year":
                return m1.releaseYear > m2.releaseYear;
            case "popularity":
                return m1.popularity > m2.popularity;
            default:
                return false;
        }
    }
    // CSV Reader
    public static List<Movie> loadMovies(String filePath) {
        List<Movie> movies = new ArrayList<>();
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                // Properly handle CSV with quoted commas
                String[] values = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                try {
                    String name = values[1].replace("\"", "");
                    int year = Integer.parseInt(values[2].replaceAll("[^0-9]", ""));
                    double rating = Double.parseDouble(values[6]);
                    int popularity = Integer.parseInt(values[14].replaceAll("[^0-9]", "")); // No_of_Votes

                    movies.add(new Movie(name, rating, year, popularity));
                } catch (Exception e) {
                    // Skip bad rows
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return movies;
    }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // Load dataset
        System.out.println("Loading movie dataset");
        List<Movie> movies = loadMovies("imdb_top_1000.csv");
        System.out.println("Total movies loaded: " + movies.size());
        // Get user input
        System.out.println("\nChoose sorting parameter:");
        System.out.println("1. rating");
        System.out.println("2. year");
        System.out.println("3. popularity");
        System.out.print("Enter your choice: ");
        String choice = sc.nextLine().trim().toLowerCase();

        // Validate input
        if (!(choice.equals("rating") || choice.equals("year") || choice.equals("popularity"))) {
            System.out.println("Invalid choice. Defaulting to rating.");
            choice = "rating";
        }
        // Sort using QuickSort
        long start = System.currentTimeMillis();
        quickSort(movies, 0, movies.size() - 1, choice);
        long end = System.currentTimeMillis();
        // Display top 10 recommended movies
        System.out.println("\nTop 10 Recommended Movies (sorted by " + choice + "):\n");
        for (int i = 0; i < Math.min(10, movies.size()); i++) {
            System.out.println(movies.get(i));
        }

        System.out.println("\nSorting completed in " + (end - start) + " ms.");
        sc.close();
    }
}
