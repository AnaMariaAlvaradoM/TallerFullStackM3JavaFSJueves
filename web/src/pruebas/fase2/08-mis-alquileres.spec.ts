import { MisAlquileres } from '../../app/pages/mis-alquileres/mis-alquileres';
import { Herramientas } from '../../app/pages/herramientas/herramientas';
import { API, RespuestaSimulada, configurar, fechaEnDias, http, refrescar, renderizar, sesionComo, texto, todos, uno } from '../utilidades';

function alquiler(id: number, estado: string, inicio: number, fin: number) {
  return {
    id, herramientaId: 1, herramientaNombre: `Herramienta ${id}`, clienteId: 2, clienteNombre: 'Pedro Salazar',
    fechaInicio: fechaEnDias(inicio), fechaFin: fechaEnDias(fin), dias: fin - inicio + 1, valorTotal: 25000 * (fin - inicio + 1),
    deposito: 100000, estado, creadoEn: '2026-11-01T10:00:00',
  };
}

const MIOS = [alquiler(1, 'RESERVADO', 5, 7), alquiler(2, 'RESERVADO', 0, 2), alquiler(3, 'ENTREGADO', -2, 1), alquiler(4, 'CANCELADO', 8, 9)];
const RESPUESTA: RespuestaSimulada[] = [{ metodo: 'GET', url: '/api/alquileres/mios', cuerpo: MIOS }];

const TALADRO = { id: 1, nombre: 'Taladro percutor', categoria: 'ELECTRICA', descripcion: '', precioDia: 25000, deposito: 100000, unidades: 3, imagenUrl: null, activa: true };
const NIVEL = { id: 6, nombre: 'Nivel láser', categoria: 'MEDICION', descripcion: '', precioDia: 30000, deposito: 200000, unidades: 1, imagenUrl: null, activa: false };

describe('2.8 · Mis alquileres y botón Alquilar', () => {
  beforeEach(() => {
    localStorage.clear();
    configurar();
  });

  it('pinta un data-testid="alquiler" por cada alquiler, con su estado', async () => {
    sesionComo('CLIENTE');
    const fixture = await renderizar(MisAlquileres, {}, RESPUESTA);
    const filas = todos(fixture, '[data-testid="alquiler"]');
    expect(filas.length, 'Debería haber 4 data-testid="alquiler" (lo que respondió GET /api/alquileres/mios)').toBe(4);
    expect(texto(filas[2].querySelector('[data-testid="estado-alquiler"]')).toLowerCase(), 'Cada alquiler muestra su estado en data-testid="estado-alquiler"').toContain('entregado');
  });

  it('solo se puede cancelar lo RESERVADO que todavía no empieza', async () => {
    sesionComo('CLIENTE');
    const fixture = await renderizar(MisAlquileres, {}, RESPUESTA);
    const conBoton = todos(fixture, '[data-testid="alquiler"]').filter((f) => f.querySelector('[data-testid="boton-cancelar"]'));
    expect(conBoton.length, 'Solo el alquiler 1 (RESERVADO, empieza en 5 días) debería tener boton-cancelar. El que empieza hoy, el entregado y el cancelado no').toBe(1);
    expect(texto(conBoton[0]), 'El botón debería estar en el alquiler que empieza en 5 días').toContain('Herramienta 1');
  });

  it('pide confirmación antes de cancelar', async () => {
    sesionComo('CLIENTE');
    const fixture = await renderizar(MisAlquileres, {}, RESPUESTA);
    uno(fixture, '[data-testid="boton-cancelar"]')!.click();
    await refrescar(fixture);
    expect(http().match((p) => p.url.endsWith('/cancelar')).length, 'El primer clic NO debería cancelar todavía').toBe(0);
    const confirmar = uno(fixture, '[data-testid="confirmar-cancelacion"]');
    expect(confirmar, 'Después del primer clic debería aparecer data-testid="confirmar-cancelacion" (sin usar window.confirm)').not.toBeNull();
    confirmar!.click();
    const peticion = http().expectOne(`${API}/alquileres/1/cancelar`, 'Al confirmar debería hacer PATCH /api/alquileres/1/cancelar');
    expect(peticion.request.method).toBe('PATCH');
  });

  it('sin alquileres muestra un mensaje', async () => {
    sesionComo('CLIENTE');
    const fixture = await renderizar(MisAlquileres, {}, [{ metodo: 'GET', url: '/api/alquileres/mios', cuerpo: [] }]);
    expect(uno(fixture, '[data-testid="sin-alquileres"]'), 'Con la lista vacía debería verse data-testid="sin-alquileres" (usa @empty)').not.toBeNull();
  });

  it('el cliente ve "Alquilar" en las herramientas activas y lleva a /herramientas/{id}/alquilar', async () => {
    sesionComo('CLIENTE');
    const fixture = await renderizar(Herramientas, {}, [{ metodo: 'GET', url: '/api/herramientas', cuerpo: [TALADRO, NIVEL] }]);
    const enlaces = todos(fixture, '[data-testid="boton-alquilar"]');
    expect(enlaces.length, 'Solo la herramienta activa debería tener data-testid="boton-alquilar"').toBe(1);
    expect(enlaces[0].getAttribute('href'), 'El enlace debería ir a /herramientas/1/alquilar').toBe('/herramientas/1/alquilar');
  });

  it('el administrador no ve "Alquilar"', async () => {
    sesionComo('ADMIN');
    const fixture = await renderizar(Herramientas, {}, [{ metodo: 'GET', url: '/api/herramientas', cuerpo: [TALADRO] }]);
    expect(todos(fixture, '[data-testid="boton-alquilar"]').length, 'Hernán no tiene ALQUILERES_CREAR: no debería ver boton-alquilar').toBe(0);
  });
});
