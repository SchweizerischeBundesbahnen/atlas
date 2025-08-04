import { TestBed } from '@angular/core/testing';
import { TranslationSortingService } from './translation-sorting.service';
import { TranslatePipe } from '@ngx-translate/core';
import { AppTestingModule } from '../../app.testing.module';
import { translateServiceProvider } from '../../app.testing.mocks';

const translatePipeSpy = jasmine.createSpyObj('translatePipe', ['transform']);
translatePipeSpy.transform
  .withArgs('p.A')
  .and.returnValue('A')
  .withArgs('p.B')
  .and.returnValue('B')
  .withArgs('p.C')
  .and.returnValue('C');

describe('TranslationSortingService', () => {
  let service: TranslationSortingService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        translateServiceProvider,
        { provide: TranslationSortingService },
        { provide: TranslatePipe, useValue: translatePipeSpy },
      ],
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
