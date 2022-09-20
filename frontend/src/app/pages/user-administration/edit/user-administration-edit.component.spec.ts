import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationEditComponent } from './user-administration-edit.component';
import {
  TranslateFakeLoader,
  TranslateLoader,
  TranslateModule,
  TranslatePipe,
} from '@ngx-translate/core';
import { Component } from '@angular/core';
import { MaterialModule } from '../../../core/module/material.module';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-dialog-close',
  template: '',
})
class MockDialogCloseComponent {}

describe('UserAdministrationEditComponent', () => {
  let component: UserAdministrationEditComponent;
  let fixture: ComponentFixture<UserAdministrationEditComponent>;

  const dialogMock = {
    close: () => {
      // Mock implementation
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserAdministrationEditComponent, MockDialogCloseComponent],
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
        MaterialModule,
        RouterTestingModule,
        HttpClientTestingModule,
      ],
      providers: [TranslatePipe, { provide: MatDialogRef, useValue: dialogMock }],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationEditComponent);
    component = fixture.componentInstance;
    component.user = {};
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
