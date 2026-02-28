// Define the package for this class - part of the banking client
package newbank.client;

// Import BufferedReader for reading input efficiently in lines
import java.io.BufferedReader;
// Import IOException for handling input/output errors
import java.io.IOException;
// Import InputStreamReader for converting bytes to characters from input stream
import java.io.InputStreamReader;
// Import PrintWriter for printing data to output stream
import java.io.PrintWriter;
// Import Socket for network communication with the server
import java.net.Socket;
// Import UnknownHostException for when hostname cannot be resolved
import java.net.UnknownHostException;

/**
 * ExampleClient class represents a client that connects to the NewBankServer
 * Extends Thread because client runs in its own thread
 * Handles bidirectional communication: sends commands to server and receives responses
 */
public class ExampleClient extends Thread{
	
	// Private Socket object for network connection to the remote banking server
	private Socket server;
	// Private PrintWriter to send data/commands to the server through the socket
	private PrintWriter bankServerOut;	
	// Private BufferedReader to read user input from keyboard console
	private BufferedReader userInput;
	// Private Thread object reference for the separate thread that listens for server responses
	private Thread bankServerResponceThread;
	
	/**
	 * Constructor: Establishes connection to the banking server and sets up communication
	 * Initializes input/output streams and starts a separate thread to listen for server responses
	 * @param ip - The IP address or hostname of the server (e.g., "localhost" or "192.168.1.1")
	 * @param port - The port number the server is listening on (e.g., 14002)
	 * @throws UnknownHostException - If the IP address/hostname cannot be resolved
	 * @throws IOException - If connection fails or I/O setup fails
	 */
	public ExampleClient(String ip, int port) throws UnknownHostException, IOException {
		// Create a new Socket object connecting to the specified IP and port on the server
		server = new Socket(ip,port);
		// Create BufferedReader to read user input from standard input (keyboard)
		// InputStreamReader converts System.in bytes to characters for BufferedReader to read
		userInput = new BufferedReader(new InputStreamReader(System.in)); 
		// Create PrintWriter to send data to server through socket's output stream
		// true parameter means auto-flush (automatically send data without waiting)
		bankServerOut = new PrintWriter(server.getOutputStream(), true); 
		
		// Create anonymous inner class that extends Thread to listen for server responses separately
		bankServerResponceThread = new Thread() {
			// Create BufferedReader inside the thread to read responses from the server
			// This is inside the thread so each thread instance gets its own reader
			private BufferedReader bankServerIn = new BufferedReader(new InputStreamReader(server.getInputStream()));
			
			/**
			 * Run method: Executed when the thread is started
			 * Continuously reads lines from server and prints them to console
			 */
			public void run() {
				// Use try-catch to handle any IOExceptions that might occur
				try {
					// Infinite loop to continuously receive and display server messages
					while(true) {
						// Read one line from the server's response
						String responce = bankServerIn.readLine();
						// Check if we received null (which means server closed connection)
						if(responce == null) {
							// Break out of loop if server closed connection
							break;
						}
						// Print the server's response message to the console for user to see
						System.out.println(responce);
					}
				} catch (IOException e) {
					// If an IOException occurred, print the stack trace for debugging
					e.printStackTrace();
					// Return from this thread (end thread execution)
					return;
				}
			}
		};
		// Start the bankServerResponceThread so it begins listening for server responses
		bankServerResponceThread.start();
	}
	
	/**
	 * Run method: Executed when the ExampleClient thread is started
	 * Continuously reads user commands from keyboard and sends them to the server
	 * This runs in parallel with bankServerResponceThread which listens for responses
	 */
	public void run() {
		// Infinite loop to continuously process user commands
		while(true) {
			// Use try-catch to handle any IOExceptions that might occur
			try {
				// Inner infinite loop for reading user commands
				while(true) {
					// Read a command line that the user typed in from the keyboard
					String command = userInput.readLine();
					// Send the user's command to the server through the PrintWriter
					bankServerOut.println(command);
				}				
			} catch (IOException e) {
				// TODO Auto-generated catch block - placeholder for error handling
				// If an IOException occurred, print the stack trace for debugging
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Main method: Entry point for running the client program
	 * Creates an instance of ExampleClient connected to localhost port 14002
	 * @param args - Command line arguments (not used in this implementation)
	 * @throws UnknownHostException - If "localhost" cannot be resolved
	 * @throws IOException - If connection to server fails
	 * @throws InterruptedException - If thread interruption occurs (rare in this case)
	 */
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		// Create a new ExampleClient instance connecting to "localhost" (same machine) on port 14002
		// ExampleClient constructor establishes connection and starts communication threads
		new ExampleClient("localhost",14002).start();
	}
}
