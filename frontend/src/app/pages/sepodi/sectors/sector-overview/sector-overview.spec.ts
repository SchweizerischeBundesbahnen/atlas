import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SectorOverview } from './sector-overview';

describe('SectorOverview', () => {
  let component: SectorOverview;
  let fixture: ComponentFixture<SectorOverview>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SectorOverview]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SectorOverview);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
