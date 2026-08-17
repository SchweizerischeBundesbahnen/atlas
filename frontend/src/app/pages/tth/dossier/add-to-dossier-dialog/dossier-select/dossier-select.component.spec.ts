import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { DossierSelectComponent } from './dossier-select.component';
import { firstValueFrom, of } from 'rxjs';
import { FormControl, FormGroup } from '@angular/forms';
import { DossierInternalService } from '../../../../../api/service/workflow/dossier-internal.service';
import { ContainerTthDossier } from '../../../../../api/model/containerTthDossier';
import { SwissCanton } from '../../../../../api';
import { DossierStatus } from '../../../../../api/model/dossierStatus';
import { translateServiceProvider } from '../../../../../app.testing.mocks';

describe('DossierSelectComponent', () => {
  let component: DossierSelectComponent;
  let fixture: ComponentFixture<DossierSelectComponent>;

  const dossierInternalService: Mocked<Pick<DossierInternalService, 'getOverview'>> = {
    getOverview: vi.fn(),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DossierSelectComponent],
      providers: [
        translateServiceProvider,
        {
          provide: DossierInternalService,
          useValue: dossierInternalService,
        },
      ],
    });

    fixture = TestBed.createComponent(DossierSelectComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('canton', SwissCanton.Bern);
    fixture.componentRef.setInput('statusRestriction', [DossierStatus.Added]);
    fixture.componentRef.setInput(
      'form',
      new FormGroup({
        dossier: new FormControl(),
      })
    );
    fixture.detectChanges();
  });

  it('should search for dossier', async () => {
    const searchResult: ContainerTthDossier = {
      objects: [
        {
          id: 1,
          topic: 'Dossier 1',
          swissCanton: SwissCanton.Bern,
          statementIds: [],
          questions: [],
        },
      ],
    };
    dossierInternalService.getOverview.mockReturnValue(of(searchResult));
    fixture.componentRef.setInput('year', 2026);
    fixture.detectChanges();
    component.search('testQuery');
    fixture.detectChanges();
    expect(dossierInternalService.getOverview).toHaveBeenCalledTimes(1);
    expect(dossierInternalService.getOverview).toHaveBeenCalledWith(
      2026,
      SwissCanton.Bern,
      undefined,
      ['testQuery'],
      [DossierStatus.Added]
    );
    const val = await firstValueFrom(component.searchResults$);
    expect(val).toEqual(searchResult.objects!);
  });
});
