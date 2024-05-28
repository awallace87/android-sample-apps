package work.wander.wikiview.data.wiki.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * Represents the default HTML of a Wikipedia page.
 *
 * @property pageTitle the title of the Wikipedia page
 * @property html the default HTML of the Wikipedia page
 * @property lastUpdated the time when the HTML was last updated
 */
@Entity(tableName = "wiki_page_default_html")
data class WikiPageDefaultHtml(
    @PrimaryKey(autoGenerate = false) val pageTitle: String,
    val html: String,
    val lastUpdated: Instant = Instant.now()
)