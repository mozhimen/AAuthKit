package com.mozhimen.authk.biometric

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.mozhimen.authk.biometric.cons.CAuthenticators
import com.mozhimen.authk.biometric.cons.EAuthState
import com.mozhimen.authk.biometric.mos.PromptInfoBundle
import com.mozhimen.kotlin.elemk.android.os.cons.CVersCode
import com.mozhimen.kotlin.elemk.android.security.cons.CKeyProperties
import com.mozhimen.kotlin.elemk.commons.IAB_Listener
import com.mozhimen.kotlin.elemk.javax.crypto.cons.CCipher
import com.mozhimen.kotlin.utilk.android.util.UtilKLogWrapper
import com.mozhimen.kotlin.utilk.commons.IUtilK
import com.mozhimen.kotlin.utilk.java.security.UtilKKeyStore
import com.mozhimen.kotlin.utilk.javax.crypto.UtilKCipher
import com.mozhimen.kotlin.utilk.wrapper.UtilKSecret
import java.nio.charset.Charset
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

/**
 * @ClassName AuthKBiometricMgr
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/6/7
 * @Version 1.0
 */
object AuthKBiometricMgr : IUtilK {
    private var _allowedAuthenticators: Int = CAuthenticators.DEFAULT

    @JvmStatic
    fun init(allowedAuthenticators: Int) {
        _allowedAuthenticators = allowedAuthenticators
    }

    @JvmStatic
    fun startAuth(
        fragmentActivity: FragmentActivity,
        promptInfoBundle: PromptInfoBundle,
        listener: IAB_Listener<EAuthState, BiometricPrompt.AuthenticationResult?>? = null
    ) {
        val biometricPrompt = BiometricPrompt(fragmentActivity, ContextCompat.getMainExecutor(fragmentActivity.applicationContext), object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                // 验证通过，可以通过result.authenticationType获取使用的生物识别类型。
                UtilKLogWrapper.d(TAG, "onAuthenticationSucceeded: ")
                listener?.invoke(EAuthState.SUCCESS, result)
            }

            override fun onAuthenticationFailed() {
                // 验证失败
                UtilKLogWrapper.d(TAG, "onAuthenticationFailed: ")
                listener?.invoke(EAuthState.FAIL, null)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                // 验证错误
                UtilKLogWrapper.d(TAG, "onAuthenticationError: errorCode $errorCode errString $errString")
                listener?.invoke(EAuthState.ERROR, null)
            }
        })

        biometricPrompt.authenticate(promptInfoBundle.getPromptInfo(_allowedAuthenticators))
    }

    @JvmStatic
    fun getPromptInfo(promptInfoBundle: PromptInfoBundle) =
        BiometricPrompt.PromptInfo.Builder().run {
            promptInfoBundle.title?.let {
                setTitle(/*"Biometric login for ExampleDemo"*/promptInfoBundle.title)
            }
            promptInfoBundle.subTitle?.let {
                setSubtitle(/*"Login using your biometric credential"*/promptInfoBundle.subTitle)
            }
            // 这里的allowedAuthenticators与检测生物识别是否可用步骤中的配置一样
            setAllowedAuthenticators(_allowedAuthenticators)
            // 需要注意，allowedAuthenticators如果没有配置DEVICE_CREDENTIAL
            // 则需要配置NegativeButtonText，否则会抛出异常 IllegalArgumentException("Negative text must be set and non-empty.")
            // 反之不可配置NegativeButtonText，否则会抛出异常 IllegalArgumentException("Negative text must not be set if device credential authentication is allowed.")
            if (_allowedAuthenticators and CAuthenticators.DEVICE_CREDENTIAL == 0 && promptInfoBundle.negativeButtonText != null) {
                setNegativeButtonText(/*"use other login"*/promptInfoBundle.negativeButtonText)
            }
            build()
        }

    @JvmStatic
    fun isSupport(context: Context): Int {
        val biometricManager = BiometricManager.from(context)
        // 如果可以允许用户不使用生物识别而是密码，可以设置DEVICE_CREDENTIAL
        return biometricManager.canAuthenticate(_allowedAuthenticators)
        /*when (canAuthenticate(allowedAuthenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // 可以使用生物识别
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                // 设备没有相应的硬件
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                // 生物识别当前不可以用
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // 没有录入相应的生物识别信息（指纹或者人脸）
                // androidx.biometric提供的录入生物信息的API仅在Android R(30)以上有效
                if (UtilKBuildVersion.isAfterV_30_11_R()) {
                    val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                        putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, allowedAuthenticators)
                    }
                    startActivityForResult(enrollIntent)
                } else {
                    // 创建弹窗提示用户录入生物信息（指纹或者人脸），进入设置页面
                    AlertDialog.Builder(this@BiometricActivity)
                        .setTitle("Create credentials")
                        .setMessage("Record fingerprints to log into the ExampleDemo")
                        .setPositiveButton("ok") { _, _ ->
                            val intent = Intent().apply {
                                action = Intent.ACTION_VIEW
                                component = ComponentName("com.android.settings", "com.android.settings.Settings")
                            }
                            startActivityForResult(intent)
                        }
                        .setNegativeButton("not now", null)
                        .show()
                }
            }

            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                // 当设备的Android版本等于或小于Q
                // 并且allowedAuthenticators设定为BIOMETRIC_STRONG or DEVICE_CREDENTIAL时，回调此方法
                // 需要通过其他API判断设备是否支持生物识别，因此当设备的Android版本等于或小于Q时
                // allowedAuthenticators建议设置为BIOMETRIC_STRONG、BIOMETRIC_STRONG或 BIOMETRIC_WEAK or DEVICE_CREDENTIAL
            }

            else -> {}
        }*/
    }

    @JvmStatic
    @RequiresApi(CVersCode.V_23_6_M)
    fun encrypt(fragmentActivity: FragmentActivity, promptInfoBundle: PromptInfoBundle, keyName: String) {
        // 调用加密
        val biometricPrompt = BiometricPrompt(fragmentActivity, ContextCompat.getMainExecutor(fragmentActivity.applicationContext), object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                result.cryptoObject?.cipher?.run {
                    // 加密处理
                    val encryptByteArray = doFinal("encrypt message".toByteArray(Charset.defaultCharset()))
                }
            }
        })
        val encryptCipher = UtilKCipher.get("${CKeyProperties.KEY_ALGORITHM_AES}/${CKeyProperties.BLOCK_MODE_CBC}/${CKeyProperties.ENCRYPTION_PADDING_PKCS7}")
        encryptCipher.init(CCipher.ENCRYPT_MODE, UtilKSecret.generateKey(keyName))
        // 生成解密密钥时需要用到ivParameterSpec
        biometricPrompt.authenticate(getPromptInfo(promptInfoBundle), BiometricPrompt.CryptoObject(encryptCipher))

    }

    @JvmStatic
    @RequiresApi(CVersCode.V_23_6_M)
    fun decrypt(fragmentActivity: FragmentActivity, promptInfoBundle: PromptInfoBundle, keyName: String, bytes: ByteArray) {
        // 调用解密
        val biometricPrompt = BiometricPrompt(fragmentActivity, ContextCompat.getMainExecutor(fragmentActivity.applicationContext), object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                result.cryptoObject?.cipher?.run {
                    // 解密处理
                    doFinal(bytes)
                }
            }
        })
        val encryptCipher = UtilKCipher.get("${CKeyProperties.KEY_ALGORITHM_AES}/${CKeyProperties.BLOCK_MODE_CBC}/${CKeyProperties.ENCRYPTION_PADDING_PKCS7}")
        encryptCipher.init(CCipher.ENCRYPT_MODE, UtilKSecret.generateKey(keyName))
        val ivParameterSpec = encryptCipher.parameters.getParameterSpec(IvParameterSpec::class.java)

        val decryptCipher = UtilKCipher.get("${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}")
        decryptCipher.init(Cipher.DECRYPT_MODE, UtilKSecret.generateKey(keyName), ivParameterSpec)
        biometricPrompt.authenticate(promptInfoBundle.getPromptInfo(_allowedAuthenticators), BiometricPrompt.CryptoObject(decryptCipher))
    }
}