for i in {1..10}
  do
	gnome-terminal --tab -- /bin/bash -c "java -jar target/io.viro.p2pfs-1.0-SNAPSHOT.jar 127.0.0.${i} 5000${i} node${i} localhost 55555"
	echo "Node ${i} created in new tab"
	sleep 3;
 done
