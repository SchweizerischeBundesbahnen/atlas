import { EditTitlePipe } from './edit-title.pipe';

describe('EditTitlePipe', () => {
  it('create an instance', () => {
    const translatePipeMock = jasmine.createSpyObj('TranslatePipe', {
      transform: 'USER_ADMIN.NOT_FOUND',
    });
    const pipe = new EditTitlePipe(translatePipeMock);
    expect(pipe).toBeTruthy();
    expect(
      pipe.transform({
        firstName: 'first',
        lastName: 'last',
      })
    ).toBe('first last');
    expect(pipe.transform(undefined)).toBe('USER_ADMIN.NOT_FOUND');
  });
});
