// Generic class - T is the type of items in the search result
// Example: SearchResult<Book> holds Book[] items
public class SearchResult<T> {
    private final T[] items;
    private final Rules.ValidationResult validation;

    private SearchResult(T[] items, Rules.ValidationResult validation) {
        this.items = items;
        this.validation = validation;
    }

    // Static factory method, needs its own <T> declaration
    // Static methods can't access the class's T, so they declare their own
    // The <> in new SearchResult<>() is the diamond operator, Java infers the type
    public static <T> SearchResult<T> success(T[] items) {
        return new SearchResult<>(items, Rules.ValidationResult.success());
    }

    // Returns SearchResult with null items and error message
    // <T> declared here too since it's static
    public static <T> SearchResult<T> failure(String message) {
        return new SearchResult<>(null, Rules.ValidationResult.failure(message));
    }

    // Instance method, can use class's T directly, no declaration needed
    public T[] getItems() {
        return items;
    }

    public boolean isValid() {
        return validation.isSuccess();
    }

    public String getMessage() {
        return validation.getMessage();
    }
}