package com.shadow.githubsearch

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), SearchHandler {

    private lateinit var searchViewModel: SearchViewModel
    private var searchResultList = ArrayList<SearchResult>()
    private lateinit var searchAdapter: SearchAdapter
    val INSTANCE = "instance"
    private var query: String? = null

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putSerializable(INSTANCE, searchResultList)
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        savedInstanceState?.let {
            try{
                searchResultList = savedInstanceState.getSerializable(INSTANCE) as ArrayList<SearchResult>
            }catch (e: Exception){
                Log.e("***",e.message)
            }
        }
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                query = p0
                query?.let {
                    searchViewModel.getSearchResults(query)?.observe(this@MainActivity, Observer<SearchResult> {
                        searchResult->
                        if(searchResult==null){
                            SearchWorker(it, this@MainActivity).doRepoSearch()
                        } else {
                            searchResultList = convertSrchResultToList(searchResult)
                            if(searchResultList.size>0){
                                searchAdapter.searchResultList = searchResultList
                                searchAdapter.notifyDataSetChanged()
                            }
                            else{
                                showAlert("0 repositories")
                                clearAdapter()
                            }
                        }
                    })
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if(p0.isNullOrEmpty()){
                    clearAdapter()
                }
                return false
            }
        })

        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this)
        searchAdapter = SearchAdapter(this)
        searchAdapter.searchResultList = searchResultList
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = searchAdapter

    }

    override fun handleResponse(searchResult: List<SearchResult>) {
        searchResultList.clear()
        searchResultList = ArrayList(searchResult)
        if(searchResultList.size>0){
            query?.let {
                searchViewModel.insert(mapSrchResp(it ,searchResult))
            }
        }else{
            showAlert("0 repositories")
            clearAdapter()
        }
    }

    override fun errorResponse(error: Throwable) {
        this.showAlert(error.message)
        clearAdapter()
    }

    private fun clearAdapter(){
        searchAdapter.searchResultList.clear()
        searchAdapter.notifyDataSetChanged()
    }
}
