package work.wander.wikiview.data.wiki.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "wiki_page_default_html")
data class WikiPageDefaultHtml(
    @PrimaryKey(autoGenerate = false) val pageTitle: String,
    val html: String,
    val lastUpdated: Instant = Instant.now()
)