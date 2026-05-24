# Issues to address

## msgpack-jackson3-specific

### 1. `isClosed()` always returns false — FIXED

**File:** `msgpack-jackson3/.../MessagePackGenerator.java` (close method)

Fixed by delegating to `super.close()` (which sets `_closed = true` and calls
`_releaseBuffers()`). `AUTO_CLOSE_TARGET` handling was moved to `_closeInput()` so the
base-class lifecycle is used correctly. Our `close()` override calls `flush()` first to
drain pending nodes, then delegates the rest to `super.close()`.

### 2. `MessagePackFactory.snapshot()` returns `this` — FIXED

**File:** `msgpack-jackson3/.../MessagePackFactory.java`

Fixed: `snapshot()` now delegates to `copy()`, and `rebuild()` is implemented via `MessagePackFactoryBuilder`.

### 3. Build: `msgpack-jackson3` fails to compile locally on Java < 17 — FIXED

`build.sbt` conditionally includes `msgpack-jackson3` in the root aggregate only when
running on Java 17+. Developers on older JDKs and CI on older JDK matrix entries
skip the module cleanly.

### 4. `MessagePackGenerator` Jackson 3 generator contract — FIXED

**File:** `msgpack-jackson3/.../MessagePackGenerator.java`

Several issues fixed across two passes:

**Pass 1** — `streamWriteContext()` returns null:
Added a `SimpleStreamWriteContext writeContext` field initialized to
`SimpleStreamWriteContext.createRootContext(null)`. `streamWriteContext()` returns
it; `writeStartArray/Object` push a child context; `endCurrentContainer` pops via
`clearAndGetParent()`; `currentValue()` and `assignCurrentValue()` delegate to it.

Also fixed in the same pass:
- `version()` now returns `PackageVersion.VERSION` in generator, parser, and factory.

**Pass 2** — Align with CBORGenerator pattern (reference: jackson-dataformats-binary 3.x):
- `_verifyValueWrite`: checks return value of `writeContext.writeValue()` and calls
  `_reportError` on failure, so writing a value inside an object without a preceding
  property name is detected at the Jackson API level.
- `writeName(String/SerializableString)`: checks return value of
  `writeContext.writeName()` and calls `_reportError` when not in Object context or
  when a name is written twice without an intervening value.
- `writeStartArray/writeStartObject`: call `_verifyValueWrite` before creating the
  child context, matching the CBORGenerator pattern.
- `addValueNode`: calls `writeContext.writeValue()` to keep Jackson context state in
  sync with our internal node-based state. This was the root cause of all `writeName`
  failures: `GeneratorBase` does NOT auto-call `_verifyValueWrite` for abstract write
  methods (writeString, writeNumber, etc.), so without this call `_gotPropertyId` was
  never reset after the first `writeName`, making every subsequent `writeName` return
  false.

Note: `messageBufferOutputHolder` ThreadLocal OutputStream retention — FIXED (see
preexisting-issues section 4 below). `messageBufferOutputHolder.remove()` caused a 23%
regression; instead `_releaseBuffers()` now calls `messageBufferOutput.reset(null)` to
release the OutputStream reference while keeping the `OutputStreamBufferOutput` object
alive for reuse.

### 5. `writeString(Reader, int)` len≥0 path allocates unbounded buffer — FIXED

**File:** `msgpack-jackson3/.../MessagePackGenerator.java` (writeString(Reader, int))

The len≥0 path allocated `new char[len]` upfront — an unbounded allocation if the
caller passed a large hint value. Fixed by switching to chunked reading (8192-char
buffer) into a `StringBuilder`, matching the pattern already used in the len<0 branch.

The len<0 path (StringBuilder + 1024-char chunk) still allocates an extra copy vs.
`CharArrayWriter`, but the overhead is minor and not worth optimizing at this layer.

### 6. Node buffering: full-tree design — FYI, no action needed

**File:** `msgpack-jackson3/.../MessagePackGenerator.java` (node buffering design)

`MessagePackGenerator` buffers the entire document as a tree of `ValueNode` objects and
only serializes to MessagePack bytes when the root container closes (or on `flush()`).
This is required because MessagePack's array and map headers encode the element count
upfront (e.g. `fixarray 0x9X` or `array 16 0xdc`), and the count is not known until all
children have been written.

A potential optimization would be to eagerly serialize completed sub-trees (e.g. flush a
completed nested array into bytes as soon as its `writeEndArray` is called). This reduces
the peak heap footprint for large flat nested collections, since the child nodes can be
GC'd once their bytes are emitted. However:

- It does not eliminate the root-container buffering problem: the root map/array must
  still buffer all its children until the root closes, because its own header depends on
  the total count.
- It adds an extra copy per sub-tree (the bytes for each sub-tree must be merged into the
  parent's payload at close time).
- In practice, the dominant cost is the root-level buffer, which this optimization does
  not address.

Decision: left as-is. Document as a known architectural constraint.

### 7. Nested POJO generator inefficiency — FYI, no action needed

**File:** `msgpack-jackson3/.../MessagePackGenerator.java` (writePOJO / nested generators)

When a serializer calls `gen.writePOJO(someObject)`, Jackson internally creates a new
`JsonGenerator` wrapping the same output stream and serializes the nested object into it.
For `MessagePackGenerator` this means the nested generator also buffers a full node tree
and emits bytes only when it closes — triggering a `flush()` of its own root context.
The parent generator then reads those bytes back as a raw blob and re-wraps them in a
`RawValueNode`.

The round-trip (buffer → bytes → re-buffer as raw) means one extra allocation and copy
per nested POJO. For deeply nested structures this can add up.

Decision: this is an inherent consequence of the deferred-flush design (see item 6) and
cannot be fixed without a larger architectural change. Left as-is.

### 8. `writeRaw*` / `writeRawValue*` — FYI, no action needed

**File:** `msgpack-jackson3/.../MessagePackGenerator.java`

CBORGenerator and SmileGenerator both throw `UnsupportedOperationException` for all
`writeRaw*` / `writeRawValue*` overloads. This was flagged in code review as something
MessagePackGenerator should match.

After investigation, the current implementation (which delegates to `addValueNode`, same
as `writeString`) is correct and need not change:

- The `writeRaw` contract requires: no escaping, no separators, content preserved
  unchanged. All three are satisfied — MessagePack has no JSON-style escaping or
  separators, and the text is packed verbatim via `packString`.
- CBOR/Smile throwing is a design choice, not a technical requirement. They could
  equally implement it as "write as a text value" — they chose to be strict instead.
- Our lenient approach (degrade gracefully to `writeString` behaviour) is just as
  defensible, and avoids breaking callers that use `writeRaw` as a `writeString`
  synonym.

---

## msgpack-jackson pre-existing issues

These issues were identified during review of msgpack-jackson3 and confirmed to exist
identically in msgpack-jackson. They should be addressed in both modules together.

## 1. `MessagePackParser`: Same byte-array input skips unpacker reset

**Files:**
- `msgpack-jackson/src/main/java/org/msgpack/jackson/dataformat/MessagePackParser.java:130`
- `msgpack-jackson3/src/main/java/org/msgpack/jackson/dataformat/MessagePackParser.java:108` — FIXED

When `AUTO_CLOSE_SOURCE` is disabled and the same `byte[]` instance is parsed more
than once, the condition `messageUnpackerTuple.first() != src` is false, so the unpacker
is not reset. The second parse continues from where the first left off (typically EOF)
instead of from the beginning.

Bug only manifests when all three conditions hold: (1) `reuseResourceInParser=true`,
(2) `AUTO_CLOSE_SOURCE` disabled (non-default), (3) the same `byte[]` reference reused.

**Note on msgpack-jackson3:** In practice, issue #2's fix (clearing the `byte[]` from the
ThreadLocal on `close()`) incidentally prevents this bug from manifesting — after close,
the cached src becomes `null`, so the next parse always sees `null != bytes` and resets.
Fixed explicitly anyway (`|| src instanceof byte[]`) to make intent clear and remove the
subtle dependency between the two fixes. Regression test:
`MessagePackParserTest.testByteArrayReuseResetsUnpackerWhenAutoCloseSourceDisabled`.

Needs the same explicit fix in msgpack-jackson (where issue #2 is also not yet fixed, so
both bugs compound).

## 2. `MessagePackParser`: ThreadLocal retains last byte-array payload per thread

**Files:**
- `msgpack-jackson/src/main/java/org/msgpack/jackson/dataformat/MessagePackParser.java:135`
- `msgpack-jackson3/src/main/java/org/msgpack/jackson/dataformat/MessagePackParser.java` — FIXED

`messageUnpackerHolder` is never cleared on parser close. For byte-array inputs this
retains the entire last parsed payload for each thread in a pool indefinitely, which
can cause unbounded memory retention after large messages.

Fixed in msgpack-jackson3: on `close()`, if the cached source is a `byte[]`, it is
replaced with `null` in the ThreadLocal (keeping the unpacker alive for reuse but
releasing the byte-array reference). InputStream sources are left unchanged because
they are needed to detect same-stream reuse. Needs the same fix in msgpack-jackson.

## 3. `MessagePackGenerator`: `close()` does not set `isClosed()` to true

**Files:**
- `msgpack-jackson/src/main/java/org/msgpack/jackson/dataformat/MessagePackGenerator.java` (close method)
- `msgpack-jackson3/src/main/java/org/msgpack/jackson/dataformat/MessagePackGenerator.java` — FIXED

The `close()` override never sets the closed flag, so `isClosed()` remains false.
Fixed in msgpack-jackson3 by setting `_closed = true` directly (calling `super.close()`
is not viable since it unconditionally closes the underlying stream, ignoring
`AUTO_CLOSE_TARGET`). Needs the same fix in msgpack-jackson.

## 4. `MessagePackGenerator`: `writeString(Reader, int)` crashes on length -1

**File:** `msgpack-jackson/src/main/java/org/msgpack/jackson/dataformat/MessagePackGenerator.java` (writeString(Reader, int))

`new char[len]` throws `NegativeArraySizeException` when `len` is -1, which is a
valid Jackson API usage meaning "unknown length, read until EOF".

## 5. `MessagePackGenerator`: `writeNumber(String)` tries `Double.parseDouble` before `BigInteger`

**File:** `msgpack-jackson/src/main/java/org/msgpack/jackson/dataformat/MessagePackGenerator.java:699`

Integer strings outside the `long` range are serialized as floating-point values,
losing precision, even though MessagePack can encode big integers exactly. The order
should try integer parsing first.

## 6. `MessagePackGenerator`: Closing a container leaves stale `currentState`

**Files:**
- `msgpack-jackson/src/main/java/org/msgpack/jackson/dataformat/MessagePackGenerator.java` (writeEndArray/writeEndObject)
- `msgpack-jackson3/src/main/java/org/msgpack/jackson/dataformat/MessagePackGenerator.java` — FIXED

After closing the root container, `currentState` was not reset to `IN_ROOT`. After
`flush()` clears `nodes`, any subsequent root-level value was treated as if inside the
old container. Fixed in msgpack-jackson3 by adding `currentState = IN_ROOT` in
`endCurrentContainer()`; needs the same fix in msgpack-jackson.

## 7. `MessagePackSerializedString`: Most interface methods are stubs

**Files:**
- `msgpack-jackson/src/main/java/org/msgpack/jackson/dataformat/MessagePackSerializedString.java:70`
- `msgpack-jackson3/src/main/java/org/msgpack/jackson/dataformat/MessagePackSerializedString.java` — FIXED

Most `SerializableString` methods return 0 or do nothing. Any Jackson code path
that calls these methods (e.g. for length or byte-copy operations) will silently
produce incorrect results. Fixed in msgpack-jackson3 by implementing all append/write/put
methods using the existing `asUnquotedUTF8()` / `asQuotedUTF8()` helpers.
Needs the same fix in msgpack-jackson.

## 8. `MessagePackGenerator`: `getBytesIfAscii` writes to wrong index when offset > 0

**Files:**
- `msgpack-jackson/src/main/java/org/msgpack/jackson/dataformat/MessagePackGenerator.java:474`
- `msgpack-jackson3/src/main/java/org/msgpack/jackson/dataformat/MessagePackGenerator.java` — FIXED

`bytes[i] = (byte) c` should be `bytes[i - offset] = (byte) c`. When offset > 0, `i`
starts above 0 but `bytes` is length `len`, so it throws `ArrayIndexOutOfBoundsException`.
Fixed in msgpack-jackson3; needs the same fix in msgpack-jackson.

**Practical impact:** Low. `writeString(char[], offset, len)` is a low-level Jackson
streaming API used by Jackson's own internals and performance-sensitive custom serializers,
not by typical application code. Normal users call `writeString(String)` instead.

## 9. `MessagePackGenerator`: `writeByteArrayTextValue` ASCII path ignores offset

**Files:**
- `msgpack-jackson/src/main/java/org/msgpack/jackson/dataformat/MessagePackGenerator.java:512`
- `msgpack-jackson3/src/main/java/org/msgpack/jackson/dataformat/MessagePackGenerator.java` — FIXED

`addValueNode(new AsciiCharString(text))` stores the entire backing array instead of
the requested slice `[offset, offset+len)`. Callers such as `writeRawUTF8String` and
`writeUTF8String` with non-zero offsets will serialize garbage bytes.
Fixed in msgpack-jackson3 using `System.arraycopy`; needs the same fix in msgpack-jackson.

**Practical impact:** Low. `writeUTF8String(byte[], offset, len)` is a low-level API
called by Jackson's streaming infrastructure or custom serializers working with raw
byte buffers. Typical application code goes through ObjectMapper, which always passes
offset=0 for plain byte arrays.

## 10. `MessagePackGenerator`: ByteBuffer serialization ignores `position()`

**Files:**
- `msgpack-jackson/src/main/java/org/msgpack/jackson/dataformat/MessagePackGenerator.java:369`
- `msgpack-jackson3/src/main/java/org/msgpack/jackson/dataformat/MessagePackGenerator.java` — FIXED

`writePayload(bb.array(), bb.arrayOffset(), len)` ignores `bb.position()`. For any
ByteBuffer with a non-zero position (e.g. from `ByteBuffer.wrap(data, offset, len)` or
after reads), this serializes bytes starting at the wrong offset.
Fixed in msgpack-jackson3 using `bb.arrayOffset() + bb.position()`; needs the same fix in msgpack-jackson.

**Practical impact:** Moderate. This is the most realistic end-user scenario: a POJO
with a `ByteBuffer` field that was sliced or partially consumed will silently produce
corrupt serialized output. No exception is thrown.

## 11. `MessagePackFactory`: byte-array parser copies the input slice unnecessarily

**Files:**
- `msgpack-jackson/src/main/java/org/msgpack/jackson/dataformat/MessagePackFactory.java:143`
- `msgpack-jackson3/src/main/java/org/msgpack/jackson/dataformat/MessagePackFactory.java` — FIXED

`_createParser(byte[], int, int)` calls `Arrays.copyOfRange(data, offset, offset + len)` to
extract the slice, then wraps the copy in `InputStreamBufferInput`. `ArrayBufferInput` already
accepts `(data, offset, len)` directly and avoids the copy entirely.
Fixed in msgpack-jackson3 by constructing `ArrayBufferInput(data, offset, len)` and passing it
to the `MessageBufferInput`-taking constructor; needs the same fix in msgpack-jackson.

**Practical impact:** Low for small payloads; proportional to payload size for large messages
since the entire slice is copied on every `readValue` call.

## 12. `MessagePackGenerator`: non-array-backed ByteBuffer read advances caller's position

**Files:**
- `msgpack-jackson/src/main/java/org/msgpack/jackson/dataformat/MessagePackGenerator.java:373`
- `msgpack-jackson3/src/main/java/org/msgpack/jackson/dataformat/MessagePackGenerator.java` — FIXED

When serializing a `ByteBuffer` whose `hasArray()` is false (e.g. direct buffers), the
generator calls `bb.get(data)` which advances the buffer's position as a side effect. The
`hasArray()` fast path is non-destructive (`writePayload(bb.array(), bb.arrayOffset() +
bb.position(), len)`), so the two paths are inconsistent.
Fixed in msgpack-jackson3 using `bb.duplicate().get(data)` — `duplicate()` shares the
backing store without copying data (O(1)) but has its own independent position.
Needs the same fix in msgpack-jackson.

**Practical impact:** Low. Direct ByteBuffers are uncommon in typical POJO fields, and the
mutation is only observable if the caller inspects or reuses the buffer after serialization.

