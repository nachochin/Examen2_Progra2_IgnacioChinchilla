package Entidades;

import java.time.LocalTime;

public class Registro {

    private String tipo, placa;
    private LocalTime horaEntrada, horaSalida;
    private double monto;

    // --- NUEVO CONSTRUCTOR (Para registros nuevos de entrada) ---
    // Este es el que faltaba y causaba el error en GestionParqueo
    public Registro(String tipo, String placa, LocalTime horaEntrada) {
        this.tipo = tipo;
        this.placa = placa;
        this.horaEntrada = horaEntrada;
        this.horaSalida = null;
        this.monto = 0.0;
    }

    // Constructor para archivos (5 parámetros)
    // Se usa en CrearArchivo.java para reconstruir los datos guardados
    public Registro(String tipo, String placa, LocalTime horaEntrada,
                    LocalTime horaSalida, double monto) {
        this.tipo = tipo;
        this.placa = placa;
        this.horaEntrada = horaEntrada;
        this.horaSalida  = horaSalida;
        this.monto       = monto;
    }

    // Setters
    public void setTipo(String tipo) { 
        this.tipo = tipo; 
    }
    public void setPlaca(String placa) { 
        this.placa = placa; 
    }
    public void setHoraSalida(LocalTime horaSalida) { 
        this.horaSalida = horaSalida; 
    }
    public void setMonto(double monto) { 
        this.monto = monto; 
    }

    // Getters
    public String getTipo() { 
        return tipo; 
    }
    public String getPlaca() { 
        return placa; 
    }
    public LocalTime getHoraEntrada() { 
        return horaEntrada; 
    }
    public LocalTime getHoraSalida() { 
        return horaSalida; 
    }
    public double getMonto() { 
        return monto; 
    }

    // Estado del vehículo
    public boolean estaActivo() {
        return horaSalida == null;
    }
} 