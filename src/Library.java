public class Library {
    private static final int ISBN_LENGTH = 13;
    private static final int DEFAULT_MAX_BOOKS = 1;
    private static final int MAX_SEARCH_TERM_LENGTH = 100;
    private static final int MAX_SEARCH_TYPE_LENGTH = 10;
    private static final int MAX_LIBRARY_NAME_LENGTH = 100;
    private static final String DEFAULT_LIBRARY_NAME = "Unnamed Library";
    private static final String[] VALID_SEARCH_TYPES = { "title", "author", "isbn", "genre", "any" };

    private String libraryName;
    private Book[] books;
    private int bookCount;
    private static long isbnCounter = 0;

    public Library(String libraryName, int maxBooks) {
        Rules.ValidationResult nameResult = Rules.notEmpty().validate(libraryName);
        if (nameResult.isSuccess()) {
            this.libraryName = libraryName;
        } else {
            this.libraryName = DEFAULT_LIBRARY_NAME;
        }

        Rules.ValidationResult maxBooksResult = Rules.positive().validate(maxBooks);
        if (maxBooksResult.isSuccess()) {
            this.books = new Book[maxBooks];
        } else {
            this.books = new Book[DEFAULT_MAX_BOOKS];
        }

        this.bookCount = 0;
    }

    public static Rules.ValidationResult validateLibraryName(String name) {
        return Rules.all(Rules.notEmpty(), Rules.maxLength(MAX_LIBRARY_NAME_LENGTH)).validate(name);
    }

    public static Rules.ValidationResult validateMaxBooks(String input) {
        return Rules.positiveInteger().validate(input);
    }

    public Rules.ValidationResult validateIndex(int index) {
        return Rules.inRange(0, bookCount - 1).validate(index);
    }

    // synchronized is needed here to prevent multiple libraries from generating the
    // same isbn.
    public synchronized String generateUniqueIsbn() {
        isbnCounter++;

        String counterStr = Long.toString(isbnCounter);
        int remaining = ISBN_LENGTH - counterStr.length();

        String nanoStr = Long.toString(System.nanoTime());

        // take middle digits to avoid time always being 0 at the end
        int start = Math.max(0, nanoStr.length() - remaining - 3);
        String filler = nanoStr.substring(start, start + remaining);

        return counterStr + filler;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public int getBookCount() {
        return bookCount;
    }

    public Book[] getBooks() {
        return books;
    }

    public void setBooks(Book[] books) {
        this.books = books;
    }

    public String listAllBooks() {
        if (isEmpty()) {
            return "No books in the library";
        }

        String list = "";
        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null) {
                list += i + ": " + books[i].toString() + "\n";
            } // this is o(n^2) without string builder :(
        }

        if (list.isEmpty()) {
            return "No books in the library";
        }

        return list;
    }

    public String listAvailableBooks() {
        if (isEmpty()) {
            return "No books in the library";
        }

        String list = "";
        boolean hasAvailable = false;
        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null && !books[i].isOnLoan()) {
                list += i + ": " + books[i].toString() + "\n";
                hasAvailable = true;
            } // Same
        }

        if (!hasAvailable) {
            return "No available books";
        }

        return list;
    }

    public int countBooksOnLoan() {
        int count = 0;
        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null && books[i].isOnLoan()) {
                count++;
            }
        }

        return count;
    }

    public double calculateAverageRating() {
        if (isEmpty()) {
            return 0;
        }

        double total = 0;
        int ratedCount = 0;
        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null && books[i].isRated()) {
                total += books[i].getRating();
                ratedCount++;
            }
        }

        if (ratedCount == 0) {
            return 0;
        }

        return total / ratedCount;
    }

    public Book findHighestRatedBook() {
        if (isEmpty()) {
            return null;
        }

        Book highest = null;
        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null) {
                if (highest == null && books[i].isRated()) {
                    highest = books[i];
                }

                if (highest != null && books[i].isRated() && books[i].getRating() > highest.getRating()) {
                    highest = books[i];
                }
            }
        }

        return highest;
    }

    public boolean addBook(Book book) {
        if (book == null) {
            return false;
        }
        if (isFull()) {
            return false;
        }

        books[bookCount] = book;
        bookCount++;
        return true;
    }

    private boolean isValidSearchType(String searchType) {
        for (String valid : VALID_SEARCH_TYPES) {
            if (valid.equals(searchType.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    public SearchResult<Book> findBooks(String searchTerm, String searchType) {
        Rules.ValidationResult termResult = Rules.all(
                Rules.notEmpty(),
                Rules.maxLength(MAX_SEARCH_TERM_LENGTH)).validate(searchTerm);
        if (!termResult.isSuccess()) {
            return SearchResult.failure("Search term: " + termResult.getMessage());
        }

        Rules.ValidationResult typeResult = Rules.all(
                Rules.notEmpty(),
                Rules.maxLength(MAX_SEARCH_TYPE_LENGTH)).validate(searchType);
        if (!typeResult.isSuccess()) {
            return SearchResult.failure("Search type: " + typeResult.getMessage());
        }

        if (!isValidSearchType(searchType)) {
            return SearchResult.failure("Invalid search type. Use: title, author, isbn, genre, or any");
        }

        if (isEmpty()) {
            return SearchResult.success(new Book[0]);
        }

        int matchCount = 0;
        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null && matchesSearch(books[i], searchTerm, searchType)) {
                matchCount++;
            }
        }

        if (matchCount == 0) {
            return SearchResult.success(new Book[0]);
        }

        Book[] results = new Book[matchCount];
        int resultIndex = 0;
        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null && matchesSearch(books[i], searchTerm, searchType)) {
                results[resultIndex] = books[i];
                resultIndex++;
            }
        }

        return SearchResult.success(results);
    }

    private boolean isStringInSearch(String str, String searchTerm) {
        return str.toLowerCase().contains(searchTerm.toLowerCase());
    }

    private boolean matchesSearch(Book book, String searchTerm, String searchType) {
        return switch (searchType.toLowerCase()) {
            case "title" ->
                isStringInSearch(book.getTitle(), searchTerm);

            case "author" ->
                isStringInSearch(book.getAuthor(), searchTerm);

            case "isbn" ->
                book.getIsbn().equals(searchTerm);

            case "genre" ->
                isStringInSearch(book.getGenre(), searchTerm);

            case "any" ->
                isStringInSearch(book.getTitle(), searchTerm) ||
                        isStringInSearch(book.getAuthor(), searchTerm) ||
                        book.getIsbn().equals(searchTerm) ||
                        isStringInSearch(book.getGenre(), searchTerm);

            default ->
                false;
        };
    }

    public boolean rateBook(int index, double rating) {
        if (index < 0 || index >= bookCount || books[index] == null) {
            return false;
        }

        return books[index].setRating(rating);
    }

    public boolean borrowBook(int index) {
        Rules.ValidationResult indexResult = Rules.inRange(0, bookCount - 1).validate(index);
        if (!indexResult.isSuccess()) {
            return false;
        }
        if (books[index] == null) {
            return false;
        }
        if (books[index].isOnLoan()) {
            return false;
        }

        books[index].borrowBook();
        return true;
    }

    public boolean returnBook(int index) {
        Rules.ValidationResult indexResult = Rules.inRange(0, bookCount - 1).validate(index);
        if (!indexResult.isSuccess()) {
            return false;
        }
        if (books[index] == null) {
            return false;
        }
        if (!books[index].isOnLoan()) {
            return false;
        }

        books[index].returnBook();
        return true;
    }

    public boolean isEmpty() {
        return bookCount == 0;
    }

    public boolean isFull() {
        return bookCount == books.length;
    }

    @Override
    public String toString() {
        return "Library: " + libraryName + " (Books: " + bookCount + "/" + books.length + ")";
    }
}