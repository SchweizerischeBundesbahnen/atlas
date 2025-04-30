import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {
  TranslateFakeLoader,
  TranslateLoader,
  TranslateModule,
  TranslatePipe,
} from '@ngx-translate/core';
import { DateModule } from './core/module/date.module';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { RouterModule } from '@angular/router';

const dialogMock = {
  close: () => {
    // Mock implementation
  },
};

@NgModule({
  imports: [
    BrowserAnimationsModule,
    DateModule.forRoot(),
    HttpClientTestingModule,
    ReactiveFormsModule,
    RouterModule.forRoot([]),
    TranslateModule.forRoot({
      loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
    }),
  ],
  exports: [
    BrowserAnimationsModule,
    DateModule,
    HttpClientTestingModule,
    ReactiveFormsModule,
    RouterModule,
    TranslateModule,
  ],
  providers: [
    { provide: MatDialogRef, useValue: dialogMock },
    { provide: TranslatePipe },
  ],
})
export class AppTestingModule {}
