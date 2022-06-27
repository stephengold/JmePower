/*
 Copyright (c) 2020-2022, Stephen Gold
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

import com.jme3.anim.AnimClip;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.AnimFactory;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.cinematic.Cinematic;
import com.jme3.cinematic.PlayState;
import com.jme3.cinematic.events.AnimEvent;
import com.jme3.cinematic.events.CinematicEvent;
import com.jme3.cinematic.events.CinematicEventListener;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.material.Materials;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.shadow.SpotLightShadowRenderer;
import com.jme3.texture.Texture;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;
import jme3utilities.Loadable;
import jme3utilities.Validate;

/**
 * An AppState to display a Cinematic while warming up the AssetCache. When its
 * work is done, it disables (but does not detach) itself.
 */
public class JmeLoadingState extends BaseAppState {
    // *************************************************************************
    // constants and loggers

    /**
     * message logger for this class
     */
    final public static Logger logger
            = Logger.getLogger(JmeLoadingState.class.getName());
    /**
     * name of the input mapping to cancel the Cinematic
     */
    final private static String cancelAction = "cancel cinematic";
    /**
     * name of the input mapping to pause the Cinematic
     */
    final private static String pauseAction = "toggle pause";
    // *************************************************************************
    // fields

    /**
     * listen for the Tab key
     */
    final private ActionListener cancelListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (cinematic != null) {
                cinematic.stop();
            }
        }
    };
    /**
     * listen for the Pause key
     */
    final private ActionListener pauseListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (keyPressed && cinematic != null) {
                PlayState playState = cinematic.getPlayState();
                if (playState == PlayState.Playing) {
                    cinematic.pause();
                } else if (playState == PlayState.Paused) {
                    cinematic.play();
                }
            }
        }
    };
    /**
     * entertain the user
     */
    private Cinematic cinematic;
    /**
     * monitor how many locally-created threads are running
     */
    private CountDownLatch latch;
    /**
     * hide what happens in the main scene
     */
    private Geometry shutter;
    /**
     * count update()s for scheduling
     */
    private int updateCount = 0;
    /**
     * assets to be preloaded
     */
    final private Queue<Loadable> queue;
    /**
     * secondary lighting for the Cinematic
     */
    private PointLight pointLight;
    /**
     * access the AssetManager, InputManager, and scene graphs
     */
    private SimpleApplication application;
    /**
     * primary lighting for the Cinematic
     */
    private SpotLight spotlight;
    /**
     * shadows for the Cinematic
     */
    private SpotLightShadowRenderer shadowRenderer;
    // *************************************************************************
    // constructors

    /**
     * Instantiate an enabled AppState to preload the specified assets.
     *
     * @param loadables the assets to preload (not null)
     */
    public JmeLoadingState(Loadable... loadables) {
        int numLoadables = loadables.length;
        queue = new ArrayBlockingQueue<>(numLoadables);
        List<Loadable> list = Arrays.asList(loadables);
        queue.addAll(list);
    }
    // *************************************************************************
    // protected methods

    /**
     * Access the latch used to track the completions of background threads.
     *
     * @return the pre-existing instance (not null)
     */
    final protected CountDownLatch getLatch() {
        return latch;
    }

    /**
     * Create and start the background threads.
     *
     * @param numAdditionalThreads (&ge;0)
     */
    protected void startThreads(int numAdditionalThreads) {
        Validate.nonNegative(numAdditionalThreads,
                "number of additional threads");
        /*
         * Add all of the loadables to a queue.
         */
        int numLoadables = queue.size();
        int maxPreloaders = 2;
        int numPreloaders = Math.min(numLoadables, maxPreloaders);
        int numThreadsToCreate = numPreloaders + numAdditionalThreads;
        latch = new CountDownLatch(numThreadsToCreate);
        /*
         * Start preload threads to warm up the AssetCache.
         */
        AssetManager assetManager = application.getAssetManager();
        for (int threadIndex = 0; threadIndex < numPreloaders; ++threadIndex) {
            Thread thread = new Preloader(queue, assetManager, latch);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
        }
    }
    // *************************************************************************
    // BaseAppState methods

    /**
     * Callback invoked after this AppState is detached or during application
     * shutdown if the state is still attached. onDisable() is called before
     * this cleanup() method if the state is enabled at the time of cleanup.
     *
     * @param application the application instance (not null)
     */
    @Override
    protected void cleanup(Application application) {
        // do nothing
    }

    /**
     * Callback invoked after this AppState is attached but before onEnable().
     *
     * @param application the application instance (not null)
     */
    @Override
    protected void initialize(Application application) {
        this.application = (SimpleApplication) application;
    }

    /**
     * Callback invoked whenever this AppState ceases to be both attached and
     * enabled.
     */
    @Override
    protected void onDisable() {
        if (shutter != null) {
            shutter.removeFromParent(); // TODO application should do this
            shutter = null;
        }

        InputManager inputManager = application.getInputManager();
        inputManager.deleteMapping(cancelAction);
        inputManager.removeListener(cancelListener);

        inputManager.deleteMapping(pauseAction);
        inputManager.removeListener(pauseListener);
    }

    /**
     * Callback invoked whenever this AppState becomes both attached and
     * enabled.
     */
    @Override
    protected void onEnable() {
        InputManager inputManager = application.getInputManager();
        inputManager.addListener(cancelListener, cancelAction);
        KeyTrigger trigger = new KeyTrigger(KeyInput.KEY_TAB);
        inputManager.addMapping(cancelAction, trigger);

        inputManager.addListener(pauseListener, pauseAction);
        trigger = new KeyTrigger(KeyInput.KEY_PAUSE);
        inputManager.addMapping(pauseAction, trigger);
    }

    /**
     * Callback to update this AppState, invoked once per frame when the
     * AppState is both attached and enabled.
     *
     * @param tpf the time interval between frames (in seconds, &ge;0)
     */
    @Override
    public void update(float tpf) {
        ++updateCount;
        switch (updateCount) {
            case 1:
                setupStage();
                return;
            case 2:
                int numAdditionalThreads = 0;
                startThreads(numAdditionalThreads);
                break;
            case 3:
                startCinematic();
                break;

            default: // 4 or more
                PlayState playState = cinematic.getPlayState();
                if (playState == PlayState.Paused
                        || playState == PlayState.Playing) {
                    return;
                }
        }
        /*
         * The Cinematic completed or was cancelled by the user.
         */
        long latchCount = latch.getCount();
        if (latchCount < 1L) {
            /*
             * Lemur has been initialized, and all asynchronous asset loads
             * have completed.
             */
            setupShutter();

            Node rootNode = application.getRootNode();
            rootNode.detachAllChildren();
            if (pointLight != null) {
                rootNode.removeLight(pointLight);
            }
            if (spotlight != null) {
                rootNode.removeLight(spotlight);
            }
            if (shadowRenderer != null) {
                application.getViewPort().removeProcessor(shadowRenderer);
            }
            getStateManager().detach(cinematic);
            setEnabled(false);
        }
    }
    // *************************************************************************
    // private methods

    /**
     * Load the Jaime model with 2 extra animation clips.
     */
    private Node loadJaime() {
        AssetManager assetManager = application.getAssetManager();
        Node result
                = (Node) assetManager.loadModel("/Models/Jaime/Jaime-new.j3o");
        result.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        /*
         * Add a 7-second clip to translate Jaime forward during a jump.
         */
        float fps = 30f;
        AnimFactory af = new AnimFactory(7f, "JumpForward", fps);
        af.addTimeTranslation(0f, new Vector3f(0f, 0f, -3f));
        af.addTimeTranslation(0.35f, new Vector3f(0f, 1f, -1.5f));
        af.addTimeTranslation(0.7f, new Vector3f());
        AnimClip forwardClip = af.buildAnimation(result);
        AnimComposer composer = result.getControl(AnimComposer.class);
        composer.addAnimClip(forwardClip);
        /*
         * Add a 1-second clip to translate Jaime upward during a jump.
         */
        af = new AnimFactory(1f, "JumpUpward", fps);
        af.addTimeTranslation(0f, new Vector3f());
        af.addTimeTranslation(0.7f, new Vector3f(0f, 4f, 0f));
        AnimClip upClip = af.buildAnimation(result);
        composer.addAnimClip(upClip);

        return result;
    }

    /**
     * Set up the Cinematic.
     *
     * @param jaime the root of the monkey's C-G model (not null)
     */
    private void setupCinematic(final Node jaime) {
        Node rootNode = application.getRootNode();
        float duration = 60f; // seconds, overridden by fitDuration()
        cinematic = new Cinematic(rootNode, duration);
        AnimComposer composer = jaime.getControl(AnimComposer.class);
        composer.makeLayer("SpatialLayer", null);
        String boneLayer = AnimComposer.DEFAULT_LAYER;

        AnimEvent idleHalfSecond = new AnimEvent(composer, "Idle", boneLayer);
        idleHalfSecond.setInitialDuration(0.5f);
        cinematic.enqueueCinematicEvent(idleHalfSecond);
        float jumpStart = cinematic.enqueueCinematicEvent(
                new AnimEvent(composer, "JumpStart", boneLayer));
        cinematic.addCinematicEvent(jumpStart + 0.2f,
                new AnimEvent(composer, "JumpForward", "SpatialLayer"));
        cinematic.enqueueCinematicEvent(
                new AnimEvent(composer, "JumpEnd", boneLayer));
        cinematic.enqueueCinematicEvent(
                new AnimEvent(composer, "Taunt", boneLayer));
        cinematic.enqueueCinematicEvent(
                new AnimEvent(composer, "Punches", boneLayer));
        cinematic.enqueueCinematicEvent(
                new AnimEvent(composer, "SideKick", boneLayer));
        cinematic.enqueueCinematicEvent(
                new AnimEvent(composer, "SideKick", boneLayer));
        AnimEvent idleOneSecond = new AnimEvent(composer, "Idle", boneLayer);
        idleOneSecond.setInitialDuration(1f);
        cinematic.enqueueCinematicEvent(
                idleOneSecond);
        cinematic.enqueueCinematicEvent(
                new AnimEvent(composer, "Wave", boneLayer));
        float jumpStart2 = cinematic.enqueueCinematicEvent(
                new AnimEvent(composer, "JumpStart", boneLayer));
        cinematic.addCinematicEvent(jumpStart2 + 0.2f,
                new AnimEvent(composer, "JumpUpward", boneLayer));
        cinematic.enqueueCinematicEvent(
                new AnimEvent(composer, "JumpEnd", boneLayer));
        AnimEvent idleShort = new AnimEvent(composer, "Idle", boneLayer);
        idleShort.setInitialDuration(0.2f);
        cinematic.enqueueCinematicEvent(idleShort);

        cinematic.addListener(new CinematicEventListener() {
            @Override
            public void onPlay(CinematicEvent c) {
                // do nothing
            }

            @Override
            public void onPause(CinematicEvent c) {
                // do nothing
            }

            @Override
            public void onStop(CinematicEvent c) {
                jaime.removeFromParent();
            }
        });
        cinematic.fitDuration();
        cinematic.setSpeed(1.2f);
    }

    private Geometry setupFloor() {
        AssetManager assetManager = application.getAssetManager();

        // TODO use MyAsset
        String assetPath = "/Textures/JmePower/powered-by.jpeg";
        Texture tex = assetManager.loadTexture(assetPath);
        Material material = new Material(assetManager, Materials.LIGHTING);
        material.setTexture("DiffuseMap", tex);

        Quad mesh = new Quad(2.2f, 2.2f);
        Geometry result = new Geometry("floor", mesh);
        result.rotate(-FastMath.HALF_PI, 0f, 0f);
        result.center();
        result.setMaterial(material);
        result.setShadowMode(RenderQueue.ShadowMode.Receive);

        return result;
    }

    /**
     * Add lights and shadows to the specified scene.
     *
     * @param scene (not null)
     */
    private void setupLightsAndShadows(Node scene) {
        spotlight = new SpotLight();
        scene.addLight(spotlight);

        Vector3f position = new Vector3f(1f, 10f, 4f);
        Vector3f direction = position.normalize().negateLocal();
        spotlight.setDirection(direction);
        spotlight.setPosition(position);
        spotlight.setSpotInnerAngle(0.004f);
        spotlight.setSpotOuterAngle(0.12f);

        // a PointLight to fake indirect lighting from the ground
        pointLight = new PointLight();
        scene.addLight(pointLight);

        pointLight.setColor(ColorRGBA.White.mult(1.5f));
        pointLight.setPosition(Vector3f.UNIT_Z);
        pointLight.setRadius(2f);

        AssetManager assetManager = application.getAssetManager();
        shadowRenderer = new SpotLightShadowRenderer(assetManager, 512);
        application.getViewPort().addProcessor(shadowRenderer);
        shadowRenderer.setEdgeFilteringMode(EdgeFilteringMode.PCF8);
        shadowRenderer.setLight(spotlight);
        shadowRenderer.setShadowIntensity(0.5f);
    }

    /**
     * Create and attach a Quad to hide what's happening in the main scene.
     */
    private void setupShutter() {
        AssetManager assetManager = application.getAssetManager();
        Material material = new Material(assetManager, Materials.UNSHADED);
        material.setColor("Color", new ColorRGBA(0.4f, 0.4f, 0.4f, 1f));

        Camera camera = application.getCamera();
        Mesh mesh = new Quad(camera.getWidth(), camera.getHeight());
        shutter = new Geometry("shutter", mesh);
        shutter.setMaterial(material);

        application.getGuiNode().attachChild(shutter);
    }

    /**
     * Set the stage for the Cinematic.
     */
    private void setupStage() {
        Node rootNode = application.getRootNode();
        setupLightsAndShadows(rootNode);

        Camera camera = application.getCamera();
        camera.setLocation(new Vector3f(0f, 1.2f, 2.7f));
        camera.lookAt(new Vector3f(0f, 0.5f, 0f), Vector3f.UNIT_Y);

        Geometry floor = setupFloor();
        rootNode.attachChild(floor);
    }

    /**
     * Set up and play a short Cinematic of Jaime.
     */
    private void startCinematic() {
        Node jaime = loadJaime();
        application.getRootNode().attachChild(jaime);
        jaime.move(0f, 0f, -3f);
        setupCinematic(jaime);

        getStateManager().attach(cinematic);
        cinematic.play();
    }
}
