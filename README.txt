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
