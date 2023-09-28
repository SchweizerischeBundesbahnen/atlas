import { TestBed } from '@angular/core/testing';
import { ServiceWorkerService } from './service-worker.service';
import { SwUpdate } from '@angular/service-worker';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { DialogComponent } from './core/components/dialog/dialog.component';
import { of, Subject } from 'rxjs';

describe('ServiceWorkerService', () => {
  let service: ServiceWorkerService;

  const matDialogSpy = jasmine.createSpyObj<MatDialog>(['open']);
  const matDialogRefSpy = jasmine.createSpyObj<MatDialogRef<DialogComponent>>(['afterClosed']);
  matDialogSpy.open.and.returnValue(matDialogRefSpy);

  class SwUpdateMock {
    versionUpdates = new Subject<{ type: string }>();
    unrecoverable = new Subject<void>();
    isEnabled = true;
    checkForUpdate = () => Promise.resolve(false);
  }

  let swUpdateMock: SwUpdateMock;

  beforeEach(() => {
    swUpdateMock = new SwUpdateMock();

    TestBed.configureTestingModule({
      providers: [
        ServiceWorkerService,
        { provide: SwUpdate, useValue: swUpdateMock },
        { provide: MatDialog, useValue: matDialogSpy },
      ],
    });

    service = TestBed.inject(ServiceWorkerService);

    spyOn<any>(service, 'openSWDialog').and.callThrough();
    spyOn<any>(ServiceWorkerService, 'reloadPage');
    matDialogSpy.open.calls.reset();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should open dialog on versionUpdate event and reload page', () => {
    matDialogRefSpy.afterClosed.and.returnValue(of(true));
    swUpdateMock.versionUpdates.next({
      type: 'VERSION_READY',
    });
    expect(service['openSWDialog']).toHaveBeenCalledOnceWith(
      'SW_DIALOG.UPDATE_TITLE',
      'SW_DIALOG.UPDATE_MESSAGE',
    );
    expect(matDialogSpy.open).toHaveBeenCalledOnceWith(DialogComponent, {
      data: {
        confirmText: 'DIALOG.RELOAD',
        title: 'SW_DIALOG.UPDATE_TITLE',
        message: 'SW_DIALOG.UPDATE_MESSAGE',
      },
      panelClass: 'atlas-dialog-panel',
      backdropClass: 'atlas-dialog-backdrop',
    });
    expect(ServiceWorkerService['reloadPage']).toHaveBeenCalledOnceWith();
  });

  it('should not open dialog on versionUpdate event', () => {
    swUpdateMock.versionUpdates.next({
      type: 'VERSION_DETECTED',
    });
    expect(matDialogSpy.open).not.toHaveBeenCalled();
  });

  it('should open dialog on unrecoverable event', () => {
    matDialogRefSpy.afterClosed.and.returnValue(of(false));
    swUpdateMock.unrecoverable.next();
    expect(service['openSWDialog']).toHaveBeenCalledOnceWith(
      'SW_DIALOG.UNRECOVERABLE_TITLE',
      'SW_DIALOG.UNRECOVERABLE_MESSAGE',
    );
    expect(matDialogSpy.open).toHaveBeenCalledOnceWith(DialogComponent, {
      data: {
        confirmText: 'DIALOG.RELOAD',
        title: 'SW_DIALOG.UNRECOVERABLE_TITLE',
        message: 'SW_DIALOG.UNRECOVERABLE_MESSAGE',
      },
      panelClass: 'atlas-dialog-panel',
      backdropClass: 'atlas-dialog-backdrop',
    });
    expect(ServiceWorkerService['reloadPage']).not.toHaveBeenCalled();
  });
});
