package com.utc.ventas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.utc.ventas.entidades.AdaptadorClientes;

import java.util.ArrayList;
import java.util.Arrays;

// GESTIONAR CLIENTES
/*
* 1) Registrar Clientes
* 2) Lista de Clientes
*
* */
public class Clientes extends AppCompatActivity {

    EditText edt_cedula, edt_nombre, edt_apellido, edt_telefono, edt_direccion;
    BaseDeDatos miBdd;

    ListView lista_clientes;

    ArrayList<Cliente> arrayClientes = new ArrayList<Cliente>();

    Cursor clientesObtenidos;

    AdaptadorClientes adaptadorClientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        edt_cedula = (EditText) findViewById(R.id.edt_cedula);
        edt_nombre = (EditText) findViewById(R.id.edt_nombre);
        edt_apellido = (EditText) findViewById(R.id.edt_apellido);
        edt_telefono = (EditText) findViewById(R.id.edt_telefono);
        edt_direccion = (EditText) findViewById(R.id.edt_direccion);

        lista_clientes = (ListView) findViewById(R.id.lista_clientes);

        miBdd = new BaseDeDatos(getApplicationContext());

        consultarDatos();


    }


    @Override
    protected void onResume() {
        super.onResume();
        consultarDatos();
    }

    public void registrarCliente(View view){
        String cedula = edt_cedula.getText().toString();
        String nombre = edt_nombre.getText().toString();
        String apellido = edt_apellido.getText().toString();
        String telefono = edt_telefono.getText().toString();
        String direccion = edt_direccion.getText().toString();

        if(!validarVacios(cedula, nombre, apellido, telefono, direccion)){
            miBdd.agregarCliente(cedula, apellido, nombre, telefono, direccion);
            Toast.makeText(getApplicationContext(), "Cliente Registrado Correctamente!", Toast.LENGTH_SHORT).show();
            limpiar(null);
            consultarDatos();
        }else{
            Toast.makeText(getApplicationContext(), "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validarVacios(String cedula, String nombre, String apellido, String telefono, String direccion){
        if(cedula.isEmpty()){
            edt_cedula.setError("Debe escribir su Cedula");
            return true;
        }
        if(nombre.isEmpty()){
            edt_nombre.setError("Debe escribir su Nombre");
            return true;
        }
        if(apellido.isEmpty()){
            edt_apellido.setError("Debe escribir su Apellido");
            return true;
        }
        if(telefono.isEmpty()){
            edt_telefono.setError("Debe escribir su Telefono");
            return true;
        }
        if(direccion.isEmpty()){
            edt_direccion.setError("Debe escribir su Direccion");
            return true;
        }
        return false;
    }

    public void limpiar(View view){
        edt_cedula.setText("");
        edt_nombre.setText("");
        edt_apellido.setText("");
        edt_telefono.setText("");
        edt_direccion.setText("");
        onBackPressed();
    }


    public void consultarDatos() {
        arrayClientes.clear();
        clientesObtenidos = miBdd.consultarClientes();
        if (clientesObtenidos != null && clientesObtenidos.getCount() > 0) {
            clientesObtenidos.moveToFirst();
            do {
                int idCliente = clientesObtenidos.getInt(0);
                String nombreCliente = clientesObtenidos.getString(3);
                String apellidoCliente = clientesObtenidos.getString(2);
                arrayClientes.add(new Cliente(idCliente, nombreCliente, apellidoCliente));
            } while (clientesObtenidos.moveToNext());
            adaptadorClientes = new AdaptadorClientes(this, arrayClientes, miBdd);
            lista_clientes.setAdapter(adaptadorClientes);
        }
    }

}
