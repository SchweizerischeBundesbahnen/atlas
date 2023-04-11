import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TthChangeStatusDialogComponent } from './tth-change-status-dialog.component';

describe('TthChangeStatusDialogComponent', () => {
  let component: TthChangeStatusDialogComponent;
  let fixture: ComponentFixture<TthChangeStatusDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TthChangeStatusDialogComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TthChangeStatusDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
