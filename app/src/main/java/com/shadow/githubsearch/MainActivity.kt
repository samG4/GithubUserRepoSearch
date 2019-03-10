package com.shadow.githubsearch

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import com.amitshekhar.DebugDB
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), RepositoryHandler, SearchHandler {
    override fun handleContributors(contributorList: List<Owner>) {

    }


    private lateinit var searchViewModel: SearchViewModel

    private var searchResultList = ArrayList<GitRepository>()
    private var query: String? = null
    private lateinit var searchAdapter: SearchAdapter
    private val searchWorker : SearchWorker by lazy {
        SearchWorker(this@MainActivity)
    }

    companion object {
        const val INSTANCE = "instance"
        const val TAG = "MainActivity"
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putSerializable(INSTANCE, searchResultList)
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG,DebugDB.getAddressLog())
        savedInstanceState?.let {
            try {
                searchResultList = savedInstanceState.getSerializable(INSTANCE) as ArrayList<GitRepository>
            } catch (e: Exception) {
                Log.e(TAG, e.message)
            }
        }
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                query = p0
                query?.let {
                    searchViewModel.getSearchResults(query)
                        ?.observe(this@MainActivity, Observer<List<GitRepository>> { repoList ->
                            if (repoList.isNullOrEmpty()) {
                                searchWorker.searchRepository(it)
                            } else {
                                searchResultList = repoList as ArrayList<GitRepository>
                                if (searchResultList.size > 0) {
                                    searchAdapter.gitRepositoryList = searchResultList
                                    searchAdapter.notifyDataSetChanged()
                                }
                            }
                        })
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0.isNullOrEmpty()) {
                    clearAdapter()
                }
                return false
            }
        })

        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this)
        searchAdapter = SearchAdapter(this, this)
        searchAdapter.gitRepositoryList = searchResultList
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = searchAdapter

    }

    private fun clearAdapter() {
        searchAdapter.gitRepositoryList.clear()
        searchAdapter.notifyDataSetChanged()
    }

    override fun handleSearch(gitItem: GitItem) {
        searchResultList.clear()
        searchResultList = ArrayList(gitItem.items?.take(10))
        Log.d(TAG, "${searchResultList.size}")
        if (searchResultList.size > 0) {
            searchWorker.compositeDisposable.dispose()
            searchResultList.map {
                it.avatarUrl = it.owner?.avatarUrl
            }
            searchViewModel.insert(searchResultList)
        } else {
            showAlert("0 repositories")
            clearAdapter()
        }
    }

    override fun errorResponse(error: Throwable) {
        this.showAlert(error.message)
        clearAdapter()
    }

    override fun handleRepository(gitRepoList: List<GitRepository>) {
    }

    override fun onRepositorySelected(repository: GitRepository) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra("Git", repository)
        startActivity(intent)
    }
}
