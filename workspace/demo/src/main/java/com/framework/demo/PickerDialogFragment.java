package com.framework.demo;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.widget.recycler.picker.AppCompatPickerView;
import com.framework.widget.recycler.picker.PickerLayoutManager;
import com.framework.core.ui.abs.UIDecorDialogFragment;
import com.navigation.floating.UIDialogFragmentCompat;

/**
 * @Author create by Zhengzelong on 2023-03-30
 * @Email : 171905184@qq.com
 * @Description :
 */
public class PickerDialogFragment extends UIDecorDialogFragment {
    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.dialog_picker;
    }

    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        super.onUICreated(savedInstanceState);
        UIDialogFragmentCompat
                .with(this)
                .setWidth(1.00f)
                .setHeight(0.40f)
                .setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM)
                .setAnimationStyle(R.style.UIAnimation_Dialog_InBottomOutBottom);
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
        PickerLayoutManager pickerLayoutManager;
        AppCompatPickerView appCompatPickerView;

        appCompatPickerView = this.requireViewById(R.id.appCompatPickerView1);
        appCompatPickerView.setAdapter(adapter1);

        appCompatPickerView = this.requireViewById(R.id.appCompatPickerView2);
        appCompatPickerView.setAdapter(adapter2);
        pickerLayoutManager = appCompatPickerView.requireLayoutManager();
        pickerLayoutManager.setLoopScrollEnabled(true);

        appCompatPickerView = this.requireViewById(R.id.appCompatPickerView3);
        appCompatPickerView.setAdapter(adapter3);
        pickerLayoutManager = appCompatPickerView.requireLayoutManager();
        pickerLayoutManager.setLoopScrollEnabled(true);
    }

    private final Adapter adapter1 = new Adapter("Y");
    private final Adapter adapter2 = new Adapter("M");
    private final Adapter adapter3 = new Adapter("D");

    static class Adapter extends AppCompatPickerView.Adapter<AppCompatPickerView.ViewHolder> {
        private final String suffix;

        public Adapter(@Nullable String suffix) {
            this.suffix = suffix;
        }

        @Override
        public AppCompatPickerView.ViewHolder onCreateGroupViewHolder(@NonNull ViewGroup parent, int itemViewType) {
            return new AppCompatPickerView.ViewHolder(this.inflate(R.layout.item_match_picker_layout, parent)) {
            };
        }

        @Override
        public void onBindGroupViewHolder(@NonNull AppCompatPickerView.ViewHolder holder, int groupPosition) {
            TextView textView;
            textView = holder.requireViewById(R.id.text);
            textView.setText(String.format("%s", 1970 + groupPosition));
            textView = holder.requireViewById(R.id.text2);
            textView.setText(String.format("%s", this.suffix));
        }

        @Override
        public int getGroupItemCount() {
            return 12;
        }
    }
}
