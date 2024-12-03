import { UserDisplayNamePipe } from './user-display-name.pipe';
import { of } from 'rxjs';

describe('UserDisplayNamePipe', () => {

  const userAdministrationService = jasmine.createSpyObj('UserAdministrationService', ['getUserDisplayName']);

  it('create an instance', () => {
    const pipe = new UserDisplayNamePipe(userAdministrationService);
    expect(pipe).toBeTruthy();
  });

  it('empty observable if userId undefined', (done) => {
    const pipe = new UserDisplayNamePipe(userAdministrationService);
    pipe.transform().subscribe({
      complete: () => {
        expect(userAdministrationService.getUserDisplayName).not.toHaveBeenCalled();
        done();
      }
    });
  });

  it('should return displayName over service', (done) => {
    userAdministrationService.getUserDisplayName.and.returnValue(of({ displayName: 'Atlas User' }));
    const pipe = new UserDisplayNamePipe(userAdministrationService);
    pipe.transform('u123456').subscribe((result) => {
      expect(result).toEqual('Atlas User');
      done();
    });
  });
});
