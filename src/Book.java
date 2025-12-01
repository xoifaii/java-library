public class Book {
    private static final int MAX_TITLE_LENGTH = 10;
    private static final int MAX_AUTHOR_LENGTH = 20;
    private static final int MAX_GENRE_LENGTH = 20;
    private static final int ISBN_LENGTH = 13;

    private static final double MIN_RATING = 0;
    private static final double MAX_RATING = 5;

    private static final String DEFAULT_TITLE = "";
    private static final String DEFAULT_AUTHOR = "";
    private static final String DEFAULT_GENRE = "Default";
    private static final String DEFAULT_ISBN = "0000000000000";

    private String title;
    private String author;
    private String isbn;
    private String genre;

    private boolean onLoan;
    private boolean isRated;

    private double rating;

    public Book(String title, String author, String genre, String isbn) {
        Rules.Validator<String> titleValidator = Rules.all(Rules.notNull(), Rules.maxLength(MAX_TITLE_LENGTH));
        Rules.Validator<String> authorValidator = Rules.all(Rules.notNull(), Rules.maxLength(MAX_AUTHOR_LENGTH));
        Rules.Validator<String> genreValidator = Rules.all(Rules.notNull(), Rules.maxLength(MAX_GENRE_LENGTH));
        Rules.Validator<String> isbnValidator = Rules.exactLength(ISBN_LENGTH);

        Rules.ValidationResult titleResult = titleValidator.validate(title);
        if (titleResult.isSuccess()) {
            this.title = title;
        } else {
            if (title == null) {
                this.title = DEFAULT_TITLE;
            } else {
                this.title = title.substring(0, Math.min(MAX_TITLE_LENGTH, title.length()));
            }
        }

        Rules.ValidationResult authorResult = authorValidator.validate(author);
        if (authorResult.isSuccess()) {
            this.author = author;
        } else {
            if (author == null) {
                this.author = DEFAULT_AUTHOR;
            } else {
                this.author = author.substring(0, Math.min(MAX_AUTHOR_LENGTH, author.length()));
            }
        }

        Rules.ValidationResult genreResult = genreValidator.validate(genre);
        if (genreResult.isSuccess()) {
            this.genre = genre;
        } else {
            if (genre == null) {
                this.genre = DEFAULT_GENRE;
            } else {
                this.genre = genre.substring(0, Math.min(MAX_GENRE_LENGTH, genre.length()));
            }
        }

        Rules.ValidationResult isbnResult = isbnValidator.validate(isbn);
        if (isbnResult.isSuccess()) {
            this.isbn = isbn;
        } else {
            this.isbn = DEFAULT_ISBN;
        }

        this.onLoan = false;
        this.rating = 0;
    }

    // Some setters/getters are simply here for future proofing
    // Updating title/author/genre/isbn is not currently a requirement
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        Rules.ValidationResult result = Rules.all(Rules.notNull(), Rules.maxLength(MAX_TITLE_LENGTH)).validate(title);
        if (result.isSuccess()) {
            this.title = title;
        }
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        Rules.ValidationResult result = Rules.all(Rules.notNull(), Rules.maxLength(MAX_AUTHOR_LENGTH)).validate(author);
        if (result.isSuccess()) {
            this.author = author;
        }
    }

    public String getGenre() {
        return this.genre;
    }

    public void setGenre(String genre) {
        Rules.ValidationResult result = Rules.all(Rules.notNull(), Rules.maxLength(MAX_GENRE_LENGTH)).validate(genre);
        if (result.isSuccess()) {
            this.genre = genre;
        }
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        Rules.ValidationResult result = Rules.exactLength(ISBN_LENGTH).validate(isbn);
        if (result.isSuccess()) {
            this.isbn = isbn;
        }
    }

    public boolean isOnLoan() {
        return onLoan;
    }

    public void borrowBook() {
        this.onLoan = true;
    }

    public void returnBook() {
        this.onLoan = false;
    }

    public double getRating() {
        return rating;
    }

    public boolean isRated() {
        return isRated;
    }

    public void setRating(double rating) {
        Rules.ValidationResult result = Rules.inRangeDouble(MIN_RATING, MAX_RATING).validate(rating);
        if (result.isSuccess()) {
            this.rating = rating;
            this.isRated = true;
        }
    }

    @Override
    public String toString() {
        return "Title: " + title + " | Author: " + author + " | Genre: " + genre
                + " | ISBN: " + isbn + " | OnLoan: " + (onLoan ? "Yes" : "No")
                + " | Rating: " + (int) rating + " stars";
    }
}