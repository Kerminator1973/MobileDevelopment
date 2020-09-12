package ru.kerminator.sms_transmitter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

// Статьи об отправке SMS-сообщений из приложения Android:
// https://www.androidtutorialpoint.com/basics/send-sms-programmatically-android-tutorial/
// https://stackoverflow.com/questions/32635704/android-permission-doesnt-work-even-if-i-have-declared-it

// Импортируем статическую константу для установки права отправки SMS-сообщений из приложения
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.SEND_SMS;


public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int REQUEST_SMS = 0;

    // BroadcastReceiver необходим для получения сообщений об отправке и получении
    // SMS-сообщений. Это необходимо для контроля результата выполнения команды
    private BroadcastReceiver sentStatusReceiver, deliveredStatusReceiver;

    // Объект реализующий API для получения информации от сервера
    TransmitterService mClient;

    // Ссылка на органы управления приложения
    private EditText messageText;
    private EditText phone;

    // Кнопка запуска/остановки таймера проверки новых SMS-сообщений
    private Button mStart;

    // Создаём массив, в котором будет хранится информация о принятых наличных
    ArrayList<SentSMS> mMessages = new ArrayList<>();

    // Определяем ссылку на RecyclerView и адаптер для создания
    // ViewHolder и связывания его с данными (binding)
    private MessagesAdapter mAdapter;
    private RecyclerView mSMSList;

    // Флаг управления таймером
    private boolean mIsTimerActive = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Получаем ссылку на RecyclerView текущей Activity
        mSMSList = findViewById(R.id.rvHistory);

        // Добавляем компонент управления разметкой внутри RecyclerView
        // и для оптимизации, устанавливает фиксированную высоту элементов
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mSMSList.setLayoutManager(layoutManager);
        mSMSList.setHasFixedSize(true);

        // Создаём экземпляр адаптера и связываем его с RecyclerView
        mAdapter = new MessagesAdapter(mMessages);
        mSMSList.setAdapter(mAdapter);

        // Регистрируем callback-функцию, обрабатывающую любые изменения
        // в SharedPreferences: любое изменение в пользовательском интерфейсе,
        // и даже вход в диалог Settings, будет приводить к вызовы callback,
        // см. onSharedPreferenceChanged
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener( this );

        // При создании Activity обновляем адрес web-сервера, используя параметры
        // SharedPreferences
        String apiBaseUrl = getApiBaseUrl(sharedPreferences);

        // TODO: придумать способ проверки корректности URL, без обработки исключения
        try {
            // Создаём экземпляр класса, реализующий API web-сервера
            mClient =  ServiceGenerator.getInstance(apiBaseUrl).create(TransmitterService.class);
        }
        catch(IllegalArgumentException ex) {

            mClient =  ServiceGenerator.getInstance("https://127.0.0.1:8080/")
                    .create(TransmitterService.class);
        }

        // Определяем объекты-ссылки на элементы пользовательского интерфейса
        messageText = findViewById(R.id.etMessageText);
        phone = findViewById(R.id.etPhone);

        // Определяем систематическую задачу получения от сервера команд на отправку новых сообщений
        //
        // по проверке новых сообщений для отправки
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {

            @Override
            public void run() {

                Call<SMSResponse> call = mClient.getLast();
                call.enqueue(new Callback<SMSResponse>() {
                    @Override
                    public void onResponse(Call<SMSResponse> call, Response<SMSResponse> response) {

                        if(response.isSuccessful()) {
                            if (response.body().phone.length() > 0 &&
                                    response.body().message.length() > 0) {

                                // Если получена информация о новом сообщении, выводим
                                // соответствующую информацию в окно сообщений
                                mMessages.add(new SentSMS(response.body().phone,
                                        response.body().message));
                                mAdapter.notifyDataSetChanged();

                                sendSMS(response.body().phone, response.body().message);
                                return;
                            }
                        }

                        // TODO: фиксировать код ответа, выводить диагностическое сообщение
                    }

                    @Override
                    public void onFailure(Call<SMSResponse> call, Throwable t) {
                        Log.i("Info", "Failure to connect");
                    }
                });

                // Повторяем задачу каждые 10 секунд
                handler.postDelayed(this, 10000);
            }
        };

        // Находим кнопку запуска таймера проверки наличия новых SMS на сервере
        // и активируем систематический запрос сообщений
        mStart = findViewById(R.id.btnTimer);
        mStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(mIsTimerActive) {
                    handler.removeCallbacks(runnable);  // Останавливаем таймер
                } else {
                    handler.removeCallbacks(runnable);
                    handler.post(runnable);             // Запускаем таймер
                }

                // Переключаем режим
                mIsTimerActive = !mIsTimerActive;
                mStart.setText( mIsTimerActive ? "STOP!" : "START!" );
            }
        });

        // Определение поведения запроса нового сообщения с web-сервера
        Button webRequestButton = findViewById(R.id.web_request);
        webRequestButton.setOnClickListener(new View.OnClickListener() {

              @Override
              public void onClick(View view) {

                  Call<SMSResponse> call = mClient.getLast();
                  call.enqueue(new Callback<SMSResponse>() {
                      @Override
                      public void onResponse(Call<SMSResponse> call, Response<SMSResponse> response) {

                          if(response.isSuccessful()) {
                              StringBuilder builder = new StringBuilder();
                              builder.append("PhoneNo: ").append(response.body().phone);
                              builder.append(", Message: ").append(response.body().message);

                              Toast.makeText(MainActivity.this, builder.toString(),
                                      Toast.LENGTH_LONG).show();
                              return;
                          }

                          // TODO: фиксировать код ответа, выводить диагностическое сообщение
                      }

                      @Override
                      public void onFailure(Call<SMSResponse> call, Throwable t) {

                          Toast.makeText(MainActivity.this, "Failure!",
                                  Toast.LENGTH_LONG).show();
                      }
                  });
              }
        });

        // Определение поведения при тестовой отправке SMS-сообщения
        Button sendButton = findViewById(R.id.send_message);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                    int hasSMSPermission = checkSelfPermission(Manifest.permission.SEND_SMS);
                    if (hasSMSPermission != PackageManager.PERMISSION_GRANTED) {

                        // Определяем, следует ли запрашивать у пользователя право для
                        // выполнения отправки SMS-сообщений
                        if (!shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)) {

                            showMessageOKCancel("You need to allow access to Send SMS",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[] {Manifest.permission.SEND_SMS},
                                                        REQUEST_SMS);
                                            }
                                        }
                                    });
                            return;
                        }

                        requestPermissions(new String[] {Manifest.permission.SEND_SMS},
                                REQUEST_SMS);
                        return;
                    }

                    // Если все необходимые права получены, отправляем из приложения
                    // SMS-сообщение
                    sendMessageBySMS();
                }
            }
        });
    }

    public void onResume() {
        super.onResume();

        sentStatusReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {

                String s = "Unknown Error";
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        s = "Message Sent Successfully !!";
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        s = "Generic Failure Error";
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        s = "Error : No Service Available";
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        s = "Error : Null PDU";
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        s = "Error : Radio is off";
                        break;
                    default:
                        break;
                }

                // TODO: размещать сообщение в специальном поле
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
            }
        };

        deliveredStatusReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {

                String s = "Message Not Delivered";
                switch(getResultCode()) {
                    case Activity.RESULT_OK:
                        s = "Message Delivered Successfully";
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }

                phone.setText("");
                messageText.setText("");

                // TODO: размещать сообщение в специальном поле
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
            }
        };

        // При восстановлении работоспособности Activity, повторно регистрируем  обработчики
        // широковещательных сообщений системы, посредством которых может быть код завершения
        // отправки SMS-сообщения (sentStatusReceiver), а также подтверждение о получении
        // SMS-сообщения (deliveredStatusReceiver)
        registerReceiver(sentStatusReceiver, new IntentFilter("SMS_SENT"));
        registerReceiver(deliveredStatusReceiver, new IntentFilter("SMS_DELIVERED"));
    }

    public void onPause() {
        super.onPause();

        // При приостановке Activity, отменяем подписку на широковещательные сообщения
        unregisterReceiver(sentStatusReceiver);
        unregisterReceiver(deliveredStatusReceiver);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    // Метод осуществляет отправку SMS-сообщения
    public void sendMessageBySMS() {

        // Считываем значения из полей ввода
        String strPhone = phone.getText().toString();
        String strMessageText = messageText.getText().toString();

        if(strPhone.length() < 10) {
            Toast.makeText(MainActivity.this, "Length is less than 10 numbers",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if(strMessageText.length() < 3) {
            Toast.makeText(MainActivity.this, "Message is less than 3 chars",
                    Toast.LENGTH_LONG).show();
            return;
        }

        sendSMS(strPhone, strMessageText);
    }

    public void sendSMS(String phone, String message) {

        // Получаем компонент Manager, который отвечает за доставку SMS-сообщений
        SmsManager sms = SmsManager.getDefault();

        // Если сообщение слишком длинное, разбиваем его на несколько коротких сообщений
        List<String> messages = sms.divideMessage(message);
        for (String msg : messages) {

            PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
            PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);
            sms.sendTextMessage(phone, null, msg, sentIntent, deliveredIntent);
        }
    }

    // Вспомогательные методы-обёртки для работы с разрешениями отправки SMS-сообщений
    private boolean checkPermission() {
        return ( ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS ) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{SEND_SMS}, REQUEST_SMS);
    }

    // Методы позволяют добавить в прилодение дополнительное меню
    // настройки параметров приложения
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
        }
        return super.onOptionsItemSelected(item);
    }

    private String getApiBaseUrl(SharedPreferences  sharedPreferences)
    {
        String keyHostAddress = getString(R.string.pref_host_address);
        String hostAddress = sharedPreferences.getString(keyHostAddress, "https://10.0.2.2");
        Log.i("Info", "The host address: " + hostAddress);

        String keyHostPort = getString(R.string.pref_host_port);
        String hostPort = sharedPreferences.getString(keyHostPort, "3000");
        Log.i("Info", "Successfully loaded hostPort: " + hostPort);

        if (!hostAddress.startsWith("https://"))
            hostAddress = "https://" + hostAddress;

        return hostAddress + ":" + hostPort + "/";
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        // TODO: данный обработчик вызывается при изменении каждого параметра и
        // создавать здесь новых объект retrofit является избыточным поведением

        // Выполняем трассировку изменений в SharedPreferences. Этот метод вызывается только
        // в том случае, если какие-то из SharedPreferences действительно изменились.
        // При запуске приложения, этот метод не вызывается
        if (key.equals(getString(R.string.pref_host_address)) || (key.equals(getString(R.string.pref_host_port)))) {

            // Изменяем URL web-сервера, к которому будет подключаться приложение
            String apiBaseUrl = getApiBaseUrl(sharedPreferences);

            // Создаём новый класс для работы с API
            try {
                ServiceGenerator.changeApiBaseUrl(apiBaseUrl);
            }
            catch(IllegalArgumentException ex) {

                // TODO: Нужно разумно отработать данное исключение
                ServiceGenerator.changeApiBaseUrl("https://127.0.0.1:8080/");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // При завершении Activity необходимо отменять регистрацию
        // callback-функции обработки изменений в SharedPreferences
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

}
