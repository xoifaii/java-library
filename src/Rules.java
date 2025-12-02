public class Rules {
    // Generic interface, T is a type parameter (placeholder for any type)
    // Functional interface (one abstract method) so lambdas can implement it
    public interface Validator<T> {
        ValidationResult validate(T value);
    }

    // Simple result container, not generic, just holds success/failure + message
    public static class ValidationResult {
        private final boolean success;
        private final String message;

        private ValidationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult failure(String message) {
            return new ValidationResult(false, message);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }

    // Returns Validator<String>, T is now String
    // Lambda implements the validate(String value) method
    public static Validator<String> notNull() {
        return value -> {
            if (value == null) {
                return ValidationResult.failure("Value cannot be null");
            }

            return ValidationResult.success();
        };
    }

    public static Validator<String> notEmpty() {
        return value -> {
            if (value == null) {
                return ValidationResult.failure("Value cannot be null");
            }

            if (value.isEmpty()) {
                return ValidationResult.failure("Value cannot be empty");
            }

            boolean allWhitespace = true;
            for (int i = 0; i < value.length(); i++) {
                if (!Character.isWhitespace(value.charAt(i))) {
                    allWhitespace = false;
                    i = value.length();
                }
            }

            if (allWhitespace) {
                return ValidationResult.failure("Value cannot be empty");
            }

            return ValidationResult.success();
        };
    }

    public static Validator<String> maxLength(int max) {
        return value -> {
            if (value == null) {
                return ValidationResult.failure("Value cannot be null");
            }

            if (value.length() > max) {
                return ValidationResult.failure("Value exceeds maximum length of " + max);
            }

            return ValidationResult.success();
        };
    }

    public static Validator<String> exactLength(int length) {
        return value -> {
            if (value == null) {
                return ValidationResult.failure("Value cannot be null");
            }

            if (value.length() != length) {
                return ValidationResult.failure("Value must be exactly " + length + " characters");
            }

            return ValidationResult.success();
        };
    }

    // Returns Validator<Integer>, T is Integer here
    public static Validator<Integer> positive() {
        return value -> {
            if (value == null) {
                return ValidationResult.failure("Value cannot be null");
            }
            if (value <= 0) {
                return ValidationResult.failure("Value must be positive");
            }

            return ValidationResult.success();
        };
    }

    public static Validator<Integer> inRange(int min, int max) {
        return value -> {
            if (value == null) {
                return ValidationResult.failure("Value cannot be null");
            }
            if (value < min || value > max) {
                return ValidationResult.failure("Value must be between " + min + " and " + max);
            }

            return ValidationResult.success();
        };
    }

    public static Validator<Double> inRangeDouble(double min, double max) {
        return value -> {
            if (value == null) {
                return ValidationResult.failure("Value cannot be null");
            }

            if (value < min || value > max) {
                return ValidationResult.failure("Value must be between " + min + " and " + max);
            }

            return ValidationResult.success();
        };
    }

    public static Validator<String> positiveInteger() {
        return value -> {
            if (value == null || value.length() > 9) {
                return ValidationResult.failure("Value must be a valid positive integer");
            }

            String str = value.trim();
            for (int i = 0; i < str.length(); i++) {
                if (!Character.isDigit(str.charAt(i))) {
                    return ValidationResult.failure("Value must be a valid positive integer");
                }
            }

            if (str.isEmpty() || Integer.parseInt(str) <= 0) {
                return ValidationResult.failure("Value must be a valid positive integer");
            }

            return ValidationResult.success();
        };
    }

    // Generic method, <T> declares its own type parameter
    // Takes multiple Validator<T> and returns a combined Validator<T>
    // Java infers T from the validators passed in
    // Example: all(notNull(), maxLength(10)) -> T inferred as String
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> Validator<T> all(Validator<T>... validators) {
        return value -> {
            for (Validator<T> validator : validators) {
                ValidationResult result = validator.validate(value);
                if (!result.isSuccess()) {
                    return result;
                }
            }

            return ValidationResult.success();
        };
    }

    // Wraps a validator to allow null values to pass
    public static <T> Validator<T> optional(Validator<T> validator) {
        return value -> {
            if (value == null) {
                return ValidationResult.success();
            }

            return validator.validate(value);
        };
    }

    // Creates validator from a custom function
    public static <T> Validator<T> custom(ValidationFunction<T> function) {
        return value -> {
            boolean isValid = function.test(value);
            if (isValid) {
                return ValidationResult.success();
            }

            return ValidationResult.failure("Custom validation failed");
        };
    }

    public static <T> Validator<T> customWithMessage(ValidationFunction<T> function, String message) {
        return value -> {
            boolean isValid = function.test(value);
            if (isValid) {
                return ValidationResult.success();
            }

            return ValidationResult.failure(message);
        };
    }

    // Another functional interface for custom validation logic
    @FunctionalInterface
    public interface ValidationFunction<T> {
        boolean test(T value);
    }
}