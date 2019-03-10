package com.shadow.githubsearch

import android.app.Application
import android.os.AsyncTask
import android.util.Log

class GitSearchRepo(application: Application) {

    private val gitSearchDatabase = GitSearchDatabase.getInstance(application)
    private val searchDao: SearchDao by lazy {
        gitSearchDatabase.searchDao()
    }

    fun getSearchedResult(query: String) = searchDao.getRepos(query)
    fun getLanguages() = searchDao.getLanguages()

    fun insert(gitRepository: List<GitRepository>) {
        InsertData(searchDao).execute(gitRepository)
    }

    fun update(gitRepository: GitRepository) {
        UpdateData(searchDao).execute(gitRepository)
    }

    fun deleteAll() {
        DeleteData(searchDao).execute()
    }

    companion object {
        private class InsertData(val searchDao: SearchDao) : AsyncTask<List<GitRepository>, Void, Void>() {
            override fun doInBackground(vararg params: List<GitRepository>?): Void? {
                params[0]?.let {
                        Log.d("Async","${it.size}")
                        searchDao.insert(it)
                }
                return null
            }
        }

        private class UpdateData(val searchDao: SearchDao) : AsyncTask<GitRepository, Void, Void>() {
            override fun doInBackground(vararg params: GitRepository?): Void? {
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