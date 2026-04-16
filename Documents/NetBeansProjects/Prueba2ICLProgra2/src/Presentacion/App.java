package Presentacion;
/**
 * @author Ignacio CH
 */
import Entidades.Registro;
import Logica.GestionParqueo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class App extends JFrame {

    // Lógica de negocio — única dependencia permitida desde Presentación
    private final GestionParqueo gestion = new GestionParqueo();

    // ── Componentes pestaña Entrada ──────────────────────────────────────────
    private JTextField txtPlacaEntrada;
    private JComboBox<String> cmbTipo;
    private JButton btnRegistrarEntrada;
    private JLabel lblMensajeEntrada;

    // ── Componentes pestaña Salida ───────────────────────────────────────────
    private JTextField txtPlacaSalida;
    private JButton btnRegistrarSalida;
    private JLabel lblMensajeSalida;

    // ── Componentes pestaña Parqueo actual ───────────────────────────────────
    private JTable tablaActivos;
    private DefaultTableModel modeloActivos;

    // ── Componentes pestaña Historial ────────────────────────────────────────
    private JTable tablaHistorial;
    private DefaultTableModel modeloHistorial;
    private JTextField txtPlacaEliminar;
    private JButton btnEliminar;
    private JLabel lblMensajeHistorial;

    // ── Constructor ──────────────────────────────────────────────────────────
    public App() {
        setTitle("Sistema de Parqueo Público");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 480);
        setLocationRelativeTo(null); // centrar en pantalla
        setResizable(false);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Entrada",        buildPanelEntrada());
        pestanas.addTab("Salida",         buildPanelSalida());
        pestanas.addTab("Parqueo Actual", buildPanelActivos());
        pestanas.addTab("Historial",      buildPanelHistorial());

        // Cada vez que se cambia de pestaña se recargan las tablas
        pestanas.addChangeListener(e -> {
            refrescarTablaActivos();
            refrescarTablaHistorial();
        });

        add(pestanas);
    }

    // ── Panel: Registro de Entrada ───────────────────────────────────────────
    private JPanel buildPanelEntrada() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.anchor = GridBagConstraints.WEST;

        // Título
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        JLabel titulo = new JLabel("Registro de Entrada");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(titulo, g);

        // Placa
        g.gridwidth = 1;
        g.gridx = 0; g.gridy = 1;
        panel.add(new JLabel("Placa:"), g);
        g.gridx = 1;
        txtPlacaEntrada = new JTextField(15);
        panel.add(txtPlacaEntrada, g);

        // Tipo
        g.gridx = 0; g.gridy = 2;
        panel.add(new JLabel("Tipo:"), g);
        g.gridx = 1;
        cmbTipo = new JComboBox<>(new String[]{"Carro", "Moto"});
        panel.add(cmbTipo, g);

        // Botón
        g.gridx = 1; g.gridy = 3;
        btnRegistrarEntrada = new JButton("Registrar Entrada");
        panel.add(btnRegistrarEntrada, g);

        // Mensaje resultado
        g.gridx = 0; g.gridy = 4; g.gridwidth = 2;
        lblMensajeEntrada = new JLabel(" ");
        lblMensajeEntrada.setFont(new Font("SansSerif", Font.ITALIC, 12));
        panel.add(lblMensajeEntrada, g);

        // Acción del botón
        btnRegistrarEntrada.addActionListener(e -> {
            String placa = txtPlacaEntrada.getText();
            String tipo  = (String) cmbTipo.getSelectedItem();
            String resultado = gestion.registrarEntrada(placa, tipo);
            mostrarMensaje(lblMensajeEntrada, resultado);
            if (resultado.startsWith("OK")) {
                txtPlacaEntrada.setText("");
            }
        });

        return panel;
    }

    // ── Panel: Registro de Salida ────────────────────────────────────────────
    private JPanel buildPanelSalida() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.anchor = GridBagConstraints.WEST;

        // Título
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        JLabel titulo = new JLabel("Registro de Salida");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(titulo, g);

        // Placa
        g.gridwidth = 1;
        g.gridx = 0; g.gridy = 1;
        panel.add(new JLabel("Placa:"), g);
        g.gridx = 1;
        txtPlacaSalida = new JTextField(15);
        panel.add(txtPlacaSalida, g);

        // Botón
        g.gridx = 1; g.gridy = 2;
        btnRegistrarSalida = new JButton("Registrar Salida");
        panel.add(btnRegistrarSalida, g);

        // Mensaje resultado
        g.gridx = 0; g.gridy = 3; g.gridwidth = 2;
        lblMensajeSalida = new JLabel(" ");
        lblMensajeSalida.setFont(new Font("SansSerif", Font.ITALIC, 12));
        panel.add(lblMensajeSalida, g);

        // Acción del botón
        btnRegistrarSalida.addActionListener(e -> {
            String placa    = txtPlacaSalida.getText();
            String resultado = gestion.registrarSalida(placa);
            mostrarMensaje(lblMensajeSalida, resultado);
            if (resultado.startsWith("OK")) {
                txtPlacaSalida.setText("");
                refrescarTablaActivos();
                refrescarTablaHistorial();
            }
        });

        return panel;
    }

    // ── Panel: Vehículos actualmente en el parqueo ───────────────────────────
    private JPanel buildPanelActivos() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("Vehículos en el Parqueo");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(titulo, BorderLayout.NORTH);

        // Columnas: Placa | Tipo | Hora Entrada
        modeloActivos = new DefaultTableModel(
            new String[]{"Placa", "Tipo", "Hora Entrada"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaActivos = new JTable(modeloActivos);
        tablaActivos.setRowHeight(24);
        tablaActivos.getTableHeader().setReorderingAllowed(false);
        panel.add(new JScrollPane(tablaActivos), BorderLayout.CENTER);

        // Botón refrescar manual
        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> refrescarTablaActivos());
        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sur.add(btnRefrescar);
        panel.add(sur, BorderLayout.SOUTH);

        return panel;
    }

    // ── Panel: Historial ─────────────────────────────────────────────────────
    private JPanel buildPanelHistorial() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("Historial de Vehículos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(titulo, BorderLayout.NORTH);

        // Columnas: Placa | Tipo | Entrada | Salida | Monto
        modeloHistorial = new DefaultTableModel(
            new String[]{"Placa", "Tipo", "Hora Entrada", "Hora Salida", "Monto (₡)"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaHistorial = new JTable(modeloHistorial);
        tablaHistorial.setRowHeight(24);
        tablaHistorial.getTableHeader().setReorderingAllowed(false);
        panel.add(new JScrollPane(tablaHistorial), BorderLayout.CENTER);

        // Panel inferior: campo placa + botón eliminar + mensaje
        JPanel sur = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sur.add(new JLabel("Placa a eliminar:"));
        txtPlacaEliminar = new JTextField(10);
        sur.add(txtPlacaEliminar);
        btnEliminar = new JButton("Eliminar Registro");
        sur.add(btnEliminar);
        lblMensajeHistorial = new JLabel(" ");
        lblMensajeHistorial.setFont(new Font("SansSerif", Font.ITALIC, 12));
        sur.add(lblMensajeHistorial);
        panel.add(sur, BorderLayout.SOUTH);

        // Clic en fila → autocompleta el campo de placa
        tablaHistorial.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaHistorial.getSelectedRow();
            if (fila >= 0) {
                txtPlacaEliminar.setText(
                    modeloHistorial.getValueAt(fila, 0).toString()
                );
            }
        });

        // Acción eliminar
        btnEliminar.addActionListener(e -> {
            String placa    = txtPlacaEliminar.getText();
            String resultado = gestion.eliminarHistorial(placa);
            mostrarMensaje(lblMensajeHistorial, resultado);
            if (resultado.startsWith("OK")) {
                txtPlacaEliminar.setText("");
                refrescarTablaHistorial();
            }
        });

        return panel;
    }

    // ── Refrescar tablas ─────────────────────────────────────────────────────

    private void refrescarTablaActivos() {
        modeloActivos.setRowCount(0); // limpiar filas
        List<Registro> activos = gestion.getVehiculosActivos();
        for (Registro v : activos) {
            modeloActivos.addRow(new Object[]{
                v.getPlaca(),
                v.getTipo(),
                v.getHoraEntrada()
            });
        }
    }

    private void refrescarTablaHistorial() {
        modeloHistorial.setRowCount(0); // limpiar filas
        List<Registro> historial = gestion.getHistorial();
        for (Registro v : historial) {
            modeloHistorial.addRow(new Object[]{
                v.getPlaca(),
                v.getTipo(),
                v.getHoraEntrada(),
                v.getHoraSalida(),
                String.format("%.0f", v.getMonto())
            });
        }
    }

    // ── Utilidad: colorear mensaje OK/ERROR ──────────────────────────────────
    private void mostrarMensaje(JLabel label, String mensaje) {
        label.setText(mensaje);
        if (mensaje.startsWith("OK")) {
            label.setForeground(new Color(0, 130, 0)); // verde
        } else {
            label.setForeground(Color.RED);
        }
    }

    // ── Main ─────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            App ventana = new App();
            ventana.setVisible(true);
        });
    }

} // fin clase App