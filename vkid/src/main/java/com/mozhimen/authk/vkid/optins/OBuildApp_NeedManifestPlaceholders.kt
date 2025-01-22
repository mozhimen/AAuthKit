package com.mozhimen.authk.vkid.optins

import com.mozhimen.authk.vkid.cons.CBuild
import com.mozhimen.kotlin.lintk.annors.AManifestRequire

/**
 * @ClassName OBuildApp_NeedManifestPlaceholders
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/3/5
 * @Version 1.0
 */
@AManifestRequire(CBuild.ManifestPlaceholders)
@RequiresOptIn("The api is must add < addManifestPlaceholders > to your app/ build.gradle. 这个Api需要将addManifestPlaceholders的几个配置加到你app/目录下的build.gradle里", RequiresOptIn.Level.WARNING)
annotation class OBuildApp_NeedManifestPlaceholders