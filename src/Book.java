
public class Book {
    private String title;
    private String author;
    private String isbn;
    private String genre;

    private boolean onLoan;
    private boolean isRated;

    private double rating;

    public Book(String title, String author, String genre, String isbn) {
        Rules.Validator<String> titleValidator = Rules.all(Rules.notNull(), Rules.maxLength(10));
        Rules.Validator<String> authorValidator = Rules.all(Rules.notNull(), Rules.maxLength(20));
        Rules.Validator<String> genreValidator = Rules.all(Rules.notNull(), Rules.maxLength(20));
        Rules.Validator<String> isbnValidator = Rules.exactLength(13);

        Rules.ValidationResult titleResult = titleValidator.validate(title);
        if (titleResult.isSuccess()) {
            this.title = title;
        } else {
            if (title == null) {
                this.title = "";
            } else {
                this.title = title.substring(0, Math.min(10, title.length()));
            }
        }

        Rules.ValidationResult authorResult = authorValidator.validate(author);
        if (authorResult.isSuccess()) {
            this.author = author;
        } else {
            if (author == null) {
                this.author = "";
            } else {
                this.author = author.substring(0, Math.min(20, author.length()));
            }
        }

        Rules.ValidationResult genreResult = genreValidator.validate(genre);
        if (genreResult.isSuccess()) {
            this.genre = genre;
        } else {
            if (genre == null) {
                this.genre = "Default";
            } else {
                this.genre = genre.substring(0, Math.min(20, genre.length()));
            }
        }

        Rules.ValidationResult isbnResult = isbnValidator.validate(isbn);
        if (isbnResult.isSuccess()) {
            this.isbn = isbn;
        } else {
            this.isbn = "0000000000000";
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
        Rules.ValidationResult result = Rules.all(Rules.notNull(), Rules.maxLength(10)).validate(title);
        if (result.isSuccess()) {
            this.title = title;
        }
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        Rules.ValidationResult result = Rules.all(Rules.notNull(), Rules.maxLength(20)).validate(author);
        if (result.isSuccess()) {
            this.author = author;
        }
    }

    public String getGenre() {
        return this.genre;
    }

    public void setGenre(String genre) {
        Rules.ValidationResult result = Rules.all(Rules.notNull(), Rules.maxLength(20)).validate(genre);
        if (result.isSuccess()) {
            this.genre = genre;
        }
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        Rules.ValidationResult result = Rules.exactLength(13).validate(isbn);
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
        Rules.ValidationResult result = Rules.inRangeDouble(0, 5).validate(rating);
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