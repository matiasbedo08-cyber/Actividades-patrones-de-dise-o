package estrategia;

import excepciones.Excepciones.PromocionInvalidaException;
import modelo.Modelo.ItemCarrito;
import java.util.List;

public class Estrategias {

    public interface PromocionStrategy {
        double calcularTotal(List<ItemCarrito> items);
        String getNombre();
    }

    public static class SinPromocion implements PromocionStrategy {
        @Override
        public double calcularTotal(List<ItemCarrito> items) {
            double total = 0;
            for (ItemCarrito item : items) {
                total += item.getSubtotal();
            }
            return total;
        }

        @Override
        public String getNombre() {
            return "Sin promocion";
        }
    }

    public static class DescuentoPorcentaje implements PromocionStrategy {
        private final double porcentaje;

        public DescuentoPorcentaje(double porcentaje) throws PromocionInvalidaException {
            if (porcentaje <= 0 || porcentaje > 100) {
                throw new PromocionInvalidaException("El porcentaje debe estar entre 1 y 100");
            }
            this.porcentaje = porcentaje;
        }

        @Override
        public double calcularTotal(List<ItemCarrito> items) {
            double total = 0;
            for (ItemCarrito item : items) {
                total += item.getSubtotal();
            }
            return total - (total * porcentaje / 100);
        }

        @Override
        public String getNombre() {
            return porcentaje + "% de descuento";
        }
    }

    public static class DosPorUno implements PromocionStrategy {
        @Override
        public double calcularTotal(List<ItemCarrito> items) {
            double total = 0;
            for (ItemCarrito item : items) {
                int cantidad = item.getCantidad();
                // De cada 2 unidades se paga 1; si sobra una, se paga completa
                int pagadas = cantidad / 2 + cantidad % 2;
                total += item.getProducto().getPrecio() * pagadas;
            }
            return total;
        }

        @Override
        public String getNombre() {
            return "2x1";
        }
    }

    public static class DescuentoPorMonto implements PromocionStrategy {
        private final double montoMinimo;
        private final double descuento;

        public DescuentoPorMonto(double montoMinimo, double descuento) {
            this.montoMinimo = montoMinimo;
            this.descuento = descuento;
        }

        @Override
        public double calcularTotal(List<ItemCarrito> items) {
            double total = 0;
            for (ItemCarrito item : items) {
                total += item.getSubtotal();
            }
            if (total >= montoMinimo) {
                total -= descuento;
            }
            return total;
        }

        @Override
        public String getNombre() {
            return "S/ " + descuento + " de descuento desde S/ " + montoMinimo;
        }
    }
}