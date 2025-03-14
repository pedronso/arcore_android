package com.example.sceneform_maintained_test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.CamcorderProfile;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import com.google.ar.sceneform.animation.ModelAnimator;
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

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
    private PhotoSaver photoSaver = new PhotoSaver(this);
    private VideoRecorder videoRecorder = new VideoRecorder(this);
    private boolean isRecording = false;

    private static final int pic_id = 123;

    private FloatingActionButton take_photo_id;
    private ImageView click_image_id;
    static Bitmap bitmap;

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
        FloatingActionButton take_photo_id = findViewById(R.id.fab);

        //ImageView click_image_id = findViewById(R.id.click_image);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.models, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelSelection.setAdapter(adapter);
        modelSelection.setOnItemSelectedListener(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        loadModels();
        lightChange();
        setupFab();
        //doPhotoPrintScreen();
    }

    public void setupFab(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(!isRecording) {
            if (fab != null) {
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (modelNodeLamp != null && modelNodeLamp.isSelected())
                            modelNodeLamp.getTransformationSystem().selectNode(null);
                        if (modelNodeMari != null && modelNodeMari.isSelected()) {
                            modelNodeMari.getTransformationSystem().selectNode(null);
                        }
                        arFragment.getArSceneView().getPlaneRenderer().setVisible(false);

                        view.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                photoSaver.takePhoto(arFragment.getArSceneView(), new ImageResult() {
                                    @Override
                                    public void onResult(Bitmap bitmap) {

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent n = new Intent(ARActivity.this, ImageShareActivity.class);
                                                ARActivity.bitmap = bitmap;
                                                startActivity(n);
                                            }
                                        });


                                    }
                                });
                            }
                        }, 100);
                        view.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                arFragment.getArSceneView().getPlaneRenderer().setVisible(true);
                            }
                        }, 100);

                    }
                });
            }
        }

        if(true){
            doPhotoPrintScreen();
        }
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

        photoSaver =  new PhotoSaver(this);
        videoRecorder = new VideoRecorder(this);
        videoRecorder.sceneView = arFragment.getArSceneView();
        videoRecorder.setVideoQuality(CamcorderProfile.QUALITY_1080P, getResources().getConfiguration().orientation);
    }

    public void loadModels() {
        WeakReference<ARActivity> weakActivity = new WeakReference<>(this);
        ModelRenderable.builder()
                .setSource(this, R.raw.lamp_final)
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
                .setSource(this, R.raw.maria_final)
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
                modelNodeLamp.getScaleController().setMinScale(0.2f);
                modelNodeLamp.getScaleController().setMaxScale(0.5f);
                modelNodeLamp.setLocalScale(new Vector3(0.33f, 0.33f, 0.33f));

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

                modelNodeMari.getScaleController().setMinScale(0.08f);
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
                ModelAnimator.ofAnimation(modelNodeLamp.getRenderableInstance(), "Chouched").start();
            }
        } else if (modelSelection.getSelectedItem().toString().equals("Maria Bonita")) {
            if (placedMari) {
                ModelAnimator.ofAnimation(modelNodeMari.getRenderableInstance(), "Chouching.001").start();
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

    protected void doPhotoPrintScreen() {

        File imgFile = new File("DCIM/MuseuDoCangacoAR/20221101070533_screenshot.jpg");
        //if (imgFile.exists()) {
        //Bitmap photo = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        ImageView click_image_id = findViewById(R.id.click_image);
        click_image_id.setImageURI(Uri.fromFile(imgFile));
        ContentResolver resolver = getApplicationContext()
                .getContentResolver();

        // "rw" for read-and-write;
        // "rwt" for truncating or overwriting existing file contents.
        String readOnlyMode = "r";
        try (ParcelFileDescriptor pfd =
                     resolver.openFileDescriptor(Uri.fromFile(imgFile)

                             , readOnlyMode)) {
            // Perform operations on "pfd".
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}

