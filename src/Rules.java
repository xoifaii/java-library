
public class Rules {
    // <T> is a generic type parameter, it's a placeholder for any type
    // When you use Validator<String>, T becomes String
    // When you use Validator<Integer>, T becomes Integer.

    public interface Validator<T> {

        ValidationResult validate(T value);
    }

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

    // Returns Validator<String> the <String> tells the compiler T = String
    // Java infers the lambda return type automatically from the method signature
    // A lambda expression is a short block of code that takes in parameters and
    // returns a value.
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

            if (value.trim().isEmpty()) {
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

    // Returns Validator<Integer> now T = Integer instead of String
    // The lambda parameter 'value' is inferred to be type Integer
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

    @SafeVarargs
    @SuppressWarnings("varargs")
    // <T> before the return type means this method itself is generic
    // T is inferred from the validators you pass in
    // If you pass Validator<String> validators, T becomes String automatically.
    // Pretty handy.
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

    public static <T> Validator<T> optional(Validator<T> validator) {
        return value -> {
            if (value == null) {
                return ValidationResult.success();
            }

            return validator.validate(value);
        };
    }

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

    @FunctionalInterface
    public interface ValidationFunction<T> {

        boolean test(T value);
    }
}
