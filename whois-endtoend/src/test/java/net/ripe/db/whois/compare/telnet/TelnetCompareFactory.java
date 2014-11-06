package net.ripe.db.whois.compare.telnet;

import net.ripe.db.whois.common.support.QueryExecutorConfiguration;
import net.ripe.db.whois.compare.CompareResults;
import net.ripe.db.whois.compare.QueryReader;
import net.ripe.db.whois.compare.TargetInterface;

import java.io.File;
import java.net.UnknownHostException;

import static net.ripe.db.whois.compare.TargetInterface.WHOIS;
import static net.ripe.db.whois.compare.TargetInterface.NRTM;

public class TelnetCompareFactory {

    public CompareResults createCompareResults(final QueryExecutorConfiguration config1,
                                               final QueryExecutorConfiguration config2,
                                               final QueryReader queryReader,
                                               final File targetDir,
                                               final TargetInterface targetInterface) throws UnknownHostException {



        final TelnetExecutorFactory telnetExecutorFactory = new TelnetExecutorFactory();

        if (targetInterface == NRTM) {
            return new CompareTelnetResults(
                    queryReader,
                    targetDir,
                    telnetExecutorFactory.createTelnetExecutor(NRTM, config1),
                    telnetExecutorFactory.createTelnetExecutor(NRTM, config2));

        } else if (targetInterface == WHOIS) {
            return new CompareTelnetResults(
                    queryReader,
                    targetDir,
                    telnetExecutorFactory.createTelnetExecutor(WHOIS, config1),
                    telnetExecutorFactory.createTelnetExecutor(WHOIS, config2));

        } else {
            throw new UnsupportedOperationException("This executor should only be used for telnet-like operation");
        }
    }
}
