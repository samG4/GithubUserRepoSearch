package com.shadow.githubsearch

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: GitSearchRepo by lazy {
        GitSearchRepo(application)
    }

    fun getSearchResults(query: String?=""): LiveData<List<GitRepository>>?{
        return query?.let {
            repository.getSearchedResult(query)
        }
    }

    fun getLanguages(): LiveData<List<String>>?{
        return repository.getLanguages()
    }

    fun insert(result: List<GitRepository>) {
        repository.insert(result)
    }

    fun update(result: GitRepository) {
        repository.update(result)
    }

    fun deleteAll(){
        repository.deleteAll()
    }

}