/* *****************************************************************************
 *  @author Serhii Yeshchenko
 *  (based on R. Sedgewick, K. Wayne from Coursera, ALGORITHM Part I)
 *  Date: Sep'2020
 *  Description: BST.java represents a binary tree in symmetric order.
 *               A binary tree is either:
 *                                       - Empty
 *                                       - Two disjoint binary trees (left and right).
 *               Symmetric order: each node has a key, and every node’s key is:
 *                                - Larger than all keys in its left subtree
 *                                - Smaller than all keys in its right subtree
 *
 *  Performance: search, insert, min/max, floor/ceiling, rank, select
 *               - h = height of BST (proportional to log N if keys inserted in random order);
 *               ordered iteration - N;
 *               isBST() - determines whether a given tree is a binary search tree,
 *                         uses space proportion to the height h of the tree.
 *               keys() -  perform an in-order traversal of a binary search tree
 *                         using only a constant amount of extra space (i.e.
 *                         without function call stack used by recursion)
 *  Data files:   BST_input.txt
 *  Improvement:
 *
 *  <p>
 *  Coursera, ALGORITHM Part I, week 4, Interview question 2 (Elementary Symbol Tables )
 **************************************************************************** */

package com.uzviz;

import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class BST<Key extends Comparable<Key>, Value> {
    private Node root;  // root of BST

    private class Node {
        private Key key;
        private Value val;
        private Node left, right; // A reference to the left and right subtree
        private Node parent;      // A reference to the parent node
        private int count;        // number of nodes in subtree

        public Node (Key key, Value val) {
            this.key = key;
            this.val = val;
        }
    }
    /* Associate value with key                               */
    /* Cost: Number of compares is equal to 1 + depth of node */
    public void put(Key key, Value val) {
        root = put(root, key, val);
    }
    private Node put(Node x, Key key, Value val) {
        if (x == null) return new Node(key, val);
        int cmp = key.compareTo(x.key);

        if      (cmp < 0)  x.left = put(x.left, key, val);
        else if (cmp > 0)  x.right = put(x.right, key, val);
        else if (cmp == 0) x.val = val;

        // In each node, we store the number of nodes in the subtree rooted at that node;
        // This facilitates efficient implementation of rank() and select().
        x.count = 1 + size(x.left) + size(x.right);

        return x;
    }

    public boolean contains(Key key) {
        return (get(key) != null);
    }

    /* Return value corresponding to given key, or null if no such key. */
    /* Cost: Number of compares is equal to 1 + depth of node.          */
    public Value get(Key key) {
        Node x = root;
        while (x != null) {
            int cmp = key.compareTo(x.key);
            if      (cmp < 0)  x = x.left;
            else if (cmp > 0)  x = x.right;
            else if (cmp == 0) return x.val;
        }
        return null;
    }

    /* Computing the floor - largest key <= a given key */
    public Key floor(Key key) {
        Node x = floor(root, key);
        if (x == null) return null;
        return x.key;
    }
    private Node floor(Node x, Key key) {
        if (x == null) return null;
        int cmp = key.compareTo(x.key);

        /* The floor of k is k */
        if (cmp == 0) return x;

        /* The floor of k is in the left subtree */
        if (cmp < 0) floor(x.left, key);

        /* The floor of k is in the right subtree      */
        /* (if there is any key ≤ k in right subtree); */
        /* otherwise it is the key in the root.        */
        Node t = floor(x.right, key);
        if (t != null) return t;
        else           return x;
    }

    /* Computing the ceiling - smallest key >= a given key */
    public Key ceiling(Key key) {
        Node x = ceiling(root, key);
        if (x == null) return null;
        return x.key;
    }
    private Node ceiling(Node x, Key key) {
        if (x == null) return null;
        int cmp = key.compareTo(x.key);

        /* The ceiling of k is k */
        if (cmp == 0) return x;

        /* The ceiling of k is in the right subtree */
        if (cmp > 0) ceiling(x.right, key);

        Node t = ceiling(x.left, key);
        if (t != null) return t;
        else           return x;
    }

    public void delete(Key key) {
        root = delete(root, key);
    }
    private Node delete(Node x, Key key) {
        if (x == null) return null;
        int cmp = key.compareTo(x.key);

        if      (cmp < 0) x.left = delete(x.left, key);
        else if (cmp > 0) x.right = delete(x.right, key);
        else {
            if (x.right == null) return x.left;
            if (x.left == null) return x.right;

            Node t = x;

            // the code from lecture slides: x = min(t.right),- but 'min' is actually 'ceiling' ;
            x = ceiling(t.right, t.key);

            x.right = deleteMin(t.right);
            x.left = t.left;
        }
        x.count = size(x.left) + size(x.right) + 1;
        return x;
    }

    /* Deleting the minimum */
    public void deleteMin() {
        root = deleteMin(root);
    }
    /* Go left until finding a node with a null left link */
    /* Replace that node by its right link                */
    /* Update subtree counts                              */
    private Node deleteMin(Node x) {
        if (x.left == null) return x.right;
        x.left = deleteMin(x.left);
        x.count = 1 + size(x.left) + size(x.right);
        return x;
    }
    /* Deleting the maximum */
    public void deleteMax() {
        root = deleteMax(root);
    }
    /* Go right until finding a node with a null right link */
    /* Replace that node by its left link                   */
    /* Update subtree counts                                */
    private Node deleteMax(Node x) {
        if (x.right == null) return x.left;
        x.right = deleteMax(x.right);
        x.count = 1 + size(x.left) + size(x.right);
        return x;
    }

    /* counts number of nodes in subtree*/
    public int size() {
        return size(root);
    }
    private int size(Node x) {
        if (x == null) return 0;
        return x.count;
    }

    /* Calculates how many keys < k */
    public int rank(Key key) {
        return rank(key, root);
    }
    private int rank(Key key, Node x) {
        if (x == null) return 0;
        int cmp = key.compareTo(x.key);
        if      (cmp < 0) return rank(key, x.left);
        else if (cmp > 0) return 1 + size(x.left) + rank(key, x.right);
        else return size(x.left); // if (cmp == 0)
    }

    /*
     * Check if a binary tree is a BST. Given a binary tree where each
     * Node contains a key, determine whether it is a binary search tree.
     */
    public boolean isBST() {
       return isBST(root, null, null);
    }

    // is the tree rooted at x a BST with all keys strictly between min and max
    // Credit: Bob Dondero's elegant solution
    private boolean isBST(Node x, Key keyMin, Key keyMax) {
        if (x == null) return true;
        if (keyMin != null && keyMin.compareTo(x.key) > 0) return false;
        if (keyMax != null && keyMax.compareTo(x.key) < 0) return false;

        return isBST(x.left, keyMin, x.key) &&
                                                isBST(x.right, x.key, keyMax);
    }

    /* Inorder traversal of a BST yields keys in ascending order */
    public Iterable<Key> iterator() {
        Queue<Key> q = new Queue<Key>();
        inorder(root, q);
        return q;
    }
    /* Traverse left subtree - Enqueue key - Traverse right subtree */
    private void inorder(Node x, Queue<Key> q) {
        if (x == null) return;
        inorder(x.left, q);
        q.enqueue(x.key);
        inorder(x.right, q);
    }

    // print all the keys in a BST in reverse order
    public void printReverse() {
        printReverse(root);
    }
    private void printReverse(Node x) {
        if (x == null) return;
        printReverse(x.right);
        StdOut.println(x.key);
        printReverse(x.left);
    }

    /* Inorder traversal of a BST yields keys in ascending order */
    /* using only a constant amount of extra space               */
    /* (i.e. without  recursion)                                 */
    public Iterable<Key> keys() {
        Queue<Key> q = new Queue<Key>();
        keys(root, q);
        return q;
    }
    /*
    * 1. If the previous node is this node’s parent node, descend to the left child node.
	* 2. If the previous node is this node’s left child node, descend to the right child node.
	* 3. If the previous node is this node’s right child node, ascend to the parent node.
    *  If there is no left child to descend to, traverse to the right one; and if there
    *  is no right child to descend to,- ascend to the parent.
    *  Traversal is complete when an attempt to ascend to the parent node fails
    *  because there is no parent.
    *
    * Credit: by Aristotle from https://www.perlmonks.org/?node_id=600456
    */
    private void keys(Node currNode, Queue<Key> q) {
        if (currNode == null) return;

        Node prevNode = currNode;

        while (currNode != null) {
            Node nextNode = null;

            // prevNode == currNode - to start traversing from the root node
            if (prevNode == currNode.parent || prevNode == currNode) {
                if (currNode.left == null && currNode.right == null) {
                    nextNode = currNode.parent; // move up the tree
                    q.enqueue(currNode.key);
                    currNode.parent = null;   // to reverse the pointer (return to BST original form)
                }
                else if (currNode.left == null) {
                    nextNode = currNode.right;
                    q.enqueue(currNode.key);
                    nextNode.parent = currNode; // make the child node point back to the parent
                }
                else {
                    nextNode = currNode.left;
                    nextNode.parent = currNode;  // make the child node point back to the parent
                }
            }
            else if (prevNode == currNode.left) {
                if (currNode.right == null) {
                    nextNode = currNode.parent;  // move up the tree
                    currNode.parent = null;      // to reverse the pointer (return to BST original form)
                } else {
                    nextNode = currNode.right;
                    nextNode.parent = currNode;  // make the child node point back to the parent
                }
                q.enqueue(currNode.key);
            } else if (prevNode == currNode.right) {
                nextNode = currNode.parent;  // move up the tree
                currNode.parent = null;   // to reverse the pointer (return to BST original form)
            }

            prevNode = currNode;
            currNode = nextNode;
        }
    }

    /**
     * Unit tests the {@code BST} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        BST<String, Integer> bst = new BST<>();

        for (int i = 0; !StdIn.isEmpty(); i++) {
            String key = StdIn.readString();
            bst.put(key, i);
        }
        for (String s : bst.iterator()) {
            StdOut.println(s + " " + bst.get(s));
        }
        StdOut.println("Is this tree a BST? - " + bst.isBST());
        StdOut.println("Print BST in reverse order:");
        bst.printReverse();
        StdOut.println();
        StdOut.println("Print BST via in-order traversal with constant extra space (without recursion)");
        for (String s : bst.keys()) {
            StdOut.println(s + " " + bst.get(s));
        }
    }
}
