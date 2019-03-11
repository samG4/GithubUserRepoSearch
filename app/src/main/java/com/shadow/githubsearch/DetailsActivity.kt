package com.shadow.githubsearch

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import android.widget.TextView
import com.shadow.githubsearch.adapter.ContributorsGridAdapter
import kotlinx.android.synthetic.main.item_detail.*
import kotlinx.android.synthetic.main.web_screen.*

class DetailsActivity : AppCompatActivity(), SearchHandler, ContributorHandler {

    companion object {
        const val TAG = "DetailScreen"
        const val INSTANCE = "Item"
    }

    private val gitRepositoryItem: GitRepository by lazy {
        intent.getParcelableExtra(GIT_REPO) as GitRepository
    }

    private val searchHandler: SearchWorker by lazy {
        SearchWorker(this@DetailsActivity)
    }
    private val customAdapter: ContributorsGridAdapter by lazy {
        ContributorsGridAdapter(this@DetailsActivity, this)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putSerializable(INSTANCE, customAdapter.contributorsList)
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_detail)
        var temp = ArrayList<Contributor>()
        savedInstanceState?.let {
            try {
                temp = savedInstanceState.getSerializable(INSTANCE) as ArrayList<Contributor>
            } catch (e: Exception) {
                Log.e(TAG, e.message)
            }
        }
        Log.d(TAG, "$gitRepositoryItem")
        imgBack.setOnClickListener {
            finish()
        }
        gitRepositoryItem?.let {
            Picasso.get().load(it.avatarUrl).into(imgAvatar)
            tvName.text = it.name
            tvProjectLine.text = it.url
            tvDescription.text = it.description
            if(temp.isNullOrEmpty())
                it.contributorsUrl?.let { contributors -> searchHandler.getContributors(contributors) }
            else{
                customAdapter.contributorsList = temp
                customAdapter.notifyDataSetChanged()
                temp.clear()
            }

        }
        tvProjectLine.setOnClickListener {
            val link = (it as TextView).text.toString()
            val dialog = this.getDialog(R.layout.web_screen)
            dialog.let {
                val webView = it.webView
                webView.loadUrl(link)
                webView.webViewClient = WebViewClient()
                webView.settings.javaScriptEnabled=true
            }
            dialog.show()
        }
        gvContributors.adapter = customAdapter
    }

    override fun handleRepository(gitRepoList: List<GitRepository>) {
    }

    override fun handleSearch(gitRepository: GitItem) {
    }

    override fun errorResponse(error: Throwable) {
        Log.e(TAG, error.message)
    }

    override fun handleContributors(contributorList: List<Contributor>) {
        Log.d(TAG, "$contributorList")
        if(contributorList.isNotEmpty())
           tvContributors.visibility = View.VISIBLE
        customAdapter.contributorsList = contributorList as ArrayList<Contributor>
        customAdapter.notifyDataSetChanged()
    }

    override fun onContributorTapped(contributor: Contributor) {
        Log.d(TAG, "$contributor")
        val intent = Intent(this, ContributorDetails::class.java)
        intent.putExtra(CONTRIBUTOR, contributor)
        startActivity(intent)
    }
}