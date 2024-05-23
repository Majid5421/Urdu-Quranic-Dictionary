package com.example.myapplication;

import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private TableLayout tableLayoutWords;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private List<Word> allWords;
    private int currentPage = 0;
    private int pageSize =10; // Number of items per page
    private TextView pageNumberTextView; // TextView to display the page number

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("words");

        // Initialize views
        tableLayoutWords = findViewById(R.id.tableLayoutWords);
        drawerLayout = findViewById(R.id.Drawerlayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.app_bar);
        pageNumberTextView = findViewById(R.id.textViewPageNumber); // Initialize the TextView for page number

        // Set toolbar as support action bar
        setSupportActionBar(toolbar);

        // Set hamburger icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_menu_24);

        // Retrieve data from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allWords = new ArrayList<>();
                for (DataSnapshot wordSnapshot : dataSnapshot.getChildren()) {
                    String arabicWord = wordSnapshot.child("arabic_word").getValue(String.class);
                    String urduMeaning = wordSnapshot.child("urdu_meaning").getValue(String.class);
                    String englishMeaning = wordSnapshot.child("english_meaning").getValue(String.class);
                    allWords.add(new Word(arabicWord, englishMeaning, urduMeaning));
                }
                displayWords(currentPage);
                updatePageNumber(); // Update the page number after retrieving data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SearchActivity.this, "Failed to retrieve data from Firebase", Toast.LENGTH_SHORT).show();
            }
        });

        // Set navigation item selected listener
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here
                int id = item.getItemId();

                if (id == R.id.menu_spinner) {
                    // Start SpinnerActivity
                    startActivity(new Intent(SearchActivity.this, SpinnerActivity.class));
                } else if (id == R.id.menu_about) {
                    // Handle About action
                    startActivity(new Intent(SearchActivity.this, AboutActivity.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (id == R.id.menu_exit) {
                    // Prompt user to confirm exit
                    showExitConfirmationDialog();
                }

                // Close the navigation drawer
                drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });

        // Handle search functionality
        handleSearch();
    }

    private void handleSearch() {
    }

    private void displayWords(int page) {
        // Clear existing rows from the table
        tableLayoutWords.removeAllViews();

        // Calculate start and end index for the current page
        int start = page * pageSize;
        int end = Math.min((page + 1) * pageSize, allWords.size());

        for (int i = start; i < end; i++) {
            Word word = allWords.get(i);
            // Create a new row for each word
            TableRow row = new TableRow(SearchActivity.this);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(layoutParams);

            // Create TextViews to display data
            TextView textViewEnglish = createTextView(word.getEnglishMeaning(), Gravity.START); // English meaning
            TextView textViewUrdu = createTextView(word.getUrduMeaning(), Gravity.CENTER); // Urdu meaning
            TextView textViewArabic = createTextView(word.getArabicWord(), Gravity.RIGHT); // Arabic word

            // Add long click listener to each TextView to copy the word
            textViewEnglish.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    copyToClipboard(textViewEnglish.getText().toString());
                    return true;
                }
            });
            textViewUrdu.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    copyToClipboard(textViewUrdu.getText().toString());
                    return true;
                }
            });
            textViewArabic.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    copyToClipboard(textViewArabic.getText().toString());
                    return true;
                }
            });

            // Add TextViews to the row
            row.addView(textViewEnglish); // English meaning
            row.addView(textViewUrdu); // Urdu meaning
            row.addView(textViewArabic); // Arabic word

            // Add the row to the table layout
            tableLayoutWords.addView(row);
        }
    }

    private TextView createTextView(String text, int gravity) {
        TextView textView = new TextView(SearchActivity.this);
        textView.setText(text);
        textView.setPadding(50, 10, 50, 15);
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
        textView.setGravity(gravity);// Set gravity
        textView.setTextSize(16);
        return textView;
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    displayWords(currentPage);
                } else {
                    filter(newText);
                }
                return false;
            }
        });
        return true;
    }

    private void filter(String query) {
        List<Word> filteredWords = new ArrayList<>();
        for (Word word : allWords) {
            if (word.getArabicWord().toLowerCase().contains(query.toLowerCase())) {
                filteredWords.add(word);
            }
        }
        displaySearchResults(filteredWords);
    }

    private void displaySearchResults(List<Word> searchResults) {
        // Clear existing rows from the table
        tableLayoutWords.removeAllViews();

        for (Word word : searchResults) {
            // Create a new row for each word
            TableRow row = new TableRow(SearchActivity.this);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(layoutParams);

            // Create TextViews to display data
            TextView textViewEnglish = createTextView(word.getEnglishMeaning(), Gravity.START); // English meaning
            TextView textViewUrdu = createTextView(word.getUrduMeaning(), Gravity.CENTER); // Urdu meaning
            TextView textViewArabic = createTextView(word.getArabicWord(), Gravity.RIGHT); // Arabic word

            // Add long click listener to each TextView to copy the word
            textViewEnglish.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    copyToClipboard(textViewEnglish.getText().toString());
                    return true;
                }
            });
            textViewUrdu.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    copyToClipboard(textViewUrdu.getText().toString());
                    return true;
                }
            });
            textViewArabic.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    copyToClipboard(textViewArabic.getText().toString());
                    return true;
                }
            });

            // Add TextViews to the row
            row.addView(textViewEnglish); // English meaning
            row.addView(textViewUrdu); // Urdu meaning
            row.addView(textViewArabic); // Arabic word

            // Add the row to the table layout
            tableLayoutWords.addView(row);
        }
    }

    private void search(String query) {
        if (query.isEmpty()) {
            displayWords(currentPage);
        } else {
            List<Word> searchResults = new ArrayList<>();
            for (Word word : allWords) {
                if (word.getArabicWord().toLowerCase().contains(query.toLowerCase())) {
                    searchResults.add(word);
                }
            }
            displaySearchResults(searchResults);
        }
    }

    // Load the previous page of data
    public void loadPreviousPage(View view) {
        if (currentPage > 0) {
            currentPage--;
            displayWords(currentPage);
            updatePageNumber(); // Update the page number after moving to the previous page
        }
    }

    // Load the next page of data
    public void loadNextPage(View view) {
        int totalPages = (int) Math.ceil((double) allWords.size() / pageSize);
        if (currentPage < totalPages - 1) {
            currentPage++;
            displayWords(currentPage);
            updatePageNumber(); // Update the page number after moving to the next page
        }
    }
    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit Confirmation")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Close the entire application and return to home screen
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finishAffinity(); // Close all activities
                        System.exit(0); // Close the app
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                })
                .show();
    }



    // Update the page number displayed on the screen
    private void updatePageNumber() {
        pageNumberTextView.setText("Page " + (currentPage + 1)); // Update the page number
    }
}
