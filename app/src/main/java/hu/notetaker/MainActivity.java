package hu.notetaker;

import static com.google.android.gms.common.util.CollectionUtils.setOf;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import hu.notetaker.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hu.notetaker.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);

        binding.appBarMain.fab.setOnClickListener(view -> {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        });

        var navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        var navController = navHostFragment.getNavController();

        var appBarConfiguration = new AppBarConfiguration.Builder(setOf(
                R.id.nav_transform, R.id.nav_reflow, R.id.nav_slideshow, R.id.nav_settings))
                .setOpenableLayout(binding.drawerLayout)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        if (binding.navView != null) {
            NavigationUI.setupWithNavController(binding.navView, navController);
        }

        appBarConfiguration = new AppBarConfiguration.Builder(setOf(
                R.id.nav_transform, R.id.nav_reflow, R.id.nav_slideshow))
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        if (binding.appBarMain.contentMain.bottomNavView != null) {
            NavigationUI.setupWithNavController(binding.appBarMain.contentMain.bottomNavView, navController);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        var result = super.onCreateOptionsMenu(menu);
        // Using findViewById because NavigationView exists in different layout files
        // between w600dp and w1240dp
        var navView = (NavigationView) findViewById(R.id.nav_view);
        if (navView == null) {
            // The navigation drawer already has the items including the items in the overflow menu
            // We only inflate the overflow menu if the navigation drawer isn't visible
            getMenuInflater().inflate(R.menu.overflow, menu);
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_settings) {
            var navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_settings);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        var navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
