
package com.cb.client.graphic;

import com.cb.client.Constants;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.net.*;
import java.util.logging.Logger;

public abstract class GenericSocket implements SocketListener {

    private final static Logger LOGGER =
            Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    public int port;
    protected Socket socketConnection = null;
    private BufferedWriter output = null;
    private BufferedReader input = null;
    private boolean ready = false;
    private Thread socketReaderThread;
    private Thread setupThread;
    private int debugFlags;


    public boolean debugFlagIsSet(int flag) {
        return ((flag & debugFlags) != 0);
    }



    public void connect() {
        try {
            /*
             * Background thread to set up and open the input and
             * output data streams.
             */
            setupThread = new SetupThread();
            setupThread.start();
            /*
             * Background thread to continuously read from the input stream.
             */
            socketReaderThread = new SocketReaderThread();
            socketReaderThread.start();
        } catch (Exception e) {
            if (debugFlagIsSet(Constants.instance().DEBUG_EXCEPTIONS)) {
                LOGGER.info(e.getMessage());
            }
        }
    }


    public void shutdown() {
        close();
    }


    private void close() {
        try {
            if (socketConnection != null && !socketConnection.isClosed()) {
                socketConnection.close();
            }

            closeAdditionalSockets();
            if (debugFlagIsSet(Constants.instance().DEBUG_STATUS)) {
                LOGGER.info("Connection closed");
            }

            onClosedStatus(true);
        } catch (IOException e) {
            if (debugFlagIsSet(Constants.instance().DEBUG_EXCEPTIONS)) {
                LOGGER.info(e.getMessage());
            }
        }
    }


    protected abstract void initSocketConnection() throws SocketException;

    protected abstract void closeAdditionalSockets();

    private synchronized void waitForReady() {
        while (!ready) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
    }

    private synchronized void notifyReady() {
        ready = true;
        notifyAll();
    }

    public void sendMessage(String msg) {
        try {
            output.write(msg, 0, msg.length());
            output.newLine();
            output.flush();
            if (debugFlagIsSet(Constants.instance().DEBUG_SEND)) {
                String logMsg = "send> " + msg;
                LOGGER.info(logMsg);
            }
        } catch (IOException e) {
            if (debugFlagIsSet(Constants.instance().DEBUG_EXCEPTIONS)) {
                LOGGER.info(e.getMessage());
            }
        }
    }

    class SetupThread extends Thread {

        @Override
        public void run() {
            try {
                initSocketConnection();
                if (socketConnection != null && !socketConnection.isClosed()) {
                    /*
                     * Get input and output streams
                     */
                    input = new BufferedReader(new InputStreamReader(
                            socketConnection.getInputStream()));
                    output = new BufferedWriter(new OutputStreamWriter(
                            socketConnection.getOutputStream()));
                    output.flush();
                }
                /*
                 * Notify SocketReaderThread that it can now start.
                 */
                notifyReady();
            } catch (IOException e) {
                if (debugFlagIsSet(Constants.instance().DEBUG_EXCEPTIONS)) {
                    LOGGER.info(e.getMessage());
                }
                /*
                 * This will notify the SocketReaderThread that it should exit.
                 */
                notifyReady();
            }
        }
    }

    class SocketReaderThread extends Thread {

        @Override
        public void run() {

            waitForReady();

            if (socketConnection != null && socketConnection.isConnected()) {
                onClosedStatus(false);
            }

            try {
                if (input != null) {
                    String line;
                    while ((line = input.readLine()) != null) {
                        if (debugFlagIsSet(Constants.instance().DEBUG_RECV)) {
                            String logMsg = "recv> " + line;
                            LOGGER.info(logMsg);
                        }

                        onMessage(line);
                    }
                }
            } catch (IOException e) {
                if (debugFlagIsSet(Constants.instance().DEBUG_EXCEPTIONS)) {
                    LOGGER.info(e.getMessage());
                }
            } finally {
                close();
            }
        }
    }


    public GenericSocket(int port, int debugFlags) {
        this.port = port;
        this.debugFlags = debugFlags;
    }
}
