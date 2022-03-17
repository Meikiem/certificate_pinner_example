package com.example.sslpin.config

import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object Config {

    fun createSecureOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(createLoggingInterceptor())
            .certificatePinner(createCertificatePinner())
            .build()
    }

    fun createPinningOnlyOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(createLoggingInterceptor())
            .certificatePinner(createCertificatePinner())
            .build()

    }

    fun insecureOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(createLoggingInterceptor())
            .build()
    }

    private fun createCertificatePinner(): CertificatePinner {
        return CertificatePinner.Builder()
            .add(Pinning.DOMAIN_INFO, Pinning.CERT)
            .build()
    }

    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        return loggingInterceptor
    }

}
