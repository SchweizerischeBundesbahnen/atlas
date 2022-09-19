import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationEditComponent } from './user-administration-edit.component';
import { AppTestingModule } from '../../../app.testing.module';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { Component } from '@angular/core';

@Component({
  selector: 'app-dialog-close',
  template: '',
})
class MockDialogCloseComponent {}

describe('UserAdministrationEditComponent', () => {
  let component: UserAdministrationEditComponent;
  let fixture: ComponentFixture<UserAdministrationEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserAdministrationEditComponent, MockDialogCloseComponent],
      imports: [AppTestingModule],
      providers: [
        TranslatePipe,
        {
          provide: MAT_DIALOG_DATA,
          useValue: { user: undefined },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
