package com.example

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class MyWebsiteProvider : MainAPI() {
    override var mainUrl = "https://new.katdrama.my/"
    override var name = "My Website"
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)
    override var lang = "en"
    override val hasMainPage = true

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val document = app.get(mainUrl).document
        val home = document.select("div.movie-item").mapNotNull { element ->
            val title = element.selectFirst("h2")?.text() ?: return@mapNotNull null
            val link = fixUrl(element.selectFirst("a")?.attr("href") ?: return@mapNotNull null)
            val poster = fixUrlNull(element.selectFirst("img")?.attr("src"))
            
            newMovieSearchResponse(title, link, TvType.Movie) {
                this.posterUrl = poster
            }
        }
        return HomePageResponse(arrayListOf(HomePageList("Trending", home)))
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val document = app.get("$mainUrl/?s=$query").document
        return document.select("div.movie-item").mapNotNull { element ->
            val title = element.selectFirst("h2")?.text() ?: return@mapNotNull null
            val link = fixUrl(element.selectFirst("a")?.attr("href") ?: return@mapNotNull null)
            val poster = fixUrlNull(element.selectFirst("img")?.attr("src"))
            
            newMovieSearchResponse(title, link, TvType.Movie) {
                this.posterUrl = poster
            }
        }
    }

    override suspend fun load(url: String): LoadResponse {
        val document = app.get(url).document
        val title = document.selectFirst("h1")?.text() ?: "No Title"
        val poster = fixUrlNull(document.selectFirst("img.poster")?.attr("src"))
        val description = document.selectFirst("div.description")?.text()

        return newMovieLoadResponse(title, url, TvType.Movie, url) {
            this.posterUrl = poster
            this.plot = description
        }
    }

    override suspend fun loadLinks(data: String, isCasting: Boolean, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit): Boolean {
        val document = app.get(data).document
        // Add your video extraction logic here
        return true
    }
}
