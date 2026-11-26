import { validarCuenta } from '../../app/core/validaciones';
import { Registro } from '../../app/pages/registro/registro';
import { Login } from '../../app/pages/login/login';
import { API, configurar, enviar, escribir, http, refrescar, renderizar, sesionComo, texto, todos, uno } from '../utilidades';

const BIEN = { nombre: 'Camilo Torres', email: 'camilo@correo.com', password: 'obra2026segura' };

async function llenar(confirmacion: string) {
  sesionComo(null);
  const fixture = await renderizar(Registro);
  escribir(fixture, 'nombre', BIEN.nombre);
  escribir(fixture, 'email', BIEN.email);
  escribir(fixture, 'password', BIEN.password);
  escribir(fixture, 'confirmacion', confirmacion);
  return fixture;
}

describe('3.8 · Registro', () => {
  beforeEach(() => {
    localStorage.clear();
    configurar();
  });

  it('validarCuenta acepta una cuenta bien escrita', () => {
    expect(validarCuenta(BIEN, BIEN.password), 'Con nombre, email válido, 8+ caracteres y la misma confirmación debería devolver []').toEqual([]);
  });

  it('validarCuenta encuentra cada error', () => {
    expect(validarCuenta({ ...BIEN, nombre: '' }, BIEN.password).length, 'Sin nombre debería haber 1 error').toBe(1);
    expect(validarCuenta({ ...BIEN, email: 'camilo-correo.com' }, BIEN.password).length, 'Con un email sin @ debería haber 1 error').toBe(1);
    expect(validarCuenta({ ...BIEN, password: 'corta' }, 'corta').length, 'Con menos de 8 caracteres debería haber 1 error').toBe(1);
    expect(validarCuenta(BIEN, 'otra-clave-distinta').length, 'Si la confirmación no coincide debería haber 1 error').toBe(1);
  });

  it('si las contraseñas no coinciden, avisa y no envía nada', async () => {
    const fixture = await llenar('otra-clave-distinta');
    enviar(fixture);
    await refrescar(fixture);
    expect(todos(fixture, '[data-testid="errores-registro"] li').length, 'Debería listar el error en <li> dentro de data-testid="errores-registro"').toBeGreaterThan(0);
    expect(http().match((p) => p.url === `${API}/auth/registro`).length, 'Con errores NO debería enviarse el POST (usa validarCuenta antes)').toBe(0);
  });

  it('con datos válidos envía solo nombre, email y contraseña', async () => {
    const fixture = await llenar(BIEN.password);
    enviar(fixture);
    await refrescar(fixture);
    const peticion = http().expectOne(`${API}/auth/registro`, 'Con datos válidos debería hacer POST /api/auth/registro');
    expect(peticion.request.body, 'El cuerpo es { nombre, email, password }: sin confirmacion y sin rol').toEqual(BIEN);
  });

  it('si el backend rechaza el email, muestra su mensaje', async () => {
    const fixture = await llenar(BIEN.password);
    enviar(fixture);
    await refrescar(fixture);
    http().expectOne(`${API}/auth/registro`).flush(
      { codigo: 409, mensaje: 'Ya existe una cuenta con el email camilo@correo.com' },
      { status: 409, statusText: 'Conflict' },
    );
    await refrescar(fixture);
    expect(texto(uno(fixture, '[data-testid="error-registro"]')), 'data-testid="error-registro" debería mostrar el mensaje del backend')
      .toBe('Ya existe una cuenta con el email camilo@correo.com');
  });

  it('el login tiene un enlace para crear cuenta', async () => {
    sesionComo(null);
    const fixture = await renderizar(Login);
    const enlace = uno(fixture, '[data-testid="enlace-registro"]');
    expect(enlace, 'El login debería tener data-testid="enlace-registro"').not.toBeNull();
    expect(enlace!.getAttribute('href'), 'El enlace debería ir a /registro').toBe('/registro');
  });
});
