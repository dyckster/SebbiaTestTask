package com.example.dyckster.sebbiatesttask.app.model;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.dyckster.sebbiatesttask.app.api.ServerError;
import com.example.dyckster.sebbiatesttask.utils.Log;
import com.example.dyckster.sebbiatesttask.utils.Observer;


/**
 * Created by Kostyrev on 20/01/15.
 */
public abstract class PageableModel<T> extends UpdatableList<T> {

    public static final int FIRST_PAGE = 0;

    public interface PageableListener<T extends PageableModel> {
        public void pagingStarted(T pageable);

        public void pageLoaded(T pageable, boolean success, ServerError error);
    }

    public abstract boolean hasMore();

    protected abstract ServerError performPaging(@NonNull AsyncTask<Void, Void, ServerError> runnable, int nextPage);

    protected int currentPage = FIRST_PAGE;
    private transient Observer<PageableListener> pageableListeners;
    private transient AsyncTask<Void, Void, ServerError> pageTask;

    public PageableModel() {
        pageableListeners = new Observer<>();
    }

    public void loadNextPage() {
        Log.d("loading of next page in PageableModel");
        reportPagingStarted();

        pageTask = new AsyncTask<Void, Void, ServerError>() {
            @Override
            protected ServerError doInBackground(Void... params) {
                return performPaging(this, currentPage);
            }

            @Override
            protected void onPostExecute(ServerError error) {
                super.onPostExecute(error);
                if (!isCancelled()) {
                    if (error == ServerError.NO_ERROR) {
                        currentPage++;
                    }
                    reportPagingFinish(error);
                }
                pageTask = null;
            }

        }.execute();
    }

    public void addPageableListener(PageableListener pageableListener) {
        synchronized (PageableModel.class) {
            pageableListeners.addStrongListener(pageableListener);
        }
    }

    public void removePageableListener(PageableListener pageableListener) {
        synchronized (PageableModel.class) {
            pageableListeners.removeListener(pageableListener);
        }
    }

    @Override
    protected void reportUpdateStarted() {
        super.reportUpdateStarted();
        currentPage = FIRST_PAGE;
    }

    @Override
    protected void reportUpdateFinish(ServerError error) {
        super.reportUpdateFinish(error);
        currentPage = FIRST_PAGE + 1;
    }

    @SuppressWarnings("unchecked")
    protected void reportPagingStarted() {
        mainLoopHanlder.post(new Runnable() {
            @Override
            public void run() {
                synchronized (PageableModel.class) {
                    for (PageableListener pageableListener : pageableListeners.getListeners())
                        pageableListener.pagingStarted(PageableModel.this);
                }
            }
        });
    }

    public Observer<PageableListener> getPageableListeners() {
        return pageableListeners;
    }

    @SuppressWarnings("unchecked")
    protected void reportPagingFinish(final ServerError error) {
        mainLoopHanlder.post(new Runnable() {
            @Override
            public void run() {
                synchronized (PageableModel.class) {
                    for (PageableListener pageableListener : pageableListeners.getListeners())
                        pageableListener.pageLoaded(PageableModel.this, error == ServerError.NO_ERROR, error);
                }
            }
        });
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public boolean isPagingInProgress() {
        return pageTask != null && !pageTask.isCancelled();
    }

    public void cancelPaging() {
        if (isPagingInProgress())
            pageTask.cancel(true);
    }


}
