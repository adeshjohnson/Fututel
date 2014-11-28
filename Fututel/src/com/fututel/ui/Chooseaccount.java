package com.fututel.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.fututel.R;
import com.fututel.api.SipProfile;
import com.fututel.api.SipProfileState;
import com.fututel.utils.Log;

import java.util.ArrayList;

/**
 * Created by Administrator on 2014.04.06.
 */
public class Chooseaccount extends SherlockFragmentActivity  {

    private Button createnewaccount;
    private Button haveaccount;
    public static Activity sipparent;



    class AccountStatusContentObserver extends ContentObserver {

        public AccountStatusContentObserver(Handler h) {
            super(h);
        }

        public void onChange(boolean selfChange) {
           //startLogin(getBaseContext());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chooseaccount);
        createnewaccount = (Button) findViewById(R.id.createnewaccount);
        haveaccount = (Button) findViewById(R.id.haveaccount);
        sipparent = this;

        Cursor c = getContentResolver().query(SipProfile.ACCOUNT_URI, new String[] {
                SipProfile.FIELD_ID
        }, null, null, null);
        int accountCount = 0;
        if (c != null) {
            try {
                accountCount = c.getCount();
            } catch (Exception e) {
            } finally {
                c.close();
            }
        }

        if (accountCount != 0) {
            finish();
            Intent newintent = new Intent(Chooseaccount.this, SipHome.class);
            Chooseaccount.this.startActivity(newintent); //rangdong
        }

        createnewaccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            finish();
            Intent newintent = new Intent(Chooseaccount.this, Createnewaccount.class);
            Chooseaccount.this.startActivity(newintent); //rangdong

            }
        });

        haveaccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
                Intent newintent = new Intent(Chooseaccount.this, SipHome.class);
                Chooseaccount.this.startActivity(newintent); //rangdong
            }
        });
    }


    public  static void startLogin(Context context)
    {
        ArrayList<SipProfileState> activeProfilesState = new ArrayList<SipProfileState>();
        Cursor c = context.getContentResolver().query(SipProfile.ACCOUNT_STATUS_URI, null, null, null, null);
        if (c != null) {
            try {
                if(c.getCount() > 0) {
                    c.moveToFirst();
                    do {
                        SipProfileState ps = new SipProfileState(c);
                        if(ps.isValidForCall()) {
                            activeProfilesState.add(ps);
                        }
                    } while ( c.moveToNext() );
                }
            } catch (Exception e) {
            } finally {
                c.close();
            }
        }


        if (activeProfilesState.size() > 0) {
            if (sipparent != null) {
                sipparent.finish();
            }
//            if (pb != null)
//                pb.setVisibility(View.INVISIBLE);
        }
    }








}
