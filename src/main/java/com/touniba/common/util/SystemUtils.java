package com.touniba.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;

/**
 * System utils.
 */
public class SystemUtils {

    /**
     * Build the cmd process
     *
     * @param cmd
     * @param err
     * @param out
     * @return
     * @throws Exception
     */
    public static Process cmdProcess(String cmd, final StringBuilder err, final StringBuilder out) throws Exception {
        String[] cmdArray = new String[3];
        if (isWindows()) {
            cmdArray[0] = "cmd.exe";
            cmdArray[1] = "/c";
        } else {
            cmdArray[0] = "/bin/sh";
            cmdArray[1] = "-c";
        }
        cmdArray[2] = cmd;
        final Process process = Runtime.getRuntime().exec(cmdArray);
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line = null;
                try {
                    while (null != (line = br.readLine())) {
                        if (null != err) {
                            err.append(line).append("\n");
                        }
                    }
                } catch (IOException e) {
                    // nothing
                }
            }
        }).start();
        ;
        new Thread(new Runnable() {
            @Override
            public void run() {
                // input
                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = null;
                try {
                    while (null != (line = br.readLine())) {
                        if (null != out) {
                            out.append(line).append("\n");
                        }
                    }
                } catch (IOException e) {
                    // nothing
                }
            }
        }).start();
        return process;
    }

    /**
     * Execute
     *
     * @param cmd
     * @param err
     * @param out
     * @return
     * @throws Exception
     */
    public static int exec(String cmd, final StringBuilder err, final StringBuilder out) throws Exception {
        return cmdProcess(cmd, err, out).waitFor();
    }

    /**
     * Get local name
     *
     * @return
     */
    public static String getLocalName() {
        return ManagementFactory.getRuntimeMXBean().getName();
    }

    /**
     * Is windows?
     *
     * @return
     */
    public static boolean isWindows() {
        String os = System.getProperty("os.name");
        return os.toLowerCase().startsWith("win");
    }
}
