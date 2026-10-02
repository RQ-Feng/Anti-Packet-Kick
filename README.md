# Anti Packet Kick

一个 **Minecraft 1.21.8** 客户端 Mod，防止服务器发送恶意构造的**超大数据包或损坏数据包**把你强行踢下线。

同时支持 **Fabric** 和 **NeoForge** 构建，配置界面基于 **Cloth Config API**。

> 本项目**改编自 [Meteor Client](https://github.com/MeteorDevelopment/meteor-client) 的部分代码**（GPL-3.0），核心逻辑抽自其 `anti-packet-kick` 模块，并重构为独立模组。详见 [License](#license) 说明。

---

## 防踢原理

通过 4 个 Mixin 注入修改客户端的底层网络逻辑：

* **解除解压限制**（`CompressionDecoderMixin`）
  * 注入 `CompressionDecoder#decode`
  * 将解压上限从 `8388608` 提升至 `Integer.MAX_VALUE`，防范解压炸弹。
* **解除 NBT 配额**（`FriendlyByteBufMixin`）
  * 注入 `FriendlyByteBuf.readNbt(ByteBuf)`
  * 移除 NbtAccounter 的内存配额限制（`unlimitedHeap()`）。
* **放开 Bundle 包上限**（`PacketBundlingMixin`）
  * 注入 `BundlerInfo$1$1#addPacket`
  * 单次合包上限从 `4096` 提升至 `Integer.MAX_VALUE`。
* **静默异常**（`ConnectionExceptionMixin`）
  * 注入 `Connection#exceptionCaught`
  * 捕获并忽略数据包损坏导致的异常（保留网络超时与编码错误），避免直接断开连接。

配置选项：`catchExceptions`（捕获异常）、`logExceptions`（记录异常日志）和 `chatNotifications`（检测到坏包时在聊天栏提示）。

---

## 项目结构

```
AntiPacketKick/
├── common/                  # 双端共享源码
│   └── src/main/
│       ├── java/antipacketkick/
│       │   ├── AntiPacketKick.java
│       │   ├── AntiPacketKickState.java
│       │   ├── AntiPacketKickNotifier.java
│       │   ├── platform/    # 极简跨端抽象（仅处理 configDir）
│       │   ├── config/      # Gson 本地配置 + Cloth Config 界面
│       │   └── mixin/       # 核心 Mixin 注入
│       └── resources/
├── fabric/                  # Fabric 适配层
└── neoforge/                # NeoForge 适配层
```

`common/` 源码会被两个平台各自打进产物，无需额外的跨平台 Remap 步骤。

---

## 构建与调试

> ⚠️ 本工程虽然放在 meteor-client 仓库内，但**是一个独立的 Gradle 构建**（有自己的 `settings.gradle`）。
> 必须**先进入 `AntiPacketKick/` 目录**再执行，或在仓库根目录用 `-p AntiPacketKick`。
> 直接在仓库根目录敲 `./gradlew :fabric:build`，跑的会是 meteor 自己的构建（它没有 `:fabric` 子项目），必然失败。

环境要求：**JDK 21+**（MC 1.21.8 强制要求）。

```bash
cd AntiPacketKick

# 构建 Fabric 版 -> fabric/build/libs/antipacketkick-fabric-1.0.0+1.21.8.jar
./gradlew :fabric:build

# 构建 NeoForge 版 -> neoforge/build/libs/antipacketkick-neoforge-1.0.0+1.21.8.jar
./gradlew :neoforge:build

# 同时构建双端
./gradlew build
```

开发环境运行：

```bash
cd AntiPacketKick
./gradlew :fabric:runClient
./gradlew :neoforge:runClient
```

### 映射说明（Mojmap）

为了让 `common/` 里的 Mixin 能跨端复用，Fabric 端（`fabric/build.gradle`）显式使用了 `loom.officialMojangMappings()`，与 NeoForge 一起统一使用官方 Mojmap。如果 Fabric 改用 Yarn，Mixin 里的类名和方法名就无法统一了。

---

## 配置界面

* 配置文件路径：`<游戏目录>/config/antipacketkick.json`
* **改动实时生效，无需重进世界或重启**：
  * 在配置界面点 **Save** 后立即应用；
  * 在游戏外直接编辑该 JSON 时，游戏内约 1 秒后自动重载。
* 打开方式：
  * **NeoForge**：模组列表中点击 **Config** 按钮，或按默认快捷键 **K**。
  * **Fabric**：按下快捷键 **K**（可在控制菜单的“按键绑定”中修改）。

---

## 依赖版本 (`gradle.properties`)

| 组件 | 版本 |
|------|------|
| Minecraft | 1.21.8 |
| Fabric Loader | 0.19.5 |
| Fabric API | 0.136.1+1.21.8 |
| Fabric Loom | 1.14.10 |
| NeoForge | 21.8.54 |
| Cloth Config | 19.0.147 |

> **提示**：Loom 维持在 `1.14.10` 是因为 `1.15+` 开始强制要求 Gradle 守护进程跑在 JDK 25 上。如果你的构建机已经是 JDK 25，可以升到 `1.18.2`。

---

## 跨版本维护注意项

升到新版 MC（例如 1.21.9+）时，重点关注这几个坑：

1. **匿名类混淆**：机制 3 的 Mixin 目标 `BundlerInfo$1$1` 是匿名内部类，换版本后先用 `javap` 检查 `4096` 常量落到了哪个类名下。
2. **常量硬编码**：机制 1 匹配的是 `intValue=8388608`，如果 Mojang 改了这个默认值，Mixin 就会失灵。
3. **方法签名**：机制 2 的 `readNbt` 方法签名跨大版本经常改动。
4. **底层稳定项**：机制 4 拦截的是标准的 Netty `exceptionCaught`，基本不受 MC 升级影响。
5. **KeyMapping 改动**：1.21.9+ 的 `KeyMapping` 构造函数中，category 参数从 `String` 改为了 `KeyMapping.Category` 枚举。

---

## License

本项目基于 **GNU General Public License v3.0 (GPL-3.0)** 开源。

核心 Mixin 逻辑改编自 [Meteor Client](https://github.com/MeteorDevelopment/meteor-client)（Copyright (c) Meteor Development，GPL-3.0），因此本模组整体（及编译后的 Jar）遵循 GPL-3.0 协议：

* **允许**：自由使用、修改及再分发（包含商业用途）。
* **要求**：衍生版本必须**公开完整源代码**，并保持 GPL-3.0 协议开源。
* **限制**：禁止将本项目或其衍生作品用于**闭源**或**混淆**的项目中。

完整条款见 [LICENSE](./LICENSE)。上游项目：https://github.com/MeteorDevelopment/meteor-client