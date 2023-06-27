import java.io.*; // import packages for java library 
import java.net.*;
import java.util.*;

/*
Project Description: This project requires students to implement an iterative (single-threaded) server for use in a client-server configuration to examine, analyze, and study the effects an iterative server has on the efficiency (average turn-around time) of processing client requests.
By: Christopher Clark

*/

// start of class IterativeServer
public class IterativeServer {
    // The constants representing the operations & request counts
    private static final String[] OPERATIONS = {"Date and Time", "Uptime", "Memory Use", "Netstat", "Current Users", "Running Processes"};
    private static final int[] REQUEST_COUNTS = {1, 5, 10, 15, 20, 25};

    public static void main(String[] args) {
         // Create scanner for user input
        Scanner scanner = new Scanner(System.in);

         // Get server address & port from user
        String serverAddress = getInput(scanner, "Enter server address: ");
        int serverPort = Integer.parseInt(getInput(scanner, "Enter server port: "));

        // Display available operations & get selected operation from user
        printOptions("\nOperations:", OPERATIONS);
        int operationNumber = Integer.parseInt(getInput(scanner, "\nEnter operation number: "));
        
        // Display available request counts & get selected request count from user
        printOptions("\nRequest counts:", REQUEST_COUNTS);
        int requestCountNumber = Integer.parseInt(getInput(scanner, "\nEnter request count number: "));

         // Create & start request threads
        List<RequestThread> requestThreads = createAndStartThreads(REQUEST_COUNTS[requestCountNumber - 1], serverAddress, serverPort, OPERATIONS[operationNumber - 1]);
        
        // Calc & print turnaround times
        calculateAndPrintTurnaroundTime(requestThreads, REQUEST_COUNTS[requestCountNumber - 1]);
    }

    // Method to retrieve user input with a prompt message
    private static String getInput(Scanner scanner, String message) {
        System.out.print(message);
        return scanner.nextLine();
    }

    // Method to print available options for String arrays
    private static void printOptions(String message, String[] options) {
        System.out.println(message);
        for (int i = 0; i < options.length; i++) {
            System.out.printf("%d. %s%n", i + 1, options[i]);
        }
    }

    // Method to print available options for int arrays
    private static void printOptions(String message, int[] options) {
        System.out.println(message);
        for (int i = 0; i < options.length; i++) {
            System.out.printf("%d. %d%n", i + 1, options[i]);
        }
    }

    // Method to create, start and return a list of request threads
    private static List<RequestThread> createAndStartThreads(int count, String serverAddress, int serverPort, String operation) {
        List<RequestThread> requestThreads = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            RequestThread requestThread = new RequestThread(serverAddress, serverPort, operation);
            requestThread.start();
            requestThreads.add(requestThread);
        }
        return requestThreads;
    }
    
    // Method to calcu & print total/average turnaround time
    private static void calculateAndPrintTurnaroundTime(List<RequestThread> requestThreads, int count) {
        long totalTurnaroundTime = requestThreads.stream().mapToLong(RequestThread::getTurnaroundTime).sum();
        double averageTurnaroundTime = (double) totalTurnaroundTime / count;

        System.out.printf("%nTotal turnaround time: %d ms%n", totalTurnaroundTime);
        System.out.printf("Average turnaround time: %.2f ms%n", averageTurnaroundTime);
    }

    // Inner class representing a request thread
    private static class RequestThread extends Thread {
        // Server address, port & operation for this request
        private final String serverAddress;
        private final int serverPort;
        private final String operation;
        private long turnaroundTime;  // Var to hold the turnaround time for the request

         // Constructor
        public RequestThread(String serverAddress, int serverPort, String operation) {
            this.serverAddress = serverAddress;
            this.serverPort = serverPort;
            this.operation = operation;
        }

        // Method to get the turnaround time for the request
        public long getTurnaroundTime() {
            return turnaroundTime;
        }

        // Method that contains the logic executed by the thread
        @Override
        public void run() {
            // Try to establish socket connection & streams
            try (Socket socket = new Socket(serverAddress, serverPort);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

                // Record the start time of the request
                long startTime = System.currentTimeMillis();
               
                // Send operation to the server
                writer.write(operation);
                writer.newLine();
                writer.flush();

                // Receive response from the server
                String response = reader.readLine();

                // Record the end time & calc turnaround time
                long endTime = System.currentTimeMillis();
                turnaroundTime = endTime - startTime;

                // Print the server response & turnaround time
                System.out.printf("Response from server: %s (turnaround time: %d ms)%n", response, turnaroundTime);
            } catch (IOException e) {
                e.printStackTrace(); // Print stack trace if a I/O error occurs
            }
        }
    }
}

               
