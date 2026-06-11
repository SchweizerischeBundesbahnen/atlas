import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SublineShorteningDialogComponent } from './subline-shortening-dialog.component';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { AppTestingModule } from '../../../../app.testing.module';
import { Router } from '@angular/router';
import { Pages } from '../../../pages';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';

describe('SublineShorteningDialogComponent', () => {
  let component: SublineShorteningDialogComponent;
  let fixture: ComponentFixture<SublineShorteningDialogComponent>;
  let router: Mocked<Pick<Router, 'createUrlTree' | 'serializeUrl'>>;

  beforeEach(() => {
    router = {
      createUrlTree: vi.fn(),
      serializeUrl: vi.fn(),
    };

    TestBed.configureTestingModule({
      imports: [AppTestingModule, SublineShorteningDialogComponent],
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
        { provide: Router, useValue: router },
      ],
    });

    fixture = TestBed.createComponent(SublineShorteningDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should open new tab with correct url', () => {
    const slnid = '123';
    const expectedUrl = '/line-directory/sublines/123';

    router.serializeUrl.mockReturnValue(expectedUrl);
    const windowOpenSpy = vi.spyOn(window, 'open').mockImplementation(() => null);

    component.openNewTabOfSubline(slnid);

    expect(router.createUrlTree).toHaveBeenCalledWith([Pages.LIDI.path, Pages.SUBLINES.path, slnid]);
    expect(windowOpenSpy).toHaveBeenCalledExactlyOnceWith(expectedUrl, '_blank');
  });
});
