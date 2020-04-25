# smart-result

## maven dependency
```xml
        <dependency>
            <groupId>io.wangxin</groupId>
            <artifactId>smart-result</artifactId>
            <version>1.0.0</version>
        </dependency>

```

## Code description
Result entity wrapper class, including return value status, object, error information, etc

### Introduction to some classes
 
#### [Result](https://github.com/lovepoem/smart-result/blob/master/src/main/java/io/wangxin/result/Result.java)
    Unified api return result entity object

#### [IFailCode](https://github.com/lovepoem/smart-result/blob/master/src/main/java/io/wangxin/result/IFailCode.java)
    Unified error code abstract interface, each project uses its own implementation enumeration class to put error code.

#### [ResultUtils](https://github.com/lovepoem/smart-result/blob/master/src/main/java/io/wangxin/result/utils/ResultUtils.java)
    The result encapsulation util tool is a rich method. Support error code enumeration class, error code plus exception information, exception and other error result encapsulation.

#### [SmartException](https://github.com/lovepoem/smart-result/blob/master/src/main/java/io/wangxin/result/utils/SecException.java)

    SmartException： Common Exception with  @see IFailCode

### Some conventions

* Use result and resultutils to encapsulate the API return value. It can return status value and corresponding result at the same time.
