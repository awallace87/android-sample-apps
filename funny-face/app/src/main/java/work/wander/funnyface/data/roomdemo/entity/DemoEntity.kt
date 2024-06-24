package work.wander.funnyface.data.roomdemo.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "examples")
data class DemoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val data: String,
    val lastModifiedDate: Instant = Instant.now(),
) {
}
