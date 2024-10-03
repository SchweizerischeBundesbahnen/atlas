import { UserDisplayNamePipe } from './user-display-name.pipe';
import { of } from 'rxjs';

describe('UserDisplayNamePipe', () => {
  it('create an instance', () => {
    const pipe = new UserDisplayNamePipe(jasmine.createSpyObj(['getUserDisplayName']));
    expect(pipe).toBeTruthy();
  });

  it('empty observable if userId undefined', (done) => {
    const pipe = new UserDisplayNamePipe(jasmine.createSpyObj(['getUserDisplayName']));
    pipe.transform().subscribe({ complete: () => done() });
  });

  it('should return displayName over service', (done) => {
    const userAdministrationService = jasmine.createSpyObj(['getUserDisplayName']);
    userAdministrationService.getUserDisplayName.and.returnValue(of({ displayName: 'Atlas User' }));
    const pipe = new UserDisplayNamePipe(userAdministrationService);
    pipe.transform('u123456').subscribe((result) => {
      expect(result).toEqual('Atlas User');
      done();
    });
  });
});
