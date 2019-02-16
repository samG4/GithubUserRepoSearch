package com.shadow.githubsearch

import android.app.Activity
import android.os.Build
import android.support.v7.app.AlertDialog


fun mapSrchResp(userName: String, searchResult: List<SearchResult>):SearchResult{
    val star = StringBuilder()
    val fork = StringBuilder()
    val lang = StringBuilder()
    val name = StringBuilder()
    searchResult.forEach { res->
        star.append(res.stars,",")
        fork.append(res.forks,",")
        lang.append(res.language,",")
        name.append(res.name,",")
    }
    return SearchResult(userName,name.toString(),lang.toString(),star.toString(), fork.toString())
}

fun convertSrchResultToList(searchResult: SearchResult): ArrayList<SearchResult>{
    val starList :List<String> = searchResult.stars.split(",")
    val forkList :List<String> = searchResult.forks.split(",")
    val langList :List<String>? = searchResult.language?.split(",")
    val nameList :List<String> = searchResult.name.split(",")
    val userName = searchResult.userName
    val searchList = ArrayList<SearchResult>()
    nameList.forEachIndexed {index,data->
        val lang = langList?.let {lang->
            if(lang?.size>index)lang[index] else ""
        }?:""
        searchList.add(SearchResult(userName, data, lang, starList[index], forkList[index]))
    }
    searchList.removeAt(searchList.lastIndex)
    return searchList
}

fun Activity.showAlert(message: String?){
    val builder: AlertDialog.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
    } else {
        AlertDialog.Builder(this)
    }
    builder.setTitle("Alert")
        .setMessage(message)
        .setPositiveButton(android.R.string.ok) { _, _->
        }
        .setIcon(android.R.drawable.ic_dialog_alert)
        .show()
}