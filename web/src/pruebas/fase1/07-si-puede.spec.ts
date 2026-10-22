import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthService } from '../../app/core/auth.service';
import { SiPuede } from '../../app/shared/si-puede';
import { API, configurar, http, refrescar, renderizar, sesionComo, tokenDePrueba, uno } from '../utilidades';

@Component({
  imports: [SiPuede],
  template: `
    <button *appSiPuede="'CLIENTES_GESTIONAR'" data-testid="solo-admin">Bloquear</button>
    <button *appSiPuede="'ALQUILERES_CREAR'" data-testid="solo-cliente">Alquilar</button>
  `,
})
class Anfitrion {}

describe('1.7 · Directiva *appSiPuede', () => {
  beforeEach(() => {
    localStorage.clear();
    configurar();
  });

  it('el cliente ve lo que su permiso le da y no lo demás', async () => {
    sesionComo('CLIENTE');
    const fixture = await renderizar(Anfitrion);
    expect(uno(fixture, '[data-testid="solo-cliente"]'), 'Pedro tiene ALQUILERES_CREAR: el botón debería existir').not.toBeNull();
    expect(uno(fixture, '[data-testid="solo-admin"]'), 'Pedro NO tiene CLIENTES_GESTIONAR: el botón NO debería existir en el DOM').toBeNull();
  });

  it('el administrador ve lo suyo', async () => {
    sesionComo('ADMIN');
    const fixture = await renderizar(Anfitrion);
    expect(uno(fixture, '[data-testid="solo-admin"]'), 'Hernán tiene CLIENTES_GESTIONAR: el botón debería existir').not.toBeNull();
    expect(uno(fixture, '[data-testid="solo-cliente"]'), 'Hernán NO tiene ALQUILERES_CREAR: el botón NO debería existir').toBeNull();
  });

  it('sin sesión no dibuja nada', async () => {
    sesionComo(null);
    const fixture = await renderizar(Anfitrion);
    expect(uno(fixture, 'button'), 'Sin sesión no debería dibujarse ningún botón protegido').toBeNull();
  });

  it('reacciona cuando la sesión cambia (usa la señal de permisos)', async () => {
    sesionComo(null);
    const fixture = await renderizar(Anfitrion);
    vi.spyOn(TestBed.inject(Router), 'navigateByUrl').mockResolvedValue(true);
    TestBed.inject(AuthService).iniciarSesion({ email: 'admin@llavemaestra.co', password: 'llave123' });
    http().expectOne(`${API}/auth/login`).flush({ token: tokenDePrueba('ADMIN') });
    await refrescar(fixture);
    expect(uno(fixture, '[data-testid="solo-admin"]'), 'Después de iniciar sesión como Hernán, el botón debería aparecer sin recargar (usa effect con la señal)').not.toBeNull();
  });
});
