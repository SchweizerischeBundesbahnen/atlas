import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SublineShorteningDialogComponent } from './subline-shortening-dialog.component';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { AppTestingModule } from '../../../../app.testing.module';
import { Router } from '@angular/router';
import { Pages } from '../../../pages';

describe('SublineShorteningDialogComponent', () => {
  let component: SublineShorteningDialogComponent;
  let fixture: ComponentFixture<SublineShorteningDialogComponent>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const routerSpy = jasmine.createSpyObj('Router', [
      'createUrlTree',
      'serializeUrl',
    ]);

    await TestBed.configureTestingModule({
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
        { provide: Router, useValue: routerSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SublineShorteningDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should open new tab with correct url', () => {
    const slnid = '123';

    const expectedUrl = '/line-directory/sublines/123';
    router.serializeUrl.and.returnValue(expectedUrl);

    spyOn(window, 'open');

    component.openNewTabOfSubline(slnid);

    expect(router.createUrlTree).toHaveBeenCalledWith([
      Pages.LIDI.path,
      Pages.SUBLINES.path,
      slnid,
    ]);
    expect(window.open).toHaveBeenCalledWith(expectedUrl, '_blank');
  });
});
