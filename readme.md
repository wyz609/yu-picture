`UserRoleEnum`枚举类详细介绍：

定义了一个枚举类 `UserRoleEnum`，用于表示用户角色。以下是代码的详细业务逻辑解析：

### 1. **枚举定义**
```java
USER("用户","user"),
ADMIN("管理员","admin");
```
- 定义了两个枚举常量：
    - `USER`：表示普通用户，中文描述为“用户”，对应的值为 `"user"`。
    - `ADMIN`：表示管理员用户，中文描述为“管理员”，对应的值为 `"admin"`。
- 每个枚举常量都包含两个属性：`text`（中文描述）和 `value`（实际值）。

---

### 2. **字段定义**
```java
private final String text;
private final String value;
```
- `text`：存储枚举常量的中文描述。
- `value`：存储枚举常量的实际值（如 `"user"` 或 `"admin"`）。
- 这两个字段都被声明为 `final`，表示它们的值在枚举实例化后不可更改。

---

### 3. **构造方法**
```java
UserRoleEnum(String text, String value) {
    this.text = text;
    this.value = value;
}
```
- 构造方法用于初始化枚举常量的 `text` 和 `value` 字段。
- 当定义枚举常量时（如 `USER("用户", "user")`），会调用此构造方法。

---

### 4. **静态方法：`getEnumByValue`**
```java
public static UserRoleEnum getEnumByValue(String value) {
    if (ObjUtil.isAllEmpty(value)) {
        return null;
    }

    for (UserRoleEnum anEnum : UserRoleEnum.values()) {
        if (anEnum.getValue().equals(value)) {
            return anEnum;
        }
    }
    return null;
}
```
- **功能**：根据传入的 `value` 值，返回对应的枚举常量。
- **逻辑**：
    1. 使用 `ObjUtil.isAllEmpty(value)` 检查传入的 `value` 是否为空。如果为空，直接返回 `null`。
    2. 遍历所有枚举常量（通过 `UserRoleEnum.values()` 获取枚举常量数组）。
    3. 比较每个枚举常量的 `value` 值是否与传入的 `value` 相等。
    4. 如果找到匹配的枚举常量，则返回该枚举常量。
    5. 如果遍历结束后没有找到匹配的枚举常量，则返回 `null`。

- **用途**：该方法可以用于根据数据库或外部输入的角色值（如 `"user"` 或 `"admin"`）获取对应的枚举常量。

---

### 5. **`@Getter` 注解**
- `@Getter` 是 Lombok 提供的注解，用于自动生成 `text` 和 `value` 字段的 getter 方法。
- 通过该注解，开发者无需手动编写 `getText()` 和 `getValue()` 方法。

---

### 6. **业务场景**
- **用户角色管理**：该枚举类可以用于系统中用户角色的管理。例如：
    - 在用户注册时，分配默认角色 `USER`。
    - 在权限校验时，根据用户的角色值（如 `"admin"`）判断是否具有管理员权限。
- **数据转换**：通过 `getEnumByValue` 方法，可以将外部输入的角色值（如 `"user"`）转换为枚举常量，便于后续逻辑处理。

---

### 总结
该代码的核心功能是定义用户角色的枚举类型，并提供了一个静态方法 `getEnumByValue`，用于根据角色值获取对应的枚举常量。这种设计可以提高代码的可读性和可维护性，同时避免硬编码角色值。


该代码实现了用户注册功能的业务逻辑，具体分为以下几个步骤：

### 1. 参数校验
代码首先对用户输入的参数进行校验，确保数据的合法性：
- 使用 `StrUtil.hasBlank` 检查 `userAccount`、`userPassword` 和 `checkPassword` 是否为空。如果为空，抛出 `BusinessException` 异常，提示“参数不能为空”。
- 检查 `userAccount` 的长度是否小于 4。如果小于 4，抛出异常，提示“账号长度不能小于4”。
- 检查 `userPassword` 和 `checkPassword` 的长度是否小于 8。如果小于 8，抛出异常，提示“密码长度不能小于8”。
- 检查 `userPassword` 和 `checkPassword` 是否一致。如果不一致，抛出异常，提示“两次密码不一致”。

### 2. 检查账号是否重复
- 使用 MyBatis-Plus 的 `QueryWrapper` 构造查询条件，检查数据库中是否已存在相同的 `userAccount`。
- 如果查询结果的数量大于 0，说明账号已存在，抛出异常，提示“账号已存在”。

### 3. 密码加密
- 调用 `getEncryptPassword` 方法对用户密码进行加密。
- 加密逻辑是将密码加上一个固定的盐值（`SALT`），然后使用 MD5 算法生成加密后的密码。

### 4. 数据存储
- 创建一个新的 `User` 对象，并设置以下属性：
  - `userAccount`：用户账号。
  - `userPassword`：加密后的密码。
  - `userName`：默认设置为“刘德华”。
  - `userRole`：默认设置为普通用户角色（`UserRoleEnum.USER.getValue()`）。
- 调用 `this.save(user)` 将用户信息保存到数据库。
- 如果保存失败，抛出异常，提示“注册失败”。

### 5. 返回用户 ID
- 如果保存成功，返回新用户的 ID。

### 加密方法 `getEncryptPassword`
- 该方法通过在原始密码前加上一个固定的盐值（`SALT`），然后使用 Spring 提供的 `DigestUtils.md5DigestAsHex` 方法对字符串进行 MD5 加密，生成加密后的密码。

### 业务逻辑总结
该代码的核心是实现用户注册功能，确保用户输入的参数合法、账号唯一，并对密码进行加密后存储到数据库中。通过一系列的校验和异常处理，保证了数据的完整性和安全性，同时使用了枚举类 `UserRoleEnum` 来管理用户角色，增强了代码的可读性和可维护性。