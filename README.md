# NewBank CLI Application

## How to Run

This application consists of a server and client that need to be run separately.

### 1. Start the Server

First, compile and run the server:

```bash
cd newbank/server
javac *.java
cd ../..
java newbank.server.NewBankServer
```

The server will start listening on port 14002.

### 2. Start the Client

In a separate terminal, compile and run the client:

```bash
cd newbank/client
javac *.java
cd ../..
java newbank.client.ExampleClient
```

The client will connect to the server at localhost:14002.

### 3. Using the Application

Once connected, you'll be prompted to log in. After authentication, you can use the following commands:

### Available Commands

A customer enters the command below and sees the messages returned 

`SHOWMYACCOUNTS`
Returns a list of all the customers accounts along with their current balance 
e.g. Main: 1000.0 

`NEWACCOUNT <Name>`
e.g. `NEWACCOUNT Savings`
Returns `SUCCESS` or `FAIL`

`MOVE <Amount> <From> <To>`
e.g. `MOVE 100 Main Savings`
Returns `SUCCESS` or `FAIL`

`PAY <Person/Company> <Ammount>`
e.g. `PAY John 100`
Returns `SUCCESS` or `FAIL`
