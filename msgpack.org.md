# MessagePack for Java

QuickStart for msgpack-java is available [here](https://github.com/msgpack/msgpack-java/wiki/QuickStart).

## How to install

You can install msgpack via maven:

    <dependencies>
      ...
      <dependency>
        <groupId>org.msgpack</groupId>
        <artifactId>msgpack</artifactId>
        <version>${msgpack.version}</version>
      </dependency>
      ...
    </dependencies>

## Simple Serialization/Deserialization/Duck Typing using Value

    // Create serialize objects.
    List<String> src = new ArrayList<String>();
    src.add("msgpack");
    src.add("kumofs");
    src.add("viver");

    MessagePack msgpack = new MessagePack();
    // Serialize
    byte[] raw = msgpack.write(src);

    // Deserialize directly using a template
    List<String> dst1 = msgpack.read(raw, Templates.tList(Templates.TString));
    System.out.println(dst1.get(0));
    System.out.println(dst1.get(1));
    System.out.println(dst1.get(2));

    // Or, Deserialze to Value then convert type.
    Value dynamic = msgpack.read(raw);
    List<String> dst2 = new Converter(dynamic)
        .read(Templates.tList(Templates.TString));
    System.out.println(dst2.get(0));
    System.out.println(dst2.get(1));
    System.out.println(dst2.get(2));



