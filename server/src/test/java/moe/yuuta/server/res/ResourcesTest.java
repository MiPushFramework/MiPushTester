package moe.yuuta.server.res;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Resources.class)
public class ResourcesTest {
    private static final String KEY_TEST = "test";
    private static final String VALUE_TEST = "Test";
    private static final Locale LOCALE = Locale.ENGLISH;

    @Before
    public void setUp () {
        // mockStatic(Resources.class);
        PowerMockito.stub(method(Resources.class, "getBundle")).toReturn(new ResourceBundle() {
            @Override
            protected Object handleGetObject(String s) {
                return s.equals(KEY_TEST) ? VALUE_TEST : null;
            }

            @Override
            public Enumeration<String> getKeys() {
                Set<String> set = new HashSet<>(1);
                set.add(KEY_TEST);
                return new Vector<>(set).elements();
            }
        });
    }

    @Test
    public void getString() {
        String value = Resources.getString(KEY_TEST, LOCALE);
        assertNotNull(value);
        assertEquals(value, VALUE_TEST);
    }

    @Test(expected = MissingResourceException.class)
    public void getNotFoundString () {
        assertNull(Resources.getString("wueofsdifoq3wr", LOCALE));
    }

    @Test
    public void getRequestLocale() {

    }

    @Test
    public void getValueOrResourcesString() {

    }
}