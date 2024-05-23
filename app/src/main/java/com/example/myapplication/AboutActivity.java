package com.example.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Find TextViews by their IDs
        TextView textViewTitle = findViewById(R.id.textViewTitle);
        TextView textViewDescription = findViewById(R.id.textViewDescription);

        // Set the title text
        textViewTitle.setText("About My App");

        // Set the description text
        textViewDescription.setText("About Our App:\n\n" +
                "Welcome to our language learning application! Our app is designed to help users expand their vocabulary in multiple languages effortlessly. Whether you're looking to enhance your understanding of  Arabic, Urdu, English, or all three, our comprehensive tool provides you with an extensive database of words and their meanings.\n\n" +
                "Key Features:\n\n" +
                "Intuitive Search: Easily search for words in Arabic, Urdu, or English using our user-friendly search feature. With real-time filtering, finding the words you need is quick and convenient.\n\n" +
                "Interactive Spinner: Explore words alphabetically with our interactive spinner. Select a letter to filter words by starting with the chosen letter, allowing for a structured learning experience.\n\n" +
                "Clipboard Integration: Copy words and their meanings to your device's clipboard with a simple long-press gesture. This feature makes it easy to save and utilize new vocabulary in your studies or everyday conversations.\n\n" +
                "Navigation Drawer: Seamlessly navigate between different sections of the app using the integrated navigation drawer. Access additional functionalities such as accessing the spinner, learning about the app, and gracefully exiting the application.\n\n" +
                "Multi-Language Support: Learn words in Arabic, Urdu, and English simultaneously. Our app supports multiple languages, enabling users to broaden their language skills effortlessly.\n\n" +
                "How to Use:\n\n" +
                "Start by exploring words using the interactive spinner or the search feature.\n\n" +
                "Long-press on any word to copy it to your device's clipboard for easy reference.\n\n" +
                "Utilize the navigation drawer to access different sections of the app and learn more about its features.\n\n" +
                "Download our app today and embark on a journey to expand your language proficiency like never before!");
    }
}
