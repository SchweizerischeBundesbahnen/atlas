import { TestBed } from '@angular/core/testing';
import { TthTableService } from './tth-table.service';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';

describe('TthTableService', () => {
  let service: TthTableService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
    });
    service = new TthTableService(TestBed.inject(Router));
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
