# Lockfree-Assembly-Line

The assembly is comprised of reservoirs, intermediary assemly nodes and a root node.</br>

The reservoirs are assigned a random periodicity which dictates how fast they create objects.</br>

Intermediary nodes may consume items from reservoirs or other intermediary nodes to create an item. Similarly for the root node.</br>

Once the root node created the target amount of items, the simulation ends.</br></br>

p = periodicity each assembly node is randomly assigned a periodicity between [1, p]   1 < p < 30</br>   
c = assembly nodes maximum capacity (how many items they can hold)    c >= 1</br>
k = target amount of items    k > 1000</br></br>

./Q1a p c k    launches the program with a blocking linked list</br>
./Q1b p c k    launches the program with a lockfree linked list</br>
