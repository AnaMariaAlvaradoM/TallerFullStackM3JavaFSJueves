import { TestBed } from '@angular/core/testing';
import { HerramientasService } from '../../app/services/herramientas.service';
import { Herramientas } from '../../app/pages/herramientas/herramientas';
import { API, RespuestaSimulada, configurar, http, renderizar, sesionComo, texto, todos, uno } from '../utilidades';

const TALADRO = { id: 1, nombre: 'Taladro percutor', categoria: 'ELECTRICA', descripcion: 'Taladro de 800 W', precioDia: 25000, deposito: 100000, unidades: 3, imagenUrl: null, activa: true };
const MEZCLADORA = { id: 3, nombre: 'Mezcladora de concreto', categoria: 'CONSTRUCCION', descripcion: 'Mezcladora', precioDia: 60000, deposito: 300000, unidades: 1, imagenUrl: null, activa: true };
const CATALOGO: RespuestaSimulada[] = [{ metodo: 'GET', url: '/api/herramientas', cuerpo: [TALADRO, MEZCLADORA] }];

describe('1.8 · Herramientas (servicio y página)', () => {
  beforeEach(() => {
    localStorage.clear();
    configurar();
  });

  it('HerramientasService.cargar() trae el catálogo de GET /api/herramientas', () => {
    sesionComo('CLIENTE');
    const servicio = TestBed.inject(HerramientasService);
    servicio.cargar();
    http().expectOne(`${API}/herramientas`, 'cargar() debería hacer GET a http://localhost:8080/api/herramientas').flush([TALADRO]);
    expect(servicio.herramientas().length, 'Después de cargar, herramientas() debería tener lo que respondió el backend').toBe(1);
  });

  it('HerramientasService.cambiarEstado() hace PATCH y actualiza la lista', () => {
    sesionComo('ADMIN');
    const servicio = TestBed.inject(HerramientasService);
    servicio.cargar();
    http().expectOne(`${API}/herramientas`).flush([TALADRO, MEZCLADORA]);
    servicio.cambiarEstado(TALADRO as never, false);
    const peticion = http().expectOne(`${API}/herramientas/1/estado`, 'cambiarEstado debería hacer PATCH a /api/herramientas/1/estado');
    expect(peticion.request.method).toBe('PATCH');
    expect(peticion.request.body, 'El cuerpo debería ser { activa: false }').toEqual({ activa: false });
    peticion.flush({ ...TALADRO, activa: false });
    expect(servicio.herramientas().find((h) => h.id === 1)?.activa, 'La lista debería actualizarse con la respuesta (activa: false)').toBe(false);
  });

  it('la página pinta una tarjeta por herramienta con su nombre y precio', async () => {
    sesionComo('CLIENTE');
    const fixture = await renderizar(Herramientas, {}, CATALOGO);
    const tarjetas = todos(fixture, '[data-testid="herramienta"]');
    expect(tarjetas.length, 'Debería haber un elemento data-testid="herramienta" por cada herramienta (2)').toBe(2);
    expect(texto(uno(fixture, '[data-testid="nombre-herramienta"]')), 'Debería mostrar el nombre en data-testid="nombre-herramienta"').toBe('Taladro percutor');
    expect(texto(uno(fixture, '[data-testid="precio-dia"]')), 'El precio por día debería verse con formato de pesos (25.000)').toContain('25.000');
  });

  it('el cliente no ve el botón de retirar', async () => {
    sesionComo('CLIENTE');
    const fixture = await renderizar(Herramientas, {}, CATALOGO);
    expect(todos(fixture, '[data-testid="boton-estado"]').length, 'Pedro no tiene HERRAMIENTAS_GESTIONAR: no debería ver data-testid="boton-estado" (usa *appSiPuede)').toBe(0);
  });

  it('el administrador retira una herramienta desde la página', async () => {
    sesionComo('ADMIN');
    const fixture = await renderizar(Herramientas, {}, CATALOGO);
    const botones = todos(fixture, '[data-testid="boton-estado"]');
    expect(botones.length, 'Hernán debería ver un data-testid="boton-estado" por herramienta').toBe(2);
    botones[0].click();
    const peticion = http().expectOne(`${API}/herramientas/1/estado`, 'El botón debería llamar a PATCH /api/herramientas/1/estado');
    expect(peticion.request.body, 'Retirar una herramienta activa envía { activa: false }').toEqual({ activa: false });
  });
});
