//
// MessagePack for Java
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package org.msgpack.value;

/**
 * Interface for implementing the visitor pattern on message-packed values
 */
public interface ValueVisitor {

    void visitNil();
    void visitBoolean(BooleanValue v);
    void visitInteger(IntegerValue v);
    void visitFloat(FloatValue v);
    void visitBinary(BinaryValue v);
    void visitString(StringValue v);
    void visitArray(ArrayValue v);
    void visitMap(MapValue v);
    void visitExtension(ExtensionValue v);

    /**
     * Visitor can use this method to handle an exception occurred while visiting a value
     * @param e
     */
    void onError(Exception e);
}
