import { UserDisplayNamePipe } from './user-display-name.pipe';

describe('UserDisplayNamePipe', () => {
  it('create an instance', () => {
    const pipe = new UserDisplayNamePipe(jasmine.createSpyObj(['getUserDisplayName']));
    expect(pipe).toBeTruthy();
  });

  it('empty observable if userId undefined', (done) => {
    const pipe = new UserDisplayNamePipe(jasmine.createSpyObj(['getUserDisplayName']));
    pipe.transform().subscribe((result) => {
      expect(result).toBeUndefined();
      done();
    });
  });

  it('should return displayName over service', (done) => {
    const pipe = new UserDisplayNamePipe(
      jasmine.createSpyObj({ getUserDisplayName: () => ({ displayName: 'Atlas User' }) }),
    );
    pipe.transform('u123456').subscribe((result) => {
      expect(result).toEqual('Atlas User');
      done();
    });
  });
});
