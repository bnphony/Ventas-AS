package com.utc.ventas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MenuPrincipal extends AppCompatActivity {

    TextView txt_usuario;


    SharedPreferences preferencias;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);


        txt_usuario = (TextView) findViewById(R.id.txt_usuario);

        preferencias = getSharedPreferences("inicio_sesion", Context.MODE_PRIVATE);
        txt_usuario.setText("Bienvenido < " + preferencias.getString("nombre_usuario", "") + " >" );
        editor = preferencias.edit();
    }

    public void pantallaClientes(View view){
        Intent intent = new Intent(getApplicationContext(), Clientes.class);
        startActivity(intent);
    }

    public void pantallaProductos(View view){
        Intent intent = new Intent(getApplicationContext(), Productos.class);
        startActivity(intent);
    }

    public void pantallaVentas(View view){
        Intent intent = new Intent(getApplicationContext(), Ventas.class);
        startActivity(intent);
    }

    public void cerrarSesion(View view){
        finish();
        editor.putBoolean("sesion", false);
        editor.putString("nombre_usuario", "");
        editor.putString("password", "");
        editor.apply();
        Toast.makeText(getApplicationContext(), "Sesion Finalizada", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), InicioSesion.class);
        startActivity(intent);
    }
}
