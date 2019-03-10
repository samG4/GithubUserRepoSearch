package com.shadow.githubsearch

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.shadow.githubsearch.adapter.RepoAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.contributor_details.*

class ContributorDetails : AppCompatActivity(), SearchHandler, RepositoryHandler {
    override fun handleRepository(gitRepoList: List<GitRepository>) {
        gitRepoList.map {
            it.avatarUrl = it.contributor?.avatarUrl
        }
        searchViewModel.insert(gitRepoList)

    }

    override fun handleContributors(contributorList: List<Contributor>) {

    }

    override fun handleSearch(gitRepository: GitItem) {

    }

    override fun errorResponse(error: Throwable) {
        Log.e(TAG, error.message)
    }

    override fun onRepositorySelected(repository: GitRepository) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra(GIT_REPO, repository)
        startActivity(intent)
    }

    companion object {
        const val TAG = "DetailScreen"
    }

    private val contributorItem: Contributor? by lazy {
        intent.getParcelableExtra(CONTRIBUTOR) as Contributor
    }
    private val searchWorker: SearchWorker by lazy {
        SearchWorker(this)
    }
    private val adapter: RepoAdapter by lazy {
        RepoAdapter(this, this)
    }
    private lateinit var searchViewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contributor_details)
        Log.d(DetailsActivity.TAG, "$contributorItem")
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        imgBack.setOnClickListener {
            finish()
        }
        contributorItem?.let {
            tvName.text = it.name
            Picasso.get().load(it.avatarUrl).into(imgAvatar)
            it.name?.let { name ->
                searchWorker.getRepos(name)
            }
        }
        searchViewModel.getSearchResults(contributorItem?.name)
            ?.observe(this, Observer<List<GitRepository>> { repoList ->
                if(repoList.isNullOrEmpty()){
                    contributorItem?.name?.let { searchWorker.getRepos(it) }
                }
                else{
                    adapter.repoList = repoList as ArrayList<GitRepository>
                    adapter.notifyDataSetChanged()
                }
            }
            )
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    /*
    * Cases left
    * 1. New repositories present in web but not in local..
    * 2. Always show the repos from web...and store them in local only if they dont exist..if network is offline then
    * show from local*/
}
