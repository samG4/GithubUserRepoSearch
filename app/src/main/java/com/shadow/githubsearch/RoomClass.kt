package com.shadow.githubsearch

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Database
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Insert
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.Query
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Update
import android.content.Context
import com.google.gson.annotations.SerializedName

@Entity
data class SearchResult(
    @PrimaryKey
    var userName: String,
    var name: String
    ,
    var language: String?,
    @SerializedName("stargazers_count")
    var stars: String,
    var forks: String
)

@Dao
interface SearchDao {
    @Insert
    fun insert(searchResult: SearchResult)

    @Update
    fun update(searchResult: SearchResult)

    @Query("DELETE FROM SearchResult")
    fun deleteAll()

    @Query("SELECT * FROM SearchResult where userName = :name ")
    fun getSearchResult(name: String): LiveData<SearchResult>
}

@Database(entities = [SearchResult::class], version = 1)
abstract class GitSearchDatabase : RoomDatabase(){
    abstract fun searchDao(): SearchDao

    companion object {
        private var gitSearchResultDb: GitSearchDatabase? = null

        @Synchronized
        fun getInstance(context: Context): GitSearchDatabase {
            if (gitSearchResultDb == null) {
                gitSearchResultDb = Room
                    .databaseBuilder(context.applicationContext, GitSearchDatabase::class.java, "git_database")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return gitSearchResultDb!!
        }
    }
}