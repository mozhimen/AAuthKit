package com.mozhimen.authk.biometric.cons

import androidx.biometric.BiometricManager


/**
 * @ClassName CAuthenticators
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/6/7
 * @Version 1.0
 */
object CAuthenticators {
    const val BIOMETRIC_STRONG = BiometricManager.Authenticators.BIOMETRIC_STRONG
    const val DEVICE_CREDENTIAL = BiometricManager.Authenticators.DEVICE_CREDENTIAL
    const val DEFAULT = BIOMETRIC_STRONG or DEVICE_CREDENTIAL
}