import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { HerramientaForm } from '../../app/pages/herramienta-form/herramienta-form';
import { Herramientas } from '../../app/pages/herramientas/herramientas';
import { API, configurar, enviar, escribir, http, refrescar, renderizar, sesionComo, texto, todos, uno } from '../utilidades';

const TALADRO = { id: 3, nombre: 'Taladro percutor', categoria: 'ELECTRICA', descripcion: 'Taladro de 800 W', precioDia: 25000, deposito: 100000, unidades: 3, imagenUrl: null, activa: true };

async function nueva() {
  sesionComo('ADMIN');
  const fixture = await renderizar(HerramientaForm);
  escribir(fixture, 'nombre', 'Compresor 50 L');
  const categoria = uno(fixture, '[name="categoria"]') as HTMLSelectElement | null;
  if (!categoria) throw new Error('No encontré un <select name="categoria">');
  categoria.value = 'ELECTRICA';
  escribir(fixture, 'precioDia', '20000');
  escribir(fixture, 'deposito', '120000');
  escribir(fixture, 'unidades', '2');
  return fixture;
}

describe('3.9 · Formulario de herramientas', () => {
  beforeEach(() => {
    localStorage.clear();
    configurar();
  });

  it('sin id, guardar hace POST /api/herramientas con números', async () => {
    const fixture = await nueva();
    const router = TestBed.inject(Router);
    const porUrl = vi.spyOn(router, 'navigateByUrl').mockResolvedValue(true);
    const porRuta = vi.spyOn(router, 'navigate').mockResolvedValue(true);
    enviar(fixture);
    await refrescar(fixture);
    const peticion = http().expectOne(`${API}/herramientas`, 'Sin id debería crear con POST /api/herramientas');
    expect(peticion.request.method).toBe('POST');
    const cuerpo = peticion.request.body;
    expect(cuerpo.nombre, 'El cuerpo debería traer el nombre').toBe('Compresor 50 L');
    expect(cuerpo.categoria, 'El cuerpo debería traer la categoría del <select name="categoria">').toBe('ELECTRICA');
    expect([cuerpo.precioDia, cuerpo.deposito, cuerpo.unidades], 'precioDia, deposito y unidades deberían viajar como NÚMEROS (usa Number(...))').toEqual([20000, 120000, 2]);
    peticion.flush({ ...TALADRO, id: 9 }, { status: 201, statusText: 'Created' });
    await refrescar(fixture);
    const destinos = [...porUrl.mock.calls, ...porRuta.mock.calls].map((llamada) => JSON.stringify(llamada[0]));
    expect(destinos.some((d) => d.includes('herramientas')), 'Después de guardar debería volver a /herramientas').toBe(true);
  });

  it('con id, carga la herramienta y llena el formulario', async () => {
    sesionComo('ADMIN');
    const fixture = await renderizar(HerramientaForm, { id: '3' }, [{ metodo: 'GET', url: '/api/herramientas/3', cuerpo: TALADRO }]);
    const nombre = uno(fixture, '[name="nombre"]') as HTMLInputElement | null;
    expect(nombre?.value, 'Con id = "3" debería pedir GET /api/herramientas/3 y poner su nombre en el campo name="nombre"').toBe('Taladro percutor');
    expect((uno(fixture, '[name="precioDia"]') as HTMLInputElement | null)?.value, 'El campo precioDia debería traer 25000').toBe('25000');
  });

  it('con id, guardar hace PUT /api/herramientas/{id}', async () => {
    sesionComo('ADMIN');
    const fixture = await renderizar(HerramientaForm, { id: '3' }, [{ metodo: 'GET', url: '/api/herramientas/3', cuerpo: TALADRO }]);
    escribir(fixture, 'precioDia', '27000');
    enviar(fixture);
    await refrescar(fixture);
    const peticion = http().expectOne(`${API}/herramientas/3`, 'Con id debería actualizar con PUT /api/herramientas/3');
    expect(peticion.request.method).toBe('PUT');
    expect(peticion.request.body.precioDia, 'El PUT debería llevar el precio nuevo como número').toBe(27000);
  });

  it('si el backend rechaza, muestra su mensaje', async () => {
    const fixture = await nueva();
    enviar(fixture);
    await refrescar(fixture);
    http().expectOne(`${API}/herramientas`).flush(
      { codigo: 409, mensaje: 'Ya existe un registro con esos datos' },
      { status: 409, statusText: 'Conflict' },
    );
    await refrescar(fixture);
    expect(texto(uno(fixture, '[data-testid="error-herramienta"]')), 'data-testid="error-herramienta" debería mostrar el mensaje del backend')
      .toBe('Ya existe un registro con esos datos');
  });

  it('Hernán ve "Nueva herramienta" y "Editar"; Pedro no', async () => {
    sesionComo('ADMIN');
    const admin = await renderizar(Herramientas, {}, [{ metodo: 'GET', url: '/api/herramientas', cuerpo: [TALADRO] }]);
    expect(uno(admin, '[data-testid="boton-nueva-herramienta"]')?.getAttribute('href'), 'Hernán debería ver data-testid="boton-nueva-herramienta" hacia /herramientas/nueva').toBe('/herramientas/nueva');
    expect(uno(admin, '[data-testid="boton-editar"]')?.getAttribute('href'), 'Cada herramienta debería tener data-testid="boton-editar" hacia /herramientas/{id}/editar').toBe('/herramientas/3/editar');
    TestBed.resetTestingModule();
    configurar();
    sesionComo('CLIENTE');
    const pedro = await renderizar(Herramientas, {}, [{ metodo: 'GET', url: '/api/herramientas', cuerpo: [TALADRO] }]);
    expect(todos(pedro, '[data-testid="boton-nueva-herramienta"], [data-testid="boton-editar"]').length, 'Pedro no tiene HERRAMIENTAS_GESTIONAR: no debería ver Nueva ni Editar').toBe(0);
  });
});
