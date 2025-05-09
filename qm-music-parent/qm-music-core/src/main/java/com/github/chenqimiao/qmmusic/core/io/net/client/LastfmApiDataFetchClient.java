package com.github.chenqimiao.qmmusic.core.io.net.client;

import com.github.chenqimiao.qmmusic.core.constant.RateLimiterConstants;
import com.github.chenqimiao.qmmusic.core.exception.RateLimitException;
import com.github.chenqimiao.qmmusic.core.third.lastfm.LastfmClient;
import com.github.chenqimiao.qmmusic.core.third.lastfm.model.Artist;
import com.github.chenqimiao.qmmusic.core.third.lastfm.model.ArtistInfo;
import com.github.chenqimiao.qmmusic.core.third.lastfm.model.Track;
import com.github.chenqimiao.qmmusic.core.util.TransliteratorUtils;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2025/4/15 16:19
 **/
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@AllArgsConstructor
public class LastfmApiDataFetchClient implements MetaDataFetchApiClient {

    @Autowired
    private LastfmClient lastfmClient;

    @Override
    public Boolean supportChinaRegion() {
        return Boolean.TRUE;
    }

    @Nullable
    @Override
    public com.github.chenqimiao.qmmusic.core.io.net.model.ArtistInfo fetchArtistInfo(String artistName) {
        var artistInfo = new ArtistInfo();
        if (TransliteratorUtils.isChineseString(artistName)) {
           artistInfo = lastfmClient.getArtistInfo(artistName, "zh");
        }else {
            artistInfo = lastfmClient.getArtistInfo(artistName, "en");
        }
        if (artistInfo == null) {
            return null;
        }
        com.github.chenqimiao.qmmusic.core.io.net.model.ArtistInfo result = new com.github.chenqimiao.qmmusic.core.io.net.model.ArtistInfo();
        result.setArtistName(artistName);
        if (artistInfo.getBio() != null) {
            result.setBiography(artistInfo.getBio().getSummary());
        }
        return result;
    }

    @Override
   public List<String> scrapeSimilarArtists(String artistName) {
        // 繁体 or 简体
        List<Artist> similarArtists = lastfmClient.getSimilarArtists(artistName, 5);
        if (CollectionUtils.isNotEmpty(similarArtists)) {
            return similarArtists.stream().sorted((n1, n2) -> {
                return (int)(n2.getMatchScore() -n1.getMatchScore());
            }).map(Artist::getName).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> scrapeSimilarTrack(String trackName, String artistName, Integer limit) {
        // 简体
        List<Track> similarTracks = lastfmClient.getSimilarTracks(trackName, artistName, limit);
        if (CollectionUtils.isNotEmpty(similarTracks)) {
            return similarTracks.stream().sorted((n1, n2) -> {
                return (int)(n2.getMatchScore() -n1.getMatchScore());
            }).map(Track::getName).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> topTrack(String artistName, Integer limit) {
        List<Track> topTracks = lastfmClient.getTopTracks(artistName, NumberUtils.INTEGER_ONE, limit);
        if (CollectionUtils.isEmpty(topTracks)) {
            return Collections.emptyList();
        }

        return topTracks.stream().map(Track::getName).collect(Collectors.toList());
    }


    @Override
    public void rateLimit() {
        RateLimiter limiter = RateLimiterConstants
                .limiters.computeIfAbsent(RateLimiterConstants.LAST_FM_API_LIMIT_KET,
                        key -> RateLimiter.create(6));

        // 尝试获取令牌
        if (!limiter.tryAcquire(1, TimeUnit.MILLISECONDS)) {
            throw new RateLimitException();
        }

    }
}

