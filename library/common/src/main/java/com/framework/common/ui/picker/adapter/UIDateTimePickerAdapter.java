package com.framework.common.ui.picker.adapter;

import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.common.R;
import com.framework.common.util.DateUtils;
import com.framework.widget.recycler.picker.AppCompatPickerView;
import com.framework.core.compat.UIRes;

import java.util.Calendar;

/**
 * @Author create by Zhengzelong on 2023-04-06
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UIDateTimePickerAdapter extends UIPickerAdapter<UIPickerViewHolder> {
    private int field;
    private int itemCount;
    private long timestamp;
    private Calendar calendar;

    public UIDateTimePickerAdapter() {
        this(null);
    }

    public UIDateTimePickerAdapter(@Nullable AppCompatPickerView upstream) {
        super(upstream);
    }

    @Nullable
    @Override
    public UIPickerViewHolder onCreateEmptyViewHolder(@NonNull ViewGroup parent, int itemViewType) {
        return new UIPickerViewHolder(this.inflate(R.layout.ui_item_picker_layout, parent));
    }

    @Nullable
    @Override
    public UIPickerViewHolder onCreateGroupViewHolder(@NonNull ViewGroup parent, int itemViewType) {
        return new UIPickerViewHolder(this.inflate(R.layout.ui_item_picker_layout, parent));
    }

    @Override
    public void onBindEmptyViewHolder(@NonNull UIPickerViewHolder holder, int position) {
        final TextView textView = holder.requireViewById(android.R.id.text1);
        textView.setText(null);
    }

    @Override
    public void onBindGroupViewHolder(@NonNull UIPickerViewHolder holder, int groupPosition) {
        final int field = this.field;
        final String suffix;
        if (Calendar.YEAR == field) {
            suffix = UIRes.getString(R.string.uiYear);
        } else if (Calendar.MONTH == field) {
            suffix = UIRes.getString(R.string.uiMonth);
        } else if (Calendar.DAY_OF_MONTH == field) {
            suffix = UIRes.getString(R.string.uiDayOfMonth);
        } else {
            throw new IllegalStateException("ERROR");
        }
        final TextView textView = holder.requireViewById(android.R.id.text1);
        textView.setText(String.format("%s%s", groupPosition + 1, suffix));
    }

    @Override
    public int getEmptyItemCount() {
        return 1;
    }

    @Override
    public int getGroupItemCount() {
        return this.itemCount;
    }

    public int getField() {
        return this.field;
    }

    public void setField(int field) {
        this.field = field;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public final void setItemCount(int itemCount) {
        this.setItemCount(itemCount, RecyclerView.NO_POSITION);
    }

    public void setItemCount(int itemCount, int defPosition) {
        this.itemCount = itemCount;
        this.notifyDataSetChanged();
        if (defPosition == RecyclerView.NO_POSITION) {
            return;
        }
        final AppCompatPickerView thisV = this.getRecyclerView();
        if (thisV != null) {
            thisV.stopScroll();
            thisV.setCurrentPosition(defPosition, false);
        }
    }

    @NonNull
    public final Calendar getCalendar() {
        if (this.calendar == null) {
            this.calendar = Calendar.getInstance();
        }
        return this.calendar;
    }

    public final int getCurrentValueInt() {
        final int currentPosition = this.getCurrentPosition();
        if (currentPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION;
        }
        return currentPosition + 1;
    }

    public final int getUpstreamValueInt() {
        final UIDateTimePickerAdapter upstreamAd = this.getUpstreamAd();
        if (upstreamAd == null) {
            return RecyclerView.NO_POSITION;
        }
        return upstreamAd.getCurrentValueInt();
    }

    @Override
    public void onUpstreamPositionChanged(int upstreamPosition) {
        final long timestamp = this.timestamp;
        final int field = this.field;
        final int defPos = this.computeDefPosition(field, timestamp);
        if (!this.hasUpstream()) {
            // year/month/dayOfMonth
            this.setItemCount(this.itemCount, defPos);
            this.timestamp = -1L;
            return;
        }
        if (upstreamPosition == RecyclerView.NO_POSITION) {
            this.setItemCount(0);
            return;
        }
        if (Calendar.MONTH == field) {
            // month
            this.setItemCount(12, defPos);
        } else if (Calendar.DAY_OF_MONTH == field) {
            final Calendar calendar = this.getCalendar();
            calendar.setTimeInMillis(System.currentTimeMillis());
            // dayOfMonth
            final UIDateTimePickerAdapter upstreamAd = this.requireUpstreamAd();
            int year = upstreamAd.getUpstreamValueInt();
            if (year == RecyclerView.NO_POSITION) {
                year = calendar.get(Calendar.YEAR);
            }
            int month = upstreamPosition;
            if (month == RecyclerView.NO_POSITION) {
                month = calendar.get(Calendar.MONTH);
            }
            calendar.set(year, month, 1);
            final int daysOfMonth;
            daysOfMonth = this.calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            this.setItemCount(daysOfMonth, defPos);
        } else {
            throw new IllegalStateException("ERROR");
        }
        this.timestamp = -1L;
    }

    protected int computeDefPosition(int field, long timestamp) {
        if (timestamp == -1L) {
            return RecyclerView.NO_POSITION;
        }
        try {
            // yyyy-MM-dd
            final String timestampStr = DateUtils.formatTimeMillis(timestamp);
            final String[] timestampArr = timestampStr.split("-");
            final int index;
            if (Calendar.YEAR == field) {
                index = 0;
            } else if (Calendar.MONTH == field) {
                index = 1;
            } else if (Calendar.DAY_OF_MONTH == field) {
                index = 2;
            } else {
                throw new IllegalStateException("ERROR");
            }
            return Integer.parseInt(timestampArr[index]) - 1;
        } catch (@NonNull Exception e) {
            return RecyclerView.NO_POSITION;
        }
    }
}
