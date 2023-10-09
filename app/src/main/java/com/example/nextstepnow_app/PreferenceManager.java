package com.example.nextstepnow_app;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static final String PREF_NAME = "com.example.nextstepnow_app.PREF";
    private static final String KEY_TASK_PREFIX = "task_";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public boolean getTaskCompletionStatus(String taskId) {
        return sharedPreferences.getBoolean(KEY_TASK_PREFIX + taskId, false);
    }

    public void setTaskCompletionStatus(String taskId, boolean isCompleted) {
        editor.putBoolean(KEY_TASK_PREFIX + taskId, isCompleted);
        editor.apply();
    }
}
