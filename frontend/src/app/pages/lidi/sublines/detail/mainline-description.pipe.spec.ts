import {MainlineDescriptionPipe} from './mainline-description.pipe';
import {Line} from '../../../../api';

describe('MainlineDescriptionPipe', () => {
  it('create an instance', () => {
    const translatePipeMock = jasmine.createSpyObj('TranslatePipe', {
      transform: 'LIDI.SUBLINE.NO_LINE_DESIGNATION_AVAILABLE',
    });
    const pipe = new MainlineDescriptionPipe(translatePipeMock);
    expect(pipe).toBeTruthy();

    const lineWithDescription = {
      description: 'desc',
    } as Line;
    expect(pipe.transform(lineWithDescription)).toBe('desc');

    const lineWithoutDescription = {swissLineNumber: 'swissLineNumber'} as Line;
    expect(pipe.transform(lineWithoutDescription)).toBe('(LIDI.SUBLINE.NO_LINE_DESIGNATION_AVAILABLE)');
  });
});
