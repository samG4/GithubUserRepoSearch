package com.shadow.githubsearch

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url


class SearchWorker(private val searchHandler: SearchHandler) {

    companion object {
        const val GithubApiUrl = "https://api.github.com/"
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BASIC
    }
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

    val compositeDisposable = CompositeDisposable()

    private val requestInterface = Retrofit.Builder()
        .baseUrl(GithubApiUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build().create(GitSearchApi::class.java)

    fun getRepos(userName: String){
        compositeDisposable.add(
            requestInterface.getRepo(userName)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(searchHandler::handleRepository, searchHandler::errorResponse)
        )
    }

    fun searchRepository(query: String) {
        compositeDisposable.add(
            requestInterface.searchRepositories(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(searchHandler::handleSearch, searchHandler::errorResponse)
        )
    }

    fun getContributors(fullName: String){
        compositeDisposable.add(
            requestInterface.getContributors(fullName)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(searchHandler::handleContributors, searchHandler::errorResponse)
        )

    }
}

interface GitSearchApi {
    @GET("users/{name}/repos")
    fun getRepo(@Path("name") name: String): Observable<List<GitRepository>>

    @GET("search/repositories?sort=watchers_count&order=desc")
    fun searchRepositories(@Query("q") query: String): Observable<GitItem>
/*
    @GET("repos/{fullName}/contributors")
    fun getContributors(@Path("fullName") fullName: String): Observable<List<Contributor>>*/
    @GET
    fun getContributors(@Url contributors: String): Observable<List<Contributor>>
}