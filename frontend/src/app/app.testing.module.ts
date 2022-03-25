import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { DateModule } from './core/module/date.module';
import { MaterialModule } from './core/module/material.module';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  imports: [
    BrowserAnimationsModule,
    DateModule.forRoot(),
    HttpClientTestingModule,
    MaterialModule,
    ReactiveFormsModule,
    RouterTestingModule,
    TranslateModule.forRoot({
      loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
    }),
  ],
  exports: [
    BrowserAnimationsModule,
    DateModule,
    HttpClientTestingModule,
    MaterialModule,
    ReactiveFormsModule,
    RouterTestingModule,
    TranslateModule,
  ],
})
export class AppTestingModule {}
