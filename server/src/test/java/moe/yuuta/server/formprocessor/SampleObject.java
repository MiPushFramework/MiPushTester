package moe.yuuta.server.formprocessor;

class SampleObject {
    @FormData(name = "normalString")
    private String normalString = "haoye";

    @FormData(name = "normalInteger")
    private int normalInteger = 123;

    @FormData(name = "encodeString", urlEncode = true)
    private String encodeString = "   Ri kk a";

    @FormData(name = "ignorableInteger")
    private int ignorableInteger = 0;

    @FormData(name = "shouldIgnored")
    private String shouldIgnored = "";

    @FormData(name = "shouldIgnored2")
    private String shouldIgnored2 = null;

    @FormData(name = "shouldIgnored3", ignorable = false)
    private String shouldIgnored3 = null;

    @FormData(name = "shouldNotIgnored", ignorable = false)
    private String shouldNotIgnored = "";

    @FormData(name = "shouldNotIgnored2", ignorable = false)
    private int shouldNotIgnored2 = 0;

    public String getNormalString() {
        return normalString;
    }

    public void setNormalString(String normalString) {
        this.normalString = normalString;
    }

    public int getNormalInteger() {
        return normalInteger;
    }

    public void setNormalInteger(int normalInteger) {
        this.normalInteger = normalInteger;
    }

    public String getEncodeString() {
        return encodeString;
    }

    public void setEncodeString(String encodeString) {
        this.encodeString = encodeString;
    }

    public int getIgnorableInteger() {
        return ignorableInteger;
    }

    public void setIgnorableInteger(int ignorableInteger) {
        this.ignorableInteger = ignorableInteger;
    }
}
