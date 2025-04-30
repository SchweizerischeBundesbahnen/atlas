import { TestBed } from '@angular/core/testing';
import { TranslateModule } from '@ngx-translate/core';
import { ReferencePointCreationHintService } from './reference-point-creation-hint.service';
import { DialogService } from '../../../../../../core/components/dialog/dialog.service';
import { of } from 'rxjs';
import { Router } from '@angular/router';

const dialogService = jasmine.createSpyObj('dialogService', ['confirm']);

describe('ReferencePointCreationHintService', () => {
  let referencePointCreationHintService: ReferencePointCreationHintService;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [{ provide: DialogService, useValue: dialogService }],
    });
    referencePointCreationHintService = TestBed.inject(
      ReferencePointCreationHintService
    );
    router = TestBed.inject(Router);
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
  });

  it('should route to new reference point on confirmation', () => {
    dialogService.confirm.and.returnValue(of(true));

    referencePointCreationHintService.showHint();
    expect(router.navigate).toHaveBeenCalled();
  });

  it('should do nothing on cancel', () => {
    dialogService.confirm.and.returnValue(of(false));

    referencePointCreationHintService.showHint();
    expect(router.navigate).not.toHaveBeenCalled();
  });
});
