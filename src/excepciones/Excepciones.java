package excepciones;

public class Excepciones {

    public static class TiendaException extends Exception {
        public TiendaException(String mensaje) {
            super(mensaje);
        }
    }

    public static class ProductoNoEncontradoException extends TiendaException {
        public ProductoNoEncontradoException(int id) {
            super("No existe un producto con id " + id);
        }
    }

    public static class StockInsuficienteException extends TiendaException {
        public StockInsuficienteException(String nombre, int disponible) {
            super("Stock insuficiente de " + nombre + ". Disponible: " + disponible);
        }
    }

    public static class CarritoVacioException extends TiendaException {
        public CarritoVacioException() {
            super("El carrito esta vacio");
        }
    }

    public static class PromocionInvalidaException extends TiendaException {
        public PromocionInvalidaException(String mensaje) {
            super(mensaje);
        }
    }

    // Solo se usa en la version con base de datos; en la otra version no estorba
    public static class BaseDeDatosException extends TiendaException {
        public BaseDeDatosException(String mensaje) {
            super("Error de base de datos: " + mensaje);
        }
    }
}