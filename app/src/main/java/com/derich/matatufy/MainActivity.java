package com.derich.matatufy;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private FirebaseUser mUser;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpNavigation();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }
    public void setUpNavigation(){
        bottomNavigationView =findViewById(R.id.bttm_nav);
        NavHostFragment navHostFragment = (NavHostFragment)getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView,
                navHostFragment.getNavController());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main,menu);
        this.menu = menu;
        MenuItem mItem= menu.findItem(R.id.saveStage);
        mItem.setVisible(false);
        if (mUser !=null){
            MenuItem menuItem = menu.findItem(R.id.login);
            menuItem.setIcon(R.drawable.ic_login);
        }
        else {
            MenuItem menuItem = menu.findItem(R.id.login);
            menuItem.setIcon(R.drawable.ic_logout);
        }
        MenuItem item1 = menu.findItem(R.id.saveRideShare);
        item1.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        switch (item.getItemId()){
            case R.id.login:
                if (mUser!=null){
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_logout));
                    signOut();
                }
                else {
                    Intent intent= new Intent(MainActivity.this,FirebaseUI.class);
                    startActivity(intent);
                }
                default:
                    return super.onOptionsItemSelected(item);
        }
    }
    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        startActivity(getIntent());
                    }
                });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
