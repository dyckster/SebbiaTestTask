package com.example.dyckster.sebbiatesttask.app.ui;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.dyckster.sebbiatesttask.R;
import com.example.dyckster.sebbiatesttask.app.model.UpdatableModel;
import com.example.dyckster.sebbiatesttask.app.ui.news.CategoryListFragment;
import com.example.dyckster.sebbiatesttask.app.ui.news.NewsDetailFragment;
import com.example.dyckster.sebbiatesttask.app.ui.news.NewsListFragment;

public class MainActivity extends AppCompatActivity implements OnFragmentChange {
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            setCurrentScreen(FragmentType.CATEGORY_LIST, false);
        }
        //updateRef(CategoriesList.getInstance());
    }

    private void updateRef(UpdatableModel... models){
        for (UpdatableModel model : models) {
            if (model.needsUpdate()){
                model.update(false);
            }
        }

    }
    @Override
    public void onFragmentChange(Fragment fragment) {
//        fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.fragment_parent, fragment).addToBackStack(String.valueOf(fragment.getId())).commit();
    }

    public void setCurrentScreen(Fragment fragment, boolean addToBackStack, boolean animate) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (animate) {

        }
        transaction.replace(R.id.container, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public void setCurrentScreen(@NonNull FragmentType type, boolean addToBackStack) {
        Fragment fragment = getFragmentByType(type);
        if (fragment != null) {
            setCurrentScreen(fragment, addToBackStack, false);
        }
    }

    public enum FragmentType {
        CATEGORY_LIST,
        NEWS_LIST,
        NEWS_DETAILS;

        FragmentType(long id) {
            this.id = id;
        }

        FragmentType() {
        }

        private long id;

        public void setId(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }
    }

    private Fragment getFragmentByType(FragmentType fragmentType) {
        Fragment result = null;
        switch (fragmentType) {
            case CATEGORY_LIST:
                result = new CategoryListFragment();
                break;
            case NEWS_LIST:
                // TODO: 27.12.16 check getId for null
                result = new NewsListFragment().newInstance(fragmentType.getId());
                break;
            case NEWS_DETAILS:

                result = new NewsDetailFragment().newInstance((int) fragmentType.getId());
                break;
        }
        return result;
    }
}
