public class Library {

  private String libraryName;
  private Book[] books;
  private int bookCount;
  private int isbnCounter;

  public Library(String libraryName, int maxBooks) {
    Rules.ValidationResult nameResult = Rules.notEmpty().validate(libraryName);
    if (nameResult.isSuccess()) {
      this.libraryName = libraryName;
    } else {
      this.libraryName = "Unnamed Library";
    }

    Rules.ValidationResult maxBooksResult = Rules.positive().validate(maxBooks);
    if (maxBooksResult.isSuccess()) {
      this.books = new Book[maxBooks];
    } else {
      this.books = new Book[1];
    }

    this.bookCount = 0;
    this.isbnCounter = 0;
  }

  public synchronized String generateUniqueIsbn(String title, String author) {
    if (isbnCounter == Integer.MAX_VALUE) {
      isbnCounter = 0;
    }
    isbnCounter = isbnCounter + 1;
    long timestamp = System.currentTimeMillis();
    String input = isbnCounter + title + author + timestamp;
    int hash = XXHash32.hash(input.getBytes(), isbnCounter);
    String hashHex = Integer.toHexString(hash);
    String isbn = "";
    for (int i = 0; i < hashHex.length() && isbn.length() < 13; i++) {
      char c = hashHex.charAt(i);
      if (c >= '0' && c <= '9') {
        isbn = isbn + c;
      }
    }
    while (isbn.length() < 13) {
      isbn = isbn + "0";
    }
    return isbn;
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
        list = list + i + ": " + books[i].toString() + "\n";
      }
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
        list = list + i + ": " + books[i].toString() + "\n";
        hasAvailable = true;
      }
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
        count = count + 1;
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
      if (books[i] != null && books[i].getRating() >= 1) {
        total = total + books[i].getRating();
        ratedCount = ratedCount + 1;
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
        if (highest == null || books[i].getRating() > highest.getRating()) {
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
    bookCount = bookCount + 1;
    return true;
  }

  public Book findBook(String isbn) {
    if (isbn == null) {
      return null;
    }
    for (int i = 0; i < bookCount; i++) {
      if (books[i] != null && books[i].getIsbn().equals(isbn)) {
        return books[i];
      }
    }
    return null;
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