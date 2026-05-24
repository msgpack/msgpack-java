package org.msgpack.jackson.dataformat.benchmark.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"content", "images"})
public class MediaItem
{
    public MediaContent content;
    public List<Image> images;

    public MediaItem() {}

    public MediaItem(MediaContent content)
    {
        this.content = content;
    }

    public void addImage(Image image)
    {
        if (images == null) {
            images = new ArrayList<>();
        }
        images.add(image);
    }
}
