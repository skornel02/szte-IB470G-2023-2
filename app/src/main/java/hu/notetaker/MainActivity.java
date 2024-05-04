package hu.notetaker;

import static com.google.android.gms.common.util.CollectionUtils.setOf;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.net.URL;

import hu.notetaker.databinding.ActivityMainBinding;
import hu.notetaker.databinding.NavHeaderMainBinding;
import hu.notetaker.service.UserService;
import kotlin.collections.SetsKt;

public class MainActivity extends AppCompatActivity {

    private Thread imageLoader;
    private boolean imageLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        var binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);

        var displayMetrics = getResources().getDisplayMetrics();
        var dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        if (dpWidth >= 600 && dpWidth < 1240) {
            var openMenuButton = binding.appBarMain.toolbar.findViewById(R.id.open_navbar);

            openMenuButton.setOnClickListener(view -> {
                binding.drawerLayout.open();
            });
        }

        binding.appBarMain.fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
            startActivity(intent);
        });

        var navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        var navController = navHostFragment.getNavController();

        var sideNavItems = SetsKt.setOf(
                R.id.nav_notes,
                R.id.nav_countnotes,
                R.id.nav_settings,
                R.id.nav_logout);
        var appBarConfiguration = new AppBarConfiguration.Builder(sideNavItems)
                .setOpenableLayout(binding.drawerLayout)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        if (binding.navView != null) {
            NavigationUI.setupWithNavController(binding.navView, navController);

            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {
                logout();
                return true;
            });
        }

        var bottomNavItems = SetsKt.setOf(
                R.id.nav_notes,
                R.id.nav_countnotes);
        var bottomNavigationConfiguration = new AppBarConfiguration.Builder(bottomNavItems)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, bottomNavigationConfiguration);
        if (binding.appBarMain.contentMain.bottomNavView != null) {
            NavigationUI.setupWithNavController(binding.appBarMain.contentMain.bottomNavView, navController);
        }

        var userOptional = UserService.getUser();

        if (!userOptional.isPresent()) {
            logout();
            return;
        }

        final var navigationView = binding.appBarMain.contentMain.navView != null
                ? binding.appBarMain.contentMain.navView
                : binding.navView;

        // Load user data
        if (navigationView != null) {
            userOptional.ifPresent(user -> {
                var navHeaderMainBinding = NavHeaderMainBinding.bind(navigationView.getHeaderView(0));

                navHeaderMainBinding.emailView.setText(user.getEmailAddress());

                imageLoader = new Thread(() -> {
                    try {
                        var gravatarUri = new URL(user.getAvatarUrl());
                        var bitmap = BitmapFactory.decodeStream(gravatarUri.openConnection().getInputStream());

                        Handler mainHandler = new Handler(getBaseContext().getMainLooper());

                        // This is your code
                        Runnable myRunnable = () -> {
                            navHeaderMainBinding.imageView.setImageBitmap(bitmap);
                            imageLoaded = true;
                        };
                        mainHandler.post(myRunnable);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        var result = super.onCreateOptionsMenu(menu);
        var navView = (NavigationView) findViewById(R.id.nav_view);
        if (navView == null) {
            getMenuInflater().inflate(R.menu.overflow, menu);
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        var navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        if (item.getItemId() == R.id.nav_settings) {
            handleSettings(navController);
            return true;

        } else if (item.getItemId() == R.id.nav_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        var navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    private void handleSettings(NavController navController) {
        navController.navigate(R.id.nav_settings);
    }

    private void logout() {
        var mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        var loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);

        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (imageLoader != null && !imageLoader.isAlive() && !imageLoaded) {
            imageLoader.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (imageLoader != null && imageLoader.isAlive()) {
            imageLoader.interrupt();
        }
    }
}
