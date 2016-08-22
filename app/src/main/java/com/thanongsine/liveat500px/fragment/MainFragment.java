package com.thanongsine.liveat500px.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.thanongsine.liveat500px.R;
import com.thanongsine.liveat500px.adapter.PhotoListAdapter;
import com.thanongsine.liveat500px.dao.PhotoItemCollectionDao;
import com.thanongsine.liveat500px.manager.PhotoListManager;
import com.thanongsine.liveat500px.manager.http.HttpManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nuuneoi on 11/16/2014.
 */
public class MainFragment extends Fragment {

    /*****************
     * Variables Zone *
     *****************/

    ListView listView;
    PhotoListAdapter listAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    PhotoListManager photoListManager;
    Button btnNewPhotos;
    boolean isLoadingMore;

    /************
     * Function Zone*
     ************/

    public MainFragment() {
        super();
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize Fragment level's variables
        photoListManager = new PhotoListManager();

        if (savedInstanceState != null){
            onRestoreInstanceState(savedInstanceState); // Restore Instance state
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initInstances(rootView);
        return rootView;
    }

    private void initInstances(View rootView) {
        // init instance with rootView.findViewById here
        //setRetainInstance(true);

        isLoadingMore = false;

        btnNewPhotos = (Button) rootView.findViewById(R.id.btn_new_photos);

        btnNewPhotos.setOnClickListener(btnClickListener);

        listView = (ListView) rootView.findViewById(R.id.list_view);
        listAdapter = new PhotoListAdapter();
        listView.setAdapter(listAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout)
                rootView.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(pullToRefreshListener);

        listView.setOnScrollListener(listViewScrollListener);

        refreshData();

    }

    private void refreshData() {
        if (photoListManager.getCount() == 0) {
            reloadData();
        } else {
            reloadDataNewer();

        }
    }

    private void reloadDataNewer() {
        int maxId = photoListManager.getMaximumId();
        Call<PhotoItemCollectionDao> call = HttpManager.getInstance()
                .getService()
                .loadPhotoListAfterId(maxId);
        call.enqueue(new PhotoListLoadCallback(PhotoListLoadCallback.MODE_RELOAD_NEWER));
    }

    private void reloadData() {
        Call<PhotoItemCollectionDao> call = HttpManager
                .getInstance()
                .getService()
                .loadPhotoList();
        call.enqueue(new PhotoListLoadCallback(PhotoListLoadCallback.MODE_RELOAD));
    }

    private void loadMoreData() {
        if (isLoadingMore)
            return;
        isLoadingMore = true;

        int minId = photoListManager.getMaximumId();
        Call<PhotoItemCollectionDao> call = HttpManager.getInstance()
                .getService()
                .loadPhotoListBeforeId(minId);
        call.enqueue(new PhotoListLoadCallback(PhotoListLoadCallback.MODE_LOAD_MORE));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save Instant State here
        //TODO: Save PhotoListManager to outSave
    }

    private void onRestoreInstanceState(Bundle savedRestoreInstanceState){
        //Restore Instance state here

    }

    /*
     * Restore Instance State Here
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void showButtonNewPhotos() {
        btnNewPhotos.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(
                Contextor.getInstance().getContext(),
                R.anim.zoom_fade_in
        );
        btnNewPhotos.startAnimation(anim);

    }

    private void hideButtonNewPhotos() {
        btnNewPhotos.setVisibility(View.GONE);
        Animation anim = AnimationUtils.loadAnimation(
                Contextor.getInstance().getContext(),
                R.anim.zoom_fade_out
        );
        btnNewPhotos.startAnimation(anim);
    }

    private void showToast(String text){
        Toast.makeText(
                Contextor.getInstance().getContext(),
                text,
                Toast.LENGTH_SHORT)
                .show();
    }

    /*****************
     * Listener Zone *
     *****************/

    final View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == btnNewPhotos){
                listView.smoothScrollToPosition(0);
                hideButtonNewPhotos();
            }
        }
    };



    final SwipeRefreshLayout.OnRefreshListener pullToRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refreshData();
        }
    };

    final AbsListView.OnScrollListener listViewScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView absListView,
                                         int scrollSate) {

        }

        @Override
        public void onScroll(AbsListView absListView,
                             int firstVisibleItem,
                             int visibleItemCount,
                             int totalItemCount) {
            if (absListView == listView){
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0);
                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    if (photoListManager.getCount() > 0) {
                        //Load More
                        loadMoreData();
                    }
                }
            }
        }
    };


    /*****************
     * Inner Class Zone *
     *****************/

    class PhotoListLoadCallback implements Callback<PhotoItemCollectionDao> {

        public static final int MODE_RELOAD = 1;
        public static final int MODE_RELOAD_NEWER = 2;
        public static final int MODE_LOAD_MORE = 3;

        int mode;

        public PhotoListLoadCallback(int mode) {
            this.mode = mode;
        }

        @Override
        public void onResponse(Call<PhotoItemCollectionDao> call,
                               Response<PhotoItemCollectionDao> response) {
            swipeRefreshLayout.setRefreshing(false);
            if (response.isSuccessful()) {
                PhotoItemCollectionDao dao = response.body();

                int firstVisiblePosition = listView.getFirstVisiblePosition();
                View c = listView.getChildAt(0);
                int top = 0;
                if (c == null) {
                    top = 0;
                } else {
                    c.getTop();
                }
                if (mode == MODE_RELOAD_NEWER) {
                    photoListManager.insertDaoAtTopPosition(dao);
                } else if (mode == MODE_LOAD_MORE) {
                    photoListManager.appendDaoAtBottomPosition(dao);
                    clearLoadingMoreFlagIfCapable(mode);

                } else {
                    photoListManager.setDao(dao);
                }

                photoListManager.insertDaoAtTopPosition(dao);
                listAdapter.setDao(photoListManager.getDao());
                listAdapter.notifyDataSetChanged();

                if (mode == MODE_RELOAD_NEWER) {
                    // Maintain Scroll Position
                    int additionalSize =
                            (dao != null && dao.getData() != null)
                                    ? dao.getData().size() : 0;
                    listAdapter.increaseLastPosition(additionalSize);
                    listView.setSelectionFromTop(firstVisiblePosition + additionalSize,
                            top);
                    //Check if there are new data show button new photos
                    if (additionalSize > 0) {
                        showButtonNewPhotos();
                    }
                } else {

                }

                //TODO: Toast na
                showToast("Load Completed");
            } else {
                //Handle
                if (mode == MODE_LOAD_MORE) {
                    clearLoadingMoreFlagIfCapable(mode);
                }
                try {
                    showToast(response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onFailure(Call<PhotoItemCollectionDao> call,
                              Throwable t) {
            //Handle
            clearLoadingMoreFlagIfCapable(mode);

            swipeRefreshLayout.setRefreshing(false);
            showToast(t.toString());
        }

        private void clearLoadingMoreFlagIfCapable(int mode){
            if (mode == MODE_LOAD_MORE) {
                isLoadingMore = false;
            }
        }
    }
}
