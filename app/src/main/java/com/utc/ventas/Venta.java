package com.utc.ventas;

import androidx.annotation.NonNull;

public class Venta {
    private int id_producto;
    private String nombre_producto;
    private double cantidad;
    private double subTotal;

    public Venta(){

    }

    public int getIdProducto(){
        return this.id_producto;
    }
    public void setId_producto(int id){
        this.id_producto = id;
    }

    public String getNombre_producto(){
        return this.nombre_producto;
    }
    public void setNombre_producto(String nombre_producto){
        this.nombre_producto = nombre_producto;
    }

    public double getCantidad(){
        return this.cantidad;
    }
    public void setCantidad(double cantidad){
        this.cantidad = cantidad;
    }

    public double getSubTotal(){
        return this.subTotal;
    }
    public void setSubTotal(double subTotal){
        this.subTotal = subTotal;
    }


    @NonNull
    @Override
    public String toString() {
        return String.format("%5s%15s%15s%10s", this.getIdProducto(), this.getNombre_producto(),
                (int) this.getCantidad(), "$ " + this.getSubTotal());
    }


}
