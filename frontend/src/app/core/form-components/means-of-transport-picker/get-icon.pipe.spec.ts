import { GetIconPipe } from './get-icon.pipe';

describe('GetIconPipe', () => {
  it('should return filled icon when selectedMeans includes mean', () => {
    const pipe = new GetIconPipe();
    const icon = pipe.transform('BUS', ['TRAIN', 'BUS']);
    expect(icon).toEqual('BUS');
  });
  it('should return gray icon when selectedMeans not includes mean', () => {
    const pipe = new GetIconPipe();
    const icon = pipe.transform('BUS', ['TRAIN']);
    expect(icon).toEqual('BUS_GRAY');
  });
});
// todo: test servicepoint means selection
