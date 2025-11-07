package com.example.cineclube;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {

    protected void setupBottomNav(int selectedItemId, String currentUserEmail) {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        if (bottomNav == null) return;

        if (selectedItemId != -1) {
            bottomNav.setSelectedItemId(selectedItemId);
        }

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home && !(this instanceof MainActivity)) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("user_email", currentUserEmail);
                startActivity(intent);
                finish(); // fecha a activity atual
                return true;
            }
            else if (id == R.id.nav_conta && !(this instanceof MinhaContaActivity)) {
                Intent intent = new Intent(this, MinhaContaActivity.class);
                intent.putExtra("user_email", currentUserEmail);
                startActivity(intent);
                finish();
                return true;
            }

            return true;
        });
    }
}
