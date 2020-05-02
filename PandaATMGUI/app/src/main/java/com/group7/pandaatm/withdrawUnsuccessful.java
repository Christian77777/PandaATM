package com.group7.pandaatm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class withdrawUnsuccessful extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_unsuccessful_screen);

        findViewById(R.id.main1).setOnClickListener(buttonClickListener);
    }

    private View.OnClickListener buttonClickListener = v -> {
        switch (v.getId()) {
            case R.id.main1:    //if cancel button is clicked, go back to main screen
                Intent main1 = new Intent(withdrawUnsuccessful.this, MenuScreen.class);
                startActivity(main1);
                break;
            default:
                break;
        }
    };
}
