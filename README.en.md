# QM-Music 🎵
[中文](README.md) | [English](README.en.md)
🎧☁️ Your Private Music Service
[![Docker Pulls](https://img.shields.io/docker/pulls/qmmusic/qm-music)](https://hub.docker.com/r/qmmusic/qm-music)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

**QM-Music** is a private cloud music server based on Subsonic, designed as a lightweight high-performance solution for music enthusiasts. Supports one-click Docker deployment and provides full compatibility with Subsonic ecosystem clients (e.g. StreamMusic/Audinaut), enabling secure access to your personal music library anytime, anywhere.


## 🌟 Core Features

- 🐳 **Docker Containerization** - Instant deployment with zero environment dependencies
- 🌱 **Lightweight Deployment** - Requires only ~150MB memory usage, low resource consumption, compatible with Raspberry Pi and other embedded devices
- 🎧 **Subsonic Protocol Compatibility** - ​​Seamless integration with any Subsonic-compatible client​​
- ⚡ **High-Performance Streaming** - Low-latency media transmission
- 🔄 **Smart Transcoding** - On-demand libmp3lame/acc transcoding to save bandwidth (optional)
- 📁 **Multi-Format Support** - Comprehensive compatibility with MP3/FLAC/AAC/WAV formats
- 🔒 **Self-Hosted Solution** - Full control over your music data

## 🚀 Getting Started

### Basic Deployment
#### docker run
```bash
docker run -d \
  --name qm-music \
  -p 6688:6688 \
  -v [host_music_file_path]:/data/qm-music/music_dir \
  -v [host_path]:/data/qm-music/db \
  -e QM_FFMPEG_ENABLE=true \
  -e TZ=Asia/Shanghai \
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
      - [host_path]:/data/qm-music/db
    environment:
      - QM_FFMPEG_ENABLE=true
      - TZ=Asia/Shanghai
    restart: unless-stopped
```

### ⚙️ Configuration Guide
- **Environment Variables**  
  `QM_FFMPEG_ENABLE=true` Enable intelligent audio transcoding (recommended for mobile users). Automatically switches between libmp3lame/acc encoding based on network conditions to optimize bandwidth usage (disabled by default).
  `TZ=Asia/Shanghai` Mandatory timezone configuration.

- **Volume Mounts**  
  `/data/qm-music/music_dir`：Music files storage directory
  `/data/qm-music/db`：Database and metadata storage directory (avoid storing other files)

### 🖥️ Initial Setup
1. Access admin interface at http://[Server IP]:[Port]
2. Use default credentials:
   **Username**：`admin`  
   **Password**：`admin`
3. Change default password immediately after first login
4. Navigate to "Library Management" and click 【Refresh Library】
5. Wait for metadata parsing completion (check progress via logs)
6. Configure client apps（StreamMusic/Substreamer等）with:
   ```properties
   Server: http://[Server IP]:6688
   Account: Your modified admin username
   Password: Your modified admin password
   ```
## 📜 License
This project is released under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0). You may:
- Retain original copyright notices in derivative works
- Clearly document modifications in distributed derivatives
- No warranties expressed or implied

Full license text is available in [LICENSE](LICENSE) file. Your use constitutes acceptance of these terms.