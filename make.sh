#random1=$(( ( RANDOM % 1 )  + 2 ))
#random2=$(( ( RANDOM % 2 )  + 4 ))
#random3=$(( ( RANDOM % 3 )  + 7 ))

random1=2
random2=6
random3=11

echo $random1
echo $random2
echo $random3


for i in {1..12}
  do
	if [[ $i -eq $random1 ]] || [[ $i -eq $random2 ]] || [[ $i -eq $random3 ]]
	then
  		status="true"
	else
		status="false"
	fi

	echo "java -jar target/io.viro.p2pfs-1.0-SNAPSHOT.jar 127.0.0.${i} 500${i} node${i} localhost 55555 ${status}"

	gnome-terminal --tab -- /bin/bash -c "java -jar target/io.viro.p2pfs-1.0-SNAPSHOT.jar 127.0.0.${i} 500${i} node${i} localhost 55555 ${status}"
	echo "Node ${i} created in new tab"
	sleep 1;
 done
