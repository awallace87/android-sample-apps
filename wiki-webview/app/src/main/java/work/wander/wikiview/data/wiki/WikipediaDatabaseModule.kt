package work.wander.wikiview.data.wiki

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class WikipediaDatabaseModule {

    @Provides
    @Singleton
    fun providesWikipediaDatabase(
        @ApplicationContext context: Context
    ): WikipediaDatabase {
        return Room.databaseBuilder(
            context,
            WikipediaDatabase::class.java,
            "wikipedia.db"
        ).fallbackToDestructiveMigration().build()
    }

}