package org.example.demo1;

class Parser {

    private final String expr;
    private int pos = 0;

    Parser(String expresion) {
        this.expr = expresion.replaceAll("\\s+", "");
    }

    double parsear() throws Exception {
        double resultado = suma();
        if (pos < expr.length()) {
            throw new Exception("Carácter inesperado en posición " + pos + ": '" + expr.charAt(pos) + "'");
        }
        return resultado;
    }

    private double suma() throws Exception {
        double v = producto();
        while (pos < expr.length() && (expr.charAt(pos) == '+' || expr.charAt(pos) == '-')) {
            char op = expr.charAt(pos++);
            double derecha = producto();
            v = (op == '+') ? v + derecha : v - derecha;
        }
        return v;
    }

    private double producto() throws Exception {
        double v = potencia();
        while (pos < expr.length() && (expr.charAt(pos) == '*' || expr.charAt(pos) == '/')) {
            if (expr.charAt(pos) == '*' && pos + 1 < expr.length() && expr.charAt(pos + 1) == '*') break;
            char op = expr.charAt(pos++);
            double derecha = potencia();
            if (op == '/' && derecha == 0) throw new Exception("División por cero");
            v = (op == '*') ? v * derecha : v / derecha;
        }
        return v;
    }

    private double potencia() throws Exception {
        double base = unario();
        if (pos + 1 < expr.length() && expr.charAt(pos) == '*' && expr.charAt(pos + 1) == '*') {
            pos += 2;
            double exp = unario();
            return Math.pow(base, exp);
        }
        return base;
    }

    private double unario() throws Exception {
        if (pos < expr.length() && expr.charAt(pos) == '-') { pos++; return -unario(); }
        if (pos < expr.length() && expr.charAt(pos) == '+') { pos++; return unario(); }
        return primario();
    }

    private double primario() throws Exception {
        if (pos < expr.length() && expr.charAt(pos) == '(') {
            pos++;
            double v = suma();
            if (pos >= expr.length() || expr.charAt(pos) != ')')
                throw new Exception("Falta ')' en la expresión");
            pos++;
            return v;
        }

        if (Character.isLetter(expr.charAt(pos))) {
            StringBuilder nombre = new StringBuilder();
            while (pos < expr.length() && Character.isLetter(expr.charAt(pos)))
                nombre.append(expr.charAt(pos++));
            String fn = nombre.toString();
            if (pos >= expr.length() || expr.charAt(pos) != '(')
                throw new Exception("Se esperaba '(' después de '" + fn + "'");
            pos++;
            double arg = suma();
            if (pos >= expr.length() || expr.charAt(pos) != ')')
                throw new Exception("Falta ')' en llamada a " + fn);
            pos++;
            return switch (fn) {
                case "sin"   -> Math.sin(arg);
                case "cos"   -> Math.cos(arg);
                case "tan"   -> Math.tan(arg);
                case "sqrt"  -> { if (arg < 0) throw new Exception("sqrt de número negativo"); yield Math.sqrt(arg); }
                case "log"   -> { if (arg <= 0) throw new Exception("log de número no positivo"); yield Math.log(arg); }
                case "log10" -> { if (arg <= 0) throw new Exception("log10 de número no positivo"); yield Math.log10(arg); }
                case "exp"   -> Math.exp(arg);
                case "abs"   -> Math.abs(arg);
                case "ceil"  -> Math.ceil(arg);
                case "floor" -> Math.floor(arg);
                default      -> throw new Exception("Función desconocida: " + fn);
            };
        }

        if (Character.isDigit(expr.charAt(pos)) || expr.charAt(pos) == '.') {
            StringBuilder num = new StringBuilder();
            while (pos < expr.length() && (Character.isDigit(expr.charAt(pos)) || expr.charAt(pos) == '.'))
                num.append(expr.charAt(pos++));
            if (pos < expr.length() && expr.charAt(pos) == 'e') {
                num.append(expr.charAt(pos++));
                if (expr.charAt(pos) == '-' || expr.charAt(pos) == '+') num.append(expr.charAt(pos++));
                while (pos < expr.length() && Character.isDigit(expr.charAt(pos))) num.append(expr.charAt(pos++));
            }
            try { return Double.parseDouble(num.toString()); }
            catch (NumberFormatException e) { throw new Exception("Número mal formado: " + num); }
        }

        throw new Exception("Token inesperado en posición " + pos + ": '" + expr.charAt(pos) + "'");
    }
}
