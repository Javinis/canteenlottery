#!/bin/bash

# 这是一个自定义的安装脚本，用于绕过 'ghcr.io' 权限问题

echo ">>> 正在启动自定义安装 (Java + Android SDK)..."

# 1. 安装必要的依赖 (Java 17, wget, unzip)
# 我们使用 sudo，因为基础镜像是 root
apt-get update
apt-get install -y openjdk-17-jdk wget unzip

# 2. 手动下载并安装 Android SDK 命令行工具
SDK_DIR="/usr/local/android-sdk"
mkdir -p $SDK_DIR
cd /tmp

# 从 Google 官方源下载
wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O cmdtools.zip

# 解压并重命名文件夹 (这是 Android SDK 的标准操作)
unzip cmdtools.zip -d $SDK_DIR/cmdline-tools
mv $SDK_DIR/cmdline-tools/cmdline-tools $SDK_DIR/cmdline-tools/latest
rm cmdtools.zip

# 3. 设置环境变量 (让 Gradle 能找到它们)
# 我们将它们写入 .bashrc，以便在所有新终端中都可用
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> /root/.bashrc
echo 'export ANDROID_HOME=/usr/local/android-sdk' >> /root/.bashrc
echo 'export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools' >> /root/.bashrc

# 立即加载这些变量，以便在脚本的剩余部分使用
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export ANDROID_HOME=/usr/local/android-sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin

# 4. 自动接受 SDK 许可
yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses

# 5. 安装构建所需的平台工具和 API
$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

# 6. 顺便给 gradlew 加上执行权限
chmod +x /workspaces/canteenlottery/gradlew

echo ">>> 自定义安装完成。"chmod +x .devcontainer/setup.sh