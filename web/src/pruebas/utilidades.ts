// Ayudas compartidas por las pruebas del taller. No es un archivo de pruebas.
import { Type } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

export const API = 'http://localhost:8080/api';
export const CLAVE_TOKEN = 'llave_token';

export type RolPrueba = 'ADMIN' | 'CLIENTE';

const PERMISOS: Record<RolPrueba, string[]> = {
  ADMIN: ['ALQUILERES_GESTIONAR', 'ALQUILERES_VER_TODOS', 'CLIENTES_GESTIONAR', 'HERRAMIENTAS_GESTIONAR', 'HERRAMIENTAS_VER', 'REPORTES_VER'],
  CLIENTE: ['ALQUILERES_CANCELAR_PROPIOS', 'ALQUILERES_CREAR', 'ALQUILERES_VER_PROPIOS', 'HERRAMIENTAS_VER'],
};

const PERSONAS: Record<RolPrueba, { sub: string; nombre: string }> = {
  ADMIN: { sub: 'admin@llavemaestra.co', nombre: 'Hernán Rojas' },
  CLIENTE: { sub: 'pedro@correo.com', nombre: 'Pedro Salazar' },
};

function base64Url(objeto: object): string {
  const bytes = new TextEncoder().encode(JSON.stringify(objeto));
  let binario = '';
  bytes.forEach((b) => (binario += String.fromCharCode(b)));
  return btoa(binario).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
}

// Un JWT con el mismo payload que firma el backend (la firma no importa: Angular no la verifica)
export function tokenDePrueba(rol: RolPrueba, minutosDeVida = 60): string {
  const payload = {
    ...PERSONAS[rol],
    rol,
    permisos: PERMISOS[rol],
    iat: Math.floor(Date.now() / 1000),
    exp: Math.floor(Date.now() / 1000) + minutosDeVida * 60,
  };
  return `${base64Url({ alg: 'HS256', typ: 'JWT' })}.${base64Url(payload)}.firma-de-prueba`;
}

// Deja la sesión guardada ANTES de crear el servicio (así arranca la app con sesión)
export function sesionComo(rol: RolPrueba | null, minutosDeVida = 60): string | null {
  localStorage.clear();
  if (!rol) return null;
  const token = tokenDePrueba(rol, minutosDeVida);
  localStorage.setItem(CLAVE_TOKEN, token);
  return token;
}

export function configurar(): void {
  TestBed.configureTestingModule({
    providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
  });
}

export function http(): HttpTestingController {
  return TestBed.inject(HttpTestingController);
}

export function esperar(ms: number): Promise<void> {
  return new Promise((resolver) => setTimeout(resolver, ms));
}

export interface RespuestaSimulada {
  metodo: string;
  url: string;
  cuerpo: object | string | number | boolean | null;
  estado?: number;
}

// Responde las peticiones pendientes que coincidan (método y final de la URL)
export function responder(respuestas: RespuestaSimulada[]): void {
  for (const respuesta of respuestas) {
    const pendientes = http().match(
      (p) => p.method === respuesta.metodo && p.url.split('?')[0].endsWith(respuesta.url),
    );
    for (const peticion of pendientes) {
      const estado = respuesta.estado ?? 200;
      peticion.flush(respuesta.cuerpo, { status: estado, statusText: estado < 400 ? 'OK' : 'Error' });
    }
  }
}

// Crea el componente, le pasa sus inputs y responde con datos simulados lo que pida por HTTP
export async function renderizar<T>(
  componente: Type<T>,
  entradas: Record<string, unknown> = {},
  respuestas: RespuestaSimulada[] = [],
): Promise<ComponentFixture<T>> {
  const fixture = TestBed.createComponent(componente);
  for (const [nombre, valor] of Object.entries(entradas)) {
    fixture.componentRef.setInput(nombre, valor);
  }
  for (let ronda = 0; ronda < 3; ronda++) {
    fixture.detectChanges();
    await esperar(0);
    responder(respuestas);
  }
  await Promise.race([fixture.whenStable(), esperar(2000)]);
  fixture.detectChanges();
  return fixture;
}

export async function refrescar(fixture: ComponentFixture<unknown>): Promise<void> {
  fixture.detectChanges();
  await esperar(0);
  await Promise.race([fixture.whenStable(), esperar(1000)]);
  fixture.detectChanges();
}

export function todos(fixture: ComponentFixture<unknown>, selector: string): HTMLElement[] {
  return Array.from((fixture.nativeElement as HTMLElement).querySelectorAll<HTMLElement>(selector));
}

export function uno(fixture: ComponentFixture<unknown>, selector: string): HTMLElement | null {
  return (fixture.nativeElement as HTMLElement).querySelector<HTMLElement>(selector);
}

export function texto(elemento: Element | null | undefined): string {
  return (elemento?.textContent ?? '').replace(/\s+/g, ' ').trim();
}

// 'AAAA-MM-DD' a N días de hoy (en la hora local, igual que un <input type="date">)
export function fechaEnDias(dias: number): string {
  const fecha = new Date();
  fecha.setDate(fecha.getDate() + dias);
  const mes = String(fecha.getMonth() + 1).padStart(2, '0');
  const dia = String(fecha.getDate()).padStart(2, '0');
  return `${fecha.getFullYear()}-${mes}-${dia}`;
}

// Escribe en un campo y avisa al formulario, como si la persona lo hubiera escrito
export function escribir(fixture: ComponentFixture<unknown>, nombre: string, valor: string): void {
  const campo = uno(fixture, `[name="${nombre}"]`) as HTMLInputElement | null;
  if (!campo) throw new Error(`No encontré un campo con name="${nombre}" en el formulario`);
  campo.value = valor;
  campo.dispatchEvent(new Event('input', { bubbles: true }));
  campo.dispatchEvent(new Event('change', { bubbles: true }));
}

export function enviar(fixture: ComponentFixture<unknown>): void {
  const formulario = uno(fixture, 'form');
  if (!formulario) throw new Error('No encontré el <form>');
  formulario.dispatchEvent(new Event('submit', { bubbles: true, cancelable: true }));
}
