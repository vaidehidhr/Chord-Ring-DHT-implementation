package edu.buffalo.cse.cse486586.simpledht;

import java.math.BigInteger;

class Node {
//    BigInteger id;
    String id;
    String port;
    Node Successor;
    Node Predecessor;
//    BigInteger Successor;
//    BigInteger Predecessor;
//    String successor_port;
//    String predecessor_port;

//    Node(BigInteger id, String port) {
    Node(String id, String port) {
        this.id = id;
        this.port = port;
        Successor = null;
        Predecessor = null;
//        successor_port = null;
//        predecessor_port = null;
    }

//    void setSuccessor(BigInteger successor_id, String successor_port){
    void setSuccessor(String successor_id, String successor_port){
        this.Successor.id = successor_id;
        this.Successor.port = successor_port;
    }

//    void setPredecessor(BigInteger predecessor_id, String predecessor_port){
    void setPredecessor(String predecessor_id, String predecessor_port){
        this.Predecessor.id = predecessor_id;
        this.Predecessor.port = predecessor_port;
    }
}