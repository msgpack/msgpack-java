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
package org.msgpack.jackson.dataformat.benchmark.model;

public class Image
{
    public String uri;
    public String title;
    public int width;
    public int height;
    public Size size;

    public Image() {}

    public Image(String uri, String title, int width, int height, Size size)
    {
        this.uri = uri;
        this.title = title;
        this.width = width;
        this.height = height;
        this.size = size;
    }
}
