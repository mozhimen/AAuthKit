package com.mozhimen.authk.vkid.optins

import com.mozhimen.authk.vkid.cons.CBuild
import com.mozhimen.kotlin.lintk.annors.AManifestRequire

/**
 * @ClassName OBuildApp_NeedProperties
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/3/5
 * @Version 1.0
 */
@AManifestRequire(CBuild.SecretsProperties)
@RequiresOptIn("The api is must add < secrets.properties > to your app/ directory. 这个Api需要将secrets.properties文件加载你的app/目录下", RequiresOptIn.Level.WARNING)
annotation class OBuildApp_NeedSecretsProperties