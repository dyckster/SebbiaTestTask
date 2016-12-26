package com.example.dyckster.sebbiatesttask.utils.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.ParcelableSpan;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.example.dyckster.sebbiatesttask.R;


public class TextViewPlus extends TextView {

    private final StringBuilder resultText = new StringBuilder();
    private final StringBuilder tmpText = new StringBuilder();
    protected int rightPaddingHeight;
    protected int rightPaddingWidth;
    protected ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
        private CharSequence formattedString;

        @Override
        public boolean onPreDraw() {
            TextViewPlus tv = TextViewPlus.this;
            CharSequence text = tv.getText();
            if (formattedString != null && ((text == null || text.equals(formattedString.toString())))) {
                return true;
            }

            formattedString = fitText(text);
            setText(formattedString);

            return true;
        }
    };
    private float defaultTextSizePx;
    private boolean scales = true;

    public TextViewPlus(Context context) {
        super(context);
        init();
    }

    public TextViewPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null)
            readAttributes(context, attrs);
        init();
    }

    public TextViewPlus(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (attrs != null)
            readAttributes(context, attrs);
        init();
    }

    private void readAttributes(Context ctx, @NonNull AttributeSet attrs) {
        if (this.isInEditMode())
            return;

        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.Font);
        String customFontName = a.getString(R.styleable.Font_font);
        scales = a.getBoolean(R.styleable.Font_scales, false);

        if (TextUtils.isEmpty(customFontName) == false) {
            Typeface typeface = FontsManager.getTypeface(customFontName);
            setTypeface(typeface);
        } else {
            Typeface typeface = FontsManager.getDefaultTypeface();
            setTypeface(typeface);
        }
        a.recycle();

        TypedArray attributeName = ctx.obtainStyledAttributes(attrs, R.styleable.TextViewPlus);
        rightPaddingHeight = attributeName.getDimensionPixelSize(R.styleable.TextViewPlus_rightImagePaddingHeight, 0);
        rightPaddingWidth = attributeName.getDimensionPixelSize(R.styleable.TextViewPlus_rightImagePaddingWidth, 0);
        attributeName.recycle();
    }

    public void setRightPaddingHeight(int rightPaddingHeight) {
        this.rightPaddingHeight = rightPaddingHeight;
    }

    public void setRightPaddingWidth(int rightPaddingWidth) {
        this.rightPaddingWidth = rightPaddingWidth;
    }

    private void init() {
        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        defaultTextSizePx = getTextSize();
        scaleTextSize();
        if (rightPaddingHeight != 0 && rightPaddingWidth != 0)
            this.getViewTreeObserver().addOnPreDrawListener(preDrawListener);
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        defaultTextSizePx = getTextSize();
        scaleTextSize();
    }

    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(unit, size);
        defaultTextSizePx = getTextSize();
        scaleTextSize();
    }

    public void setTextSizeWithScale(float size, float scale) {
        super.setTextSize(TypedValue.COMPLEX_UNIT_PX, size * scale);
    }

    private void scaleTextSize() {
        if (scales && !isInEditMode()) {
            float scale = 1.0f;
            super.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultTextSizePx * scale);
        }
    }

    protected CharSequence fitText(CharSequence text) {
        resultText.delete(0, resultText.length());
        tmpText.delete(0, tmpText.length());
        String[] modifiedTextWords = text.toString().split("\\s|(?<=-)");
        Paint paint = this.getPaint();
        int paddedLines = rightPaddingHeight / this.getLineHeight();
        int lineNumber = 0;
        for (String word : modifiedTextWords) {
            if ((lineNumber <= paddedLines)
                    &&
                    (paint.measureText(tmpText.append(word), 0, tmpText.length()) > this.getWidth() - rightPaddingWidth)) {
                resultText.append('\n');
                lineNumber++;
                tmpText.delete(0, tmpText.length() - word.length());
            }
            tmpText.append(' ');
            resultText.append(word);
            resultText.append(' ');

        }
        resultText.deleteCharAt(resultText.length() - 1);
        return resultText;
    }

    private class SpanHolder {
        ParcelableSpan span;
        int start;
        int end;
        int flags;
        CharSequence text;

        private SpanHolder(ParcelableSpan span, int start, int end, int flags, CharSequence text) {
            this.span = span;
            this.start = start;
            this.end = end;
            this.flags = flags;
            this.text = text;
        }

    }

}
