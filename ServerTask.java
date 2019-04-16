package edu.buffalo.cse.cse486586.simpledht;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import edu.buffalo.cse.cse486586.simpledht.HashSpace;
import edu.buffalo.cse.cse486586.simpledht.Node;

class ServerTask extends AsyncTask<Void, String, Void> {
//class ServerTask extends AsyncTask<ServerSocket, String, Void> {
    static final String REMOTE_PORT[]= {"11108", "11112", "11116", "11120", "11124"};
//static final String REMOTE_PORT[] = {"5554", "5556", "5558", "5560", "5562"};
    HashSpace h;
    ServerSocket serverSocket;
    String myPort;
    Node mynode;
    Uri mUri;
    ContentValues mContentValues;
    ContentResolver cr;
    final String TAG = ServerTask.class.getSimpleName();

    ServerTask(ServerSocket serverSocket, HashSpace h, String myPort, Node mynode, ContentResolver cr){
//    ServerTask(HashSpace h, String myPort, Node mynode){
        this.serverSocket = serverSocket;
        this.h = h;
        this.myPort = myPort;
        this.mynode = mynode;
        this.cr = cr;
//        try {
//            serverSocket.setReuseAddress(true);
//        } catch (SocketException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
//    protected Void doInBackground(ServerSocket... sockets) {
//        ServerSocket serverSocket = sockets[0];
        try {
            while (true) {
                Socket socket_s = serverSocket.accept();
                DataInputStream din_server = new DataInputStream(socket_s.getInputStream());
                String strings= din_server.readUTF();
                if (strings.contains("Node join request")) {
                    strings = strings.replace("Node join request", "");
                    int separator = strings.indexOf(".");
                    String port = strings.substring(0,separator);
                    strings = strings.replace(port,"");
                    String hashed_key = strings.replace(".","");
//                    BigInteger hashed_port = new BigInteger(hashed_key);
//                    SimpleDhtProvider s = new SimpleDhtProvider();
//                    try {
//                        String hashed_key_str = s.genHash(old_key);
//                        BigInteger hashed_key = new BigInteger(hashed_key_str, 16);
//                    Node n = new Node(hashed_port, port);
                    Node n = new Node(hashed_key, port);
                    h.add(n);
                    h.displayHashSpace();
//                    String new_predecessor[] = h.getPredecessor(n);
//                    String new_successor[] = h.getSuccessor(n);
                    String new_predecessor[] = h.getPredecessor_details(n);
                    String new_successor[] = h.getSuccessor_details(n);
//                    if(mynode.Successor == null && mynode.Predecessor == null) {
//                        mynode.Predecessor = hashed_key;
//                        mynode.predecessor_port = old_key;
//                        mynode.Successor = hashed_key;
//                        mynode.successor_port = old_key;
                    DataOutputStream dout_server = new DataOutputStream(socket_s.getOutputStream());
                    String msgtosend = "Node join response: Predecessor = " + new_predecessor[0] + "predecessor_port = " + new_predecessor[1] + "Successor = " + new_successor[0] + "successor_port = " + new_successor[1];
                    dout_server.writeUTF(msgtosend);
//                        new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "Node join request",myPort);

//                        dout_server.writeUTF("Predecessor = "+mynode.id);
//                        dout_server.writeUTF("predecessor_port = "+myPort);
//                        dout_server.writeUTF("Successor = "+mynode.id);
//                        dout_server.writeUTF("successor_port = "+myPort);
//                    }
//            Node n = new Node(hashed_key, old_key);
//                    if(old_key.equals(myPort)){
//                if(hashed_key.compareTo(new BigInteger(")))
//                h.add(n);
//                    }
//                    else {
//                        if (mynode.id.compareTo(hashed_key) == -1 && mynode.Successor.compareTo(hashed_key) == 1)
//                            mynode.Successor = hashed_key;
//                        if (mynode.id.compareTo(hashed_key) == 1 && mynode.Predecessor.compareTo(hashed_key) == -1)
//                            mynode.Predecessor = hashed_key;
//                        new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "Node join",myPort);
//                        publishProgress(strings);
//                    }
//                    } catch (NoSuchAlgorithmException e) {
//                        e.printStackTrace();
//                    }
                }
                if (strings.contains("Node join response")) {
                    strings = strings.replace("Node join response: ","");
                    strings = strings.replace("Predecessor = ","");
                    int separator = strings.indexOf("predecessor_port");
                    String Predecessor = strings.substring(0,separator);
                    strings = strings.replaceFirst(Predecessor,"");
                    strings = strings.replace("predecessor_port = ","");
                    separator = strings.indexOf("Successor");
                    String predecessor_port = strings.substring(0,separator);
                    strings = strings.replaceFirst(predecessor_port,"");
                    strings = strings.replace("Successor = ","");
                    separator = strings.indexOf("successor_port");
                    String Successor = strings.substring(0,separator);
                    strings = strings.replace(Successor, "");
                    strings = strings.replace("successor_port = ","");
                    String successor_port = strings;
//                    mynode.Predecessor = new BigInteger(Predecessor);
//                    mynode.predecessor_port = predecessor_port;
//                    mynode.Successor = new BigInteger(Successor);
//                    mynode.successor_port = successor_port;
//                    mynode.Predecessor.id = new BigInteger(Predecessor);
//                    mynode.Predecessor.port = predecessor_port;
//                    mynode.Successor.id = new BigInteger(Successor);
//                    mynode.Successor.port = successor_port;
//                    Node new_predecessor = new Node(new BigInteger(Predecessor), predecessor_port);
                    Node new_predecessor = new Node(Predecessor, predecessor_port);
                    mynode.Predecessor = new_predecessor;
//                    Node new_Successor = new Node(new BigInteger(Successor), successor_port);
                    Node new_Successor = new Node(Successor, successor_port);
                    mynode.Successor = new_Successor;
                    DataOutputStream dout_server = new DataOutputStream(socket_s.getOutputStream());
                    dout_server.writeUTF("Message received");
//                publishProgress(strings);
//                socket_s.close();
                }
                if (strings.contains("Set successor")){
                    strings = strings.replace("Set successor","");
                    int separator = strings.indexOf(".");
                    String port = strings.substring(0,separator);
                    strings = strings.replace(port,"");
                    String hashed_port = strings.replace(".","");
//                    mynode.Successor = new BigInteger(hashed_port);
//                    mynode.successor_port = hashed_port;

//                    mynode.Successor.id = new BigInteger(hashed_port);
//                    mynode.Successor.port = hashed_port;
//                    Node New_Successor = new Node(new BigInteger(hashed_port),port);
                    Node New_Successor = new Node(hashed_port,port);
                    mynode.Successor = New_Successor;
                    DataOutputStream dout_server = new DataOutputStream(socket_s.getOutputStream());
                    dout_server.writeUTF("Message received");
                }
                if (strings.contains("Set predecessor")){
                    strings = strings.replace("Set predecessor","");
                    int separator = strings.indexOf(".");
                    String port = strings.substring(0,separator);
                    strings = strings.replace(port,"");
                    String hashed_port = strings.replace(".","");
//                    mynode.Predecessor = new BigInteger(hashed_port);
//                    mynode.predecessor_port = hashed_port;
//                    mynode.Predecessor.id = new BigInteger(hashed_port);
//                    mynode.Predecessor.port = hashed_port;
//                    Node New_Predecessor = new Node(new BigInteger(hashed_port), port);
                    Node New_Predecessor = new Node(hashed_port, port);
                    mynode.Predecessor = New_Predecessor;
                    DataOutputStream dout_server = new DataOutputStream(socket_s.getOutputStream());
                    dout_server.writeUTF("Message received");
                }
                if (strings.contains("insert request")){
                    strings = strings.replace("insert request","");
                    int separator = strings.indexOf(";");
                    String request_port = strings.substring(0, separator);
                    strings = strings.replace(request_port,"");
                    strings = strings.replaceFirst(";","");
                    separator = strings.indexOf(";");
                    String uri_str = strings.substring(0,separator);
                    strings = strings.replace(uri_str,"");
                    strings = strings.replaceFirst(";","");
                    separator = strings.indexOf(";");
                    String old_key = strings.substring(0,separator);
                    strings = strings.replace(old_key,"");
                    String msg = strings.replace(";","");
//                    boolean local_storage = h.isSuccessor(new BigInteger(strings), mynode);
//                    if(local_storage) {
                        SimpleDhtProvider s = new SimpleDhtProvider();
//                        Uri uri = Uri.parse(uri_str);
                        mUri = s.buildUri("content", "edu.buffalo.cse.cse486586.simpledht.provider");
                        mContentValues = s.initTestValues(old_key, msg);
                        try {
                            cr.insert(mUri, mContentValues).toString();
                        }
                        catch (NullPointerException e){
                            System.out.println("NullPointerException");
                            Log.e(TAG,"");
                        }
                    DataOutputStream dout_server = new DataOutputStream(socket_s.getOutputStream());
                    dout_server.writeUTF("Message received");
//                    }
//                        ContentValues c = s.initTestValues(old_key, msg);
//                        c.insert
//                        s.insert(uri, );
//                        s.actual_insert(uri,values, new BigInteger(strings),strings);
                    }

                if (strings.contains("query request")) {
                    strings = strings.replace("query request", "");
                    int separator = strings.indexOf(";");
                    String request_port = strings.substring(0, separator);
                    strings = strings.replace(request_port, "");
                    strings = strings.replaceFirst(";", "");
                    separator = strings.indexOf(";");
                    String uri_str = strings.substring(0, separator);
                    strings = strings.replace(uri_str, "");
                    String selection_key = strings.replace(";", "");
//                    separator = strings.indexOf(";");
//                    String old_key = strings.substring(0, separator);
//                    strings = strings.replace(old_key, "");
//                    String msg = strings.replace(";", "");
//                    boolean local_storage = h.isSuccessor(new BigInteger(strings), mynode);
//                    if(local_storage) {
                    SimpleDhtProvider s = new SimpleDhtProvider();
//                        Uri uri = Uri.parse(uri_str);
                    mUri = s.buildUri("content", "edu.buffalo.cse.cse486586.simpledht.provider");
//                    mContentValues = s.initTestValues(old_key, msg);
//                    try {
//                        cr.insert(mUri, mContentValues).toString();
                    Cursor resultCursor = cr.query(mUri, null, selection_key+";"+request_port, null, null);  // Use cursor.getString
//                    int i = resultCursor.getColumnIndex("key");
//                    int j = resultCursor.getColumnIndex("value");

                    DataOutputStream dout_server = new DataOutputStream(socket_s.getOutputStream());
//                    String msgtoseng = resultCursor.getString(i)+resultCursor.getString(j);
//                    int keyIndex = resultCursor.getColumnIndex("key");
//                    int valueIndex = resultCursor.getColumnIndex("value");
                    resultCursor.moveToFirst();
                    // Reference: https://stackoverflow.com/questions/18863816/putting-cursor-data-into-an-array
                    ArrayList<String> keys = new ArrayList<String>();
                    ArrayList<String> values = new ArrayList<String>();
                    while(!resultCursor.isAfterLast()) {
                        keys.add(resultCursor.getString(0));
                        values.add(resultCursor.getString(1));
                        resultCursor.moveToNext();
                    }
                    resultCursor.close();
//                    String msgtosend = "Message received"+keys+";"+values;
                    String msgtosend = "Message received"+keys.toString()+";"+values.toString();

                    dout_server.writeUTF(msgtosend);

                    Log.e(TAG,"Server side: Cursor is trturned to client: key "+keys.toString()+" value "+values.toString()+" from server "+myPort);
//                    } catch (NullPointerException e) {
//                        System.out.println("NullPointerException");
//                            Log.e(TAG,);
//                    }
                }
                if (strings.contains("delete request")) {
                    strings = strings.replace("delete request", "");
                    int separator = strings.indexOf(";");
                    String request_port = strings.substring(0, separator);
                    strings = strings.replace(request_port, "");
                    strings = strings.replaceFirst(";", "");
                    separator = strings.indexOf(";");
                    String uri_str = strings.substring(0, separator);
                    strings = strings.replace(uri_str, "");
                    String selection_key = strings.replace(";", "");

                    SimpleDhtProvider s = new SimpleDhtProvider();
                    mUri = s.buildUri("content", "edu.buffalo.cse.cse486586.simpledht.provider");
//                    mContentValues = s.initTestValues(old_key, msg);
                    try {
                        cr.delete(mUri, selection_key+";"+request_port, null);
                    }
                    catch (NullPointerException e){
                        System.out.println("NullPointerException");
//                            Log.e(TAG,);
                    }
                    DataOutputStream dout_server = new DataOutputStream(socket_s.getOutputStream());
                    dout_server.writeUTF("Message received");
                }
//                    else{
//
//                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    protected void onProgressUpdate(String... strings) {
//        String old_key = strings[0].replace("Node join","");
//        SimpleDhtProvider s = new SimpleDhtProvider();
//        try {
//            String hashed_key_str = s.genHash(old_key);
//            BigInteger hashed_key = new BigInteger(hashed_key_str, 16);
//            if(mynode.Successor == null && mynode.Predecessor == null) {
//                mynode.Successor = hashed_key;
//                mynode.Predecessor = hashed_key;
//            }
////            Node n = new Node(hashed_key, old_key);
//            if(old_key.equals(myPort)){
////                if(hashed_key.compareTo(new BigInteger(")))
////                h.add(n);
//            }
//            else {
//                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "Node join",strings[0]);
//            }
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
    }
}