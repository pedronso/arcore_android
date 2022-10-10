package com.example.sceneform_maintained_test;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.media.CamcorderProfile;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentOnAttachListener;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Light;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.PlaneRenderer;

import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.gorisse.thomas.sceneform.light.LightEstimationConfig;
import com.gorisse.thomas.sceneform.light.LightEstimationKt;

import org.w3c.dom.Text;

import java.lang.ref.WeakReference;

public class ARActivity extends AppCompatActivity implements
        FragmentOnAttachListener,
        BaseArFragment.OnTapArPlaneListener,
        BaseArFragment.OnSessionConfigurationListener,
        ArFragment.OnViewCreatedListener,
        AdapterView.OnItemSelectedListener{

    private ArFragment arFragment;
    private Renderable SelectedModel, LampModel, MariaModel;
    private AnchorNode anchorNode;
    private TransformableNode modelNodeLamp, modelNodeMari;
    private boolean placedLamp = false, placedMari = false;

    private SeekBar lightSlide;
    private Spinner modelSelection;

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
        modelSelection = (Spinner) findViewById(R.id.modelSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.models, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelSelection.setAdapter(adapter);
        modelSelection.setOnItemSelectedListener(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

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
                .setSource(this, R.raw.lamp_weapon_second_animation)
                .setIsFilamentGltf(true)
                .setAsyncLoadEnabled(true)
                .build()
                .thenAccept(model -> {
                    ARActivity activity = weakActivity.get();
                    if (activity != null) {
                        activity.LampModel = model;
                    }
                })
                .exceptionally(throwable -> {
                    Toast.makeText(
                            this, "Não foi possível carregar o modelo", Toast.LENGTH_SHORT).show();
                    return null;
                });
        ModelRenderable.builder()
                .setSource(this, R.raw.maria)
                .setIsFilamentGltf(true)
                .setAsyncLoadEnabled(true)
                .build()
                .thenAccept(model -> {
                    ARActivity activity = weakActivity.get();
                    if (activity != null) {
                        activity.MariaModel = model;
                    }
                })
                .exceptionally(throwable -> {
                    Toast.makeText(
                            this, "Não foi possível carregar o modelo", Toast.LENGTH_SHORT).show();
                    return null;
                });
    }

    @Override
    public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
        if (SelectedModel == null) {
            Toast.makeText(this, "Carregando...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (modelSelection.getSelectedItem().toString().equals("Lampião")) {

            if (placedLamp == false) {
                // Create the Anchor.
                Anchor anchor = hitResult.createAnchor();
                anchorNode = new AnchorNode(anchor);
                anchorNode.setParent(arFragment.getArSceneView().getScene());

                // Create the transformable model and add it to the anchor.
                modelNodeLamp = new TransformableNode(arFragment.getTransformationSystem());
                //model.getScaleController().setMinScale(0.1f);
                //modelNodeLamp.setLocalScale(new Vector3(1f, 1f, 1f));

                modelNodeLamp.setParent(anchorNode);

                modelNodeLamp.setRenderable(this.SelectedModel)
                        .animate(false);

                lightSlide.setProgress((int) arFragment.getArSceneView()._environment.getIndirectLight().getIntensity());
                modelNodeLamp.select();
                placedLamp = true;
            } else {
                Toast.makeText(this, "Lampião já está na cena", Toast.LENGTH_SHORT).show();
            }
        }else if (modelSelection.getSelectedItem().toString().equals("Maria Bonita")) {
            if (placedMari == false) {
                // Create the Anchor.
                Anchor anchor = hitResult.createAnchor();
                anchorNode = new AnchorNode(anchor);
                anchorNode.setParent(arFragment.getArSceneView().getScene());

                // Create the transformable model and add it to the anchor.
                modelNodeMari = new TransformableNode(arFragment.getTransformationSystem());

                modelNodeMari.getScaleController().setMinScale(0.1f);
                modelNodeMari.getScaleController().setMaxScale(0.2f);
                modelNodeMari.setLocalScale(new Vector3(0.125f, 0.125f, 0.125f));
                modelNodeMari.setParent(anchorNode);
                modelNodeMari.setRenderable(this.SelectedModel)
                        .animate(false);

                lightSlide.setProgress((int) arFragment.getArSceneView()._environment.getIndirectLight().getIntensity());
                modelNodeMari.select();
                placedMari = true;
            } else {
                Toast.makeText(this, "Maria Bonita já está na cena", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void removeNode(View v){
        if(placedLamp == true) {
            modelNodeLamp.setParent(null);
            this.placedLamp = false;
            lightSlide.setProgress(30000);
        }
        if(placedMari == true) {
            modelNodeMari.setParent(null);
            this.placedMari = false;
            lightSlide.setProgress(30000);
        }
        //Toast.makeText(this, "Removido", Toast.LENGTH_SHORT).show();
    }

    public void modelAnimation(View v) {
        if (modelSelection.getSelectedItem().toString().equals("Lampião")) {
            if (placedLamp) {
                modelNodeLamp.setRenderable(this.SelectedModel)
                        .animate(false).start();
            }
        } else if (modelSelection.getSelectedItem().toString().equals("Maria Bonita")) {
            if (placedMari) {
                modelNodeMari.setRenderable(this.SelectedModel)
                        .animate(false).start();
            }
        }
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


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String text = modelSelection.getSelectedItem().toString();
            if (text.equals("Lampião")) {
                SelectedModel = LampModel;
            } else if (text.equals("Maria Bonita")) {
                SelectedModel = MariaModel;
            }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}