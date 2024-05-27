package work.wander.wikiview.data.wiki

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import work.wander.wikiview.data.common.room.converters.InstantConverter
import work.wander.wikiview.data.wiki.entity.WikiPageMetadata
import work.wander.wikiview.data.wiki.entity.WikiPageMobileHtml

@Database(entities = [WikiPageMetadata::class, WikiPageMobileHtml::class], version = 2, exportSchema = false)
@TypeConverters(value = [InstantConverter::class])
abstract class WikipediaDatabase : RoomDatabase() {

    abstract fun metadataDao(): MetadataDao

    abstract fun pageHtmlDao(): PageHtmlDao

}