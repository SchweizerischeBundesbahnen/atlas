import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TranslatePipe } from '@ngx-translate/core';
import { DateModule } from './core/module/date.module';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { RouterModule } from '@angular/router';
import { translateServiceProvider } from './app.testing.mocks';

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
  ],
  exports: [
    BrowserAnimationsModule,
    DateModule,
    HttpClientTestingModule,
    ReactiveFormsModule,
    RouterModule,
  ],
  providers: [
    { provide: MatDialogRef, useValue: dialogMock },
    { provide: TranslatePipe },
    translateServiceProvider,
  ],
})
export class AppTestingModule {}
