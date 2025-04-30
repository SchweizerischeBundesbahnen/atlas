import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AtlasSlideToggleComponent } from './atlas-slide-toggle.component';

describe('AtlasSlideToggleComponent', () => {
  let component: AtlasSlideToggleComponent;
  let fixture: ComponentFixture<AtlasSlideToggleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AtlasSlideToggleComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AtlasSlideToggleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
