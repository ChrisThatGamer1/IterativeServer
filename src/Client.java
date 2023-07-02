// import packages for java library 
import java.io.*;
import java.net.*;
import java.util.*;
/*
Project Description: This project requires students to implement an iterative (single-threaded) server for use in a client-server configuration to examine, analyze, and study the effects an iterative server has on the efficiency (average turn-around time) of processing client requests.
By: Christopher Clark

*/

public class Client {
    private static final String[] OPERATIONS = {"date", "uptime", "memory", "netstat", "users", "processes"};
    private static final int[] REQUEST_COUNTS = {1, 5, 10, 15, 20, 25};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Get server address and port
        System.out.print("Enter server address: ");
        String serverAddress = scanner.nextLine();
        System.out.print("Enter server port: ");
        int serverPort = scanner.nextInt();
        scanner.nextLine();

        // Display available operations
        for (int i = 0; i < OPERATIONS.length; i++) {
            System.out.printf("%d. %s%n", i + 1, OPERATIONS[i]);
        }
        System.out.print("\nEnter operation number: ");
        int operationNumber = scanner.nextInt();

        // Display available request counts
        for (int i = 0; i < REQUEST_COUNTS.length; i++) {
            System.out.printf("%d. %d%n", i + 1, REQUEST_COUNTS[i]);
        }
        System.out.print("\nEnter request count number: ");
        int requestCountNumber = scanner.nextInt();

        // Create and start client threads
        List<ClientThread> clientThreads = new ArrayList<>();
        for (int i = 0; i < REQUEST_COUNTS[requestCountNumber - 1]; i++) {
            ClientThread clientThread = new ClientThread(serverAddress, serverPort, OPERATIONS[operationNumber - 1]);
            clientThread.start();
            clientThreads.add(clientThread);
        }

        // Wait for all threads to finish and calculate total and average turnaround time
        long totalTurnaroundTime = 0;
        for (ClientThread clientThread : clientThreads) {
            try {
                clientThread.join();
                totalTurnaroundTime += clientThread.getTurnaroundTime();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        double averageTurnaroundTime = (double) totalTurnaroundTime / REQUEST_COUNTS[requestCountNumber - 1];

        System.out.printf("%nTotal turnaround time: %d ms%n", totalTurnaroundTime);
        System.out.printf("Average turnaround time: %.2f ms%n", averageTurnaroundTime);
    }

    private static class ClientThread extends Thread {
        private final String serverAddress;
        private final int serverPort;
        private final String operation;
        private long turnaroundTime;

        public ClientThread(String serverAddress, int serverPort, String operation) {
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
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Send operation to server
                long startTime = System.currentTimeMillis();
                
                int operationIndex = Arrays.asList(OPERATIONS).indexOf(operation);
                out.println(operationIndex + 1);  // Addin 1 b/c server options start from 1 also hopes to avoid previous server error 


                // Receive response from server
                String response = in.readLine();
                long endTime = System.currentTimeMillis();

                turnaroundTime = endTime - startTime;

                System.out.printf("Response from server: %s (turnaround time: %d ms)%n", response, turnaroundTime);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


