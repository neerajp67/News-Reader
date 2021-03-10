package com.adverse.newsreader.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.adverse.newsreader.R;
import com.adverse.newsreader.fragment.FragmentBusiness;
import com.adverse.newsreader.fragment.FragmentDateSearch;
import com.adverse.newsreader.fragment.FragmentEntertainment;
import com.adverse.newsreader.fragment.FragmentHealthFitness;
import com.adverse.newsreader.fragment.FragmentHome;
import com.adverse.newsreader.fragment.FragmentKeywordSearch;
import com.adverse.newsreader.fragment.FragmentSpeechSearch;
import com.adverse.newsreader.fragment.FragmentSports;
import com.adverse.newsreader.fragment.FragmentTechnology;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    androidx.appcompat.widget.Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private int mYear, mMonth, mDay;
    private static final int REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                return itemSelected(item);
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentHome()).commit();
            navigationView.setCheckedItem(R.id.item1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);

        MenuItem searchViewItem = menu.findItem(R.id.search_keyword);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                FragmentManager fragmentManager = getSupportFragmentManager();

                final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                final FragmentKeywordSearch myFragment = new FragmentKeywordSearch();

                Log.i("test date", searchView.getQuery().toString());
                Bundle b = new Bundle();
                b.putString("query", searchView.getQuery().toString());
                myFragment.setArguments(b);
                fragmentTransaction.replace(R.id.fragment_container, myFragment).commit();
                searchView.onActionViewCollapsed();
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentKeywordSearch()).commit();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {

            case R.id.search_date:
//                Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                if (year > mYear) {
                                    Toast.makeText(MainActivity.this, "Can not select future date", Toast.LENGTH_SHORT).show();
                                } else if (monthOfYear > mMonth && year >= mYear) {
                                    Toast.makeText(MainActivity.this, "Can not select future date", Toast.LENGTH_SHORT).show();
                                } else if (dayOfMonth > mDay && monthOfYear >= mMonth && year >= mYear) {
                                    Toast.makeText(MainActivity.this, "Can not select future date", Toast.LENGTH_SHORT).show();
                                } else {
                                    //monthOfYear += 1;
                                    String currentDate = mYear + "-" + (mMonth + 1) + "-" + mDay;
                                    String dateSearchFrom = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                    Log.i("currentDate", currentDate);
                                    FragmentManager fragmentManager = getSupportFragmentManager();

                                    final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    final FragmentDateSearch myFragment = new FragmentDateSearch();
                                    Log.i("dateSearchFrom", dateSearchFrom);
                                    Bundle b = new Bundle();
                                    b.putString("dateUser", dateSearchFrom);
                                    myFragment.setArguments(b);
                                    fragmentTransaction.replace(R.id.fragment_container, myFragment).commit();
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                return true;

            case R.id.search_voice:
                startVoiceRecognitionActivity();
                return true;

            default:
                return false;
        }
    }

    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice searching..");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // Populate the wordsList with the String values the recognition engine thought it heard
            assert data != null;
            final ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (!matches.isEmpty()) {
                String speechQuery = matches.get(0);
                Log.i("voice searchL: ", speechQuery);

                FragmentManager fragmentManager = getSupportFragmentManager();

                final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                final FragmentSpeechSearch myFragment = new FragmentSpeechSearch();
                Bundle b = new Bundle();
                b.putString("speechQuery", speechQuery);
                myFragment.setArguments(b);
                fragmentTransaction.replace(R.id.fragment_container, myFragment).commit();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public boolean itemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentHome()).commit();
                break;
            case R.id.item2:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentSports()).commit();
                break;
            case R.id.item3:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentTechnology()).commit();
                break;
            case R.id.item4:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentEntertainment()).commit();
                break;
            case R.id.item5:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentBusiness()).commit();
                break;
            case R.id.item6:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentHealthFitness()).commit();
                break;
            case R.id.test1:
                ShareCompat.IntentBuilder.from(MainActivity.this)
                        .setType("text/plain")
                        .setChooserTitle("News Reader")
                        .setText("code for News Reader app is available at https://github.com/neerajp67/News-Reader\nstay connected  with us on https://github.com/neerajp67/ for more exiting projects.. ")
                        .startChooser();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}