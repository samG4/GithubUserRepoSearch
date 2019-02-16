package com.shadow.githubsearch

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: GitSearchRepo by lazy {
        GitSearchRepo(application)
    }
    fun getSearchResults(name: String?=""): LiveData<SearchResult>?{
        return name?.let {
            repository.getSearchedResult(name)
        }
    }

    fun insert(result: SearchResult) {
        repository.insert(result)
    }

    fun update(result: SearchResult) {
        repository.update(result)
    }

    fun deleteAll(){
        repository.deleteAll()
    }

}