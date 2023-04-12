import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogManageTthComponent } from './dialog-manage-tth.component';

describe('DialogManageTthComponent', () => {
  let component: DialogManageTthComponent;
  let fixture: ComponentFixture<DialogManageTthComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DialogManageTthComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DialogManageTthComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
