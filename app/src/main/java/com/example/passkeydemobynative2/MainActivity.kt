package com.example.passkeydemobynative2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var passkeysManager: PasskeysManager
    
    private lateinit var usernameInput: TextInputEditText
    private lateinit var displayNameInput: TextInputEditText
    private lateinit var registerButton: Button
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 初始化 PasskeysManager
        passkeysManager = PasskeysManager(this)
        
        // 初始化视图
        usernameInput = findViewById(R.id.username_input)
        displayNameInput = findViewById(R.id.display_name_input)
        registerButton = findViewById(R.id.register_button)
        loginButton = findViewById(R.id.login_button)
        progressBar = findViewById(R.id.progress_bar)
        
        // 注册按钮点击事件
        registerButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val displayName = displayNameInput.text.toString()
            
            if (username.isEmpty()) {
                Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            registerPasskey(username, displayName.ifEmpty { username })
        }
        
        // 登录按钮点击事件
        loginButton.setOnClickListener {
            val username = usernameInput.text.toString()
            authenticateWithPasskey(username.ifEmpty { null })
        }
    }
    
    /**
     * 注册通行密钥
     */
    private fun registerPasskey(username: String, displayName: String) {
        lifecycleScope.launch {
            try {
                setLoading(true)
                Toast.makeText(this@MainActivity, "正在注册...", Toast.LENGTH_SHORT).show()
                
                val result = passkeysManager.register(username, displayName)
                
                if (result.isSuccess) {
                    Toast.makeText(
                        this@MainActivity,
                        result.getOrNull() ?: "注册成功",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "注册失败: ${result.exceptionOrNull()?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "注册错误: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            } finally {
                setLoading(false)
            }
        }
    }
    
    /**
     * 使用通行密钥登录
     */
    private fun authenticateWithPasskey(username: String?) {
        lifecycleScope.launch {
            try {
                setLoading(true)
                Toast.makeText(this@MainActivity, "正在登录...", Toast.LENGTH_SHORT).show()
                
                val result = passkeysManager.authenticate(username)
                
                if (result.isSuccess) {
                    Toast.makeText(
                        this@MainActivity,
                        result.getOrNull() ?: "登录成功",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "登录失败: ${result.exceptionOrNull()?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "登录错误: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                setLoading(false)
            }
        }
    }
    
    /**
     * 设置加载状态
     */
    private fun setLoading(loading: Boolean) {
        registerButton.isEnabled = !loading
        loginButton.isEnabled = !loading
        usernameInput.isEnabled = !loading
        displayNameInput.isEnabled = !loading
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
}