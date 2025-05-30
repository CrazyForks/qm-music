package com.github.chenqimiao.qmmusic.core.service.impl;

import com.github.chenqimiao.qmmusic.core.constant.ModelMapperTypeConstants;
import com.github.chenqimiao.qmmusic.core.dto.AlbumDTO;
import com.github.chenqimiao.qmmusic.core.service.AlbumService;
import com.github.chenqimiao.qmmusic.dao.DO.AlbumDO;
import com.github.chenqimiao.qmmusic.dao.repository.AlbumRepository;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 19:37
 **/
@Service("subsonicAlbumService")
public class SubsonicAlbumServiceImpl implements AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    @Resource
    private ModelMapper ucModelMapper;


    @Override
    public List<AlbumDTO> searchByName(String albumName, Integer pageSize, Integer offset) {
        List<AlbumDO> albums = albumRepository.searchByTitle(albumName, pageSize, offset);
        return ucModelMapper.map(albums, ModelMapperTypeConstants.TYPE_LIST_ALBUM_DTO);
    }

    @Override
    public List<AlbumDTO> batchQueryAlbumByAlbumIds(List<Long> albumIds) {
        if (CollectionUtils.isEmpty(albumIds)) {
            return Collections.emptyList();
        }
        List<AlbumDO> albums = albumRepository.queryByIds(albumIds);
        return ucModelMapper.map(albums, ModelMapperTypeConstants.TYPE_LIST_ALBUM_DTO);

    }

    @Override
    public AlbumDTO queryAlbumByAlbumId(Long albumId) {
        List<AlbumDTO> albums = this.batchQueryAlbumByAlbumIds(Lists.newArrayList(albumId));
        if (CollectionUtils.isNotEmpty(albums)) {
            return albums.getFirst();
        }
        return null;
    }


}
