import { MainlineSelectOptionPipe } from './mainline-select-option.pipe';
import { Line } from '../../../../api';

describe('MainlineSelectOptionPipe', () => {
  it('create an instance', () => {
    const translatePipeMock = jasmine.createSpyObj('TranslatePipe', {
      transform: 'LIDI.SUBLINE.NO_LINE_DESIGNATION_AVAILABLE',
    });
    const pipe = new MainlineSelectOptionPipe(translatePipeMock);
    expect(pipe).toBeTruthy();
    expect(
      pipe.transform({
        swissLineNumber: 'swissLineNumber',
        description: 'desc',
      } as Line)
    ).toBe('swissLineNumber desc');
    expect(pipe.transform({ swissLineNumber: 'swissLineNumber' } as Line)).toBe(
      'swissLineNumber (LIDI.SUBLINE.NO_LINE_DESIGNATION_AVAILABLE)'
    );
  });
});
