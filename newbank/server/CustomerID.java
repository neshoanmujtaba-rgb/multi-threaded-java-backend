// Define the package for this class
package newbank.server;

/**
 * CustomerID class represents a unique customer identifier token
 * This is used to identify authenticated customers in the banking system
 * Provides a secure way to pass customer identity information without exposing passwords
 */
public class CustomerID {
	// Private String variable to store the unique customer key (typically the username)
	private String key;
	
	/**
	 * Constructor: Creates a new CustomerID with the given key
	 * @param key - The unique customer identifier (usually the username)
	 */
	public CustomerID(String key) {
		// Assign the parameter key to this object's key field
		this.key = key;
	}
	
	/**
	 * Getter method: Returns the customer's unique key
	 * @return String - The customer's identifier/username
	 */
	public String getKey() {
		// Return the stored customer key
		return key;
	}
}
