
package Entidades;
/**
 *
 * @author IgnacioCH
 */
import java.time.LocalTime;
public class Registro {

    private String tipo, placa;
    private LocalTime horaEntrada, horaSalida;
    private double monto;
    
    
    public Registro(String tipo, String placa, LocalTime horaEntrada, double monto){
        this.placa = placa;
        this.tipo = tipo;
        this.horaEntrada = horaEntrada;
        this.horaSalida = null; // como no salen todasvia no tiene valor
        this.monto = monto; 
    }// fin constructor

    // Setters
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setPlaca (String placa) {
        this.placa = placa;  
    }
    public void setHoraSalida(LocalTime horaSalida){ 
        this.horaSalida = horaSalida; 
    }
    public void setMonto(double monto){ 
        this.monto = monto; 
    }

    
    //Getters

    public String getTipo() {
        return tipo;
    }

    public String getPlaca() {
        return placa;
    }
    public LocalTime getHoraEntrada(){ 
        return horaEntrada; 
    }
    public LocalTime getHoraSalida(){ 
        return horaSalida; 
    }
    public double getMonto(){ 
        return monto; 
    }
    //estado del vehiculo
    public boolean estaActivo() {
        return horaSalida == null;
    }
    
}//fin clase registro


