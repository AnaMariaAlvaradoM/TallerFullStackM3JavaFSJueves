import { Alquileres } from '../../app/pages/alquileres/alquileres';
import { API, RespuestaSimulada, configurar, http, refrescar, renderizar, sesionComo, texto, todos, uno } from '../utilidades';

function alquiler(id: number, estado: string, cliente: string) {
  return {
    id, herramientaId: 1, herramientaNombre: `Herramienta ${id}`, clienteId: id, clienteNombre: cliente,
    fechaInicio: '2026-11-10', fechaFin: '2026-11-12', dias: 3, valorTotal: 75000, deposito: 100000,
    estado, creadoEn: '2026-11-01T10:00:00',
  };
}

const TODOS = [alquiler(1, 'RESERVADO', 'Pedro Salazar'), alquiler(2, 'ENTREGADO', 'Diana Mejía'), alquiler(3, 'DEVUELTO', 'Pedro Salazar'), alquiler(4, 'CANCELADO', 'Diana Mejía')];
const RESPUESTA: RespuestaSimulada[] = [{ metodo: 'GET', url: '/api/alquileres', cuerpo: TODOS }];

describe('3.7 · Alquileres (vista de Hernán)', () => {
  beforeEach(() => {
    localStorage.clear();
    configurar();
  });

  it('pinta un data-testid="alquiler" por alquiler, con el nombre del cliente', async () => {
    sesionComo('ADMIN');
    const fixture = await renderizar(Alquileres, {}, RESPUESTA);
    const filas = todos(fixture, '[data-testid="alquiler"]');
    expect(filas.length, 'Debería haber 4 data-testid="alquiler" (lo que respondió GET /api/alquileres)').toBe(4);
    expect(texto(filas[1]), 'Hernán ve de quién es cada alquiler: debería mostrar clienteNombre').toContain('Diana Mejía');
  });

  it('Entregar solo en lo RESERVADO y Recibir solo en lo ENTREGADO', async () => {
    sesionComo('ADMIN');
    const fixture = await renderizar(Alquileres, {}, RESPUESTA);
    const filas = todos(fixture, '[data-testid="alquiler"]');
    expect(todos(fixture, '[data-testid="boton-entregar"]').length, 'Solo el alquiler RESERVADO debería tener boton-entregar').toBe(1);
    expect(filas[0].querySelector('[data-testid="boton-entregar"]'), 'boton-entregar debería estar en el alquiler RESERVADO').not.toBeNull();
    expect(todos(fixture, '[data-testid="boton-devolver"]').length, 'Solo el alquiler ENTREGADO debería tener boton-devolver').toBe(1);
    expect(filas[1].querySelector('[data-testid="boton-devolver"]'), 'boton-devolver debería estar en el alquiler ENTREGADO').not.toBeNull();
  });

  it('Entregar hace PATCH /api/alquileres/{id}/entregar', async () => {
    sesionComo('ADMIN');
    const fixture = await renderizar(Alquileres, {}, RESPUESTA);
    uno(fixture, '[data-testid="boton-entregar"]')!.click();
    const peticion = http().expectOne(`${API}/alquileres/1/entregar`, 'Al hacer clic en Entregar debería hacer PATCH /api/alquileres/1/entregar');
    expect(peticion.request.method).toBe('PATCH');
  });

  it('el filtro de estado pide al backend ?estado=...', async () => {
    sesionComo('ADMIN');
    const fixture = await renderizar(Alquileres, {}, RESPUESTA);
    const filtro = uno(fixture, '[data-testid="filtro-estado"]') as HTMLSelectElement | null;
    expect(filtro, 'Debería haber un <select data-testid="filtro-estado">').not.toBeNull();
    filtro!.value = 'ENTREGADO';
    filtro!.dispatchEvent(new Event('change'));
    await refrescar(fixture);
    const pedidas = http().match((p) => p.method === 'GET' && p.urlWithParams === `${API}/alquileres?estado=ENTREGADO`);
    expect(pedidas.length, 'Al escoger ENTREGADO debería pedir GET /api/alquileres?estado=ENTREGADO (el backend filtra)').toBe(1);
  });

  it('sin alquileres muestra un mensaje', async () => {
    sesionComo('ADMIN');
    const fixture = await renderizar(Alquileres, {}, [{ metodo: 'GET', url: '/api/alquileres', cuerpo: [] }]);
    expect(uno(fixture, '[data-testid="sin-alquileres"]'), 'Con la lista vacía debería verse data-testid="sin-alquileres" (usa @empty)').not.toBeNull();
  });
});
