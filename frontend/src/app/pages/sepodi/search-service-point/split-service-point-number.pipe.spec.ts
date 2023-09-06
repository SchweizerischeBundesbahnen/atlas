import { SplitServicePointNumberPipe } from './split-service-point-number.pipe';

describe('SplitServicePointNumberPipe', () => {
  it('create an instance', () => {
    const pipe = new SplitServicePointNumberPipe();
    expect(pipe).toBeTruthy();
  });

  it('should split without highlight', () => {
    const pipe = new SplitServicePointNumberPipe();
    expect(pipe.transform('8507000')).toBe('85 07000');
  });

  it('should split with highlight', () => {
    const pipe = new SplitServicePointNumberPipe();
    expect(pipe.transform('850<b>7000</b>')).toBe('85 0<b>7000</b>');
  });

  it('should split at beginning with highlight', () => {
    const pipe = new SplitServicePointNumberPipe();
    expect(pipe.transform('<b>85</b>07000')).toBe('<b>85 </b>07000');
  });
});
