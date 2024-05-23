package com.example.myapplication;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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

public class SpinnerActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private TableLayout tableLayoutWords;
    private Spinner spinnerLetters;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinner);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("words");

        // Initialize DrawerLayout
        drawerLayout = findViewById(R.id.drawerLayout);

        // Initialize Toolbar with hamburger icon
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_menu_24);

        // Set OnClickListener for the hamburger icon
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the navigation drawer
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Initialize TableLayout
        tableLayoutWords = findViewById(R.id.tableLayoutWords);

        // Initialize the spinner for selecting letters
        spinnerLetters = findViewById(R.id.spinnerLetters);
        List<String> lettersList = new ArrayList<>();
        // Add Arabic letters from Alif to Yay
        for (char letter = 'أ'; letter <= 'ي'; letter++) {
            lettersList.add(String.valueOf(letter));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lettersList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLetters.setAdapter(adapter);
        spinnerLetters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLetter = lettersList.get(position);
                filterWordsByLetter(selectedLetter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing if nothing is selected
            }
        });

        // Set up navigation menu item click listener
        NavigationView navigationView = findViewById(R.id.menu_spinner);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_dictionary) {
                    // Handle dictionary menu item click
                    Intent searchIntent = new Intent(SpinnerActivity.this, SearchActivity.class);
                    startActivity(searchIntent);
                    drawerLayout.closeDrawers();
                    return true;
                } else if (itemId == R.id.menu_about) {
                    // Handle about menu item click
                    Intent aboutIntent = new Intent(SpinnerActivity.this, AboutActivity.class);
                    startActivity(aboutIntent);
                    drawerLayout.closeDrawers();
                    return true;
                } else if (itemId == R.id.menu_exit) {
                    // Show exit confirmation dialog
                    showExitConfirmationDialog();
                    return true;
                }
                return false;
            }
        });
    }

    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Close the app
                        finishAffinity();
                    }
                })
                .setNegativeButton(android.R.string.no, null) // Added negative button
                .setIcon(android.R.drawable.ic_dialog_alert).show();
    }


    private void filterWordsByLetter(String selectedLetter) {
        // Clear existing rows from the table
        tableLayoutWords.removeAllViews();

        // Retrieve data from Firebase and filter words based on the selected letter
        databaseReference.orderByChild("arabic_word").startAt(selectedLetter).endAt(selectedLetter + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot wordSnapshot : dataSnapshot.getChildren()) {
                    String arabicWord = wordSnapshot.child("arabic_word").getValue(String.class);
                    String urduMeaning = wordSnapshot.child("urdu_meaning").getValue(String.class);
                    String englishMeaning = wordSnapshot.child("english_meaning").getValue(String.class);
                    // Create a new row for each word
                    TableRow row = new TableRow(SpinnerActivity.this);
                    // Create TextViews to display data
                    TextView textViewEnglish = createTextView(englishMeaning, Gravity.START);
                    // English meaning
                    TextView textViewUrdu = createTextView(urduMeaning, Gravity.CENTER);
                    // Urdu meaning
                    TextView textViewArabic = createTextView(arabicWord, Gravity.RIGHT);
                    // Arabic word
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
                    row.addView(textViewEnglish);
                    row.addView(textViewUrdu);
                    row.addView(textViewArabic);
                    // Add the row to the table layout
                    tableLayoutWords.addView(row);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private TextView createTextView(String text, int gravity) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(50, 10, 50, 15);
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
        textView.setGravity(gravity);
        return textView;
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Word copied to clipboard", Toast.LENGTH_SHORT).show();
    }
}
