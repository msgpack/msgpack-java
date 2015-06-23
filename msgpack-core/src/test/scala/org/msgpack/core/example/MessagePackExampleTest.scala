package org.msgpack.core.example

import org.msgpack.core.MessagePackSpec

/**
 *
 */
class MessagePackExampleTest
  extends MessagePackSpec {

  "example" should {

    "have basic usage" in {
      MessagePackExample.basicUsage()
    }

    "have packer usage" in {
      MessagePackExample.packer()
    }

    "have file read/write example" in {
      MessagePackExample.readAndWriteFile();
    }

    "have configuration example" in {
      MessagePackExample.configuration();
    }
  }
}
