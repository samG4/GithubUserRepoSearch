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

    companion object {
        const val GithubApiUrl = "https://api.github.com/"
    }

    private val compositeDisposable = CompositeDisposable()
    private val requestInterface = Retrofit.Builder()
        .baseUrl(GithubApiUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build().create(GitSearchApi::class.java)

    fun doRepoSearch(){
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
    fun getRepo(@Path("name") name: String) : Observable<List<GitRepository>>

    @GET("search/repositories")
    fun searchRepositories(@Path("name") name: String) : Observable<List<GitRepository>>
}

interface SearchHandler{
    fun handleResponse(gitRepository: List<GitRepository>)
    fun errorResponse(error: Throwable)
}