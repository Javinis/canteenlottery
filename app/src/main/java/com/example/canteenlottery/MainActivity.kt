// MainActivity.kt
package com.example.canteenlottery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge // 沉浸式
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canteenlottery.ui.theme.CanteenLotteryTheme
import java.time.Duration // 【新增】用于计算时间差
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 启用沉浸式布局
        enableEdgeToEdge()

        // 2. setContent
        setContent {
            CanteenLotteryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CanteenAppScreen()
                }
            }
        }
    }
}

/**
 * 这是我们的主 Composable (可组合) 函数。
 */
@Composable
fun CanteenAppScreen() {
    // --- 1. 定义状态 (State) ---

    // a. 【修改】获取 *启动时* 的时间 (仅用于首次自动勾选)
    val initialTime = remember { LocalTime.now() }

    // b. 创建我们的“勾选状态”
    val canteenCheckStates = remember {
        val initialMap = allCanteens.associate {
            it.name to it.isOpen(initialTime) // 使用 initialTime
        }
        mutableStateMapOf(*initialMap.toList().toTypedArray())
    }

    // c. 抽签结果的状态
    val resultText = remember { mutableStateOf("---") }
    
    // d. 【新增状态】: 用于存储 "剩余关门时间" 的文本
    val closingTimeText = remember { mutableStateOf("") }

    // --- 2. 声明 UI 布局 ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            // 自动获取状态栏(顶部)和导航栏(底部)的 padding
            .windowInsetsPadding(WindowInsets.safeDrawing)
            // 在安全区域 *内部* 再加上你自己的 16.dp 边距
            .padding(16.dp)
    ) {

        // --- 顶部信息 ---
        Text(
            // 【修改】明确这是 "启动时" 的时间
            text = "当前时间: ${initialTime.format(DateTimeFormatter.ofPattern("HH:mm"))} (启动时)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "请选择要加入抽签的食堂：",
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 16.dp)
        )

        // --- 食堂列表 ---
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // 自动填充所有可用空间
                .padding(vertical = 8.dp)
        ) {
            items(allCanteens) { canteen ->
                CanteenRow(
                    canteenName = canteen.name,
                    isChecked = canteenCheckStates[canteen.name] ?: false,
                    onCheckedChange = { newCheckedState ->
                        canteenCheckStates[canteen.name] = newCheckedState
                    }
                )
            }
        }

        // --- 抽签按钮 ---
        Button(
            // 【修改】更新按钮的 onClick 监听器
            onClick = {
                // --- 抽签逻辑 (已更新) ---

                // 1. 【新增】获取 *点击时* 的准确时间
                val clickTime = LocalTime.now()
                
                // 2. 从状态 Map 中过滤出所有被勾选的食堂名称
                val lotteryList = canteenCheckStates
                    .filter { it.value } // 过滤 Map 中 value 为 true 的条目
                    .keys.toList() // 获取它们的 key (食堂名称)

                // 3. 处理边缘情况
                if (lotteryList.isEmpty()) {
                    resultText.value = "你没有选择任何食堂！"
                    closingTimeText.value = "" // 【新增】清空剩余时间
                    return@Button // 【新增】提前退出
                } 
                
                // 4. 执行抽签
                val winnerName = lotteryList.random()
                
                // 5. 【新增】找到获胜食堂的 *完整对象*
                val winnerCanteen = allCanteens.find { it.name == winnerName }

                // 6. 【新增】检查食堂在 *点击时* 是否仍然开放
                if (winnerCanteen == null || !winnerCanteen.isOpen(clickTime)) {
                    resultText.value = "哦豁！在你抽签时【${winnerName}】刚关门..."
                    closingTimeText.value = "请重新勾选并抽签"
                    return@Button // 提前退出
                }

                // 7. 【新增】找到当前激活的营业时段
                val activeSlot = winnerCanteen.slots.find { slot ->
                    (clickTime.isAfter(slot.first) || clickTime == slot.first) &&
                     clickTime.isBefore(slot.second)
                }

                // 8. 【新增】计算剩余时间
                if (activeSlot != null) {
                    val closeTime = activeSlot.second
                    
                    // 使用 java.time.Duration 计算时间差
                    val duration = Duration.between(clickTime, closeTime)
                    
                    val totalMinutesLeft = duration.toMinutes()
                    val hoursLeft = totalMinutesLeft / 60
                    val minutesPart = totalMinutesLeft % 60
                    
                    val closeTimeStr = closeTime.format(DateTimeFormatter.ofPattern("HH:mm"))

                    // 9. 更新两个状态
                    resultText.value = "恭喜你！今天去【${winnerName}】"
                    
                    if (hoursLeft > 0) {
                        closingTimeText.value = "该时段还剩 ${hoursLeft} 小时 ${minutesPart} 分钟关门 ( ${closeTimeStr} )"
                    } else {
                        closingTimeText.value = "该时段还剩 ${minutesPart} 分钟关门 ( ${closeTimeStr} )"
                    }
                    
                } else {
                    // 备用情况 (理论上不应该发生)
                    resultText.value = "恭喜你！今天去【${winnerName}】"
                    closingTimeText.value = "（无法计算关门时间）"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("开始抽签！", fontSize = 18.sp)
        }

        // --- 结果文本 (食堂名称) ---
        Text(
            text = resultText.value, // 文本 *订阅* resultText 状态
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.CenterHorizontally)
        )
        
        // --- 【新增】结果文本 (剩余时间) ---
        Text(
            text = closingTimeText.value, // 订阅剩余时间
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.secondary, // 使用主题的次要颜色
            modifier = Modifier
                .padding(top = 8.dp) // 放在主结果的下方
                .align(Alignment.CenterHorizontally)
        )
    }
}

/**
 * 这是一个可重用的 Composable，用于显示一行（复选框 + 文本）
 * (保持不变)
 */
@Composable
fun CanteenRow(
    canteenName: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    // Row 水平排列元素
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) } // 使整行都可以点击
            .padding(vertical = 4.dp)
    ) {
        // 复选框
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
        // 食堂名称
        Text(
            text = canteenName,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

/**
 * 这是 Android Studio 的预览功能
 * (保持不变)
 */
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CanteenLotteryTheme {
        CanteenAppScreen()
    }
}