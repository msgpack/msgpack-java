# jackson-dataformat-msgpack

This Jackson extension library handles reading and writing of data encoded in [MessagePack](http://msgpack.org/) data format.
It extends standard Jackson streaming API (`JsonFactory`, `JsonParser`, `JsonGenerator`), and as such works seamlessly with all the higher level data abstractions (data binding, tree model, and pluggable extensions). For the details of Jackson-annotations, please see https://github.com/FasterXML/jackson-annotations.

## Install

### Maven

```
<dependency>
  <groupId>org.msgpack</groupId>
  <artifactId>jackson-dataformat-msgpack</artifactId>
  <version>0.7.1</version>
</dependency>
```

### Gradle
```
repositories {
    mavenCentral()
}

dependencies {
    compile 'org.msgpack:jackson-dataformat-msgpack:0.7.1'
}
```


## Usage

Only thing you need to do is to instantiate MessagePackFormatFactory and pass it to the constructor of ObjectMapper.

```
  ObjectMapper objectMapper = new ObjectMapper(new MessagePackFormatFactory());
  ExamplePojo orig = new ExamplePojo("komamitsu");
  byte[] bytes = objectMapper.writeValueAsBytes(orig);
  ExamplePojo value = objectMapper.readValue(bytes, ExamplePojo.class);
  System.out.println(value.getName()); // => komamitsu
```

Also, you can exchange data among multiple languages.

Java

```
  // Serialize
  Map<String, Object> obj = new HashMap<String, Object>();
  obj.put("foo", "hello");
  obj.put("bar", "world");
  byte[] bs = objectMapper.writeValueAsBytes(obj);
  // bs => [-126, -93, 102, 111, 111, -91, 104, 101, 108, 108, 111,
  //        -93, 98, 97, 114, -91, 119, 111, 114, 108, 100]
```

Ruby

```
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

```
  // Deserialize
  bs = new byte[] {(byte) 148, (byte) 164, 122, 101, 114, 111, 1,
                   (byte) 203, 64, 0, 0, 0, 0, 0, 0, 0, (byte) 192};
  TypeReference<List<Object>> typeReference = new TypeReference<List<Object>>(){};
  List<Object> xs = objectMapper.readValue(bs, typeReference);
  // xs => [zero, 1, 2.0, null]
```

