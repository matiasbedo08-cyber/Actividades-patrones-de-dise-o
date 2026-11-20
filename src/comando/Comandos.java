package comando;

import estrategia.Estrategias.PromocionStrategy;
import excepciones.Excepciones.TiendaException;
import modelo.Modelo.ItemCarrito;
import servicio.TiendaService;
import java.util.List;
import java.util.Stack;

public class Comandos {

    public interface Comando {
        void ejecutar() throws TiendaException;
        void deshacer() throws TiendaException;
        String getDescripcion();

        default boolean esDeshacible() {
            return true;
        }
    }

    public static class AgregarProductoComando implements Comando {
        private final TiendaService tienda;
        private final int idProducto;
        private final int cantidad;

        public AgregarProductoComando(TiendaService tienda, int idProducto, int cantidad) {
            this.tienda = tienda;
            this.idProducto = idProducto;
            this.cantidad = cantidad;
        }

        @Override
        public void ejecutar() throws TiendaException {
            tienda.agregar(idProducto, cantidad);
        }

        @Override
        public void deshacer() throws TiendaException {
            tienda.quitar(idProducto, cantidad);
        }

        @Override
        public String getDescripcion() {
            return "Agregar " + cantidad + " unidad(es) del producto " + idProducto;
        }
    }

    public static class QuitarProductoComando implements Comando {
        private final TiendaService tienda;
        private final int idProducto;
        private final int cantidad;

        public QuitarProductoComando(TiendaService tienda, int idProducto, int cantidad) {
            this.tienda = tienda;
            this.idProducto = idProducto;
            this.cantidad = cantidad;
        }

        @Override
        public void ejecutar() throws TiendaException {
            tienda.quitar(idProducto, cantidad);
        }

        @Override
        public void deshacer() throws TiendaException {
            tienda.agregar(idProducto, cantidad);
        }

        @Override
        public String getDescripcion() {
            return "Quitar " + cantidad + " unidad(es) del producto " + idProducto;
        }
    }

    public static class CambiarPromocionComando implements Comando {
        private final TiendaService tienda;
        private final PromocionStrategy nueva;
        private PromocionStrategy anterior;

        public CambiarPromocionComando(TiendaService tienda, PromocionStrategy nueva) {
            this.tienda = tienda;
            this.nueva = nueva;
        }

        @Override
        public void ejecutar() throws TiendaException {
            anterior = tienda.getPromocion();
            tienda.cambiarPromocion(nueva);
        }

        @Override
        public void deshacer() throws TiendaException {
            tienda.cambiarPromocion(anterior);
        }

        @Override
        public String getDescripcion() {
            return "Cambiar promocion a: " + nueva.getNombre();
        }
    }

    public static class VaciarCarritoComando implements Comando {
        private final TiendaService tienda;
        private List<ItemCarrito> respaldo;

        public VaciarCarritoComando(TiendaService tienda) {
            this.tienda = tienda;
        }

        @Override
        public void ejecutar() throws TiendaException {
            respaldo = tienda.obtenerItems();
            tienda.vaciar();
        }

        @Override
        public void deshacer() throws TiendaException {
            tienda.restaurarItems(respaldo);
        }

        @Override
        public String getDescripcion() {
            return "Vaciar carrito";
        }
    }

    public static class ConfirmarCompraComando implements Comando {
        private final TiendaService tienda;

        public ConfirmarCompraComando(TiendaService tienda) {
            this.tienda = tienda;
        }

        @Override
        public void ejecutar() throws TiendaException {
            double total = tienda.confirmarCompra();
            System.out.println("   Total cobrado: S/ " + String.format("%.2f", total));
        }

        @Override
        public void deshacer() throws TiendaException {
            throw new TiendaException("Una compra confirmada no se puede deshacer");
        }

        @Override
        public boolean esDeshacible() {
            return false;
        }

        @Override
        public String getDescripcion() {
            return "Confirmar compra";
        }
    }
    public static class ControlRemoto {
        private final Stack<Comando> historial = new Stack<>();

        public void presionar(Comando comando) {
            System.out.println(">> " + comando.getDescripcion());
            try {
                comando.ejecutar();
                if (comando.esDeshacible()) {
                    historial.push(comando);
                } else {
                    historial.clear();
                }
            } catch (TiendaException e) {
                System.out.println("   ERROR: " + e.getMessage());
            }
        }

        public void deshacer() {
            if (historial.isEmpty()) {
                System.out.println(">> Deshacer\n   No hay nada que deshacer");
                return;
            }
            Comando ultimo = historial.pop();
            System.out.println(">> Deshacer: " + ultimo.getDescripcion());
            try {
                ultimo.deshacer();
            } catch (TiendaException e) {
                System.out.println("   ERROR: " + e.getMessage());
            }
        }
    }
}
