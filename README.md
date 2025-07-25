# QM-Music 🎵
[中文](README.md) | [English](README.en.md)
🎧☁️ Your Private Music Service
[![Docker Pulls](https://img.shields.io/docker/pulls/qmmusic/qm-music)](https://hub.docker.com/r/qmmusic/qm-music)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

**QM-Music** 是一个基于 Subsonic 构建的轻量级私有云音乐服务器，专为音乐爱好者设计的轻量级高性能解决方案。支持 Docker 一键部署，完美兼容 Subsonic 生态客户端（如音流/Amperfy/substreamer/feishin/music-assistant），让您随时随地安全访问个人音乐库。
<div style="display: flex; gap: 5px;">
  <img src="docs/assets/433026310-55019589-116b-4afd-ba90-dc0b7a704520.png" style="width:25%" />
  <img src="docs/assets/433026335-9ddbb5e3-8bb0-4336-b282-9f0d712a12d7.png" style="width:25%" />
  <img src="docs/assets/433027765-68ecdc95-df2c-4424-983d-dc44103af125.png" style="width:25%" />
  <img src="docs/assets/433027782-26769aea-be11-447c-89c1-524827fc6175.png" style="width:25%" />
  <img src="docs/assets/433026371-93d66adb-8659-4351-ad57-731c16668224.png" style="width:25%" />
  <img src="docs/assets/433026401-e91adfc7-2721-4b5e-82df-5a967fc6ae87.png" style="width:25%" />
  <img src="docs/assets/433033515-cc6d37dd-0d06-4dd9-8466-673f5cc2c02c.png" style="width:25%" />
  <img src="docs/assets/433033525-7a207bdd-26c8-48d2-ace3-227130ef0374.png" style="width:25%" />
  <img src="docs/assets/433033533-de036d64-b1db-45c4-b545-9c75bc2bc2cc.png" style="width:25%" />
  <img src="docs/assets/IMG118.jpg" style="width:25%" />
  <img src="docs/assets/IMG119.jpg" style="width:25%" />
  <img src="docs/assets/IMG120.jpg" style="width:25%" />
</div>


## 🌟 核心特性

- 🐳 **Docker 容器化部署** - 快速启动，零环境依赖
- 🌱 **轻量级部署** - 仅需约150MB内存占用，低资源消耗
- 🎧 **Subsonic 协议兼容** - 可在支持 subsonic api 的客户端连接使用
- ⚡ **高性能媒体服务** - 低延迟流媒体传输
- 🔄 **智能转码支持** - 按需开启 libmp3lame/acc 转码节省流量
- 📁 **多格式支持** - 全面兼容 MP3/FLAC/AAC/WAV 等格式
- 🔒 **私有化部署** - 完全掌控您的音乐数据

## 🚀 快速开始

### 基本部署
#### docker run
```bash
docker run -d \
  --name qm-music \
  -p 6688:6688 \
  -v [host_music_file_path]:/data/qm-music/music_dir \
  -v [host_path_db_path]:/data/qm-music/db \
  -v [host_path_cache_path]:/data/qm-music/cache \
  -e QM_FFMPEG_ENABLE=true \
  -e TZ=Asia/Shanghai \
  -e QM_SPOTIFY_ENABLE=false \
  -e QM_SPOTIFY_CLIENT_ID=[QM_SPOTIFY_CLIENT_ID] \
  -e QM_SPOTIFY_CLIENT_SECRET=[QM_SPOTIFY_CLIENT_SECRET] \
  -e QM_LASTFM_ENABLE=false \
  -e QM_LASTFM_API_KEY=[QM_LASTFM_API_KEY] \
  -e QM_REFRESH_AUTO=true \
  --restart unless-stopped \
  qmmusic/qm-music:latest
```
#### docker compose
```bash
version: '3'

services:
  qm-music:
    container_name: qm-music
    image: qmmusic/qm-music:latest
    ports:
      - "6688:6688"
    volumes:
      - [host_music_file_path]:/data/qm-music/music_dir
      - [host_path_db_path]:/data/qm-music/db
      - [host_path_cache_path]:/data/qm-music/cache 
    environment:
      - QM_FFMPEG_ENABLE=true
      - TZ=Asia/Shanghai
      - QM_SPOTIFY_ENABLE=false
      - QM_SPOTIFY_CLIENT_ID=[QM_SPOTIFY_CLIENT_ID]
      - QM_SPOTIFY_CLIENT_SECRET=[QM_SPOTIFY_CLIENT_SECRET]
      - QM_LASTFM_ENABLE=false
      - QM_LASTFM_API_KEY=[QM_LASTFM_API_KEY]
      - QM_REFRESH_AUTO=true
    restart: unless-stopped
```

### ⚙️ 配置说明
- **环境变量**  
  - `QM_FFMPEG_ENABLE=true` 启用智能音频转码（推荐在户外使用的用户开启），支持按网络状况自动切换 libmp3lame/acc 编码，有效节省流量消耗（默认关闭）
  - `TZ=Asia/Shanghai` 请务必根据所在地区设置
  - `QM_SPOTIFY_ENABLE=false` 非必须 开启后，可以获得更多元数据支持 [SPOTIFY KEY 申请地址](https://developer.spotify.com/documentation/wes-api) 
  - `QM_SPOTIFY_CLIENT_ID` 非必须
  - `QM_SPOTIFY_CLIENT_SECRET` 非必须
  - `QM_LASTFM_ENABLE=false` 非必须 开启后，可以获得更多元数据支持 [LAST FM KEY 申请地址](https://www.last.fm/api#getting-started)
  - `QM_LASTFM_API_KEY` 非必须
  - `QM_REFRESH_AUTO=true` 非必须 定时刷新曲库开关 默认开启
  - `QM_CLEAN_PLAY_HISTORY_AUTO=true` 非必须 定时清理播放记录开关 默认开启
  - `QM_SAVE_PLAY_HISTORY_MONTH=24` 非必须 保留播放记录月份数 默认24
- **卷挂载**  
  - `/data/qm-music/music_dir`：音乐文件存储目录
  - `/data/qm-music/db`：数据库及元数据存储目录（请勿存放其他文件）
  - `/data/qm-music/cache`: 缓存文件
### 🖥️ 初始化使用
1. 访问 `http://[Server IP]:[Port]` 进入管理界面
2. 使用默认凭证登录：  
   **用户名**：`admin`  
   **密码**：`admin`
3. 在首页立即更改默认密码
4. 前往「曲库管理」点击【刷新曲库】按钮
5. 等待曲目元数据解析完成（可通过日志查看进度）
6. 在客户端应用（音流/Amperfy/substreamer/music-assistant等）使用以下连接参数：
   ```properties
   服务器地址: http://[服务器IP]:6688
   账户: 修改后的管理员账号
   密码: 修改后的管理员密码
   ```

## 📜 开源协议
本项目基于 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 开源协议发布，您可以：
- 二次开发时需保留原始版权声明
- 修改后的衍生版本需做明显变更说明
- 不提供任何明示或暗示的担保

完整协议内容请查看 [LICENSE](LICENSE) 文件，使用即表示您同意协议条款。

## 🚨 免责声明

本项目仅供学习和研究目的，不得用于任何商业活动。用户在使用本项目时应遵守所在地区的法律法规，对于违法使用所导致的后果，本项目及作者不承担任何责任。
本项目可能存在未知的缺陷和风险（包括但不限于设备损坏和账号封禁等），使用者应自行承担使用本项目所产生的所有风险及责任。
作者不保证本项目的准确性、完整性、及时性、可靠性，也不承担任何因使用本项目而产生的任何损失或损害责任。
使用本项目即表示您已阅读并同意本免责声明的全部内容。
   
## 📋 功能概览

### 🚀 核心功能
- **Docker 容器化部署** - 一键启动，轻量无依赖
- **Subsonic 协议全兼容** - 完美适配音流/Amperfy/substreamer/music-assistant 等客户端
- **智能音频转码** - 动态切换 libmp3lame/acc 编码（按需启用）,节省带宽流量
- **多格式支持** - MP3/FLAC/AAC/WAV 等主流格式全覆盖
- **低资源占用** - 运行内存仅需约 150MB

### 🎧 用户体验
- **多用户体系** - 独立账号系统与权限管理
- **个性化歌单** - 支持创建/管理自定义播放列表
- **收藏与喜欢** - 实时同步的收藏夹功能
- **歌词同步** - 智能匹配本地与在线歌词资源

### 🗂️ 音乐管理
- **自动化曲库** - 定时监测音乐目录变动，自动刷新元数据
- **高效元数据解析** - 精准识别 ID3 标签与专辑信息
- **结构化展示** - 专辑/艺术家/流派多维分类浏览
- **云端播放统计** - 记录播放历史与用户偏好

### 🔍 探索发现
- **全局搜索** - 支持歌曲/专辑/艺术家关键词检索
- **智能推荐** - 基于风格的相似歌曲与歌手推荐
- **流派分类** - 按音乐风格快速筛选曲目

### 🌐 生态扩展
* [![Spotify](https://img.shields.io/badge/Spotify-%231ED760.svg?logo=spotify&logoColor=white)](https://developer.spotify.com/)
* [![Last.fm](https://img.shields.io/badge/Last.fm-%23D51007.svg?logo=last.fm&logoColor=white)](https://www.last.fm/api)
* [![Kugou](https://img.shields.io/badge/Kugou-%2300AFF0.svg?logo=kugou&logoColor=white)](https://www.kugou.com/)

### ⏭️ 开发路线图
#### 基础设施
- [ ] 支持外置数据库

#### 客户端生态
- [ ] 跨平台客户端开发
- [ ] Web 播放器功能增强

#### 质量保障
- [ ] 单元测试覆盖率提升

...

## ☕ 赞助支持
如果这个项目对您有帮助，请帮忙点个Star或打赏支持(请作者喝一杯快乐水(,,・ω・,,))！您的支持是我们持续维护和改进的最大动力！

<div align="center" style="margin-top:30px">
  <table>
    <tr>
      <th>微信赞赏</th>
      <th>支付宝赞赏</th>
    </tr>
    <tr>
      <td><img src="qm-music-parent/qm-music-app/src/main/resources/static/img/wechat_qr.webp" width="500"></td>
      <td><img src="qm-music-parent/qm-music-app/src/main/resources/static/img/alipay_qr.webp" width="500"></td>
    </tr>
    <tr>
      <td colspan="2" align="center" style="padding-top:20px">👉 您的支持将帮助我们持续改进 QM-Music</td>
    </tr>
  </table>
</div>
