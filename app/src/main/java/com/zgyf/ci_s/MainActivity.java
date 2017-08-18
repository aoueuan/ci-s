package com.zgyf.ci_s;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  public void cap(View view) {
    Observable.timer(15, TimeUnit.SECONDS)
        .subscribeOn(Schedulers.computation())
        .observeOn(Schedulers.io())
        .flatMap(new Function<Long, ObservableSource<Boolean>>() {
          @Override public ObservableSource<Boolean> apply(@NonNull Long aLong) throws Exception {
            return Observable.create(new ObservableOnSubscribe<Boolean>() {
              private final String baseCommand = "screencap -p /sdcard/%s.png\n";
              private final int fixValue = 100;
              private final String nextLine = "\n";

              @Override public void subscribe(@NonNull ObservableEmitter<Boolean> emitter)
                  throws Exception {
                BufferedReader successReader = null;
                BufferedReader errorReader = null;
                DataOutputStream dos = null;
                try {
                  Process exec = Runtime.getRuntime().exec("su\n");
                  dos = new DataOutputStream(exec.getOutputStream());
                  dos.write(
                      String.format(baseCommand, String.valueOf(new Date().getTime())).getBytes());
                  dos.flush();
                  dos.writeBytes("exit\n");
                  dos.flush();
                  dos.close();
                  dos = null;
                  successReader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
                  errorReader = new BufferedReader(new InputStreamReader(exec.getErrorStream()));
                  int r = exec.waitFor();
                  StringBuilder successSb = new StringBuilder();
                  StringBuilder errorSb = new StringBuilder();
                  for (String str = successReader.readLine(); str != null;
                      str = successReader.readLine()) {
                    successSb.append(str);
                  }
                  for (String str = errorReader.readLine(); str != null;
                      str = errorReader.readLine()) {
                    errorSb.append(str).append(nextLine);
                  }
                  String text;
                  if (r == 0) {
                    emitter.onNext(true);
                    emitter.onComplete();
                  } else {
                    text = errorSb.toString();
                    emitter.onError(new RuntimeException(text));
                  }
                } catch (IOException e) {
                  e.printStackTrace();
                  emitter.onError(e);
                } catch (InterruptedException e) {
                  e.printStackTrace();
                  emitter.onError(e);
                } finally {
                  if (successReader != null) {
                    try {
                      successReader.close();
                    } catch (IOException e) {
                      e.printStackTrace();
                    }
                  }
                  if (errorReader != null) {
                    try {
                      errorReader.close();
                    } catch (IOException e) {
                      e.printStackTrace();
                    }
                  }
                  if (dos != null) {
                    dos.close();
                  }
                }
              }
            });
          }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new DisposableObserver<Boolean>() {
          @Override public void onNext(@NonNull Boolean aBoolean) {
            Log.d("TEST", "result: " + aBoolean);
            Toast.makeText(getApplicationContext(), "result: " + aBoolean, Toast.LENGTH_SHORT)
                .show();
          }

          @Override public void onError(@NonNull Throwable e) {
            Log.e("TEST", "onError: " + e.getMessage());
            Toast.makeText(getApplicationContext(), "onError: " + e.getMessage(),
                Toast.LENGTH_SHORT).
                show();
          }

          @Override public void onComplete() {
            Log.d("TEST", "onComplete");
            Toast.makeText(getApplicationContext(), "onComplete", Toast.LENGTH_SHORT).show();
          }
        });
  }

  private void t() {
    Observable.create(new ObservableOnSubscribe<Boolean>() {
      private final String baseCommand = "screencap -p /sdcard/%s.png\n";
      private final int fixValue = 100;
      private final String nextLine = "\n";

      @Override public void subscribe(@NonNull ObservableEmitter<Boolean> emitter)
          throws Exception {
        BufferedReader successReader = null;
        BufferedReader errorReader = null;
        DataOutputStream dos = null;
        try {
          Process exec = Runtime.getRuntime().exec("su\n");
          dos = new DataOutputStream(exec.getOutputStream());
          dos.write(String.format(baseCommand, String.valueOf(new Date().getTime())).getBytes());
          dos.flush();
          dos.writeBytes("exit\n");
          dos.flush();
          dos.close();
          dos = null;
          successReader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
          errorReader = new BufferedReader(new InputStreamReader(exec.getErrorStream()));
          int r = exec.waitFor();
          StringBuilder successSb = new StringBuilder();
          StringBuilder errorSb = new StringBuilder();
          for (String str = successReader.readLine(); str != null; str = successReader.readLine()) {
            successSb.append(str);
          }
          for (String str = errorReader.readLine(); str != null; str = errorReader.readLine()) {
            errorSb.append(str).append(nextLine);
          }
          String text;
          if (r == 0) {
            emitter.onNext(true);
            emitter.onComplete();
          } else {
            text = errorSb.toString();
            emitter.onError(new RuntimeException(text));
          }
        } catch (IOException e) {
          e.printStackTrace();
          emitter.onError(e);
        } catch (InterruptedException e) {
          e.printStackTrace();
          emitter.onError(e);
        } finally {
          if (successReader != null) {
            try {
              successReader.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
          if (errorReader != null) {
            try {
              errorReader.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
          if (dos != null) {
            dos.close();
          }
        }
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new DisposableObserver<Boolean>() {
          @Override public void onNext(@NonNull Boolean aBoolean) {
            Log.d("TEST", "result: " + aBoolean);
          }

          @Override public void onError(@NonNull Throwable e) {
            Log.e("TEST", "onError: " + e.getMessage());
          }

          @Override public void onComplete() {
            Log.d("TEST", "onComplete");
          }
        });
  }
}
