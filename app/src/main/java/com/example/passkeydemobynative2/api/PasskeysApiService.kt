package com.example.passkeydemobynative2.api

import com.google.gson.JsonObject
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PasskeysApiService {
    
    @POST("passkeys/register/start")
    suspend fun startRegistration(
        @Body request: RegistrationStartRequest
    ): Response<RegistrationStartResponse>
    
//    @POST("passkeys/register/finish")
//    suspend fun finishRegistration(
//        @Body request: RegistrationFinishRequest
//    ): Response<RegistrationFinishResponse>

     @POST("passkeys/register/finish")
    suspend fun finishRegistration(
         @Body body: RequestBody
    ): Response<RegistrationFinishResponse>


    @POST("passkeys/authenticate/start")
    suspend fun startAuthentication(
        @Body request: AuthenticationStartRequest
    ): Response<AuthenticationStartResponse>
    
    @POST("passkeys/authenticate/finish")
    suspend fun finishAuthentication(
        @Body body: RequestBody
    ): Response<AuthenticationFinishResponse>

//    @POST("passkeys/authenticate/finish")
//    suspend fun finishAuthentication(
//        @Body request: AuthenticationFinishRequest
//    ): Response<AuthenticationFinishResponse>
//
//
}

// 请求和响应数据类
data class RegistrationStartRequest(
    val username: String,
    val displayName: String
)

data class RegistrationStartResponse(
    val success: Boolean,
    val options: JsonObject // JSON 对象
)

data class RegistrationFinishRequest(
    val username: String,
    val credential: JsonObject // JSON 字符串
)

data class RegistrationFinishResponse(
    val success: Boolean,
    val message: String,
    val username: String,
    val credentialId: String
)

data class AuthenticationStartRequest(
    val username: String? = null
)

data class AuthenticationStartResponse(
    val success: Boolean,
    val options: JsonObject, // JSON 对象
    val requestId: String,
)

data class AuthenticationFinishRequest(
    val credential: String // JSON 字符串
)

data class AuthenticationFinishResponse(
    val success: Boolean,
    val username: String,
    val userId: String,
    val displayName: String,
    val credentialId: String
)

