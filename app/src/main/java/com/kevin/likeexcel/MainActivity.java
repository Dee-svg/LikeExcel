package com.kevin.likeexcel;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kevin.likeexcel.bean.FloorData;
import com.kevin.likeexcel.bean.RoomData;
import com.kevin.likeexcel.bean.UnitData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.house_loading_map_title_tv)
    TextView houseLoadingMapTitleTv;
    @BindView(R.id.house_loading_map_title_line)
    ImageView houseLoadingMapTitleLine;
    @BindView(R.id.house_loading_map_title)
    LinearLayout houseLoadingMapTitle;
    @BindView(R.id.house_loading_map_left_recycler_view)
    RecyclerView houseLoadingMapLeftRecyclerView;
    @BindView(R.id.house_loading_map_top_recycler_view)
    RecyclerView houseLoadingMapTopRecyclerView;
    @BindView(R.id.house_loading_map_content_recycler_view)
    RecyclerView houseLoadingMapContentRecyclerView;

    private BaseQuickAdapter<UnitData, BaseViewHolder> topAdapter;
    private BaseQuickAdapter<FloorData, BaseViewHolder> leftAdapter;
    private BaseQuickAdapter<List<RoomData>, BaseViewHolder> rightAdapter;
    private int maxroom = 1;
    private int maxunit = 1;

    private Context context;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        handler=new Handler();
        context = this;
        bindAdapter();
        List<FloorData> floorList = DataTools.getFloorList();
        initRecyclerView(floorList);
    }


    private void bindAdapter() {
        topAdapter = new BaseQuickAdapter<UnitData, BaseViewHolder>(
                R.layout.activity_house_loading_map_top_item) {
            @Override
            protected void convert(BaseViewHolder helper, UnitData item) {
                final int bar_width = getChildItemWidth((ViewGroup) helper.itemView);
                final View itemView = helper.itemView;
                itemView.post(new Runnable() {
                    @Override
                    public void run() {
                        ViewGroup.LayoutParams lp = itemView.getLayoutParams();
                        lp.width = bar_width * maxroom;
                        itemView.setLayoutParams(lp);
                    }
                });
                String dyh = item.getUnitname().contains("单元") ? item.getUnitname() : item.getUnitname() + "单元";
                helper.setText(R.id.loading_map_top_item_tv, dyh);
            }
        };
        leftAdapter = new BaseQuickAdapter<FloorData, BaseViewHolder>(
                R.layout.activity_house_loading_map_left_item) {
            @Override
            protected void convert(BaseViewHolder helper, FloorData item) {
                helper.setText(R.id.loading_map_left_item_tv, item.getFloorname() + "层");
            }
        };
        rightAdapter = new BaseQuickAdapter<List<RoomData>, BaseViewHolder>(
                R.layout.activity_house_loading_map_content_item) {
            @Override
            protected void convert(BaseViewHolder helper,final List<RoomData> itemList) {
                RecyclerView contentItemRecyclerView = helper.getView(R.id.house_loading_map_content_item_recycler_view);
                contentItemRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(maxroom, StaggeredGridLayoutManager.VERTICAL));
                contentItemRecyclerView.setNestedScrollingEnabled(false);//禁止滑动
                contentItemRecyclerView.addItemDecoration(new ItemDecoration(context, ItemDecoration.DividerMode.VERTICAL, false,R.drawable.divider_line_white));
                contentItemRecyclerView.setAdapter(new BaseQuickAdapter<RoomData, BaseViewHolder>(
                        R.layout.activity_house_loading_map_content_item_item, itemList) {
                    @Override
                    protected void convert(BaseViewHolder helper, final RoomData item) {
                        helper.setText(R.id.loading_map_content_item_item_tv, item.getRoomname());
                        helper.setOnClickListener(R.id.loading_map_content_item_item_tv, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (item.getRoomname() == null || item.getRoomname().equals("")) {
                                    Toast.makeText(context, "空房", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, item.getRoomname(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        };
        houseLoadingMapTopRecyclerView.addItemDecoration(new ItemDecoration(context,R.drawable.divider_line_white));

        houseLoadingMapLeftRecyclerView.addItemDecoration(
                new ItemDecoration(context, ItemDecoration.DividerMode.HORIZONTAL, false,R.drawable.divider_line));
        houseLoadingMapContentRecyclerView.addItemDecoration(
                new ItemDecoration(context, ItemDecoration.DividerMode.HORIZONTAL, false,R.drawable.divider_line_white));

        houseLoadingMapTopRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(10, StaggeredGridLayoutManager.VERTICAL));
        houseLoadingMapContentRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(10, StaggeredGridLayoutManager.VERTICAL));

        houseLoadingMapTopRecyclerView.setAdapter(topAdapter);

        houseLoadingMapLeftRecyclerView.setAdapter(leftAdapter);
        houseLoadingMapContentRecyclerView.setAdapter(rightAdapter);

        houseLoadingMapLeftRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, final int dx,final int dy) {
                super.onScrolled(recyclerView, dx, dy);
                /*dx : 水平滚动距离
                dy : 垂直滚动距离

                dx > 0 时为手指向左滚动,列表滚动显示右面的内容
                dx < 0 时为手指向右滚动,列表滚动显示左面的内容
                dy > 0 时为手指向上滚动,列表滚动显示下面的内容
                dy < 0 时为手指向下滚动,列表滚动显示上面的内容*/
                if (recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            houseLoadingMapContentRecyclerView.scrollBy(dx, dy);
                        }
                    });
                }
            }
        });
        houseLoadingMapContentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView,final int dx,final int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            houseLoadingMapLeftRecyclerView.scrollBy(dx, dy);
                        }
                    });
                }
            }
        });
    }

    private void initRecyclerView(List<FloorData> floorList) {
        //示例 解析数据 根据实际情况解析
        List<UnitData> top = new ArrayList<>();
        List<FloorData> left ;
        List<List<RoomData>> right = new ArrayList<>();
        //获取左侧标题
        left = floorList;
        Collections.sort(left, new Comparator<FloorData>() {

            @Override
            public int compare(FloorData o1, FloorData o2) {
                //该方法的返回值0代表相等，1表示大于，-1表示小于
                int xh1 = isNumber(o1.getFloorname()) ? Integer.parseInt(o1.getFloorname()) : 0;
                int xh2 = isNumber(o2.getFloorname()) ? Integer.parseInt(o2.getFloorname()) : 0;
                return xh2 - xh1;
            }
        });
        //获取顶部标题
        for (int i = 0; i < left.size(); i++) {
            FloorData lc = left.get(i);
            //检测 没有的楼层并添加
            for (int j = 0; j < lc.getUnitList().size(); j++) {
                UnitData dy = lc.getUnitList().get(j);
                if (chickUnitList(dy, top) == -1) {
                    top.add(dy);
                }
            }
        }
        Collections.sort(top, new Comparator<UnitData>() {

            @Override
            public int compare(UnitData o1, UnitData o2) {
                //该方法的返回值0代表相等，1表示大于，-1表示小于
                int xh1 = isNumber(o1.getUnitname()) ? Integer.parseInt(o1.getUnitname()) : 0;
                int xh2 = isNumber(o2.getUnitname()) ? Integer.parseInt(o2.getUnitname()) : 0;
                return xh1 - xh2;
            }
        });

        //获取房间
        for (int i = 0; i < left.size(); i++) {
            FloorData floorData = left.get(i);
            List<UnitData> unitList = floorData.getUnitList();
            if (maxunit < unitList.size()) {
                maxunit = unitList.size();
            }

            if(top.size()>unitList.size()){
                //检测单元并添加空房间
                for (int j = 0; j < top.size(); j++) {
                    UnitData data =top.get(j);
                    if (chickUnitList(data, unitList) == -1) {
                        data.setRoomList(new ArrayList<RoomData>());
                        unitList.add(data);
                    }
                }
            }
            List<List<RoomData>> rightfjlist = new ArrayList<>();
            for (int j = 0; j < unitList.size(); j++) {
                UnitData dateInfo = unitList.get(j);
                List<RoomData> roomList = dateInfo.getRoomList();
                //最大房间数
                if (maxroom < roomList.size()) {
                    maxroom = roomList.size();
                }
                rightfjlist.add(roomList);
            }
            right.addAll(rightfjlist);
        }
       //检测 房间 添加空房间
        for (int i = 0; i < right.size(); i++) {
            List<RoomData> roomList = right.get(i);
            int fjsize = roomList.size();
            if (fjsize < maxroom) {
                for (int j = 0; j < maxroom - fjsize; j++) {
                    RoomData roomData = new RoomData();
                    roomData.setRoomname("");
                    roomList.add(roomData);
                }
                right.remove(i);
                right.add(i, roomList);
            }
        }

        houseLoadingMapTopRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(top.size(), StaggeredGridLayoutManager.VERTICAL));
        houseLoadingMapContentRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(top.size(), StaggeredGridLayoutManager.VERTICAL));

        topAdapter.replaceData(top);
        leftAdapter.replaceData(left);
        rightAdapter.replaceData(right);

        houseLoadingMapTitle.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams lp = houseLoadingMapTitle.getLayoutParams();
                lp.width = houseLoadingMapLeftRecyclerView.getWidth();
                lp.height = houseLoadingMapTopRecyclerView.getHeight();
                houseLoadingMapTitle.setLayoutParams(lp);
                houseLoadingMapTitleTv.setText("单元");
                houseLoadingMapTitleLine.setImageResource(R.drawable.divider_line);
            }
        });
    }

    private int chickUnitList(UnitData unitData, List<UnitData> dateInfoList) {
        String unitname = unitData.getUnitname();
        for (int i = 0; i < dateInfoList.size(); i++) {
            UnitData flooritem = dateInfoList.get(i);
            if (unitname.equals(flooritem.getUnitname())) {
                return i;
            }
        }
        return -1;
    }

    private int getChildItemWidth(ViewGroup root) {
        BaseViewHolder itemviewHolder = new BaseViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.activity_house_loading_map_content_item_item, root, false));
        final View itemViewitem = itemviewHolder.itemView;
        ViewGroup.LayoutParams lp = itemViewitem.getLayoutParams();
        return lp.width;
    }

    /**
     * 判断string 是否为数字
     * double AppBarStateChangeListener = -19162431.1254
     * 当数字位数很长时，系统会自动转为科学计数法。所以aa=-1.91624311254E7.
     *
     * @param numstr 待验证文本
     * @return {@code true}: 为数字<br>{@code false}: 不为数字
     */
    public static boolean isNumber(String numstr) {
        String numRegex = "-?[0-9]+\\.?[0-9]*";
        return !JsonUtils.isNullString(numstr) && Pattern.matches(numRegex, numstr);
    }
}
