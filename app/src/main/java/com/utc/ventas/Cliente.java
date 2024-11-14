package com.utc.ventas;

import androidx.annotation.NonNull;
// COmentario en cliente para probrar el push
public class Cliente {
    private int idCliente;
    private String nombreCliente;
    private String apellidoCliente;

    public Cliente(){

    }

    public int getIdCliente(){
        return this.idCliente;
    }
    public void setIdCliente(int idCliente){
        this.idCliente = idCliente;
    }

    public String getNombreCliente(){
        return this.nombreCliente;
    }

    public void setNombreCliente(String nombreCliente){
        this.nombreCliente = nombreCliente;
    }

    public String getApellidoCliente(){
        return this.apellidoCliente;
    }
    public void setApellidoCliente(String apellidoCliente){
        this.apellidoCliente = apellidoCliente;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getNombreCliente() + " " + this.getApellidoCliente();
    }
}
