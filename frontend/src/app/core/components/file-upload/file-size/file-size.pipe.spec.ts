import { FileSizePipe } from './file-size.pipe';

describe('FileSizePipe', () => {
  it('create an instance', () => {
    const pipe = new FileSizePipe();
    expect(pipe).toBeTruthy();
  });

  it('should format 20 MB correctly', () => {
    const pipe = new FileSizePipe();
    expect(pipe.transform(20 * 1024 * 1024)).toBe('20 MB');
  });
});
