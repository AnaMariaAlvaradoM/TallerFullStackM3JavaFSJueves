import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { jwtInterceptor } from './core/jwt-interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    // withComponentInputBinding: el :id de la URL llega como input() al componente
    provideRouter(routes, withComponentInputBinding()),
    // Todas las peticiones pasan por el interceptor, que les pega el token
    provideHttpClient(withInterceptors([jwtInterceptor])),
  ],
};
