package observer;

import java.util.ArrayList;
import java.util.List;

public class Observadores {

    public interface Observador {
        void actualizar(String evento, String detalle);
    }

    public interface Sujeto {
        void agregarObservador(Observador o);
        void quitarObservador(Observador o);
        void notificar(String evento, String detalle);
    }

    public static class CentroNotificaciones implements Sujeto {
        private final List<Observador> observadores = new ArrayList<>();

        @Override
        public void agregarObservador(Observador o) {
            observadores.add(o);
        }

        @Override
        public void quitarObservador(Observador o) {
            observadores.remove(o);
        }

        @Override
        public void notificar(String evento, String detalle) {
            for (Observador o : observadores) {
                o.actualizar(evento, detalle);
            }
        }
    }

    public static class Cliente implements Observador {
        private final String nombre;

        public Cliente(String nombre) {
            this.nombre = nombre;
        }

        @Override
        public void actualizar(String evento, String detalle) {
            if (evento.equals("PROMOCION")) {
                System.out.println("   [Notificacion a " + nombre + "] Nueva promocion: " + detalle);
            }
        }
    }

    public static class Administrador implements Observador {
        private final String nombre;

        public Administrador(String nombre) {
            this.nombre = nombre;
        }

        @Override
        public void actualizar(String evento, String detalle) {
            if (evento.equals("COMPRA") || evento.equals("STOCK_BAJO")) {
                System.out.println("   [Admin " + nombre + "] " + evento + ": " + detalle);
            }
        }
    }
}
