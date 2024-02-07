package com.utc.ventas;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BaseDeDatos extends SQLiteOpenHelper {

    private static final String nombreBdd = "bdd_ventas";
    private static final int versionBdd = 2;
    private static final String tablaUsuario = "create table usuario(id_usu integer primary key autoincrement, " +
            "nombre_usu text, nombre_usuario_usu text, email_usu text, password_usu text)";
    private static final String tablaCliente = "create table cliente(id_cli integer primary key autoincrement, " +
            "cedula_cli text, apellido_cli text, nombre_cli text, telefono_cli text, direccion_cli text);";
    private static final String tablaProducto = "create table producto(id_prod integer primary key autoincrement, " +
            "nombre_prod text, descripcion_prod text, precio_prod real, iva_prod real, stock_prod real, f_caducidad_prod text);";


    private static final String tablaVenta = "create table venta(id_ven integer primary key autoincrement, " +
            "titulo_ven text, fecha_ven text, estado_ven text, total_ven real, observacion_ven text, " +
            "fk_cli_ven integer, foreign key(fk_cli_ven) references cliente(id_cli));";

    private static final String tablaProductosVendidos = "create table productos_vendidos(id_prod_vendido integer primary key autoincrement, " +
            "cantidad_vendido real, sub_total_vendido real, fk_prod_vendido integer, " +
            "fk_ven_vendido integer, " +
            "foreign key(fk_prod_vendido) references producto(id_cli), " +
            "foreign key(fk_ven_vendido) references venta(id_ven));";

    // Constructor
    public BaseDeDatos(Context contexto){
        super(contexto, nombreBdd, null, versionBdd);
    }


    // Proceso 1: Metodo que se ejecuta automaticamente cuando se contruye la clase de la Base de Datos
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tablaUsuario); // Ejecutar el query DOL para crear la tabla de Usuarios
        db.execSQL(tablaCliente); // Ejecutar el query DOL para crear la tabla de Clientes
        db.execSQL(tablaProducto); // Ejecutar el query DOL para crear la tabla Productos
        db.execSQL(tablaVenta); // Ejecutar el query DOL para crear la tabla Ventas
        db.execSQL(tablaProductosVendidos); // Ejecutar el query DOL para crear la tabla Ventas
    }

    // Proceso 2: Metodo que se ejecuta automaticamente cuando se detectan cambios en la Base de Datos
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS usuario"); // Eliminar la version anterior de la tabla Usuarios
        db.execSQL(tablaUsuario); // Ejecutar el codigo para crear la tabla Usuario
        db.execSQL("DROP TABLE IF EXISTS cliente"); // Eliminar la version anterio de la tabla Clientes
        db.execSQL(tablaCliente); // Ejecutar el codigo para crear la tabla Cliente
        db.execSQL("DROP TABLE IF EXISTS producto"); // Eliminar la version anterior de la tabla Productos
        db.execSQL(tablaProducto); // Ejecutar el codigo para crear la tabla Producto
        db.execSQL("DROP TABLE IF EXISTS venta"); // Eliminar la vesrion anterior de la tabla Ventas
        db.execSQL(tablaVenta); // Ejecutar el codigo para crear la tabla Venta
        db.execSQL("DROP TABLE IF EXISTS productos_vendidos"); // Eliminar la version anterior de la tabla Productos Vendidos
        db.execSQL(tablaProductosVendidos); // Ejecutar el codigo para crear la tabla Productos Vendidos
    }


    /* USUARIO */
    // Proceso 3: Metodo para insertar datos dentro de la tabla usuario, retorna true cuando inserta o false cuando hay algun error
    public boolean agregarUsuario(String nombre, String nombre_usuario, String email, String password){
        SQLiteDatabase miBdd = getWritableDatabase(); // Llamar a la base de datos
        if(miBdd != null){ // Validar que la base de datos exista (que no sea null)
            miBdd.execSQL("insert into usuario(nombre_usu, nombre_usuario_usu, email_usu, password_usu) values " +
                    "('"+nombre+"', '"+nombre_usuario+"', '"+email+"', '"+password+"')"); // Ejecutando la sentencia de isercion SQL
            miBdd.close(); // Cerrar la conexion a la base de datos
            return true; // Retorno cuando la insercion es exitosa
        }
        return false; // Retorno cuando no exista la base de datos
    }

    public Cursor validarUsuario(String nombre_usuario, String password){
        SQLiteDatabase miBdd = getWritableDatabase();
        Cursor usuario = miBdd.rawQuery("select * from usuario where nombre_usuario_usu = '"+nombre_usuario+"' " +
                "and password_usu = '"+password+"';", null);
        if(usuario.moveToFirst()){ // Verificar que el objeto usuario tenga resultados
            return usuario; // Retornamos los datos encontrados
        }else{
            // No se encuentra el usuario -> Porque no existe o porque el email o el password son incorrectos
            return null;
        }
    }

    public boolean validarNombreUsuario(String nombre_usuario){
        SQLiteDatabase nombreBdd = getReadableDatabase();
        Cursor cursor = nombreBdd.rawQuery("select nombre_usuario_usu from usuario " +
                "where nombre_usuario_usu = '"+nombre_usuario+"';", null);
        if(cursor.moveToFirst()){
            nombreBdd.close();
            return true;
        }
        return false;
    }


    /* PRODUCTO */

    public boolean registrarProducto(String nombre, String descripcion, double precio, double iva, double stock, String f_caducidad){
        SQLiteDatabase miBdd = getWritableDatabase(); // Crear un objeto de la base de datos
        if(miBdd != null){ // Validar que exista la base de datos
            // Realizar el proceso de insercion
            miBdd.execSQL("insert into producto(nombre_prod, descripcion_prod, precio_prod, iva_prod, stock_prod, f_caducidad_prod) values " +
                    "('"+nombre+"', '"+descripcion+"', "+precio+", "+iva+", "+stock+", '"+f_caducidad+"');");
            miBdd.close(); // Cerrar la base de datos
            return true; // Retorno si se insertar correctamente
        }
        return false; // Retorno si no existe la base de datos
    }

    public Cursor consultarProductos(){
        SQLiteDatabase miBdd = getWritableDatabase();
        // Consultar los productos en la base de datos y guardarlos en un Cursor
        Cursor productos = miBdd.rawQuery("select * from producto;", null);
        if(productos.moveToFirst()){
            miBdd.close();
            // Retorno el Cursor con la lista de productos encontrados
            return productos;
        }else{
            return null; // Retorna una lista vacia
        }
    }

    public int conseguirIdProducto(){
        SQLiteDatabase idBdd = getReadableDatabase();
        int id_maximo = 0; // Variable para almacenar la id del proximo producto
        // Crear un objeto de tipo cursor para almacenar el id del proximo producto
        Cursor cursor = idBdd.rawQuery("select max(id_prod)+1 from producto", null);
        if(cursor.moveToFirst()){
            idBdd.close(); // Cerrar la base de datos
            id_maximo = cursor.getInt(0); // Guardar el id del proximo producto
        }
        return id_maximo; // Retorna el proximo id
    }

    public boolean actualizarProducto(String idProducto, String nombre, String descripcion, double precio, double iva, double stock, String f_caducidad){
        SQLiteDatabase bdd = getWritableDatabase();
        if(bdd != null){
            bdd.execSQL("update producto set nombre_prod='"+nombre+"', descripcion_prod='"+descripcion+"', " +
                    "precio_prod="+precio+", iva_prod="+iva+", stock_prod="+stock+", " +
                    "f_caducidad_prod='"+f_caducidad+"' " +
                    "where id_prod = " + idProducto);
            bdd.close();
            return true;
        }
        return false;
    }

    public boolean eliminarProducto(String idProducto){
        SQLiteDatabase bdd = getWritableDatabase();
        if(bdd != null){
            bdd.execSQL("delete from producto where id_prod = " + idProducto);
            bdd.close();
            return true;
        }
        return false;
    }

    public Cursor obtenerProducto(int idProducto) {
        SQLiteDatabase miBdd = getReadableDatabase();
        Cursor producto = miBdd.rawQuery("SELECT * FROM producto WHERE id_prod = " + idProducto, null);

        if (producto.moveToFirst()) {
            miBdd.close();
            return producto;
        }
        return null;
    }


    /* CLIENTE */

    public boolean agregarCliente(String cedula, String apellido, String nombre, String telefono, String direccion){
        SQLiteDatabase miBdd = getWritableDatabase();
        if(miBdd != null){
            miBdd.execSQL("insert into cliente(cedula_cli, apellido_cli, nombre_cli, telefono_cli, direccion_cli) values " +
                    "('"+cedula+"', '"+apellido+"', '"+nombre+"', '"+telefono+"', '"+direccion+"');");
            miBdd.close();
            return true;
        }
        return false; // Retorno en el caso de errores con la base de datos
    }

    public Cursor consultarClientes(){
        SQLiteDatabase miBdd = getWritableDatabase(); // Objeto para manejar la base de datos
        // Consultar los clientes en el abase de datos y guardarlos en un cursor
        Cursor clientes = miBdd.rawQuery("select * from cliente;", null);
        if(clientes.moveToFirst()){
            // Retornar el cursor que contiene el listado de cliente
            miBdd.close();
            return clientes;
        }else{
            return clientes; // Se retorna cuando no hay clientes dentro de la tabla clientes
        }
    }

    public boolean actualizarCliente(String cedula, String apellido, String nombre, String telefono,
                                     String direccion, String id){
        SQLiteDatabase miBdd = getWritableDatabase(); // Objeto para manejar la base de datos
        if(miBdd != null){
            miBdd.execSQL("update cliente set cedula_cli = '"+cedula+"', apellido_cli = '"+apellido+"', " +
                    "nombre_cli = '"+nombre+"', telefono_cli = '"+telefono+"', direccion_cli = '"+direccion+"' " +
                    "where id_cli = " + id);
            miBdd.close(); // Cerrar la conexion a la base de datos
            return true; // Retorna verdadero cuando se actualiza la informacion del cliente
        }
        return false; // Se retoran falso cuando no existe la base de datos
    }

    public boolean eliminarCliente(String id){
        SQLiteDatabase miBdd = getWritableDatabase(); // Objeto para manejar la base de datos
        if(miBdd != null){
            miBdd.execSQL("delete from cliente where id_cli = " + id);
            miBdd.close(); // Cerrar la conexion a la base de datos
            return true; // Retorna verdadero cuando se elimina un cliente
        }
        return false; // Se retorna cuando no existe la base de datos
    }

    public Cursor obtenerCliente(int idCliente) {
        SQLiteDatabase miBdd = getReadableDatabase();
        Cursor cliente = miBdd.rawQuery("SELECT * FROM cliente WHERE id_cli = " + idCliente, null);
        if (cliente.moveToFirst()) {
            miBdd.close();
            return cliente;
        }
        return null;
    }


    /* VENTAS */

    public int conseguirIdVenta(){
        SQLiteDatabase bdd = getReadableDatabase();
        int idVenta = 1;
        Cursor cursor = bdd.rawQuery("select max(id_ven)+1 from venta;", null);
        if(cursor.moveToFirst()){
            bdd.close();
            idVenta = cursor.getInt(0);
        }
        return idVenta;
    }

    public boolean registrarVenta(String titulo, String fecha, String estado, double total,
                                  String observacion, int idCliente){
        SQLiteDatabase bdd = getWritableDatabase();
        if(bdd != null){
            bdd.execSQL("insert into venta(titulo_ven, fecha_ven, estado_ven," +
                    " total_ven, observacion_ven, fk_cli_ven) values " +
                    "('"+titulo+"', '"+fecha+"', '"+estado+"', "+total+", '"+observacion+"', "+idCliente+");");
            bdd.close();
            return true;
        }
        return false;
    }

    public boolean registrarVentas(double cantidad, double subTotal, int idProducto, int idVenta){
        SQLiteDatabase bdd = getWritableDatabase();
        if(bdd != null){
            bdd.execSQL("insert into productos_vendidos(cantidad_vendido, sub_total_vendido, " +
                    "fk_prod_vendido, fk_ven_vendido) values " +
                    "("+cantidad+", "+subTotal+", "+idProducto+", "+idVenta+");");
            bdd.close();
            return true;
        }
        return false;
    }

    public boolean actualizarCantidadProducto(int idProducto, double cantidad){
        SQLiteDatabase bdd = getWritableDatabase();
        if(bdd != null){
            bdd.execSQL("update producto set stock_prod =  stock_prod - " + cantidad + " " +
                    "where id_prod = " + idProducto);
            bdd.close();
            return true;
        }
        return false;
    }

    public Cursor consultarVentas(){
        SQLiteDatabase bdd = getReadableDatabase();
        if(bdd != null){
            System.out.println("ENTRO EN La condicion");
            Cursor ventas = bdd.rawQuery("select id_ven, titulo_ven, fecha_ven, total_ven, nombre_cli " +
                    "from venta as v " +
                    "inner join cliente as c on c.id_cli = v.fk_cli_ven;", null);
            if(ventas.moveToFirst()){
                bdd.close();
                System.out.println("SALIO de l adocnidon: " + ventas.getString(0));
                return ventas;
            }

        }
        return null;
    }

    public ArrayList<String> consultarProductosVendidos(String idVenta){
        SQLiteDatabase bdd = getReadableDatabase();
        String producto;
        double cantidad, subTotal;
        ArrayList<String> productos = new ArrayList<>();
        if(bdd != null){
            Cursor productos_vendidos = bdd.rawQuery("select nombre_prod, cantidad_vendido, sub_total_vendido " +
                    "from productos_vendidos as pv " +
                    "inner join producto as p on p.id_prod = pv.fk_prod_vendido " +
                    "where fk_ven_vendido = " + idVenta, null);
            if(productos_vendidos.moveToFirst()){
                bdd.close();
                do{
                    producto = productos_vendidos.getString(0);
                    cantidad = productos_vendidos.getDouble(1);
                    subTotal = productos_vendidos.getDouble(2);
                    productos.add(String.format("%15s%15s%15s", producto, cantidad, subTotal));
                    System.out.println(String.format("%15s%15s%15s", producto, cantidad, subTotal));
                }while(productos_vendidos.moveToNext());
                return productos;
            }
        }
        return productos;
    }








}
