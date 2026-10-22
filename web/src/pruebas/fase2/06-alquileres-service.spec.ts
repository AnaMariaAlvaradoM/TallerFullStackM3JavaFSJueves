import { TestBed } from '@angular/core/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { mensajeDeError } from '../../app/core/errores';
import { AlquileresService } from '../../app/services/alquileres.service';
import { API, configurar, http, sesionComo } from '../utilidades';

const ALQUILER = {
  id: 7, herramientaId: 1, herramientaNombre: 'Taladro percutor', clienteId: 2, clienteNombre: 'Pedro Salazar',
  fechaInicio: '2026-11-12', fechaFin: '2026-11-14', dias: 3, valorTotal: 75000, deposito: 100000,
  estado: 'RESERVADO', creadoEn: '2026-11-01T10:00:00',
};

function error(status: number, cuerpo: object | null = null): HttpErrorResponse {
  return new HttpErrorResponse({ status, error: cuerpo, url: `${API}/alquileres` });
}

describe('2.6 · Mensajes de error y AlquileresService', () => {
  beforeEach(() => {
    localStorage.clear();
    configurar();
  });

  it('mensajeDeError usa el mensaje del backend en un 400 o un 409', () => {
    expect(mensajeDeError(error(409, { codigo: 409, mensaje: 'No quedan unidades de Mezcladora de concreto para esas fechas' })),
      'Con 409 y { mensaje }, debería devolver ese mensaje tal cual').toBe('No quedan unidades de Mezcladora de concreto para esas fechas');
    expect(mensajeDeError(error(400, { codigo: 400, mensaje: 'El alquiler máximo es de 15 días' })),
      'Con 400 y { mensaje }, debería devolver ese mensaje').toBe('El alquiler máximo es de 15 días');
  });

  it('mensajeDeError explica cuando no hay conexión o no hay permiso', () => {
    expect(mensajeDeError(error(0)).toLowerCase(), 'Con status 0 (backend apagado) el mensaje debería mencionar el servidor').toContain('servidor');
    expect(mensajeDeError(error(403)).toLowerCase(), 'Con 403 el mensaje debería mencionar el permiso').toContain('permiso');
  });

  it('mensajeDeError nunca devuelve un texto vacío', () => {
    expect(mensajeDeError(error(500)).trim().length, 'Con un 500 sin cuerpo debería devolver un mensaje genérico').toBeGreaterThan(0);
  });

  it('crear() hace POST /api/alquileres solo con herramienta y fechas', () => {
    sesionComo('CLIENTE');
    const servicio = TestBed.inject(AlquileresService);
    servicio.crear({ herramientaId: 1, fechaInicio: '2026-11-12', fechaFin: '2026-11-14' }).subscribe();
    const peticion = http().expectOne(`${API}/alquileres`, 'crear() debería hacer POST a http://localhost:8080/api/alquileres');
    expect(peticion.request.method).toBe('POST');
    expect(peticion.request.body, 'El cuerpo es { herramientaId, fechaInicio, fechaFin }: el valor lo calcula el backend')
      .toEqual({ herramientaId: 1, fechaInicio: '2026-11-12', fechaFin: '2026-11-14' });
  });

  it('cargarMios() trae GET /api/alquileres/mios', () => {
    sesionComo('CLIENTE');
    const servicio = TestBed.inject(AlquileresService);
    servicio.cargarMios();
    http().expectOne(`${API}/alquileres/mios`, 'cargarMios() debería hacer GET a /api/alquileres/mios').flush([ALQUILER]);
    expect(servicio.alquileres().length, 'alquileres() debería tener lo que respondió el backend').toBe(1);
  });

  it('cancelar() hace PATCH y actualiza el estado en la lista', () => {
    sesionComo('CLIENTE');
    const servicio = TestBed.inject(AlquileresService);
    servicio.cargarMios();
    http().expectOne(`${API}/alquileres/mios`).flush([ALQUILER]);
    servicio.cancelar(ALQUILER as never);
    const peticion = http().expectOne(`${API}/alquileres/7/cancelar`, 'cancelar() debería hacer PATCH a /api/alquileres/7/cancelar');
    expect(peticion.request.method).toBe('PATCH');
    peticion.flush({ ...ALQUILER, estado: 'CANCELADO' });
    expect(servicio.alquileres()[0].estado, 'La lista debería quedar con el estado que respondió el backend').toBe('CANCELADO');
  });

  it('si cancelar falla, error() trae el mensaje del backend', () => {
    sesionComo('CLIENTE');
    const servicio = TestBed.inject(AlquileresService);
    servicio.cancelar(ALQUILER as never);
    http().expectOne(`${API}/alquileres/7/cancelar`).flush(
      { codigo: 409, mensaje: 'Ya no se puede cancelar: el alquiler empieza hoy o ya empezó' },
      { status: 409, statusText: 'Conflict' },
    );
    expect(servicio.error(), 'error() debería traer el mensaje del 409').toBe('Ya no se puede cancelar: el alquiler empieza hoy o ya empezó');
  });
});
