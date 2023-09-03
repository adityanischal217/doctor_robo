package com.demo.doctor_robo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //creating variables for our widgets in xml file.
    private RecyclerView chatsRV;
    private ImageButton sendMsgIB;
    private EditText userMsgEdt;
    private final String USER_KEY = "user";
    private final String BOT_KEY = "bot";
    //creating a variable for our volley request queue.
    private RequestQueue mRequestQueue;
    //creating a variable for array list and adapter class.
    private ArrayList<MessageModal> messageModalArrayList;
    private MessageRVAdapter messageRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //on below line we are initializing all our views.
        chatsRV = findViewById(R.id.idRVChats);
        sendMsgIB = findViewById(R.id.idIBSend);
        userMsgEdt = findViewById(R.id.idEdtMessage);
        //below line is to initialize our request queue.
        mRequestQueue = Volley.newRequestQueue(MainActivity.this);
        mRequestQueue.getCache().clear();
        //creating a new array list
        messageModalArrayList = new ArrayList<>();
        //adding on click listener for send message button.
        sendMsgIB.setOnClickListener(v -> {
            //checking if the message entered by user is empty or not.
            if (userMsgEdt.getText().toString().isEmpty()) {
                //if the edit text is empty display a toast message.
                Toast.makeText(MainActivity.this, "Please enter your message..", Toast.LENGTH_SHORT).show();
                return;
            }
            //calling a method to send message to our bot to get response.
            sendMessage(userMsgEdt.getText().toString());
            //below line we are setting text in our edit text as empty
            userMsgEdt.setText("");

        });

        userMsgEdt.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                sendMessage(userMsgEdt.getText().toString());
                userMsgEdt.setText("");
                return true;
            }
            return false;
        });


        //on below line we are initialiing our adapter class and passing our array lit to it.
        messageRVAdapter = new MessageRVAdapter(messageModalArrayList, this);
        //below line we are creating a variable for our linear layout manager.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
        //below line is to set layout manager to our recycler view.
        chatsRV.setLayoutManager(linearLayoutManager);
        //below line we are setting adapter to our recycler view.
        chatsRV.setAdapter(messageRVAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sendMessage(String userMsg) {
        //below line is to pass message to our array list which is entered by the user.
        messageModalArrayList.add(new MessageModal(userMsg, USER_KEY));
        messageRVAdapter.notifyDataSetChanged();
        chatsRV.smoothScrollToPosition(Objects.requireNonNull(chatsRV.getAdapter()).getItemCount());
        //url for our brain
        //make sure to add mshape for uid.

        String url = "http://api.brainshop.ai/get?bid=177616&key=9fciofc5NNJCB0ui&uid=[uid]&msg=" + userMsg;
        //creating a variable for our request queue.
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        //on below line we are making a json object request for a get request and passing our url .
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                //in on response method we are extracting data from json response and adding this response to our array list.
                String botResponse = response.getString("cnt");
                messageModalArrayList.add(new MessageModal(botResponse, BOT_KEY));
                //  messageModalArrayList.add(new MessageModal("https://play.google.com/store/apps/details?id=hied.esanjeevaniabopd.com&hl=en&gl=US", BOT_KEY));
                //notifying our adapter as data changed.
                messageRVAdapter.notifyDataSetChanged();
                chatsRV.smoothScrollToPosition(Objects.requireNonNull(chatsRV.getAdapter()).getItemCount());
            } catch (JSONException e) {
                e.printStackTrace();
                //handling error response from bot.
                messageModalArrayList.add(new MessageModal("No response", BOT_KEY));
                messageRVAdapter.notifyDataSetChanged();

            }

        }, error -> {
            //error handling.
            messageModalArrayList.add(new MessageModal("Sorry no response found", BOT_KEY));
            Toast.makeText(MainActivity.this, "No response from the bot..", Toast.LENGTH_SHORT).show();
        });
        //at last adding json object request to our queue.
        queue.add(jsonObjectRequest);

    }
}