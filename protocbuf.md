# Protocol Buffers

### 使用protocol buffers的步骤

- 在.proto文件中定义一个消息结构
- 使用protocol buffer编译器对其编译
- 使用java protocol buffer api去读写消息

### 为什么使用protocol buffers

你如何序列化和获取结构化数据，以下是一些解决方案：

- 使用java 序列化。这是java默认的方式，但是他有一个臭名昭著的问题，无法实现跨语言共享数据。
- 你可以发明一种将数据项编码为单个字符串的临时方法-比如编码4个ints成"12:3:-23:67"，这个方法很简单也易于扩展，在编写一次性的编码和解码，解码会花费一些运行时性能，这对于简单的程序很有效。
- 序列化数据到xml，由于XML是人类（一种）可读的，并且存在用于多种语言的绑定库，因此该方法可能非常有吸引力。如果要与其他应用程序/项目共享数据，这可能是一个不错的选择。但是，众所周知，XML占用大量空间，对它进行编码/解码会给应用程序带来巨大的性能损失。 而且，导航XML DOM树比通常导航类中的简单字段要复杂得多。

protocol buffers是灵活，高效，自动化的解决方案，可以准确地解决此问题，使用protocol buffers你可以编写要存储的数据结构的.proto描述。 由此，protocol buffers编译器创建了一个类，该类以有效的二进制格式实现protocol buffers的自动编码和解析。 生成的类为构成protocol buffers字段提供获取器和设置器，并以协议为单位来详细阅读和写入protocol buffers。 重要的是，协protocol buffers格式支持随时间扩展格式的想法，以使代码仍可以读取以旧格式编码的数据。

### 定义你的Protocol格式

```
syntax = "proto2";

package tutorial;

option java_package = "com.example.tutorial";
option java_outer_classname = "AddressBookProtos";

message Person {
  required string name = 1;
  required int32 id = 2;
  optional string email = 3;

  enum PhoneType {
    MOBILE = 0;
    HOME = 1;
    WORK = 2;
  }

  message PhoneNumber {
    required string number = 1;
    optional PhoneType type = 2 [default = HOME];
  }

  repeated PhoneNumber phones = 4;
}

message AddressBook {
  repeated Person people = 1;
}
```

这个.proto文件以声明一个package开始，这个可以防止在不同的项目中命名冲突，这个package名称会被应用到java package中，除非你指定了java_package，即使你定义了一个java_package，你也任然应该定义一个普通的package，以避免 Protocol Buffers的命名空间 冲突。

package声明之后，你可以看到两个java格式的选项：java_package和java_outer_classname。java_package指定了你生成的类应该放在哪个包下。如果你没有特殊的指定，他会默认使用package所指定的路径。但是这个名字和java包名相差甚远。java_outer_classname选项定义了类名，该类名应包含此文件中的所有类。如果你未明确给出java_outer_classname，它将通过将文件名转换为驼峰大小写来生成。 例如，默认情况下，“ my_proto.proto”将使用“ MyProto”作为外部类名称。

接下来是一个消息定义。消息只是包含一组类型字段的汇总。许多标准的简单数据类型可用作字段类型，包括bool`, `int32`, `float`, `double`, 和 `string。你可以添加更多的结构到你的消息中，通过使用其他消息类型作为属性。以上类型中，person 消息糙汉了PhoneNumber消息，AddressBook message包含了Person message。您甚至可以定义嵌套在其他消息中的消息类型- 正如你所见，PhoneNumber被定义在Person中。你也可以定义enum类型如果你希望您的一个字段具有一个预定义的值列表之一。

每个元素上的“ = 1”，“ = 2”标记标识该字段在二进制编码中使用的唯一“标记”。标记1-15会比更高数字（16以上少一个字节）

每个字段都必须使用以下修饰符之一进行注释（尽量不使用`required`）：

- `required`:必须提供该字段的值，否则该消息将被视为“未初始化”。 尝试构建未初始化的消息将引发RuntimeException。 解析未初始化的消息将引发IOException。 除此之外，必填字段的行为与可选字段完全相同。
- `optional`: 该字段可以设置也可以不设置。 如果未设置可选字段值，则使用默认值。 对于简单类型，您可以指定自己的默认值，就像在示例中为电话号码类型所做的那样。 否则，将使用系统默认值：数字类型为零，字符串为空字符串，布尔值为false。 对于嵌入式消息，默认值始终是消息的“默认实例”或“原型”，没有设置任何字段。 调用访问器以获取未显式设置的可选（或必填）字段的值始终会返回该字段的默认值。
- `repeated`: 该字段可以重复任意次（包括零次）。 重复值的顺序将保留在 protocol buffer中。 将重复字段视为动态大小的数组。

编译命令：

```
protoc -I=$SRC_DIR --java_out=$DST_DIR $SRC_DIR/addressbook.proto
```