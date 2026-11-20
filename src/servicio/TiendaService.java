package servicio;

import estrategia.Estrategias.PromocionStrategy;
import estrategia.Estrategias.SinPromocion;
import excepciones.Excepciones.*;
import modelo.Modelo.*;
import observer.Observadores.Sujeto;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TiendaService {
    private static final int STOCK_MINIMO = 2;

    private final Sujeto notificador;
    private final Map<Integer, Producto> productos = new LinkedHashMap<>();
    private final Map<Integer, ItemCarrito> carrito = new LinkedHashMap<>();
    private PromocionStrategy promocion = new SinPromocion();

    public TiendaService(Sujeto notificador) {
        this.notificador = notificador;
    }

    public void registrarProducto(Producto p) {
        productos.put(p.getId(), p);
    }

    private Producto buscarProducto(int id) throws ProductoNoEncontradoException {
        Producto p = productos.get(id);
        if (p == null) {
            throw new ProductoNoEncontradoException(id);
        }
        return p;
    }

    public void agregar(int id, int cantidad) throws TiendaException {
        validarCantidad(cantidad);
        Producto p = buscarProducto(id);
        ItemCarrito item = carrito.get(id);
        int enCarrito = (item == null) ? 0 : item.getCantidad();

        // Se valida contra el stock menos lo que ya esta en el carrito
        if (enCarrito + cantidad > p.getStock()) {
            throw new StockInsuficienteException(p.getNombre(), p.getStock() - enCarrito);
        }
        if (item == null) {
            carrito.put(id, new ItemCarrito(p, cantidad));
        } else {
            item.aumentar(cantidad);
        }
    }

    public void quitar(int id, int cantidad) throws TiendaException {
        validarCantidad(cantidad);
        ItemCarrito item = carrito.get(id);
        if (item == null || item.getCantidad() < cantidad) {
            throw new TiendaException("No hay " + cantidad + " unidad(es) del producto " + id + " en el carrito");
        }
        item.disminuir(cantidad);
        if (item.getCantidad() == 0) {
            carrito.remove(id);
        }
    }

    public void cambiarPromocion(PromocionStrategy nueva) {
        this.promocion = nueva;
        notificador.notificar("PROMOCION", nueva.getNombre());
    }

    public PromocionStrategy getPromocion() {
        return promocion;
    }

    public void vaciar() {
        carrito.clear();
    }

    // Copias de los items para que el comando pueda respaldar el estado
    public List<ItemCarrito> obtenerItems() {
        List<ItemCarrito> copia = new ArrayList<>();
        for (ItemCarrito i : carrito.values()) {
            copia.add(new ItemCarrito(i.getProducto(), i.getCantidad()));
        }
        return copia;
    }

    public void restaurarItems(List<ItemCarrito> items) {
        carrito.clear();
        for (ItemCarrito i : items) {
            carrito.put(i.getProducto().getId(), new ItemCarrito(i.getProducto(), i.getCantidad()));
        }
    }

    public double calcularTotal() {
        return promocion.calcularTotal(new ArrayList<>(carrito.values()));
    }

    public double confirmarCompra() throws TiendaException {
        if (carrito.isEmpty()) {
            throw new CarritoVacioException();
        }
        double total = calcularTotal();

        for (ItemCarrito item : carrito.values()) {
            Producto p = item.getProducto();
            p.reducirStock(item.getCantidad());
            if (p.getStock() <= STOCK_MINIMO) {
                notificador.notificar("STOCK_BAJO", p.getNombre() + " quedan " + p.getStock());
            }
        }
        carrito.clear();
        notificador.notificar("COMPRA", "Compra confirmada por S/ " + String.format("%.2f", total));
        return total;
    }

    public String resumen() {
        StringBuilder sb = new StringBuilder("   --- Carrito ---\n");
        if (carrito.isEmpty()) {
            sb.append("   (vacio)\n");
        }
        for (ItemCarrito i : carrito.values()) {
            sb.append("   ").append(i.getCantidad()).append(" x ")
              .append(i.getProducto().getNombre())
              .append(" = S/ ").append(String.format("%.2f", i.getSubtotal())).append("\n");
        }
        sb.append("   Promocion: ").append(promocion.getNombre()).append("\n");
        sb.append("   TOTAL: S/ ").append(String.format("%.2f", calcularTotal()));
        return sb.toString();
    }

    public List<Producto> catalogo() {
        return new ArrayList<>(productos.values());
    }

    private void validarCantidad(int cantidad) throws TiendaException {
        if (cantidad <= 0) {
            throw new TiendaException("La cantidad debe ser mayor a 0");
        }
    }
}