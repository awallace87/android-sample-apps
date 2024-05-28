package work.wander.wikiview.data.wiki

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import work.wander.wikiview.data.wiki.entity.WikiPageDefaultHtml
import work.wander.wikiview.data.wiki.entity.WikiPageMobileHtml

@Dao
interface PageHtmlDao {

    @Query("SELECT * FROM wiki_page_mobile_html WHERE pageTitle = :pageTitle")
    suspend fun getMobileHtmlForPage(pageTitle: String): WikiPageMobileHtml?

    @Insert
    suspend fun insertMobileHtmlForPage(mobileHtml: WikiPageMobileHtml): Long

    @Query("SELECT * FROM wiki_page_default_html WHERE pageTitle = :pageTitle")
    suspend fun getDefaultHtmlForPage(pageTitle: String): WikiPageDefaultHtml?

    @Insert
    suspend fun insertDefaultHtmlForPage(defaultHtml: WikiPageDefaultHtml): Long
}