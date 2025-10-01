import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SectorGroupDetailComponent } from './sector-group-detail.component';

describe('SectorGroupDetailComponent', () => {
  let component: SectorGroupDetailComponent;
  let fixture: ComponentFixture<SectorGroupDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SectorGroupDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SectorGroupDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
