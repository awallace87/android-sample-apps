package work.wander.wikiview.data.wiki

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import work.wander.wikiview.data.wiki.entity.WikiPageMetadata

@Dao
interface MetadataDao {

    @Query("SELECT * FROM wiki_page_metadata")
    fun getAll(): Flow<List<WikiPageMetadata>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(metadata: List<WikiPageMetadata>) : List<Long>

}