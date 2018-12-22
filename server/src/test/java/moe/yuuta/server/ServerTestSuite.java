package moe.yuuta.server;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import moe.yuuta.server.api.ApiHandlerImplTest;
import moe.yuuta.server.api.ApiUtilsTest;
import moe.yuuta.server.api.ApiVerticleTest;
import moe.yuuta.server.dataverify.DataVerifierTest;
import moe.yuuta.server.formprocessor.HttpFormTest;
import moe.yuuta.server.res.ResourcesTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ResourcesTest.class,
        MainVerticleTest.class,
        DataVerifierTest.class,
        ApiVerticleTest.class,
        ApiUtilsTest.class,
        ApiHandlerImplTest.class,
        HttpFormTest.class
})
public class ServerTestSuite {
}
