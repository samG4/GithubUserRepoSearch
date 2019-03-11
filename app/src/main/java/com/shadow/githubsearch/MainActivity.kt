package com.shadow.githubsearch

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import com.shadow.githubsearch.adapter.GvLangAdapter
import com.shadow.githubsearch.adapter.SearchAdapter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), RepositoryHandler, SearchHandler, View.OnClickListener, FilterHandler {
    private lateinit var searchViewModel: SearchViewModel
    private val dialog: Dialog by lazy {
        this.getDialog(R.layout.filter_view)
    }
    private val gvLang: GridView by lazy {
        dialog.findViewById<GridView>(R.id.gvLang)
    }
    private val etMinStar: EditText by lazy {
        dialog.findViewById<EditText>(R.id.etMinStar)
    }
    private val etMaxStar: EditText by lazy {
        dialog.findViewById<EditText>(R.id.etMaxStar)
    }
    private val btnApply: Button by lazy {
        dialog.findViewById<Button>(R.id.btnApply)
    }
    private val btnReset: Button by lazy {
        dialog.findViewById<Button>(R.id.btnReset)
    }
    private val searchWorker: SearchWorker by lazy {
        SearchWorker(this@MainActivity)
    }
    private var filLanguages = ArrayList<String>()
    private var repoSearchList = ArrayList<GitRepository>()
    private var query: String? = null
    private lateinit var searchAdapter: SearchAdapter
    private var languageList = ArrayList<String>()

    companion object {
        const val INSTANCE = "instance"
        const val FIL_LANG= "filtered"
        const val MIN_VALUE = "min"
        const val MAX_VALUE = "max"
        const val TAG = "MainActivity"
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putSerializable(INSTANCE, searchAdapter.gitRepositoryList)
        outState?.putSerializable(FIL_LANG, filLanguages)
        outState?.putString(MIN_VALUE, etMinStar.text.toString())
        outState?.putString(MAX_VALUE, etMaxStar.text.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, DebugDB.getAddressLog())
        savedInstanceState?.let {
            try {
                repoSearchList = it.getSerializable(INSTANCE) as ArrayList<GitRepository>
                filLanguages = it.getSerializable(FIL_LANG) as ArrayList<String>
                etMinStar.setText(it.getString(MIN_VALUE))
                etMaxStar.setText(it.getString(MAX_VALUE))
                null
            } catch (e: Exception) {
                Log.e(TAG, e.message)
            }
        }
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        searchViewModel.getLanguages()?.observe(
            this@MainActivity, Observer<List<String>> { languageList ->
                this.languageList = languageList as ArrayList<String>
            }
        )
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                query = p0
                query?.let {
                    searchViewModel.getSearchResults(query)
                        ?.observe(this@MainActivity, Observer<List<GitRepository>> { repoList ->
                            if (repoList.isNullOrEmpty()) {
                                searchWorker.searchRepository(it)
                            } else {
                                repoSearchList = repoList as ArrayList<GitRepository>
                                if (repoSearchList.size > 0) {
                                    searchAdapter.gitRepositoryList = repoSearchList
                                    searchAdapter.notifyDataSetChanged()
                                }
                            }
                        })
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        searchAdapter = SearchAdapter(this, this)
        searchAdapter.gitRepositoryList = repoSearchList
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = searchAdapter
        imgFilter.setOnClickListener(this)
        btnApply.setOnClickListener(this)
        btnReset.setOnClickListener(this)
    }

    private fun clearAdapter() {
        searchAdapter.gitRepositoryList.clear()
        searchAdapter.notifyDataSetChanged()
    }

    override fun handleSearch(gitItem: GitItem) {
        repoSearchList.clear()
        repoSearchList = ArrayList(gitItem.items?.take(10))
        Log.d(TAG, "${repoSearchList.size}")
        if (repoSearchList.size > 0) {
            searchWorker.compositeDisposable.dispose()
            repoSearchList.map {
                it.avatarUrl = it.contributor?.avatarUrl
            }
            searchViewModel.insert(repoSearchList)
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
        intent.putExtra(GIT_REPO, repository)
        startActivity(intent)
    }

    override fun filterLanguages(language: ArrayList<String>) {
        filLanguages = language
    }

    override fun handleContributors(contributorList: List<Contributor>) {
    }

    override fun onClick(p0: View?) {
        when (p0) {
            imgFilter -> {
                dialog.show()
                gvLang.adapter = GvLangAdapter(this, this, languageList, filLanguages)
            }
            btnApply -> {
                val filteredListLang = if (!filLanguages.isNullOrEmpty()) {
                    repoSearchList.filter {
                        filLanguages!!.contains(it.language)
                    }
                } else null
                var minStar: Int = 0
                etMinStar.text.toString().apply {
                    if (this.isNotEmpty())
                        minStar = this.toInt()
                }
                var maxStar: Int = 0
                etMaxStar.text.toString().apply {
                    if (this.isNotEmpty())
                        maxStar = this.toInt()
                }
                val starCountList = repoSearchList.filter {
                    minStar < (it.stars?.toInt()!!) && (it.stars?.toInt()!!) < maxStar
                }
                val filteredList = when {
                    filteredListLang.isNullOrEmpty() -> starCountList
                    starCountList.isNullOrEmpty() -> filteredListLang
                    else -> filteredListLang?.intersect(starCountList)
                }
                searchAdapter.gitRepositoryList = filteredList as ArrayList<GitRepository>
                searchAdapter.notifyDataSetChanged()
                dialog.dismiss()

            }
            btnReset -> {
                searchAdapter.gitRepositoryList = repoSearchList
                searchAdapter.notifyDataSetChanged()
                filLanguages.clear()
                etMinStar.text.clear()
                etMaxStar.text.clear()
                dialog.dismiss()
            }
        }
    }
}
