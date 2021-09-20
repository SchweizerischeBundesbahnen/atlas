import { TestBed } from '@angular/core/testing';

import { NotificationService } from './notification.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';
import { MaterialModule } from '../module/material.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('NotificationService', () => {
  let service: NotificationService;
  const matSnackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);

  const translateServiceStub = {
    get() {
      return of('Notification with value');
    },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        MatSnackBarModule,
        TranslateModule.forRoot(),
        MaterialModule,
        BrowserAnimationsModule,
      ],
      providers: [
        { provide: TranslateService, useValue: translateServiceStub },
        { provide: MatSnackBar, useValue: matSnackBarSpy },
      ],
    });
    service = TestBed.inject(NotificationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should be success', () => {
    service.success('msg');
    expect(service).toBeTruthy();
    expect(matSnackBarSpy.open).toHaveBeenCalledWith(
      'Notification with value',
      '',
      Object({
        duration: 5000,
        horizontalPosition: 'right',
        verticalPosition: 'top',
        panelClass: ['success', 'notification'],
      })
    );
  });

  it('should be error', () => {
    service.error('msg');
    expect(service).toBeTruthy();
    expect(matSnackBarSpy.open).toHaveBeenCalledWith(
      'Notification with value',
      '',
      Object({
        duration: 5000,
        horizontalPosition: 'right',
        verticalPosition: 'top',
        panelClass: ['error', 'notification'],
      })
    );
  });

  it('should be info', () => {
    service.info('msg');
    expect(service).toBeTruthy();
    expect(matSnackBarSpy.open).toHaveBeenCalledWith(
      'Notification with value',
      '',
      Object({
        duration: 5000,
        horizontalPosition: 'right',
        verticalPosition: 'top',
        panelClass: ['info', 'notification'],
      })
    );
  });
});
