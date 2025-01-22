package com.mozhimen.authk.vkid

import android.app.Activity
import com.mozhimen.kotlin.utilk.android.util.UtilKLogWrapper
import androidx.annotation.IdRes
import androidx.lifecycle.LifecycleOwner
import com.mozhimen.authk.vkid.optins.OBuildApp_NeedManifestPlaceholders
import com.mozhimen.authk.vkid.optins.OBuildApp_NeedSecretsProperties
import com.mozhimen.basick.bases.BaseWakeBefDestroyLifecycleObserver
import com.mozhimen.kotlin.elemk.commons.IA_Listener
import com.mozhimen.kotlin.elemk.commons.I_Listener
import com.mozhimen.kotlin.lintk.optins.OApiCall_BindLifecycle
import com.mozhimen.kotlin.lintk.optins.OApiCall_BindViewLifecycle
import com.mozhimen.kotlin.lintk.optins.OApiInit_ByLazy
import com.mozhimen.kotlin.utilk.android.view.addAndRemoveOnGlobalLayoutListener
import com.vk.id.AccessToken
import com.vk.id.VKIDAuthFail
import com.vk.id.onetap.xml.OneTapBottomSheet

/**
 * @ClassName AuthKVKIDOnTapBottomSheet
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/3/5
 * @Version 1.0
 */
@OApiInit_ByLazy
@OApiCall_BindLifecycle
@OApiCall_BindViewLifecycle
@OBuildApp_NeedSecretsProperties
@OBuildApp_NeedManifestPlaceholders
class AuthKVKIDOnTapBottomSheetProxy<A>(private var _activity: A?) : BaseWakeBefDestroyLifecycleObserver() where  A : LifecycleOwner, A : Activity {
    private var _oneTapBottomSheet: OneTapBottomSheet? = null
    private var _isAutoAuth: Boolean = false

    /////////////////////////////////////////////////////////////

    fun isAutoAuth(boolean: Boolean) {
        _isAutoAuth = boolean
        UtilKLogWrapper.d(TAG, "isAutoAuth: $boolean")
    }

    fun initBottomSheet(@IdRes intResId: Int) {
        _oneTapBottomSheet = _activity?.findViewById(intResId)
    }

    fun showBottomSheet() {
        _oneTapBottomSheet?.show().also { UtilKLogWrapper.d(TAG, "showBottomSheet: ") }
    }

    fun hideBottomSheet() {
        _oneTapBottomSheet?.hide()
    }

    fun setOnAuthCallback(onSuccess: IA_Listener<AccessToken>, onCancel: I_Listener? = null, onFail: I_Listener? = null) {
        _oneTapBottomSheet?.setCallbacks(onAuth = onSuccess, onFail = { vkidAuthFail: VKIDAuthFail ->
            when (vkidAuthFail) {
                is VKIDAuthFail.Canceled -> onCancel?.invoke()
                else -> onFail?.invoke()
            }
        })
    }

    /////////////////////////////////////////////////////////////

    override fun onCreate(owner: LifecycleOwner) {
        if (_isAutoAuth) {
            _oneTapBottomSheet?.addAndRemoveOnGlobalLayoutListener {
                showBottomSheet()
            }
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        hideBottomSheet()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        _oneTapBottomSheet = null
        _activity = null
        super.onDestroy(owner)
    }
}