import java.util.Scanner;

public class LibraryDriver {
    private static final int INVALID_INPUT = -1;
    private static final int MENU_QUIT = 10;
    private static final double MIN_RATING = 0;
    private static final double MAX_RATING = 5;

    private final Scanner input;
    private final Library lib;

    public static void main(String[] args) {
        new LibraryDriver();
    }

    public LibraryDriver() {
        input = new Scanner(System.in);
        String libraryName = getValidLibraryName();

        int maxBooks = getValidMaxBooks();
        lib = new Library(libraryName, maxBooks);

        runMenu();
    }

    private String getValidLibraryName() {
        System.out.print("Enter in the name of the Library: ");
        String name = input.nextLine();
        Rules.ValidationResult result = Rules.notEmpty().validate(name);

        while (!result.isSuccess()) {
            System.out.println(result.getMessage());
            System.out.print("Enter in the name of the Library: ");

            name = input.nextLine();
            result = Rules.notEmpty().validate(name);
        }

        return name;
    }

    private int getValidMaxBooks() {
        System.out.print("Enter the maximum number of books that the library can hold: ");
        while (!input.hasNextInt()) {
            System.out.println("Invalid input. Please enter a positive number.");
            input.nextLine();
            System.out.print("Enter the maximum number of books that the library can hold: ");
        }

        int maxBooks = input.nextInt();
        input.nextLine();

        Rules.ValidationResult result = Rules.positive().validate(maxBooks);
        while (!result.isSuccess()) {
            System.out.println(result.getMessage());
            System.out.print("Enter the maximum number of books that the library can hold: ");

            while (!input.hasNextInt()) {
                System.out.println("Invalid input. Please enter a positive number.");
                input.nextLine();
                System.out.print("Enter the maximum number of books that the library can hold: ");
            }

            maxBooks = input.nextInt();
            input.nextLine();
            result = Rules.positive().validate(maxBooks);
        }

        return maxBooks;
    }

    private int mainMenu() {
        System.out.println("==== Library Menu ====");
        System.out.println("1. Add new book");
        System.out.println("2. Borrow a book");
        System.out.println("3. Return a book");
        System.out.println("4. Rate a book");
        System.out.println("5. List all books");
        System.out.println("6. List available books");
        System.out.println("7. Find a book");
        System.out.println("8. Show average rating");
        System.out.println("9. Show highest-rated book");
        System.out.println("10. Quit");
        System.out.print("Enter option: ");

        if (!input.hasNextInt()) {
            input.nextLine();
            return INVALID_INPUT;
        }

        int option = input.nextInt();
        input.nextLine();
        return option;
    }

    private void runMenu() {
        int option = mainMenu();
        while (option != MENU_QUIT) {
            processOption(option);
            System.out.println();
            option = mainMenu();
        }

        System.out.println("Exiting. Goodbye!");
        input.close();
    }

    private void processOption(int option) {
        switch (option) {
            case 1 ->
                addBook();
            case 2 ->
                borrowBook();
            case 3 ->
                returnBook();
            case 4 ->
                rateBook();
            case 5 ->
                listAllBooks();
            case 6 ->
                listAvailableBooks();
            case 7 ->
                findBook();
            case 8 ->
                showAverageRating();
            case 9 ->
                showHighestRatedBook();
            default ->
                System.out.println("Invalid option");
        }
    }

    private void addBook() {
        if (lib.isFull()) {
            System.out.println("Library is full! Cannot add more books.");
            return;
        }

        System.out.print("Enter book title: ");
        String title = input.nextLine();

        Rules.ValidationResult titleResult = Rules.notEmpty().validate(title);
        if (!titleResult.isSuccess()) {
            System.out.println(titleResult.getMessage());
            return;
        }

        System.out.print("Enter book author: ");
        String author = input.nextLine();

        Rules.ValidationResult authorResult = Rules.notEmpty().validate(author);
        if (!authorResult.isSuccess()) {
            System.out.println(authorResult.getMessage());
            return;
        }

        System.out.print("Enter book genre: ");
        String genre = input.nextLine();

        Rules.ValidationResult genreResult = Rules.notEmpty().validate(genre);
        if (!genreResult.isSuccess()) {
            System.out.println(genreResult.getMessage());
            return;
        }

        String isbn = lib.generateUniqueIsbn();
        Book book = new Book(title, author, genre, isbn);
        if (lib.addBook(book)) {
            System.out.println("Book added successfully! ISBN: " + isbn);
        } else {
            System.out.println("Failed to add book");
        }
    }

    private void borrowBook() {
        if (lib.isEmpty()) {
            System.out.println("No books in library");
            return;
        }

        System.out.println(lib.listAllBooks());

        int index = getValidIndex("Enter index of book to borrow: ");
        if (index == INVALID_INPUT) {
            return;
        }

        if (lib.borrowBook(index)) {
            System.out.println("Book borrowed successfully!");
        } else {
            System.out.println("Book is already on loan");
        }
    }

    private void returnBook() {
        if (lib.isEmpty()) {
            System.out.println("No books in library");
            return;
        }
        if (lib.countBooksOnLoan() == 0) {
            System.out.println("No books are currently on loan");
            return;
        }

        System.out.println(lib.listAllBooks());

        int index = getValidIndex("Enter index of book to return: ");
        if (index == INVALID_INPUT) {
            return;
        }

        if (lib.returnBook(index)) {
            System.out.println("Book returned successfully!");
        } else {
            System.out.println("Book was not on loan");
        }
    }

    private void rateBook() {
        if (lib.isEmpty()) {
            System.out.println("No books in library");
            return;
        }

        System.out.println(lib.listAllBooks());

        int index = getValidIndex("Enter index of book to rate: ");
        if (index == INVALID_INPUT) {
            return;
        }

        double rating = getValidRating();
        if (rating == INVALID_INPUT) {
            return;
        }

        lib.rateBook(index, rating);
        System.out.println("Book rated successfully!");
    }

    private int getValidIndex(String prompt) {
        if (lib.isEmpty()) {
            System.out.println("No books in library");
            return INVALID_INPUT;
        }

        System.out.print(prompt);
        if (!input.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            input.nextLine();
            return INVALID_INPUT;
        }

        int index = input.nextInt();
        input.nextLine();

        Rules.ValidationResult result = Rules.inRange(0, lib.getBookCount() - 1).validate(index);
        if (!result.isSuccess()) {
            System.out.println(result.getMessage());
            return INVALID_INPUT;
        }

        return index;
    }

    private double getValidRating() {
        System.out.print("Enter rating (" + (int) MIN_RATING + "-" + (int) MAX_RATING + "): ");
        if (!input.hasNextDouble()) {
            System.out.println("Invalid input. Please enter a number between " + (int) MIN_RATING + " and " + (int) MAX_RATING + ".");
            input.nextLine();
            return INVALID_INPUT;
        }

        double rating = input.nextDouble();
        input.nextLine();

        Rules.ValidationResult result = Rules.inRangeDouble(MIN_RATING, MAX_RATING).validate(rating);
        if (!result.isSuccess()) {
            System.out.println(result.getMessage());
            return INVALID_INPUT;
        }

        return rating;
    }

    private void listAllBooks() {
        System.out.println(lib.listAllBooks());
    }

    private void listAvailableBooks() {
        System.out.println(lib.listAvailableBooks());
    }

    private void showAverageRating() {
        double avg = lib.calculateAverageRating();
        if (avg == 0) {
            System.out.println("No rated books in library");
        } else {
            System.out.println("Average rating: " + avg);
        }
    }

    private void findBook() {
        System.out.print("Search by (title/author/isbn/genre/any): ");
        String searchType = input.nextLine();

        System.out.print("Enter search term: ");
        String searchTerm = input.nextLine();

        SearchResult<Book> result = lib.findBooks(searchTerm, searchType);

        if (!result.isValid()) {
            System.out.println("Error: " + result.getMessage());
            return;
        }

        Book[] books = result.getItems();
        if (books.length == 0) {
            System.out.println("No books found matching the search.");
        } else {
            System.out.println("Search results:");
            for (int i = 0; i < books.length; i++) {
                System.out.println(i + ": " + books[i].toString());
            }
        }
    }

    private void showHighestRatedBook() {
        Book highest = lib.findHighestRatedBook();
        if (highest == null) {
            System.out.println("No rated books in library");
        } else {
            System.out.println("Highest rated book:");
            System.out.println(highest.toString());
        }
    }
}