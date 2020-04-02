package com.yyy.djk.dropdownmenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.yyydjk.library.DeviceUtils;
import com.yyydjk.library.DropDownMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    DropDownMenu mDropDownMenu;
    TextView textView;

    private String[] headers = {"地區不限", "预算不限", "風格不限"};
    private Integer[] weights = {1, 1, 1};
    private List<View> popupViews = new ArrayList<>();

    private GridDropDownAdapter locationAdapter;
    private GridDropDownAdapter budgetAdapter;
    private MultiGridDropDownAdapter styleAdapter;

    private String[] locations = {"地區不限", "北部地區", "中部地區", "東部地區", "南部地區", "離島地區"};
    private String[] budgets = {"预算不限", "51-80萬", "81-100萬", "101-200萬", "201-300萬", "301-400萬", "401-500萬", "501萬以上"};
    private String[] styles = {"風格不限", "現代風", "混搭風", "北歐風", "工業風", "日式風", "古典風", "鄉村風", "其他"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        initView();
    }

    private void initView() {
        mDropDownMenu = findViewById(R.id.dropDownMenu);
        textView = findViewById(R.id.textView);

        mDropDownMenu.setPopupWindowHeight(DeviceUtils.getScreenSize(this).y - mDropDownMenu.dpTpPx(105));
        mDropDownMenu.setOnTabChangedListener(() -> {
            styleAdapter.resetList();
        });

        final View locationView = getLayoutInflater().inflate(R.layout.layout_grid_location, null);
        GridView locationGridView = locationView.findViewById(R.id.layoutGrid);
        locationAdapter = new GridDropDownAdapter(this, Arrays.asList(locations));
        locationGridView.setAdapter(locationAdapter);

        final View budgetView = getLayoutInflater().inflate(R.layout.layout_grid_budget, null);
        GridView budgetGridView = budgetView.findViewById(R.id.layoutGrid);
        budgetAdapter = new GridDropDownAdapter(this, Arrays.asList(budgets));
        budgetGridView.setAdapter(budgetAdapter);

        final View styleView = getLayoutInflater().inflate(R.layout.layout_grid_style, null);
        GridView styleGridView = styleView.findViewById(R.id.layoutGridMulti);
        styleAdapter = new MultiGridDropDownAdapter(this, Arrays.asList(styles));
        styleGridView.setAdapter(styleAdapter);
        TextView ok = styleView.findViewById(R.id.ok);
        ok.setOnClickListener(v -> {
            StringBuilder sb = new StringBuilder();
            List<String> list = styleAdapter.getConfirmList();
            for (int i = 0; i < list.size(); ++i) {
                if (sb.toString().isEmpty()) sb.append(list.get(i));
                else sb.append('、').append(list.get(i));
            }
            mDropDownMenu.setTabText(sb.toString());
            mDropDownMenu.closeMenu();
        });

        popupViews.add(locationView);
        popupViews.add(budgetView);
        popupViews.add(styleView);

        locationGridView.setOnItemClickListener((adapterView, view, position, id) -> {
            locationAdapter.setCheckItem(position);
            mDropDownMenu.setTabText(position == 0 ? headers[0] : locations[position]);
            mDropDownMenu.closeMenu();
        });
        budgetGridView.setOnItemClickListener((adapterView, view, position, id) -> {
            budgetAdapter.setCheckItem(position);
            mDropDownMenu.setTabText(position == 0 ? headers[1] : budgets[position]);
            mDropDownMenu.closeMenu();
        });
        styleGridView.setOnItemClickListener((adapterView, view, position, id) -> {
            styleAdapter.setCheckItem(position);
        });

        textView.setOnClickListener(view -> {
            Toast.makeText(this, "show text", Toast.LENGTH_SHORT).show();
        });

        mDropDownMenu.setDropDownMenu(Arrays.asList(headers), popupViews, Arrays.asList(weights));
    }

    @Override
    public void onBackPressed() {
        //退出activity前关闭菜单
        if (mDropDownMenu.isShowing()) {
            mDropDownMenu.closeMenu();
        } else {
            super.onBackPressed();
        }
    }
}
