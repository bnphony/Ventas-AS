package com.utc.ventas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;


// EDITAR/ELIMINAR
/*
* 1) Editar Cliente
* 2) Eliminar Cliente
* */
public class EditarCliente extends AppCompatActivity {

    String id, cedula, apellido, nombre, telefono, direccion;

    TextView txt_idClienteEditar;
    EditText edt_cedulaClienteEditar, edt_apellidoClienteEditar, edt_nombreClienteEditar,
            edt_telefonoClienteEditar, edt_direccionClienteEditar;
    BaseDeDatos miBdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_cliente);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        txt_idClienteEditar = (TextView) findViewById(R.id.txt_id_cliente);
        edt_cedulaClienteEditar = (EditText) findViewById(R.id.edt_cedula);
        edt_apellidoClienteEditar = (EditText) findViewById(R.id.edt_apellido);
        edt_nombreClienteEditar = (EditText) findViewById(R.id.edt_nombre);
        edt_telefonoClienteEditar = (EditText) findViewById(R.id.edt_telefono);
        edt_direccionClienteEditar = (EditText) findViewById(R.id.edt_direccion);

        miBdd = new BaseDeDatos(getApplicationContext());


        Bundle parametrosExtra = getIntent().getExtras();
        if(parametrosExtra != null){
            try {
                id = parametrosExtra.getString("id");
                cedula = parametrosExtra.getString("cedula");
                apellido = parametrosExtra.getString("apellido");
                nombre = parametrosExtra.getString("nombre");
                telefono = parametrosExtra.getString("telefono");
                direccion = parametrosExtra.getString("direccion");
            }catch (Exception ex){
                Toast.makeText(getApplicationContext(), "Error al procesar la solicitud "  + ex.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        txt_idClienteEditar.setText("ID del Cliente: " + id);
        edt_cedulaClienteEditar.setText(cedula);
        edt_apellidoClienteEditar.setText(apellido);
        edt_nombreClienteEditar.setText(nombre);
        edt_telefonoClienteEditar.setText(telefono);
        edt_direccionClienteEditar.setText(direccion);
    }


    public void volver(View view){
        finish();
        Intent ventanaGestionClientes = new Intent(getApplicationContext(), Clientes.class);
        startActivity(ventanaGestionClientes);
    }

    public void eliminarCliente(View view){
        AlertDialog.Builder estructuraConfirmacion = new AlertDialog.Builder(this)
                .setTitle("Confirmacion")
                .setMessage("Esta seguro de eliminar el cliente seleccionado?")
                .setPositiveButton("SI, Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        miBdd.eliminarCliente(id);
                        Toast.makeText(getApplicationContext(), "Se elimino el Cliente Correctamente!", Toast.LENGTH_SHORT).show();
                        volver(null);
                    }
                })
                .setNegativeButton("NO, Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "Cancelado el proceso de Eliminacion", Toast.LENGTH_SHORT).show();
                    }
                }).setCancelable(true);
        AlertDialog cuadroDialogo = estructuraConfirmacion.create();
        cuadroDialogo.show();

    }

    public void actualizarCliente(View view){
        String cedula = edt_cedulaClienteEditar.getText().toString();
        String apellido = edt_apellidoClienteEditar.getText().toString();
        String nombre = edt_nombreClienteEditar.getText().toString();
        String telefono = edt_telefonoClienteEditar.getText().toString();
        String direccion = edt_direccionClienteEditar.getText().toString();

        if(!validarVacios(cedula, apellido, nombre, telefono, direccion)){
            miBdd.actualizarCliente(cedula, apellido, nombre, telefono, direccion, id);
            Toast.makeText(getApplicationContext(), "Cliente Actualizado Correctamente!", Toast.LENGTH_SHORT).show();
            volver(null);
        }else{
            Toast.makeText(getApplicationContext(), "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean validarVacios(String cedula, String apellido, String nombre, String telefono, String direccion){
        if(cedula.isEmpty()){
            edt_cedulaClienteEditar.setError("Debe ingresar un numero de Cedula");
            return true;
        }
        if(apellido.isEmpty()){
            edt_apellidoClienteEditar.setError("Debe ingresar un Apellido");
            return true;
        }
        if(nombre.isEmpty()){
            edt_nombreClienteEditar.setError("Debe ingresar un Nombre");
            return true;
        }
        if(telefono.isEmpty()){
            edt_telefonoClienteEditar.setError("Debe ingresar un numero de Telefono");
            return true;
        }
        if(direccion.isEmpty()){
            edt_direccionClienteEditar.setError("Debe ingresar una Direccion");
            return true;
        }

        return false;
    }
}
