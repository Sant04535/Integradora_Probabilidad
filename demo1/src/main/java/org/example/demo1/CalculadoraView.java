package org.example.demo1;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.function.Function;

public class CalculadoraView {

    private final BorderPane root = new BorderPane();

    private TextField        campFuncion;
    private TextField        campA;
    private TextField        campB;
    private Slider           sliderN;
    private Label            lblNValor;
    private ComboBox<String> comboMetodo;
    private Button           btnCalcular;
    private Label            lblEstado;

    private Label lblResultado;
    private Label lblDx;
    private Label lblMetodoInfo;

    private Canvas canvas;

    private static final Color COLOR_FONDO_APP = Color.web("#1e1e2e");
    private static final Color COLOR_PANEL     = Color.web("#2a2a3e");
    private static final Color COLOR_CURVA     = Color.web("#f38ba8");
    private static final Color COLOR_AREA      = Color.web("#89b4fa");
    private static final Color COLOR_EJE       = Color.web("#6c7086");
    private static final Color COLOR_GRID      = Color.web("#313244");
    private static final Color COLOR_TEXTO     = Color.web("#cdd6f4");
    private static final Color COLOR_ACENTO    = Color.web("#cba6f7");
    private static final Color COLOR_OK        = Color.web("#a6e3a1");
    private static final Color COLOR_ERROR     = Color.web("#f38ba8");

    private static final String[] METODOS = {
            "Riemann Izquierdo", "Riemann Derecho",
            "Punto Medio", "Trapecio", "Simpson 1/3"
    };

    private static final String[] INFO_METODOS = {
            "Rectángulos con altura en el extremo IZQUIERDO de cada subintervalo.",
            "Rectángulos con altura en el extremo DERECHO de cada subintervalo.",
            "Rectángulos con altura en el CENTRO de cada subintervalo. Más preciso.",
            "Conecta puntos con líneas rectas formando trapecios. Muy usado.",
            "Aproxima con parábolas (n debe ser par). Alta precisión."
    };

    public CalculadoraView() {
        construirUI();
        calcular();
    }

    public BorderPane getRoot() { return root; }

    private void construirUI() {
        root.setStyle("-fx-background-color: #1e1e2e;");
        root.setTop(crearHeader());
        root.setLeft(crearPanelControles());
        root.setCenter(crearPanelGrafica());
        root.setBottom(crearBarraResultados());
    }

    private HBox crearHeader() {
        Label titulo = new Label("∫  Calculadora de Área Bajo la Curva");
        titulo.setFont(Font.font("Monospace", FontWeight.BOLD, 20));
        titulo.setTextFill(COLOR_ACENTO);

        Label subtitulo = new Label("Integración numérica");
        subtitulo.setFont(Font.font("Monospace", 13));
        subtitulo.setTextFill(COLOR_EJE);

        VBox textos = new VBox(2, titulo, subtitulo);
        HBox header = new HBox(textos);
        header.setPadding(new Insets(18, 24, 14, 24));
        header.setStyle("-fx-background-color: #181825; -fx-border-color: #313244; -fx-border-width: 0 0 1 0;");
        return header;
    }

    private VBox crearPanelControles() {
        Label lblFn = etiqueta("f(x)");
        campFuncion = campoTexto("sin(x)");
        campFuncion.setOnAction(e -> calcular());

        Label ejemplos = new Label("Ej: x^2, sin(x), exp(-x), x^3-2*x");
        ejemplos.setFont(Font.font("Monospace", 10));
        ejemplos.setTextFill(COLOR_EJE);
        ejemplos.setWrapText(true);

        Label lblLimites = etiqueta("Intervalo [a, b]");
        campA = campoTexto("0");
        campA.setPrefWidth(75);
        campB = campoTexto("3.14159");
        campB.setPrefWidth(75);
        Label sep = new Label("→");
        sep.setTextFill(COLOR_EJE);
        sep.setAlignment(Pos.CENTER);
        HBox filasLimites = new HBox(8, campA, sep, campB);

        Label lblPart = etiqueta("Particiones  n = ");
        lblNValor = new Label("10");
        lblNValor.setFont(Font.font("Monospace", FontWeight.BOLD, 14));
        lblNValor.setTextFill(COLOR_ACENTO);
        HBox filaSliderLabel = new HBox(8, lblPart, lblNValor);
        filaSliderLabel.setAlignment(Pos.CENTER_LEFT);

        sliderN = new Slider(2, 200, 10);
        sliderN.setMajorTickUnit(50);
        sliderN.setMinorTickCount(4);
        sliderN.setShowTickMarks(true);
        sliderN.setShowTickLabels(true);
        sliderN.setStyle("-fx-control-inner-background: #313244;");
        sliderN.valueProperty().addListener((obs, ov, nv) -> {
            int n = (int) Math.round(nv.doubleValue());
            if ("Simpson 1/3".equals(comboMetodo.getValue()) && n % 2 != 0) n++;
            lblNValor.setText(String.valueOf(n));
            calcular();
        });

        Label lblMet = etiqueta("Método de integración");
        comboMetodo = new ComboBox<>();
        comboMetodo.getItems().addAll(METODOS);
        comboMetodo.setValue(METODOS[0]);
        comboMetodo.setMaxWidth(Double.MAX_VALUE);
        comboMetodo.setStyle(
                "-fx-background-color: #313244; -fx-text-fill: #cdd6f4; " +
                        "-fx-border-color: #45475a; -fx-border-radius: 4; -fx-background-radius: 4;");
        comboMetodo.setOnAction(e -> { actualizarInfoMetodo(); calcular(); });

        lblMetodoInfo = new Label(INFO_METODOS[0]);
        lblMetodoInfo.setWrapText(true);
        lblMetodoInfo.setFont(Font.font("Monospace", 11));
        lblMetodoInfo.setTextFill(COLOR_EJE);
        lblMetodoInfo.setMaxWidth(220);

        btnCalcular = new Button("▶  CALCULAR");
        btnCalcular.setMaxWidth(Double.MAX_VALUE);
        btnCalcular.setFont(Font.font("Monospace", FontWeight.BOLD, 13));
        btnCalcular.setStyle(
                "-fx-background-color: #cba6f7; -fx-text-fill: #1e1e2e; " +
                        "-fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 10 0;");
        btnCalcular.setOnAction(e -> calcular());
        btnCalcular.setOnMouseEntered(e ->
                btnCalcular.setStyle("-fx-background-color: #b48bef; -fx-text-fill: #1e1e2e; " +
                        "-fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 10 0;"));
        btnCalcular.setOnMouseExited(e ->
                btnCalcular.setStyle("-fx-background-color: #cba6f7; -fx-text-fill: #1e1e2e; " +
                        "-fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 10 0;"));

        lblEstado = new Label("");
        lblEstado.setWrapText(true);
        lblEstado.setFont(Font.font("Monospace", 11));
        lblEstado.setTextFill(COLOR_OK);
        lblEstado.setMaxWidth(220);

        Label lblRef = etiqueta("Funciones disponibles");
        Label ref = new Label(
                "sin(x)   cos(x)   tan(x)\n" +
                        "sqrt(x)  log(x)   exp(x)\n" +
                        "abs(x)   x^2      x^3\n" +
                        "Constantes: pi, e");
        ref.setFont(Font.font("Monospace", 10));
        ref.setTextFill(COLOR_EJE);
        ref.setStyle("-fx-background-color: #181825; -fx-padding: 8; -fx-background-radius: 4;");

        VBox panel = new VBox(10,
                lblFn, campFuncion, ejemplos,
                separador(),
                lblLimites, filasLimites,
                separador(),
                filaSliderLabel, sliderN,
                separador(),
                lblMet, comboMetodo, lblMetodoInfo,
                separador(),
                btnCalcular, lblEstado,
                separador(),
                lblRef, ref
        );
        panel.setPadding(new Insets(16));
        panel.setPrefWidth(240);
        panel.setStyle("-fx-background-color: #2a2a3e; -fx-border-color: #313244; -fx-border-width: 0 1 0 0;");
        return panel;
    }

    private StackPane crearPanelGrafica() {
        canvas = new Canvas(620, 430);
        StackPane pane = new StackPane(canvas);
        pane.setStyle("-fx-background-color: #1e1e2e;");
        pane.setPadding(new Insets(12));
        pane.widthProperty().addListener((obs, ov, nv) -> { canvas.setWidth(nv.doubleValue() - 24); calcular(); });
        pane.heightProperty().addListener((obs, ov, nv) -> { canvas.setHeight(nv.doubleValue() - 24); calcular(); });
        return pane;
    }

    private HBox crearBarraResultados() {
        lblResultado = labelResultado("Área ≈ —");
        lblDx        = labelResultado("Δx = —");
        Label lblSep1 = new Label("|");
        lblSep1.setTextFill(COLOR_EJE);
        HBox barra = new HBox(20, lblResultado, lblSep1, lblDx);
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.setPadding(new Insets(12, 20, 12, 20));
        barra.setStyle("-fx-background-color: #181825; -fx-border-color: #313244; -fx-border-width: 1 0 0 0;");
        return barra;
    }

    private void calcular() {
        String exprRaw = campFuncion.getText().trim();
        double a, b;
        int n;

        try {
            a = Double.parseDouble(campA.getText().trim());
            b = Double.parseDouble(campB.getText().trim());
        } catch (NumberFormatException e) {
            mostrarError("a y b deben ser números válidos.");
            return;
        }
        if (a >= b) { mostrarError("a debe ser menor que b."); return; }

        n = (int) Math.round(sliderN.getValue());
        if ("Simpson 1/3".equals(comboMetodo.getValue()) && n % 2 != 0) n++;

        final double aFinal = a, bFinal = b;
        final int    nFinal = n;
        final String exprFinal = exprRaw;

        try { Integrador.evaluar(exprFinal, (a + b) / 2.0); }
        catch (Exception e) { mostrarError("Función inválida: " + e.getMessage()); dibujarGraficaVacia(); return; }

        Function<Double, Double> f = x -> {
            try { return Integrador.evaluar(exprFinal, x); }
            catch (Exception ex) { return Double.NaN; }
        };

        double area;
        try { area = Integrador.calcular(comboMetodo.getValue(), f, a, b, n); }
        catch (Exception e) { mostrarError("Error al calcular: " + e.getMessage()); return; }

        double dx = (b - a) / n;
        lblResultado.setText(String.format("Área ≈ %.8f", area));
        lblDx.setText(String.format("Δx = %.6f  (n = %d)", dx, n));
        lblEstado.setTextFill(COLOR_OK);
        lblEstado.setText("✓ Calculado correctamente");

        dibujarGrafica(f, a, b, n, exprFinal);
    }

    private void dibujarGrafica(Function<Double, Double> f, double a, double b, int n, String expr) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double W = canvas.getWidth(), H = canvas.getHeight();
        double mx = 55, my = 30, mbx = 40, mby = 35;
        double gW = W - mx - mbx, gH = H - my - mby;

        gc.setFill(COLOR_FONDO_APP);
        gc.fillRect(0, 0, W, H);

        int PTS = 400;
        double[] xs = new double[PTS + 1], ys = new double[PTS + 1];
        double yMin = Double.MAX_VALUE, yMax = -Double.MAX_VALUE;
        for (int i = 0; i <= PTS; i++) {
            xs[i] = a + (b - a) * i / PTS;
            ys[i] = f.apply(xs[i]);
            if (!Double.isNaN(ys[i]) && !Double.isInfinite(ys[i])) {
                yMin = Math.min(yMin, ys[i]);
                yMax = Math.max(yMax, ys[i]);
            }
        }
        yMin = Math.min(yMin, 0.0);
        yMax = Math.max(yMax, 0.0);
        double yPad = Math.max((yMax - yMin) * 0.12, 0.3);
        yMin -= yPad; yMax += yPad;

        final double yMinF = yMin, yMaxF = yMax;
        java.util.function.DoubleUnaryOperator toPixX = x -> mx + (x - a) / (b - a) * gW;
        java.util.function.DoubleUnaryOperator toPixY = y -> my + gH - (y - yMinF) / (yMaxF - yMinF) * gH;

        gc.setStroke(COLOR_GRID); gc.setLineWidth(0.5);
        for (int i = 0; i <= 8; i++) { double px = mx + gW * i / 8; gc.strokeLine(px, my, px, my + gH); }
        for (int i = 0; i <= 6; i++) { double py = my + gH * i / 6; gc.strokeLine(mx, py, mx + gW, py); }

        double dx = (b - a) / n;
        String metodo = comboMetodo.getValue();

        gc.setFill(Color.color(COLOR_AREA.getRed(), COLOR_AREA.getGreen(), COLOR_AREA.getBlue(), 0.25));
        gc.setStroke(Color.color(COLOR_AREA.getRed(), COLOR_AREA.getGreen(), COLOR_AREA.getBlue(), 0.7));
        gc.setLineWidth(1.0);

        if ("Trapecio".equals(metodo) || "Simpson 1/3".equals(metodo)) {
            double[] polyX = new double[n * 2 + 4], polyY = new double[n * 2 + 4];
            int idx = 0;
            polyX[idx] = toPixX.applyAsDouble(a); polyY[idx++] = toPixY.applyAsDouble(0);
            for (int i = 0; i <= n; i++) {
                double xi = a + i * dx, yi = f.apply(xi);
                if (Double.isNaN(yi)) yi = 0;
                polyX[idx] = toPixX.applyAsDouble(xi); polyY[idx++] = toPixY.applyAsDouble(yi);
            }
            polyX[idx] = toPixX.applyAsDouble(b); polyY[idx++] = toPixY.applyAsDouble(0);
            gc.fillPolygon(polyX, polyY, idx);
            gc.setStroke(Color.color(COLOR_AREA.getRed(), COLOR_AREA.getGreen(), COLOR_AREA.getBlue(), 0.5));
            for (int i = 0; i <= n; i++) {
                double xi = a + i * dx, yi = f.apply(xi);
                if (!Double.isNaN(yi)) gc.strokeLine(toPixX.applyAsDouble(xi), toPixY.applyAsDouble(0), toPixX.applyAsDouble(xi), toPixY.applyAsDouble(yi));
            }
        } else {
            for (int i = 0; i < n; i++) {
                double xL = a + i * dx, xR = a + (i + 1) * dx, xM = (xL + xR) / 2;
                double xAltura = "Riemann Izquierdo".equals(metodo) ? xL : "Riemann Derecho".equals(metodo) ? xR : xM;
                double h = f.apply(xAltura);
                if (Double.isNaN(h)) continue;
                double px1 = toPixX.applyAsDouble(xL), px2 = toPixX.applyAsDouble(xR);
                double py0 = toPixY.applyAsDouble(0),  pyH = toPixY.applyAsDouble(h);
                gc.fillRect(px1, Math.min(py0, pyH), px2 - px1, Math.abs(py0 - pyH));
                gc.strokeRect(px1, Math.min(py0, pyH), px2 - px1, Math.abs(py0 - pyH));
            }
        }

        gc.setStroke(COLOR_EJE); gc.setLineWidth(1.2);
        gc.strokeLine(mx, toPixY.applyAsDouble(0), mx + gW, toPixY.applyAsDouble(0));

        gc.setStroke(COLOR_CURVA); gc.setLineWidth(2.2);
        gc.beginPath();
        boolean primerPunto = true;
        for (int i = 0; i <= PTS; i++) {
            if (Double.isNaN(ys[i]) || Double.isInfinite(ys[i])) { primerPunto = true; continue; }
            double px = toPixX.applyAsDouble(xs[i]), py = toPixY.applyAsDouble(ys[i]);
            if (primerPunto) { gc.moveTo(px, py); primerPunto = false; } else gc.lineTo(px, py);
        }
        gc.stroke();

        gc.setStroke(COLOR_EJE); gc.setLineWidth(1.0);
        gc.strokeRect(mx, my, gW, gH);
        gc.setFill(COLOR_TEXTO); gc.setFont(Font.font("Monospace", 10));
        for (int i = 0; i <= 8; i++) {
            double xv = a + (b - a) * i / 8;
            gc.fillText(String.format("%.2f", xv), mx + gW * i / 8 - 12, my + gH + 16);
        }
        for (int i = 0; i <= 6; i++) {
            double yv = yMinF + (yMaxF - yMinF) * (6 - i) / 6;
            String txt = String.format("%.2f", yv);
            gc.fillText(txt, mx - txt.length() * 6 - 4, my + gH * i / 6 + 4);
        }
        gc.setFill(COLOR_ACENTO); gc.setFont(Font.font("Monospace", FontWeight.BOLD, 12));
        gc.fillText("f(x) = " + expr, mx + 6, my + 16);
        gc.setFill(COLOR_CURVA); gc.fillRect(W - mbx - 100, my + 6, 10, 10);
        gc.setFill(COLOR_TEXTO); gc.setFont(Font.font("Monospace", 10));
        gc.fillText("curva f(x)", W - mbx - 86, my + 16);
        gc.setFill(Color.color(COLOR_AREA.getRed(), COLOR_AREA.getGreen(), COLOR_AREA.getBlue(), 0.5));
        gc.fillRect(W - mbx - 100, my + 22, 10, 10);
        gc.setFill(COLOR_TEXTO);
        gc.fillText("área aprox.", W - mbx - 86, my + 32);
    }

    private void dibujarGraficaVacia() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(COLOR_FONDO_APP);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(COLOR_EJE); gc.setFont(Font.font("Monospace", 14));
        gc.fillText("Ingresa una función válida para ver la gráfica.", 40, canvas.getHeight() / 2);
    }

    private Label etiqueta(String texto) {
        Label l = new Label(texto);
        l.setFont(Font.font("Monospace", FontWeight.BOLD, 12));
        l.setTextFill(COLOR_ACENTO);
        return l;
    }

    private TextField campoTexto(String valorInicial) {
        TextField tf = new TextField(valorInicial);
        tf.setFont(Font.font("Monospace", 13));
        tf.setStyle("-fx-background-color: #313244; -fx-text-fill: #cdd6f4; " +
                "-fx-border-color: #45475a; -fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 6 8;");
        return tf;
    }

    private Label labelResultado(String texto) {
        Label l = new Label(texto);
        l.setFont(Font.font("Monospace", FontWeight.BOLD, 14));
        l.setTextFill(COLOR_OK);
        return l;
    }

    private Separator separador() {
        Separator s = new Separator();
        s.setStyle("-fx-background-color: #313244;");
        return s;
    }

    private void mostrarError(String msg) {
        lblEstado.setTextFill(COLOR_ERROR);
        lblEstado.setText("✗ " + msg);
    }

    private void actualizarInfoMetodo() {
        int idx = comboMetodo.getSelectionModel().getSelectedIndex();
        if (idx >= 0 && idx < INFO_METODOS.length) lblMetodoInfo.setText(INFO_METODOS[idx]);
    }
}