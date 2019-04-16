package edu.buffalo.cse.cse486586.simpledht;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import static edu.buffalo.cse.cse486586.simpledht.SimpleDhtProvider.TAG;

class ClientTask extends AsyncTask<String, Void, Void> {
    static final String REMOTE_PORT[]= {"11108", "11112", "11116", "11120", "11124"};
//    static final String REMOTE_PORT[] = {"5554", "5556", "5558", "5560", "5562"};
    @Override
    protected Void doInBackground(String... msgs) {
        String strings = "";
        if(msgs[0].contains("Node join request")) {
            try {
                // Reference: https://stackoverflow.com/questions/14777391/making-a-connection-with-socket-connect-using-timeout/14779241#14779241
                Socket socket = new Socket();
//                socket.setSoTimeout(500);
                socket.connect(new InetSocketAddress(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(REMOTE_PORT[0])), 500);
//                Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(REMOTE_PORT[0]));
                String msgToSend = msgs[0] + msgs[1] +"."+ msgs[2];
                DataOutputStream dout_client = new DataOutputStream(socket.getOutputStream());
                dout_client.writeUTF(msgToSend);
                try {
//                    socket.setSoTimeout(500);
                    DataInputStream din_client = new DataInputStream(socket.getInputStream());
                    strings = din_client.readUTF();
                    if (strings.contains("Node join response")) {
                        dout_client.flush();
                        socket.close();
//                        return true;
                    }
                } catch (SocketTimeoutException e) {
                    Log.e(TAG, "Socket time out exception");
                    return null;
                }
            } catch (UnknownHostException e) {
                Log.e(TAG, "ClientTask UnknownHostException");
//                return false;
            } catch (IOException e) {
                Log.e(TAG, "ClientTask socket IOException");
                return null;
//                return false;
//                Socket socket = null;
//                try {
//                    socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(msgs[1]));
//                    String msgToSend = msgs[0]+msgs[1];
//                    DataOutputStream dout_client = new DataOutputStream(socket.getOutputStream());
//                    dout_client.writeUTF(msgToSend);
//                    DataInputStream din_client = new DataInputStream(socket.getInputStream());
//                    if (din_client.readUTF().equals("Message received")) {
//                        dout_client.flush();
//                        socket.close();
//                    }
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
            }
            try {
                Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(msgs[1]));
                String msgToSend = strings;
                DataOutputStream dout_client = new DataOutputStream(socket.getOutputStream());
                dout_client.writeUTF(msgToSend);
                DataInputStream din_client = new DataInputStream(socket.getInputStream());
                if (din_client.readUTF().contains("Message received")) {
                    dout_client.flush();
                    socket.close();
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
//                if (din_client.readUTF().contains("Node join response")) {
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

//                }
            if(!predecessor_port.equals("11108")) {
//            if(!predecessor_port.equals("5554")) {
                try {
                    Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(predecessor_port));
                    String msgToSend = "Set successor" + msgs[1] + "." + msgs[2];
                    DataOutputStream dout_client = new DataOutputStream(socket.getOutputStream());
                    dout_client.writeUTF(msgToSend);
                    DataInputStream din_client = new DataInputStream(socket.getInputStream());
                    if (din_client.readUTF().contains("Message received")) {
                        dout_client.flush();
                        socket.close();
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(!successor_port.equals("11108")) {
//            if(!successor_port.equals("5554")) {
                try {
                    Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(successor_port));
                    String msgToSend = "Set predecessor" + msgs[1] + "." + msgs[2];
                    DataOutputStream dout_client = new DataOutputStream(socket.getOutputStream());
                    dout_client.writeUTF(msgToSend);
                    DataInputStream din_client = new DataInputStream(socket.getInputStream());
                    if (din_client.readUTF().contains("Message received")) {
                        dout_client.flush();
                        socket.close();
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if(msgs[0].contains("insert request")) {
            String next_node = msgs[2];
            try {
                String request_port = msgs[1];
                Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(next_node));
                String msgToSend = msgs[0]+msgs[1]+";"+msgs[3]+";"+msgs[4]+";"+msgs[5];
                DataOutputStream dout_client = new DataOutputStream(socket.getOutputStream());
                dout_client.writeUTF(msgToSend);
                DataInputStream din_client = new DataInputStream(socket.getInputStream());
                String response = din_client.readUTF();
//                if (response.equals("Message received")) {
//                    Log.e(TAG,"Query inserted successfully");
                    dout_client.flush();
                    socket.close();
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(msgs[0].contains("query request")) {
            String next_node = msgs[2];
            try {
                String request_port = msgs[1];
                Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(next_node));
                String msgToSend = msgs[0]+msgs[1]+";"+msgs[3]+";"+msgs[4];
                Log.e(TAG,"Query: Client side: request sent: "+msgToSend);
                DataOutputStream dout_client = new DataOutputStream(socket.getOutputStream());
                dout_client.writeUTF(msgToSend);
                DataInputStream din_client = new DataInputStream(socket.getInputStream());
                String response = din_client.readUTF();
                if (response.contains("Message received")) {
//                    Log.e(TAG,"Query inserted successfully");
                dout_client.flush();
                socket.close();
                response = response.replace("Message received", "");
                Log.e(TAG,"Query: Client side: Response received at client side "+response+" from server "+next_node);
                SimpleDhtProvider s = new SimpleDhtProvider();
                s.setResponseCursor(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else if(msgs[0].contains("delete request")) {
            String next_node = msgs[2];
            try {
                String request_port = msgs[1];
                Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(next_node));
                String msgToSend = msgs[0]+msgs[1]+";"+msgs[3]+";"+msgs[4];
                DataOutputStream dout_client = new DataOutputStream(socket.getOutputStream());
                dout_client.writeUTF(msgToSend);
                DataInputStream din_client = new DataInputStream(socket.getInputStream());
                String response = din_client.readUTF();
                if (response.equals("Message received")) {
//                    Log.e(TAG,"Query inserted successfully");
                    dout_client.flush();
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        else {
//            for (int i = 0; i < 5; i++) {
//                try {
//                    Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(REMOTE_PORT[i]));
//                    String msgToSend = msgs[0];
//
//                     For this section code from PA1 has been referred
//                    DataOutputStream dout_client = new DataOutputStream(socket.getOutputStream());
//                    dout_client.writeUTF(msgToSend);
//
//                    DataInputStream din_client = new DataInputStream(socket.getInputStream());
//                    if (din_client.readUTF().equals("Message received")) {
//                        dout_client.flush();
//                        socket.close();
//                        return true;
//                    }

//                } catch (UnknownHostException e) {
//                    Log.e(TAG, "ClientTask UnknownHostException");
//                    return false;
//                } catch (IOException e) {
//                    Log.e(TAG, "ClientTask socket IOException");
//                    return false;
//                }
//            }
//        }
        return null;
    }
}