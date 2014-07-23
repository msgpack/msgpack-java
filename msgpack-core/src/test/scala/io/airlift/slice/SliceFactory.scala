package io.airlift.slice

import org.msgpack.core.buffer.MessageBuffer

/**
 *
 */
object SliceFactory {
  def newSlice(b:MessageBuffer) = {
    new Slice(b.getBase, b.getAddress, b.size(), b.getReference)
  }

}
