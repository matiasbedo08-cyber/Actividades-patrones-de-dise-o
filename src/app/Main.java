package app;

import comando.Comandos.*;
import estrategia.Estrategias.*;
import excepciones.Excepciones.TiendaException;
import modelo.Modelo.Producto;
import observer.Observadores.*;
import servicio.TiendaService;

public class Main {
    public static void main(String[] args) throws TiendaException {
        CentroNotificaciones notificador = new CentroNotificaciones();
        notificador.agregarObservador(new Cliente("Ana"));
        notificador.agregarObservador(new Cliente("Luis"));
        notificador.agregarObservador(new Administrador("Carlos"));

        TiendaService tienda = new TiendaService(notificador);
        tienda.registrarProducto(new Producto(1, "Pantalon jean", 80.0, 10));
        tienda.registrarProducto(new Producto(2, "Camisa", 50.0, 6));
        tienda.registrarProducto(new Producto(3, "Polo", 35.0, 4));
        tienda.registrarProducto(new Producto(4, "Casaca", 150.0, 2));

        ControlRemoto remoto = new ControlRemoto();

        System.out.println("=== CATALOGO ===");
        for (Producto p : tienda.catalogo()) {
            System.out.println("   " + p);
        }

        System.out.println("\n=== AGREGAR PRODUCTOS ===");
        remoto.presionar(new AgregarProductoComando(tienda, 1, 3));
        remoto.presionar(new AgregarProductoComando(tienda, 2, 2));
        System.out.println(tienda.resumen());

        System.out.println("\n=== PROMOCIONES (Strategy + Observer) ===");
        remoto.presionar(new CambiarPromocionComando(tienda, new DosPorUno()));
        System.out.println(tienda.resumen());
        remoto.presionar(new CambiarPromocionComando(tienda, new DescuentoPorcentaje(20)));
        System.out.println(tienda.resumen());

        System.out.println("\n=== ERRORES (Excepciones) ===");
        remoto.presionar(new AgregarProductoComando(tienda, 4, 10));
        remoto.presionar(new AgregarProductoComando(tienda, 99, 1));
        remoto.presionar(new QuitarProductoComando(tienda, 3, 1));

        System.out.println("\n=== DESHACER (Pila) ===");
        remoto.deshacer();
        System.out.println(tienda.resumen());
        remoto.presionar(new VaciarCarritoComando(tienda));
        System.out.println(tienda.resumen());
        remoto.deshacer();
        System.out.println(tienda.resumen());

        System.out.println("\n=== CONFIRMAR COMPRA ===");
        remoto.presionar(new AgregarProductoComando(tienda, 4, 2));
        remoto.presionar(new ConfirmarCompraComando(tienda));
        remoto.presionar(new ConfirmarCompraComando(tienda));
        remoto.deshacer();

        System.out.println("\n=== CATALOGO FINAL ===");
        for (Producto p : tienda.catalogo()) {
            System.out.println("   " + p);
        }
    }
}