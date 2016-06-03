package com.linwoain.storytelling.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.linwoain.library.LViewHelper;
import com.linwoain.storytelling.R;

/**
 * create by wuxuejian on 2016/3/29
 */
public class CommonListView extends LinearLayout
    implements SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener,
    AdapterView.OnItemClickListener {

  private int lastVisiableIndex;

  private View footView;
  /**
   * 是否已经没有更多，若为true，则loadMore事件不再调用
   */
  private boolean noMore;

  public CommonListView(Context context) {
    super(context);
    init();
  }

  public CommonListView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  SwipeRefreshLayout lin_swipe;

  ListView lin_listview;

  private View lin_reload;
  private View lin_loading;

  void init() {

    if (isInEditMode()) {
      return;
    }

    View.inflate(getContext(), R.layout.list_view_common_layout, this);

    lin_swipe = (SwipeRefreshLayout) findViewById(R.id.lin_swipe);
    lin_listview = (ListView) findViewById(R.id.lin_listview);
    lin_reload = findViewById(R.id.lin_reload);
    lin_loading = findViewById(R.id.lin_loading);

    lin_swipe.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE);
    lin_swipe.setOnRefreshListener(this);
    lin_listview.setOnScrollListener(this);

    footView = LViewHelper.getView(R.layout.foot_view, getContext());
    lin_loading = footView.findViewById(R.id.lin_loading);
    lin_reload = footView.findViewById(R.id.lin_reload);
    lin_listview.addFooterView(footView);
    lin_listview.setOnItemClickListener(this);
  }

  /**
   * 加载更多时失败
   */
  public void setLoadMoreError() {
    lin_loading.setVisibility(GONE);
    lin_reload.setVisibility(VISIBLE);
    footView.setOnClickListener(footerClickedListener);
  }

  private OnClickListener footerClickedListener = new OnClickListener() {
    @Override public void onClick(View v) {
      showLoadingMore();
      v.setOnClickListener(null);//显示加载更多时，使footview的点击事件不再生效
    }
  };

  private void showLoadingMore() {

    callBack.onLoadMore(this);
    lin_loading.setVisibility(VISIBLE);
    lin_reload.setVisibility(GONE);
  }

  public ListView getListView() {
    return lin_listview;
  }

  public void setAdapter(ListAdapter adapter) {
    lin_listview.setAdapter(adapter);
  }

  /**
   * 显示刷新中动画
   */
  public void setShowRefresh() {
    lin_swipe.post(new Runnable() {
      @Override public void run() {
        lin_swipe.setRefreshing(true);
      }
    });
  }

  @Override public void onRefresh() {
    if (callBack != null) {
      callBack.onRefresh(this);
    }
  }

  @Override public void onScrollStateChanged(AbsListView view, int scrollState) {
    ListAdapter adapter = lin_listview.getAdapter();

    if (adapter != null && !noMore && callBack != null && scrollState == SCROLL_STATE_IDLE &&
        lastVisiableIndex == adapter.getCount()) {
      showLoadingMore();
    }
  }

  public void setNoMore() {
    lin_listview.removeFooterView(footView);
    noMore = true;
  }

  @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
      int totalItemCount) {
    lastVisiableIndex = firstVisibleItem + visibleItemCount;
    if (heightChangedCallBack != null) {
      View childAt = view.getChildAt(0);
      if (childAt == null) {
        return;
      }
      int top = childAt.getTop();
      heightChangedCallBack.onScrollHeightChanged(Math.abs(top), firstVisibleItem, this);
    }
  }

  private RefreshCallBack callBack;

  public void setRefreshCallBack(RefreshCallBack callBack) {
    this.callBack = callBack;
  }

  public void addHeaderView(View headerView) {
    lin_listview.addHeaderView(headerView);
    headerAdd = true;
  }

  private boolean headerAdd;

  public void setRefreshing(boolean b) {
    lin_swipe.setRefreshing(b);
  }

  @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    if (itemClickCallBack != null) {
      if (headerAdd) {
        position--;
      }
      itemClickCallBack.onItemClick(position, this);
    }
  }

  public interface RefreshCallBack {
    void onRefresh(CommonListView view);

    void onLoadMore(CommonListView view);
  }

  public interface ScrollHeightChangedCallBack {
    void onScrollHeightChanged(int scrollHeight, int firstVisibleItem, CommonListView view);
  }

  public interface OnItemClickCallBack {
    void onItemClick(int position, CommonListView view);
  }

  private OnItemClickCallBack itemClickCallBack;
  private ScrollHeightChangedCallBack heightChangedCallBack;

  public void setItemClickCallBack(OnItemClickCallBack itemClickCallBack) {
    this.itemClickCallBack = itemClickCallBack;
  }

  public void setHeightChangedCallBack(ScrollHeightChangedCallBack heightChangedCallBack) {
    this.heightChangedCallBack = heightChangedCallBack;
  }
}
