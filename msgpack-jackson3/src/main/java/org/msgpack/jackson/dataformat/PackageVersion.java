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
package org.msgpack.jackson.dataformat;

import tools.jackson.core.Version;
import tools.jackson.core.Versioned;

public class PackageVersion
        implements Versioned
{
    public static final Version VERSION = new Version(0, 9, 12, null, "org.msgpack", "jackson-dataformat-msgpack-jackson3");

    @Override
    public Version version()
    {
        return VERSION;
    }
}
