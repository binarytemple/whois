package net.ripe.db.whois.compare.rest;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import net.ripe.db.whois.common.domain.ResponseObject;
import net.ripe.db.whois.compare.CompareResults;
import net.ripe.db.whois.compare.ComparisonExecutor;
import net.ripe.db.whois.compare.QueryReader;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static net.ripe.db.whois.compare.ComparisonPrinter.writeDifferences;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class RestCompareResults implements CompareResults {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestCompareResults.class);

    private final ComparisonExecutor executor1;
    private final ComparisonExecutor executor2;
    private final ExecutorService executorService;
    private final QueryReader queryReader;
    private final File targetDir;

    public RestCompareResults(final QueryReader queryReader,
                                final File targetDir,
                                final ComparisonExecutor executor1,
                                final ComparisonExecutor executor2) throws UnknownHostException {
        this.targetDir = targetDir;
        this.queryReader = queryReader;
        this.executorService = Executors.newFixedThreadPool(2);
        this.executor1 = executor1;
        this.executor2 = executor2;
    }

    @Override
    public void runCompareTest() throws Exception {
        LOGGER.info("Diffs saved in: {}", targetDir.getAbsolutePath());
        assertFalse("Dir should not exist: " + targetDir.getAbsolutePath(), targetDir.exists());
        assertTrue("Unable to create: " + targetDir.getAbsolutePath(), targetDir.mkdirs());
        assertTrue(new File(targetDir, "0_deltas_go_here.txt").createNewFile());

        int failedQueries = 0;

        for (final String queryString : queryReader.getQueries()) {

            if (StringUtils.isBlank(queryString) || queryString.startsWith("#")) {
                continue;
            }

            LOGGER.info("Compare query: {}", queryString);

            final Future<List<ResponseObject>> queryExecutor1Future = executeQuery(executor1, queryString);
            final Future<List<ResponseObject>> queryExecutor2Future = executeQuery(executor2, queryString);

            final List<ResponseObject> queryExecutor1Result = queryExecutor1Future.get();
            final List<ResponseObject> queryExecutor2Result = queryExecutor2Future.get();

            final Patch patch = DiffUtils.diff(queryExecutor1Result, queryExecutor2Result);
            final List<Delta> deltas = patch.getDeltas();
            if (!deltas.isEmpty()) {
                writeDifferences(targetDir, queryString, queryExecutor1Result, queryExecutor2Result, deltas);
                failedQueries++;
                LOGGER.error("Query '{}' has differences", queryString);
            }
        }

        assertThat("Number of failed queries", failedQueries, is(0));
    }

    @Override
    public Future<List<ResponseObject>> executeQuery(final ComparisonExecutor comparisonExecutor, final String queryString) {
        return executorService.submit(new Callable<List<ResponseObject>>() {
            @Override
            public List<ResponseObject> call() throws Exception {
                return comparisonExecutor.getResponse(queryString);
            }
        });
    }
}
