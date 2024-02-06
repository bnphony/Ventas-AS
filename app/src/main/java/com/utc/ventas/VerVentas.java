package com.utc.ventas;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class VerVentas extends AppCompatActivity {

    ListView lista_ventas;
    ArrayList<String> ventas = new ArrayList<>();
    BaseDeDatos bdd;
    String id, titulo, fecha, cliente;
    double total;

    ArrayList<String> productos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_ventas);

        lista_ventas = (ListView) findViewById(R.id.lista_ventas);

        bdd = new BaseDeDatos(getApplicationContext());

        imprimirVentas();

    }

    public void imprimirVentas(){
        Cursor cursor = bdd.consultarVentas();

        if(cursor != null && cursor.getCount() > 0){
            do{
                System.out.println("LLEGO A LA CLASE VER VENTAS");
                id = cursor.getString(0);
                titulo = cursor.getString(1);
                fecha = cursor.getString(2);
                total = cursor.getDouble(3);
                cliente = cursor.getString(4);

                ventas.add(String.format("%5s%30s", id, titulo) + "\nFecha: " + fecha + " Total: $ " + total + " " +
                        "\nCliente: " + cliente);

                productos = bdd.consultarProductosVendidos(id);
                for(int i = 0; i < productos.size(); i++){
                    ventas.add(productos.get(i));
                }

            }while(cursor.moveToNext());
            ArrayAdapter<String> adaptadorVentas = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ventas);
            lista_ventas.setAdapter(adaptadorVentas);
        }



    }
}
