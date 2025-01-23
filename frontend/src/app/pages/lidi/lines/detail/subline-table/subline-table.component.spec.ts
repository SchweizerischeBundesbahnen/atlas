import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SublineTableComponent } from './subline-table.component';

describe('SublineTableComponent', () => {
  let component: SublineTableComponent;
  let fixture: ComponentFixture<SublineTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SublineTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SublineTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
