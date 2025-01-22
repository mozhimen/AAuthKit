package com.mozhimen.authk.vkid.cons

/**
 * @ClassName CBuild
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/3/5
 * @Version 1.0
 */
object CBuild {
    const val SecretsProperties = """
VKIDClientSecret=xxxxxxxxxxxx //Защищённый ключ
VKIDClientID=xxx //ID приложения
    """

    const val ManifestPlaceholders = """
android {
    namespace = "xxx.xxxx"

    defaultConfig {
        //...

        addManifestPlaceholders(mapOf(
            "VKIDClientID" to "123456", // 应用程序ID (app_id)
            "VKIDClientSecret" to "xxxxxxxxxxxxx", // 你的安全密钥 (client_secret)
            "VKIDRedirectHost" to "vk.com", // 通常使用vk.com
            "VKIDRedirectScheme" to "vk123456", // 通常使用vk{应用程序ID}
        ))
    """
}