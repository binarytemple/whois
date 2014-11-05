package net.ripe.db.whois.query.endtoend.compare.query;

import net.ripe.db.whois.common.domain.ResponseObject;
import net.ripe.db.whois.common.support.QueryExecutorConfiguration;
import net.ripe.db.whois.query.endtoend.compare.ComparisonExecutor;
import net.ripe.db.whois.query.endtoend.compare.TelnetInterface;
import net.ripe.db.whois.query.endtoend.compare.TelnetQueryExecutor;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

public class WhoisQueryExecutor implements ComparisonExecutor {
    private final TelnetQueryExecutor telnetQueryExecutor;

    public WhoisQueryExecutor(final QueryExecutorConfiguration configuration) throws UnknownHostException {
        telnetQueryExecutor = new TelnetQueryExecutor(configuration, TelnetInterface.WHOIS);
    }

    @Override
    public List<ResponseObject> getResponse(final String query) throws IOException {
        return telnetQueryExecutor.getResponse(query);
    }
}
