package com.shadow.githubsearch

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class GitRepository(
    @PrimaryKey(autoGenerate = true)
    var repoId: Int = 0,
    var avatarUrl: String ?= null,
    @Ignore
    var owner : Owner?=null,
    var name: String ?= null,
    @SerializedName("full_name")
    var fullName: String ?=null,
    @SerializedName("watchers_count")
    var watchersCount : Int ?=0,
    var language: String ?= null,
    @SerializedName("stargazers_count")
    var stars: String ?= null,
    var forks: String ?= null,
    @SerializedName("contributors_url")
    var contributorsUrl : String ?= null,
    var description : String ?= null,
    @SerializedName("html_url")
    var url : String ?= null
): Parcelable

data class GitItem(
    var items: List<GitRepository>?=null
)

@Parcelize
data class Owner(
    @SerializedName("login")
    var name: String ?= null,
    @SerializedName("avatar_url")
    var avatarUrl: String ?= null
): Parcelable
