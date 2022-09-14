import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReadOnlyDataComponent } from './read-only-data.component';

describe('ReadOnlyDataComponent', () => {
  let component: ReadOnlyDataComponent<any>;
  let fixture: ComponentFixture<ReadOnlyDataComponent<any>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ReadOnlyDataComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ReadOnlyDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
