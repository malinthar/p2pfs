# p2pfs
A Peer-to-Peer file sharing system

###quick start
Step 1: Clone the repository 

Step 2: Go to project's root directory and use `mvn clean install` to build the project.

Step 3: Start the `BootstrapServer` in a different terminal.

Step 4: Start up several terminals  from the project's root directory and run the following command to start each node providing appropriate arguments.

`java -jar target/io.viro.p2pfs-1.0-SNAPSHOT.jar` `<NodeIP>` `<NodePort>` `<NodeUsername>` `<BootstrapServerIP>` `<BootstrapServerPort>`

eg: `java -jar target/io.viro.p2pfs-1.0-SNAPSHOT.jar 192.168.1.5 55560 node1 localhost 55555`
