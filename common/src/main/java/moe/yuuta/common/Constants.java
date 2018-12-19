package moe.yuuta.common;

public class Constants {
    public static final String SERVER_URL = "https://mipush.yuuta.moe/";
    public static final int PUSH_DELAY_MS_MAX = 1000 * 60 * 2;
    public static final int DISPLAY_ALL = 0;
    public static final int DISPLAY_SOUND = 1;
    public static final int DISPLAY_VIBRATE = 2;
    public static final int DISPLAY_LIGHTS = 3;
    public static final String HEADER_LOCALE = "X-MiPushTester-Local";
    public static final String HEADER_VERSION = "X-MiPushTester-Version";
    public static final String EXTRA_MIPUSHTESTER_PREFIX = "mpt-";
    public static final String EXTRA_REQUEST_LOCALE = EXTRA_MIPUSHTESTER_PREFIX + "request_locale";
    public static final String EXTRA_REQUEST_TIME = EXTRA_MIPUSHTESTER_PREFIX + "request_time";
    public static final String EXTRA_CLIENT_VERSION = EXTRA_MIPUSHTESTER_PREFIX + "client_version";
    // The package name of client, for more details, see BUILD.md
    public static final String CLIENT_ID = "moe.yuuta.mipushtester";
}
