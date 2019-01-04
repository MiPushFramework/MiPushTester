package moe.yuuta.common

object Constants {
    const val SERVER_URL = "https://mipush.yuuta.moe/"
    const val PUSH_DELAY_MS_MAX = 1000 * 60 * 2
    const val DISPLAY_ALL = 0
    const val DISPLAY_SOUND = 1
    const val DISPLAY_VIBRATE = 2
    const val DISPLAY_LIGHTS = 3
    const val HEADER_LOCALE = "X-MiPush-Local"
    const val HEADER_VERSION = "X-MiPush-Version"
    const val HEADER_PRODUCT = "X-MiPush-Product"
    const val EXTRA_MIPUSHTESTER_PREFIX = "mpt-"
    const val EXTRA_REQUEST_LOCALE = EXTRA_MIPUSHTESTER_PREFIX + "request_locale"
    const val EXTRA_REQUEST_TIME = EXTRA_MIPUSHTESTER_PREFIX + "request_time"
    const val EXTRA_CLIENT_VERSION = EXTRA_MIPUSHTESTER_PREFIX + "client_version"
    const val TESTER_CLIENT_ID = "moe.yuuta.mipushtester"
    const val FRAMEWORK_CLIENT_ID = "top.trumeet.mipush"
    const val REG_ID_TYPE_REG_ID = 0
    const val REG_ID_TYPE_ALIAS = 1
    const val REG_ID_TYPE_ACCOUNT = 2
}
