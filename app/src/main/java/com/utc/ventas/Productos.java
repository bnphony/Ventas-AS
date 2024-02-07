package com.utc.ventas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.utc.ventas.entidades.Producto;
import com.utc.ventas.entidades.RangoNumeros;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Productos extends AppCompatActivity {
    // Entrada
    // Instanciar los objetos necesarios para la clase
    TextView txt_id_prod;
    EditText edt_nombre_prod, edt_precio_prod, edt_iva_prod, edt_stock_prod, edt_f_caducidad_prod, edt_descripcion_prod;
    Button btn_fecha;
    int dia, mes, year; // Variables para almacenar la fecha
    // Intanciar un array para almacenar los registros consultados de la Base de Datos
    ArrayList<String> listaProductos = new ArrayList<>();
    BaseDeDatos bdd; // Instanciar un objeto de la clase de Base de Datos

    // Salida
    private TableLayout tbl_productos;
    private TableRow fila;
    private Button btn_editar_tabla;
    int indexFila = -1;
    private ArrayList<Producto> listaProductosTabla = new ArrayList<>();
    private Cursor cursorTabla;

    // Para la Fila entera
    TableRow.LayoutParams layoutFila = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

    // Para cada columna
    TableRow.LayoutParams layoutNumFila = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
    TableRow.LayoutParams layoutNombre = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
    TableRow.LayoutParams layoutPrecio = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
    TableRow.LayoutParams layoutCantidad = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
    TableRow.LayoutParams layoutEditar = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);

    // Columnas de la tabla
    TextView txt_layoutNumFila, txt_layoutNombre, txt_layoutPrecio, txt_layoutCantidad, txt_layoutEditar;

    // Colores
    private int yellow_1;


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
        edt_iva_prod.setFilters(new InputFilter[] { new RangoNumeros(0.0, 100.0)});
        edt_stock_prod = (EditText) findViewById(R.id.edt_stock_prod);
        edt_f_caducidad_prod = (EditText) findViewById(R.id.edt_f_caducidad_prod);
        edt_descripcion_prod = (EditText) findViewById(R.id.edt_descripcion_prod);
        btn_fecha = (Button) findViewById(R.id.btn_fecha);

        yellow_1 = ContextCompat.getColor(Productos.this, R.color.yellow_1);

        tbl_productos = (TableLayout) findViewById(R.id.tbl_productos);

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

        graficarEncabezadosTabla();
        consultarProductosTabla();
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
                        consultarProductosTabla();
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
        consultarProductosTabla();
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


    // Proceso 10: Consultar Datos para la TablaLayout
    private void consultarProductosTabla() {
        listaProductosTabla.clear();
//        TableRow headerView = (TableRow) tbl_productos.getChildAt(0);
        tbl_productos.removeAllViews();
//        tbl_productos.addView(headerView);
        graficarEncabezadosTabla();
        cursorTabla = bdd.consultarProductos();
        if (cursorTabla != null) {
            do {
                String id = cursorTabla.getString(0);
                String nombre = cursorTabla.getString(1);
                double precio = cursorTabla.getDouble(3);
                double stock = cursorTabla.getDouble(5);
                int cantidad = (int) stock;
                listaProductosTabla.add(new Producto(Integer.parseInt(id), nombre, precio, cantidad));
                fila = new TableRow(this);
                fila.setBackgroundResource(R.drawable.fondo_fila);
                fila.setLayoutParams(layoutFila);

                txt_layoutNumFila = crearTextView(id, Color.BLACK, layoutNumFila);
                fila.addView(txt_layoutNumFila);
                txt_layoutNombre = crearTextView(nombre, Color.BLACK, layoutNombre);
                fila.addView(txt_layoutNombre);

                TableRow.LayoutParams paramsNombre = (TableRow.LayoutParams) txt_layoutNombre.getLayoutParams();
                paramsNombre.weight = 1;
                txt_layoutNombre.setLayoutParams(paramsNombre);
                txt_layoutPrecio = crearTextView("$ " + String.valueOf(precio), Color.BLACK, layoutPrecio);
                fila.addView(txt_layoutPrecio);
                txt_layoutCantidad = crearTextView(String.valueOf(cantidad), Color.BLACK, layoutCantidad);
                fila.addView(txt_layoutCantidad);

                txt_layoutEditar = crearTextView("Editar", Color.BLACK, layoutEditar);
                txt_layoutEditar.setBackgroundColor(yellow_1);
                txt_layoutEditar.setId(Integer.parseInt(id));
                fila.addView(txt_layoutEditar);
                txt_layoutEditar.setOnClickListener(new ButtonsOnClickListener());
                tbl_productos.addView(fila);

            } while (cursorTabla.moveToNext());
        } else {
            Toast.makeText(getApplicationContext(), "No existen productos registrados!", Toast.LENGTH_SHORT).show();
        }
    }

    // Proceso 11: Graficar Encabezados de la Tabla
    private void graficarEncabezadosTabla() {
        fila = new TableRow(this);
        fila.setBackgroundResource(R.drawable.fondo_app);
        fila.setLayoutParams(layoutFila);

        txt_layoutNumFila = crearTextView("N°", Color.BLACK, layoutNumFila);
        fila.addView(txt_layoutNumFila);
        txt_layoutNombre = crearTextView("Nombre", Color.BLACK, layoutNombre);
        fila.addView(txt_layoutNombre);
        txt_layoutPrecio = crearTextView("Precio", Color.BLACK, layoutPrecio);
        fila.addView(txt_layoutPrecio);
        txt_layoutCantidad = crearTextView("Uds.", Color.BLACK, layoutCantidad);
        fila.addView(txt_layoutCantidad);

        txt_layoutEditar = crearTextView("Editar", Color.BLACK, layoutEditar);
        fila.addView(txt_layoutEditar);
        tbl_productos.addView(fila);


    }

    // Proceso 12: Crear Elementos TextView
    private TextView crearTextView(String contenido, int textColor, ViewGroup.LayoutParams layoutParams) {
        TextView textView = new TextView(this);
        textView.setText(contenido);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(18);
        textView.setTextColor(textColor);
        textView.setPadding(10, 20, 10, 20);
        textView.setLayoutParams(layoutParams);
        return textView;
    }


    // Proceso 13: Controlar cuando se da TAP al boton EDITAR de un registro de la tabla
    class ButtonsOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View vista) {
            TextView editar = (TextView) vista;
            int id = editar.getId();
            Cursor productoObtenido = bdd.obtenerProducto(id);
            if (productoObtenido != null) {
                String idProducto = productoObtenido.getString(0);
                String nombreProducto = productoObtenido.getString(1);
                String descripcionProducto = productoObtenido.getString(2);
                double precioProducto = productoObtenido.getDouble(3);
                double ivaProducto = productoObtenido.getDouble(4);
                double stockProducto = productoObtenido.getDouble(5);
                String f_caducidadProducto = productoObtenido.getString(6);

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
        }
    }


}
