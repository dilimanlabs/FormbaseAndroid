package com.dilimanlabs.formbase.authentication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by user on 4/23/2015.
 */
public class FormbaseAuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        FormbaseAuthenticator authenticator = new FormbaseAuthenticator(this);
        return authenticator.getIBinder();
    }
}
