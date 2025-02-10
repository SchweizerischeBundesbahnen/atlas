import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SublineShorteningDialogComponent } from './subline-shortening-dialog.component';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { AppTestingModule } from '../../../../app.testing.module';

describe('SublineShorteningDialogComponent', () => {
  let component: SublineShorteningDialogComponent;
  let fixture: ComponentFixture<SublineShorteningDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SublineShorteningDialogComponent],
      imports: [AppTestingModule],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: {
            isAllowed: true,
            affectedSublines: {
              allowedSublines: [],
              notAllowedSublines: [],
            },
          },
        },
        { provide: TranslatePipe },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SublineShorteningDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
