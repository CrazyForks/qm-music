package com.github.chenqimiao.qmmusic.core.io.local;


import com.github.chenqimiao.qmmusic.core.io.local.model.MusicAlbumMeta;
import com.github.chenqimiao.qmmusic.core.io.local.model.MusicMeta;
import jakarta.annotation.Nullable;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;

import java.io.File;
import java.util.Iterator;

/**
 * Music local file reader
 * @author Qimiao Chen
 * @since 2025/3/23 21:51
 **/
public abstract class MusicFileReader {

    @SneakyThrows
    @Nullable
    public static MusicMeta readMusicMeta(File musicFile) {
        AudioFile f = null;
        try {
            f = AudioFileIO.read(musicFile);

        }catch (CannotReadException e) {
            return null;
        }
        Tag tag = f.getTag();
        AudioHeader audioHeader = f.getAudioHeader();
        String trackGrain = tag.getFirst("TXXX:REPLAYGAIN_TRACK_GAIN");
        if (StringUtils.isBlank(trackGrain)) {
            trackGrain = tag.getFirst("REPLAYGAIN_TRACK_GAIN");
        }
        String trackPeak = tag.getFirst("TXXX:REPLAYGAIN_TRACK_PEAK");
        if (StringUtils.isBlank(trackPeak)) {
            trackPeak = tag.getFirst("REPLAYGAIN_TRACK_PEAK");

        }

        String discNo = tag.getFirst(FieldKey.DISC_NO);
        String discTotal = tag.getFirst(FieldKey.DISC_TOTAL);
        String trackTotal = tag.getFirst(FieldKey.TRACK_TOTAL);


        return MusicMeta.builder().title(tag.getFirst(FieldKey.TITLE))
                .musicAlbumMeta(MusicAlbumMeta.builder()
                        .album(tag.getFirst(FieldKey.ALBUM))
                        .albumArtist(tag.getFirst(FieldKey.ALBUM_ARTIST))
                        // 按需获取byte[]
                        .artworks(tag.getArtworkList())
                        .musicbrainzReleaseType(tag.getFirst(FieldKey.MUSICBRAINZ_RELEASE_TYPE))
                        .originalYear(tag.getFirst(FieldKey.ORIGINAL_YEAR))
                        .year(tag.getFirst(FieldKey.YEAR))
                        .musicbrainzReleaseType(tag.getFirst(FieldKey.MUSICBRAINZ_RELEASE_TYPE))
                        .genre(tag.getFirst("ALBUMGENRE"))
                        .albumPeak(tag.getFirst("TXXX:REPLAYGAIN_ALBUM_PEAK"))
                        .albumGain(tag.getFirst("TXXX:REPLAYGAIN_ALBUM_GAIN"))
                        .trackTotal(trackTotal)
                        .build()
                )
                .artist(tag.getFirst(FieldKey.ARTIST))
                .genre(tag.getFirst(FieldKey.GENRE))
                .lyrics(tag.getFirst(FieldKey.LYRICS))
                .comment(tag.getFirst(FieldKey.COMMENT))
                .format(audioHeader.getFormat())
                .bitRate(audioHeader.getBitRate())
                .trackLength(audioHeader.getTrackLength())
                .track(tag.getFirst(FieldKey.TRACK))
                .samplingRate(audioHeader.getSampleRate())
                .channels(audioHeader.getChannels())
                .bitDepth(audioHeader.getBitRate())
                .trackPeak(trackPeak)
                .trackGain(trackGrain)
                .discNo(discNo)
                .discTotal(discTotal)
                .build();
    }

    @SneakyThrows
    public static MusicMeta readMusicMeta(String musicFileName) {

        return MusicFileReader.readMusicMeta(new File(musicFileName));
    }

    public static String beautifyReleaseYear(String releaseYear) {
        if (StringUtils.length(releaseYear) <= 4) {
            return releaseYear;
        }
        // maybe 2024-01-02
        return releaseYear.substring(0, 4);
    }

    @SneakyThrows
    public static long calMetadataBytesSize(String musicFilePath) {
        AudioFile audioFile = AudioFileIO.read(new File(musicFilePath));
        Tag tag = audioFile.getTag();
        Iterator<TagField> iterator = tag.getFields();

        long totalSize = 0;
        while (iterator.hasNext()) {
            TagField next = iterator.next();
            totalSize = totalSize + next.getRawContent().length;
        }
        return totalSize;
    }
}
