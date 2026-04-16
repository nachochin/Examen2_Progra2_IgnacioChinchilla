package AccesoADatos;
/**
 *
 * @author Ignacio CH
 */
import Entidades.Registro;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CrearArchivo {

    private static final String ARCHIVO = "Archivos.txt";
    private static final String SEPARADOR = ";";
    public void guardarTodos(List<Registro> productos) {
        // List utiliza todos los atributos de la entidad de Inventario. ID, nombre, cantidad y Precio
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO, false))) {
            for (Registro p : productos) {
               
                bw.write(p.getTipo() + SEPARADOR
                        + p.getPlaca() + SEPARADOR);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al guardar archivo: " + e.getMessage());
        }
    }

    // ── Lee el archivo y devuelve la lista de productos ──────────────────────
    public List<Registro> leerTodos() {
        List<Registro> lista = new ArrayList<>();
        File archivo = new File(ARCHIVO);

        // Si el archivo no existe aún, devuelve lista vacía sin error
        if (!archivo.exists()) {
            return lista;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                // Ignorar líneas vacías o corruptas
                if (linea.trim().isEmpty()) continue;

                String[] partes = linea.split(SEPARADOR);
                if (partes.length == 4) {
                    int id         = Integer.parseInt(partes[0]);
                    String nombre  = partes[1];
                    int cantidad   = Integer.parseInt(partes[2]);
                    double precio  = Double.parseDouble(partes[3]);
                    lista.add(new Registro(placa, tipo));
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer archivo: " + e.getMessage());
        }
        return lista;
    }
} // fin clase CrearFile