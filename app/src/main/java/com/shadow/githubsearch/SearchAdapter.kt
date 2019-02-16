package com.shadow.githubsearch

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.search_result_item.view.*

class SearchAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var searchResultList: ArrayList<SearchResult>


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return SearchViewHolder(
            LayoutInflater.from(context).inflate(R.layout.search_result_item, p0, false)
        )
    }

    override fun getItemCount(): Int {
        return searchResultList.size
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        (p0 as SearchViewHolder).bind(searchResultList[p1])
    }

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName = itemView.tvName
        private val tvLang = itemView.tvLang
        private val tvStar = itemView.tvStar
        private val tvFork = itemView.tvFork
        private val imgLang = itemView.imgLang
        fun bind(searchResult: SearchResult) {
            tvName.text = searchResult.name
            tvLang.text = searchResult.language
            tvStar.text = searchResult.stars
            tvFork.text = searchResult.forks
            imgLang.setColorFilter(
                when (tvLang.text) {
                    "Kotlin" -> Color.rgb(241, 142, 51)
                    "Java" -> Color.rgb(176, 114, 25)
                    "HTML" -> Color.rgb(227, 76, 38)
                    "PowerShell" -> Color.rgb(1, 36, 86)
                    "C++"->Color.rgb(243, 75, 125)
                    "JavaScript"->Color.rgb(241, 224, 90)
                    "Python" -> Color.rgb(53, 114, 165)
                    "Shell" -> Color.rgb(137, 224, 81)
                    else -> Color.rgb(106, 115, 125)
                }
            )
        }
    }
}