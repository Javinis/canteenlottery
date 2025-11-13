package com.example.canteenlottery
import java.time.LocalTime

// 辅助函数，使 LocalTime.of() 调用更短
private fun time(hour: Int, min: Int) = LocalTime.of(hour, min)

// 3. 创建所有食堂的主列表 (等同于你的 C++ std::array<Canteen, 9>)
val allCanteens = listOf(
    Canteen("毓秀餐厅一层", listOf(
        Pair(time(6, 30), time(8, 45)),
        Pair(time(10, 45), time(12, 45)),
        Pair(time(16, 30), time(18, 45))
    )),
    Canteen("毓秀餐厅二层", listOf(
        Pair(time(6, 30), time(8, 45)),
        Pair(time(10, 45), time(12, 45)),
        Pair(time(16, 30), time(18, 45))
    )),
    Canteen("尚德餐厅", listOf(
        Pair(time(6, 30), time(8, 45)),
        Pair(time(10, 45), time(12, 45)),
        Pair(time(16, 30), time(18, 45))
    )),
    Canteen("聚贤阁", listOf(
        Pair(time(6, 30), time(9, 30)),
        Pair(time(10, 45), time(14, 0)),
        Pair(time(16, 0), time(21, 0))
    )),
    Canteen("乐膳轩", listOf(
        Pair(time(6, 30), time(9, 30)),
        Pair(time(10, 45), time(14, 0)),
        Pair(time(16, 0), time(21, 0))
    )),
    Canteen("民族餐厅", listOf(
        Pair(time(6, 30), time(9, 30)),
        Pair(time(10, 30), time(13, 30)),
        Pair(time(16, 30), time(21, 0))
    )),
    Canteen("欣荣居", listOf(
        Pair(time(10, 30), time(15, 30)),
        Pair(time(16, 30), time(21, 30))
    )),
    Canteen("国教餐厅", listOf(
        Pair(time(10, 30), time(14, 0)),
        Pair(time(16, 30), time(21, 0))
    )),
    Canteen("麦当劳", listOf(
        Pair(time(7, 0), time(23, 0))
    ))
)