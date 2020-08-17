package moe.yuuta.server.formprocessor;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HttpFormTest {

    @Test
    public void shouldToBuffer() {
        assertEquals("normalString=haoye&" +
                        "normalInteger=123&" +
                        "encodeString=+++Ri+kk+a&" +
                        "shouldNotIgnored=&" +
                        "shouldNotIgnored2=0",
                HttpForm.toBuffer(new SampleObject()).toString().trim());
        // Give the ignorable integer a value so it won't be ignored
        SampleObject sampleObject = new SampleObject();
        sampleObject.setIgnorableInteger(2333);
        assertEquals("normalString=haoye&" +
                        "normalInteger=123&" +
                        "encodeString=+++Ri+kk+a&" +
                        "ignorableInteger=2333&" +
                        "shouldNotIgnored=&" +
                        "shouldNotIgnored2=0",
                HttpForm.toBuffer(sampleObject).toString().trim());
    }
}