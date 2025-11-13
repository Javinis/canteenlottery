#!/bin/bash

echo ">>> 正在启动自定义安装 (Java 17, Android SDK, Git LFS)..."
set -e # 如果任何命令失败，立即停止脚本

# 1. 安装所有依赖
echo ">>> 正在安装系统依赖..."
sudo apt-get update
sudo apt-get install -y openjdk-17-jdk wget unzip git-lfs

# 2. 手动下载并安装 Android SDK 命令行工具
SDK_DIR="/usr/local/android-sdk"
sudo mkdir -p $SDK_DIR
cd /tmp 

echo ">>> 正在下载 Android SDK..."
sudo wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O cmdtools.zip

echo ">>> 正在解压 Android SDK..."
sudo unzip cmdtools.zip -d $SDK_DIR/cmdline-tools
sudo mv $SDK_DIR/cmdline-tools/cmdline-tools $SDK_DIR/cmdline-tools/latest
sudo rm cmdtools.zip 

# 3. 设置系统级环境变量
echo ">>> 正在设置系统级环境变量..."
PROFILE_SCRIPT="/etc/profile.d/android.sh"

sudo bash -c "cat > $PROFILE_SCRIPT" <<EOF
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export ANDROID_HOME=/usr/local/android-sdk
export PATH=\$PATH:\$JAVA_HOME/bin:\$ANDROID_HOME/cmdline-tools/latest/bin:\$ANDROID_HOME/platform-tools
EOF

sudo chmod +x $PROFILE_SCRIPT

# 4. 立即加载这些变量 (供 *当前* 脚本的剩余部分使用)
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export ANDROID_HOME=/usr/local/android-sdk
export PATH=$PATH:$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin

# 5. 使用 SDK Manager 自动接受许可并安装平台工具
echo ">>> 正在自动接受 SDK 许可..."
yes | sudo $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses

# 6. 【修改】安装 Gradle 需要的所有 SDK 版本
echo ">>> 正在安装平台工具 (34, 35, 36)..."
sudo $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0" "platforms;android-36" "build-tools;35.0.0"

# 7. 修复 gradlew 权限问题
echo ">>> 正在设置 gradlew 权限..."
# 确保在 /workspaces/canteenlottery (你的项目) 中
sudo chmod +x /workspaces/canteenlottery/gradlew

# 8. 激活 Git LFS
git lfs install

# 9. 【最关键的修复】
# 将 SDK 目录的“所有权”从 root 转交给当前用户
# 这样 Gradle (作为当前用户) 就可以在未来写入此目录
echo ">>> 正在修复 SDK 目录权限..."
sudo chown -R $(whoami) $ANDROID_HOME

echo ">>> 自定义安装完成。"