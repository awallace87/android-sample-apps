package work.wander.wikiview.data.wiki.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "wiki_page_mobile_html")
data class WikiPageMobileHtml(
    @PrimaryKey(autoGenerate = false) val pageTitle: String,
    val html: String,
    val lastUpdated: Instant = Instant.now()
    )

/*data class WikiPageMetadataWithMobileHtml(
    @Embedded val metadata: WikiPageMetadata,
    @Relation(parentColumn = "wikiPageId", entityColumn = "wikiPageId") val mobileHtml: WikiPageMobileHtml,
)*/
