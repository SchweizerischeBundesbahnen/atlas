import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationDetailComponent } from './user-administration-detail.component';
import {
  TranslateFakeLoader,
  TranslateLoader,
  TranslateModule,
  TranslatePipe,
} from '@ngx-translate/core';
import { MatDialogRef } from '@angular/material/dialog';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import SpyObj = jasmine.SpyObj;
import { of } from 'rxjs';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'button[mat-icon-button]',
  template: '',
})
class MockMatIconButtonComponent {
  @Input() disableRipple = false;
}

describe('UserAdministrationDetailComponent', () => {
  let component: UserAdministrationDetailComponent;
  let fixture: ComponentFixture<UserAdministrationDetailComponent>;

  let dialogServiceSpy: SpyObj<DialogService>;

  let dialogMock: any;

  beforeEach(async () => {
    dialogMock = {
      close: () => {
        // Mock implementation
      },
    };
    dialogServiceSpy = jasmine.createSpyObj('DialogService', ['confirm', 'closeConfirmDialog']);
    await TestBed.configureTestingModule({
      declarations: [UserAdministrationDetailComponent, MockMatIconButtonComponent],
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [
        TranslatePipe,
        { provide: MatDialogRef, useValue: dialogMock },
        {
          provide: DialogService,
          useValue: dialogServiceSpy,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('test confirmCancel (showDialog=false)', () => {
    spyOn(dialogMock, 'close');
    component.confirmCancel(false);
    expect(dialogServiceSpy.confirm).not.toHaveBeenCalled();
    expect(dialogMock.close).toHaveBeenCalledOnceWith();
  });

  it('test confirmCancel (showDialog=true, result=true)', () => {
    dialogServiceSpy.confirm.and.returnValue(of(true));
    spyOn(dialogMock, 'close');
    component.confirmCancel(true);
    expect(dialogServiceSpy.confirm).toHaveBeenCalledOnceWith({
      title: 'DIALOG.DISCARD_CHANGES_TITLE',
      message: 'DIALOG.LEAVE_SITE',
    });
    expect(dialogMock.close).toHaveBeenCalledOnceWith();
    expect(dialogServiceSpy.closeConfirmDialog).not.toHaveBeenCalled();
  });

  it('test confirmCancel (showDialog=true, result=false)', () => {
    dialogServiceSpy.confirm.and.returnValue(of(false));
    spyOn(dialogMock, 'close');
    component.confirmCancel(true);
    expect(dialogServiceSpy.confirm).toHaveBeenCalledOnceWith({
      title: 'DIALOG.DISCARD_CHANGES_TITLE',
      message: 'DIALOG.LEAVE_SITE',
    });
    expect(dialogMock.close).not.toHaveBeenCalled();
    expect(dialogServiceSpy.closeConfirmDialog).toHaveBeenCalledOnceWith();
  });
});
