// TODO: Add levenshtein distance for fuzzy searching?

public class SearchResult<T> {
    private final T[] items;
    private final Rules.ValidationResult validation;

    private SearchResult(T[] items, Rules.ValidationResult validation) {
        this.items = items;
        this.validation = validation;
    }

    public static <T> SearchResult<T> success(T[] items) {
        return new SearchResult<>(items, Rules.ValidationResult.success());
    }

    public static <T> SearchResult<T> failure(String message) {
        return new SearchResult<>(null, Rules.ValidationResult.failure(message));
    }

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