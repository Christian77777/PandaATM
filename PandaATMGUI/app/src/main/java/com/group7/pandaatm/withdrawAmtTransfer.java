package com.group7.pandaatm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.group7.pandaatm.data.Message;
import com.group7.pandaatm.data.SessionController;

import java.io.IOException;

public class withdrawAmtTransfer extends AppCompatActivity {

    private String accountNameSrc;
    private String accountNameTar;
    private double amountSrc;
    private double amountTar;
    private double minSrc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_diff_amt);
        Intent pastIntent = getIntent();

        accountNameSrc = pastIntent.getStringExtra("accountNameSrc");
        amountSrc = pastIntent.getDoubleExtra("amountSrc", -1);
        boolean isSrcChecking = pastIntent.getBooleanExtra("isChecking", false);

        //Gets minReqBalance required if source account was a checking account
        if (isSrcChecking) {
            minSrc = pastIntent.getDoubleExtra("minSrc", -1);
        }
        //Target Accounts to Deposit the transfer money into (from account select intent)
        accountNameTar = pastIntent.getStringExtra("accountNameTar");
        amountTar = pastIntent.getDoubleExtra("amountTar", -1);

        //initialize click listeners
        findViewById(R.id.clearButton1).setOnClickListener(buttonClickListener);
        findViewById(R.id.enterButton1).setOnClickListener(buttonClickListener);
        findViewById(R.id.cancelButton2).setOnClickListener(buttonClickListener);
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText withdrawAmt = findViewById(R.id.withdrawAmt);
            switch(v.getId()){
                case R.id.clearButton1:
                    withdrawAmt.getText().clear();
                    break;
                case R.id.enterButton1:
                    String amountText = withdrawAmt.getText().toString();
                    try {
                        double amount = Double.parseDouble(amountText);
                        if (amount < amountSrc) {
                            //Alerts user that the amount will in going below min required balance
                            if(amountSrc - amount < minSrc)
                            {
                                AlertDialog.Builder belowMinReqBal = new AlertDialog.Builder(withdrawAmtTransfer.this);
                                belowMinReqBal.setMessage("Transaction will result in going below account's minimum required balance.");
                                belowMinReqBal.setTitle("Warning: Required Minimum Balance...");
                                belowMinReqBal.setPositiveButton("OK", null);
                                belowMinReqBal.setCancelable(false);
                                belowMinReqBal.create().show();
                            }
                            Thread worker24 = new Thread(() -> {
                                try {
                                    SessionController c = SessionController.getInstance();
                                    Message msgSendAmount = new Message(18);
                                    msgSendAmount.addDoubleM(amount);
                                    c.sendMessage(msgSendAmount);
                                    runOnUiThread(() -> {
                                        Intent confirmation = new Intent(withdrawAmtTransfer.this, ConfirmationScreen.class);
                                        confirmation.putExtra("srcAccountName", accountNameSrc);
                                        confirmation.putExtra("srcAmount", amountSrc);
                                        confirmation.putExtra("tarAccountName", accountNameTar);
                                        confirmation.putExtra("tarAmount", amountTar);
                                        startActivity(confirmation);
                                    });
                                } catch(IOException e) {
                                    e.printStackTrace();
                                }
                            });
                            worker24.start();
                        }
                        else {//Not enough money in source account to complete transaction
                            AlertDialog.Builder insufficientFunds = new AlertDialog.Builder(withdrawAmtTransfer.this);
                            insufficientFunds.setMessage("Insufficient Funds in Source Account.");
                            insufficientFunds.setTitle("Transaction failed...");
                            insufficientFunds.setPositiveButton("OK", null);
                            insufficientFunds.setCancelable(false);
                            insufficientFunds.create().show();
                        }
                    } catch(NumberFormatException e){
                        e.printStackTrace();
                    }
                case R.id.cancelButton2://if previous button is clicked, go to Main Menu screen
                    Thread worker25 = new Thread(() -> {
                        try {
                            SessionController c = SessionController.getInstance();
                            Message msgCancelRequest = new Message(17);
                            c.sendMessage(msgCancelRequest);
                            runOnUiThread(() -> {
                                Intent exit = new Intent(withdrawAmtTransfer.this, MenuScreen.class);
                                startActivity(exit);
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    worker25.start();
                    break;
                default://Shouldn't get here
                    break;
            }//end switch
        }//end onClick
    };//end View.OnClickListener
}//end withdrawAmtTransfer

