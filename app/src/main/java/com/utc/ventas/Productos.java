package com.utc.ventas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Productos extends AppCompatActivity {
    // Entrada
    // Instanciar los objetos necesarios para la clase
    TextView txt_id_prod;
    EditText edt_nombre_prod, edt_precio_prod, edt_iva_prod, edt_stock_prod, edt_f_caducidad_prod, edt_descripcion_prod;
    ListView lista_productos;
    Button btn_fecha;
    int dia, mes, year; // Variables para almacenar la fecha
    // Intanciar un array para almacenar los registros consultados de la Base de Datos
    ArrayList<String> listaProductos = new ArrayList<>();
    BaseDeDatos bdd; // Instanciar un objeto de la clase de Base de Datos

    // Conseguir la fecha del sistema
    Date fecha = new Date();
    // Dar un formato a la fecha
    CharSequence s1 = DateFormat.format("yyyy-MM-dd", fecha.getTime());
    String fechas_auxiliar[]; // Array para almacenar las partes de la fecha actual
    // Array para almacenar las partes de la fecha actual transformadas a entero
    int fechas[] = new int[3];
    Cursor productos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Entrada
        // Mapear o relacionar los componentes logicos con los componentes graficos
        txt_id_prod = (TextView) findViewById(R.id.txt_id_producto);
        edt_nombre_prod = (EditText) findViewById(R.id.edt_nombre_prod);
        edt_precio_prod = (EditText) findViewById(R.id.edt_precio_prod);
        edt_iva_prod = (EditText) findViewById(R.id.edt_iva_prod);
        edt_stock_prod = (EditText) findViewById(R.id.edt_stock_prod);
        edt_f_caducidad_prod = (EditText) findViewById(R.id.edt_f_caducidad_prod);
        edt_descripcion_prod = (EditText) findViewById(R.id.edt_descripcion_prod);
        btn_fecha = (Button) findViewById(R.id.btn_fecha);
        lista_productos = (ListView) findViewById(R.id.lista_productos);
        bdd = new BaseDeDatos(getApplicationContext());
        // Deshabilitar el EditText de la fecha de caducidad del producto
        //edt_f_caducidad_prod.setEnabled(false);
        //keyListener = edt_f_caducidad_prod.getKeyListener();
        edt_f_caducidad_prod.setKeyListener(null);
        //edt_f_caducidad_prod.setFocusable(false);


        // Separar el dia, mes y anio de la fecha
        fechas_auxiliar = String.valueOf(s1).split("-");
        // Transformar el dia, mes y anio en entero
        for(int i = 0; i < fechas_auxiliar.length; i++){
            fechas[i] = Integer.parseInt(fechas_auxiliar[i]);
        }
        // Colocar el id del proximo registro en un TextView
        txt_id_prod.setText("ID Producto: " + bdd.conseguirIdProducto());
        // Llamar al metodo para consutlar los productos de la Base de Datos
        consultarProductos();

        lista_productos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                productos.moveToPosition(position);
                String idProducto = productos.getString(0);
                String nombreProducto = productos.getString(1);
                String descripcionProducto = productos.getString(2);
                double precioProducto = productos.getDouble(3);
                double ivaProducto = productos.getDouble(4);
                double stockProducto = productos.getDouble(5);
                String f_caducidadProducto = productos.getString(6);

                Intent gestion_productos = new Intent(getApplicationContext(), GestionProductos.class);
                gestion_productos.putExtra("id", idProducto);
                gestion_productos.putExtra("nombre", nombreProducto);
                gestion_productos.putExtra("descripcion", descripcionProducto);
                gestion_productos.putExtra("precio", precioProducto);
                gestion_productos.putExtra("iva", ivaProducto);
                gestion_productos.putExtra("stock", stockProducto);
                gestion_productos.putExtra("f_caducidad", f_caducidadProducto);
                startActivity(gestion_productos);
            }
        });
    }
    // Proceso 1: Metodo para registrar los nuevos productos
    public void registrarProducto(View view){
        // Obtener el contenido de los EditText y guardarlos en variables de tipo String
        String nombre = edt_nombre_prod.getText().toString();
        String precio_string = edt_precio_prod.getText().toString();
        String iva_string = edt_iva_prod.getText().toString();
        String stock_string = edt_stock_prod.getText().toString();
        String f_caducidad = edt_f_caducidad_prod.getText().toString();
        String descripcion = edt_descripcion_prod.getText().toString();
        String auxiliar[];
        // Condicion para validar que todos los campos esten llenos
        if(validarEspaciosBlanco(nombre, precio_string, iva_string, stock_string, f_caducidad, descripcion)){
            edt_f_caducidad_prod.setError(null); // Quitar el mensaje de error
            // Condicion para validar que los campos precio, iva y stock contienen numeros
            if(validarDecimal(precio_string, iva_string, stock_string)){
                // Convertir string a double
                double precio = Double.parseDouble(precio_string);
                double iva = Double.parseDouble(iva_string);
                double stock = Double.parseDouble(stock_string);
                // Condicion para valir que el precio y cantidad sean mayores a 0
                if(validarPrecioCantidad(precio, stock)){
                    auxiliar = f_caducidad.split("-"); // Separar la fecha en partes dia, mes y anio
                    // Condicion para comprobar si la fecha de caducidad es igual o mayor a la fecha actual
                    if(Integer.parseInt(auxiliar[0]) >= fechas[0] && Integer.parseInt(auxiliar[1]) >= fechas[1]
                            && Integer.parseInt(auxiliar[2]) >= fechas[2]){
                        edt_f_caducidad_prod.setError(null); // Quitar el mensjae de error
                        bdd.registrarProducto(nombre, descripcion, precio, iva, stock, f_caducidad); // Registra el Nuevo Producto en la Base de Datos
                        limpiarCampos(); // Llamar a la funcion para limpiar los campos
                        // Mensaje para informar que se registro correctamente el Nuevo Producto
                        Toast.makeText(getApplicationContext(), "Nuevo Producto Registrado, Correctamente!", Toast.LENGTH_SHORT).show();
                        // Actualizar el ID del proximo registro
                        txt_id_prod.setText("ID Producto: " + bdd.conseguirIdProducto());
                        consultarProductos(); // Actualizar la Lista de Productos
                    }else{
                        // Mostrar un mensaje de error en el EditText
                        edt_f_caducidad_prod.setError("La fecha debe ser mayor o igual a la fecha actual");
                        // Mensaje para informar que no se cumple la condicion de la fecha
                        Toast.makeText(getApplicationContext(), "La fecha debe ser igual o mayor al dia actual", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    // Mensaje para informqr que el precio y stock no cumplen la condicion de cantidad minima
                    Toast.makeText(getApplicationContext(), "El precio y stock deben ser mayores a 0", Toast.LENGTH_SHORT).show();
                }
            }else{
                // Mensaje para informar que el precio, iva y stock no cumplen la condicion del tipo de dato
                Toast.makeText(getApplicationContext(), "El <Precio>, <IVA>, <Stock> deben ser numeros", Toast.LENGTH_SHORT).show();
            }
        }else{
            // Mensaje para informar que los campos no deben estar vacios
            Toast.makeText(getApplicationContext(), "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        consultarProductos();
    }

    // Proceso 2: Metodo para consultar todos los productos de la Base de Datos
    public void consultarProductos(){
        listaProductos.clear(); // Limpiar todos lo datos que contenga la lista de productos
        // Crear un objeto cursor para almacenar todos los productos obtenidos de la base de datos
        productos = bdd.consultarProductos();
        if(productos != null){ // Comprobar que se hayan encontrado registros en la Base de Datos
            // Ciclo Do-While para llenar la lista con los registros encontrados en la Base de Datos
            do{
                String id = productos.getString(0);
                String nombre = productos.getString(1);
                double precio = productos.getDouble(3);
                double stock = productos.getDouble(5);
                listaProductos.add(id + "\nNombre: "+nombre+"\nPrecio: $ "+ precio + "\nStock: " + stock); // Agregar un registro a la Lista
                // Asignar un formato y el contenido al Adaptador de la lista
                ArrayAdapter<String> adaptadorProductos = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaProductos);
                // Asignar el adaptador a la Lista de Productos
                lista_productos.setAdapter(adaptadorProductos);
            }while(productos.moveToNext()); // Condicion para recorrer el cursor
        }else{
            // Mensaje para informar que no se encontraron registros en el Cursor
            Toast.makeText(getApplicationContext(), "No existen Productos registrados", Toast.LENGTH_SHORT).show();
        }
    }


    // Proceso 5: Metodo para cerrar la Actividad
    public void cerrarActividad(View view){
        finish();
    }

    // Proceso 6: Metodo para limpiar todos los campos
    public void limpiarCampos(){
        edt_nombre_prod.setText("");
        edt_precio_prod.setText("");
        edt_iva_prod.setText("");
        edt_stock_prod.setText("");
        edt_f_caducidad_prod.setText("");
        edt_descripcion_prod.setText("");
    }

    // Proceso 7: Metodo para validar la cantidad minima del precio y el stock
    public boolean validarPrecioCantidad(double precio, double stock){
        boolean validar = true;
        if(precio <= 0){
            edt_precio_prod.setError("El precio debe ser mayor a 0");
            validar = false;
        }
        if(stock <= 0){
            edt_stock_prod.setError("La cantidad debe ser mayor a 0");
            validar = false;
        }
        return validar;
    }

    // Proceso 8: Metodo para validar que no haya campos vacios
    public Boolean validarEspaciosBlanco(String nombre, String precio, String iva, String stock, String f_caducidad, String descripcion){
        boolean valido = true;
        if(nombre.isEmpty()){
            edt_nombre_prod.setError("Debe Ingresar el Nombre del Producto");
            valido = false;
        }
        if(precio.isEmpty()){
            edt_precio_prod.setError("Debe Ingresar un Precio del Producto");
            valido = false;
        }
        if(iva.isEmpty()){
            edt_iva_prod.setError("Debe Ingresar el IVA");
            valido = false;
        }
        if(stock.isEmpty()){
            edt_stock_prod.setError("Debe Ingresar la cantidad del existencias");
            valido = false;
        }
        if(f_caducidad.isEmpty()){
            edt_f_caducidad_prod.setError("Debe Ingresar la Fecha de Caducidad");
            // edt_f_caducidad_prod.setKeyListener(keyListener);
            valido = false;
        }
        if(descripcion.isEmpty()){
            edt_descripcion_prod.setError("Debe Ingresar una Descripcion del Producto");
            valido = false;
        }
        return valido;
    }

    // Proceso 9: Metodo para Validar el Tipo de Dato de los campos
    public boolean validarDecimal(String precio, String iva, String stock){
        boolean validar = true;
        if(!validar(precio)){
            edt_precio_prod.setError("El precio debe ser un Numero");
            validar = false;
        }
        if(!validar(iva)){
            edt_iva_prod.setError("El IVA debe ser un Numero");
            validar = false;
        }
        if(!validar(stock)){
            edt_stock_prod.setError("La cantidad debe ser un Numero");
            validar = false;
        }
        return validar;
    }


    // Proceso 3: Valicacion de los datos tipo Double
    public boolean validar(String x){
        try{
            Double.parseDouble(x);
            return true;
        }catch (Exception e) {
            return false;
        }
    }


    // Proceso 4: Metodo para seleccionar la fecha


    public void seleccionarFecha(View view){
        final Calendar c = Calendar.getInstance();
        dia = c.get(Calendar.DAY_OF_MONTH);
        mes = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                edt_f_caducidad_prod.setText(y + "-" + (m + 1) + "-" + d);

            }
        }, year, mes, dia);
        datePickerDialog.show();
    }
}
