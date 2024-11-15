package com.utc.ventas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 3000;
    // Creacion de Variables
    Animation top_anim, bottom_anim;

    ImageView img;
    TextView ventas;
    ConstraintLayout contenedor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        // Mapear o instanciar los componentes

        top_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_animation);
        bottom_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_animation);

        img = (ImageView) findViewById(R.id.img_1);
        ventas = (TextView) findViewById(R.id.txt_ventas);

        contenedor = (ConstraintLayout) findViewById(R.id.c_contenedor_1);


        img.setAnimation(top_anim);
        ventas.setAnimation(bottom_anim);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, InicioSesion.class);
                Pair[] pairs = new Pair[3];
                pairs[0] = new Pair<View, String>(img, "logo_imagen");
                pairs[1] = new Pair<View, String>(ventas, "logo_texto");
                pairs[2] = new Pair<View, String>(contenedor, "tran_contenedor");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                startActivity(intent, options.toBundle());


            }
        }, SPLASH_SCREEN);

    }
}
