package modelo;

public class Modelo {

    public static class Producto {
        private final int id;
        private final String nombre;
        private final double precio;
        private int stock;

        public Producto(int id, String nombre, double precio, int stock) {
            this.id = id;
            this.nombre = nombre;
            this.precio = precio;
            this.stock = stock;
        }

        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public double getPrecio() { return precio; }
        public int getStock() { return stock; }

        public void reducirStock(int cantidad) {
            stock -= cantidad;
        }

        @Override
        public String toString() {
            return id + " - " + nombre + " | S/ " + String.format("%.2f", precio) + " | stock: " + stock;
        }
    }

    public static class ItemCarrito {
        private final Producto producto;
        private int cantidad;

        public ItemCarrito(Producto producto, int cantidad) {
            this.producto = producto;
            this.cantidad = cantidad;
        }

        public Producto getProducto() { return producto; }
        public int getCantidad() { return cantidad; }

        public double getSubtotal() {
            return producto.getPrecio() * cantidad;
        }

        public void aumentar(int c) { cantidad += c; }
        public void disminuir(int c) { cantidad -= c; }
    }
}