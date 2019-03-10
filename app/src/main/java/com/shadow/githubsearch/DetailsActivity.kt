package com.shadow.githubsearch

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.webkit.WebViewClient
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_detail.*
import kotlinx.android.synthetic.main.web_screen.*

class DetailsActivity : AppCompatActivity(), SearchHandler, ContributorHandler {

    companion object {
        const val TAG = "DetailScreen"
    }

    private val gitRepositoryItem: GitRepository? by lazy {
        intent.getParcelableExtra("Git") as GitRepository
    }
    private val searchHandler: SearchWorker by lazy {
        SearchWorker(this@DetailsActivity)
    }
    private val customAdapter: ContributorsGridAdapter by lazy {
        ContributorsGridAdapter(this@DetailsActivity, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_detail)
        Log.d(TAG, "$gitRepositoryItem")
        imgBack.setOnClickListener {
            finish()
        }
        gitRepositoryItem?.let {
            Picasso.get().load(it.avatarUrl).into(imgAvatar)
            tvName.text = it.name
            tvProjectLine.text = it.url
            tvDescription.text = it.description
            it.contributorsUrl?.let { contributors -> searchHandler.getContributors(contributors) }
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

    override fun handleContributors(contributorList: List<Owner>) {
        Log.d(TAG, "$contributorList")
        customAdapter.contributorsList = contributorList as ArrayList<Owner>
        customAdapter.notifyDataSetChanged()
    }

    override fun onContributorTapped(owner: Owner) {
        Log.d(TAG, "$owner")
        val intent = Intent(this, ContributorDetails::class.java)
        intent.putExtra("contributor", owner)
        startActivity(intent)
    }
}