package juego;

import java.util.Random;
import java.awt.Color;
import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;
import entorno.InterfaceJuego;

public class Juego extends InterfaceJuego {
	private Image fondo;
	private Entorno entorno;

	private Piso[] pisos;
	private Computadora computadora;

	private Barbarianna barbarianna;
	private Rayo rayoDeBarbarianna;

	private Velociraptor[] velociraptors;
	private Rayo[] rayoDeVelociraptors;
	private double tiempoDeEsperaParaCrearVelociraptor; // para que aparezcan de manera aleatoria
	private double tiempoDeEsperaParaCrearRayo;
	private Random random;

	private int points;
	private int lives;
	private int kills;

	private boolean gano;

	public Juego() {
		this.entorno = new Entorno(this, "Castlevania", 800, 600);
		this.fondo = Herramientas.cargarImagen("fondo.png");
		this.gano = false;

		double x = entorno.ancho() / 2;
		double y = entorno.alto() / 2;
		this.pisos = new Piso[5];
		pisos[0] = new Piso(x, y + 240, "piso.png");
		pisos[1] = new Piso(x - 164, y + 140, "pisoSuperiores.png");
		pisos[2] = new Piso(x + 164, y + 40, "pisoSuperiores.png");
		pisos[3] = new Piso(x - 164, y - 60, "pisoSuperiores.png");
		pisos[4] = new Piso(x + 164, y - 160, "pisoSuperiores.png");

		this.computadora = new Computadora(entorno.ancho() / 2 + 15, entorno.alto() - 500);
		this.barbarianna = new Barbarianna(entorno.ancho() - 775, entorno.alto() - 100, 2.5);
		this.barbarianna.actualizarPisos(pisos);

		this.velociraptors = new Velociraptor[6];
		this.rayoDeVelociraptors = new Rayo[6];
		random = new Random();

		this.points = 0;
		this.lives = 5;
		this.kills = 0;
		// Inicia el juego!
		this.entorno.iniciar();
	}

	public void tick() {
		
		entorno.dibujarImagen(fondo, entorno.ancho() / 2, entorno.alto() / 2, 0);
		entorno.cambiarFont("sans", 20, Color.WHITE);
		entorno.escribirTexto("lives: " + lives, 40, entorno.alto() - 20);
		entorno.escribirTexto("points: " + points, entorno.ancho() / 2 - 40, entorno.alto() - 20);
		entorno.escribirTexto("kills: " + kills, entorno.ancho() - 120, entorno.alto() - 20);

		for (Piso p : pisos) {
			p.dibujar(entorno);
		}

		computadora.dibujar(entorno);

		if (tiempoDeEsperaParaCrearVelociraptor > 0) {
			tiempoDeEsperaParaCrearVelociraptor--;
		} else if (tiempoDeEsperaParaCrearVelociraptor == 0) {

		}

		if (tiempoDeEsperaParaCrearRayo > 0) {
			tiempoDeEsperaParaCrearRayo--;
		} else if (tiempoDeEsperaParaCrearRayo == 0) {

		}

		for (int i = 0; i < velociraptors.length; i++) {
			if (velociraptors[i] != null) {
				velociraptors[i].dibujar(entorno);
				velociraptors[i].actualizar(entorno, pisos);
				if (velociraptors[i].llegueAlFinalDelCamino() == true) {
					velociraptors[i] = null;
				} else if (rayoDeBarbarianna != null && velociraptors[i].meChocoElRayo(rayoDeBarbarianna)) {
					velociraptors[i] = null;
					rayoDeBarbarianna = null;
					kills++;
					points = points + 10;
				}
			}
			if (velociraptors[i] == null && tiempoDeEsperaParaCrearVelociraptor == 0) {
				velociraptors[i] = new Velociraptor(entorno.ancho() + 100, entorno.alto() - 502, 1.5, pisos);
				tiempoDeEsperaParaCrearVelociraptor = random.nextInt(150) + 200;
			}
		}

		for (int r = 0; r < rayoDeVelociraptors.length; r++) {
			if (rayoDeVelociraptors[r] != null) {
				rayoDeVelociraptors[r].dibujar(entorno);
				rayoDeVelociraptors[r].mover();
				if (rayoDeVelociraptors[r].getX() > entorno.ancho() || rayoDeVelociraptors[r].getX() < 0) {
					rayoDeVelociraptors[r] = null;
				}
			} else if (rayoDeVelociraptors[r] == null && velociraptors[r] != null && tiempoDeEsperaParaCrearRayo == 0) {
				rayoDeVelociraptors[r] = velociraptors[r].dispararRayo();
				tiempoDeEsperaParaCrearRayo = random.nextInt(100);
				System.out.print(tiempoDeEsperaParaCrearRayo);
			}
			if (barbarianna.chocasteConRayo(rayoDeVelociraptors)) {
				rayoDeVelociraptors[r]=null;
				barbarianna = null;
				barbarianna = new Barbarianna(entorno.ancho() - 775, entorno.alto() - 95, 2.5);
				
			}
		}

		barbarianna.dibujar(entorno);
		barbarianna.Actualizar(entorno);
		barbarianna.actualizarPisos(pisos);
		if (entorno.estaPresionada('w')) {
			barbarianna.saltar();
		}
		if (entorno.estaPresionada(entorno.TECLA_ESPACIO) && rayoDeBarbarianna == null) {
			rayoDeBarbarianna = barbarianna.dispararRayo();
		}
		if (entorno.estaPresionada('u') && barbarianna.estaHabilitadoParaSubirDePiso(entorno)) {
			barbarianna.cuandoSubirUnPiso(entorno);
		}
		if (entorno.estaPresionada('a')) {
			barbarianna.moverHaciaIzquierda(entorno);
		} else if (entorno.estaPresionada('d')) {
			barbarianna.moverHaciaDerecha(entorno);
		} else if (entorno.estaPresionada('s')) {
			barbarianna.agachar();
		} else {
			barbarianna.estaQuieta();
		}
		if (barbarianna.estaSubiendoUnPiso()) {
			barbarianna.saltarUnPiso(entorno, pisos);
		}
		if (barbarianna.chocasteConVelociraptor(velociraptors)) {
			barbarianna = null;
			lives--;
			barbarianna = new Barbarianna(entorno.ancho() - 775, entorno.alto() - 95, 2.5);
			
		}
		if (barbarianna.chocasteConRayo(rayoDeVelociraptors)) {
			barbarianna = null;
			lives--;
			barbarianna = new Barbarianna(entorno.ancho() - 775, entorno.alto() - 95, 2.5);
		}

		
		if (barbarianna.estaTocandoLaComputadora(computadora)) {
			gano = true;
		}

		if (rayoDeBarbarianna != null) {
			rayoDeBarbarianna.dibujar(entorno);
			rayoDeBarbarianna.mover();
			if (rayoDeBarbarianna.getX() > entorno.ancho() || rayoDeBarbarianna.getX() < 0) {
				rayoDeBarbarianna = null;
			}
		}
		
		if (lives == 0) {
			entorno.dibujarImagen(Herramientas.cargarImagen("gameOver.jpg"), entorno.ancho() / 2, entorno.alto() / 2,
					0);
			entorno.cambiarFont("sans", 24, Color.RED);
			entorno.escribirTexto("points: " + points, entorno.ancho() / 2 - 150, entorno.alto() / 2 + 150);
			entorno.escribirTexto("kills: " + kills, entorno.ancho() - 220, entorno.alto() / 2 + 150);
			
		}
		
	if (gano) {
			entorno.dibujarImagen(Herramientas.cargarImagen("win.jpg"), entorno.ancho() / 2, entorno.alto() / 2, 0);
			entorno.cambiarFont("sans", 24, Color.GREEN);
			entorno.escribirTexto("points: " + (points + 100), entorno.ancho() / 2 - 150, entorno.alto() / 2 + 150);
			entorno.escribirTexto("kills: " + kills, entorno.ancho() - 220, entorno.alto() / 2 + 150);
			
	}

}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Juego juego = new Juego();
	}
}
