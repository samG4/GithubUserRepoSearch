package com.shadow.githubsearch.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.shadow.githubsearch.FilterHandler
import com.shadow.githubsearch.R
import kotlinx.android.synthetic.main.language_item.view.*

class GvLangAdapter(
    private val context: Context, private val filterHandler: FilterHandler,
    private var languageList: ArrayList<String>? = null,
    private val languagesFiltered: ArrayList<String>
) : BaseAdapter() {

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        var myView = view
        var holder: ViewHolder
        if (myView == null) {
            val mInflater = (context as Activity).layoutInflater
            myView = mInflater.inflate(R.layout.language_item, parent, false)
            holder = ViewHolder(myView)
            myView.tag = holder
        } else {
            holder = myView.tag as ViewHolder
        }
        languageList?.get(position).let {
            holder.bindView(it!!)
        }
        return myView!!    }

    override fun getItem(p0: Int): Any = languageList!!

    override fun getItemId(p0: Int): Long = 0

    override fun getCount(): Int = languageList?.size?:0

    inner class ViewHolder(itemView: View) {
        private val tvLanguage = itemView.tvLangName
        private val cbLang = itemView.cbLang
        fun bindView(language: String) {
            tvLanguage.text = language
            if(languagesFiltered.contains(language)){
                cbLang.isChecked = true
            }
            cbLang.setOnCheckedChangeListener { compoundButton, checked ->
                if(checked){
                    languagesFiltered.add(language)
                }else{
                    languagesFiltered.remove(language)
                }
                filterHandler.filterLanguages(languagesFiltered)
            }
        }
    }
}