package moe.yuuta.server.formprocessor;

class SampleObject {
    @FormData(value = "normalString")
    private String normalString = "haoye";

    @FormData(value = "normalInteger")
    private int normalInteger = 123;

    @FormData(value = "encodeString", urlEncode = true)
    private String encodeString = "   Ri kk a";

    @FormData(value = "ignorableInteger")
    private int ignorableInteger = 0;

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
