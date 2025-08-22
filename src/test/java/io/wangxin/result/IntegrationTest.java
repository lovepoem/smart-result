package io.wangxin.result;

import io.wangxin.result.utils.ResultUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test class
 * Tests the collaboration and actual usage scenarios of the entire smart-result framework
 * 
 * @author Test
 */
@DisplayName("Smart Result Integration Test")
public class IntegrationTest {

    @Test
    @DisplayName("Test complete success flow")
    void testCompleteSuccessFlow() {
        // Simulate business data
        User user = new User("John", "john@example.com", 25);
        
        // Use ResultUtils to wrap success response
        Result<User> result = ResultUtils.wrapSuccess(user);
        
        // Verify result
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(0, result.getCode());
        assertEquals("", result.getMessage());
        assertEquals(user, result.getData());
        
        // Verify user data
        User returnedUser = result.getData();
        assertEquals("John", returnedUser.getName());
        assertEquals("john@example.com", returnedUser.getEmail());
        assertEquals(25, returnedUser.getAge());
    }

    @Test
    @DisplayName("Test complete failure flow")
    void testCompleteFailureFlow() {
        // Use MyFailCode to create failure response
        Result<Void> result = ResultUtils.wrapFailure(MyFailCode.USER_NOT_FOUND, "123");
        
        // Verify result
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(MyFailCode.USER_NOT_FOUND.getValue(), result.getCode());
        assertEquals("User not found: 123", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Test exception handling flow")
    void testExceptionHandlingFlow() {
        try {
            // Simulate throwing business exception
            throw new UserNotFoundException("User ID: 999");
        } catch (UserNotFoundException e) {
            // Use ResultUtils to wrap exception
            Result<Void> result = ResultUtils.wrapFailure(e.getCode(), e.getDesc());
            
            // Verify result
            assertNotNull(result);
            assertFalse(result.isSuccess());
            assertEquals(MyFailCode.USER_NOT_FOUND.getValue(), result.getCode());
            assertTrue(result.getMessage().contains("User ID: 999"));
        }
    }

    @Test
    @DisplayName("Test complete usage of error code enumeration")
    void testErrorCodeEnumCompleteUsage() {
        // Test various error scenarios
        testErrorScenario(MyFailCode.USER_ALREADY_EXISTS, "test@example.com");
        testErrorScenario(MyFailCode.USER_PASSWORD_TOO_SHORT, "4");
        testErrorScenario(MyFailCode.ORDER_NOT_FOUND, "ORDER-001");
        testErrorScenario(MyFailCode.PRODUCT_OUT_OF_STOCK, "PROD-001");
        testErrorScenario(MyFailCode.VALIDATION_FAILED, "Email format error");
        testErrorScenario(MyFailCode.EXTERNAL_SERVICE_UNAVAILABLE, "Payment service");
        testErrorScenario(MyFailCode.SYSTEM_MAINTENANCE);
    }

    @Test
    @DisplayName("Test complete lifecycle of Result object")
    void testResultObjectLifecycle() {
        // Create Result object
        Result<String> result = new Result<>();
        
        // Set initial values
        result.setCode(200);
        result.setMessage("Processing");
        result.setData("Initial data");
        
        // Verify initial state
        assertEquals(200, result.getCode());
        assertEquals("Processing", result.getMessage());
        assertEquals("Initial data", result.getData());
        assertFalse(result.isSuccess());
        
        // Update to success state
        result.setCode(0);
        result.setMessage("");
        result.setData("Final data");
        
        // Verify success state
        assertEquals(0, result.getCode());
        assertEquals("", result.getMessage());
        assertEquals("Final data", result.getData());
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("Test complete usage of SmartException")
    void testSmartExceptionCompleteUsage() {
        // Create custom exception
        SmartException exception = new SmartException(MyFailCode.USER_ACCOUNT_LOCKED);
        
        // Verify exception information
        assertEquals(MyFailCode.USER_ACCOUNT_LOCKED.getValue(), exception.getCode());
        assertEquals(MyFailCode.USER_ACCOUNT_LOCKED.getDesc(), exception.getDesc());
        
        // Use exception to create failure response
        Result<Void> result = ResultUtils.wrapFailure(exception.getCode(), exception.getDesc());
        
        // Verify response
        assertFalse(result.isSuccess());
        assertEquals(exception.getCode(), result.getCode());
        assertEquals(exception.getDesc(), result.getMessage());
    }

    @Test
    @DisplayName("Test boundary values and edge cases")
    void testBoundaryValuesAndEdgeCases() {
        // Test empty data
        Result<Void> emptyResult = ResultUtils.wrapSuccess();
        assertTrue(emptyResult.isSuccess());
        assertNull(emptyResult.getData());
        
        // Test null message
        Result<Void> nullMessageResult = ResultUtils.wrapFailure(100, null);
        assertEquals(100, nullMessageResult.getCode());
        assertNull(nullMessageResult.getMessage());
        
        // Test zero value error code
        Result<Void> zeroCodeResult = ResultUtils.wrapFailure(0, "Zero value error");
        assertEquals(0, zeroCodeResult.getCode());
        assertFalse(zeroCodeResult.isSuccess()); // Note: 0 is not a success code
        
        // Test maximum value
        Result<Void> maxCodeResult = ResultUtils.wrapFailure(Integer.MAX_VALUE, "Maximum value error");
        assertEquals(Integer.MAX_VALUE, maxCodeResult.getCode());
    }

    @Test
    @DisplayName("Test data type diversity")
    void testDataTypeDiversity() {
        // Test String type
        Result<String> stringResult = ResultUtils.wrapSuccess("String data");
        assertEquals("String data", stringResult.getData());
        
        // Test Integer type
        Result<Integer> intResult = ResultUtils.wrapSuccess(12345);
        assertEquals(12345, intResult.getData());
        
        // Test Boolean type
        Result<Boolean> boolResult = ResultUtils.wrapSuccess(true);
        assertEquals(true, boolResult.getData());
        
        // Test custom object type
        User user = new User("Jane", "jane@example.com", 30);
        Result<User> userResult = ResultUtils.wrapSuccess(user);
        assertEquals(user, userResult.getData());
        
        // Test collection type
        java.util.List<String> list = java.util.Arrays.asList("Item 1", "Item 2", "Item 3");
        Result<java.util.List<String>> listResult = ResultUtils.wrapSuccess(list);
        assertEquals(list, listResult.getData());
    }

    @Test
    @DisplayName("Test error code formatting functionality")
    void testErrorCodeFormatting() {
        // Test single parameter formatting
        Result<Void> result1 = ResultUtils.wrapFailure(MyFailCode.USER_NOT_FOUND, "123");
        assertEquals("User not found: 123", result1.getMessage());
        
        // Test multiple parameter formatting
        Result<Void> result2 = ResultUtils.wrapFailure(MyFailCode.USER_PASSWORD_TOO_SHORT, "8");
        assertEquals("Password too short, minimum 8 characters required", result2.getMessage());
        
        // Test no parameter formatting
        Result<Void> result3 = ResultUtils.wrapFailure(MyFailCode.USER_ACCOUNT_LOCKED);
        assertEquals(MyFailCode.USER_ACCOUNT_LOCKED.getDesc(), result3.getMessage());
    }

    /**
     * Helper method: Test error scenario
     */
    private void testErrorScenario(MyFailCode errorCode, String... params) {
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
    private static class User {
        private String name;
        private String email;
        private int age;

        public User(String name, String email, int age) {
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
            User user = (User) obj;
            return age == user.age && 
                   name.equals(user.name) && 
                   email.equals(user.email);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(name, email, age);
        }
    }

    /**
     * Test user not found exception
     */
    private static class UserNotFoundException extends SmartException {
        public UserNotFoundException(String userId) {
            super(MyFailCode.USER_NOT_FOUND, userId);
        }
    }
}
