package uu.toolbox.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import uu.toolbox.logging.UULog;

/**
 * A simple named timer that wraps Timer and TimerTask
 */
@SuppressWarnings("unused")
public class UUTimer
{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Public Interfaces
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Delegate callback when a timer fires
     */
    public interface TimerDelegate
    {
        /**
         * Delegate method invoked when a timer fires
         *
         * @param timer the timer
         * @param userInfo user info if supplied when starting timer
         */
        void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo);
    }

    /**
     * Delegate callback for a watchdog timer
     */
    public interface WatchdogTimerDelegate
    {
        /**
         * Delegate method invoked when a watchdog timer fires
         *
         * @param userInfo user info if supplied when starting timer
         */
        void onTimer(@Nullable Object userInfo);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Data Members
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private @NonNull String timerId = "";
    private @Nullable Object userInfo = null;
    private @Nullable TimerDelegate timerDelegate = null;
    private long interval = 0;
    private boolean repeat = false;
    private @Nullable Timer underlyingTimer;
    private @Nullable TimerTask underlyingTimerTask;

    private static final @NonNull HashMap<String, UUTimer> theActiveTimers = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Data Accessors
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets the timer ID
     *
     * @return string timer ID
     */
    public @NonNull String getTimerId()
    {
        return timerId;
    }

    /**
     * Gets the user info associated with the timer
     *
     * @return object
     */
    public @Nullable Object getUserInfo()
    {
        return userInfo;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Construction
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a timer.
     *
     * @param timerId timer ID. it is up to callers to enforce unique ID's
     * @param interval timer interval in milliseconds
     * @param repeat whether the timer repeats or fires once.
     * @param userInfo optional user info to pass along with the timer delegate
     * @param delegate the timer callback
     */
    public UUTimer(
        final @NonNull String timerId,
        final long interval,
        final boolean repeat,
        final @Nullable Object userInfo,
        final @Nullable TimerDelegate delegate)
    {
        this.timerId = timerId;
        this.interval = interval;
        this.repeat = repeat;
        this.userInfo = userInfo;
        this.timerDelegate = delegate;
        this.underlyingTimer = new Timer();

        this.underlyingTimerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                handlerTimerFired();
            }
        };
    }

    /**
     * Starts the timer
     */
    public void start()
    {
        addTimer(this);
        safeStartTimer();
    }

    /**
     * Cancels the timer
     */
    public void cancel()
    {
        safeCancelTimer();
        underlyingTimer = null;
        underlyingTimerTask = null;
        removeTimer(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Private Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void safeStartTimer()
    {
        try
        {
            if (underlyingTimer != null)
            {
                if (repeat)
                {
                    underlyingTimer.schedule(underlyingTimerTask, interval, interval);
                }
                else
                {
                    underlyingTimer.schedule(underlyingTimerTask, interval);
                }
            }
        }
        catch (Exception ex)
        {
            UULog.debug(getClass(), "safeStartTimer", ex);
        }
    }

    private void safeCancelTimer()
    {
        try
        {
            if (underlyingTimer != null)
            {
                underlyingTimer.cancel();
            }
        }
        catch (Exception ex)
        {
            UULog.debug(getClass(), "safeCancelTimer", ex);
        }
        finally
        {
            underlyingTimer = null;
        }
    }

    private void handlerTimerFired()
    {
        try
        {
            if (timerDelegate != null)
            {
                timerDelegate.onTimer(this, userInfo);
            }
        }
        catch (Exception ex)
        {
            UULog.debug(getClass(), "handlerTimerFired", ex);
        }
        finally
        {
            if (!repeat)
            {
                cancel();
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Private Class Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static void addTimer(final @NonNull UUTimer timer)
    {
        logAllTimers("addTimer.before");

        try
        {
            synchronized (theActiveTimers)
            {
                theActiveTimers.put(timer.getTimerId(), timer);
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUTimer.class, "addTimer", ex);
        }
        finally
        {
            logAllTimers("addTimer.after");
        }
    }

    private static void removeTimer(final @NonNull UUTimer timer)
    {
        logAllTimers("removeTimer.before");

        try
        {
            synchronized (theActiveTimers)
            {
                theActiveTimers.remove(timer.getTimerId());
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUTimer.class, "removeTimer", ex);
        }
        finally
        {
            logAllTimers("removeTimer.after");
        }
    }

    private static void logAllTimers(final @NonNull String fromWhere)
    {
        try
        {
            Collection<UUTimer> list = listActiveTimers();
            UULog.debug(UUTimer.class, "logAllTimers", "There are " + list.size() + " active timers, fromWhere: " + fromWhere);

            for (UUTimer t : list)
            {
                UULog.debug(UUTimer.class, "logAllTimers", "Timer: " + t.getTimerId() + ", userInfo: " + t.getUserInfo());
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUTimer.class, "logAllTimers", ex);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Public Class Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Finds an active timer by ID
     *
     * @param timerId the timer ID to lookup
     *
     * @return a timer or null if not found
     */
    public static @Nullable UUTimer findActiveTimer(final @NonNull String timerId)
    {
        return theActiveTimers.get(timerId);
    }

    /**
     * Lists all active timers
     *
     * @return a list of UUTimer's
     */
    public static @NonNull Collection<UUTimer> listActiveTimers()
    {
        return theActiveTimers.values();
    }

    /**
     * Fires a named timer
     *
     * @param timerId timer ID
     * @param timeoutMilliseconds timout in milliseconds
     * @param userInfo optional user context
     * @param delegate timer callback
     */
    public static void startTimer(
        final @NonNull String timerId,
        final long timeoutMilliseconds,
        final @Nullable Object userInfo,
        final @NonNull WatchdogTimerDelegate delegate)
    {
        cancelActiveTimer(timerId);

        if (timeoutMilliseconds > 0)
        {
            UUTimer t = new UUTimer(timerId, timeoutMilliseconds, false, userInfo, new TimerDelegate()
            {
                @Override
                public void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo)
                {
                    delegate.onTimer(userInfo);
                }
            });

            t.start();
        }
    }

    /**
     * Cancels a named timer
     *
     * @param timerId timer ID
     */
    public static void cancelActiveTimer(final @NonNull String timerId)
    {
        UUTimer timer = findActiveTimer(timerId);
        if (timer != null)
        {
            timer.cancel();
        }
    }
}
