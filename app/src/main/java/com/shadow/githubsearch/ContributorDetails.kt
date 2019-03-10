package com.shadow.githubsearch

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.contributor_details.*

class ContributorDetails : AppCompatActivity(), SearchHandler, RepositoryHandler {
    override fun handleRepository(gitRepoList: List<GitRepository>) {
        gitRepoList.map {
            it.avatarUrl = it.owner?.avatarUrl
        }

        adapter.repoList = gitRepoList as ArrayList<GitRepository>
        adapter.notifyDataSetChanged()
    }

    override fun handleContributors(contributorList: List<Owner>) {

    }

    override fun handleSearch(gitRepository: GitItem) {

    }

    override fun errorResponse(error: Throwable) {
        Log.e(TAG, error.message)
    }

    override fun onRepositorySelected(repository: GitRepository) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra("Git", repository)
        startActivity(intent)
    }

    companion object {
        const val TAG = "DetailScreen"
    }

    private val contributorItem: Owner? by lazy {
        intent.getParcelableExtra("contributor") as Owner
    }
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
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }
}
