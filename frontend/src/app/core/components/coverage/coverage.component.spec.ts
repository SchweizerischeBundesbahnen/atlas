import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CoverageComponent } from './coverage.component';
import { AppTestingModule } from '../../../app.testing.module';

describe('CoverageComponent', () => {
  let component: CoverageComponent;
  let fixture: ComponentFixture<CoverageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CoverageComponent],
      imports: [AppTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CoverageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
