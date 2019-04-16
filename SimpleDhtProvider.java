package edu.buffalo.cse.cse486586.simpledht;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;

import static edu.buffalo.cse.cse486586.simpledht.SimpleDhtProvider.TAG;

public class SimpleDhtProvider extends ContentProvider {

    static final String TAG = SimpleDhtProvider.class.getSimpleName();
    String myPort;
    HashSpace h;
    Node mynode;
    static final int SERVER_PORT = 10000;
    static final String REMOTE_PORT[] = {"11108", "11112", "11116", "11120", "11124"};
//    static final String REMOTE_PORT[] = {"5554", "5556", "5558", "5560", "5562"};
    private static final String KEY_FIELD = "key";
    private static final String VALUE_FIELD = "value";
    String columnnames[] = new String[] {"key","value"};
    static MatrixCursor responseCursor= null;
    static boolean set = false;
//    static Cursor responsecursor = null;
//    MatrixCursor responsecursor = new MatrixCursor(columnnames);

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        Log.e(TAG, "SimpleDHTprovider delete");
        String request_port = "";
//        try {
        int separator = selection.indexOf(";");
        if (separator != -1) {
            request_port = selection.substring((separator + 1));
            selection = selection.replace(request_port, "");
            selection = selection.replace(";", "");
        }
        String file_list[]=getContext().fileList();
        int i = 0;
        if(selection.equals("@")||(selection.equals("*") && mynode.Predecessor.port.equals(mynode.port))) {
            Log.e(TAG, "delete for @/ delete for local");
            while (i < file_list.length) {
                Context context = getContext();
                boolean success = context.deleteFile(file_list[i]);
                i++;
            }
        }
        else if (selection.equals("*")){
            Log.e(TAG, "Check for * on global storage");
            while (i < file_list.length) {
                Context context = getContext();
                boolean success = context.deleteFile(file_list[i]);
                i++;
            }
            if(!mynode.Successor.port.equals(request_port)) {
                if(request_port.equals(""))
                    new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "delete request", myPort, mynode.Successor.port, uri.toString(), selection);
                else
                    new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "delete request", request_port, mynode.Successor.port, uri.toString(), selection);
            }
        }
        else {
            try {
                String hashed_selection_str = genHash(selection);
//                BigInteger hashed_selection = new BigInteger(hashed_selection_str, 16);
//                boolean local_storage = isSuccessor(hashed_selection, mynode);
                boolean local_storage = isSuccessor(hashed_selection_str, mynode);
                if (local_storage) {
                    while (i < file_list.length) {
                        if (file_list[i].equals(hashed_selection_str.toString())) {
                            Context context = getContext();
                            boolean success = context.deleteFile(file_list[i]);
                            break;
//                        FileInputStream temp;
//                        Context context = getContext();
//                        try {
//                            temp = context.openFileInput(file_list[i]);
////                            StringBuffer fileContent = new StringBuffer("");
////                            byte[] buffer = new byte[1024];
////                            int n;
////                            while ((n = temp.read(buffer)) != -1) {
////                                fileContent.append(new String(buffer, 0, n));
////                            }
////                            int separator_index = fileContent.indexOf(".");
////                            String old_key = fileContent.substring(0, separator_index);
////                            String msg = fileContent.substring(separator_index + 1);
////                            cursor.addRow(new Object[]{old_key, msg});
//                            break;
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        } else
                            i++;
                    }
                } else {
                    new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "delete request", myPort, mynode.Successor.port, uri.toString(), selection);
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        Log.e(TAG, "SimpleDHTprovider getType");
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
        Log.e(TAG, "SimpleDHTprovider insert");
        // My code
        String arg = values.toString();
        String temp[] = arg.split("=");
        String old_key = temp[2];
        try {
            String hashed_key_str = genHash(old_key);
//            BigInteger hashed_key = new BigInteger(hashed_key_str, 16);
            String msg = temp[1].replace(" key", "");
            String filecontents = old_key + "." + msg;
//            boolean local_storage = isSuccessor(hashed_key, mynode);
            boolean local_storage = isSuccessor(hashed_key_str, mynode);
            if (local_storage) {
//            if (myPort.equals("11108")){
//                Log.e(TAG,"Hashed key "+hashed_key.toString()+" inserted to node "+mynode.id.toString());
                Log.e(TAG,"Hashed key "+hashed_key_str+", key "+old_key+" inserted to node "+mynode.id.toString());
                FileOutputStream outputStream;
                Context context = getContext();
                try {
//                    outputStream = context.openFileOutput(hashed_key.toString(), Context.MODE_PRIVATE);      //hashed_key should be BigInteger or String
                    outputStream = context.openFileOutput(hashed_key_str, Context.MODE_PRIVATE);      //hashed_key should be BigInteger or String
                    outputStream.write(filecontents.getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    Log.e(TAG, "File write failed");
                }
                Log.v("insert", values.toString());
                return uri;
//                actual_insert(uri,values, hashed_key,filecontents);
            } else {
                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "insert request", myPort, mynode.Successor.port, uri.toString(), old_key, msg);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;    // for noSuchAlgorithmException return null, else return uri
        // My code ends
        //return null;
    }

//    public Uri actual_insert(Uri uri, ContentValues values, BigInteger hashed_key, String filecontents){
//        FileOutputStream outputStream;
//        Context context = getContext();
//        try {
//            outputStream = context.openFileOutput(hashed_key.toString(), Context.MODE_PRIVATE);      //hashed_key should be BigInteger or String
//            outputStream.write(filecontents.getBytes());
//            outputStream.close();
//        } catch (Exception e) {
//            Log.e(TAG, "File write failed");
//        }
//        Log.v("insert", values.toString());
//        return uri;
//    }

    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub
//        Log.e(TAG, "SimpleDHTprovider onCreate");
        Context context = getContext();
        TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        myPort = String.valueOf((Integer.parseInt(portStr) * 2));
//        myPort = portStr;
//        ContentResolver mContentResolver = getContentResolver();
//        Context context = getContext();
        ContentResolver cr = (ContentResolver)context.getContentResolver();
//        for(int i = 0; i < REMOTE_PORT.length; i++) {
        h = new HashSpace();
        String hashed_port_str = null;
        try {
//            hashed_port_str = genHash(myPort);
            hashed_port_str = genHash(portStr);
//            BigInteger hashed_port = new BigInteger(hashed_port_str, 16);
//                Log.e(TAG, "myport: "+myPort+" :hashed port for " + REMOTE_PORT[i] + " in oncreate is" + hashed_port.toString());
//            mynode = new Node(hashed_port, myPort);
            mynode = new Node(hashed_port_str, myPort);
//            if(myPort.equals("11108"))
                h.add(mynode);
            try {
                ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
                new ServerTask(serverSocket, h, myPort, mynode, cr).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                new ServerTask(h, myPort, mynode).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
            } catch (android.os.NetworkOnMainThreadException e) {
                Log.e(TAG, "Can't create a ServerSocket");
//            return;
            } catch (IOException e) {
                e.printStackTrace();
            }
//            AsyncTask c = new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "Node join", REMOTE_PORT[0]);
            if(!myPort.equals("11108"))
//            if(!myPort.equals("5554"))
//                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "Node join request",myPort,hashed_port.toString());
                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "Node join request",myPort,hashed_port_str);
//            System.out.println();
//                for(int i = 0; i < REMOTE_PORT.length; i++) {
//                    new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "Node join", REMOTE_PORT[i]);
//                }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
//        }
//        mynode = h.getMyNode(myPort);
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // TODO Auto-generated method stub
        Log.e(TAG,"SimpleDHTprovider query");
        String request_port = "";
//        try {
            int separator = selection.indexOf(";");
            if (separator != -1) {
                request_port = selection.substring((separator + 1));
                selection = selection.replace(request_port, "");
                selection = selection.replace(";", "");
            }
//        }
//        catch (NullPointerException e){
//            Log.e(TAG, "Query request generated from local port");
//        }
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        // My code
        String columnnames[] = new String[] {"key","value"};
        MatrixCursor cursor = new MatrixCursor(columnnames);

        String file_list[]=getContext().fileList();
        int i = 0;
        if(selection.equals("@")||(selection.equals("*") && mynode.Predecessor.port.equals(mynode.port))) {
            Log.e(TAG, "Check for @/ Check for local");
            while (i < file_list.length) {
                FileInputStream temp;
                Context context = getContext();
                try {
                    // Reference: https://stackoverflow.com/questions/9095610/android-fileinputstream-read-txt-file-to-string
                    temp = context.openFileInput(file_list[i]);
                    StringBuffer fileContent = new StringBuffer("");
                    byte[] buffer = new byte[1024];
                    int n;
                    while ((n = temp.read(buffer)) != -1) {
                        fileContent.append(new String(buffer, 0, n));
                    }
                    int separator_index = fileContent.indexOf(".");
                    String old_key = fileContent.substring(0,separator_index);
                    String msg = fileContent.substring(separator_index+1);
                    cursor.addRow(new Object[]{old_key, msg});
                    i++;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (selection.equals("*")){
            Log.e(TAG, "Check for * on global storage");
            if(!mynode.Successor.port.equals(request_port)) {
                if(request_port.equals(""))
                    new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "query request", myPort, mynode.Successor.port, uri.toString(), selection);
                else
                    new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "query request", request_port, mynode.Successor.port, uri.toString(), selection);
                while (set == false) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                cursor = responseCursor;
                responseCursor = null;
                set = false;
            }
            while (i < file_list.length) {
                FileInputStream temp;
                Context context = getContext();
                try {
                    // Reference: https://stackoverflow.com/questions/9095610/android-fileinputstream-read-txt-file-to-string
                    temp = context.openFileInput(file_list[i]);
                    StringBuffer fileContent = new StringBuffer("");
                    byte[] buffer = new byte[1024];
                    int n;
                    while ((n = temp.read(buffer)) != -1) {
                        fileContent.append(new String(buffer, 0, n));
                    }
                    int separator_index = fileContent.indexOf(".");
                    String old_key = fileContent.substring(0,separator_index);
                    String msg = fileContent.substring(separator_index+1);
                    cursor.addRow(new Object[]{old_key, msg});
                    i++;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            try {
                String hashed_selection_str = genHash(selection);
//                BigInteger hashed_selection = new BigInteger(hashed_selection_str, 16);
//                boolean local_storage = isSuccessor(hashed_selection, mynode);
                boolean local_storage = isSuccessor(hashed_selection_str, mynode);
                if (local_storage) {
                    while (i < file_list.length) {
//                        if (file_list[i].equals(hashed_selection.toString())) {
                        if (file_list[i].equals(hashed_selection_str.toString())) {
                            FileInputStream temp;
                            Context context = getContext();
                            try {
                                //https://stackoverflow.com/questions/9095610/android-fileinputstream-read-txt-file-to-string
                                temp = context.openFileInput(file_list[i]);
                                StringBuffer fileContent = new StringBuffer("");
                                byte[] buffer = new byte[1024];
                                int n;
                                while ((n = temp.read(buffer)) != -1) {
                                    fileContent.append(new String(buffer, 0, n));
                                }
                                int separator_index = fileContent.indexOf(".");
                                String old_key = fileContent.substring(0, separator_index);
                                String msg = fileContent.substring(separator_index + 1);
                                cursor.addRow(new Object[]{old_key, msg});
                                Log.e(TAG,"Query: Main thread: key "+old_key+" found");
                                break;
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else
                            i++;
                    }
                }
                else {
                    new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "query request", myPort, mynode.Successor.port, uri.toString(), selection);
                    while(set == false)
                        Thread.sleep(100);
                    Log.e(TAG,"Query: Main thread: Setting cursor to response cursor ");
                    cursor = responseCursor;
                    Log.e(TAG,"Query: Main thread: cursor is set in main thread ");
                    responseCursor = null;
                    set = false;
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.v("query", selection);
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        Log.e(TAG, "Query: Main thread: returning from main thread "+cursor+" at port "+myPort);
        return cursor;
        // My code ends
        //return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        Log.e(TAG,"SimpleDHTprovider update");
        return 0;
    }

    private String genHash(String input) throws NoSuchAlgorithmException {
        Log.e(TAG,"SimpleDHTprovider genhash");
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] sha1Hash = sha1.digest(input.getBytes());
        Formatter formatter = new Formatter();
        for (byte b : sha1Hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

//    String returnHash(String input) throws NoSuchAlgorithmException {
//        return genHash(input);
//    }

    HashSpace getHashSpace(){
        return h;
    }

    ContentValues initTestValues(String old_key, String msg){
        ContentValues cv = new ContentValues();
        cv.put(KEY_FIELD, old_key);
        cv.put(VALUE_FIELD, msg);
        return cv;
    }

    Uri buildUri(String scheme, String authority) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.scheme(scheme);
        return uriBuilder.build();
    }

//    boolean isSuccessor(BigInteger hashed_key, Node mynode) {
    boolean isSuccessor(String hashed_key, Node mynode) {
        if (mynode.Successor == mynode)
            return true;
        else {
//            if (mynode.id.compareTo(hashed_key) == 1 && mynode.Predecessor.id.compareTo(hashed_key) == -1) {
//            if (mynode.id.compareTo(hashed_key) == 1 && mynode.Predecessor.id.compareTo(hashed_key) == -1) {
            if (mynode.id.compareTo(hashed_key) > 0 && mynode.Predecessor.id.compareTo(hashed_key) < 0) {
                return true;
            }
//            else if (mynode.Predecessor.id.compareTo(mynode.id) == 1 && hashed_key.compareTo(mynode.Predecessor.id) == 1)
            else if (mynode.Predecessor.id.compareTo(mynode.id) > 0 && hashed_key.compareTo(mynode.Predecessor.id) > 0)
                return true;
//            else if (mynode.Predecessor.id.compareTo(mynode.id) == 1 && mynode.id.compareTo(hashed_key) == 1)
            else if (mynode.Predecessor.id.compareTo(mynode.id) > 0 && mynode.id.compareTo(hashed_key) > 0)
                return true;
        }
        return false;
    }

    void setResponseCursor(String cursor_string){
        Log.e(TAG, "Query: setResponseCursor: Response received from the client: "+myPort+": "+cursor_string);
        responseCursor = new MatrixCursor(columnnames);
        int separator = cursor_string.indexOf(";");
        String old_key = cursor_string.substring(0, separator);
        String value = cursor_string.substring(separator+1);
//        String[] old_keys = new ArrayList<String>();
//        ArrayList<String> values = new ArrayList<String>();
        old_key = old_key.replace(" ","").replace("[","").replace("]","");
        value = value.replace(" ","").replace("[","").replace("]","");
        String old_key_arr[] = old_key.split(",");
        String values_arr[] = value.split(",");
        for (int i = 0; i < old_key_arr.length; i++)
            responseCursor.addRow(new Object[]{old_key_arr[i], values_arr[i]});
//        Log.e(TAG,"Query: setResponseCursor: responseCursor set");
        Log.e(TAG,"Query: setResponseCursor: responseCursor set"+old_key_arr.toString()+";"+values_arr.toString());
        set = true;
    }

//    Cursor getResponseCursor(){
//        responseCursor = new MatrixCursor(columnnames);
//        return responseCursor;
//    }
}