// Canteen.kt
package com.example.canteenlottery
import java.time.LocalTime

/**
 * 这是我们的纯数据模型。
 * 它不关心 UI，只关心食堂的名称和营业时间。
 */
data class Canteen(
    val name: String,
    val slots: List<Pair<LocalTime, LocalTime>>
) {
    /**
     * 检查在给定时间是否开放，逻辑与你的 C++ 版本完全相同。
     */
    fun isOpen(currentTime: LocalTime): Boolean {
        return slots.any { (openTime, closeTime) ->
            // 逻辑: [openTime, closeTime)
            (currentTime.isAfter(openTime) || currentTime == openTime) &&
                    currentTime.isBefore(closeTime)
        }
    }
}