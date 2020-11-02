package com.derich.matatufy;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private FirebaseUser mUser;
    private Menu menu;
        private AdView mAdView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpNavigation();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        MobileAds.initialize(this,"ca-app-pub-5255941258844848/6369140378");
       // AdRequest adRequest= new AdRequest().Builder().build();
        //main.xml ca-app-pub-5255941258844848/6369140378
        //main.xml test ca-app-pub-5255941258844848~4812516122
      //  adViewMain.loadAd(AdRequest);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adViewMain);
        mAdView.setVisibility(View.GONE);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Toast.makeText(MainActivity.this,"banner ad loaded" ,Toast.LENGTH_LONG).show();
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Toast.makeText(MainActivity.this,"Failed to load banner ad errorcode " + errorCode,Toast.LENGTH_LONG).show();
                Log.d("mAdView", "onAdFailedToLoad. But why? "+errorCode);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });



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
