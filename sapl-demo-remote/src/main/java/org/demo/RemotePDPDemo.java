/*******************************************************************************
 * Copyright 2017-2018 Dominic Heutelbeck (dheutelbeck@ftk.de)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.demo;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import io.sapl.api.pdp.Request;
import io.sapl.api.pdp.Response;
import io.sapl.pdp.remote.RemotePolicyDecisionPoint;

public class RemotePDPDemo {
    private static final Logger LOG = LoggerFactory.getLogger(RemotePDPDemo.class);

    private static final String DEFAULT_PORT = "8443";
    private static final String DEFAULT_HOST = "localhost";
    private static final String HELP_DOC = "print this message";
    private static final String PORT_DOC = "port of the policy decision point";
    private static final String HOST_DOC = "hostname of the policy decision point";
    private static final String USAGE = "java -jar sapl-demo-remote-2.0.0-SNAPSHOT-jar-with-dependencies.jar";
    private static final String HELP = "help";
    private static final String PORT = "port";
    private static final String HOST = "host";

    private static final int RUNS = 10;
    private static final double BILLION = 1_000_000_000.0D;
    private static final double MILLION = 1_000_000.0D;

    private static double nanoToMs(double nanoseconds) {
        return nanoseconds / MILLION;
    }

    private static double nanoToS(double nanoseconds) {
        return nanoseconds / BILLION;
    }

    public static void main(String[] args) {

        Options options = new Options();

        options.addOption(HOST, true, HOST_DOC);
        options.addOption(PORT, true, PORT_DOC);
        options.addOption(HELP, false, HELP_DOC);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            LOG.info("encountered an error running the demo: {}",
                    e.getMessage(), e);
            System.exit(1);
        }

        if (cmd.hasOption(HELP)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(USAGE, options);
        } else {
            String host = cmd.getOptionValue(HOST);
            if (host == null) {
                host = DEFAULT_HOST;
            }
            String portOption = cmd.getOptionValue(PORT);
            if (portOption == null) {
                portOption = DEFAULT_PORT;
            }
            int port = Integer.parseInt(portOption);
            runDemo(host, port);
        }
    }

    private static void runDemo(String host, int port) {
        RemotePolicyDecisionPoint pdp = new RemotePolicyDecisionPoint(host, port);

        long start = System.nanoTime();
        for (int i = 0; i < RUNS; i++) {
            Response response = pdp.decide(buildRequest("willi", "test-read", "something")).block();
            LOG.info("response: {}", response);
        }
        long end = System.nanoTime();

        LOG.info("Start : {}", start);
        LOG.info("End   : {}", end);
        LOG.info("Runs  : {}", RUNS);
        LOG.info("Total : {}s", nanoToS((double) end - start));
        LOG.info("Avg.  : {}ms", nanoToMs(((double) end - start) / RUNS));
    }
    
    private static final Request buildRequest(Object subject, Object action, Object resource) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());
		return new Request(mapper.valueToTree(subject), mapper.valueToTree(action), mapper.valueToTree(resource), null);
	}
}
