package com.pecs.pecsi;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.pecs.pecsi.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String FILE_PATH = Environment.getExternalStorageDirectory().toString() + "/Download/response.json";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "alert_monitor_channel";

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private Handler handler;
    private Runnable runnable;
    private FileObserver fileObserver;
    private long lastModified;

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d(TAG, "Permission granted");
                } else {
                    Log.d(TAG, "Permission not granted");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_policy_settings, R.id.nav_alert_history)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        checkAndRequestPermissions();
        setupPeriodicFileCheck();
        setupFileObserver();
        createNotificationChannel();
    }

    private void checkAndRequestPermissions() {
        String permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted
            Log.d(TAG, "Permission already granted");
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            Log.d(TAG, "Showing permission rationale");
            showPermissionRationaleUI(permission);
        } else {
            // Request the permission
            requestPermissionLauncher.launch(permission);
        }
    }

    private void showPermissionRationaleUI(String permission) {
        // Show a dialog or other UI to explain why the permission is needed
        // and request the permission again after user acknowledgment
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("This app needs storage access to save settings. Please grant the required permission.")
                .setPositiveButton("OK", (dialog, which) -> {
                    requestPermissionLauncher.launch(permission);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    Log.d(TAG, "Permission request canceled by user");
                })
                .create()
                .show();
    }

    private void setupPeriodicFileCheck() {
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                checkFile();
                handler.postDelayed(this, 10000); // Repeat every 10 seconds
            }
        };
        handler.post(runnable);
    }

    private void setupFileObserver() {
        fileObserver = new FileObserver(FILE_PATH, FileObserver.MODIFY) {
            @Override
            public void onEvent(int event, String path) {
                if (path != null && path.equals("response.json")) {
                    Log.d(TAG, "File modified: " + path);
                    sendNotification();
                }
            }
        };
        fileObserver.startWatching();
    }

    private void checkFile() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            long lastModifiedTime = file.lastModified();
            if (lastModifiedTime == 0 || lastModifiedTime != lastModified) {
                lastModified = lastModifiedTime;
                Log.d(TAG, "File modified detected by periodic check");
                sendNotification();
            }
        }
    }

    private void sendNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_menu_alert_history)
                .setContentTitle("Privacy Violation Detected!")
                .setContentText("Please check details in Alert History.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "File Monitor Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for file monitoring notifications");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        fileObserver.stopWatching();
    }
}
