import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthService } from '../../app/core/auth.service';
import { API, CLAVE_TOKEN, configurar, http, sesionComo, tokenDePrueba } from '../utilidades';

function servicio(): AuthService {
  return TestBed.inject(AuthService);
}

describe('1.5 · Sesión (AuthService)', () => {
  beforeEach(() => {
    localStorage.clear();
    configurar();
  });

  it('sin token no hay sesión ni permisos', () => {
    sesionComo(null);
    const auth = servicio();
    expect(auth.sesionActiva(), 'Sin token en localStorage, sesionActiva() debería ser false').toBe(false);
    expect(auth.puede('HERRAMIENTAS_VER'), 'Sin sesión, puede(...) debería ser false').toBe(false);
  });

  it('lee el nombre y el rol del token guardado en "llave_token"', () => {
    sesionComo('CLIENTE');
    const auth = servicio();
    expect(auth.sesionActiva(), 'Con un token vigente en localStorage["llave_token"], sesionActiva() debería ser true').toBe(true);
    expect(auth.nombre(), 'nombre() debería salir del claim "nombre" del token').toBe('Pedro Salazar');
    expect(auth.rol(), 'rol() debería salir del claim "rol" del token').toBe('CLIENTE');
  });

  it('puede(...) responde según los permisos del token', () => {
    sesionComo('CLIENTE');
    const auth = servicio();
    expect(auth.puede('ALQUILERES_CREAR'), 'Pedro (CLIENTE) tiene ALQUILERES_CREAR').toBe(true);
    expect(auth.puede('CLIENTES_GESTIONAR'), 'Pedro (CLIENTE) NO tiene CLIENTES_GESTIONAR').toBe(false);
  });

  it('un token vencido no cuenta como sesión', () => {
    sesionComo('ADMIN', -5);
    const auth = servicio();
    expect(auth.sesionActiva(), 'Si exp ya pasó, sesionActiva() debería ser false (compara exp * 1000 con Date.now())').toBe(false);
    expect(auth.puede('HERRAMIENTAS_VER'), 'Con el token vencido, puede(...) debería ser false').toBe(false);
  });

  it('iniciarSesion envía email y contraseña a /api/auth/login y guarda el token', () => {
    sesionComo(null);
    const auth = servicio();
    const router = TestBed.inject(Router);
    const porUrl = vi.spyOn(router, 'navigateByUrl').mockResolvedValue(true);
    const porRuta = vi.spyOn(router, 'navigate').mockResolvedValue(true);

    auth.iniciarSesion({ email: 'pedro@correo.com', password: 'llave123' });
    const peticion = http().expectOne(`${API}/auth/login`, 'Debería hacer POST a http://localhost:8080/api/auth/login');
    expect(peticion.request.method, 'El login es un POST').toBe('POST');
    expect(peticion.request.body, 'El cuerpo debería ser { email, password }').toEqual({ email: 'pedro@correo.com', password: 'llave123' });

    const token = tokenDePrueba('CLIENTE');
    peticion.flush({ token });
    expect(localStorage.getItem(CLAVE_TOKEN), 'El token debería guardarse en localStorage con la clave "llave_token"').toBe(token);
    expect(auth.sesionActiva(), 'Después del login, sesionActiva() debería ser true').toBe(true);
    const destinos = [...porUrl.mock.calls, ...porRuta.mock.calls].map((llamada) => JSON.stringify(llamada[0]));
    expect(destinos.some((d) => d.includes('herramientas')), 'Después del login debería navegar a /herramientas').toBe(true);
  });

  it('si el backend rechaza el login, muestra su mensaje y no guarda nada', () => {
    sesionComo(null);
    const auth = servicio();
    auth.iniciarSesion({ email: 'pedro@correo.com', password: 'mala' });
    http().expectOne(`${API}/auth/login`).flush(
      { codigo: 401, mensaje: 'Email o contraseña incorrectos' },
      { status: 401, statusText: 'Unauthorized' },
    );
    expect(auth.error(), 'error() debería traer el mensaje del backend').toBe('Email o contraseña incorrectos');
    expect(localStorage.getItem(CLAVE_TOKEN), 'Si el login falla, no se guarda ningún token').toBeNull();
  });

  it('cerrarSesion borra el token y la sesión', () => {
    sesionComo('ADMIN');
    const auth = servicio();
    vi.spyOn(TestBed.inject(Router), 'navigateByUrl').mockResolvedValue(true);
    auth.cerrarSesion();
    expect(localStorage.getItem(CLAVE_TOKEN), 'cerrarSesion() debería borrar "llave_token" de localStorage').toBeNull();
    expect(auth.sesionActiva(), 'Después de cerrarSesion(), sesionActiva() debería ser false').toBe(false);
  });
});
