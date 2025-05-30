package com.github.chenqimiao.qmmusic.core.enums;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 00:46
 **/
@AllArgsConstructor
@Getter
public enum EnumAudioCodec {
    ACC("acc", Lists.newArrayList(EnumAudioFormat.MP4, EnumAudioFormat.FLV), "高效音频编码"),
    LIB_MP3_LAME("libmp3lame", Lists.newArrayList(EnumAudioFormat.MP3), "MP3 编码"),
    lib_x264("libx264", Lists.newArrayList(EnumAudioFormat.MP4, EnumAudioFormat.FLV), "H.264/AVC 编码"),
    vp9("vp9", Lists.newArrayList(EnumAudioFormat.WEBM), "VP9 编码"),
    ;

    private final String name;

    private final List<EnumAudioFormat> supportedFormats;

    private final String desc;

    public static EnumAudioCodec parseObjByName(String name){
        Optional<EnumAudioCodec> instance = Arrays.stream(values()).filter(obj -> obj.getName().equals(name)).findFirst();
        return instance.orElse(null);
    }

    public static List<EnumAudioCodec> byFormat(String format){
        final String f = format.toLowerCase();
        return Arrays.stream(values()).filter(obj -> {
            return obj.getSupportedFormats().contains(EnumAudioFormat.parseObjByName(f));
        }).collect(Collectors.toList());
    }
}
