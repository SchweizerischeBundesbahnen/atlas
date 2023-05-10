import { ShowTitlePipe } from './show-title.pipe';
import { FormatPipe } from './format.pipe';
import SpyObj = jasmine.SpyObj;
import { TableColumn } from '../table-column';

describe('ShowTitlePipe', () => {
  let formatPipe: SpyObj<FormatPipe>;
  let showTitlePipe: ShowTitlePipe;

  beforeEach(() => {
    formatPipe = jasmine.createSpyObj<FormatPipe>('FormatPipeMockSpy', ['transform']);
    showTitlePipe = new ShowTitlePipe(formatPipe);
  });

  it('create an instance', () => {
    expect(showTitlePipe).toBeTruthy();
  });

  it('should transform and hideTooltip should be false', () => {
    formatPipe.transform.and.returnValue('testContentMustBeLongerThan20');
    const transformed = showTitlePipe.transform('test', {} as TableColumn<object>);
    expect(transformed).toEqual('testContentMustBeLongerThan20');
    expect(formatPipe.transform).toHaveBeenCalledOnceWith('test', {} as TableColumn<object>);
  });
});
