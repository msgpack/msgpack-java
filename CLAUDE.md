# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

MessagePack-Java is a binary serialization library that provides a fast and compact alternative to JSON. The project consists of two main modules:
- **msgpack-core**: Standalone MessagePack implementation with no external dependencies
- **msgpack-jackson**: Jackson integration for object mapping capabilities

## Essential Development Commands

### Build and Compile
```bash
./sbt compile              # Compile source code
./sbt "Test / compile"     # Compile source and test code
./sbt package              # Create JAR files
```

### Testing
```bash
./sbt test                 # Run all tests
./sbt ~test                # Run tests continuously on file changes
./sbt testOnly *TestClass  # Run specific test class
./sbt "testOnly *TestClass -- -z pattern"  # Run tests matching pattern

# Test with universal buffer mode (for compatibility testing)
./sbt test -J-Dmsgpack.universal-buffer=true
```

### Code Quality
```bash
./sbt jcheckStyle          # Run checkstyle (Facebook Presto style)
./sbt scalafmtAll          # Format all Scala and sbt code
```

### Publishing
```bash
./sbt publishLocal         # Install to local .ivy2 repository
./sbt publishM2            # Install to local .m2 Maven repository
```

## Architecture Overview

### Core API Structure
The main entry point is the `MessagePack` factory class which creates:
- **MessagePacker**: Serializes objects to MessagePack binary format
- **MessageUnpacker**: Deserializes MessagePack binary data

Key locations:
- Core interfaces: `msgpack-core/src/main/java/org/msgpack/core/`
- Jackson integration: `msgpack-jackson/src/main/java/org/msgpack/jackson/dataformat/`

### Buffer Management System
MessagePack uses an efficient buffer abstraction layer:
- **MessageBuffer**: Platform-optimized buffer implementations
  - Uses `sun.misc.Unsafe` for performance when available
  - Falls back to ByteBuffer on restricted platforms
- **MessageBufferInput/Output**: Manages buffer sequences for streaming

### Jackson Integration
The msgpack-jackson module provides:
- **MessagePackFactory**: Jackson JsonFactory implementation
- **MessagePackMapper**: Pre-configured ObjectMapper for MessagePack
- Support for field IDs (integer keys) for compact serialization
- Extension type support including timestamps

### Testing Structure
- **msgpack-core tests**: Written in Scala (always use the latest Scala 3 version) using AirSpec framework
  - Location: `msgpack-core/src/test/scala/`
- **msgpack-jackson tests**: Written in Java using JUnit
  - Location: `msgpack-jackson/src/test/java/`

## Important JVM Options

For JDK 17+ compatibility, these options are automatically added:
```
--add-opens=java.base/java.nio=ALL-UNNAMED
--add-opens=java.base/sun.nio.ch=ALL-UNNAMED
```

## Code Style Requirements
- Java code follows Facebook Presto style (enforced by checkstyle)
- Scala test code uses Scalafmt with Scala 3 dialect and 100 character line limit
- Checkstyle runs automatically during compilation
- No external dependencies allowed in msgpack-core

## Key Design Principles
1. **Zero Dependencies**: msgpack-core has no external dependencies
2. **Platform Optimization**: Uses platform-specific optimizations when available
3. **Streaming Support**: Both streaming and object-based APIs
4. **Type Safety**: Immutable Value hierarchy for type-safe data handling
5. **Extension Support**: Extensible type system for custom data types