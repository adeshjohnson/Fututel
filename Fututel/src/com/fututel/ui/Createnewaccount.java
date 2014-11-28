package com.fututel.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.HttpConn.AsyncHttpClient;
import com.HttpConn.AsyncHttpResponseHandler;
import com.HttpConn.JsonHttpResponseHandler;
import com.HttpConn.RequestParams;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.fututel.R;
import com.fututel.api.SipProfile;
import com.fututel.api.SipUri;
import com.fututel.db.DBProvider;
import com.fututel.utils.PreferencesWrapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2014.04.06.
 */
public class Createnewaccount extends SherlockFragmentActivity  {

    private EditText email;
    private Button submiteaccount;
    private AsyncHttpResponseHandler handler = null;
    private String loginid = "f739f7060d";
    private String apisecuretkey = "JTqQpnMs4C(J56g#";
    private String hash = "";
    private String uname = "";
    private String passwd = "";
    private ProgressDialog progDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createnewaccount);
        submiteaccount = (Button) findViewById(R.id.submiteaccount);
        email = (EditText) findViewById(R.id.email);


        submiteaccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (email.getText().length() == 0) {
                    new AlertDialog.Builder(Createnewaccount.this)
                            .setTitle("Please input email.")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }
                handler_progress();

                try {
                    hash = SHA1(email.getText().toString()+loginid+apisecuretkey);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                AsyncHttpClient client = new AsyncHttpClient();
                client.setTimeout(4000);

                RequestParams params = new RequestParams();
                params.put("id", "f739f7060d");
                params.put("email", email.getText().toString());
                params.put("API_Secret_Key", "b1ea4735aeec8809ee6d659b80f6a77003bcd311");
                params.put("hash", hash);


                client.post("http://sip2.fututel.com/billing/api/user_register", params, handler);

                progDialog = ProgressDialog.show(
                        Createnewaccount.this,
                        "",
                        "Waiting",
                        true,
                        true,
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                            }
                        }
                );

            }
        });
    }


    private void handler_progress()
    {
        handler = new AsyncHttpResponseHandler()
        {
            int result = 0;
            @Override
            public void onSuccess(String data) {
                uname = parseUsername(data);
                passwd = parsePassword(data);
                if (uname != null && uname.length() != 0 && passwd != null && passwd.length() != 0)
                    LoginFunction();
                else
                {
                    if(data.contains("This email address is already in use"))
                    {
                        new AlertDialog.Builder(Createnewaccount.this)
                                .setTitle("This email address is already in use.")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                    }
                                })

                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                    }
                }
            }

            @Override
            public void onFailure(Throwable ex, String exception){
                String printstr = exception;
            }

            @Override
            public void onFinish()
            {
                progDialog.dismiss();
            }
        };
    }

    class AccountStatusContentObserver extends ContentObserver {

        public AccountStatusContentObserver(Handler h) {
            super(h);
        }

        public void onChange(boolean selfChange) {
            finish();
        }
    }


    private void LoginFunction()
    {

        SipProfile account = null;
        AccountStatusContentObserver statusObserver = null;
        Handler mHandler = new Handler();

        PreferencesWrapper prefs = new PreferencesWrapper(getApplication());
        account =  SipProfile.getProfileFromDbId(this, 1, DBProvider.ACCOUNT_FULL_PROJECTION);
        account = buildAccount(account);


        account.wizard = "BASIC";
        if (account.id == SipProfile.INVALID_ID) {
            prefs.startEditing();
            prefs.endEditing();
            Uri uri = getContentResolver().insert(SipProfile.ACCOUNT_URI, account.getDbContentValues());

            account.id = ContentUris.parseId(uri);

        } else {
            prefs.startEditing();
            prefs.endEditing();
            getContentResolver().update(ContentUris.withAppendedId(SipProfile.ACCOUNT_ID_URI_BASE, account.id), account.getDbContentValues(), null, null);
        }
        if(statusObserver == null) {
            statusObserver = new AccountStatusContentObserver(mHandler);
            getContentResolver().registerContentObserver(SipProfile.ACCOUNT_STATUS_URI, true, statusObserver);
        }

        finish();
        Intent newintent = new Intent(this, SipHome.class);
        this.startActivity(newintent); //rangdong
    }


    private  SipProfile buildAccount(SipProfile account) {
        String domain =  "sip2.fututel.com";
        account.display_name = "Fututel";

        account.acc_id = "<sip:" + SipUri.encodeUser(uname) + "@" + domain +">";

        String regUri = "sip:"+ domain;
        account.reg_uri = regUri;
        account.proxies = new String[] { regUri } ;


        account.realm = "*";
        account.username =  uname;
        account.data = passwd;
        account.scheme = SipProfile.CRED_SCHEME_DIGEST;
        account.datatype = SipProfile.CRED_DATA_PLAIN_PASSWD;
        account.transport = SipProfile.TRANSPORT_UDP;

        return account;
    }




    private String parseUsername(String htmldata)
    {
        String username = "";
        Pattern authorization_code_pattern = Pattern.compile("<username>.+?</username>", Pattern.DOTALL);
        Matcher username_code_matcher = authorization_code_pattern.matcher(htmldata);
        while(username_code_matcher.find()) {
            username = username_code_matcher.group();
            username = username.replace("<username>", "").replace("</username>", "").trim();

            return username;
        }

        return "";
    }


    private String parsePassword(String htmldata)
    {
        String password = "";
        Pattern authorization_code_pattern = Pattern.compile("<password>.+?</password>", Pattern.DOTALL);
        Matcher password_code_matcher = authorization_code_pattern.matcher(htmldata);
        while(password_code_matcher.find()) {
            password = password_code_matcher.group();
            password = password.replace("<password>", "").replace("</password>", "").trim();

            return password;
        }

        return "";
    }


    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }


}
