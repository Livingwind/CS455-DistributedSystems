This was an exercise to build a P2P network using a ring topology. A large portion of this assignment has to do with constructing and deconstructing network sockets without the use of Java NIO.

### Registry

To start the Registry, simply run `Registry <port>`. The registry accepts a few commands:

##### Commands

`list-messaging-nodes`
  
  Prints to the console every messaging node that has connected to the registry.

`setup-overlay <routing table size>`
  
  Constructs the overlay by organizing a routing table for each node. Naturally, this command must be ran before list-routing-tables and start. Since this uses a raw ring topology, you won't see much performance after you pass a routing table of size 4 due to it being constructed with ln(size).

`list-routing-tables`
  
  Display each node and it's respective routing table
  
`start <number of messages>`
  
  Begin passing messages between nodes. Each message has a destination and a randomly determined integer payload. Once a node receives all of it's messages it sends a completion message to the registry. After waiting a predetermined ammount of time, the registry will print the results of the relay.

### Messaging Nodes
A messaging node can be started by running `MessagingNode <registry hostname> <registry port>`.

##### Commands

`print-counters-and-diagnostics`
  Displays the current count of messages sent and received as well as the running total of all integers received.
  
`exit-overlay`
  Signals the registry that this node will be shutting down then exits. This command is mostly a formality as the registry will automatically drop disconnected nodes from it's namespace. There is no mechanisms to redistribute within the network if a node goes down so make sure only to exit when a job is complete.

Ideally, this program would be ran over a cluster of machines to demonstrate the topology but if you don't have access to a server farm (join the club) multiple instances can be ran on the same computer.

### Explainations of files

node
  MessagingNode.java - main to run an instance of a messenger node, takes the registry hostname and port
  Registry.java - main to run the registry, takes a port number as an argument
  Node.java - parent class of both messaging and registry nodes. contains methods to start and stop all threads

routing
  RegistryEntry.java - stores information that the registry uses such as routing tables and flags relating to running jobs
  RoutingEntry.java - encapsulates routing table entry information
  RoutingTable.java - container to manage routing table entries

transport
  TCPConnection.java - wraps a socket in an easily interfaced class
  TCPConnectionsCache.java - stores current tcp connections and creates/adds new connections as the server accepts them
  TCPReceiverThread.java - pulls messages from the socket
  TCPSenderThread.java - sends messages to a socket
  TCPServerThread.java - checks for incoming connections and queues them for processing

util
  InteractiveCommandParser.java - accepts user commands and places them into a queue to be parsed elsewhere
  StatisticsCollectorAndDisplay.java - contains totals of statistics entries placed into it's list
  StatisticsEntry.java - single unit of information that comes from the traffic summary request
  
wireformats
  Event.java - parent class for all events, parses based on the child class's readBytes and writeBytes methods
  EventFactory.java - singleton to determine a message type and return a full instance of the event
  Protocol.java - interface to store all the message constants
  All other classes are specific implementations of each event
