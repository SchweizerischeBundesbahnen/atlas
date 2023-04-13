import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OverviewTabComponent } from './overview-tab.component';
import { AppTestingModule } from '../../../app.testing.module';

describe('TimetableHearingOverviewTabComponent', () => {
  let component: OverviewTabComponent;
  let fixture: ComponentFixture<OverviewTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OverviewTabComponent],
      imports: [AppTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(OverviewTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
