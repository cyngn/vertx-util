package com.cyngn.vertx.validation;

/**
 * Validation Result object for validate method for request objects.
 *
 * @author asarda@cyngn.com (Ajay Sarda) 9/8/15.
 */
public class ValidationResult {
    public static ValidationResult SUCCESS = new ValidationResult(true, null);

    public boolean valid;
    public String errorMsg;

    public ValidationResult(boolean valid, String errorMsg) {
        this.valid = valid;
        this.errorMsg = errorMsg;
    }
}
