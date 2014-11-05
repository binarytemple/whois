package net.ripe.db.whois.query.endtoend.compare.nrtm;

import net.ripe.db.whois.common.ManualTest;
import net.ripe.db.whois.common.support.QueryExecutorConfiguration;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;

@Category(ManualTest.class)
public class CompareTwoNrtmInstances {

    @Test
    public void test_deployed_versions() throws Exception {

        new CompareNrtmResults(
                QueryExecutorConfiguration.PRE1,
                QueryExecutorConfiguration.PRE2,
                new NrtmReader(Long.valueOf(System.getProperty("latestSerialId"))),
                new File("target/qry/nrtm_comparison_files"),
                1).runCompareTest();
    }

    @Test
    public void printInputQueryList(){
        System.out.println(">>>>>>");
        System.out.println(NrtmReader.generateQueries(5000));
        System.out.println("<<<<<<");
    }
}


