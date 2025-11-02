package com.example.passkeydemobynative2

import android.content.Context
import android.util.Log
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PublicKeyCredential
import com.example.passkeydemobynative2.api.ApiClient
import com.example.passkeydemobynative2.api.AuthenticationFinishRequest
import com.example.passkeydemobynative2.api.AuthenticationStartRequest
import com.example.passkeydemobynative2.api.RegistrationFinishRequest
import com.example.passkeydemobynative2.api.RegistrationStartRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class PasskeysManager(private val context: Context) {
    
    private val credentialManager = CredentialManager.create(context)
    
    /**
     * 注册通行密钥
     */
    suspend fun register(username: String, displayName: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. 从服务器获取注册选项
                val startResponse = ApiClient.passkeysApi.startRegistration(
                    RegistrationStartRequest(username, displayName)
                )
                
                if (!startResponse.isSuccessful || startResponse.body()?.success != true) {
                    return@withContext Result.failure(
                        Exception("获取注册选项失败: ${startResponse.message()}")
                    )
                }
                startResponse.body()!!.options.getAsJsonObject("authenticatorSelection").remove("authenticatorAttachment")
                startResponse.body()!!.options.addProperty("attestation", "direct")
                Log.d("ddddd", "register: ${startResponse.body()!!.options}")
                val optionsJson = startResponse.body()!!.options.toString()
                val optionsJson2 = """{
  "rp": {
    "name": "Passkeys Demo Server",
    "id": "nonresilient-boundedly-aleta.ngrok-free.dev"
  },
  "user": {
    "name": "name5",
    "displayName": "showName5",
    "id": "jZiySf1PuQ7ALmAPbC0DSeOotnYq0Q0sXq338pwG1cg"
  },
  "challenge": "woPq9PqVq-67R3DbUbxDK4R8tREajkt2_wnuJ0F9lKw",
  "pubKeyCredParams": [
    {"alg": -7, "type": "public-key"}
  ],
  "excludeCredentials":[],
  "authenticatorSelection": {
    "residentKey": "required",
    "userVerification": "required"
  },
  "attestation": "direct",
  "extensions": {
    "credProps": true
  }
}
"""
                var optionsJson3 = """{
    "rp": {
        "name": "Passkeys Demo Server",
        "id": "nonresilient-boundedly-aleta.ngrok-free.dev"
    },
    "user": {
        "name": "name",
        "displayName": "showname5",
        "id": "yGmIhbSD1HxouATwd-74pkmniQjWtK-LfbRDWYd110s"
    },
    "challenge": "0TTV9s_8-eUnu1GZaZw0kLS4XjkZm8bqnnE5AsOQK48",
    "pubKeyCredParams": [
        {"alg": -7, "type": "public-key"}
    ],
    "timeout": null,
    "excludeCredentials": [

    ],
    "authenticatorSelection": {
        "requireResidentKey": true,
        "residentKey": "required",
        "userVerification": "required"
    },
    "attestation": "direct",
    "extensions": {
        "appidExclude": null,
        "credProps": true,
        "largeBlob": null
    }
}"""

                // 2. 使用 Credential Manager 创建凭证
                val createRequest = CreatePublicKeyCredentialRequest(optionsJson, preferImmediatelyAvailableCredentials=false)
                val credential = withContext(Dispatchers.Main) {
                    credentialManager.createCredential(
                        request = createRequest,
                        context = context
                    ) as CreatePublicKeyCredentialResponse
                }
                
                // 3. 将凭证发送到服务器完成注册
                val rawJson = """
                {
                  "username": "$username",
                  "credential": ${credential.registrationResponseJson}
                }
                """.trimIndent()

                val requestBody = rawJson.toRequestBody("application/json".toMediaType())
                val finishResponse = ApiClient.passkeysApi.finishRegistration(
                    requestBody
                )
                
                if (!finishResponse.isSuccessful || finishResponse.body()?.success != true) {
                    return@withContext Result.failure(
                        Exception("完成注册失败: ${finishResponse.message()}")
                    )
                }
                
                Result.success("注册成功！凭证 ID: ${finishResponse.body()!!.credentialId}")
                
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }
    
    /**
     * 使用通行密钥登录
     */
    suspend fun authenticate(username: String? = null): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. 从服务器获取认证选项
                val startResponse = ApiClient.passkeysApi.startAuthentication(
                    AuthenticationStartRequest(username)
                )
                
                if (!startResponse.isSuccessful || startResponse.body()?.success != true) {
                    return@withContext Result.failure(
                        Exception("获取认证选项失败: ${startResponse.message()}")
                    )
                }
                
                val optionsJson = startResponse.body()!!.options.toString()
                
                // 2. 使用 Credential Manager 获取凭证
                val getPublicKeyCredentialOption = GetPublicKeyCredentialOption(optionsJson)
                val getCredRequest = GetCredentialRequest(
                    listOf(getPublicKeyCredentialOption)
                )
                
                val credential = withContext(Dispatchers.Main) {
                    val credentialResponse = credentialManager.getCredential(
                        request = getCredRequest,
                        context = context
                    )
                    credentialResponse.credential as PublicKeyCredential
                }
                
                // 3. 将凭证发送到服务器完成认证
                val finishResponse = ApiClient.passkeysApi.finishAuthentication(
                    AuthenticationFinishRequest(
                        credential = credential.authenticationResponseJson
                    )
                )
                
                if (!finishResponse.isSuccessful || finishResponse.body()?.success != true) {
                    return@withContext Result.failure(
                        Exception("认证失败: ${finishResponse.message()}")
                    )
                }
                
                val userData = finishResponse.body()!!
                Result.success("登录成功！用户: ${userData.username} (${userData.displayName})")
                
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

