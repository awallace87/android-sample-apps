package work.wander.wikiview.data.wiki.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * Represents the metadata of a Wikipedia page.
 *
 * @property wikiPageId the ID of the Wikipedia page
 * @property key the key of the metadata
 * @property title the title of the metadata
 * @property excerpt the excerpt of the metadata
 * @property description the description of the metadata
 * @property lastUpdated the time when the metadata was last updated
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
