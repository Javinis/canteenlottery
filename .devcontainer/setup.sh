# ... (在 apt-get install 之后) ...

# 设置环境变量 (写入 .bashrc 供 *未来* 终端使用)
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> /root/.bashrc
echo 'export ANDROID_HOME=/usr/local/android-sdk' >> /root/.bashrc
echo 'export PATH=$PATH:$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools' >> /root/.bashrc

# 立即加载这些变量 (供 *当前* 脚本使用)
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export ANDROID_HOME=/usr/local/android-sdk
export PATH=$PATH:$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin

# ... (sdkmanager --licenses 等) ...