package com.shadow.githubsearch

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.shadow.githubsearch.adapter.RepoAdapter
import kotlinx.android.synthetic.main.contributor_details.*

class ContributorDetails : AppCompatActivity(), SearchHandler, RepositoryHandler {
    override fun handleRepository(gitRepoList: List<GitRepository>) {
        gitRepoList.map {
            it.avatarUrl = it.contributor?.avatarUrl
        }
        adapter.repoList = gitRepoList as ArrayList<GitRepository>
        adapter.notifyDataSetChanged()

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putSerializable(INSTANCE, adapter.repoList)
        super.onSaveInstanceState(outState)
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
        const val INSTANCE = "Item"
    }

    private val contributorItem: Contributor by lazy { intent.getParcelableExtra(CONTRIBUTOR) as Contributor }

    private val searchWorker: SearchWorker by lazy {
        SearchWorker(this)
    }
    private val adapter: RepoAdapter by lazy {
        RepoAdapter(this, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contributor_details)
        Log.d(DetailsActivity.TAG, "$contributorItem")
        var temp = ArrayList<GitRepository>()
        savedInstanceState?.let {
            try {
                temp = savedInstanceState.getSerializable(INSTANCE) as ArrayList<GitRepository>
            } catch (e: Exception) {
                Log.e(DetailsActivity.TAG, e.message)
            }
        }
        imgBack.setOnClickListener {
            finish()
        }
        contributorItem?.let {
            tvName.text = it.name
            Picasso.get().load(it.avatarUrl).into(imgAvatar)
            it.name?.let { name ->
                if (temp.isNullOrEmpty()) {
                    searchWorker.getRepos(name)
                } else {
                    adapter.repoList = temp
                    adapter.notifyDataSetChanged()
                    temp.clear()
                }
            }
        }

        contributorItem?.name?.let { searchWorker.getRepos(it) }
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
