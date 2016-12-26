package com.example.dyckster.sebbiatesttask.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.regex.Matcher;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

public class AdvancedLogger {

    private static class Database extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "logs.db";
        private static final int DATABASE_VERSION = 1;

        private final class LogsTable {

            private static final String TABLE_NAME = "logs";

            private static final String COLUMN_FILENAME = "filename";
            private static final String COLUMN_START_DATE = "start";

        }

        public Database(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(String.format("create table %s (%s text NOT NULL PRIMARY KEY, %s INTEGER)",
                    LogsTable.TABLE_NAME,
                    LogsTable.COLUMN_FILENAME,
                    LogsTable.COLUMN_START_DATE));
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

    }

    private static Database database;
    private static Logger logger;

    private static File getLogsDirectory(Context context) {
        File logsDirectory = new File(context.getFilesDir(), "logs");
        return logsDirectory;
    }

    private static File getNewLogFile(Context context, long timeSeconds) {
        return new File(getLogsDirectory(context), "log" + timeSeconds + ".log");
    }

    private static String getLogsDirectoryExternal() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp";
    }

    public static void initialize(Context context) {
        try {
            database = new Database(context);
            long timeSeconds = new Date().getTime() / 1000;
            long minDate = timeSeconds - 60 * 60 * 24;
            File logsDirectory = getLogsDirectory(context);
            logsDirectory.mkdirs();
            File logFile = getNewLogFile(context, timeSeconds);

            SQLiteDatabase db = database.getWritableDatabase();
            try {
                db.beginTransaction();
                int entriesDeleted = db.delete(Database.LogsTable.TABLE_NAME, Database.LogsTable.COLUMN_START_DATE + " < " + minDate, null);
                android.util.Log.e("Logger", "Deleted " + entriesDeleted + " outdated logs");
                db.execSQL("INSERT INTO " + Database.LogsTable.TABLE_NAME + " VALUES (\"" + logFile.getAbsolutePath() + "\", " + timeSeconds + ")");
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            try {
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("log(\\d+).log");
                File[] files = logsDirectory.listFiles();
                for (File file : files) {
                    try {
                        Matcher m = pattern.matcher(file.getName());
                        if (m.matches()) {
                            long date = Long.parseLong(m.group(1));
                            if (date < minDate) {
                                file.delete();
                                android.util.Log.e("Logger", "Deleting log file " + file.getName());
                            }
                        }
                    } catch (Exception e) {
                        android.util.Log.e("Logger", "Error checking log file");
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                android.util.Log.e("Logger", "Error deleting outdated log files");
                e.printStackTrace();
            }

            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            lc.reset();

            PatternLayoutEncoder encoder1 = new PatternLayoutEncoder();
            encoder1.setContext(lc);
            encoder1.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %msg%n");
            encoder1.start();

            FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
            fileAppender.setContext(lc);
            fileAppender.setFile(logFile.getAbsolutePath());
            fileAppender.setEncoder(encoder1);
            fileAppender.start();

            ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            root.addAppender(fileAppender);
            logger = LoggerFactory.getLogger("LOG");
        } catch (Exception e) {
            android.util.Log.e("Logger", "Cannot log to file, logging to logcat instead");
            e.printStackTrace();
        }
    }

    private static void appendLog(BufferedWriter fw, String filename) {
        BufferedReader fr = null;
        try {
            fw.write(filename);
            fw.write('\n');
            fw.write(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            fw.write('\n');
            fr = new BufferedReader(new FileReader(filename));
            String line = fr.readLine();
            while (line != null) {
                fw.write(line);
                fw.write('\n');
                line = fr.readLine();
            }
            fw.write("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            fw.write('\n');
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (Exception ignore) {
                }
            }
        }
    }

    public static File getLogToSend(Context context, String outName) throws IOException {
        copyFile(getCombinedLog(context), outName);
        File file = new File(getLogsDirectoryExternal() + "/" + outName);
        return file;
    }

    private static void copyFile(File inputFile, String outputFile) {

        String outputPath = getLogsDirectoryExternal();
        InputStream in = null;
        OutputStream out = null;
        try {

            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File outFile = new File(outputPath + "/" + outputFile);
            if (outFile.exists()) {
                outFile.delete();
            }

            in = new FileInputStream(inputFile);
            out = new FileOutputStream(outFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();

            out.flush();
            out.close();

        } catch (FileNotFoundException fnfe1) {
            Log.e(fnfe1.getMessage());

        } catch (Exception e) {
            Log.e(e.getMessage());
        }

    }

    public static File getCombinedLog(Context context) throws IOException {
        File logsDirectory = getLogsDirectory(context);
        logsDirectory.mkdirs();
        File combinedLog = new File(logsDirectory, "combined.log");
        if (combinedLog.exists()) {
            combinedLog.delete();
        }
        combinedLog.createNewFile();

        BufferedWriter fw = new BufferedWriter(new FileWriter(combinedLog));
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor c = null;
        try {
            c = db.query(Database.LogsTable.TABLE_NAME, null, null, null, null, null, null);
            while (c.moveToNext()) {
                String filename = c.getString(0);
                appendLog(fw, filename);
            }
        } finally {
            if (c != null) c.close();
            if (db != null) db.close();
        }

        fw.close();

        long logLenght = combinedLog.length();
        long megabyte = 5 * 1024 * 1024;
        if (logLenght > megabyte) {
            File truncatedLog = new File(logsDirectory, "truncated.log");
            if (truncatedLog.exists()) {
                truncatedLog.delete();
            }
            truncatedLog.createNewFile();

            InputStream fis = null;
            OutputStream fos = null;

            try {
                fis = new BufferedInputStream(new FileInputStream(combinedLog), 8096);
                fos = new BufferedOutputStream(new FileOutputStream(truncatedLog), 8096);

                fis.skip(logLenght - megabyte);

                byte[] buffer = new byte[8096];
                int dataRead;
                while ((dataRead = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, dataRead);
                }
            } finally {
                if (fis != null)
                    fis.close();
                if (fos != null)
                    fos.close();
            }
            return truncatedLog;
        } else {
            return combinedLog;
        }
    }

    public static String getLastLogs(Context context) {
        String result = null;
        final int KB = 1 * 1024;

        final int sizeForSending = 25 * KB;

        SQLiteDatabase db = database.getReadableDatabase();
        Cursor c = db.query( // заберем последнее имя файла логов из базы
                Database.LogsTable.TABLE_NAME,
                new String[]{Database.LogsTable.COLUMN_FILENAME},
                null,
                null,
                null,
                null,
                Database.LogsTable.COLUMN_START_DATE + " DESC",
                "1"
        );

        if (!c.moveToFirst()) return null;

        File lastLogFile = new File(c.getString(0));

        if (c != null) c.close();

        BufferedInputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(lastLogFile), sizeForSending);
            if (lastLogFile.length() > sizeForSending)
                is.skip(lastLogFile.length() - sizeForSending);

            byte[] buffer = new byte[sizeForSending];
            is.read(buffer);
            result = new String(buffer);

        } catch (FileNotFoundException e) {
            android.util.Log.e("Logger", "Не найден файл логов!", e);
        } catch (IOException e) {
            android.util.Log.e("Logger", "Ошибка при открытии файла логов!", e);
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    android.util.Log.e("Logger", "Ошибка при закрытии файла логов!", e);
                }
        }

        return result;
    }

    public static void info(String message) {
        if (logger != null) {
            logger.info(message);
        }
    }

    public static void error(String message) {
        if (logger != null) {
            logger.error(message);
        }
    }

    public static void debug(String message) {
        if (logger != null) {
            logger.debug(message);
        }
    }

    public static void warn(String message) {
        if (logger != null) {
            logger.warn(message);
        }
    }

}
