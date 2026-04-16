import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { ShowTitlePipe } from './show-title.pipe';
import { FormatPipe } from './format.pipe';
import { TableColumn } from '../table-column';
import { TestBed } from '@angular/core/testing';

describe('ShowTitlePipe', () => {
  let showTitlePipe: ShowTitlePipe;

  let formatPipeStub: Mocked<Pick<FormatPipe, 'transform'>>;

  beforeEach(() => {
    formatPipeStub = {
      transform: vi.fn(),
    };

    TestBed.configureTestingModule({
      providers: [{ provide: FormatPipe, useValue: formatPipeStub }, ShowTitlePipe],
    });

    showTitlePipe = TestBed.inject(ShowTitlePipe);
  });

  it('create an instance', () => {
    expect(showTitlePipe).toBeTruthy();
  });

  it('should transform and hideTooltip should be false', () => {
    formatPipeStub.transform.mockReturnValue('testContentMustBeLongerThan20');
    const transformed = showTitlePipe.transform('test', {} as TableColumn<object>);
    expect(transformed).toEqual('testContentMustBeLongerThan20');
    expect(formatPipeStub.transform).toHaveBeenCalledExactlyOnceWith('test', {} as TableColumn<object>);
  });
});
