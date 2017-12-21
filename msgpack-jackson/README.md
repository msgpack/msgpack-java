# jackson-dataformat-msgpack

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.msgpack/jackson-dataformat-msgpack/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.msgpack/jackson-dataformat-msgpack/)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/org.msgpack/jackson-dataformat-msgpack/badge.svg)](http://www.javadoc.io/doc/org.msgpack/jackson-dataformat-msgpack)

This Jackson extension library is a component to easily read and write [MessagePack](http://msgpack.org/) encoded data through jackson-databind API.

It extends standard Jackson streaming API (`JsonFactory`, `JsonParser`, `JsonGenerator`), and as such works seamlessly with all the higher level data abstractions (data binding, tree model, and pluggable extensions). For the details of Jackson-annotations, please see https://github.com/FasterXML/jackson-annotations.

This library isn't compatibile with msgpack-java v0.6 or earlier by default in serialization/deserialization of POJO. See **Advanced usage** below for details.

## Install

### Maven

```
<dependency>
  <groupId>org.msgpack</groupId>
  <artifactId>jackson-dataformat-msgpack</artifactId>
  <version>(version)</version>
</dependency>
```

### Sbt

```
libraryDependencies += "org.msgpack" % "jackson-dataformat-msgpack" % "(version)"
```

### Gradle
```
repositories {
    mavenCentral()
}

dependencies {
    compile 'org.msgpack:jackson-dataformat-msgpack:(version)'
}
```


## Basic usage

### Serialization/Deserialization of POJO

Only thing you need to do is to instantiate `MessagePackFactory` and pass it to the constructor of `com.fasterxml.jackson.databind.ObjectMapper`. And then, you can use it for MessagePack format data in the same way as jackson-databind.

```java
  // Instantiate ObjectMapper for MessagePack
  ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
  
  // Serialize a Java object to byte array
  ExamplePojo pojo = new ExamplePojo("komamitsu");
  byte[] bytes = objectMapper.writeValueAsBytes(pojo);
  
  // Deserialize the byte array to a Java object
  ExamplePojo deserialized = objectMapper.readValue(bytes, ExamplePojo.class);
  System.out.println(deserialized.getName()); // => komamitsu
```

### Serialization/Deserialization of List

```java
  // Instantiate ObjectMapper for MessagePack
  ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());

  // Serialize a List to byte array
  List<Object> list = new ArrayList<>();
  list.add("Foo");
  list.add("Bar");
  list.add(42);
  byte[] bytes = objectMapper.writeValueAsBytes(list);

  // Deserialize the byte array to a List
  List<Object> deserialized = objectMapper.readValue(bytes, new TypeReference<List<Object>>() {});
  System.out.println(deserialized); // => [Foo, Bar, 42]
```

### Serialization/Deserialization of Map

```java
  // Instantiate ObjectMapper for MessagePack
  ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());

  // Serialize a Map to byte array
  Map<String, Object> map = new HashMap<>();
  map.put("name", "komamitsu");
  map.put("age", 42);
  byte[] bytes = objectMapper.writeValueAsBytes(map);

  // Deserialize the byte array to a Map
  Map<String, Object> deserialized = objectMapper.readValue(bytes, new TypeReference<Map<String, Object>>() {});
  System.out.println(deserialized); // => {name=komamitsu, age=42}
 
 ```

### Example of Serialization/Deserialization over multiple languages

Java

```java
  // Serialize
  Map<String, Object> obj = new HashMap<String, Object>();
  obj.put("foo", "hello");
  obj.put("bar", "world");
  byte[] bs = objectMapper.writeValueAsBytes(obj);
  // bs => [-126, -93, 102, 111, 111, -91, 104, 101, 108, 108, 111,
  //        -93, 98, 97, 114, -91, 119, 111, 114, 108, 100]
```

Ruby

```ruby
  require 'msgpack'

  # Deserialize
  xs = [-126, -93, 102, 111, 111, -91, 104, 101, 108, 108, 111,
        -93, 98, 97, 114, -91, 119, 111, 114, 108, 100]
  MessagePack.unpack(xs.pack("C*"))
  # => {"foo"=>"hello", "bar"=>"world"}

  # Serialize
  ["zero", 1, 2.0, nil].to_msgpack.unpack('C*')
  # => [148, 164, 122, 101, 114, 111, 1, 203, 64, 0, 0, 0, 0, 0, 0, 0, 192]
```

Java

```java
  // Deserialize
  bs = new byte[] {(byte) 148, (byte) 164, 122, 101, 114, 111, 1,
                   (byte) 203, 64, 0, 0, 0, 0, 0, 0, 0, (byte) 192};
  TypeReference<List<Object>> typeReference = new TypeReference<List<Object>>(){};
  List<Object> xs = objectMapper.readValue(bs, typeReference);
  // xs => [zero, 1, 2.0, null]
```

## Advanced usage

### Serialize/Deserialize POJO as MessagePack array type to keep compatibility with msgpack-java:0.6

In msgpack-java:0.6 or earlier, a POJO was serliazed and deserialized as an array of values in MessagePack format. The order of values depended on an internal order of Java class's variables and it was a naive way and caused some issues since Java class's variables order isn't guaranteed over Java implementations.

On the other hand, jackson-databind serializes and deserializes a POJO as a key-value object. So this `jackson-dataformat-msgpack` also handles POJOs in the same way. As a result, it isn't compatible with msgpack-java:0.6 or earlier in serialization and deserialization of POJOs.

But if you want to make this library handle POJOs in the same way as msgpack-java:0.6 or earlier, you can use `JsonArrayFormat` like this:

```java
  ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
  objectMapper.setAnnotationIntrospector(new JsonArrayFormat());
```

### Serialize multiple values without closing an output stream

`com.fasterxml.jackson.databind.ObjectMapper` closes an output stream by default after it writes a value. If you want to serialize multiple values in a row without closing an output stream, set `JsonGenerator.Feature.AUTO_CLOSE_TARGET` to false.

```java
  OutputStream out = new FileOutputStream(tempFile);
  ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
  objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

  objectMapper.writeValue(out, 1);
  objectMapper.writeValue(out, "two");
  objectMapper.writeValue(out, 3.14);
  out.close();

  MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(new FileInputStream(tempFile));
  System.out.println(unpacker.unpackInt());      // => 1
  System.out.println(unpacker.unpackString());   // => two
  System.out.println(unpacker.unpackFloat());    // => 3.14
```

### Deserialize multiple values without closing an input stream

`com.fasterxml.jackson.databind.ObjectMapper` closes an input stream by default after it reads a value. If you want to deserialize multiple values in a row without closing an output stream, set `JsonParser.Feature.AUTO_CLOSE_SOURCE` to false.

```java
  MessagePacker packer = MessagePack.newDefaultPacker(new FileOutputStream(tempFile));
  packer.packInt(42);
  packer.packString("Hello");
  packer.close();

  FileInputStream in = new FileInputStream(tempFile);
  ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
  objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
  System.out.println(objectMapper.readValue(in, Integer.class));
  System.out.println(objectMapper.readValue(in, String.class));
  in.close();
```

### Serialize not using str8 type

Old msgpack-java (e.g 0.6.7) doesn't support MessagePack str8 type. When your application needs to comunicate with such an old MessagePack library, you can disable the data type like this:

```java
  MessagePack.PackerConfig config = new MessagePack.PackerConfig().withStr8FormatSupport(false);
  ObjectMapper mapperWithConfig = new ObjectMapper(new MessagePackFactory(config));
  // This string is serialized as bin8 type
  byte[] resultWithoutStr8Format = mapperWithConfig.writeValueAsBytes(str8LengthString);
```

### Serialize using non-String as a key of Map

When you want to use non-String value as a key of Map, use `MessagePackKeySerializer` for key serialization.

```java
  @JsonSerialize(keyUsing = MessagePackKeySerializer.class)
  private Map<Integer, String> intMap = new HashMap<>();

      :
  {
      intMap.put(42, "Hello");

      ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
      byte[] bytes = objectMapper.writeValueAsBytes(intMap);

      Map<Integer, String> deserialized = objectMapper.readValue(bytes, new TypeReference<Map<Integer, String>>() {});
      System.out.println(deserialized);   // => {42=Hello}
  }
```

### Deserialize extension types with ExtensionTypeCustomDeserializers

`ExtensionTypeCustomDeserializers` helps you to deserialize extension types easily.

#### With target Java class

```java
  NestedListComplexPojo parent = new NestedListComplexPojo();
  parent.children = Arrays.asList(new TinyPojo("Foo"), new TinyPojo("Bar"));

  // In this application, extension type 17 is used for NestedListComplexPojo
  byte[] bytes;
  {
      // This ObjectMapper is just for temporary serialization
      ObjectMapper tempObjectMapper = new ObjectMapper(new MessagePackFactory());
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      MessagePacker packer = MessagePack.newDefaultPacker(outputStream);

      byte[] extBytes = tempObjectMapper.writeValueAsBytes(parent);
      packer.packExtensionTypeHeader((byte) 17, extBytes.length);
      packer.addPayload(extBytes);
      packer.close();

      bytes = outputStream.toByteArray();
  }

  // Register the type and the class to ExtensionTypeCustomDeserializers
  ExtensionTypeCustomDeserializers extTypeCustomDesers = new ExtensionTypeCustomDeserializers();
  extTypeCustomDesers.addTargetClass((byte) 17, NestedListComplexPojo.class);
  ObjectMapper objectMapper = new ObjectMapper(
          new MessagePackFactory().setExtTypeCustomDesers(extTypeCustomDesers));

  System.out.println(objectMapper.readValue(bytes, Object.class));
    // => NestedListComplexPojo{children=[TinyPojo{name='Foo'}, TinyPojo{name='Bar'}]}
```

#### With type reference

```java
  Map<String, Integer> map = new HashMap<>();
  map.put("one", 1);
  map.put("two", 2);

  // In this application, extension type 31 is used for Map<String, Integer>
  byte[] bytes;
  {
      // Same as above
        :
      packer.packExtensionTypeHeader((byte) 31, extBytes.length);
        :
  }

  // Register the type and the type reference to ExtensionTypeCustomDeserializers
  ExtensionTypeCustomDeserializers extTypeCustomDesers = new ExtensionTypeCustomDeserializers();
  extTypeCustomDesers.addTargetTypeReference((byte) 31,
  	      new TypeReference<Map<String, Integer>>() {});
  ObjectMapper objectMapper = new ObjectMapper(
          new MessagePackFactory().setExtTypeCustomDesers(extTypeCustomDesers));

  System.out.println(objectMapper.readValue(bytes, Object.class));
    // => {one=1, two=2}
```

#### With custom deserializer

```java
  // In this application, extension type 59 is used for byte[]
  byte[] bytes;
  {
      // This ObjectMapper is just for temporary serialization
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      MessagePacker packer = MessagePack.newDefaultPacker(outputStream);

      packer.packExtensionTypeHeader((byte) 59, hexspeak.length);
      packer.addPayload(hexspeak);
      packer.close();

      bytes = outputStream.toByteArray();
  }

  // Register the type and a deserializer to ExtensionTypeCustomDeserializers
  ExtensionTypeCustomDeserializers extTypeCustomDesers = new ExtensionTypeCustomDeserializers();
  extTypeCustomDesers.addCustomDeser((byte) 59, data -> {
      if (Arrays.equals(data,
                new byte[] {(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE})) {
          return "Java";
      }
      return "Not Java";
  }
  );
  ObjectMapper objectMapper = new ObjectMapper(
          new MessagePackFactory().setExtTypeCustomDesers(extTypeCustomDesers));

  System.out.println(objectMapper.readValue(bytes, Object.class));
    // => Java
```
