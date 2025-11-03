package com.example.passkeydemobynative2

///@author 11022
///@updateTime 2025/11/3 11:50
///@description 

import com.google.gson.JsonNull
import com.google.gson.JsonObject

/**
 * 移除 JsonObject 中所有值为 null 的字段。
 * 支持递归处理嵌套 JsonObject。
 */
fun JsonObject.removeNullFields(recursive: Boolean = true) {
    val iterator = entrySet().iterator()
    while (iterator.hasNext()) {
        val entry = iterator.next()
        val value = entry.value

        when {
            value.isJsonNull -> iterator.remove()
            recursive && value.isJsonObject -> value.asJsonObject.removeNullFields(true)
        }
    }
}