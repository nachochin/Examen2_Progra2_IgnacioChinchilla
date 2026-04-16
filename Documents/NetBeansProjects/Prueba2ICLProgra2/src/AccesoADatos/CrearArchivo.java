package AccesoADatos;
/**
 * @author Ignacio CH
 */
import Entidades.Registro;
import java.io.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CrearArchivo {

    private static final String ARCHIVO   = "Archivos.txt";
    private static final String SEPARADOR = ";";

    // Escribe TODA la lista en el archivo (sobreescribe cada vez)
    public void guardarTodos(List<Registro> vehiculos) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO, false))) {
            for (Registro v : vehiculos) {
                // Si horaSalida es null (activo), grabamos el texto "null"
                String salida = (v.getHoraSalida() == null) ? "null"
                : v.getHoraSalida().toString();
                // Formato: tipo;placa;horaEntrada;horaSalida;monto
                bw.write(
                    v.getTipo()        + SEPARADOR +
                    v.getPlaca()       + SEPARADOR +
                    v.getHoraEntrada() + SEPARADOR +
                    salida             + SEPARADOR +
                    v.getMonto()
                );
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar archivo: " + e.getMessage());
        }
    }

    // Lee el archivo y reconstruye la lista de registros
    public List<Registro> leerTodos() {
        List<Registro> lista = new ArrayList<>();
        File archivo = new File(ARCHIVO);

        if (!archivo.exists()) return lista; // primera ejecución, no hay archivo aún

        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue; // ignorar líneas vacías

                String[] p = linea.split(SEPARADOR);
                if (p.length == 5) {
                    String    tipo    = p[0].trim();
                    String    placa   = p[1].trim();
                    LocalTime entrada = LocalTime.parse(p[2].trim());
                    // Si grabamos "null" significa que aún está activo
                    LocalTime salida  = p[3].trim().equals("null") ? null
                                        : LocalTime.parse(p[3].trim());
                    double    monto   = Double.parseDouble(p[4].trim());

                    lista.add(new Registro(tipo, placa, entrada, salida, monto));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer archivo: " + e.getMessage());
        }
        return lista;
    }

} // fin clase CrearArchivo