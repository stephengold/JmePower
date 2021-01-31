/*
 Copyright (c) 2020-2021, Stephen Gold
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 * Neither the name of the copyright holder nor the names of its contributors
 may be used to endorse or promote products derived from this software without
 specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.stephengold.jmepower;

import com.jme3.asset.AssetManager;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;
import jme3utilities.Loadable;

/**
 * A Thread used to preload assets into the AssetCache.
 *
 * @author Stephen Gold sgold@sonic.net
 */
public class Preloader extends Thread {
    // *************************************************************************
    // constants and loggers

    /**
     * message logger for this class
     */
    final private static Logger logger
            = Logger.getLogger(Preloader.class.getName());
    // *************************************************************************
    // fields

    /**
     * for loading assets
     */
    final private AssetManager assetManager;
    /**
     * notify the creator when done
     */
    final private CountDownLatch completionLatch;
    /**
     * things to load
     */
    final private Queue<Loadable> loadables;
    // *************************************************************************
    // constructors

    /**
     * Instantiate a Thread to load loadables from the specified Queue.
     *
     * @param loadables the things to load (not null, alias created)
     * @param assetManager the AssetManager for loading assets (not null, alias
     * created)
     * @param completionLatch to notify the creator when done (not null, alias
     * created)
     */
    public Preloader(Queue<Loadable> loadables, AssetManager assetManager,
            CountDownLatch completionLatch) {
        this.loadables = loadables;
        this.assetManager = assetManager;
        this.completionLatch = completionLatch;
    }
    // *************************************************************************
    // new methods exposed

    @Override
    public void run() {
        Loadable loadable;
        while (true) {
            loadable = loadables.poll();
            if (loadable == null) {
                break;
            }

//            long startMillis = System.currentTimeMillis();
            loadable.load(assetManager);

//            String name = loadable.getClass().getSimpleName();
//            long latencyMillis = System.currentTimeMillis() - startMillis;
//            float seconds = latencyMillis / 1_000f;
//            System.out.println("loaded " + name + " in " + seconds + " sec.");
        }

        completionLatch.countDown();
    }
}
