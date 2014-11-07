package net.ripe.db.whois.compare.rest;

import net.ripe.db.whois.common.ManualTest;
import net.ripe.db.whois.compare.ComparisonExecutorConfig;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;

@Category(ManualTest.class)
public class RestCompareTwoInstancesByComparisonFile {

    @Test
    public void test_deployed_versions() throws Exception {

        FileUtils.deleteDirectory(new File("target/qry/comparison_rest_file"));

        new RestCompareResults(
                new RestQueryReader("comparison_rest", RestQueryReader.RestResponseType.ALL),
                new File("target/qry/comparison_rest_file"),
                new RestExecutor(ComparisonExecutorConfig.PRE1),
                new RestExecutor(ComparisonExecutorConfig.PRE2))
                    .runCompareTest();
    }
}
