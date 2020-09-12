package ru.kerminator.sms_transmitter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

public class SettingsFragment extends PreferenceFragmentCompat implements
SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_network);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();

        // Проходимся по всему списку preferences и устанавливаем значение поля "Summary"
        // считав актуальное значение из постоянного хранилища
        for (int i = 0; i < count; i++) {
            Preference p = prefScreen.getPreference(i);
            if (p instanceof EditTextPreference) {
                String value = sharedPreferences.getString(p.getKey(), "");
                p.setSummary(value);
            }
        }

        // Добавляем обработчик, который будет проверять корректность введённых данных
        // для поля ввода с идентификатором/ключём, считанным из R.string.pref_host_address.
        // Для поля ввода "порт" проверку не устанавливаем, т.к. поле числовое - там
        // проверка может быть избыточной
        Preference preference = findPreference(getString(R.string.pref_host_address));
        preference.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        // Определяем, какое свойство было изменено
        Preference preference = findPreference(key);
        if (null != preference) {
            // Обновляем поле "summary" для конкретной строки редактирования: ключ
            // для считывания из постоянного хранилища у нас есть, само свойство
            // для обновления его значения мы нашли по этому ключу
            if (preference instanceof EditTextPreference) {
                String value = sharedPreferences.getString(preference.getKey(), "");
                preference.setSummary(value);
            }
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        // Проверяем формат введённых данных для конкретного поля ввода
        // TODO: Проверку следует реализовать, сейчас используется "счастливчик"
        String preferenceKey = getString(R.string.pref_host_address);
        if (preference.getKey().equals(preferenceKey)) {

            String stringAddress = (String) newValue;
            /*
                if ([Формат адрес ошибочный]) {
                    Toast error = Toast.makeText(getContext(), "Please enter an URL", Toast.LENGTH_SHORT);
                    error.show();
                    return false;
                }
             */
        }

        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
