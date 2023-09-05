package server;

import javax.websocket.server.ServerEndpoint;
import javax.xml.crypto.Data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import server.gazepoint.api.ack.AckXmlObject;
import server.gazepoint.api.get.GetCommand;
import server.gazepoint.api.get.GetEnableSendCommand;
import server.gazepoint.api.recv.RecXmlObject;
import server.gazepoint.api.set.SetCommand;
import server.gazepoint.api.set.SetEnableSendCommand;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * Connects to GP3 and reads data.
 * Utilizes a thread safe queue to write/read data recieved from the tracker. API specifies that data is sent in the form of XML
 * so we utilize XML Annotations to construct XML objects after receiving API data. This is to simplify the process of
 * interacting with the tracking data.
 */
@ServerEndpoint("/gp3connection")
public class GP3Socket {

    private String hostName = "localhost";
    private int port = 4242;
    private Socket socket;

    private BufferedReader input;
    private PrintStream output;

    private final XmlMapper xmlMapper;
    private volatile boolean isWritingToGazeBuffer = false;


    /**
     * We use a FIFO queue to handle the gaze data being sent to preserve the correct order. if a FILO queue is used
     * the data will be out of order and you'll only be reading the most recent data.
     */
    private final int windowSize = 60;
    private final ArrayBlockingQueue<RecXmlObject> gazeDataQueue = new ArrayBlockingQueue<>(windowSize);
    private final LinkedBlockingQueue<AckXmlObject> ackDataQueue = new LinkedBlockingQueue<>();

    public GP3Socket() {
        xmlMapper = new XmlMapper();

    }

    /**
     * Opens socket port to the gazepoint tracker
     * @throws IOException
     */
    public void connect() throws IOException {

        socket = new Socket(hostName, port);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintStream(socket.getOutputStream());

    }

    /**
     * Initiates calibration on the tracker.
     * @throws JsonProcessingException
     */
    public void startCalibration() throws JsonProcessingException {
        GetCommand calibrateStart = new GetCommand(GazeApiCommands.CALIBRATE_START);
        output.println(xmlMapper.writeValueAsString(calibrateStart));
    }

    /**
     * Initiates ENABLE_SEND_DATA with the tracker to begin gaze data stream.
     * This method creates a new thread so be careful. The thread will write data into the gaze buffer.
     * @throws IOException
     */
    public void startGazeDataStream() throws IOException {
        SetEnableSendCommand setEnableSendCommand = new SetEnableSendCommand("ENABLE_SEND_DATA", true);

        output.println(xmlMapper.writeValueAsString(setEnableSendCommand));
        //Read ACK
        String ack = input.readLine();
        System.out.println("ACK: " + ack);
        //Gaze is now sending data in form of REC, begin write runnable
        Thread gazeBufferThread = new Thread(new DataStreamRunnable(this));
        gazeBufferThread.start();

    }

    private void writeToGazeBuffer() throws IOException {
        isWritingToGazeBuffer = true;
         FileWriter fileWriter = new FileWriter("rec_command_test.txt");
        while(isWritingToGazeBuffer) {
            String msg = input.readLine();
            //Only listen for REC
            //Offer data to queue, block if queue is being used.
            //Look for Acks as well.
            //Acks should be stored elsewhere.
            //Test to see if the command can map
            fileWriter.write(msg);
//            if (GazeApiCommands.mapAckStringToXmlObject(msg) != null)
//                ackDataQueue.offer(GazeApiCommands.mapAckStringToXmlObject(msg));
//
//            gazeDataQueue.offer(GazeApiCommands.mapRecCommandToXmlObject(input.readLine()));
        }
        System.out.println("ended writing to gaze buffer.");
    }

    /**
     * Grabs the head of the gaze data xml object queue (Reference gazepoint API)
     * @return Returns the XML Data Object that details whatever GazeData has been sent from the tracker
     */
    public RecXmlObject readGazeDataFromBuffer() {
        return gazeDataQueue.poll();
    }

    /**
     * Pauses the data stream by sending the ENABLE_SEND_DATA command w/ false flag.
     * @return Returns the ACK Xml from the server.
     * @throws IOException If this is thrown, it may be a logic issue where we are reading the wrong input line.
     */
    public AckXmlObject stopGazeDataStream() throws IOException {
        SetEnableSendCommand enableSendData = new SetEnableSendCommand(GazeApiCommands.ENABLE_SEND_DATA, false);
        output.println(xmlMapper.writeValueAsString(enableSendData));
        this.isWritingToGazeBuffer = false;
        return xmlMapper.readValue(input.readLine(), AckXmlObject.class);
    }

    public void write(String msg) {
        output.println(msg);
    }

    public void writeSetCommand(SetCommand setCommand) throws JsonProcessingException {
        output.println(xmlMapper.writeValueAsString(setCommand));

    }

    private class DataStreamRunnable implements Runnable {

        private final GP3Socket gp3Socket;
        DataStreamRunnable(GP3Socket gp3Socket) {
            this.gp3Socket = gp3Socket;
        }
        @Override
        public void run() {
            System.out.println("run method invoked");
            try {
                this.gp3Socket.writeToGazeBuffer();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
