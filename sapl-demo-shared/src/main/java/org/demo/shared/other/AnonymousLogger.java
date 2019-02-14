package org.demo.shared.other;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Handler;
import java.util.logging.Logger;

@Slf4j
public class AnonymousLogger {

    private static final Logger LOG = Logger.getAnonymousLogger();
    private String filename = "ObligationLog.log";
    private static  final int SIZE = 50000;
    private static final int ROTATIONCOUNT = 2;

    public AnonymousLogger(){
        configure();
    }

    public Logger getLogger(){
        return LOG;
    }

    private void configure(){
        try {
            FileHandler fh =new FileHandler(filename,SIZE,ROTATIONCOUNT,true);
            LOG.addHandler(fh);
            fh.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    SimpleDateFormat logTime = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.getDefault());
                    Calendar cal = new GregorianCalendar();
                    cal.setTimeInMillis(record.getMillis());
                    return record.getLevel()
                            + logTime.format(cal.getTime())
                            + " || "
                            + record.getSourceClassName().substring(
                            record.getSourceClassName().lastIndexOf(".")+1,
                            record.getSourceClassName().length())
                            + "."
                            + record.getSourceMethodName()
                            + "() : "
                            + record.getMessage() + "\n";
                }
            });
        } catch (IOException e) {
            LOGGER.error("IOException" ,e );
        }
        addCloseHandlersShutdownHook();


    }

    private static void addCloseHandlersShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Close all handlers to get rid of empty .LCK files
            for (Handler handler : LOG.getHandlers()) {
                handler.close();
            }
        }));
    }
}
