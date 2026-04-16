import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { RevokeButton } from './revoke-button';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { DialogService } from '../../components/dialog/dialog.service';
import { of } from 'rxjs';
import { ApplicationType } from '../../../api';

describe('RevokeButton', () => {
  let component: RevokeButton;
  let fixture: ComponentFixture<RevokeButton>;
  let dialogServiceMock: Mocked<Pick<DialogService, 'openDialogDataWithConfirmationResult'>>;

  beforeEach(() => {
    dialogServiceMock = {
      openDialogDataWithConfirmationResult: vi.fn().mockReturnValue(of(true)),
    };

    TestBed.configureTestingModule({
      providers: [translateServiceProvider, { provide: DialogService, useValue: dialogServiceMock }],
    });

    fixture = TestBed.createComponent(RevokeButton);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('applicationType', ApplicationType.Bodi);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should revoke', () => {
    //given
    vi.spyOn(component.revokeClicked, 'emit');
    //when
    component.revoke();
    //then
    expect(dialogServiceMock.openDialogDataWithConfirmationResult).toHaveBeenCalled();
    expect(component.revokeClicked.emit).toHaveBeenCalled();
  });
});
