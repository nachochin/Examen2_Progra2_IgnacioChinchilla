package Logica;
/**
 * @author Ignacio CH
 */
import AccesoADatos.CrearArchivo;
import Entidades.Registro;
import java.time.LocalTime;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;

public class GestionParqueo {

    private static final double TARIFA_POR_HORA = 500.0;
    // Resticciones de placas
    // Placa válida: 6 dígitos  O  3 letras seguidas de 3 números
    private static final String PATRON_PLACA = "^[0-9]{6}$|^[A-Za-z]{3}[0-9]{3}$";

    private final CrearArchivo   dao;
    private final List<Registro> vehiculos; // lista en memoria

    public GestionParqueo() {
        this.dao= new CrearArchivo();
        this.vehiculos= dao.leerTodos(); // carga todo al iniciar
    }

  
    public boolean placaValida(String placa) {
        if (placa == null || placa.trim().isEmpty()) return false;
        return placa.trim().toUpperCase().matches(PATRON_PLACA);
    }

    // Verifica si ya hay un vehículo ACTIVO con esa placa (Regla 1)
    public boolean placaActiva(String placa) {
        for (Registro v : vehiculos) {
            if (v.getPlaca().equalsIgnoreCase(placa.trim()) && v.estaActivo()) {
                return true;
            }
        }
        return false;
    }
    public String registrarEntrada(String placa, String tipo) {
        // Validar campos obligatorios (Regla 3)
        if (placa == null || placa.trim().isEmpty()) {
            return "ERROR: La placa es obligatoria.";
        }
        if (tipo == null || tipo.trim().isEmpty()) {
            return "ERROR: El tipo de vehículo es obligatorio.";
        }

        placa = placa.trim().toUpperCase();

        // Validar formato de placa (Regla 3)
        if (!placaValida(placa)) {
            return "ERROR: Formato de placa inválido. "
                 + "Debe ser 6 dígitos (123456) o 3 letras + 3 números (ABC123).";
        }

        // Verificar unicidad: no puede entrar si ya está adentro (Regla 1)
        if (placaActiva(placa)) {
            return "ERROR: La placa " + placa + " ya se encuentra en el parqueo.";
        }

        // Crear registro con hora de entrada automática
        Registro nuevo = new Registro(tipo.trim(), placa, LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        vehiculos.add(nuevo);
        dao.guardarTodos(vehiculos);

        return "OK: Entrada registrada — " + placa
             + " (" + tipo + ") a las " + nuevo.getHoraEntrada();
    }

    

    public String registrarSalida(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            return "ERROR: La placa es obligatoria.";
        }

        placa = placa.trim().toUpperCase();

        // Buscar el vehículo activo con esa placa
        Registro objetivo = null;
        for (Registro v : vehiculos) {
            if (v.getPlaca().equalsIgnoreCase(placa) && v.estaActivo()) {
                objetivo = v;
                break;
            }
        }

        if (objetivo == null) {
            return "ERROR: No hay vehículo activo con placa " + placa + ".";
        }

        // Registrar salida con hora automática y calcular monto (Regla 2)
        LocalTime horaSalida = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
        double monto = calcularMonto(objetivo.getHoraEntrada(), horaSalida);

        objetivo.setHoraSalida(horaSalida);
        objetivo.setMonto(monto);
        dao.guardarTodos(vehiculos);

        return String.format(
            "OK: Salida de %s a las %s | Tiempo: %s | Monto: ₡%.0f",
            placa,
            horaSalida,
            formatearDuracion(objetivo.getHoraEntrada(), horaSalida),
            monto
        );
    }

   
    public String eliminarHistorial(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            return "ERROR: La placa es obligatoria.";
        }
        final String placaFinal = placa.trim().toUpperCase();

        // Solo se pueden eliminar registros con salida (no activos)
        boolean eliminado = vehiculos.removeIf(
            v -> v.getPlaca().equalsIgnoreCase(placaFinal) && !v.estaActivo()
        );

        if (!eliminado) {
            return "ERROR: No se encontró historial para la placa " + placaFinal + ".";
        }

        dao.guardarTodos(vehiculos);
        return "OK: Historial de " + placaFinal + " eliminado.";
    }

 

    // Para la tabla "Vehículos en parqueo"
    public List<Registro> getVehiculosActivos() {
        return vehiculos.stream()
                .filter(Registro::estaActivo)
                .collect(Collectors.toList());
    }

    // Para la tabla "Historial"
    public List<Registro> getHistorial() {
        return vehiculos.stream()
                .filter(v -> !v.estaActivo())
                .collect(Collectors.toList());
    }

  

    // 500 por hora o fracción — mínimo cobro: 1 hora (Regla 2)
    private double calcularMonto(LocalTime entrada, LocalTime salida) {
        long minutos = Duration.between(entrada, salida).toMinutes();
        if (minutos <= 0) minutos = 1; // mínimo 1 minuto para evitar cobro 0
        long horas = (long) Math.ceil(minutos / 60.0); // fracción cuenta como hora completa
        return horas * TARIFA_POR_HORA;
    }

    // Devuelve duración en formato "1h 23m"
    private String formatearDuracion(LocalTime entrada, LocalTime salida) {
        long minutos = Duration.between(entrada, salida).toMinutes();
        if (minutos < 0) minutos = 0;
        return (minutos / 60) + "h " + (minutos % 60) + "m";
    }

} // fin clase GestionParqueo