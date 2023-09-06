import { DisplayCantonPipe } from './display-canton.pipe';
import { SwissCanton } from '../../api';

describe('DisplayCantonPipe', () => {
  it('create an instance', () => {
    const translatePipe = jasmine.createSpyObj('TranslatePipe', ['transform']);
    translatePipe.transform.and.callFake((value: string) => value);

    const pipe = new DisplayCantonPipe(translatePipe);
    expect(pipe).toBeTruthy();
    expect(pipe.transform(SwissCanton.Bern)).toBe('TTH.CANTON.BE');
    expect(pipe.transform()).toBe('-');
  });
});
