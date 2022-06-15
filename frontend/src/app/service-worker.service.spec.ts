import { TestBed } from '@angular/core/testing';
import { ServiceWorkerService } from './service-worker.service';
import { SwUpdate } from '@angular/service-worker';
import { MaterialModule } from './core/module/material.module';
import { EventEmitter } from '@angular/core';

describe('ServiceWorkerService', () => {
  let service: ServiceWorkerService;
  let swUpdateSpy: jasmine.SpyObj<SwUpdate>;
  const versionUpdateEventEmitter = new EventEmitter<{ type: string }>();
  const unrecoverableEventEmitter = new EventEmitter();

  beforeEach(() => {
    swUpdateSpy = jasmine.createSpyObj(['checkForUpdate'], {
      isEnabled: true,
      versionUpdates: versionUpdateEventEmitter,
      unrecoverable: unrecoverableEventEmitter,
    });

    TestBed.configureTestingModule({
      imports: [MaterialModule],
      providers: [ServiceWorkerService, { provide: SwUpdate, useValue: swUpdateSpy }],
    });

    service = TestBed.inject(ServiceWorkerService);
    spyOn<any>(service, 'openSWDialog');
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should open dialog on versionUpdate event', () => {
    versionUpdateEventEmitter.emit({ type: 'VERSION_READY' });
    expect(service['openSWDialog']).toHaveBeenCalledOnceWith(
      'SW_DIALOG.UPDATE_TITLE',
      'SW_DIALOG.UPDATE_MESSAGE'
    );
  });

  it('should not open dialog on versionUpdate event', () => {
    versionUpdateEventEmitter.emit({ type: 'VERSION_DETECTED' });
    expect(service['openSWDialog']).not.toHaveBeenCalled();
  });

  it('should open dialog on unrecoverable event', () => {
    unrecoverableEventEmitter.emit();
    expect(service['openSWDialog']).toHaveBeenCalledOnceWith(
      'SW_DIALOG.UNRECOVERABLE_TITLE',
      'SW_DIALOG.UNRECOVERABLE_MESSAGE'
    );
  });
});
