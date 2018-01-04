package com.barron.isspasstimes.clients

import com.barron.isspasstimes.models.PassTimesResponse
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.Module
import dagger.Provides
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Singleton

/**
 * Created by shaunn on 1/3/2018.
 */
interface ISSAPIClient {

    @GET("iss-pass.json")
    fun getPassTimes(@Query("lat") lat: Double, @Query("lon") long: Double): Observable<PassTimesResponse>
}

@Module
class ISSAPIModule {

    @Provides
    @Singleton
    fun provideISSAPIClient(): ISSAPIClient {
        val restAdapter = Retrofit.Builder()
                .baseUrl("http://api.open-notify.org/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()

        return restAdapter.create(ISSAPIClient::class.java)
    }
}