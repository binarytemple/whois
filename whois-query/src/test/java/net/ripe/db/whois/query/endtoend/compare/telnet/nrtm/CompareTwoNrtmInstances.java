package net.ripe.db.whois.query.endtoend.compare.telnet.nrtm;

import net.ripe.db.whois.common.ManualTest;
import net.ripe.db.whois.common.support.QueryExecutorConfiguration;
import net.ripe.db.whois.query.endtoend.compare.CompareTelnetResults;
import net.ripe.db.whois.query.endtoend.compare.telnet.TelnetClientUtils;
import net.ripe.db.whois.query.endtoend.compare.telnet.TelnetInterface;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static net.ripe.db.whois.common.support.QueryExecutorConfiguration.PRE1;
import static net.ripe.db.whois.common.support.QueryExecutorConfiguration.PRE2;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@Category(ManualTest.class)
public class CompareTwoNrtmInstances {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompareTwoNrtmInstances.class);

    @Test
    public void test_deployed_versions() throws Exception {

        FileUtils.deleteDirectory(new File("target/qry/nrtm_comparison"));

        QueryExecutorConfiguration config1 = PRE1;
        QueryExecutorConfiguration config2 = PRE2;

        LOGGER.info("Starting NRTM comparison tests");
        new CompareTelnetResults(
                config1,
                config2,
                TelnetInterface.NRTM,
                new NrtmReader(getLatestSerialId(config1, config2)),
                new File("target/qry/nrtm_comparison"),
                1).runCompareTest();
    }

    private long getLatestSerialId(QueryExecutorConfiguration config1, QueryExecutorConfiguration config2) throws IOException {
        final long latestSerialId1 = TelnetClientUtils.getLatestSerialId(config1);
        final long latestSerialId2 = TelnetClientUtils.getLatestSerialId(config2);
        assertThat("Latest Serial Ids must be the same for both servers", latestSerialId1, is(latestSerialId2));

        return latestSerialId1;
    }
}


