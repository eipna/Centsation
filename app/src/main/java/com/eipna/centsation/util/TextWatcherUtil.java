package com.eipna.centsation.util;

import android.icu.text.NumberFormat;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.ParseException;
import java.util.Locale;

public class TextWatcherUtil implements TextWatcher {

    private final EditText EDIT_TEXT;
    private boolean isFormatting;

    public TextWatcherUtil(EditText editText) {
        this.EDIT_TEXT = editText;
        this.isFormatting = false;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // Not needed
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // Not needed
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (isFormatting) return;
        isFormatting = true;

        String originalString = editable.toString().replaceAll(",", "");
        if (originalString.isEmpty()) {
            isFormatting = false;
            return;
        }

        try {
            NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
            Number number = numberFormat.parse(originalString);
            String formattedString = numberFormat.format(number);

            EDIT_TEXT.setText(formattedString);
            EDIT_TEXT.setSelection(formattedString.length());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        isFormatting = false;
    }
}