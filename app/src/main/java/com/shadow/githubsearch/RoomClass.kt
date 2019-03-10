package com.shadow.githubsearch

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Database
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Update
import android.content.Context

@Dao
interface SearchDao {
    @Insert
    fun insert(gitRepository: List<GitRepository>)

    @Update
    fun update(gitRepository: GitRepository)

    @Query("DELETE FROM GitRepository")
    fun deleteAll()

    @Query("SELECT * FROM GitRepository where name like '%' || :query || '%' order by watchersCount desc")
    fun getRepos(query: String): LiveData<List<GitRepository>>

    @Query("SELECT distinct language from GitRepository")
    fun getLanguages(): LiveData<List<String>>

}

@Database(entities = [GitRepository::class], version = 1)
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