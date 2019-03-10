package com.shadow.githubsearch

interface SearchHandler {
    fun handleRepository(gitRepoList : List<GitRepository>)
    fun handleContributors(contributorList : List<Owner>)
    fun handleSearch(gitRepository: GitItem)
    fun errorResponse(error: Throwable)
}

interface RepositoryHandler{
    fun onRepositorySelected(repository: GitRepository)
}

interface ContributorHandler{
    fun onContributorTapped(owner:Owner)
}