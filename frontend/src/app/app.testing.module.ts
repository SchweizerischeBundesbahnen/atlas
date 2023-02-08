import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { DateModule } from './core/module/date.module';
import { MaterialModule } from './core/module/material.module';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { AuthService } from './core/auth/auth.service';
import { ApplicationRole } from './api';

const dialogMock = {
  close: () => {
    // Mock implementation
  },
};

export const authServiceMock: Partial<AuthService> = {
  claims: { name: 'Test', email: 'test@test.ch', sbbuid: 'e123456', roles: [] },
  isAdmin: true,
  getPermissions: () => [],
  getApplicationUserPermission: (applicationType) => {
    return { application: applicationType, role: ApplicationRole.Supervisor, sboids: [] };
  },
  logout: () => Promise.resolve(true),
};

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
  providers: [{ provide: MatDialogRef, useValue: dialogMock }],
})
export class AppTestingModule {}
