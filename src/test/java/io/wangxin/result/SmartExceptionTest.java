package io.wangxin.result;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * SmartException class test
 * Tests various constructors and methods of SmartException class
 * 
 * @author Test
 */
@DisplayName("SmartException Class Test")
public class SmartExceptionTest {

    @Test
    @DisplayName("Test constructor with error code and description")
    void testConstructorWithCodeAndDesc() {
        // Execute
        SmartException exception = new SmartException(404, "Resource not found");
        
        // Verify
        assertEquals(404, exception.getCode());
        assertEquals("Resource not found", exception.getDesc());
    }

    @Test
    @DisplayName("Test constructor with IFailCode interface")
    void testConstructorWithIFailCode() {
        // Prepare test error code
        TestFailCode testFailCode = new TestFailCode(500, "Internal system error");
        
        // Execute
        SmartException exception = new SmartException(testFailCode);
        
        // Verify
        assertEquals(testFailCode.getValue(), exception.getCode());
        assertEquals(testFailCode.getDesc(), exception.getDesc());
    }

    @Test
    @DisplayName("Test constructor with MyFailCode enum")
    void testConstructorWithMyFailCode() {
        // Execute
        SmartException exception = new SmartException(MyFailCode.USER_NOT_FOUND);
        
        // Verify
        assertEquals(MyFailCode.USER_NOT_FOUND.getValue(), exception.getCode());
        assertEquals(MyFailCode.USER_NOT_FOUND.getDesc(), exception.getDesc());
    }

    @Test
    @DisplayName("Test boundary value error codes")
    void testBoundaryErrorCodes() {
        // Test minimum error code
        SmartException minException = new SmartException(Integer.MIN_VALUE, "Minimum error code");
        assertEquals(Integer.MIN_VALUE, minException.getCode());
        
        // Test maximum error code
        SmartException maxException = new SmartException(Integer.MAX_VALUE, "Maximum error code");
        assertEquals(Integer.MAX_VALUE, maxException.getCode());
        
        // Test zero value error code
        SmartException zeroException = new SmartException(0, "Zero value error code");
        assertEquals(0, zeroException.getCode());
    }

    @Test
    @DisplayName("Test setter and getter methods")
    void testSettersAndGetters() {
        SmartException exception = new SmartException(100, "Initial description");
        
        // Test initial values
        assertEquals(100, exception.getCode());
        assertEquals("Initial description", exception.getDesc());
        
        // Test setter methods
        exception.setCode(200);
        exception.setDesc("Updated description");
        
        // Verify updated values
        assertEquals(200, exception.getCode());
        assertEquals("Updated description", exception.getDesc());
    }

    @Test
    @DisplayName("Test null value handling")
    void testNullValueHandling() {
        // Test null description
        SmartException exception = new SmartException(100, null);
        assertNull(exception.getDesc());
        
        // Test setting null description
        exception.setDesc("Valid description");
        assertEquals("Valid description", exception.getDesc());
        
        exception.setDesc(null);
        assertNull(exception.getDesc());
    }

    @Test
    @DisplayName("Test exception inheritance")
    void testExceptionInheritance() {
        SmartException exception = new SmartException(500, "System error");
        
        // Verify inheritance
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof SmartException);
        
        // Verify exception type
        assertEquals(SmartException.class, exception.getClass());
    }

    @Test
    @DisplayName("Test exception message")
    void testExceptionMessage() {
        String errorMessage = "This is an error message";
        SmartException exception = new SmartException(500, errorMessage);
        
        // Verify exception message
        assertEquals(errorMessage, exception.getDesc());
    }

    @Test
    @DisplayName("Test multiple exception instances")
    void testMultipleExceptionInstances() {
        SmartException exception1 = new SmartException(404, "Not found");
        SmartException exception2 = new SmartException(500, "System error");
        SmartException exception3 = new SmartException(400, "Bad request");
        
        // Verify each exception instance
        assertEquals(404, exception1.getCode());
        assertEquals("Not found", exception1.getDesc());
        
        assertEquals(500, exception2.getCode());
        assertEquals("System error", exception2.getDesc());
        
        assertEquals(400, exception3.getCode());
        assertEquals("Bad request", exception3.getDesc());
    }

    @Test
    @DisplayName("Test exception chaining")
    void testExceptionChaining() {
        // Create base exception
        RuntimeException baseException = new RuntimeException("Base exception");
        
        // Create SmartException
        SmartException smartException = new SmartException(500, "Smart exception");
        
        // Set exception cause
        smartException.initCause(baseException);
        
        // Verify exception chain
        assertNotNull(smartException.getCause());
        assertEquals(baseException, smartException.getCause());
        assertEquals("Base exception", smartException.getCause().getMessage());
    }

    @Test
    @DisplayName("Test exception stack trace")
    void testExceptionStackTrace() {
        SmartException exception = new SmartException(500, "Test exception");
        
        // Get stack trace
        StackTraceElement[] stackTrace = exception.getStackTrace();
        
        // Verify stack trace is not empty
        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);
        
        // Verify stack trace contains current test method
        boolean foundTestMethod = false;
        for (StackTraceElement element : stackTrace) {
            if (element.getMethodName().equals("testExceptionStackTrace")) {
                foundTestMethod = true;
                break;
            }
        }
        assertTrue(foundTestMethod, "Stack trace should contain test method");
    }

    @Test
    @DisplayName("Test exception serialization")
    void testExceptionSerialization() {
        SmartException originalException = new SmartException(500, "Serializable exception");
        
        // Verify exception implements Serializable interface
        assertTrue(originalException instanceof java.io.Serializable);
    }

    @Test
    @DisplayName("Test exception toString method")
    void testExceptionToString() {
        SmartException exception = new SmartException(500, "Test exception");
        String toString = exception.toString();
        
        // Verify toString contains exception information
        assertNotNull(toString);
        assertTrue(toString.contains("500"));
        assertTrue(toString.contains("Test exception"));
    }

    @Test
    @DisplayName("Test exception equals and hashCode methods")
    void testExceptionEqualsAndHashCode() {
        SmartException exception1 = new SmartException(404, "Not found");
        SmartException exception2 = new SmartException(404, "Not found");
        SmartException exception3 = new SmartException(500, "System error");
        
        // Test equals
        assertEquals(exception1, exception2);
        assertNotEquals(exception1, exception3);
        assertNotEquals(exception1, null);
        assertNotEquals(exception1, "String");
        
        // Test hashCode
        assertEquals(exception1.hashCode(), exception2.hashCode());
        assertNotEquals(exception1.hashCode(), exception3.hashCode());
    }

    /**
     * Test IFailCode implementation class
     */
    private static class TestFailCode implements IFailCode {
        private final int value;
        private final String desc;

        public TestFailCode(int value, String desc) {
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
}
