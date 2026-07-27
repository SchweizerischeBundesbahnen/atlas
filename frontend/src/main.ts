import { provideZoneChangeDetection } from '@angular/core';
import { AppComponent } from './app/app.component';
import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { setWorkerUrl } from 'maplibre-gl';

bootstrapApplication(AppComponent, {
  ...appConfig,
  providers: [provideZoneChangeDetection(), ...appConfig.providers],
}).catch((err) => console.error(err));

setWorkerUrl(new URL('maplibre-gl-worker.mjs', document.baseURI).toString());
