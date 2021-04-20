random1=$(( ( RANDOM % 10 )  + 1 ))
random2=$(( ( RANDOM % 10 )  + 1 ))
random3=$(( ( RANDOM % 10 )  + 1 ))

echo $random1
echo $random2
echo $random3


for i in {1..10}
  do
	gnome-terminal --tab -- /bin/bash -c "java -jar target/io.viro.p2pfs-1.0-SNAPSHOT.jar 127.0.0.${i} 5000${i} node${i} localhost 55555"
	echo "Node ${i} created in new tab"
	sleep 2;
 done
