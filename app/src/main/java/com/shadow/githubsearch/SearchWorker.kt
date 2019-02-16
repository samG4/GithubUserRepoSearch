package com.shadow.githubsearch

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

class SearchWorker(var name: String, private val searchHandler: SearchHandler){
    private val compositeDisposable = CompositeDisposable()

    companion object {
        const val GithubApiUrl = "https://api.github.com/"
    }

    fun doRepoSearch(){
        val requestInterface = Retrofit.Builder()
            .baseUrl(GithubApiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(GitSearchApi::class.java)
        compositeDisposable.add(
            requestInterface.getRepo(name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(searchHandler::handleResponse, searchHandler::errorResponse)
        )
    }
}

interface GitSearchApi{
    @GET("users/{name}/repos")
    fun getRepo(@Path("name") name: String) : Observable<List<SearchResult>>
}

interface SearchHandler{
    fun handleResponse(searchResult: List<SearchResult>)
    fun errorResponse(error: Throwable)
}