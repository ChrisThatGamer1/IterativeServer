import java.io.*; // import packages for java library 
import java.net.*;
import java.util.*;

/*
Project Description: This project requires students to implement an iterative (single-threaded) server for use in a client-server configuration to examine, analyze, and study the effects an iterative server has on the efficiency (average turn-around time) of processing client requests.
By: Christopher Clark

*/

// start of class IterativeServer
public class IterativeServer {
    // 
    private static final String[] OPERATIONS = {"Date and Time", "Uptime", "Memory Use", "Netstat", "Current Users", "Running Processes"};
    private static final int[] REQUEST_COUNTS = {1, 5, 10, 15, 20, 25};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String serverAddress = getInput(scanner, "Enter server address: ");
        int serverPort = Integer.parseInt(getInput(scanner, "Enter server port: "));
        printOptions("\nOperations:", OPERATIONS);
        int operationNumber = Integer.parseInt(getInput(scanner, "\nEnter operation number: "));
        printOptions("\nRequest counts:", REQUEST_COUNTS);
        int requestCountNumber = Integer.parseInt(getInput(scanner, "\nEnter request count number: "));

        List<RequestThread> requestThreads = createAndStartThreads(REQUEST_COUNTS[requestCountNumber - 1], serverAddress, serverPort, OPERATIONS[operationNumber - 1]);
        calculateAndPrintTurnaroundTime(requestThreads, REQUEST_COUNTS[requestCountNumber - 1]);
    }

    private static String getInput(Scanner scanner, String message) {
        System.out.print(message);
        return scanner.nextLine();
    }

    private static void printOptions(String message, String[] options) {
        System.out.println(message);
        for (int i = 0; i < options.length; i++) {
            System.out.printf("%d. %s%n", i + 1, options[i]);
        }
    }

    private static void printOptions(String message, int[] options) {
        System.out.println(message);
        for (int i = 0; i < options.length; i++) {
            System.out.printf("%d. %d%n", i + 1, options[i]);
        }
    }

    private static List<RequestThread> createAndStartThreads(int count, String serverAddress, int serverPort, String operation) {
        List<RequestThread> requestThreads = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            RequestThread requestThread = new RequestThread(serverAddress, serverPort, operation);
            requestThread.start();
            requestThreads.add(requestThread);
        }
        return requestThreads;
    }

    private static void calculateAndPrintTurnaroundTime(List<RequestThread> requestThreads, int count) {
        long totalTurnaroundTime = requestThreads.stream().mapToLong(RequestThread::getTurnaroundTime).sum();
        double averageTurnaroundTime = (double) totalTurnaroundTime / count;

        System.out.printf("%nTotal turnaround time: %d ms%n", totalTurnaroundTime);
        System.out.printf("Average turnaround time: %.2f ms%n", averageTurnaroundTime);
    }

    private static class RequestThread extends Thread {
        private final String serverAddress;
        private final int serverPort;
        private final String operation;
        private long turnaroundTime;

        public RequestThread(String serverAddress, int serverPort, String operation) {
            this.serverAddress = serverAddress;
            this.serverPort = serverPort;
            this.operation = operation;
        }

        public long getTurnaroundTime() {
            return turnaroundTime;
        }

        @Override
        public void run() {
            try (Socket socket = new Socket(serverAddress, serverPort);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

                long startTime = System.currentTimeMillis();
                
                writer.write(operation);
                writer.newLine();
                writer.flush();

                String response = reader.readLine();

                long endTime = System.currentTimeMillis();
                turnaroundTime = endTime - startTime;

                System.out.printf("Response from server: %s (turnaround time: %d ms)%n", response, turnaroundTime);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

               
