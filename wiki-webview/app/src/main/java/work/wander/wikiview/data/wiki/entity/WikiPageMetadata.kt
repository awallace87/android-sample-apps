package work.wander.wikiview.data.wiki.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * Data class representing the metadata and contents of a given Wikipedia page.
 */
@Entity(tableName = "wiki_page_metadata")
data class WikiPageMetadata(
    @PrimaryKey(autoGenerate = false) val wikiPageId: Long,
    val key: String,
    val title: String,
    val excerpt: String?,
    val description: String?,
    val lastUpdated: Instant = Instant.now(),
)
