package com.example.myapplication;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

public class WordSuggestionAdapter extends ArrayAdapter<Word> implements Filterable {

    private List<Word> words;
    private List<Word> filteredWords;

    public WordSuggestionAdapter(Context context, List<Word> words) {
        super(context, android.R.layout.simple_list_item_1, words);
        this.words = words;
        this.filteredWords = new ArrayList<>(words);
    }

    @Override
    public int getCount() {
        return filteredWords.size();
    }

    @Override
    public Word getItem(int position) {
        return filteredWords.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Word> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(words);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Word word : words) {
                        if (word.getArabicWord().toLowerCase().contains(filterPattern)) {
                            filteredList.add(word);
                        }
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredWords.clear();
                filteredWords.addAll((List<Word>) results.values);
                notifyDataSetChanged();
            }
        };
    }
}
