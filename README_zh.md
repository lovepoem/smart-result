# Smart Result

[English](README.md) | [中文](README_zh.md)

一个轻量级的Java库，用于统一的API响应处理，具有全面的错误管理和最佳实践。

## 特性

- 🚀 **统一响应格式**: 标准化的API响应结构
- 🛡️ **错误代码管理**: 集中化的错误代码定义
- 🔧 **工具方法**: 丰富的响应创建辅助方法
- 📝 **异常处理**: 带有错误代码的智能异常
- 🎯 **类型安全**: 支持任何数据类型的泛型
- 📦 **轻量级**: 最小依赖，易于集成

## Maven 依赖

```xml
<dependency>
    <groupId>io.wangxin</groupId>
    <artifactId>smart-result</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 快速开始

### 基本用法

```java
// 带数据的成功响应
User user = new User("张三", "zhangsan@example.com");
Result<User> result = ResultUtils.wrapSuccess(user);

// 无数据的成功响应
Result<Void> result = ResultUtils.wrapSuccess();

// 错误响应
Result<Void> result = ResultUtils.wrapFailure(400, "请求参数错误");
```

### 响应格式

```json
{
  "code": 0,
  "message": "",
  "data": {
    "name": "张三",
    "email": "zhangsan@example.com"
  }
}
```

## 核心组件

### [Result](src/main/java/io/wangxin/result/Result.java)
支持泛型的统一API响应包装类。

**主要方法:**
- `isSuccess()`: 检查响应是否成功
- `getData()`: 获取响应数据
- `getCode()`: 获取响应代码
- `getMessage()`: 获取响应消息

### [IFailCode](src/main/java/io/wangxin/result/IFailCode.java)
错误代码定义的抽象接口。

**默认错误代码:**
- `SYSTEM_EXCEPTION_CODE`: 500 (系统异常)
- `OBJECT_NOT_FOUND`: 404 (对象未找到)

### [ResultUtils](src/main/java/io/wangxin/result/utils/ResultUtils.java)
具有丰富方法的响应创建和错误处理工具类。

### [SmartException](src/main/java/io/wangxin/result/SmartException.java)
与错误代码集成的自定义异常类。

## 最佳实践

### 1. 自定义错误代码实现

```java
public enum UserErrorCode implements IFailCode {
    USER_NOT_FOUND(1001, "用户未找到: %s"),
    INVALID_EMAIL(1002, "邮箱格式无效: %s"),
    DUPLICATE_USER(1003, "用户已存在: %s");

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

### 2. 服务层实现

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

### 3. 控制器层实现

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

### 4. 使用自定义错误代码的异常处理

```java
public class UserNotFoundException extends SmartException {
    public UserNotFoundException(Long userId) {
        super(UserErrorCode.USER_NOT_FOUND, userId.toString());
    }
}

// 在服务中使用
public Result<User> getUserById(Long id) {
    User user = userRepository.findById(id);
    if (user == null) {
        throw new UserNotFoundException(id);
    }
    return ResultUtils.wrapSuccess(user);
}
```

### 5. 全局异常处理器

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

### 6. 响应验证

```java
public class ResponseValidator {
    
    public static <T> boolean isValid(Result<T> result) {
        return result != null && result.isSuccess();
    }
    
    public static <T> T getDataOrThrow(Result<T> result) {
        if (!isValid(result)) {
            throw new IllegalStateException("无效的响应结果: " + result.getMessage());
        }
        return result.getData();
    }
}

// 使用示例
Result<User> result = userService.getUserById(1L);
if (ResponseValidator.isValid(result)) {
    User user = result.getData();
    // 处理用户数据
}
```

### 7. 测试最佳实践

```java
@SpringBootTest
class UserServiceTest {
    
    @Test
    void getUserById_Success() {
        // 给定条件
        Long userId = 1L;
        User expectedUser = new User("张三", "zhangsan@example.com");
        
        // 执行操作
        Result<User> result = userService.getUserById(userId);
        
        // 验证结果
        assertTrue(result.isSuccess());
        assertEquals(expectedUser, result.getData());
        assertEquals(0, result.getCode());
    }
    
    @Test
    void getUserById_NotFound() {
        // 给定条件
        Long userId = 999L;
        
        // 执行操作
        Result<User> result = userService.getUserById(userId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(UserErrorCode.USER_NOT_FOUND.getValue(), result.getCode());
        assertTrue(result.getMessage().contains("999"));
    }
}
```

## 错误代码约定

- **0**: 成功
- **1-999**: 系统级错误
- **1000-1999**: 用户/认证错误
- **2000-2999**: 业务逻辑错误
- **3000-3999**: 数据验证错误
- **4000-4999**: 外部服务错误
- **5000+**: 自定义应用错误

## 迁移指南

### 从手动响应创建迁移

**迁移前:**
```java
Map<String, Object> response = new HashMap<>();
response.put("success", true);
response.put("data", user);
response.put("message", "成功");
```

**迁移后:**
```java
Result<User> result = ResultUtils.wrapSuccess(user);
```

### 从自定义异常处理迁移

**迁移前:**
```java
try {
    // 业务逻辑
} catch (Exception e) {
    logger.error("发生错误", e);
    return ResponseEntity.status(500).body("内部服务器错误");
}
```

**迁移后:**
```java
try {
    // 业务逻辑
} catch (Exception e) {
    return ResultUtils.wrapException(e);
}
```

## 贡献指南

1. Fork 本仓库
2. 创建您的功能分支 (`git checkout -b feature/amazing-feature`)
3. 提交您的更改 (`git commit -m '添加一些很棒的功能'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 打开一个 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 支持

如果您有任何问题或需要帮助，请在 GitHub 上打开一个 issue 或联系维护者。
