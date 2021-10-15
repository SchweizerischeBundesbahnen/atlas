import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { of, throwError } from 'rxjs';
import { SublinesService, SublineVersion } from '../../../../api/lidi';
import { SublineDetailComponent } from './subline-detail.component';
import { CoreModule } from '../../../../core/module/core.module';

const sublineVersion: SublineVersion = {
  id: 1234,
  slnid: 'slnid',
  shortName: 'name',
  description: 'asdf',
  status: 'ACTIVE',
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
};

let component: SublineDetailComponent;
let fixture: ComponentFixture<SublineDetailComponent>;
let router: Router;

describe('SublineDetailComponent for existing sublineVersion', () => {
  const mockSublinesService = jasmine.createSpyObj('sublinesService', [
    'updateSublineVersion',
    'deleteSublineVersion',
  ]);
  const mockRoute = {
    snapshot: {
      data: {
        sublineDetail: sublineVersion,
      },
    },
  };

  beforeEach(() => {
    setupTestBed(mockSublinesService, mockRoute);

    fixture = TestBed.createComponent(SublineDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should update SublineVersion successfully', () => {
    mockSublinesService.updateSublineVersion.and.returnValue(of(sublineVersion));
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    fixture.componentInstance.updateRecord();
    fixture.detectChanges();

    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent).toBe('LIDI.SUBLINE.NOTIFICATION.EDIT_SUCCESS');
    expect(snackBarContainer.classList).toContain('success');
    expect(router.navigate).toHaveBeenCalled();
  });

  it('should not update Version', () => {
    const error = new Error('404');
    mockSublinesService.updateSublineVersion.and.returnValue(throwError(() => error));
    fixture.componentInstance.updateRecord();
    fixture.detectChanges();

    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent).toBe('LIDI.SUBLINE.NOTIFICATION.EDIT_ERROR');
    expect(snackBarContainer.classList).toContain('error');
  });

  it('should delete LineVersion successfully', () => {
    mockSublinesService.deleteSublineVersion.and.returnValue(of({}));
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    fixture.componentInstance.deleteRecord();
    fixture.detectChanges();

    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent).toBe('LIDI.SUBLINE.NOTIFICATION.DELETE_SUCCESS');
    expect(snackBarContainer.classList).toContain('success');
    expect(router.navigate).toHaveBeenCalled();
  });

  it('should not delete Version', () => {
    const error = new Error('404');
    mockSublinesService.deleteSublineVersion.and.returnValue(throwError(() => error));
    fixture.componentInstance.deleteRecord();
    fixture.detectChanges();

    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent).toBe('LIDI.SUBLINE.NOTIFICATION.DELETE_ERROR');
    expect(snackBarContainer.classList).toContain('error');
  });
});

describe('SublineDetailComponent for new sublineVersion', () => {
  const mockSublinesService = jasmine.createSpyObj('sublinesService', ['createSublineVersion']);
  const mockRoute = {
    snapshot: {
      data: {
        sublineDetail: 'add',
      },
    },
  };

  beforeEach(() => {
    setupTestBed(mockSublinesService, mockRoute);

    fixture = TestBed.createComponent(SublineDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('create new Version', () => {
    it('successfully', () => {
      spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
      mockSublinesService.createSublineVersion.and.returnValue(of(sublineVersion));
      fixture.componentInstance.createRecord();
      fixture.detectChanges();

      const snackBarContainer =
        fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
      expect(snackBarContainer).toBeDefined();
      expect(snackBarContainer.textContent).toBe('LIDI.SUBLINE.NOTIFICATION.ADD_SUCCESS');
      expect(snackBarContainer.classList).toContain('success');
      expect(router.navigate).toHaveBeenCalled();
    });

    it('displaying error', () => {
      const err = new Error('404');
      mockSublinesService.createSublineVersion.and.returnValue(throwError(() => err));
      fixture.componentInstance.createRecord();
      fixture.detectChanges();

      const snackBarContainer =
        fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
      expect(snackBarContainer).toBeDefined();
      expect(snackBarContainer.textContent).toBe('LIDI.SUBLINE.NOTIFICATION.ADD_ERROR');
      expect(snackBarContainer.classList).toContain('error');
    });
  });
});

function setupTestBed(
  sublinesService: SublinesService,
  activatedRoute: { snapshot: { data: { sublineDetail: string | SublineVersion } } }
) {
  TestBed.configureTestingModule({
    declarations: [SublineDetailComponent],
    imports: [
      CoreModule,
      RouterModule.forRoot([]),
      HttpClientTestingModule,
      BrowserAnimationsModule,
      TranslateModule.forRoot({
        loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
      }),
    ],
    providers: [
      { provide: FormBuilder },
      { provide: SublinesService, useValue: sublinesService },
      {
        provide: ActivatedRoute,
        useValue: activatedRoute,
      },
    ],
  })
    .compileComponents()
    .then();
}
