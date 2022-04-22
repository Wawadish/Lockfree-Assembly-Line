public class BlockingLinkedList implements MyLinkedList{

    private static class Node {
        public volatile Node next;
    }

    // atomicity guaranteed due to the synchronized nature of the operations on this linked list
    private volatile int size;
    private volatile Node head = new Node();
    private volatile Node tail = new Node();

    public BlockingLinkedList(){
        head.next = tail;
    }

    @Override
    public synchronized void add(){
        Node newNode = new Node();
        newNode.next = head.next;
        head.next = newNode;
        size++;
        // notify threads waiting in removeFirst()
        notify();
    }

    @Override
    public synchronized boolean removeFirst(){
        // While loop to handle sudden wake-ups
        while(head.next == tail){
            // Ensures the program closes properly and doesn't leave threads in the monitor
            if(!AbstractAssembly.active){
                notifyAll();
                return false;
            }
            // Sleep if no parts found
            try {wait();} catch (InterruptedException e) {}
        }
        head.next = head.next.next;
        size--;
        return true;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clean() {

    }

}
