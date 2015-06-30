package com.dilimanlabs.formbase;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;

/**
 * Created by user on 6/8/2015.
 */
public class UEH {
    public static Thread.UncaughtExceptionHandler UEHInit(final Thread.UncaughtExceptionHandler defaultUEH){


        return new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                ex.printStackTrace();
                StringWriter sw = new StringWriter();

                ex.printStackTrace(new PrintWriter(sw));

                String error=sw.toString();

                for (StackTraceElement st:thread.getStackTrace()){
                    error+=st.toString()+"\n";

                }
                logError(error);

                System.exit(2);

                // re-throw critical exception further to the os (important)
                defaultUEH.uncaughtException(thread, ex);
            }

        };
    }

    public static void logError(String error){
        //Also print into logcat

        Log.e("error",error);



        String complete_dir= Environment.getExternalStorageDirectory().getAbsolutePath()+"/FormBase/";
        File dir = new File(complete_dir);
        if(!dir.exists())
            dir.mkdirs();

        File f=new File(complete_dir+"/"+ Calendar.getInstance().getTime().toString().replace('/', '-').replace(':','-')+".txt");


        if(!f.exists()){
            try {
                f.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(f);


                outputStream.write(error.getBytes());
                outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();

                System.out.println("Something went wrong");
            }
        }
        else{

            try{
                FileOutputStream outputStream = new FileOutputStream(f,true);
                outputStream.write((error).getBytes());
                outputStream.close();


            } catch (IOException e) {
                e.printStackTrace();

                System.out.println("Something went wrong");
            }

        }
    }
}
