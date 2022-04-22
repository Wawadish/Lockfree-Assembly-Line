import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LockFreeLinkedList implements MyLinkedList {

    private static class Node {

        public AtomicReference<NodeData> data;

        public Node() {
            data = new AtomicReference<>();
        }

    }

    // This is the equivalent of <next, marked> in the lectures slide, is contained an AtomicReference wrapper
    private static class NodeData {

        Node next;
        boolean marked;

        public NodeData(Node pNext, boolean currentNodeMark) {
            next = pNext;
            marked = currentNodeMark;
        }

    }

    private AtomicInteger size = new AtomicInteger(0);
    private volatile Node head = new Node();
    private volatile Node tail = new Node();

    public LockFreeLinkedList() {
        tail.data.set(new NodeData(null, false));
        head.data.set(new NodeData(tail, false));
    }

    // Taken straight from the slides, attempt to add if the prev node is not marked
    private boolean tryAdd(Node n, Node prev) {
        NodeData prevData = prev.data.get();
        if (prevData.marked || prev == tail) return false;

        n.data.set(new NodeData(prevData.next, false));

        return prev.data.compareAndSet(prevData, new NodeData(n, false));
    }

    // Keep trying to add until it succeeds, should only be called by the producer thread.
    @Override
    public void add() {
        Node newNode = new Node();
        while (!tryAdd(newNode, head)) ;
        size.incrementAndGet();
    }

    // Attempt to mark the given node
    private boolean tryRemove(Node n) {
        NodeData nData = n.data.get();
        if (nData.marked) return false;
        Node succ = nData.next;
        return n.data.compareAndSet(nData, new NodeData(succ, true));

    }

    // Traverse the LinkedList and attempt to remove each non-marked nodes, upon failure, simply move to the next node
    // Operation is done in O(c) where c is the maximum capacity of the assemblies.
    @Override
    public boolean removeFirst() {
        Node pred = head;
        NodeData predData = pred.data.get();
        Node current = predData.next;
        NodeData currentData = current.data.get();
        while (current != tail) {
            if (tryRemove(current)) {
                if (!predData.marked &&
                        pred.data.compareAndSet(predData, new NodeData(currentData.next, false))) {
                    size.decrementAndGet();
                }
                return true;
            }
            pred = current;
            predData = pred.data.get();
            current = predData.next;
            currentData = current.data.get();
        }
        return false;
    }


    @Override
    public int size() {
        return size.get();
    }

    // In O(k^k) the method will remove marked nodes from the list. We expect generally the operation to take
    // linear time with respect to k in almost all plausible situations.
    // Will remove most marked nodes and decrease the size of the array. Should be called when the list has reached
    // max capacity. Should only be called by the producer thread. The method is the same as the lecture slides.
    @Override
    public void clean() {
        Node pred = head;
        NodeData predData = pred.data.get();
        Node curr = predData.next;
        NodeData currData = curr.data.get();
        while (curr != tail) {
            Node succ = currData.next;
            while (currData.marked) {
                if (!pred.data.compareAndSet(predData, new NodeData(succ, false))) {
                    clean();
                }
                size.decrementAndGet();
                curr = succ;
                currData = curr.data.get();
                succ = currData.next;
            }
            if (curr == tail) break;
            pred = curr;
            predData = pred.data.get();
            curr = succ;
            currData = curr.data.get();
        }
    }
}
