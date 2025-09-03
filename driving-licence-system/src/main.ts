import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { AppComponent } from './app/app.component';
import { routes } from './app/app.routes';
import { importProvidersFrom } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import './polyfills';

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),       // ✅ router is provided globally
    importProvidersFrom(HttpClientModule)
  ]
}).catch(err => console.error(err));
