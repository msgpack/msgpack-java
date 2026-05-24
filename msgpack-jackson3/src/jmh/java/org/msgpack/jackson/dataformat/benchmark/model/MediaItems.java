package org.msgpack.jackson.dataformat.benchmark.model;

public class MediaItems
{
    private static final MediaItem STD_MEDIA_ITEM;

    static {
        MediaContent content = new MediaContent();
        content.uri = "http://javaone.com/keynote.mpg";
        content.title = "Javaone Keynote";
        content.width = 640;
        content.height = 480;
        content.format = "video/mpg4";
        content.duration = 18000000L;
        content.size = 58982400L;
        content.bitrate = 262144;
        content.player = Player.JAVA;
        content.copyright = "None";
        content.addPerson("Bill Gates");
        content.addPerson("Steve Jobs");

        MediaItem item = new MediaItem(content);
        item.addImage(new Image("http://javaone.com/keynote_large.jpg", "Javaone Keynote", 1024, 768, Size.LARGE));
        item.addImage(new Image("http://javaone.com/keynote_small.jpg", "Javaone Keynote", 320, 240, Size.SMALL));

        STD_MEDIA_ITEM = item;
    }

    public static MediaItem stdMediaItem()
    {
        return STD_MEDIA_ITEM;
    }
}
