// import packages for java library 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/*
Project Description: This project requires students to implement an iterative (single-threaded) server for use in a client-server configuration to examine, analyze, and study the effects an iterative server has on the efficiency (average turn-around time) of processing client requests.
By: Christopher Clark

*/
public class Client {
    public static void main(String[] args) {
        int portNumber = 4444;
        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("Iterative server started on port" + portNumber);
            
            while(true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket.getInetAddress());
                
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String outputLine = "";
                    switch (inputLine) {
                        case "date":
                            outputLine = new Date().toString();
                            break;
                        case "uptime":
                            outputLine = getUptime();
                            break;
                        case "memory":
                            outputLine = getMemoryUsage();
                            break;
                        case "netstat":
                            outputLine = getNetstat();
                            break;
                        case "users":
                            outputLine = getCurrentUsers();
                            break;
                        case "processes":
                            outputLine = getRunningProcesses();
                            break;
                        default:
                            outputLine = "Invalid request";
                    }
                    out.println(outputLine);
                }
                
                // close the connection
                out.close();
                in.close();
                clientSocket.close();
            }
       
        } catch (IOException e) {
            System.err.println("Could'nt listen on port " + portNumber);
            System.exit(1);
        }
    }

    // create functions getUptime, getMemoryUsage, getNetstat, getCurrentUsers, getRunningProcesses
    private static String getUptime() {
        // TDO: Implement uptime functionality here 
        return "uptime functionality not implemented yet";
    }

    private static String getMemoryUsage() {
        // TDO: Implement memory usage functionality here 
        return "memory usage functionality not implemented yet";
    }

    private static String getNetstat() {
        // TDO: Implement netstat functionality here 
        return "netstat functionality not implemented yet";
    }

    private static String getCurrentUsers() {
        // TDO: Implement current users functionality here 
        return "current users functionality not implemented yet";
    }

    private static String getRunningProcesses() {
        // TDO: Implement running processes functionality here 
        return "running processes functionality not implemented yet";
    }

    
}


