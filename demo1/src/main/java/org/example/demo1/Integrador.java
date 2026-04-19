package org.example.demo1;

import java.util.function.Function;

public class Integrador {

    public static double evaluar(String expresion, double x) throws Exception {
        // Normalizamos la expresión para facilitar el parseo
        String expr = expresion.trim()
                .toLowerCase()
                .replace("pi", String.valueOf(Math.PI))
                .replace("e",  String.valueOf(Math.E))
                .replace("^",  "**");           // poder usar tanto ^ como **

        // Sustituimos la variable x por su valor numérico
        expr = sustituirX(expr, x);

        return new Parser(expr).parsear();
    }

    private static String sustituirX(String expr, double x) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            boolean esX = (c == 'x')
                    && (i == 0 || !Character.isLetter(expr.charAt(i - 1)))
                    && (i == expr.length() - 1 || !Character.isLetter(expr.charAt(i + 1)));
            if (esX) {
                sb.append("(").append(x).append(")");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static double riemannIzquierdo(Function<Double, Double> f,
                                          double a, double b, int n) {
        double dx   = (b - a) / n;
        double suma = 0.0;
        for (int i = 0; i < n; i++) {
            double xIzq = a + i * dx;
            suma += f.apply(xIzq);
        }
        return suma * dx;
    }


    public static double riemannDerecho(Function<Double, Double> f,
                                        double a, double b, int n) {
        double dx   = (b - a) / n;
        double suma = 0.0;
        for (int i = 1; i <= n; i++) {
            double xDer = a + i * dx;
            suma += f.apply(xDer);
        }
        return suma * dx;
    }

    public static double puntoMedio(Function<Double, Double> f,
                                    double a, double b, int n) {
        double dx   = (b - a) / n;
        double suma = 0.0;
        for (int i = 0; i < n; i++) {
            double xMedio = a + (i + 0.5) * dx;
            suma += f.apply(xMedio);
        }
        return suma * dx;
    }

    public static double trapecio(Function<Double, Double> f,
                                  double a, double b, int n) {
        double dx   = (b - a) / n;
        double suma = f.apply(a) + f.apply(b);
        for (int i = 1; i < n; i++) {
            double xi = a + i * dx;
            suma += 2.0 * f.apply(xi);
        }
        return suma * dx / 2.0;
    }

    public static double simpson(Function<Double, Double> f,
                                 double a, double b, int n) {
        if (n % 2 != 0) {
            n++;   // forzamos n par automáticamente
        }
        double dx   = (b - a) / n;
        double suma = f.apply(a) + f.apply(b);
        for (int i = 1; i < n; i++) {
            double xi = a + i * dx;
            suma += (i % 2 == 0) ? 2.0 * f.apply(xi) : 4.0 * f.apply(xi);
        }
        return suma * dx / 3.0;
    }

    public static double calcular(String metodo, Function<Double, Double> f,
                                  double a, double b, int n) {
        return switch (metodo) {
            case "Riemann Izquierdo" -> riemannIzquierdo(f, a, b, n);
            case "Riemann Derecho"   -> riemannDerecho(f, a, b, n);
            case "Punto Medio"       -> puntoMedio(f, a, b, n);
            case "Trapecio"          -> trapecio(f, a, b, n);
            case "Simpson 1/3"       -> simpson(f, a, b, n);
            default -> throw new IllegalArgumentException("Método desconocido: " + metodo);
        };
    }
}