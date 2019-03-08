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
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class GitRepository(
    @PrimaryKey
    var userName: String,
    @SerializedName("avatar_url")
    var avatarUrl: String ?= null,
    var name: String ?= null,
    @SerializedName("full_name")
    var fullName: String ?=null,
    @SerializedName("watchers_count")
    var watchersCount : Int ?=0,
    @SerializedName("")
    var language: String ?= null,
    @SerializedName("stargazers_count")
    var stars: String ?= null,
    var forks: String ?= null,

): Parcelable

@Dao
interface SearchDao {
    @Insert
    fun insert(gitRepository: GitRepository)

    @Update
    fun update(gitRepository: GitRepository)

    @Query("DELETE FROM GitRepository")
    fun deleteAll()

    @Query("SELECT * FROM GitRepository where userName = :name ")
    fun getSearchResult(name: String): LiveData<GitRepository>
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