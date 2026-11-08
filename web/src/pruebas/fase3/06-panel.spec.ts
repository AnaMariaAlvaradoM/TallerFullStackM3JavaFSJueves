import { Panel } from '../../app/pages/panel/panel';
import { RespuestaSimulada, configurar, renderizar, sesionComo, texto, todos, uno } from '../utilidades';

const VENCIDO = {
  id: 3, herramientaId: 2, herramientaNombre: 'Pulidora angular', clienteId: 4, clienteNombre: 'Jorge Pinzón',
  fechaInicio: '2026-10-19', fechaFin: '2026-10-26', dias: 8, valorTotal: 176000, deposito: 80000,
  estado: 'ENTREGADO', creadoEn: '2026-10-19T09:00:00',
};

function resumen(vencidos: object[]): RespuestaSimulada[] {
  return [{
    metodo: 'GET', url: '/api/reportes/resumen', cuerpo: {
      ingresosMes: 1250000, alquileresActivos: 7, vencidos,
      masAlquiladas: [{ herramienta: 'Taladro percutor', alquileres: 9 }, { herramienta: 'Andamio tubular', alquileres: 4 }],
    },
  }];
}

describe('3.6 · Panel de Hernán', () => {
  beforeEach(() => {
    localStorage.clear();
    configurar();
  });

  it('muestra los ingresos del mes en pesos', async () => {
    sesionComo('ADMIN');
    const fixture = await renderizar(Panel, {}, resumen([VENCIDO]));
    expect(texto(uno(fixture, '[data-testid="ingresos-mes"]')), 'data-testid="ingresos-mes" debería mostrar 1.250.000 (usa pesos() de core/formato)').toContain('1.250.000');
  });

  it('muestra los alquileres activos', async () => {
    sesionComo('ADMIN');
    const fixture = await renderizar(Panel, {}, resumen([VENCIDO]));
    expect(texto(uno(fixture, '[data-testid="alquileres-activos"]')), 'data-testid="alquileres-activos" debería mostrar 7').toBe('7');
  });

  it('lista cada vencido con la herramienta y el cliente', async () => {
    sesionComo('ADMIN');
    const fixture = await renderizar(Panel, {}, resumen([VENCIDO]));
    const vencidos = todos(fixture, '[data-testid="vencido"]');
    expect(vencidos.length, 'Debería haber un data-testid="vencido" por cada alquiler vencido').toBe(1);
    expect(texto(vencidos[0]), 'El vencido debería mostrar la herramienta').toContain('Pulidora angular');
    expect(texto(vencidos[0]), 'El vencido debería mostrar el cliente').toContain('Jorge Pinzón');
  });

  it('sin vencidos muestra un mensaje', async () => {
    sesionComo('ADMIN');
    const fixture = await renderizar(Panel, {}, resumen([]));
    expect(uno(fixture, '[data-testid="sin-vencidos"]'), 'Sin vencidos debería verse data-testid="sin-vencidos" (usa @empty)').not.toBeNull();
  });

  it('muestra las más alquiladas en el orden del backend', async () => {
    sesionComo('ADMIN');
    const fixture = await renderizar(Panel, {}, resumen([]));
    const filas = todos(fixture, '[data-testid="mas-alquilada"]');
    expect(filas.length, 'Debería haber un data-testid="mas-alquilada" por cada herramienta del ranking').toBe(2);
    expect(texto(filas[0]), 'La primera fila es la más alquilada (el backend ya las ordena)').toContain('Taladro percutor');
  });
});
