package io.wangxin.result;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Result class test
 * Tests various methods and functionality of Result class
 * 
 * @author Test
 */
@DisplayName("Result Class Test")
public class ResultTest {

    private Result<String> result;
    private Result<Integer> intResult;

    @BeforeEach
    void setUp() {
        result = new Result<>();
        intResult = new Result<>();
    }

    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        Result<String> defaultResult = new Result<>();
        
        assertNotNull(defaultResult);
        assertEquals(0, defaultResult.getCode());
        assertNull(defaultResult.getMessage());
        assertNull(defaultResult.getData());
    }

    @Test
    @DisplayName("Test parameterized constructor")
    void testParameterizedConstructor() {
        Result<String> paramResult = new Result<>(404, "Not Found");
        
        assertEquals(404, paramResult.getCode());
        assertEquals("Not Found", paramResult.getMessage());
        assertNull(paramResult.getData());
    }

    @Test
    @DisplayName("Test isSuccess method - success status")
    void testIsSuccess_True() {
        result.setCode(0);
        
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("Test isSuccess method - failure status")
    void testIsSuccess_False() {
        result.setCode(500);
        
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("Test isSuccess method - boundary values")
    void testIsSuccess_BoundaryValues() {
        // Test various non-zero values
        result.setCode(1);
        assertFalse(result.isSuccess());
        
        result.setCode(-1);
        assertFalse(result.isSuccess());
        
        result.setCode(999);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("Test setter and getter methods")
    void testSettersAndGetters() {
        // Test code
        result.setCode(200);
        assertEquals(200, result.getCode());
        
        // Test message
        result.setMessage("Success");
        assertEquals("Success", result.getMessage());
        
        // Test data
        String testData = "Test data";
        result.setData(testData);
        assertEquals(testData, result.getData());
    }

    @Test
    @DisplayName("Test generic data types")
    void testGenericDataTypes() {
        // Test String type
        Result<String> stringResult = new Result<>();
        stringResult.setData("String data");
        assertEquals("String data", stringResult.getData());
        
        // Test Integer type
        Result<Integer> integerResult = new Result<>();
        integerResult.setData(123);
        assertEquals(123, integerResult.getData());
        
        // Test custom object type
        TestObject testObj = new TestObject("Test object", 100);
        Result<TestObject> objectResult = new Result<>();
        objectResult.setData(testObj);
        assertEquals(testObj, objectResult.getData());
    }

    @Test
    @DisplayName("Test null value handling")
    void testNullValueHandling() {
        // Test null code
        result.setCode(0);
        result.setMessage(null);
        result.setData(null);
        
        assertEquals(0, result.getCode());
        assertNull(result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Test boundary value code")
    void testBoundaryCodeValues() {
        // Test minimum integer value
        result.setCode(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, result.getCode());
        assertFalse(result.isSuccess());
        
        // Test maximum integer value
        result.setCode(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, result.getCode());
        assertFalse(result.isSuccess());
        
        // Test zero value
        result.setCode(0);
        assertEquals(0, result.getCode());
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("Test clone method")
    void testClone() throws CloneNotSupportedException {
        result.setCode(200);
        result.setMessage("Success");
        result.setData("Test data");
        
        Result<String> clonedResult = (Result<String>) result.clone();
        
        assertNotNull(clonedResult);
        assertEquals(result.getCode(), clonedResult.getCode());
        assertEquals(result.getMessage(), clonedResult.getMessage());
        assertEquals(result.getData(), clonedResult.getData());
        
        // Verify if it's deep copy or shallow copy
        assertNotSame(result, clonedResult);
    }

    @Test
    @DisplayName("Test equals and hashCode methods")
    void testEqualsAndHashCode() {
        Result<String> result1 = new Result<>();
        result1.setCode(200);
        result1.setMessage("Success");
        result1.setData("Data 1");
        
        Result<String> result2 = new Result<>();
        result2.setCode(200);
        result2.setMessage("Success");
        result2.setData("Data 1");
        
        Result<String> result3 = new Result<>();
        result3.setCode(404);
        result3.setMessage("Not Found");
        result3.setData("Data 2");
        
        // Test equals
        assertEquals(result1, result2);
        assertNotEquals(result1, result3);
        assertNotEquals(result1, null);
        assertNotEquals(result1, "String");
        
        // Test hashCode
        assertEquals(result1.hashCode(), result2.hashCode());
        assertNotEquals(result1.hashCode(), result3.hashCode());
    }

    @Test
    @DisplayName("Test toString method")
    void testToString() {
        result.setCode(200);
        result.setMessage("Success");
        result.setData("Test data");
        
        String toString = result.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("200"));
        assertTrue(toString.contains("Success"));
        assertTrue(toString.contains("Test data"));
    }

    @Test
    @DisplayName("Test concurrency safety")
    void testConcurrencySafety() throws InterruptedException {
        // Create multiple threads to operate on Result objects simultaneously
        Thread[] threads = new Thread[10];
        Result<Integer>[] results = new Result[10];
        
        for (int i = 0; i < 10; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                Result<Integer> threadResult = new Result<>();
                threadResult.setCode(index);
                threadResult.setMessage("Thread " + index);
                threadResult.setData(index * 100);
                results[index] = threadResult;
            });
        }
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Verify results
        for (int i = 0; i < 10; i++) {
            assertNotNull(results[i]);
            assertEquals(i, results[i].getCode());
            assertEquals("Thread " + i, results[i].getMessage());
            assertEquals(i * 100, results[i].getData());
        }
    }

    /**
     * Test object class
     */
    private static class TestObject {
        private String name;
        private int value;

        public TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() { return name; }
        public int getValue() { return value; }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TestObject that = (TestObject) obj;
            return value == that.value && name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(name, value);
        }
    }
}
