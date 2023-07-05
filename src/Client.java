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

        List<Long> turnaroundTimes = new ArrayList<>();

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
        scanner.nextLine();

        // Create and start client threads
        List<ClientThread> clientThreads = new ArrayList<>();
        for (int i = 0; i < REQUEST_COUNTS[requestCountNumber - 1]; i++) {
            ClientThread clientThread = new ClientThread(serverAddress, serverPort, operationNumber, requestCountNumber);
            clientThread.start();
            clientThreads.add(clientThread);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

         

        for (ClientThread clientThread : clientThreads) {
            try {
                clientThread.join();
                long turnaroundTime = clientThread.getTurnaroundTime();
                turnaroundTimes.add(turnaroundTime);
                long totalTurnaroundTime = turnaroundTime;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
}
    

    class ClientThread extends Thread {
        private final String serverAddress;
        private final int serverPort;
        private final int operationNumber;
        private final int requestCountNumber;
        private long turnaroundTime;

        public ClientThread(String serverAddress, int serverPort, int operationNumber, int requestCountNumber) {
            this.serverAddress = serverAddress;
            this.serverPort = serverPort;
            this.operationNumber = operationNumber;
            this.requestCountNumber = requestCountNumber;
        }

        public long getTurnaroundTime() {
            return turnaroundTime;
        }

        @Override
        public void run() {
            try (Socket socket = new Socket(serverAddress, serverPort);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Send operation number and request count number as separate values
                out.println(operationNumber);
                out.println(requestCountNumber);

                long startTime = System.currentTimeMillis();

                String response;
                while ((response = in.readLine()) != null) {
                    System.out.println(response);
                }

                long endTime = System.currentTimeMillis();
                turnaroundTime = endTime - startTime;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

 

