import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { BoStatementDetailComponent } from './bo-statement-detail.component';
import { translateServiceProvider } from '../../../../../app.testing.mocks';
import { provideHttpClient } from '@angular/common/http';
import { statement } from '../../statement-test-util';
import { ActivatedRoute, Router } from '@angular/router';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { SwissCanton } from '../../../../../api';
import { Pages } from '../../../../pages';

describe('BoStatementDetail', () => {
  let component: BoStatementDetailComponent;
  let fixture: ComponentFixture<BoStatementDetailComponent>;
  let router: Router;

  beforeEach(async () => {
    const activatedRoute = {
      snapshot: {
        data: {
          statement: statement,
        },
        params: {
          canton: 'be',
        },
      },
    };
    await TestBed.configureTestingModule({
      imports: [BoStatementDetailComponent],
      providers: [
        translateServiceProvider,
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: activatedRoute,
        },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    fixture = TestBed.createComponent(BoStatementDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get anonymus document', () => {
    expect(component.anonymDocuments).toHaveLength(1);
    expect(component.anonymDocuments[0].getRawValue()).toEqual({
      id: 1,
      anonymous: true,
      fileName: 'file1',
      fileSize: 12,
    });
  });

  it('should show statement when statementAnonymous is true', () => {
    //given & when
    component.form.controls.statementAnonymous.setValue(true);
    //then
    expect(component.getStatementControlName()).toBe('statement');
  });

  it('should show anonymousStatement when statementAnonymous is false', () => {
    //given & when
    component.form.controls.statementAnonymous.setValue(false);
    //then
    expect(component.getStatementControlName()).toBe('anonymousStatement');
  });

  it('should go to dossier', () => {
    //given
    component.statement = {
      id: 1,
      swissCanton: SwissCanton.Bern,
      statement: 'Öper isch am YB-Match gsi',
      statementSender: {
        emails: new Set('fan@yb.ch'),
      },
      dossierId: 123,
    };

    vi.spyOn(router, 'navigate').mockResolvedValue(true);
    //when
    component.goToDossier();
    //then
    expect(router.navigate).toHaveBeenCalledWith(['../..', Pages.TTH_DOSSIERS.path, 123], expect.any(Object));
  });
});
