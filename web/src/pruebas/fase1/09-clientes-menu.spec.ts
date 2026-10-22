import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { UsuariosService } from '../../app/services/usuarios.service';
import { Clientes } from '../../app/pages/clientes/clientes';
import { App } from '../../app/app';
import { API, CLAVE_TOKEN, RespuestaSimulada, configurar, http, refrescar, renderizar, sesionComo, texto, todos, uno } from '../utilidades';

const HERNAN = { id: 1, nombre: 'Hernán Rojas', email: 'admin@llavemaestra.co', rol: 'ADMIN', activo: true, creadoEn: '2026-10-01T08:00:00' };
const PEDRO = { id: 2, nombre: 'Pedro Salazar', email: 'pedro@correo.com', rol: 'CLIENTE', activo: true, creadoEn: '2026-10-01T08:00:00' };
const JORGE = { id: 4, nombre: 'Jorge Pinzón', email: 'jorge@correo.com', rol: 'CLIENTE', activo: false, creadoEn: '2026-10-01T08:00:00' };
const CUENTAS: RespuestaSimulada[] = [{ metodo: 'GET', url: '/api/usuarios', cuerpo: [HERNAN, PEDRO, JORGE] }];

describe('1.9 · Clientes y menú', () => {
  beforeEach(() => {
    localStorage.clear();
    configurar();
  });

  it('UsuariosService.cargar() trae las cuentas de GET /api/usuarios', () => {
    sesionComo('ADMIN');
    const servicio = TestBed.inject(UsuariosService);
    servicio.cargar();
    http().expectOne(`${API}/usuarios`, 'cargar() debería hacer GET a http://localhost:8080/api/usuarios').flush([HERNAN, PEDRO]);
    expect(servicio.usuarios().length, 'usuarios() debería tener las cuentas que respondió el backend').toBe(2);
  });

  it('la página Clientes pinta una fila por cuenta', async () => {
    sesionComo('ADMIN');
    const fixture = await renderizar(Clientes, {}, CUENTAS);
    expect(todos(fixture, '[data-testid="cliente"]').length, 'Debería haber un data-testid="cliente" por cuenta (3)').toBe(3);
  });

  it('bloquear a Pedro envía PATCH /api/usuarios/2/estado con { activo: false }', async () => {
    sesionComo('ADMIN');
    const fixture = await renderizar(Clientes, {}, CUENTAS);
    const filaPedro = todos(fixture, '[data-testid="cliente"]').find((f) => texto(f).includes('Pedro Salazar'));
    const boton = filaPedro?.querySelector<HTMLElement>('[data-testid="boton-estado"]');
    expect(boton, 'La fila de Pedro debería tener un data-testid="boton-estado"').toBeTruthy();
    boton!.click();
    const peticion = http().expectOne(`${API}/usuarios/2/estado`);
    expect(peticion.request.method).toBe('PATCH');
    expect(peticion.request.body, 'Bloquear a una cuenta activa envía { activo: false }').toEqual({ activo: false });
  });

  it('el menú del cliente no muestra Clientes', async () => {
    sesionComo('CLIENTE');
    const fixture = await renderizar(App);
    expect(uno(fixture, '[data-testid="menu-herramientas"]'), 'Pedro debería ver data-testid="menu-herramientas"').not.toBeNull();
    expect(uno(fixture, '[data-testid="menu-clientes"]'), 'Pedro NO debería ver data-testid="menu-clientes"').toBeNull();
  });

  it('el menú del administrador muestra Clientes y su nombre', async () => {
    sesionComo('ADMIN');
    const fixture = await renderizar(App);
    expect(uno(fixture, '[data-testid="menu-clientes"]'), 'Hernán debería ver data-testid="menu-clientes"').not.toBeNull();
    expect(texto(uno(fixture, '[data-testid="nombre-usuario"]')), 'Debería mostrar el nombre del token en data-testid="nombre-usuario" (decodifica el payload como UTF-8)').toBe('Hernán Rojas');
  });

  it('el botón cerrar sesión borra la sesión', async () => {
    sesionComo('CLIENTE');
    const fixture = await renderizar(App);
    vi.spyOn(TestBed.inject(Router), 'navigateByUrl').mockResolvedValue(true);
    const boton = uno(fixture, '[data-testid="cerrar-sesion"]');
    expect(boton, 'Debería haber un botón data-testid="cerrar-sesion"').not.toBeNull();
    boton!.click();
    await refrescar(fixture);
    expect(localStorage.getItem(CLAVE_TOKEN), 'Al cerrar sesión se borra "llave_token"').toBeNull();
    expect(uno(fixture, '[data-testid="menu-herramientas"]'), 'Al cerrar sesión el menú desaparece').toBeNull();
  });
});
