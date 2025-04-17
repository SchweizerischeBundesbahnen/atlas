import { TestBed } from '@angular/core/testing';
import { DateService } from './date.service';

describe('DateService', () => {
  let service: DateService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should calculate difference in days between dates', () => {
    //when
    expect(
      DateService.differenceInDays(
        new Date('2020-01-01'),
        new Date('2019-12-31')
      )
    ).toBe(-1);
    expect(
      DateService.differenceInDays(
        new Date('2020-01-01'),
        new Date('2020-01-01')
      )
    ).toBe(0);
    expect(
      DateService.differenceInDays(
        new Date('2020-01-01'),
        new Date('2020-01-02')
      )
    ).toBe(1);
    expect(
      DateService.differenceInDays(
        new Date('2020-01-01'),
        new Date('2020-01-03')
      )
    ).toBe(2);
    //then
  });
});
