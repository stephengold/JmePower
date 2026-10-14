package com.github.stephengold.jmepower;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import jme3utilities.Loadable;

/** Checks thread completion separately from successful asset loading. */
public class PreloaderCompletionRegression {
    public static void main(String[] args) throws Exception {
        Queue<Loadable> work = new ConcurrentLinkedQueue<>();
        IllegalStateException failure = new IllegalStateException("Deliberate load failure");
        work.add(manager -> { throw failure; });
        work.add(manager -> { throw new AssertionError("Work continued after failure"); });
        CountDownLatch completed = new CountDownLatch(1);
        AtomicReference<Throwable> observed = new AtomicReference<>();
        Preloader loader = new Preloader(work, null, completed);
        loader.setUncaughtExceptionHandler((thread, error) -> observed.set(error));
        loader.start();
        loader.join(2000);
        if (loader.isAlive()) throw new AssertionError("Thread did not terminate");
        if (observed.get() != failure) throw new AssertionError("Original error was hidden");
        if (work.size() != 1) throw new AssertionError("Remaining work was discarded");
        if (completed.getCount() != 0) {
            throw new AssertionError("Terminated loader leaves completion latch blocked");
        }
        CountDownLatch emptyCompleted = new CountDownLatch(1);
        new Preloader(new ConcurrentLinkedQueue<Loadable>(), null, emptyCompleted).run();
        if (emptyCompleted.getCount() != 0) throw new AssertionError("Empty queue incomplete");
        System.out.println("PASS failed/empty completion, uncaught cause and remaining queue");
    }
}
