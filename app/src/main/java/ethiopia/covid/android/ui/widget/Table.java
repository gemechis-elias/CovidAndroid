package ethiopia.covid.android.ui.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Locale;

import ethiopia.covid.android.R;

import static ethiopia.covid.android.util.Utils.dpToPx;
import static ethiopia.covid.android.util.Utils.getCurrentTheme;

public class Table extends LinearLayout {

    private TableLayout fixedColumnTableLayout;
    private TableLayout scrollableTableLayout;
    private HorizontalScrollView scrollableTableContainer;

    private interface ApplyProperty {
        void apply(View item);
    }

    public interface OnTableRowClicked {
        void onClick(String identifier);
    }

    OnTableRowClicked onTableRowClickedCallback;

    public Table(Context context) {
        super(context);
    }

    public Table(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void populateTable(List<String> headers, List<List<String>> rowItems, boolean fixedColumn, int fixedColumnCount) {
        setOrientation(HORIZONTAL);

        fixedColumnTableLayout = new TableLayout(getContext());
        scrollableTableLayout = new TableLayout(getContext());
        scrollableTableContainer = new HorizontalScrollView(getContext());

        scrollableTableLayout.setStretchAllColumns(true);
        fixedColumnTableLayout.setStretchAllColumns(true);

        if (fixedColumn && fixedColumnCount > 0)
            addRows(fixedColumnTableLayout, null, generateTableRow(0, headers.subList(0 , fixedColumnCount), null));

        addRows(scrollableTableLayout, null, generateTableRow(0, headers.subList(fixedColumnCount , headers.size()), null));

        for (List<String> stat : rowItems) {
            addRows(fixedColumnTableLayout, stat.get(0), generateTableRow(1,headers.subList(0 , fixedColumnCount), stat.subList(0 , fixedColumnCount)));
            addRows(scrollableTableLayout, stat.get(0), generateTableRow(1,headers.subList(fixedColumnCount+1 , headers.size()), stat.subList(fixedColumnCount , stat.size())));
        }

        addView(fixedColumnTableLayout);
        scrollableTableContainer.addView(scrollableTableLayout);
        addView(scrollableTableContainer);

        LinearLayout.LayoutParams params = (LayoutParams) fixedColumnTableLayout.getLayoutParams();
        params.width = dpToPx(getContext() , 100);
        params.height = LayoutParams.MATCH_PARENT;
        fixedColumnTableLayout.setLayoutParams(params);

    }

    public void setOnTableRowClickedCallback(OnTableRowClicked onTableRowClicked) {
        this.onTableRowClickedCallback = onTableRowClicked;
    }

    private void addRows(TableLayout to, String identifier, TableRow ...rows) {
        for (TableRow row : rows) {
            row.setOnClickListener(v -> {
                if (onTableRowClickedCallback != null && identifier != null)
                    onTableRowClickedCallback.onClick(identifier);
            });
            to.addView(row);
        }
    }

    private void addMultipleViews(ViewGroup viewGroup, View... items){
        for (View item : items) viewGroup.addView(item);
    }

    private void applyToAllViews(ApplyProperty property, AppCompatTextView ...onto) {
        for(AppCompatTextView textview: onto) {
            property.apply(textview);
        }
    }

    public void clearTable() {
        fixedColumnTableLayout.removeAllViews();
        scrollableTableLayout.removeAllViews();
    }

    private TableRow generateTableRow (int position, List<String> headers, List<String> items) {

        if (position == 0) {
            TableRow headerRow = new TableRow(getContext());
            for (String header : headers) {
                AppCompatTextView textView = new AppCompatTextView(getContext());
                applyToAllViews((textview) -> {
                    ((AppCompatTextView) textview).setGravity(Gravity.CENTER);
                    ((AppCompatTextView) textview).setTextColor(ContextCompat.getColor(getContext(), getCurrentTheme(getContext()) == 0 ? R.color.black_0 : R.color.white_0));
                    ((AppCompatTextView) textview).setTypeface(null, Typeface.BOLD);
                    textview.setPadding(dpToPx(getContext(), 16) , dpToPx(getContext(), 12),
                            dpToPx(getContext(), 16), dpToPx(getContext(), 12));
                }, textView);
                textView.setText(header);
                headerRow.addView(textView);
            }
            headerRow.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey_1x));
            return headerRow;
        } else {
            TableRow itemRow = new TableRow(getContext());

            for (String content : items) {
                AppCompatTextView contentTextView = new AppCompatTextView(getContext());
                contentTextView.setText(String.format(Locale.US, "%s", content));

                applyToAllViews(
                        textview -> {
                            ((AppCompatTextView) textview).setGravity(Gravity.CENTER);
                            ((AppCompatTextView) textview).setTextColor(
                                    getCurrentTheme(getContext()) == 0 ?
                                            ContextCompat.getColor(getContext(), R.color.black_0) :
                                            ContextCompat.getColor(getContext(), R.color.white_0)
                            );
                            textview.setPadding(dpToPx(getContext(), 16), dpToPx(getContext(), 16),
                                    dpToPx(getContext(), 16), dpToPx(getContext(), 16));
                        },
                        contentTextView);

                addMultipleViews(itemRow, contentTextView);
            }
            return itemRow;
        }
    }
}