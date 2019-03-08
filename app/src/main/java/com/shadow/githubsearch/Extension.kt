package com.shadow.githubsearch

import android.app.Activity
import android.os.Build
import android.support.v7.app.AlertDialog


fun mapSrchResp(userName: String, gitRepository: List<GitRepository>):GitRepository{
    val star = StringBuilder()
    val fork = StringBuilder()
    val lang = StringBuilder()
    val name = StringBuilder()
    gitRepository.forEach { res->
        star.append(res.stars,",")
        fork.append(res.forks,",")
        lang.append(res.language,",")
        name.append(res.name,",")
    }
    return GitRepository(userName,name.toString(),lang.toString(),star.toString(), fork.toString())
}

fun convertSrchResultToList(gitRepository: GitRepository): ArrayList<GitRepository>{
    val starList :List<String> = gitRepository.stars.split(",")
    val forkList :List<String> = gitRepository.forks.split(",")
    val langList :List<String>? = gitRepository.language?.split(",")
    val nameList :List<String> = gitRepository.name.split(",")
    val userName = gitRepository.userName
    val searchList = ArrayList<GitRepository>()
    nameList.forEachIndexed {index,data->
        val lang = langList?.let {lang->
            if(lang?.size>index)lang[index] else ""
        }?:""
        searchList.add(GitRepository(userName, data, lang, starList[index], forkList[index]))
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