package com.example.egenda

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val sharedPreferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        val tokenType = sharedPreferences.getString("token_type", "") ?: ""
        val token = sharedPreferences.getString("access_token", "") ?: ""

        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", "$tokenType $token")
            .build()
        return chain.proceed(newRequest)
    }
}
