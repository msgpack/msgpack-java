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
