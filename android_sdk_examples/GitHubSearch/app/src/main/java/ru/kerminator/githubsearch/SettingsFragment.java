package ru.kerminator.githubsearch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;


public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_githubsearch, rootKey);

        // После того, как мы добавили Preferences в "pref_githubsearch.xml", необходимо
        // сделать две важных вещи:
        // 1) Реализовать поддержку Summary - у всех органов управления, кроме check box-ов,
        //      не отображаются текущие установленные значения и их нужно
        //      считывать из SharedPreferences и добавлять в Summary вручную
        // 2) Вручную контролировать корректность значений, введённых в строке редактирования.
        //      Если этого не сделать, приложение может начать падать даже при старте
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();

        int count = prefScreen.getPreferenceCount();

        // Проходим по всем preferences и устанавливаем их значения вручную
        for (int i = 0; i < count; i++) {
            Preference p = prefScreen.getPreference(i);
            // Для checkbox-ов устанавливать preferences не нужно, т.к. у них есть
            // только два возможных значения: on и off
            if (!(p instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummary(p, value);
            }
        }

        // Добавляем OnPreferenceChangeListener для того, чтобы подписаться на событие изменения
        // значения EditTextPreference - их корректность необходимо проверять в динамике
        Preference preference = findPreference(getString(R.string.pref_int_key));
        preference.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Определяем, были ли какие-то изменения сделаны
        Preference preference = findPreference(key);
        if (null != preference) {
            // Обновляем summary свойств (preferences), которые были изменены
            if (!(preference instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummary(preference, value);
            }
        }
    }

    /**
     * Обновляем the summary для конкретного (the) preference
     *
     * @param preference The preference которое должно быть обновлено
     * @param value      The value которое необходимо установить
     */
    private void setPreferenceSummary(Preference preference, String value) {
        if (preference instanceof ListPreference) {
            // Если устанавливается summary для списка, необходимо сначала
            // понять, какой элемент списка был выбран
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(value);
            if (prefIndex >= 0) {
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            if (preference instanceof EditTextPreference) {
                // Для EditTextPreferences, устанавливаем summary в последнее выбранное значение
                preference.setSummary(value);
            }
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        // Мы используем onPreferenceChange() для того, чтобы контролировать корректность
        // введенного пользователем значения в строке редактирования. Это обязательно
        // необходимо делать если мы не хотим, чтобы наше приложение развалилось (crushed)
        // при старте

        Toast error = Toast.makeText(getContext(),
                "Please choose the port number between 1 and 65535", Toast.LENGTH_SHORT);

        String intKey = getString(R.string.pref_int_key);
        if (preference.getKey().equals(intKey)) {
            String stringIntValue = (String) newValue;
            try {
                int value = Integer.parseInt(stringIntValue);
                if (value < 1 || value > 65535) {
                    error.show();
                    return false;
                }
            } catch (NumberFormatException nfe) {
                error.show();
                return false;
            }
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
