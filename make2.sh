for i in {10..12}
  do
	echo "java -jar target/io.viro.p2pfs-1.0-SNAPSHOT.jar 127.0.0.${i} 5000${i} node${i} localhost 55555 false"

	gnome-terminal --tab -- /bin/bash -c "java -jar target/io.viro.p2pfs-1.0-SNAPSHOT.jar 127.0.0.${i} 5000${i} node${i} localhost 55555 false"
	echo "Node ${i} created in new tab"
 done
