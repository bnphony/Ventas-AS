package com.utc.ventas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

/*
* El adaptador para la lista de productos escogidos para la venta
* */

public class VenderProducto extends DialogFragment {
    final ArrayList<String> listaProductos = new ArrayList<>();
    BaseDeDatos bdd;
    ListView lista_productos;
    Cursor productos;
    double cantidad, precioProducto, ivaProducto, stockProducto, subTotal;
    Button btn_aceptar, btn_cancelar;
    ExampleDialogListener exampleDialogListener;
    String nombreProducto;
    int idProducto;


    Venta venta;
    ArrayList<Venta> ventas;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.vender_productos, null);
        final EditText edt_cantidad =  (EditText)  view.findViewById(R.id.edt_cantidad);
        final TextView txt_producto = (TextView) view.findViewById(R.id.txt_producto_seleccionado);
        lista_productos = (ListView) view.findViewById(R.id.lista_productos);
        btn_aceptar = (Button) view.findViewById(R.id.btn_aceptar);
        btn_cancelar = (Button) view.findViewById(R.id.btn_cancelar);

        bdd = new BaseDeDatos(getActivity());

        consultarProductos();

        lista_productos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                productos.moveToPosition(position);

                idProducto  = productos.getInt(0);
                nombreProducto = productos.getString(1);
                precioProducto = productos.getDouble(3);
                ivaProducto = productos.getDouble(4);
                stockProducto = productos.getDouble(5);

                txt_producto.setText(nombreProducto);

                Toast.makeText(getActivity(), "Producto: " + nombreProducto, Toast.LENGTH_SHORT).show();

            }
        });





        builder.setView(view)
            .setCancelable(false);

        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cantidad_1 = edt_cantidad.getText().toString();

                if (!cantidad_1.isEmpty()) {
                    cantidad = Double.parseDouble(cantidad_1);
                    subTotal = cantidad * precioProducto;
                    venta = new Venta();
                    venta.setId_producto(idProducto);
                    venta.setNombre_producto(nombreProducto);
                    venta.setCantidad(cantidad);
                    venta.setSubTotal(subTotal);
                    if(cantidad <= stockProducto){
                        Toast.makeText(getActivity(), "Stock Restante: " + (stockProducto - cantidad), Toast.LENGTH_SHORT).show();
                        exampleDialogListener.applyText(venta);
                        dismiss();
                    }else{
                        Toast.makeText(getActivity(), "Error de Cantidad", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Alerta!, Debe ingresar una cantidad!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        return builder.create();
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        exampleDialogListener = (ExampleDialogListener) activity;
        try{

        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " debe implementar ExampleDialogListener");
        }
    }

    // Aqui se ejecuta la funcion que tiene la pantalla principal
    // la funcion llamada en la pantalla principal, actualiza la lista
    public interface ExampleDialogListener{
        void applyText(Venta venta);
    }

    public void consultarProductos(){
        listaProductos.clear(); // Limpiar todos lo datos que contenga la lista de productos
        // Crear un objeto cursor para almacenar todos los productos obtenidos de la base de datos
        productos = bdd.consultarProductos();
        if(productos != null && productos.getCount() > 0){ // Comprobar que se hayan encontrado registros en la Base de Datos
            // Ciclo Do-While para llenar la lista con los registros encontrados en la Base de Datos
            do{
                String id = productos.getString(0);
                String nombre = productos.getString(1);
                double precio = productos.getDouble(3);
                listaProductos.add(id + "\nNombre: "+nombre+"\nPrecio: $ "+ precio); // Agregar un registro a la Lista
                // Asignar un formato y el contenido al Adaptador de la lista
                ArrayAdapter<String> adaptadorProductos = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listaProductos);
                // Asignar el adaptador a la Lista de Productos
                lista_productos.setAdapter(adaptadorProductos);
                System.out.println("Encontrado Producto: " + nombre);
            }while(productos.moveToNext()); // Condicion para recorrer el cursor

        }else{
            // Mensaje para informar que no se encontraron registros en el Cursor
            Toast.makeText(getActivity(), "No existen Productos registrados", Toast.LENGTH_SHORT).show();
        }
    }
}
