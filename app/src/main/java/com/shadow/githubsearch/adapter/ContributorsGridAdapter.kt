package com.shadow.githubsearch.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.shadow.githubsearch.CircleTransform
import com.shadow.githubsearch.Contributor
import com.shadow.githubsearch.ContributorHandler
import com.shadow.githubsearch.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.contributor_item.view.*

class ContributorsGridAdapter(
    private val context: Context, private val contributorHandler: ContributorHandler,
    var contributorsList: ArrayList<Contributor>? = null
) : BaseAdapter() {
    private val circularTransform = CircleTransform()
    override fun getView(p0: Int, p1: View?, parent: ViewGroup?): View {
        var myView = p1
        var holder: ViewHolder
        if (myView == null) {
            val mInflater = (context as Activity).layoutInflater
            myView = mInflater.inflate(R.layout.contributor_item, parent, false)
            holder = ViewHolder(myView)
            myView.tag = holder
        } else {
            holder = myView.tag as ViewHolder
        }
        contributorsList?.get(p0)?.let { holder.bindView(it) }
        return myView!!
    }

    override fun getItem(p0: Int): Any {
        return contributorsList!!
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return contributorsList?.size ?: 0
    }

    inner class ViewHolder(itemView: View) {
        private val mImageView: ImageView = itemView.imgContributor
        private val mTextView: TextView = itemView.tvUserName
        private val view = itemView

        fun bindView(contributor: Contributor) {
            Picasso.get().load(contributor.avatarUrl).transform(circularTransform).into(mImageView)
            mTextView.text = contributor.name

            view.setOnClickListener {
                contributorHandler.onContributorTapped(contributor)
            }
        }
    }
}