package com.github.chenqimiao.qmmusic.app.request.subsonic;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/3/29
 **/
@Setter
@Getter
public class ArtistIndexRequest extends SubsonicRequest {

    private Long musicFolderId;

    private Long ifModifiedSince;

}
