import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogCloseComponent } from './dialog-close.component';

describe('DialogCloseComponent', () => {
  let component: DialogCloseComponent;
  let fixture: ComponentFixture<DialogCloseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DialogCloseComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DialogCloseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
