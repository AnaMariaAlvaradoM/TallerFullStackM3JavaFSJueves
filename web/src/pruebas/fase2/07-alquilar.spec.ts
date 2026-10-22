import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { Alquilar } from '../../app/pages/alquilar/alquilar';
import { API, RespuestaSimulada, configurar, enviar, escribir, fechaEnDias, http, refrescar, renderizar, sesionComo, texto, todos, uno } from '../utilidades';

const TALADRO = { id: 1, nombre: 'Taladro percutor', categoria: 'ELECTRICA', descripcion: 'Taladro de 800 W', precioDia: 25000, deposito: 100000, unidades: 3, imagenUrl: null, activa: true };
const FICHA: RespuestaSimulada[] = [{ metodo: 'GET', url: '/api/herramientas/1', cuerpo: TALADRO }];

async function pagina() {
  sesionComo('CLIENTE');
  return renderizar(Alquilar, { id: '1' }, FICHA);
}

function postsDeAlquiler() {
  return http().match((p) => p.method === 'POST' && p.url === `${API}/alquileres`);
}

describe('2.7 · Página Alquilar', () => {
  beforeEach(() => {
    localStorage.clear();
    configurar();
  });

  it('carga la herramienta de la URL con GET /api/herramientas/{id}', async () => {
    const fixture = await pagina();
    expect(texto(uno(fixture, '[data-testid="nombre-herramienta"]')), 'Con id = "1" debería pedir GET /api/herramientas/1 y mostrar su nombre').toBe('Taladro percutor');
  });

  it('muestra los días y el valor antes de enviar', async () => {
    const fixture = await pagina();
    escribir(fixture, 'fechaInicio', fechaEnDias(10));
    escribir(fixture, 'fechaFin', fechaEnDias(12));
    await refrescar(fixture);
    const resumen = texto(uno(fixture, '[data-testid="resumen-valor"]'));
    expect(resumen, 'data-testid="resumen-valor" debería mostrar los 3 días').toMatch(/3\s*d[ií]as/);
    expect(resumen, 'data-testid="resumen-valor" debería mostrar el valor (3 x 25.000 = 75.000)').toContain('75.000');
  });

  it('con las fechas vacías muestra los errores y no envía nada', async () => {
    const fixture = await pagina();
    enviar(fixture);
    await refrescar(fixture);
    const errores = todos(fixture, '[data-testid="errores-formulario"] li');
    expect(errores.length, 'Debería listar los errores en <li> dentro de data-testid="errores-formulario"').toBeGreaterThan(0);
    expect(postsDeAlquiler().length, 'Con errores NO debería enviarse el POST (usa validarAlquiler antes de llamar al servicio)').toBe(0);
  });

  it('con más de 15 días avisa y no envía', async () => {
    const fixture = await pagina();
    escribir(fixture, 'fechaInicio', fechaEnDias(10));
    escribir(fixture, 'fechaFin', fechaEnDias(25));
    enviar(fixture);
    await refrescar(fixture);
    expect(texto(uno(fixture, '[data-testid="errores-formulario"]')), 'Debería avisar el máximo de 15 días').toContain('15');
    expect(postsDeAlquiler().length, 'Con 16 días NO debería enviarse el POST').toBe(0);
  });

  it('con fechas válidas envía el alquiler y va a Mis alquileres', async () => {
    const fixture = await pagina();
    const router = TestBed.inject(Router);
    const porUrl = vi.spyOn(router, 'navigateByUrl').mockResolvedValue(true);
    const porRuta = vi.spyOn(router, 'navigate').mockResolvedValue(true);
    escribir(fixture, 'fechaInicio', fechaEnDias(10));
    escribir(fixture, 'fechaFin', fechaEnDias(12));
    enviar(fixture);
    await refrescar(fixture);
    const peticion = http().expectOne(`${API}/alquileres`, 'Con fechas válidas debería hacer POST /api/alquileres');
    expect(peticion.request.body, 'El cuerpo debería ser { herramientaId: 1, fechaInicio, fechaFin } (herramientaId como número)')
      .toEqual({ herramientaId: 1, fechaInicio: fechaEnDias(10), fechaFin: fechaEnDias(12) });
    peticion.flush({ id: 9, estado: 'RESERVADO' }, { status: 201, statusText: 'Created' });
    await refrescar(fixture);
    const destinos = [...porUrl.mock.calls, ...porRuta.mock.calls].map((llamada) => JSON.stringify(llamada[0]));
    expect(destinos.some((d) => d.includes('mis-alquileres')), 'Después de alquilar debería navegar a /mis-alquileres').toBe(true);
  });

  it('si el backend rechaza, muestra su mensaje', async () => {
    const fixture = await pagina();
    escribir(fixture, 'fechaInicio', fechaEnDias(10));
    escribir(fixture, 'fechaFin', fechaEnDias(12));
    enviar(fixture);
    await refrescar(fixture);
    http().expectOne(`${API}/alquileres`).flush(
      { codigo: 409, mensaje: 'No quedan unidades de Taladro percutor para esas fechas' },
      { status: 409, statusText: 'Conflict' },
    );
    await refrescar(fixture);
    expect(texto(uno(fixture, '[data-testid="error-alquiler"]')), 'data-testid="error-alquiler" debería mostrar el mensaje del backend')
      .toBe('No quedan unidades de Taladro percutor para esas fechas');
  });
});
