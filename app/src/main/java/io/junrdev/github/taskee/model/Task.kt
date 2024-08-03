package io.junrdev.github.taskee.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    var title: String,
    var description: String? = null,
    var priority: Priority,
    var isDone: Boolean = false
) : Parcelable

@Parcelize
sealed class Priority(val prop: String) : Parcelable {
    data object High : Priority("High")
    data object Medium : Priority("Medium")
    data object Normal : Priority("Normal")

    companion object {
        fun getPriorities() = listOf(
            High, Medium, Normal
        )

        fun get(prop: String): Priority {
            return when (prop) {
                High.prop -> High
                Medium.prop -> Medium
                Normal.prop -> Normal
                else -> Normal
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.prop
    @TypeConverter
    fun toPriority(prop: String): Priority = Priority.get(prop)
}