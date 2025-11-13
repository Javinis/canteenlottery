#!/bin/bash

# 这是一个自定义的安装脚本，用于绕过 'ghcr.io' 权限问题
# 它在 postCreateCommand 中运行，以 root 身份执行。

echo ">>> 正在启动自定义安装 (Java 17, Android SDK, Git LFS)..."
set -e # 如果任何命令失败，立即停止脚本

# 1. 更新包列表并安装所有依赖
# - openjdk-17-jdk: Java 开发工具包 (用于 Gradle 和编译)
# - wget: 用于下载
# - unzip: 用于解压
# - git-lfs: 修复你的 "pre-push" 错误
apt-get update
apt-get install -y openjdk-17-jdk wget unzip git-lfs

# 2. 手动下载并安装 Android SDK 命令行工具
SDK_DIR="/usr/local/android-sdk"
mkdir -p $SDK_DIR
cd /tmp # 切换到临时目录进行下载

echo ">>> 正在下载 Android SDK..."
# 这是你之前日志中提到的版本，很稳定
wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O cmdtools.zip

echo ">>> 正在解压 Android SDK..."
unzip cmdtools.zip -d $SDK_DIR/cmdline-tools
# 【关键】Android SDK 需要我们将 'cmdline-tools' 移动到 'latest' 子目录中
mv $SDK_DIR/cmdline-tools/cmdline-tools $SDK_DIR/cmdline-tools/latest
rm cmdtools.zip # 清理下载的 zip 文件

# 3. 设置环境变量 (让 Gradle 和终端能找到这些工具)
# 我们将它们写入 .bashrc，以便在 *未来* 的所有新终端中都可用
# (我们在这个容器中是 root，所以写入 /root/.bashrc)
echo ">>> 正在设置环境变量..."
echo '# Java 和 Android SDK 环境变量' >> /root/.bashrc
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> /root/.bashrc
echo 'export ANDROID_HOME=/usr/local/android-sdk' >> /root/.bashrc
# 注意: 我们把 $JAVA_HOME/bin 也加入了 PATH
echo 'export PATH=$PATH:$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools' >> /root/.bashrc

# 4. 立即加载这些变量 (供 *当前* 脚本的剩余部分使用)
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export ANDROID_HOME=/usr/local/android-sdk
export PATH=$PATH:$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin

# 5. 使用 SDK Manager 自动接受许可并安装平台工具
# (这是最容易出错的一步，现在应该可以了)
echo ">>> 正在自动接受 SDK 许可..."
yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses

echo ">>> 正在安装平台工具和 API 34..."
$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

# 6. 修复你之前遇到的 gradlew 权限问题
echo ">>> 正在设置 gradlew 权限..."
chmod +x /workspaces/canteenlottery/gradlew

# 7. 激活 Git LFS (修复你的 push 错误)
git lfs install

echo ">>> 自定义安装完成。"