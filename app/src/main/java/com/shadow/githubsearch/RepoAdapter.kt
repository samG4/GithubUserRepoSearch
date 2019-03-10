package com.shadow.githubsearch

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.search_result_item.view.*

class RepoAdapter(
    private val context: Context,
    private val repositoryHandler: RepositoryHandler
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var repoList = ArrayList<GitRepository>()
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return RepoListViewHolder(
            LayoutInflater.from(context).inflate(R.layout.searched_item, p0, false)
        )
    }

    override fun getItemCount(): Int {
        return repoList.size
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        (p0 as RepoListViewHolder).bind(repoList[p1])
    }

    inner class RepoListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvRepoName = itemView.tvName
        private val tvLang = itemView.tvLang
        private val tvStar = itemView.tvStar
        private val tvFork = itemView.tvFork
        private val imgLang = itemView.imgLang

        fun bind(gitRepository: GitRepository) {
            gitRepository.let {
                tvRepoName.text = it.name
                tvLang.text = it.language
                tvStar.text = it.stars
                tvFork.text = it.forks
            }

            imgLang.setColorFilter(
                when (tvLang.text) {
                    "Kotlin" -> Color.rgb(241, 142, 51)
                    "Java" -> Color.rgb(176, 114, 25)
                    "HTML" -> Color.rgb(227, 76, 38)
                    "PowerShell" -> Color.rgb(1, 36, 86)
                    "C++" -> Color.rgb(243, 75, 125)
                    "JavaScript" -> Color.rgb(241, 224, 90)
                    "Python" -> Color.rgb(53, 114, 165)
                    "Shell" -> Color.rgb(137, 224, 81)
                    else -> Color.rgb(106, 115, 125)
                }
            )

            itemView.setOnClickListener {
                repositoryHandler.onRepositorySelected(gitRepository)
            }
        }


    }
}