package com.utc.ventas.entidades;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.utc.ventas.BaseDeDatos;
import com.utc.ventas.Cliente;
import com.utc.ventas.EditarCliente;
import com.utc.ventas.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AdaptadorClientes extends BaseAdapter {

    private ArrayList<Cliente> lista = new ArrayList<Cliente>();
    private Cliente cliente;
    private Activity a;
    private int id = 0;
    private BaseDeDatos bdd;

    public AdaptadorClientes(Activity a, ArrayList<Cliente> lista, BaseDeDatos bdd) {
        this.a = a;
        this.lista = lista;
        this.bdd = bdd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int i) {
        cliente = lista.get(i);
        return null;
    }

    @Override
    public long getItemId(int i) {
        cliente = lista.get(i);
        return cliente.getIdCliente();
    }

    @Override
    public View getView(int posicion, View view, ViewGroup viewGroup) {
        View vista = view;
        if (vista == null) {
            LayoutInflater li = (LayoutInflater)  a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vista = li.inflate(R.layout.item_cliente, null);
        }

        cliente = lista.get(posicion);

        TextView txt_id = (TextView) vista.findViewById(R.id.txt_id_cli);
        TextView txt_nombre = (TextView) vista.findViewById(R.id.txt_nombre_cli);

        txt_id.setText("ID: " + cliente.getIdCliente());
        txt_nombre.setText(String.format("NOMBRE: %s %s", cliente.getNombreCliente(), cliente.getApellidoCliente()));
        vista.setTag(cliente.getIdCliente());

        vista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = Integer.parseInt(view.getTag().toString());
                Cursor clienteObtenido = bdd.obtenerCliente(id);
                if (clienteObtenido != null) {
                    String idCliente = clienteObtenido.getString(0);
                    String cedulaCliente = clienteObtenido.getString(1);
                    String apellidoCliente = clienteObtenido.getString(2);
                    String nombreCliente = clienteObtenido.getString(3);
                    String telefonoCliente = clienteObtenido.getString(4);
                    String direccionCliente = clienteObtenido.getString(5);

                    Intent gestion_cliente = new Intent(view.getContext(), EditarCliente.class);
                    gestion_cliente.putExtra("id", idCliente);
                    gestion_cliente.putExtra("cedula", cedulaCliente);
                    gestion_cliente.putExtra("apellido", apellidoCliente);
                    gestion_cliente.putExtra("nombre", nombreCliente);
                    gestion_cliente.putExtra("telefono", telefonoCliente);
                    gestion_cliente.putExtra("direccion", direccionCliente);
                    view.getContext().startActivity(gestion_cliente);
                }
            }
        });


        return vista;
    }
}
