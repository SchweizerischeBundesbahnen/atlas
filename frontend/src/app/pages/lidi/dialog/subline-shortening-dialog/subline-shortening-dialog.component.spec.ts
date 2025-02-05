import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SublineShorteningDialogComponent } from './subline-shortening-dialog.component';

describe('SublineShorteningDialogComponent', () => {
  let component: SublineShorteningDialogComponent;
  let fixture: ComponentFixture<SublineShorteningDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SublineShorteningDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SublineShorteningDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
