package top.cnzrg.lingyarn.domain;

import java.io.Serializable;

/**
 * FileName: MediaItem
 * Author: ZRG
 * Date: 2020/2/25 19:03
 */
public class MediaItem implements Serializable {
    private String name;
    private long duration;
    private long size;
    private String data;    // /storage/sdcard/zrgvideo/ysjn04.mp4
    private String artist;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "MediaItem{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", data='" + data + '\'' +
                ", artist='" + artist + '\'' +
                '}';
    }
}
