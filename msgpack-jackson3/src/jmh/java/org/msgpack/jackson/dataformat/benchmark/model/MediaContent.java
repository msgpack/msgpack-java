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

import java.util.ArrayList;
import java.util.List;

public class MediaContent
{
    public String uri;
    public String title;
    public int width;
    public int height;
    public String format;
    public long duration;
    public long size;
    public int bitrate;
    public List<String> persons;
    public Player player;
    public String copyright;

    public MediaContent() {}

    public void addPerson(String person)
    {
        if (persons == null) {
            persons = new ArrayList<>();
        }
        persons.add(person);
    }
}
