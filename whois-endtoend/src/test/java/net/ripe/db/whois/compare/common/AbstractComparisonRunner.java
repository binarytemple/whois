package net.ripe.db.whois.compare.common;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import net.ripe.db.whois.common.domain.ResponseObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public abstract class AbstractComparisonRunner implements ComparisonRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractComparisonRunner.class);

    private final ComparisonExecutor executor1;
    private final ComparisonExecutor executor2;
    private final ExecutorService executorService;
    private final QueryReader queryReader;
    private final File targetDir;

    public AbstractComparisonRunner(final QueryReader queryReader,
                                    final File targetDir,
                                    final ComparisonExecutor executor1,
                                    final ComparisonExecutor executor2) throws UnknownHostException {
        this.targetDir = targetDir;
        this.queryReader = queryReader;
        this.executorService = Executors.newFixedThreadPool(2);
        this.executor1 = executor1;
        this.executor2 = executor2;
    }

    protected abstract void preRunHook() throws IOException;
    protected abstract ComparisonResult filterOutKnownDifferences(final List<ResponseObject> executor1Result,
                                                                  final List<ResponseObject> executor2Result);

    @Override
    public void runCompareTest() throws Exception {
        LOGGER.info("Diffs saved in: {}", targetDir.getAbsolutePath());
        assertFalse("Dir should not exist: " + targetDir.getAbsolutePath(), targetDir.exists());
        assertTrue("Unable to create: " + targetDir.getAbsolutePath(), targetDir.mkdirs());
        assertTrue(new File(targetDir, "0_deltas_go_here.txt").createNewFile());

        preRunHook();

        int failedQueries = 0;

        for (final String queryString : queryReader.getQueries()) {

            if (StringUtils.isBlank(queryString) || queryString.startsWith("#")) {
                continue;
            }

            LOGGER.info("Compare query: {}", queryString);

            final Future<List<ResponseObject>> executor1Future = executeQuery(executor1, queryString);
            final Future<List<ResponseObject>> executor2Future = executeQuery(executor2, queryString);

            final List<ResponseObject> executor1Result = executor1Future.get();
            final List<ResponseObject> executor2Result = executor2Future.get();

            final ComparisonResult result = filterOutKnownDifferences(executor1Result, executor2Result);

            final Patch patch = DiffUtils.diff(result.getList1(), result.getList2());
            final List<Delta> deltas = patch.getDeltas();
            if (!deltas.isEmpty()) {
                ComparisonPrinter.writeDifferences(targetDir, queryString, executor1Result, executor2Result, deltas);
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

    protected class ComparisonResult {
        final List<ResponseObject> list1, list2;

        public ComparisonResult(final List<ResponseObject> list1, final List<ResponseObject> list2) {
            this.list1 = list1;
            this.list2 = list2;
        }

        public List<ResponseObject> getList1() {
            return list1;
        }

        public List<ResponseObject> getList2() {
            return list2;
        }
    }
}
