import { beforeEach, describe, expect, it, Mocked, vi } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { TranslationSortingService } from './translation-sorting.service';
import { translateServiceProvider } from '../../app.testing.mocks';
import { TranslatePipe } from '@ngx-translate/core';

const translatePipeMockArguments: Record<string, string> = {
  'p.A': 'A',
  'p.B': 'B',
  'p.C': 'C',
} as const;

describe('TranslationSortingService', () => {
  let service: TranslationSortingService;

  let translatePipeMock: Mocked<Pick<TranslatePipe, 'transform'>>;

  beforeEach(() => {
    translatePipeMock = {
      transform: vi.fn().mockImplementation((key: string) => translatePipeMockArguments[key]),
    };

    TestBed.configureTestingModule({
      providers: [translateServiceProvider, { provide: TranslatePipe, useValue: translatePipeMock }],
    });
    service = TestBed.inject(TranslationSortingService);
  });

  it('should sort values by translation prefix', () => {
    //when
    const result = service.sort(['A', 'C', 'B'], 'p.');
    //then
    expect(result).toBeDefined();
    expect(result).toEqual(['A', 'B', 'C']);
  });
});
