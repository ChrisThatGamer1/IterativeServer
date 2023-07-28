import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.Scanner;

/*
Project Description: This project requires students to implement a concurrent (multi-threaded) server for use in a client-server configuration.
By: Christopher Clark

*/

public class ConcurrentServer {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int port = scanner.nextInt();
        scanner.nextLine();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client is connected");
                
                Thread thread = new Thread(new RequestHandler(socket));
                thread.start();
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static class RequestHandler implements Runnable {
        private final Socket socket;

        public RequestHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

                int choice = Integer.parseInt(reader.readLine());
                int amount = Integer.parseInt(reader.readLine());
                String response;

                for (int i = 0; i < amount; i++) {
                    switch (choice) {
                        case 1:
                            response = executeCommand("date");
                            break;
                        case 2:
                            response = executeCommand("uptime");
                            break;
                        case 3:
                            response = executeCommand("free -h");
                            break;
                        case 4:
                            response = executeCommand("netstat");
                            break;
                        case 5:
                            response = executeCommand("who");
                            break;
                        case 6:
                            response = executeCommand("ps aux");
                            break;
                        default:
                            response = "Invalid option";
                    }

                    writer.println(response);
                }

                reader.close();
                writer.close();
                socket.close();
            } catch (IOException ex) {
                System.out.println("Server exception: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        private static String executeCommand(String command) {
            try {
                long startTime = System.currentTimeMillis();
                Process process = Runtime.getRuntime().exec(command);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                long endTime = System.currentTimeMillis();
                long turnAroundTime = endTime - startTime;
                System.out.println("Response from Server:" + turnAroundTime);
                reader.close();
                return output.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return "Error executing command: " + e.getMessage();
            }
        }
    }
}
