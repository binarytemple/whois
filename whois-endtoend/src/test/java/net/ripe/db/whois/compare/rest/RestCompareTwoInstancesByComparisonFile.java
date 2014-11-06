package net.ripe.db.whois.compare.rest;

import net.ripe.db.whois.common.ManualTest;
import net.ripe.db.whois.compare.ComparisonExecutorConfig;
import net.ripe.db.whois.compare.QueryReader;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

@Category(ManualTest.class)
public class RestCompareTwoInstancesByComparisonFile {

    @Test
    public void test_deployed_versions() throws Exception {

        FileUtils.deleteDirectory(new File("target/qry/comparison_rest_file"));

        new RestCompareResults(
                ComparisonExecutorConfig.PRE1,
                ComparisonExecutorConfig.PRE2,
                new QueryReader(new ClassPathResource("comparison_rest")) {
                    @Override
                    protected String getQuery(final String line) {
                        return line;
                    }
                },
                new File("target/qry/comparison_rest_file"),
                RestCompareResults.QueryType.ALL)
                .runCompareTest();
    }
}
