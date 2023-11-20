import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InformationDeskComponent } from './information-desk.component';

describe('InformationDeskComponent', () => {
  let component: InformationDeskComponent;
  let fixture: ComponentFixture<InformationDeskComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [InformationDeskComponent],
    });
    fixture = TestBed.createComponent(InformationDeskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
