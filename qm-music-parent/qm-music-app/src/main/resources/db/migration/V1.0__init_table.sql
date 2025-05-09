-- V1__Initial_schema.sql
CREATE TABLE IF NOT EXISTS user (
                                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    username VARCHAR(50) NOT NULL UNIQUE,
                                    password VARCHAR(255) NOT NULL,
                                    email VARCHAR(100),
                                    is_admin INT NOT NULL DEFAULT 0, -- 0-普通用户 1-管理员
                                    force_password_change BOOLEAN NOT NULL DEFAULT false,
                                    nick_name VARCHAR(100),
                                    gmt_create DATETIME DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime')),
                                    gmt_modify DATETIME DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime'))
);

CREATE TRIGGER update_user_timestamp AFTER UPDATE ON user
BEGIN
    UPDATE user SET gmt_modify = STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW')
    WHERE id = NEW.id;
END;

-- 艺术家表（高频查询场景：按名称搜索）
CREATE TABLE artist (
                        id INTEGER PRIMARY KEY ,
                        name VARCHAR(255) NOT NULL UNIQUE,
                        first_letter VARCHAR(1) NOT NULL,
                        cover_art varCHAR(16),
                        gmt_create DATETIME DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime')),
                        gmt_modify DATETIME DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime'))
);


CREATE INDEX idx_artist_name ON artist(name);

CREATE TRIGGER IF NOT EXISTS update_artists_gmt_modify
    AFTER UPDATE ON artist
BEGIN
    UPDATE artist
    SET gmt_modify = CAST((julianday('now') - 2440587.5) * 86400000 AS INTEGER)
    WHERE id = NEW.id;
END;

-- 专辑表（Subsonic API常用过滤：年份/流派）
CREATE TABLE album (
                       id INTEGER PRIMARY KEY,
                       title VARCHAR(255) NOT NULL unique ,
                       artist_id INTEGER,
                       release_year CHAR(4),
                       genre VARCHAR(50),
                       song_count INTEGER NOT NULL,
                       duration INTEGER NOT NULL,
                       artist_name VARCHAR(128),
                       cover_art VARCHAR(128),
                       gmt_create DATETIME DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime')),
                       gmt_modify DATETIME DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime'))
);
CREATE INDEX idx_album_artist ON album(artist_id);
CREATE INDEX idx_album_genre_year ON album(genre, release_year);
CREATE INDEX idx_album_release_year ON album(release_year);
CREATE INDEX idx_album_gmt_create ON album(gmt_create);

CREATE TRIGGER IF NOT EXISTS update_album_gmt_modify
    AFTER UPDATE ON album
BEGIN
    UPDATE album
    SET gmt_modify = CAST((julianday('now') - 2440587.5) * 86400000 AS INTEGER)
    WHERE id = NEW.id;
END;


-- 歌曲表（核心表，结合Subsonic高频访问场景）
CREATE TABLE song (
                      id INTEGER PRIMARY KEY,
                      parent INTEGER ,
                      title VARCHAR(255) NOT NULL,
                      album_id INTEGER,
                      album_title varchar(64) ,
                      artist_id INTEGER ,
                      artist_name varchar(32) ,
                      size INTEGER NOT NULL,
                      suffix VARCHAR(16),
                      content_type VARCHAR(16),
                      cover_art VARCHAR(16),
                      genre VARCHAR(50),
                      year INTEGER ,
                      duration INTEGER  CHECK(duration > 0),
                      bit_rate INTEGER CHECK(bit_rate > 0),
                      file_path VARCHAR(512) NOT NULL UNIQUE,
                      file_hash CHAR(64) NOT NULL UNIQUE,
                      gmt_create DATETIME DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime')),
                      gmt_modify DATETIME DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime')),
                      file_last_modified INTEGER,
                      track varchar(4)
);
CREATE INDEX idx_song_album ON song(album_id);
CREATE INDEX idx_song_artist ON song(artist_id);
CREATE INDEX idx_song_title ON song(title);

CREATE TRIGGER IF NOT EXISTS update_song_gmt_modify
    AFTER UPDATE ON song
BEGIN
    UPDATE song
    SET gmt_modify = STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW')
    WHERE id = NEW.id;
END;

-- 播放列表（高频访问：用户查询+分页）
CREATE TABLE playlist (
                          id INTEGER PRIMARY KEY AUTOINCREMENT,
                          user_id INTEGER NOT NULL,
                          name VARCHAR(255) NOT NULL,
                          description VARCHAR(500),
                          cover_art VARCHAR(64),
                          song_count INTEGER NOT NULL,
                          duration INTEGER,
                          visibility INT NOT NULL DEFAULT 0, -- 0-私有 1-公开 2-分享链接
                          gmt_create DATETIME DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime')),
                          gmt_modify DATETIME DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime'))
);
CREATE INDEX idx_playlist_user ON playlist(user_id);
CREATE unique index idx_playlist_user_name ON playlist(user_id, name);

CREATE TRIGGER IF NOT EXISTS update_playlist_gmt_modify
    AFTER UPDATE ON playlist
BEGIN
    UPDATE playlist
    SET gmt_modify = CAST((julianday('now') - 2440587.5) * 86400000 AS INTEGER)
    WHERE id = NEW.id;
END;


CREATE TABLE play_history (
                              id INTEGER PRIMARY KEY AUTOINCREMENT,
                              user_id INTEGER NOT NULL,
                              song_id INTEGER NOT NULL,
                              client_type VARCHAR(50) NOT NULL,
                              play_count INT DEFAULT 1,
                              gmt_create DATETIME DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime')),
                              gmt_modify DATETIME DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime'))
);
CREATE INDEX idx_song_id ON play_history(song_id);
CREATE INDEX idx_history_time ON play_history(gmt_create);
CREATE unique index uni_idx_u_s_c ON play_history(user_id,song_id,client_type);



CREATE TRIGGER IF NOT EXISTS update_play_history_gmt_modify
    AFTER UPDATE ON play_history
BEGIN
    UPDATE play_history
    SET gmt_modify = CAST((julianday('now') - 2440587.5) * 86400000 AS INTEGER)
    WHERE id = NEW.id;
END;

-- 新增播放列表项表（原playlist_tracks升级）
CREATE TABLE playlist_item (
                               id INTEGER PRIMARY KEY AUTOINCREMENT,
                               playlist_id INTEGER NOT NULL,
                               song_id INTEGER NOT NULL,
                               sort_order INT NOT NULL,
                               gmt_create DATETIME DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime')),
                               gmt_modify DATETIME DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime'))
);
CREATE UNIQUE INDEX idx_playlist_order ON playlist_item(playlist_id, sort_order);

CREATE TRIGGER IF NOT EXISTS update_playlist_item_gmt_modify
    AFTER UPDATE ON playlist_item
BEGIN
    UPDATE playlist_item
    SET gmt_modify = CAST((julianday('now') - 2440587.5) * 86400000 AS INTEGER)
    WHERE id = NEW.id;
END;


-- 艺术家表（高频查询场景：按名称搜索）
CREATE TABLE user_star (
                           id INTEGER PRIMARY KEY AUTOINCREMENT,
                           user_id integer NOT NULL ,
                           star_type integer NOT NULL,
                           relation_id INTEGER NOT NULL,
                           gmt_create DATETIME DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime')),
                           gmt_modify DATETIME DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime'))
);

CREATE UNIQUE INDEX unique_user_star ON user_star(user_id, star_type, relation_id);

CREATE TRIGGER IF NOT EXISTS update_user_star_gmt_modify
    AFTER UPDATE ON user_star
BEGIN
    UPDATE user_star
    SET gmt_modify = CAST((julianday('now') - 2440587.5) * 86400000 AS INTEGER)
    WHERE id = NEW.id;
END;

CREATE TABLE artist_relation (
                                 id INTEGER PRIMARY KEY AUTOINCREMENT,
                                 artist_id INTEGER NOT NULL,
                                 type integer NOT NULL,
                                 relation_id INTEGER NOT NULL,
                                 gmt_create DATETIME DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime')),
                                 gmt_modify DATETIME DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime'))
);

CREATE UNIQUE INDEX idx_artist_relation ON artist_relation(artist_id, type, relation_id);

CREATE TRIGGER IF NOT EXISTS artist_relation_gmt_modify
    AFTER UPDATE ON artist_relation
BEGIN
    UPDATE artist_relation
    SET gmt_modify = CAST((julianday('now') - 2440587.5) * 86400000 AS INTEGER)
    WHERE id = NEW.id;
END;