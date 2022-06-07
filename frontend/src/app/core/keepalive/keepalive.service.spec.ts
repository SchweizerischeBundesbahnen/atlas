import { TestBed } from '@angular/core/testing';
import { KeepaliveService } from './keepalive.service';

describe('KeepaliveService', () => {
  let service: KeepaliveService;

  beforeEach(() => {
    service = TestBed.inject(KeepaliveService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('test startWatching', () => {
    service.startWatching(() => null);
    expect(service['intervalId']).toBeDefined();
    expect(service['eventsSubscription']).toBeDefined();
    expect(service['timeoutId']).toBeUndefined();
    expect(service['interruptions']).toEqual([]);
  });

  it('test stopWatching', () => {
    service.startWatching(() => null);
    service.stopWatching();
    expect(service['intervalId']).toBeUndefined();
    expect(service['timeoutId']).toBeUndefined();
    expect(service['eventsSubscription']).toBeDefined();
    expect(service['eventsSubscription']?.closed).toBeTrue();
    expect(service['interruptions']).toEqual([]);
  });
});
