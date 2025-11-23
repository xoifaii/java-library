# Library
A terminal based library system built with Java 25 LTS, since I wanted to test out the new features of Java 25.

## Features
- **Book Management**: Add, borrow, and return books
- **Rating System**: Rate books on a scale of 0-5
- **ISBN Generation**: Uses XXHash64 (non cryptographic hash) to generate unique ISBN numbers
- **Book Tracking**: View all books and filter by availability
- **Statistics**: Calculate average ratings and find highest-rated books

## Quick Start

### Compile

```batch
compile.bat
```

This will compile all Java source files and place the class files in the `bin` directory.

### Run
```batch
run.bat
```

## Menu Options
1. **Add new book** - Add a book to the library
2. **Borrow a book** - Mark a book as borrowed
3. **Return a book** - Return a borrowed book
4. **Rate a book** - Rate a book (0-5 stars)
5. **List all books** - Display all books in the library
6. **List available books** - Show only available books
7. **Show average rating** - Calculate average rating across all rated books
8. **Show highest-rated book** - Display the book with the highest rating
9. **Quit** - Exit the application