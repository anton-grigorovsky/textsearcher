package ru.splattest.textsearcher.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Антон on 13.08.2018.
 */
public class MatchFinderSynchronizer {
    private Runnable runnable;
    private AtomicInteger count;
    private List<CounterObserver> observers = new ArrayList<>();

    public MatchFinderSynchronizer(Runnable runnable, int count) {
        this.runnable = runnable;
        this.count = new AtomicInteger(count);
    }


    public void countDown()
    {
        int i = count.decrementAndGet();
        notifyObservers(i);
        if(i == 0)
        {
            new Thread(runnable).start();
        }
    }

    public void registerObserver(CounterObserver observer)
    {
        observers.add(observer);
    }

    private void notifyObservers(int count)
    {
        observers.forEach(observer -> observer.onStateChanged(count));
    }




}
