package net.ripe.db.whois.query.endtoend.compare.nrtm;

import net.ripe.db.whois.common.ManualTest;
import net.ripe.db.whois.common.support.QueryExecutorConfiguration;
import net.ripe.db.whois.query.endtoend.compare.CompareTelnetResults;
import net.ripe.db.whois.query.endtoend.compare.TelnetInterface;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

@Category(ManualTest.class)
public class CompareTwoNrtmInstances {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompareTwoNrtmInstances.class);

    @Test
    public void test_deployed_versions() throws Exception {

        FileUtils.deleteDirectory(new File("target/qry/nrtm_comparison"));

        LOGGER.info("Starting NRTM comparison tests");
        new CompareTelnetResults(
                QueryExecutorConfiguration.PRE1,
                QueryExecutorConfiguration.PRE2,
                TelnetInterface.NRTM,
                new NrtmReader(Long.valueOf(System.getProperty("latestSerialId"))),
                new File("target/qry/nrtm_comparison"),
                1).runCompareTest();
    }

    @Test
    public void printInputQueryList(){
        System.out.println(">>>>>>");
        System.out.println(NrtmReader.generateQueries(5000));
        System.out.println("<<<<<<");
    }
}


