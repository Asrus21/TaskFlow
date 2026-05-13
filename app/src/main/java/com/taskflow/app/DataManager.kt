package com.taskflow.app

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate

data class TodoItem(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    var done: Boolean = false
)

data class Mission(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val desc: String = "",
    var done: Boolean = false,
    val created: String = LocalDate.now().toString()
)

data class DailyTask(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val alarm: String = "",
    var doneDate: String? = null
)

object DataManager {
    private const val PREFS = "taskflow_prefs"
    private val gson = Gson()

    private fun prefs(ctx: Context) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun getUsername(ctx: Context) = prefs(ctx).getString("username", "") ?: ""
    fun setUsername(ctx: Context, name: String) = prefs(ctx).edit().putString("username", name).apply()

    fun getTodos(ctx: Context): MutableList<TodoItem> {
        val json = prefs(ctx).getString("todos", "[]") ?: "[]"
        return gson.fromJson(json, object : TypeToken<MutableList<TodoItem>>() {}.type) ?: mutableListOf()
    }
    fun saveTodos(ctx: Context, list: List<TodoItem>) =
        prefs(ctx).edit().putString("todos", gson.toJson(list)).apply()

    fun getMissions(ctx: Context): MutableList<Mission> {
        val json = prefs(ctx).getString("missions", "[]") ?: "[]"
        return gson.fromJson(json, object : TypeToken<MutableList<Mission>>() {}.type) ?: mutableListOf()
    }
    fun saveMissions(ctx: Context, list: List<Mission>) =
        prefs(ctx).edit().putString("missions", gson.toJson(list)).apply()

    fun getDailyTasks(ctx: Context): MutableList<DailyTask> {
        val json = prefs(ctx).getString("daily", "[]") ?: "[]"
        return gson.fromJson(json, object : TypeToken<MutableList<DailyTask>>() {}.type) ?: mutableListOf()
    }
    fun saveDailyTasks(ctx: Context, list: List<DailyTask>) =
        prefs(ctx).edit().putString("daily", gson.toJson(list)).apply()
}
