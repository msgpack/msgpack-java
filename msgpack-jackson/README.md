# jackson-dataformat-msgpack

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.msgpack/jackson-dataformat-msgpack/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.msgpack/jackson-dataformat-msgpack/)
[![Javadoc](https://www.javadoc.io/badge/org.msgpack/jackson-dataformat-msgpack.svg)](https://www.javadoc.io/doc/org.msgpack/jackson-dataformat-msgpack)

This Jackson extension library is a component to easily read and write [MessagePack](http://msgpack.org/) encoded data through jackson-databind API.

It extends standard Jackson streaming API (`JsonFactory`, `JsonParser`, `JsonGenerator`), and as such works seamlessly with all the higher level data abstractions (data binding, tree model, and pluggable extensions). For the details of Jackson-annotations, please see https://github.com/FasterXML/jackson-annotations.

This library isn't compatible with msgpack-java v0.6 or earlier by default in serialization/deserialization of POJO. See **Advanced usage** below for details.

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

Or more easily:

```java
  ObjectMapper objectMapper = new MessagePackMapper();
```

We strongly recommend to call `MessagePackMapper#handleBigDecimalAsString()` if you serialize and/or deserialize BigDecimal values. See [Serialize and deserialize BigDecimal as str type internally in MessagePack format](#serialize-and-deserialize-bigdecimal-as-str-type-internally-in-messagepack-format) for details.

```java
  ObjectMapper objectMapper = new MessagePackMapper().handleBigDecimalAsString();
```

### Serialization/Deserialization of List

```java
  // Instantiate ObjectMapper for MessagePack
  ObjectMapper objectMapper = new MessagePackMapper();

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
  ObjectMapper objectMapper = MessagePackMapper();

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
  ObjectMapper objectMapper = new MessagePackMapper();
  objectMapper.setAnnotationIntrospector(new JsonArrayFormat());
```

### Serialize multiple values without closing an output stream

`com.fasterxml.jackson.databind.ObjectMapper` closes an output stream by default after it writes a value. If you want to serialize multiple values in a row without closing an output stream, set `JsonGenerator.Feature.AUTO_CLOSE_TARGET` to false.

```java
  OutputStream out = new FileOutputStream(tempFile);
  ObjectMapper objectMapper = new MessagePackMapper();
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
  ObjectMapper objectMapper = new MessagePackMapper();
  objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
  System.out.println(objectMapper.readValue(in, Integer.class));
  System.out.println(objectMapper.readValue(in, String.class));
  in.close();
```

### Serialize not using str8 type

Old msgpack-java (e.g 0.6.7) doesn't support MessagePack str8 type. When your application needs to comunicate with such an old MessagePack library, you can disable the data type like this:

```java
  MessagePack.PackerConfig config = new MessagePack.PackerConfig().withStr8FormatSupport(false);
  ObjectMapper mapperWithConfig = new MessagePackMapper(new MessagePackFactory(config));
  // This string is serialized as bin8 type
  byte[] resultWithoutStr8Format = mapperWithConfig.writeValueAsBytes(str8LengthString);
```

### Serialize using non-String as a key of Map

When you want to use non-String value as a key of Map, use `MessagePackKeySerializer` for key serialization.

```java
  @JsonSerialize(keyUsing = MessagePackKeySerializer.class)
  private Map<Integer, String> intMap = new HashMap<>();

    :

  intMap.put(42, "Hello");

  ObjectMapper objectMapper = new MessagePackMapper();
  byte[] bytes = objectMapper.writeValueAsBytes(intMap);

  Map<Integer, String> deserialized = objectMapper.readValue(bytes, new TypeReference<Map<Integer, String>>() {});
  System.out.println(deserialized);   // => {42=Hello}
```

### Serialize and deserialize BigDecimal as str type internally in MessagePack format

`jackson-dataformat-msgpack` represents BigDecimal values as float type in MessagePack format by default for backward compatibility. But the default behavior could fail when handling too large value for `double` type. So we strongly recommend to call `MessagePackMapper#handleBigDecimalAsString()` to internally handle BigDecimal values as String.

```java
  ObjectMapper objectMapper = new MessagePackMapper().handleBigDecimalAsString();

  Pojo obj = new Pojo();
  // This value is too large to be serialized as double
  obj.value = new BigDecimal("1234567890.98765432100");

  byte[] converted = objectMapper.writeValueAsBytes(obj);

  System.out.println(objectMapper.readValue(converted, Pojo.class));   // => Pojo{value=1234567890.98765432100}
```
`MessagePackMapper#handleBigDecimalAsString()` is equivalent to the following configuration.

```java
  ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
  objectMapper.configOverride(BigDecimal.class).setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.STRING));
```


### Serialize and deserialize Instant instances as MessagePack extension type

`timestamp` extension type is defined in MessagePack as type:-1. Registering `TimestampExtensionModule.INSTANCE` module enables automatic serialization and deserialization of java.time.Instant to/from the MessagePack extension type.

```java
    ObjectMapper objectMapper = new MessagePackMapper()
                                    .registerModule(TimestampExtensionModule.INSTANCE);
    Pojo pojo = new Pojo();
    // The type of `timestamp` variable is Instant
    pojo.timestamp = Instant.now();
    byte[] bytes = objectMapper.writeValueAsBytes(pojo);

    // The Instant instance is serialized as MessagePack extension type (type: -1)

    Pojo deserialized = objectMapper.readValue(bytes, Pojo.class);
    System.out.println(deserialized);   // "2022-09-14T08:47:24.922Z"
```

### Deserialize extension types with ExtensionTypeCustomDeserializers

`ExtensionTypeCustomDeserializers` helps you to deserialize your own custom extension types easily.

#### Deserialize extension type value directly

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
  });

  ObjectMapper objectMapper = new ObjectMapper(
          new MessagePackFactory().setExtTypeCustomDesers(extTypeCustomDesers));

  System.out.println(objectMapper.readValue(bytes, Object.class));
    // => Java
```

#### Use extension type as Map key

```java
  static class TripleBytesPojo
  {
    public byte first;
    public byte second;
    public byte third;

    public TripleBytesPojo(byte first, byte second, byte third)
    {
      this.first = first;
      this.second = second;
      this.third = third;
    }

    @Override
    public boolean equals(Object o)
    {
      :
    }

    @Override
    public int hashCode()
    {
      :
    }

    @Override
    public String toString()
    {
      // This key format is used when serialized as map key
      return String.format("%d-%d-%d", first, second, third);
    }

    static class KeyDeserializer
        extends com.fasterxml.jackson.databind.KeyDeserializer
    {
      @Override
      public Object deserializeKey(String key, DeserializationContext ctxt)
          throws IOException
      {
        String[] values = key.split("-");
        return new TripleBytesPojo(Byte.parseByte(values[0]), Byte.parseByte(values[1]), Byte.parseByte(values[2]));
      }
    }

    static TripleBytesPojo deserialize(byte[] bytes)
    {
      return new TripleBytesPojo(bytes[0], bytes[1], bytes[2]);
    }
  }

  :

  byte extTypeCode = 42;

  ExtensionTypeCustomDeserializers extTypeCustomDesers = new ExtensionTypeCustomDeserializers();
  extTypeCustomDesers.addCustomDeser(extTypeCode, new ExtensionTypeCustomDeserializers.Deser()
  {
    @Override
    public Object deserialize(byte[] value)
          throws IOException
    {
      return TripleBytesPojo.deserialize(value);
    }
  });

  SimpleModule module = new SimpleModule();
  module.addKeyDeserializer(TripleBytesPojo.class, new TripleBytesPojo.KeyDeserializer());
  ObjectMapper objectMapper = new ObjectMapper(
          new MessagePackFactory().setExtTypeCustomDesers(extTypeCustomDesers))
              .registerModule(module);

  Map<TripleBytesPojo, Integer> deserializedMap =
          objectMapper.readValue(serializedData,
              new TypeReference<Map<TripleBytesPojo, Integer>>() {});
```

#### Use extension type as Map value

```java
  static class TripleBytesPojo
  {
    public byte first;
    public byte second;
    public byte third;

    public TripleBytesPojo(byte first, byte second, byte third)
    {
      this.first = first;
      this.second = second;
      this.third = third;
    }

    static class Deserializer
        extends StdDeserializer<TripleBytesPojo>
    {
      protected Deserializer()
      {
        super(TripleBytesPojo.class);
      }

      @Override
      public TripleBytesPojo deserialize(JsonParser p, DeserializationContext ctxt)
          throws IOException, JsonProcessingException
      {
        return TripleBytesPojo.deserialize(p.getBinaryValue());
      }
    }

    static TripleBytesPojo deserialize(byte[] bytes)
    {
      return new TripleBytesPojo(bytes[0], bytes[1], bytes[2]);
    }
  }

  :

  byte extTypeCode = 42;

  ExtensionTypeCustomDeserializers extTypeCustomDesers = new ExtensionTypeCustomDeserializers();
  extTypeCustomDesers.addCustomDeser(extTypeCode, new ExtensionTypeCustomDeserializers.Deser()
  {
    @Override
    public Object deserialize(byte[] value)
        throws IOException
    {
      return TripleBytesPojo.deserialize(value);
    }
  });

  SimpleModule module = new SimpleModule();
  module.addDeserializer(TripleBytesPojo.class, new TripleBytesPojo.Deserializer());
  ObjectMapper objectMapper = new ObjectMapper(
          new MessagePackFactory().setExtTypeCustomDesers(extTypeCustomDesers))
              .registerModule(module);

  Map<String, TripleBytesPojo> deserializedMap =
          objectMapper.readValue(serializedData,
              new TypeReference<Map<String, TripleBytesPojo>>() {});
```

### Serialize a nested object that also serializes

When you serialize an object that has a nested object also serializing with ObjectMapper and MessagePackFactory like the following code, it throws NullPointerException since the nested MessagePackFactory modifies a shared state stored in ThreadLocal.

```java
  @Test
  public void testNestedSerialization() throws Exception
  {
      ObjectMapper objectMapper = new MessagePackMapper();
      objectMapper.writeValueAsBytes(new OuterClass());
  }

  public class OuterClass
  {
    public String getInner() throws JsonProcessingException
    {
      ObjectMapper m = new MessagePackMapper();
      m.writeValueAsBytes(new InnerClass());
      return "EFG";
    }
  }

  public class InnerClass
  {
    public String getName()
    {
      return "ABC";
    }
  }
```

There are a few options to fix this issue, but they introduce performance degredations while this usage is a corner case. A workaround that doesn't affect performance is to call `MessagePackFactory#setReuseResourceInGenerator(false)`. It might be inconvenient to call the API for users, but it's a reasonable tradeoff with performance for now.

```java
  ObjectMapper objectMapper = new ObjectMapper(
    new MessagePackFactory().setReuseResourceInGenerator(false));
```
