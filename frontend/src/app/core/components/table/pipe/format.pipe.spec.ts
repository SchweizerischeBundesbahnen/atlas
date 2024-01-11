import { FormatPipe } from './format.pipe';
import { TranslatePipe } from '@ngx-translate/core';
import { TableColumn } from '../table-column';
import SpyObj = jasmine.SpyObj;

describe('FormatPipe', () => {
  let translatePipeMock: SpyObj<TranslatePipe>;
  let formatPipe: FormatPipe;

  beforeEach(() => {
    translatePipeMock = jasmine.createSpyObj<TranslatePipe>('TranslatePipeSpy', ['transform']);
    formatPipe = new FormatPipe(translatePipeMock);
  });

  it('create an instance', () => {
    expect(formatPipe).toBeTruthy();
  });

  it('should format as Date', () => {
    const date = new Date(2023, 0, 1);
    const tableColumn: TableColumn<object> = { formatAsDate: true } as TableColumn<object>;
    const formatted = formatPipe.transform(date, tableColumn);
    expect(formatted).toEqual('01.01.2023');
  });

  it('should format undefined as empty string', () => {
    const tableColumn: TableColumn<object> = { formatAsDate: true } as TableColumn<object>;
    const formatted = formatPipe.transform(undefined, tableColumn);
    expect(formatted).toEqual('');
  });

  it('should translate withPrefix', () => {
    const value = 'test';
    const tableColumn: TableColumn<object> = {
      translate: {
        withPrefix: 'prefix.',
      },
    } as TableColumn<object>;
    formatPipe.transform(value, tableColumn);
    expect(translatePipeMock.transform).toHaveBeenCalledOnceWith('prefix.test');
  });

  it('should call column callback', () => {
    const value = 'test';
    const tableColumn = jasmine.createSpyObj('TableColumn', ['callback']);

    formatPipe.transform(value, tableColumn);
    expect(tableColumn.callback).toHaveBeenCalledOnceWith('test');
  });

  it('should only return value when no condition matches', () => {
    const value = 'test';
    const formatted = formatPipe.transform(value, {} as TableColumn<object>);
    expect(formatted).toEqual('test');
  });
});
