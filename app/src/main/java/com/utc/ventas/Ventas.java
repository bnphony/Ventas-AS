package com.utc.ventas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


import java.util.ArrayList;
import java.util.Date;

public class Ventas extends AppCompatActivity implements VenderProducto.ExampleDialogListener {

    Button btn_agregar_productos;
    Spinner sp_clientes, sp_estado;
    ListView lista_productos1;
    TextView txt_id_venta, txt_total, txt_fecha_venta, txt_encabezado;
    EditText edt_observacion, edt_titulo_ven;

    BaseDeDatos bdd;
    Cursor clientesEncontrados;
    ArrayList<Venta> ventas = new ArrayList<>();
    private Venta venta;
    double total = 0;
    Date fecha = new Date();
    CharSequence s1 = DateFormat.format("yyyy-MM-dd, hh:mm:ss", fecha.getTime());
    private Cliente cliente;
    ArrayList<Cliente> clientes = new ArrayList<>();
    private int id_venta;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        sp_clientes = (Spinner) findViewById(R.id.sp_clientes);
        bdd = new BaseDeDatos(getApplicationContext());
        btn_agregar_productos = (Button) findViewById(R.id.btn_agregar_productos);
        lista_productos1 = (ListView) findViewById(R.id.lista_productos1);
        txt_id_venta = (TextView) findViewById(R.id.txt_id_venta);
        txt_total = (TextView) findViewById(R.id.txt_total);
        txt_fecha_venta = (TextView) findViewById(R.id.txt_fecha_venta);
        edt_observacion = (EditText) findViewById(R.id.edt_observacion);
        edt_titulo_ven = (EditText) findViewById(R.id.edt_titulo_ven);
        sp_estado = (Spinner) findViewById(R.id.sp_estado);
        ArrayList<String> estados = new ArrayList<>();
        estados.add("Cancelado");
        estados.add("Pendiente");
        ArrayAdapter<String> adaptadorEstados = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, estados);
        sp_estado.setAdapter(adaptadorEstados);

        //

        cargarClientes();


        //listaProductos1.add(String.format("%3s%10s%10s%7s","ID", " NOMBRE ", " CANTIDAD ",  " SUB TOTAL "));

        id_venta = bdd.conseguirIdVenta();
        if(id_venta == 0){
            id_venta = 1;
        }
        txt_id_venta.setText("ID de la Venta: " + id_venta);
        txt_fecha_venta.setText("Fecha: " + s1);
        edt_titulo_ven.setText("Venta N" + id_venta);

        btn_agregar_productos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager dialogo = getSupportFragmentManager();
                VenderProducto v = new VenderProducto();
                v.show(dialogo, "Alerta");
            }
        });

    }

    public void realizarVenta(View view){
        String fecha = (String) s1;
        int posicion = sp_clientes.getSelectedItemPosition();
        // Si no hay clientes no se comprueba lo demas
        if (posicion != AdapterView.INVALID_POSITION) {
            cliente = clientes.get(posicion);
            String titulo = edt_titulo_ven.getText().toString();
            String observacion = edt_observacion.getText().toString();
            if(observacion.equals("")){
                observacion = "Sin Observaciones";
            }
            String estado = sp_estado.getSelectedItem().toString();
            if(!titulo.isEmpty()){
                if(sp_clientes.getSelectedItemPosition() != 0){
                    if(ventas.size() > 0){
                        boolean registroVenta = bdd.registrarVenta(titulo, fecha, estado, total, observacion, cliente.getIdCliente());
                        if(registroVenta){
                            for(int i = 0; i < ventas.size(); i++){
                                venta = ventas.get(i);
                                bdd.registrarVentas(venta.getCantidad(), venta.getSubTotal(), venta.getIdProducto(), id_venta);
                                bdd.actualizarCantidadProducto(venta.getIdProducto(), venta.getCantidad());
                            }
                            Toast.makeText(getApplicationContext(), "Venta Realizada Correctamente!", Toast.LENGTH_SHORT).show();
                            limpiar();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Debe vender por lo menos 1 Producto", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(getApplicationContext(), "Debe escoger un Cliente", Toast.LENGTH_SHORT).show();
                }


            }else{
                edt_titulo_ven.setError("Debe colocar un Titulo a la Venta");
            }
        } else {
            Toast.makeText(getApplicationContext(), "Debe escoger un Cliente", Toast.LENGTH_SHORT).show();
        }

    }

    public void limpiar(){
        id_venta = bdd.conseguirIdVenta();
        txt_id_venta.setText("ID de Venta: " + id_venta);
        edt_titulo_ven.setText("");
        sp_clientes.setSelection(0);
        ventas.clear();
        ArrayAdapter<Venta> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ventas);
        lista_productos1.setAdapter(adapter);

        txt_total.setText("");
        edt_observacion.setText("");
    }

    //
    //
    public void cargarClientes(){

        cliente = new Cliente();
        cliente.setIdCliente(0);
        cliente.setNombreCliente("Seleccione un Cliente");
        cliente.setApellidoCliente("");
        clientes.add(cliente);
        clientesEncontrados = bdd.consultarClientes();

        if(clientesEncontrados != null && clientesEncontrados.getCount() > 0){
            do{
                cliente = new Cliente();
                cliente.setIdCliente(clientesEncontrados.getInt(0));
                cliente.setNombreCliente(clientesEncontrados.getString(3));
                cliente.setApellidoCliente(clientesEncontrados.getString(2));
                clientes.add(cliente);
                ArrayAdapter<Cliente> adaptadorSpinnerCliente = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, clientes);
                sp_clientes.setAdapter(adaptadorSpinnerCliente);
            }while(clientesEncontrados.moveToNext());
        }else{
            Toast.makeText(getApplicationContext(), "No se encontraron Clientes", Toast.LENGTH_SHORT).show();
        }
    }

    public void guardarAxiliar(View view){
        int posicion = sp_clientes.getSelectedItemPosition();
        if(posicion > 0){
            clientesEncontrados.moveToPosition(posicion - 1);
            String nombre = clientesEncontrados.getString(3);
            String apellido = clientesEncontrados.getString(2);
            Toast.makeText(getApplicationContext(), "Guargar el cliente: " + nombre + " " + apellido, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "Seleccione un Cliente", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    @Override
    public void applyText(String idProducto, String nombreProducto, double cantidad, double subTotal) {
        ventas.add(v);
        listaProductos1.add(String.format("%3s%15s%15s%10s",idProducto, nombreProducto, cantidad, subTotal));
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaProductos1);
        lista_productos1.setAdapter(adaptador);

    }
     */

    // Aqui se actuliza la lista de acuerdo a lo hecho en el cuadro de dialogo
    @Override
    public void applyText(Venta venta) {
        ventas.add(venta);
        ArrayAdapter<Venta> adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ventas);
        lista_productos1.setAdapter(adaptador);
        total += venta.getSubTotal();
        txt_total.setText("Total: $ " + total);
    }


    public void pantallaRegistroVentas(View view){
        Intent intent = new Intent(getApplicationContext(), VerVentas.class);
        startActivity(intent);
    }
}
