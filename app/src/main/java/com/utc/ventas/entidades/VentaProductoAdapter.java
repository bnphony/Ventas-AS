package com.utc.ventas.entidades;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.utc.ventas.R;

import java.util.ArrayList;

public class VentaProductoAdapter extends ArrayAdapter<Producto> {

    public VentaProductoAdapter(Context context, ArrayList<Producto> productoArrayList) {
        super(context, R.layout.venta_producto_item, productoArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View vista, @NonNull ViewGroup parent) {

        Producto producto = getItem(position);

        if (vista == null) {
            vista = LayoutInflater.from(getContext()).inflate(R.layout.venta_producto_item, parent, false);
        }

        ImageView img_producto = vista.findViewById(R.id.img_producto);
        TextView txt_nombre = vista.findViewById(R.id.txt_nombre);
        TextView txt_cantidad = vista.findViewById(R.id.txt_cantidad);
        TextView txt_precio = vista.findViewById(R.id.txt_precio);

        txt_nombre.setText(producto.getNombre());
        txt_cantidad.setText(String.valueOf(producto.getCantidad()));
        txt_precio.setText(String.valueOf(producto.getPrecio()));


        return vista;
    }
}
