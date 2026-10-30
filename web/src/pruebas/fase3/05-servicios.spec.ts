import { TestBed } from '@angular/core/testing';
import { AuthService } from '../../app/core/auth.service';
import { AlquileresService } from '../../app/services/alquileres.service';
import { HerramientasService } from '../../app/services/herramientas.service';
import { ReportesService } from '../../app/services/reportes.service';
import { API, configurar, http, sesionComo, tokenDePrueba } from '../utilidades';

const ALQUILER = {
  id: 7, herramientaId: 1, herramientaNombre: 'Taladro percutor', clienteId: 2, clienteNombre: 'Pedro Salazar',
  fechaInicio: '2026-11-12', fechaFin: '2026-11-14', dias: 3, valorTotal: 75000, deposito: 100000,
  estado: 'RESERVADO', creadoEn: '2026-11-01T10:00:00',
};

const NUEVA = { nombre: 'Compresor', categoria: 'ELECTRICA', descripcion: null, precioDia: 20000, deposito: 120000, unidades: 2, imagenUrl: null };

describe('3.5 · Servicios de la Fase 3', () => {
  beforeEach(() => {
    localStorage.clear();
    configurar();
  });

  it('ReportesService.resumen() hace GET /api/reportes/resumen', () => {
    sesionComo('ADMIN');
    const servicio = TestBed.inject(ReportesService);
    let recibido: unknown = null;
    servicio.resumen().subscribe((r) => (recibido = r));
    http().expectOne(`${API}/reportes/resumen`, 'resumen() debería hacer GET a http://localhost:8080/api/reportes/resumen')
      .flush({ ingresosMes: 0, alquileresActivos: 0, vencidos: [], masAlquiladas: [] });
    expect(recibido, 'resumen() debería devolver un Observable con lo que respondió el backend').not.toBeNull();
  });

  it('AlquileresService.cargarTodos() trae GET /api/alquileres', () => {
    sesionComo('ADMIN');
    const servicio = TestBed.inject(AlquileresService);
    servicio.cargarTodos();
    http().expectOne(`${API}/alquileres`, 'cargarTodos() sin estado debería hacer GET a /api/alquileres (sin ?estado)').flush([ALQUILER]);
    expect(servicio.alquileres().length, 'alquileres() debería tener lo que respondió el backend').toBe(1);
  });

  it('cargarTodos("ENTREGADO") filtra con ?estado=ENTREGADO', () => {
    sesionComo('ADMIN');
    const servicio = TestBed.inject(AlquileresService);
    servicio.cargarTodos('ENTREGADO');
    const peticion = http().expectOne((p) => p.url.startsWith(`${API}/alquileres`) && p.method === 'GET',
      'cargarTodos("ENTREGADO") debería hacer GET a /api/alquileres');
    expect(peticion.request.urlWithParams, 'El filtro va en la URL: /api/alquileres?estado=ENTREGADO').toBe(`${API}/alquileres?estado=ENTREGADO`);
  });

  it('entregar() y devolver() hacen PATCH y actualizan la lista', () => {
    sesionComo('ADMIN');
    const servicio = TestBed.inject(AlquileresService);
    servicio.cargarTodos();
    http().expectOne(`${API}/alquileres`).flush([ALQUILER]);
    servicio.entregar(ALQUILER as never);
    const entrega = http().expectOne(`${API}/alquileres/7/entregar`, 'entregar() debería hacer PATCH a /api/alquileres/7/entregar');
    expect(entrega.request.method).toBe('PATCH');
    entrega.flush({ ...ALQUILER, estado: 'ENTREGADO' });
    expect(servicio.alquileres()[0].estado, 'Después de entregar, la lista debería quedar con el estado que respondió el backend').toBe('ENTREGADO');
    servicio.devolver({ ...ALQUILER, estado: 'ENTREGADO' } as never);
    const devolucion = http().expectOne(`${API}/alquileres/7/devolver`, 'devolver() debería hacer PATCH a /api/alquileres/7/devolver');
    expect(devolucion.request.method).toBe('PATCH');
  });

  it('si entregar falla, error() trae el mensaje del backend', () => {
    sesionComo('ADMIN');
    const servicio = TestBed.inject(AlquileresService);
    servicio.entregar(ALQUILER as never);
    http().expectOne(`${API}/alquileres/7/entregar`).flush(
      { codigo: 409, mensaje: 'Solo se entrega un alquiler RESERVADO (este está CANCELADO)' },
      { status: 409, statusText: 'Conflict' },
    );
    expect(servicio.error(), 'error() debería traer el mensaje del 409').toBe('Solo se entrega un alquiler RESERVADO (este está CANCELADO)');
  });

  it('HerramientasService.crear() hace POST y actualizar() hace PUT', () => {
    sesionComo('ADMIN');
    const servicio = TestBed.inject(HerramientasService);
    servicio.crear(NUEVA as never).subscribe();
    const crear = http().expectOne(`${API}/herramientas`, 'crear() debería hacer POST a /api/herramientas');
    expect(crear.request.method).toBe('POST');
    expect(crear.request.body, 'crear() envía los datos de la herramienta tal cual').toEqual(NUEVA);
    servicio.actualizar(3, NUEVA as never).subscribe();
    const actualizar = http().expectOne(`${API}/herramientas/3`, 'actualizar(3, …) debería hacer PUT a /api/herramientas/3');
    expect(actualizar.request.method).toBe('PUT');
  });

  it('AuthService.registrarse() hace POST /api/auth/registro y deja la sesión abierta', () => {
    sesionComo(null);
    const servicio = TestBed.inject(AuthService);
    servicio.registrarse({ nombre: 'Pedro Salazar', email: 'pedro@correo.com', password: 'llave1234' });
    const peticion = http().expectOne(`${API}/auth/registro`, 'registrarse() debería hacer POST a /api/auth/registro');
    expect(peticion.request.body, 'El registro envía solo { nombre, email, password }: nada de rol').toEqual({ nombre: 'Pedro Salazar', email: 'pedro@correo.com', password: 'llave1234' });
    peticion.flush({ token: tokenDePrueba('CLIENTE') });
    expect(servicio.sesionActiva(), 'Con el token del registro, la sesión debería quedar activa (igual que con el login)').toBe(true);
  });
});
