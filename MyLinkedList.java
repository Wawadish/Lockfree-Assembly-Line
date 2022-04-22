public interface MyLinkedList {
    void add(); // adds a node after the dummy HEAD node, keeps trying until it succeeds
    boolean removeFirst(); // true if a node was successfully removed, false otherwise
    int size();
    void clean(); // removes marked nodes
}
