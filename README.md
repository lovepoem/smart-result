# Smart Result

[English](README.md) | [中文](README_zh.md)

A lightweight Java library for unified API response handling with comprehensive error management and best practices.

## Features

- 🚀 **Unified Response Format**: Standardized API response structure
- 🛡️ **Error Code Management**: Centralized error code definitions
- 🔧 **Utility Methods**: Rich helper methods for response creation
- 📝 **Exception Handling**: Smart exception with error codes
- 🎯 **Type Safety**: Generic support for any data type
- 📦 **Lightweight**: Minimal dependencies, easy to integrate

## Maven Dependency

## Code description
* Java generic implementation version of Golang with multiple return values (including error and data)
* Result entity wrapper class, including return value status, object, error information, etc

## maven dependency
>>>>>>> origin/master
```xml
<dependency>
    <groupId>io.wangxin</groupId>
    <artifactId>smart-result</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Quick Start

### Basic Usage
### Introduction to some classes
 
#### [Result](https://github.com/lovepoem/smart-result/blob/master/src/main/java/io/wangxin/result/Result.java)
    Unified api return result entity object

```java
// Success response with data
User user = new User("John", "john@example.com");
Result<User> result = ResultUtils.wrapSuccess(user);

// Success response without data
Result<Void> result = ResultUtils.wrapSuccess();

// Error response
Result<Void> result = ResultUtils.wrapFailure(400, "Bad Request");
```

### Response Format

```json
{
  "code": 0,
  "message": "",
  "data": {
    "name": "John",
    "email": "john@example.com"
  }
}
```

## Core Components

### [Result](src/main/java/io/wangxin/result/Result.java)
Unified API response wrapper class with generic support.

**Key Methods:**
- `isSuccess()`: Check if response is successful
- `getData()`: Get response data
- `getCode()`: Get response code
- `getMessage()`: Get response message

### [IFailCode](src/main/java/io/wangxin/result/IFailCode.java)
Abstract interface for error code definitions.

**Default Error Codes:**
- `SYSTEM_EXCEPTION_CODE`: 500 (System Exception)
- `OBJECT_NOT_FOUND`: 404 (Object Not Found)

### [ResultUtils](src/main/java/io/wangxin/result/utils/ResultUtils.java)
Utility class with rich methods for response creation and error handling.

### [SmartException](src/main/java/io/wangxin/result/SmartException.java)
Custom exception class that integrates with error codes.

## Best Practices

### 1. Custom Error Code Implementation

```java
public enum UserErrorCode implements IFailCode {
    USER_NOT_FOUND(1001, "User not found: %s"),
    INVALID_EMAIL(1002, "Invalid email format: %s"),
    DUPLICATE_USER(1003, "User already exists: %s");

    private final int value;
    private final String desc;

    UserErrorCode(int value, String desc) {
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
```

### 2. Service Layer Implementation

```java
@Service
public class UserService {
    
    public Result<User> getUserById(Long id) {
        try {
            User user = userRepository.findById(id);
            if (user == null) {
                return ResultUtils.wrapFailure(UserErrorCode.USER_NOT_FOUND, id.toString());
            }
            return ResultUtils.wrapSuccess(user);
        } catch (Exception e) {
            return ResultUtils.wrapException(e);
        }
    }
    
    public Result<User> createUser(UserCreateRequest request) {
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                return ResultUtils.wrapFailure(UserErrorCode.DUPLICATE_USER, request.getEmail());
            }
            
            User user = new User(request.getName(), request.getEmail());
            User savedUser = userRepository.save(user);
            return ResultUtils.wrapSuccess(savedUser);
        } catch (Exception e) {
            return ResultUtils.wrapException(e);
        }
    }
}
```

### 3. Controller Layer Implementation

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }
    
    @PostMapping
    public Result<User> createUser(@RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }
}
```

### 4. Exception Handling with Custom Error Codes

```java
public class UserNotFoundException extends SmartException {
    public UserNotFoundException(Long userId) {
        super(UserErrorCode.USER_NOT_FOUND, userId.toString());
    }
}

// Usage in service
public Result<User> getUserById(Long id) {
    User user = userRepository.findById(id);
    if (user == null) {
        throw new UserNotFoundException(id);
    }
    return ResultUtils.wrapSuccess(user);
}
```

### 5. Global Exception Handler

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(SmartException.class)
    public ResponseEntity<Result<Void>> handleSmartException(SmartException e) {
        Result<Void> result = ResultUtils.wrapFailure(e.getCode(), e.getDesc());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleGenericException(Exception e) {
        Result<Void> result = ResultUtils.wrapException(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
}
```

### 6. Response Validation

```java
public class ResponseValidator {
    
    public static <T> boolean isValid(Result<T> result) {
        return result != null && result.isSuccess();
    }
    
    public static <T> T getDataOrThrow(Result<T> result) {
        if (!isValid(result)) {
            throw new IllegalStateException("Invalid result: " + result.getMessage());
        }
        return result.getData();
    }
}

// Usage
Result<User> result = userService.getUserById(1L);
if (ResponseValidator.isValid(result)) {
    User user = result.getData();
    // Process user
}
```

### 7. Testing Best Practices

```java
@SpringBootTest
class UserServiceTest {
    
    @Test
    void getUserById_Success() {
        // Given
        Long userId = 1L;
        User expectedUser = new User("John", "john@example.com");
        
        // When
        Result<User> result = userService.getUserById(userId);
        
        // Then
        assertTrue(result.isSuccess());
        assertEquals(expectedUser, result.getData());
        assertEquals(0, result.getCode());
    }
    
    @Test
    void getUserById_NotFound() {
        // Given
        Long userId = 999L;
        
        // When
        Result<User> result = userService.getUserById(userId);
        
        // Then
        assertFalse(result.isSuccess());
        assertEquals(UserErrorCode.USER_NOT_FOUND.getValue(), result.getCode());
        assertTrue(result.getMessage().contains("999"));
    }
}
```

## Error Code Convention

- **0**: Success
- **1-999**: System level errors
- **1000-1999**: User/authentication errors
- **2000-2999**: Business logic errors
- **3000-3999**: Data validation errors
- **4000-4999**: External service errors
- **5000+**: Custom application errors

## Migration Guide

### From Manual Response Creation

**Before:**
```java
Map<String, Object> response = new HashMap<>();
response.put("success", true);
response.put("data", user);
response.put("message", "Success");
```

**After:**
```java
Result<User> result = ResultUtils.wrapSuccess(user);
```

### From Custom Exception Handling

**Before:**
```java
try {
    // business logic
} catch (Exception e) {
    logger.error("Error occurred", e);
    return ResponseEntity.status(500).body("Internal Server Error");
}
```

**After:**
```java
try {
    // business logic
} catch (Exception e) {
    return ResultUtils.wrapException(e);
}
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Support

If you have any questions or need help, please open an issue on GitHub or contact the maintainers.
