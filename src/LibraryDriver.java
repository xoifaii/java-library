import java.util.Scanner;

public class LibraryDriver {
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
    System.out.println("7. Show average rating");
    System.out.println("8. Show highest-rated book");
    System.out.println("9. Quit");
    System.out.print("Enter option: ");

    if (!input.hasNextInt()) {
      input.nextLine();
      return 0;
    }

    int option = input.nextInt();
    input.nextLine();
    return option;
  }

  private void runMenu() {
    int option = mainMenu();
    while (option != 9) {
      processOption(option);
      System.out.println();
      option = mainMenu();
    }

    System.out.println("Exiting. Goodbye!");
    input.close();
  }

  private void processOption(int option) {
    switch (option) {
      case 1 -> addBook();
      case 2 -> borrowBook();
      case 3 -> returnBook();
      case 4 -> rateBook();
      case 5 -> listAllBooks();
      case 6 -> listAvailableBooks();
      case 7 -> showAverageRating();
      case 8 -> showHighestRatedBook();
      default -> System.out.println("Invalid option");
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

    String isbn = lib.generateUniqueIsbn(title, author);
    Book book = new Book(title, author, isbn);
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
    if (index == -1) {
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
    if (index == -1) {
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
    if (index == -1) {
      return;
    }

    double rating = getValidRating();
    if (rating == -1) {
      return;
    }

    lib.getBooks()[index].setRating(rating);
    System.out.println("Book rated successfully!");
  }

  private int getValidIndex(String prompt) {
    System.out.print(prompt);
    if (!input.hasNextInt()) {
      System.out.println("Invalid input. Please enter a number.");
      input.nextLine();
      return -1;
    }

    int index = input.nextInt();
    input.nextLine();

    Rules.ValidationResult result = Rules.inRange(0, lib.getBookCount() - 1).validate(index);
    if (!result.isSuccess()) {
      System.out.println(result.getMessage());
      return -1;
    }

    return index;
  }

  private double getValidRating() {
    System.out.print("Enter rating (0-5): ");
    if (!input.hasNextDouble()) {
      System.out.println("Invalid input. Please enter a number between 0 and 5.");
      input.nextLine();
      return -1;
    }

    double rating = input.nextDouble();
    input.nextLine();

    Rules.ValidationResult result = Rules.inRangeDouble(0, 5).validate(rating);
    if (!result.isSuccess()) {
      System.out.println(result.getMessage());
      return -1;
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