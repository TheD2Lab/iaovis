package server;

import analysis.ontomap.OntoMapCsv;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import server.gazepoint.api.XmlObject;
import server.gazepoint.api.ack.AckXmlObject;
import server.gazepoint.api.recv.RecXmlObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class GazepointSimulationServer implements Runnable {
    private String url;
    private int port;
    private List<RecXmlObject> recXmlPackets;
    private List<AckXmlObject> ackXmlPackets;
    private List<XmlObject> packets;
    private ServerSocket serverSocket;
    private Socket socket;
    private File gazeFile;
    private CSVReader gazeCsvReader;
    private List<String> headerRow;
    private XmlMapper xmlMapper;
    private PrintStream output;
    private boolean debug;

    public GazepointSimulationServer(String url, int port, File gazeFile, boolean debug) throws IOException, CsvValidationException {
        this.url = url;
        this.port = port;
        this.gazeFile = gazeFile;
        this.debug = debug;
        this.serverSocket = new ServerSocket(port);
        FileReader fileReader = new FileReader(gazeFile);

        this.gazeCsvReader = new CSVReader(fileReader);
        headerRow = Arrays.asList(gazeCsvReader.readNext());
        this.xmlMapper = new XmlMapper();

    }


    public String getUrl() {
        return url;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void run() {
        if (debug) {
            System.out.println("Simulation socket is listening on port: " + this.port);
        }

        try {
            this.socket = serverSocket.accept();
            if (debug)
                System.out.println("Accepted socket.");
            this.output = new PrintStream( this.socket.getOutputStream());

            boolean doneReading = false;
            int numLines = 0;
            if (debug)
                System.out.println("Simulation starting, reading file: " + this.gazeFile.getName());
            while (!doneReading) {
                //Get time
                double curTimeMs = System.currentTimeMillis();
                //Open gazeFile
                String[] cells = new String[0];
                //Read gaze inputs
                cells = this.gazeCsvReader.readNext();
                if (cells != null) {
                    ++numLines;
                    //Convert to recXmlObject
                    RecXmlObject recXmlObject = OntoMapCsv.getRecXmlObjectFromCells(headerRow, cells);
                    //Calculate time diff
                    double timeDiff = curTimeMs - System.currentTimeMillis();
                    //if (timeDiff < 7ms (poll rate)
                    if (timeDiff < 7.0) {
                        Thread.sleep((long) ((long) 7.0 - timeDiff));
                    }
                    //sleep for 7ms - timediff
                    //else
                    //write to socket immediately
                    output.println(xmlMapper.writeValueAsString(recXmlObject));
                } else {
                    doneReading = true;
                }
            }
            if (debug)
                System.out.println("Finished sending data. num lines sent: " + numLines);
            this.socket.close();
            serverSocket.close();

        } catch (IOException | InterruptedException | CsvValidationException e) {
            throw new RuntimeException(e);
        }

    }
}