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

import java.util.ArrayList;

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
    ArrayList<String> listaClientes = new ArrayList<>();

    Cursor clientesObtenidos;

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

        lista_clientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                clientesObtenidos.moveToPosition(position);
                String idCliente = clientesObtenidos.getString(0);
                String cedulaCliente = clientesObtenidos.getString(1);
                String apellidoCliente = clientesObtenidos.getString(2);
                String nombreCliente = clientesObtenidos.getString(3);
                String telefonoCliente = clientesObtenidos.getString(4);
                String direccionCliente = clientesObtenidos.getString(5);

                Intent gestion_cliente = new Intent(getApplicationContext(), EditarCliente.class);
                gestion_cliente.putExtra("id", idCliente);
                gestion_cliente.putExtra("cedula", cedulaCliente);
                gestion_cliente.putExtra("apellido", apellidoCliente);
                gestion_cliente.putExtra("nombre", nombreCliente);
                gestion_cliente.putExtra("telefono", telefonoCliente);
                gestion_cliente.putExtra("direccion", direccionCliente);
                startActivity(gestion_cliente);
                finish();

            }
        });

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
        finish();

    }

    public void consultarDatos(){
        listaClientes.clear();
        clientesObtenidos = miBdd.consultarClientes();
        if(clientesObtenidos != null && clientesObtenidos.getCount() > 0) {
            do{
                String id = clientesObtenidos.getString(0).toString();
                String apellido = clientesObtenidos.getString(2).toString();
                String nombre = clientesObtenidos.getString(3).toString();

                listaClientes.add("ID: " + id + "\nNombre: " + nombre + " " + apellido);
                ArrayAdapter<String> adaptadorClientes = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaClientes);
                lista_clientes.setAdapter(adaptadorClientes);
            }while(clientesObtenidos.moveToNext());
        }else{
            Toast.makeText(getApplicationContext(), "No existen clientes registrados", Toast.LENGTH_SHORT).show();
        }
    }
}
