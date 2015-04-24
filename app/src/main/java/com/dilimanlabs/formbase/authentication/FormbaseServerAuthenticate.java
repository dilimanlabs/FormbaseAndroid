package com.dilimanlabs.formbase.authentication;

import android.util.Log;

import com.dilimanlabs.formbase.DataCenter;
import com.dilimanlabs.formbase.objects.CategoryWrapper;
import com.dilimanlabs.formbase.objects.FormObjectWrapper;
import com.dilimanlabs.formbase.objects.Token;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by user on 4/24/2015.
 */
public class FormbaseServerAuthenticate {
    private static Token tokenObject;
    private static Gson gson;
    private static Token new_token;
    private FormObjectWrapper formObjectWrapper;
    private CategoryWrapper categoryWrapper;


    public static String userSignIn(final String username, final String password, final String token){
         new_token = tryLogin(username, password);
         DataCenter.TOKEN = new_token.getToken();
         return new_token.getToken();
    }



    private static Token tryLogin(String mUsername, String mPassword)
    {
        HttpURLConnection connection;
        OutputStreamWriter request = null;
        gson = new Gson();
        URL url = null;
        String response = null;
        String parameters = "username="+mUsername+"&password="+mPassword;

        try
        {
            url = new URL("http://192.168.0.7/api-token-auth/");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");
            request = new OutputStreamWriter(connection.getOutputStream());
            request.write(parameters);
            request.flush();
            request.close();
            String line = "";
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            response = sb.toString();
            tokenObject = gson.fromJson(response, Token.class);
            Log.e("Response: ", tokenObject.getToken());
            isr.close();
            reader.close();
            connection.disconnect();
            return tokenObject;

        }
        catch(MalformedURLException m){
            Log.e("Malformed", m.getMessage());
            return null;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }

    }

}
