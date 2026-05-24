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
