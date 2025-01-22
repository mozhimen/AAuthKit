package com.mozhimen.authk.biometric.mos

import androidx.biometric.BiometricPrompt
import com.mozhimen.authk.biometric.AuthKBiometricMgr
import com.mozhimen.authk.biometric.cons.CAuthenticators

/**
 * @ClassName PromptInfoBundle
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/6/7
 * @Version 1.0
 */
data class PromptInfoBundle(
    val title: String?,
    val subTitle: String?,
    val negativeButtonText: String?
) {
    fun getPromptInfo(allowedAuthenticators: Int): BiometricPrompt.PromptInfo =
        BiometricPrompt.PromptInfo.Builder().run {
            title?.let {
                setTitle(/*"Biometric login for ExampleDemo"*/title)
            }
            subTitle?.let {
                setSubtitle(/*"Login using your biometric credential"*/subTitle)
            }
            // 这里的allowedAuthenticators与检测生物识别是否可用步骤中的配置一样
            setAllowedAuthenticators(allowedAuthenticators)
            // 需要注意，allowedAuthenticators如果没有配置DEVICE_CREDENTIAL
            // 则需要配置NegativeButtonText，否则会抛出异常 IllegalArgumentException("Negative text must be set and non-empty.")
            // 反之不可配置NegativeButtonText，否则会抛出异常 IllegalArgumentException("Negative text must not be set if device credential authentication is allowed.")
            if (allowedAuthenticators and CAuthenticators.DEVICE_CREDENTIAL == 0 && negativeButtonText != null) {
                setNegativeButtonText(/*"use other login"*/negativeButtonText)
            }
            build()
        }
}