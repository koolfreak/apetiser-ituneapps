package it.cybernetics.itunesapp.service

import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface Api {
    //https://itunes.apple.com/search?term=star&amp;country=au&amp;media=movie&amp;all
    // search endpoint for itunes
    @GET("search")
    fun getSearchResults(@QueryMap params: Map<String, String>): Observable<ResponseBody>
}