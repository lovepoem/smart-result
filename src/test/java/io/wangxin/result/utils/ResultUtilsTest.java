package io.wangxin.result.utils;

import io.wangxin.result.IFailCode;
import io.wangxin.result.MyFailCode;
import io.wangxin.result.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ResultUtils utility class test
 * Tests the correctness of various response wrapper methods
 * 
 * @author Test
 */
@DisplayName("ResultUtils Utility Class Test")
public class ResultUtilsTest {

    private TestUser testUser;

    @BeforeEach
    void setUp() {
        testUser = new TestUser("John", "john@example.com", 25);
    }

    @Test
    @DisplayName("Test wrap success response - with data")
    void testWrapSuccessWithData() {
        // Execute
        Result<TestUser> result = ResultUtils.wrapSuccess(testUser);
        
        // Verify
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(0, result.getCode());
        assertEquals("", result.getMessage());
        assertEquals(testUser, result.getData());
    }

    @Test
    @DisplayName("Test wrap success response - without data")
    void testWrapSuccessWithoutData() {
        // Execute
        Result<Void> result = ResultUtils.wrapSuccess();
        
        // Verify
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(0, result.getCode());
        assertEquals("", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Test wrap failure response - using error code and message")
    void testWrapFailureWithCodeAndMessage() {
        // Execute
        Result<Void> result = ResultUtils.wrapFailure(400, "Bad Request");
        
        // Verify
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
        assertEquals("Bad Request", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Test wrap failure response - using IFailCode interface")
    void testWrapFailureWithIFailCode() {
        // Execute
        Result<Void> result = ResultUtils.wrapFailure(MyFailCode.USER_NOT_FOUND);
        
        // Verify
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(MyFailCode.USER_NOT_FOUND.getValue(), result.getCode());
        assertEquals(MyFailCode.USER_NOT_FOUND.getDesc(), result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Test wrap failure response - using IFailCode interface and dynamic parameters")
    void testWrapFailureWithIFailCodeAndParams() {
        // Execute
        Result<Void> result = ResultUtils.wrapFailure(MyFailCode.USER_NOT_FOUND, "123");
        
        // Verify
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(MyFailCode.USER_NOT_FOUND.getValue(), result.getCode());
        assertEquals("User not found: 123", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Test wrap failure response - using IFailCode interface and multiple dynamic parameters")
    void testWrapFailureWithIFailCodeAndMultipleParams() {
        // Execute
        Result<Void> result = ResultUtils.wrapFailure(MyFailCode.USER_PASSWORD_TOO_SHORT, "6");
        
        // Verify
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(MyFailCode.USER_PASSWORD_TOO_SHORT.getValue(), result.getCode());
        assertEquals("Password too short, minimum 6 characters required", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Test wrap failure response - using IFailCode interface and null parameters")
    void testWrapFailureWithIFailCodeAndNullParams() {
        // Execute
        Result<Void> result = ResultUtils.wrapFailure(MyFailCode.USER_ACCOUNT_LOCKED);
        
        // Verify
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(MyFailCode.USER_ACCOUNT_LOCKED.getValue(), result.getCode());
        assertEquals(MyFailCode.USER_ACCOUNT_LOCKED.getDesc(), result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Test wrap exception - using exception object")
    void testWrapExceptionWithException() {
        // Prepare test exception
        RuntimeException testException = new RuntimeException("Test exception");
        
        // Execute
        Result<Void> result = ResultUtils.wrapException(testException);
        
        // Verify
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(IFailCode.SYSTEM_EXCEPTION_CODE, result.getCode());
        assertEquals(IFailCode.SYSTEM_EXCEPTION_MSG, result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Test wrap exception - without exception object")
    void testWrapExceptionWithoutException() {
        // Execute
        Result<Void> result = ResultUtils.wrapException();
        
        // Verify
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(IFailCode.SYSTEM_EXCEPTION_CODE, result.getCode());
        assertEquals(IFailCode.SYSTEM_EXCEPTION_MSG, result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Test various error code wrappings")
    void testVariousErrorCodes() {
        // Test user related errors
        testErrorCode(MyFailCode.USER_ALREADY_EXISTS, "test@example.com");
        testErrorCode(MyFailCode.USER_INVALID_EMAIL, "invalid-email");
        
        // Test business logic errors
        testErrorCode(MyFailCode.ORDER_NOT_FOUND, "ORDER-001");
        testErrorCode(MyFailCode.PRODUCT_OUT_OF_STOCK, "PROD-001");
        
        // Test data validation errors
        testErrorCode(MyFailCode.VALIDATION_FAILED, "Field validation failed");
        testErrorCode(MyFailCode.REQUIRED_FIELD_MISSING, "Username");
        
        // Test external service errors
        testErrorCode(MyFailCode.EXTERNAL_SERVICE_UNAVAILABLE, "Payment service");
        testErrorCode(MyFailCode.DATABASE_CONNECTION_FAILED);
        
        // Test system level errors
        testErrorCode(MyFailCode.SYSTEM_MAINTENANCE);
        testErrorCode(MyFailCode.UNKNOWN_ERROR);
    }

    @Test
    @DisplayName("Test boundary value error codes")
    void testBoundaryErrorCodes() {
        // Test minimum error code
        Result<Void> result1 = ResultUtils.wrapFailure(1, "Minimum error code");
        assertEquals(1, result1.getCode());
        
        // Test maximum error code
        Result<Void> result2 = ResultUtils.wrapFailure(99999, "Maximum error code");
        assertEquals(99999, result2.getCode());
        
        // Test zero value error code
        Result<Void> result3 = ResultUtils.wrapFailure(0, "Zero value error code");
        assertEquals(0, result3.getCode());
    }

    @Test
    @DisplayName("Test special character messages")
    void testSpecialCharacterMessages() {
        String specialMessage = "Special characters: !@#$%^&*()_+-=[]{}|;':\",./<>?";
        Result<Void> result = ResultUtils.wrapFailure(1000, specialMessage);
        
        assertEquals(1000, result.getCode());
        assertEquals(specialMessage, result.getMessage());
    }

    @Test
    @DisplayName("Test empty message")
    void testEmptyMessage() {
        Result<Void> result = ResultUtils.wrapFailure(1000, "");
        
        assertEquals(1000, result.getCode());
        assertEquals("", result.getMessage());
    }

    @Test
    @DisplayName("Test null message")
    void testNullMessage() {
        Result<Void> result = ResultUtils.wrapFailure(1000, null);
        
        assertEquals(1000, result.getCode());
        assertNull(result.getMessage());
    }

    /**
     * Helper method: Test error code wrapping
     */
    private void testErrorCode(MyFailCode errorCode) {
        testErrorCode(errorCode, (String[]) null);
    }

    /**
     * Helper method: Test error code wrapping (with parameters)
     */
    private void testErrorCode(MyFailCode errorCode, String... params) {
        Result<Void> result = ResultUtils.wrapFailure(errorCode, params);
        
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(errorCode.getValue(), result.getCode());
        
        if (params != null && params.length > 0) {
            String expectedMessage = String.format(errorCode.getDesc(), (Object[]) params);
            assertEquals(expectedMessage, result.getMessage());
        } else {
            assertEquals(errorCode.getDesc(), result.getMessage());
        }
        
        assertNull(result.getData());
    }

    /**
     * Test user class
     */
    private static class TestUser {
        private String name;
        private String email;
        private int age;

        public TestUser(String name, String email, int age) {
            this.name = name;
            this.email = email;
            this.age = age;
        }

        public String getName() { return name; }
        public String getEmail() { return email; }
        public int getAge() { return age; }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TestUser testUser = (TestUser) obj;
            return age == testUser.age && 
                   name.equals(testUser.name) && 
                   email.equals(testUser.email);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(name, email, age);
        }
    }
}
