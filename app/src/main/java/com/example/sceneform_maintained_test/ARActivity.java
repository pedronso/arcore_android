package com.example.sceneform_maintained_test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentOnAttachListener;

import com.google.ar.core.Anchor;
import com.google.ar.core.Config;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.Sceneform;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Light;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.PlaneRenderer;

import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.lang.ref.WeakReference;

public class ARActivity extends AppCompatActivity implements
        FragmentOnAttachListener,
        BaseArFragment.OnTapArPlaneListener,
        BaseArFragment.OnSessionConfigurationListener,
        ArFragment.OnViewCreatedListener,
        Node.OnTapListener{

    private ArFragment arFragment;
    private Renderable model;
    private AnchorNode anchorNode;
    private TransformableNode modelNode;
    private boolean placed = false;
    private SeekBar lightSlide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ar);
        getSupportFragmentManager().addFragmentOnAttachListener(this);

        if (savedInstanceState == null) {
            if (Sceneform.isSupported(this)) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.arFragment, ArFragment.class, null)
                        .commit();
            }
        }

        lightSlide  = (SeekBar) findViewById(R.id.lightSlider);
        loadModels();
        lightChange();
    }

    @Override
    public void onAttachFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
        if (fragment.getId() == R.id.arFragment) {
            arFragment = (ArFragment) fragment;
            arFragment.setOnSessionConfigurationListener(this);
            arFragment.setOnViewCreatedListener(this);
            arFragment.setOnTapArPlaneListener(this);
        }
    }

    @Override
    public void onSessionConfiguration(Session session, Config config) {
        if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
            config.setDepthMode(Config.DepthMode.AUTOMATIC);
        }
    }

    @Override
    public void onViewCreated(ArSceneView arSceneView) {
        arFragment.setOnViewCreatedListener(null);

        // Fine adjust the maximum frame rate
        arSceneView.setFrameRateFactor(SceneView.FrameRate.FULL);
    }

    public void loadModels() {
        WeakReference<ARActivity> weakActivity = new WeakReference<>(this);
        ModelRenderable.builder()
                .setSource(this, R.raw.lamp_weapon_first_animation)
                .setIsFilamentGltf(true)
                .setAsyncLoadEnabled(true)
                .build()
                .thenAccept(model -> {
                    ARActivity activity = weakActivity.get();
                    if (activity != null) {
                        activity.model = model;
                    }
                })
                .exceptionally(throwable -> {
                    Toast.makeText(
                            this, "Unable to load model", Toast.LENGTH_LONG).show();
                    return null;
                });
    }

    @Override
    public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
        if (model == null) {
            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
            return;
        }
        if(placed == false) {
            Toast.makeText(this, "Loaded", Toast.LENGTH_SHORT).show();
            // Create the Anchor.
            Anchor anchor = hitResult.createAnchor();
            anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());

            // Create the transformable model and add it to the anchor.
            modelNode = new TransformableNode(arFragment.getTransformationSystem());

            //model.getScaleController().setMinScale(0.1f);

            //modelNode.setLocalScale(new Vector3(1f, 1f, 1f));
            //Log.i("teste", "teste" + modelNode.getLocalScale());

            modelNode.setParent(anchorNode);

            modelNode.setRenderable(this.model)
                    .animate(false);

            lightSlide.setProgress((int) arFragment.getArSceneView()._environment.getIndirectLight().getIntensity());
            modelNode.select();
            placed = true;
        }else{
            Toast.makeText(this, "Max reached", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
            Toast.makeText(this, "Touch tapped", Toast.LENGTH_SHORT).show();
    }

    public void removeNode(View v){
        Toast.makeText(this, "Removed", Toast.LENGTH_SHORT).show();
         modelNode.setParent(null);
         this.placed = false;
    }
    public void hideShow(View v){
        Toast.makeText(this, "Enable/Disable", Toast.LENGTH_SHORT).show();
        modelNode.setEnabled(!modelNode.isEnabled());
    }
    public void modelAnimation(View v){
        Toast.makeText(this, "Animation", Toast.LENGTH_SHORT).show();
        modelNode.setRenderable(this.model)
                .animate(false).start();
    }

    public void planeHide(View v){
        arFragment.getArSceneView().getPlaneRenderer().setEnabled(!arFragment.getArSceneView().getPlaneRenderer().isEnabled());
    }

    public void lightSet(View v){
        arFragment.getArSceneView()._environment.getIndirectLight().setIntensity(100000f);
        lightSlide.setProgress((int) arFragment.getArSceneView()._environment.getIndirectLight().getIntensity());
        Log.i("lumen", "lumen" + String.valueOf(arFragment.getArSceneView()._environment.getIndirectLight().getIntensity()));
    }

    public void lightChange(){

        lightSlide.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                arFragment.getArSceneView()._environment.getIndirectLight().setIntensity((float) i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


}