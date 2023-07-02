import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.Scanner;

public class IterativeServer {

	public static void main(String[] args) {
		//scanner created to get port number from user
		Scanner scanner = new Scanner(System.in);
		int port = scanner.nextInt();
		//socket server created from that port
		try (ServerSocket serverSocket = new ServerSocket(port)) {

			System.out.println("Server is on port " + port);

			while (true) {
				//server connects to the client
				Socket socket = serverSocket.accept();
				System.out.println("Client is connected");

				//reader is formed to output to client
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				//writer made to output the results to client
				PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

				// list of available commands are output to the client
				writer.println("Available options:");
				writer.println("1. Date and Time");
				writer.println("2. Uptime");
				writer.println("3. Memory Use");
				writer.println("4. Netstat");
				writer.println("5. Current Users");
				writer.println("6. Running Processes");
				writer.println("Please enter your choice:");

				//Server reads what the client picks
				int choice = Integer.parseInt(reader.readLine());
				writer.println("Enter how many times command should be run: 1, 5, 10, 15, 20, 25");
				int amount = Integer.parseInt(reader.readLine());
				String response;

				// clients choice is run the amount of times they selected
				for(int i =0; i <= amount; i++) 
				{
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

					// Send the response back to the client
					writer.println(response);

					

				}
				reader.close(); // move the close statements to the end to fix the issue 
				writer.close();
				socket.close();
			}
		} catch (IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	//this method runs the selected command in the console
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