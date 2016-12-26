package com.example.dyckster.sebbiatesttask.app.model;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.activeandroid.sebbia.Model;
import com.activeandroid.sebbia.annotation.Column;
import com.example.dyckster.sebbiatesttask.SebbiaTestTaskApplication;
import com.example.dyckster.sebbiatesttask.app.api.Api;
import com.example.dyckster.sebbiatesttask.app.api.Request;
import com.example.dyckster.sebbiatesttask.app.api.Response;
import com.example.dyckster.sebbiatesttask.app.api.ServerError;
import com.example.dyckster.sebbiatesttask.utils.Log;
import com.example.dyckster.sebbiatesttask.utils.Utils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Kostyrev on 17/12/14.
 */
public abstract class UpdatableModel extends Model {

    protected static Handler mainLoopHanlder = new Handler(Looper.getMainLooper());
    @Column(name = "last_updated")
    protected long lastUpdated;
    private transient List<UpdateListener<? extends UpdatableModel>> listeners;
    private transient AsyncTask<Void, Void, ServerError> updateTask;
    private transient boolean lazyLoaded;
    private transient boolean needsUpdate;
    private transient ServerError lastError;
    private static LinkedList<UpdatableModel> updatableLinkedList;
    private static AtomicInteger isPriorityInProgress = new AtomicInteger();

    public enum Priority {
        HIGH,
        MEDIUM,
        LOW
    }

    protected UpdatableModel() {
        listeners = new ArrayList<>();
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public boolean hasData() {
        return lastUpdated != 0;
    }

    public long getTimeSinceUpdate() {
        return System.currentTimeMillis() - lastUpdated;
    }

    public ServerError getLastError() {
        return lastError;
    }

    protected abstract long getUpdatePeriod();

    protected abstract Request getUpdateRequest();

    protected abstract void parseAndSave(Response response) throws JSONException;

    /**
     * Subclass must return true if it needs to load data on background thread from database.
     *
     * @return
     */
    protected boolean canLazyLoad() {
        return false;
    }

    /**
     * Will be called on background thread to allow subclass to load data from database.
     */
    protected void performLazyLoad() {

    }


    protected boolean isUpdateBlocked() {
        return false;
    }

    protected ServerError performUpdate(@NonNull AsyncTask<Void, Void, ServerError> runnable, boolean forced) {
        Log.i("Running update for " + getClass().getSimpleName() + " forced = " + forced);
        boolean startReported = false;
        if (needLazyLoading()) {
            reportUpdateStarted();
            startReported = true;
        }

        if (needLazyLoading()) {
            synchronized (Model.class) {
                Log.i("Lazy loading " + getClass().getSimpleName());
                performLazyLoad();
            }
            lazyLoaded = true;
            if (!needsUpdate() || (!isEmpty() && !Utils.isInternetAvailable(SebbiaTestTaskApplication.getInstance())))
                return null;
        }

        if (!Utils.isInternetAvailable(SebbiaTestTaskApplication.getInstance())) {
            if (startReported)
                reportUpdateFinish(ServerError.NO_CONNECTION);
            return null;
        }

        if (!startReported)
            reportUpdateStarted();

        Response response = Api.sendRequest(getUpdateRequest());
        if (!response.isSuccessful()) {

            if (shouldForceUpdateAfterResponse(response)) {
                lastUpdated = 0;
                return performUpdate(runnable, forced);
            }

            return response.getErrorCode();
        }

        this.lastUpdated = System.currentTimeMillis();
        try {
            Log.i("Parse and saving " + getClass().getSimpleName());
            synchronized (Model.class) {
                parseAndSave(response);
            }
        } catch (Exception e) {
            Log.e("Failed to parse JSON while updating " + getClass().getSimpleName(), e);
            return ServerError.UNKNOWN_ERROR;
        }
        return ServerError.NO_ERROR;
    }

    public void addListener(@NonNull UpdateListener<? extends UpdatableModel> updateListener) {
        synchronized (UpdatableModel.class) {
            listeners.add(updateListener);
        }
    }

    public void removeListener(@NonNull UpdateListener updateListener) {
        synchronized (UpdatableModel.class) {
            listeners.remove(updateListener);
        }
    }

    public final void update(final boolean forced, Priority priority) {
        if (updatableLinkedList == null) {
            updatableLinkedList = new LinkedList<>();
        }
        switch (priority) {
            case HIGH:
                refresh(forced);
                Log.d("Queue " + this.getClass().getSimpleName() + " HIGH update started");
                break;
            case MEDIUM:
                if (!updatableLinkedList.contains(this)) {
                    updatableLinkedList.addLast(this);
                    Log.d("Queue " + this.getClass().getSimpleName() + " MEDIUM added to queue");
                }
                break;
            case LOW:
                if (!updatableLinkedList.contains(this)) {
                    updatableLinkedList.addFirst(this);
                    Log.d("Queue " + this.getClass().getSimpleName() + " LOW added to queue");
                }
                break;
        }
        if (isPriorityInProgress.get() == 0) {
            if (!updatableLinkedList.isEmpty()) {
                updatableLinkedList.pollLast().refresh(forced);
                Log.d("Queue " + this.getClass().getSimpleName() + " update started");
            }
        }
    }

    private void refresh(final boolean forced) {
        if (isUpdateBlocked()) {
            return;
        }
        isPriorityInProgress.incrementAndGet();
        updateTask = new AsyncTask<Void, Void, ServerError>() {
            @Override
            protected ServerError doInBackground(Void... params) {
                return performUpdate(this, forced);
            }

            @Override
            protected void onPostExecute(ServerError error) {
                try {
                    lastError = error;
                    if (updateTask == this) {
                        updateTask = null;
                    }
                    if (!isCancelled()) {
                        reportUpdateFinish(error);
                    }
                } finally {
                    isPriorityInProgress.decrementAndGet();
                    Log.d("Queue " + UpdatableModel.this.getClass().getSimpleName() + " update finished");
                    if (!updatableLinkedList.isEmpty()) {
                        updatableLinkedList.pollLast().update(forced, Priority.HIGH);
                    } else {
                        Log.d("Queue is empty");
                    }
                }
            }
        }.execute();
    }

    public void update(final boolean forced) {
        update(forced, Priority.LOW);
    }

    public boolean isUpdateInProgress() {
        boolean check = updateTask != null && !updateTask.isCancelled();
        if (updatableLinkedList != null && !updatableLinkedList.isEmpty()) {
            return check || updatableLinkedList.contains(this);
        } else {
            return check;
        }
    }

    public void cancelUpdate() {
        if (isUpdateInProgress())
            updateTask.cancel(true);
    }

    public void setNeedsUpdate() {
        needsUpdate = true;
    }

    public boolean needsUpdate() {
        if (needsUpdate)
            return true;
        if (needLazyLoading())
            return true;
        // Comment by: dombaev_yury
        // Вопрос про код стайл
        // if (){...;} или if()...;
        // for (){...;} или for()...;
        if (System.currentTimeMillis() > (lastUpdated + TimeUnit.MINUTES.toMillis(5)))
            return true;
        return System.currentTimeMillis() > (lastUpdated + getUpdatePeriod());
    }

    @SuppressWarnings("unchecked")
    protected void reportUpdateStarted() {
        mainLoopHanlder.post(new Runnable() {
            @Override
            public void run() {
                synchronized (UpdatableModel.class) {
                    for (UpdateListener updateListener : listeners)
                        updateListener.updateStarted(UpdatableModel.this);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    protected void reportUpdateFinish(final ServerError error) {
        mainLoopHanlder.post(new Runnable() {
            @Override
            public void run() {
                synchronized (UpdatableModel.class) {
                    for (UpdateListener updateListener : listeners)
                        updateListener.onUpdated(UpdatableModel.this, error == ServerError.NO_ERROR, error);
                }
            }
        });
    }

    protected boolean shouldForceUpdateAfterResponse(Response response) {
        return false;
    }

    private boolean needLazyLoading() {
        return canLazyLoad() && getId() != null && !lazyLoaded;
    }

    public boolean isEmpty() {
        return lastUpdated == 0;
    }

    public interface UpdateListener<T extends UpdatableModel> {
        public void updateStarted(T updatableModel);

        public void onUpdated(T updatableModel, boolean success, ServerError error);
    }
}
