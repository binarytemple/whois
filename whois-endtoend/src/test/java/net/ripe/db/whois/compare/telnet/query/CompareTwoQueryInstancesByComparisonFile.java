package net.ripe.db.whois.compare.telnet.query;

import net.ripe.db.whois.common.ManualTest;
import net.ripe.db.whois.common.support.QueryExecutorConfiguration;
import net.ripe.db.whois.compare.telnet.TelnetInterface;
import net.ripe.db.whois.compare.telnet.CompareTelnetResults;
import net.ripe.db.whois.compare.QueryReader;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

@Category(ManualTest.class)
public class CompareTwoQueryInstancesByComparisonFile {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompareTwoQueryInstancesByComparisonFile.class);

    @Test
    public void test_deployed_versions() throws Exception {
        LOGGER.info("Starting Whois port 43 comparison tests");

        FileUtils.deleteDirectory(new File("target/qry/comparison_file"));

        new CompareTelnetResults(
                QueryExecutorConfiguration.PRE1,
                QueryExecutorConfiguration.PRE2,
                TelnetInterface.WHOIS,
                new QueryReader(new ClassPathResource("comparison_queries")) {
                    @Override
                    protected String getQuery(final String line) {
                        return line;
                    }
                },
                new File("target/qry/comparison_file"),
                1).runCompareTest();
    }
}
