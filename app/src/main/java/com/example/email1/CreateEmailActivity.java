package com.example.email1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.email1.model.AccountUser;
import com.example.email1.model.Message;
import com.example.email1.remote.ApiUtils;
import com.example.email1.remote.UserService;

import java.util.regex.Matcher;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEmailActivity extends AppCompatActivity {
    UserService userService;

    private EditText mEditTextTo;
    private EditText mEditTextBc;
    private EditText mEditTextCc;
    private EditText mEditTextSubject;
    private EditText mEditTextMessage;
    private AccountUser accountUser;
    Message message=new Message();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_email);
        userService= ApiUtils.getUserService();

        PreferenceManager.setDefaultValues(this,R.xml.preferences,false);
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor=sharedPreferences.edit();

        Toolbar myToolbar = findViewById(R.id.my_toolbar_create_emails);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        accountUser=getUserFromLogin(sharedPreferences);

        mEditTextTo=findViewById(R.id.edit_text_to);
        mEditTextSubject=findViewById(R.id.edit_text_subject);
        mEditTextMessage=findViewById(R.id.edit_text_message);
        mEditTextBc=findViewById(R.id.edit_text_bc);
        mEditTextCc=findViewById(R.id.edit_text_cc);
        Intent intent=getIntent();
        if(intent!=null){
            mEditTextTo.setText(intent.getStringExtra("to"));
            mEditTextCc.setText(intent.getStringExtra("cc"));
            mEditTextBc.setText(intent.getStringExtra("bcc"));
            mEditTextSubject.setText(intent.getStringExtra("subject"));
            mEditTextMessage.setText(intent.getStringExtra("text"));
        }

    }

    private void sendMail() {
        String sendto=mEditTextTo.getText().toString();
        String [] recipients=sendto.split(",");
        String subject=mEditTextSubject.getText().toString();
        String messageText=mEditTextMessage.getText().toString();
        String sendbc=mEditTextBc.getText().toString();
        String sendcc=mEditTextCc.getText().toString();

        message.setSendto(sendto);
        message.setSendbc(sendbc);
        message.setSendcc(sendcc);
        message.setSubject(subject);
        message.setContent(messageText);
        message.setAccountDto(accountUser);
        message.setSeen(false);

        Call<Message> call=userService.addMessage(message);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful()) {
                    Message m = response.body();

                    Toast.makeText(CreateEmailActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateEmailActivity.this, MessageActivity.class);
                    intent.putExtra("message_id", m.getId());
                    intent.putExtra("from", accountUser.getUsername());
                    startActivity(intent);
                    finish();
                }
            }
            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Toast.makeText(CreateEmailActivity.this,"Error , please try again",Toast.LENGTH_LONG).show();
            }
        });
//        Intent intent=new Intent(Intent.ACTION_SEND);
//        intent.putExtra(Intent.EXTRA_EMAIL,recipients);
//        intent.putExtra(Intent.EXTRA_SUBJECT,subject);
//        intent.putExtra(Intent.EXTRA_TEXT,message);
//
//        intent.setType("message/rfc822");
//        startActivity(Intent.createChooser(intent,"Choose an email client"));
    }



    //ovde su send i cancel i attachment dugmad iz toolbara
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.toolbar_create_email,menu);

        MenuItem sendItem=menu.findItem(R.id.send_email);
        MenuItem cancelItem=menu.findItem(R.id.cancel_email);
        MenuItem attachmentItem=menu.findItem(R.id.attachment);

        sendItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if( validateCc(mEditTextCc)|validateBc(mEditTextBc)| validateTo(mEditTextTo)) {
                    sendMail();
                }else {
                    Toast.makeText(CreateEmailActivity.this,"Validation failed , please try again",Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        cancelItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent=new Intent(CreateEmailActivity.this,EmailsActivity.class);
                startActivity(intent);
                finish();
                return false;
            }
        });
        attachmentItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(CreateEmailActivity.this,"Add Attachment",Toast.LENGTH_LONG).show();
                return false;
            }
        });
        return true;
    }

    private boolean validateTo(EditText mEditTextTo) {
        Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(mEditTextTo.getText());
        if (matcher.matches()) return true;
        if (mEditTextTo.getText().toString() == "") return true;
        return false;
    }
    private boolean validateBc(EditText mEditTextBc) {
        Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(mEditTextBc.getText());
        if (matcher.matches()) return true;
        if ("".equals(mEditTextBc.getText().toString())) return true;
        return false;
    }
    private boolean validateCc(EditText mEditTextCc) {
        Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(mEditTextCc.getText());
        if (matcher.matches()) return true;
        if (mEditTextCc.getText().toString() == "") return true;
        return false;
    }
    private AccountUser getUserFromLogin(SharedPreferences sharedPreferences) {
        AccountUser accountUser=new AccountUser();

        accountUser.setId(sharedPreferences.getLong("id",1));
        accountUser.setUsername(sharedPreferences.getString("username","user@example.com"));
        accountUser.setPassword(sharedPreferences.getString("password","123"));
        accountUser.setStmp(sharedPreferences.getString("stmp","stmp"));
        accountUser.setPop3(sharedPreferences.getString("pop3","pop3"));

        return accountUser;
    }
}
