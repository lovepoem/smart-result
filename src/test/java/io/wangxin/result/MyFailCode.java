package io.wangxin.result;

/**
 * Test error code enumeration class
 * Implements IFailCode interface for testing various error scenarios
 * 
 * @author Test
 */
public enum MyFailCode implements IFailCode {
    
    // User related errors (1000-1999)
    USER_NOT_FOUND(1001, "User not found: %s"),
    USER_ALREADY_EXISTS(1002, "User already exists: %s"),
    USER_INVALID_EMAIL(1003, "Invalid email format: %s"),
    USER_PASSWORD_TOO_SHORT(1004, "Password too short, minimum %d characters required"),
    USER_ACCOUNT_LOCKED(1005, "Account is locked, please contact administrator"),
    
    // Business logic errors (2000-2999)
    ORDER_NOT_FOUND(2001, "Order not found: %s"),
    ORDER_ALREADY_PAID(2002, "Order already paid, cannot pay again"),
    ORDER_AMOUNT_INVALID(2003, "Invalid order amount: %s"),
    PRODUCT_OUT_OF_STOCK(2004, "Product out of stock: %s"),
    PAYMENT_FAILED(2005, "Payment failed: %s"),
    
    // Data validation errors (3000-3999)
    VALIDATION_FAILED(3001, "Data validation failed: %s"),
    REQUIRED_FIELD_MISSING(3002, "Required field missing: %s"),
    INVALID_FORMAT(3003, "Invalid format: %s"),
    STRING_TOO_LONG(3004, "String length exceeds limit: %s"),
    NUMBER_OUT_OF_RANGE(3005, "Number out of range: %s"),
    
    // External service errors (4000-4999)
    EXTERNAL_SERVICE_UNAVAILABLE(4001, "External service unavailable: %s"),
    EXTERNAL_SERVICE_TIMEOUT(4002, "External service timeout: %s"),
    EXTERNAL_SERVICE_ERROR(4003, "External service error: %s"),
    DATABASE_CONNECTION_FAILED(4004, "Database connection failed"),
    CACHE_SERVICE_ERROR(4005, "Cache service error: %s"),
    
    // System level errors (5000+)
    SYSTEM_MAINTENANCE(5001, "System under maintenance, please try again later"),
    SYSTEM_OVERLOAD(5002, "System overload, please try again later"),
    UNKNOWN_ERROR(9999, "Unknown error");

    private final int value;
    private final String desc;

    MyFailCode(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
