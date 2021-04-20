random1=$(( ( RANDOM % 10 )  + 1 ))
random2=$(( ( RANDOM % 10 )  + 1 ))
random3=$(( ( RANDOM % 10 )  + 1 ))

echo $random1
echo $random2
echo $random3


for i in {1..10}
  do
	if [[ $i -eq $random1 ]] || [[ $i -eq $random2 ]] || [[ $i -eq $random3 ]]
	then
  		status="true"
	else
		status="false"
	fi

	echo "java -jar target/io.viro.p2pfs-1.0-SNAPSHOT.jar 127.0.0.${i} 5000${i} node${i} localhost 55555 ${status}"

	gnome-terminal --tab -- /bin/bash -c "java -jar target/io.viro.p2pfs-1.0-SNAPSHOT.jar 127.0.0.${i} 5000${i} node${i} localhost 55555 ${status}"
	echo "Node ${i} created in new tab"
	sleep 2;
 done
