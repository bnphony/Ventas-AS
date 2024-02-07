package com.utc.ventas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;

public class GestionProductos extends AppCompatActivity {

    TextView txt_id_prod;
    EditText edt_nombre_prod, edt_precio_prod, edt_iva_prod, edt_stock_prod, edt_f_caducidad_prod, edt_descripcion_prod;
    ListView lista_productos;
    Button btn_fecha;
    int dia, mes, year; // Variables para almacenar la fecha

    BaseDeDatos bdd;

    // Conseguir la fecha del sistema
    Date fecha = new Date();
    // Dar un formato a la fecha
    CharSequence s1 = DateFormat.format("yyyy-MM-dd", fecha.getTime());
    String fechas_auxiliar[]; // Array para almacenar las partes de la fecha actual
    // Array para almacenar las partes de la fecha actual transformadas a entero
    int fechas[] = new int[3];
    String id_producto;
    double precio, iva, stock;
    String nombre, descripcion, f_caducidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_productos);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        edt_nombre_prod = (EditText) findViewById(R.id.edt_nombre_prod);
        edt_precio_prod = (EditText) findViewById(R.id.edt_precio_prod);
        edt_iva_prod = (EditText) findViewById(R.id.edt_iva_prod);
        edt_stock_prod = (EditText) findViewById(R.id.edt_stock_prod);
        edt_f_caducidad_prod = (EditText) findViewById(R.id.edt_f_caducidad_prod);
        edt_descripcion_prod = (EditText) findViewById(R.id.edt_descripcion_prod);

        txt_id_prod = (TextView) findViewById(R.id.txt_id_producto);
        bdd = new BaseDeDatos(getApplicationContext());

        edt_f_caducidad_prod.setKeyListener(null);

        // Separar el dia, mes y anio de la fecha
        fechas_auxiliar = String.valueOf(s1).split("-");
        // Transformar el dia, mes y anio en entero
        for(int i = 0; i < fechas_auxiliar.length; i++){
            fechas[i] = Integer.parseInt(fechas_auxiliar[i]);
        }
        // Colocar el id del proximo registro en un TextView
        Bundle parametrosExtra = getIntent().getExtras();
        if(parametrosExtra != null){
            try{
                id_producto = parametrosExtra.getString("id");
                nombre = parametrosExtra.getString("nombre");
                descripcion = parametrosExtra.getString("descripcion");
                precio = parametrosExtra.getDouble("precio");
                iva = parametrosExtra.getDouble("iva");
                stock = parametrosExtra.getDouble("stock");
                f_caducidad = parametrosExtra.getString("f_caducidad");

            }catch (Exception ex){
                Toast.makeText(getApplicationContext(), "Error Ocurrido: " + ex.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        txt_id_prod.setText("ID Producto: " + id_producto);
        edt_nombre_prod.setText(nombre);
        edt_descripcion_prod.setText(descripcion);
        edt_precio_prod.setText(precio + "");
        edt_iva_prod.setText(iva + "");
        edt_stock_prod.setText(stock + "");
        edt_f_caducidad_prod.setText(f_caducidad);

    }

    public void actualizarProducto(View view){
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
                        bdd.actualizarProducto(id_producto, nombre, descripcion, precio, iva, stock, f_caducidad); // Registra el Nuevo Producto en la Base de Datos

                        // Mensaje para informar que se registro correctamente el Nuevo Producto
                        Toast.makeText(getApplicationContext(), "Nuevo Producto Registrado, Correctamente!", Toast.LENGTH_SHORT).show();
                        // Actualizar el ID del proximo registro
                        txt_id_prod.setText("ID Producto: " + bdd.conseguirIdProducto());
                        finish();
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

    public void eliminarProducto(View view){
        AlertDialog.Builder dialogoEliminar = new AlertDialog.Builder(this)
                .setTitle("Eliminar Producto")
                .setMessage("Esta seguro de Eliminar el Producto Seleccionado?")
                .setPositiveButton("SI, Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        bdd.eliminarProducto(id_producto);
                        Toast.makeText(getApplicationContext(), "Producto Eliminado Correctamente!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("NO, Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "Eliminacion Cancelada!", Toast.LENGTH_SHORT).show();
                    }
                }).setCancelable(false);
        AlertDialog cuadroEliminar = dialogoEliminar.create();
        cuadroEliminar.show();
    }

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

    public boolean validar(String x){
        try{
            Double.parseDouble(x);
            return true;
        }catch (Exception e) {
            return false;
        }
    }

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
