import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { DisplayCantonPipe } from './display-canton.pipe';
import { SwissCanton } from '../../api';
import { TranslatePipe } from '@ngx-translate/core';

describe('DisplayCantonPipe', () => {
  type TranslatePipeMock = Mocked<Pick<TranslatePipe, 'transform'>>;
  let translatePipe: TranslatePipeMock;
  let pipe: DisplayCantonPipe;

  beforeEach(() => {
    // Mocking: stub TranslatePipe methods the tested pipe depends on
    translatePipe = {
      transform: vi.fn().mockImplementation((value) => value),
    };

    // Config: provide the pipe and its mocked dependencies through TestBed
    TestBed.configureTestingModule({
      providers: [DisplayCantonPipe, { provide: TranslatePipe, useValue: translatePipe }],
    });

    // Arrangement: obtain the pipe instance via TestBed so DI is respected
    pipe = TestBed.inject(DisplayCantonPipe);
  });

  it('creates an instance and delegates translations', () => {
    expect(pipe).toBeTruthy();
    expect(pipe.transform(SwissCanton.Bern)).toBe('TTH.CANTON.BE');
    expect(pipe.transform()).toBe('-');
    expect(translatePipe.transform).toHaveBeenCalledWith('TTH.CANTON.BE');
  });
});
