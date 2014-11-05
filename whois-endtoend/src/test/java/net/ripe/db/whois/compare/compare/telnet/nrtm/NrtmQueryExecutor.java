package net.ripe.db.whois.compare.compare.telnet.nrtm;

import net.ripe.db.whois.common.domain.ResponseObject;
import net.ripe.db.whois.common.support.QueryExecutorConfiguration;
import net.ripe.db.whois.compare.compare.telnet.TelnetInterface;
import net.ripe.db.whois.compare.compare.ComparisonExecutor;
import net.ripe.db.whois.compare.compare.telnet.TelnetQueryExecutor;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

public class NrtmQueryExecutor implements ComparisonExecutor {
    private final TelnetQueryExecutor telnetQueryExecutor;

    public NrtmQueryExecutor(final QueryExecutorConfiguration configuration) throws UnknownHostException {
        telnetQueryExecutor = new TelnetQueryExecutor(configuration, TelnetInterface.WHOIS);
    }

    @Override
    public List<ResponseObject> getResponse(final String query) throws IOException {
        return telnetQueryExecutor.getResponse(query);
    }
}
