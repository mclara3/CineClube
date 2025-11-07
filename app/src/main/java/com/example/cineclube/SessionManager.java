package com.example.cineclube;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "user_session";
    private static final String KEY_EMAIL = "user_email";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // Salvar sessão ao logar
    public void createSession(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    // Verificar se usuário está logado
    public boolean isLoggedIn() {
        return prefs.contains(KEY_EMAIL);
    }

    // Recuperar email do usuário logado
    public String getUserEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    // Limpar sessão (logout ou exclusão)
    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}
