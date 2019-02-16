package com.shadow.githubsearch

import android.app.Application
import android.os.AsyncTask

class GitSearchRepo(application: Application) {

    private val gitSearchDatabase = GitSearchDatabase.getInstance(application)
    private val searchDao: SearchDao by lazy {
        gitSearchDatabase.searchDao()
    }

    fun getSearchedResult(name: String) = searchDao.getSearchResult(name)

    fun insert(searchResult: SearchResult) {
        InsertData(searchDao).execute(searchResult)
    }

    fun update(searchResult: SearchResult) {
        UpdateData(searchDao).execute(searchResult)
    }

    fun deleteAll(){
        DeleteData(searchDao).execute()
    }

    companion object {
        private class InsertData(val searchDao: SearchDao) : AsyncTask<SearchResult, Void, Void>() {
            override fun doInBackground(vararg params: SearchResult?): Void? {
                params[0]?.let {
                    searchDao.insert(it)
                }
                return null
            }
        }

        private class UpdateData(val searchDao: SearchDao) : AsyncTask<SearchResult, Void, Void>() {
            override fun doInBackground(vararg params: SearchResult?): Void? {
                params[0]?.let {
                    searchDao.update(it)
                }
                return null
            }
        }

        private class DeleteData(val searchDao: SearchDao) : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg params: Void): Void? {
                searchDao.deleteAll()
                return null
            }
        }
    }

}