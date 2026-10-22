import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, CanActivateFn, provideRouter, Route, RouterStateSnapshot, UrlTree } from '@angular/router';
import { jwtInterceptor } from '../../app/core/jwt-interceptor';
import { authGuard } from '../../app/core/auth-guard';
import { permisoGuard } from '../../app/core/permiso-guard';
import { routes } from '../../app/app.routes';
import { API, RolPrueba, sesionComo } from '../utilidades';

function configurarConInterceptor() {
  TestBed.configureTestingModule({
    providers: [provideRouter([]), provideHttpClient(withInterceptors([jwtInterceptor])), provideHttpClientTesting()],
  });
}

function ejecutar(guard: CanActivateFn): unknown {
  return TestBed.runInInjectionContext(() => guard({} as ActivatedRouteSnapshot, { url: '/' } as RouterStateSnapshot));
}

function destino(resultado: unknown): string {
  return resultado instanceof UrlTree ? resultado.toString() : String(resultado);
}

// Corre TODOS los guards de una ruta como lo haría el router
function resultadoDeRuta(ruta: string, rol: RolPrueba): string {
  const encontrada = routes.find((r: Route) => r.path === ruta);
  expect(encontrada, `No encontré la ruta '${ruta}' en app.routes.ts`).toBeTruthy();
  for (const guard of (encontrada!.canActivate ?? []) as CanActivateFn[]) {
    const r = ejecutar(guard);
    if (r !== true) return destino(r);
  }
  return 'true';
}

describe('1.6 · Interceptor, guards y rutas', () => {
  beforeEach(() => localStorage.clear());

  it('el interceptor agrega Authorization: Bearer <token> a las peticiones al backend', () => {
    const token = sesionComo('CLIENTE');
    configurarConInterceptor();
    TestBed.inject(HttpClient).get(`${API}/herramientas`).subscribe();
    const peticion = TestBed.inject(HttpTestingController).expectOne(`${API}/herramientas`);
    expect(peticion.request.headers.get('Authorization'), 'El interceptor debería agregar el encabezado "Authorization: Bearer <token>"').toBe(`Bearer ${token}`);
  });

  it('sin sesión el interceptor no inventa un encabezado', () => {
    sesionComo(null);
    configurarConInterceptor();
    TestBed.inject(HttpClient).get(`${API}/herramientas`).subscribe();
    const peticion = TestBed.inject(HttpTestingController).expectOne(`${API}/herramientas`);
    expect(peticion.request.headers.has('Authorization'), 'Sin token, no debería haber encabezado Authorization').toBe(false);
  });

  it('authGuard deja pasar con sesión y manda a /login sin sesión', () => {
    sesionComo(null);
    configurarConInterceptor();
    expect(destino(ejecutar(authGuard)), 'Sin sesión, authGuard debería devolver un UrlTree a /login').toBe('/login');
    TestBed.resetTestingModule();
    sesionComo('CLIENTE');
    configurarConInterceptor();
    expect(ejecutar(authGuard), 'Con sesión, authGuard debería devolver true').toBe(true);
  });

  it('permisoGuard(...) manda a /sin-acceso a quien no tiene el permiso', () => {
    sesionComo('CLIENTE');
    configurarConInterceptor();
    expect(destino(ejecutar(permisoGuard('CLIENTES_GESTIONAR'))), "permisoGuard('CLIENTES_GESTIONAR') con Pedro debería devolver un UrlTree a /sin-acceso").toBe('/sin-acceso');
    expect(ejecutar(permisoGuard('HERRAMIENTAS_VER')), "permisoGuard('HERRAMIENTAS_VER') con Pedro debería devolver true").toBe(true);
  });

  it('la ruta clientes está protegida: Pedro va a /sin-acceso y Hernán entra', () => {
    sesionComo('CLIENTE');
    configurarConInterceptor();
    expect(resultadoDeRuta('clientes', 'CLIENTE'), "Pedro escribiendo /clientes a mano debería terminar en /sin-acceso (revisa canActivate de 'clientes')").toBe('/sin-acceso');
    TestBed.resetTestingModule();
    sesionComo('ADMIN');
    configurarConInterceptor();
    expect(resultadoDeRuta('clientes', 'ADMIN'), 'Hernán debería poder entrar a /clientes').toBe('true');
  });

  it('la ruta herramientas pide sesión', () => {
    sesionComo(null);
    configurarConInterceptor();
    expect(resultadoDeRuta('herramientas', 'CLIENTE'), "Sin sesión, /herramientas debería mandar a /login (revisa canActivate de 'herramientas')").toBe('/login');
    TestBed.resetTestingModule();
    sesionComo('CLIENTE');
    configurarConInterceptor();
    expect(resultadoDeRuta('herramientas', 'CLIENTE'), 'Pedro debería poder entrar a /herramientas').toBe('true');
  });
});
