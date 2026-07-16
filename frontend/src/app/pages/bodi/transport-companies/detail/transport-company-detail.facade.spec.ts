import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { firstValueFrom, of } from 'rxjs';
import moment from 'moment';
import { TransportCompanyBoRelation } from '../../../../api';
import { TransportCompanyRelationInternalService } from '../../../../api/service/bodi/transport-company-relation-internal.service';
import { TransportCompanyRelationFormModelValidated } from './transport-company-detail.component';
import { TransportCompanyDetailFacade } from './transport-company-detail.facade';

describe('TransportCompanyDetailFacade', () => {
  let facade: TransportCompanyDetailFacade;
  let transportCompanyRelationInternalService: Mocked<
    Pick<
      TransportCompanyRelationInternalService,
      | 'createTransportCompanyRelation'
      | 'updateTransportCompanyRelation'
      | 'deleteTransportCompanyRelation'
      | 'getTransportCompanyBoRelations'
    >
  >;

  const initialRelations: TransportCompanyBoRelation[] = [{ id: 11 }, { id: 22 }];
  const reloadedRelations: TransportCompanyBoRelation[] = [{ id: 33 }, { id: 44 }];

  const validatedForm: TransportCompanyRelationFormModelValidated = {
    transportCompanyId: 1234,
    businessOrganisation: {
      sboid: 'SBO-4711',
    } as TransportCompanyRelationFormModelValidated['businessOrganisation'],
    validFrom: moment('2026-01-01'),
    validTo: moment('2026-12-31'),
  };

  beforeEach(() => {
    transportCompanyRelationInternalService = {
      createTransportCompanyRelation: vi.fn(),
      updateTransportCompanyRelation: vi.fn(),
      deleteTransportCompanyRelation: vi.fn(),
      getTransportCompanyBoRelations: vi.fn(),
    };

    transportCompanyRelationInternalService.createTransportCompanyRelation.mockReturnValue(of({ id: 99 }));
    transportCompanyRelationInternalService.updateTransportCompanyRelation.mockReturnValue(of(undefined));
    transportCompanyRelationInternalService.deleteTransportCompanyRelation.mockReturnValue(of(undefined));
    transportCompanyRelationInternalService.getTransportCompanyBoRelations.mockReturnValue(of(reloadedRelations));

    TestBed.configureTestingModule({
      providers: [
        TransportCompanyDetailFacade,
        { provide: TransportCompanyRelationInternalService, useValue: transportCompanyRelationInternalService },
      ],
    });

    facade = TestBed.inject(TransportCompanyDetailFacade);
    facade.init(initialRelations, 777);
  });

  it('should expose initialized relations and default state', () => {
    expect(facade.transportCompanyRelationsReadonly()).toEqual(initialRelations);
    expect(facade.isRelationSelected()).toBe(false);
    expect(facade.selectedRelation()).toBeNull();
    expect(facade.selectedRelationIndex()).toBe(-1);
    expect(facade.isEditMode()).toBe(false);
  });

  it('should select relation by index and expose computed selection', () => {
    facade.selectRelation(1);

    expect(facade.isRelationSelected()).toBe(true);
    expect(facade.selectedRelation()?.id).toBe(22);
    expect(facade.selectedRelationIndex()).toBe(1);
  });

  it('should toggle and leave edit mode', () => {
    facade.toggleEditMode();
    expect(facade.isEditMode()).toBe(true);

    facade.leaveEditMode();
    expect(facade.isEditMode()).toBe(false);
  });

  it('should create relation on save when no relation is selected and reset state after reload', async () => {
    facade.toggleEditMode();

    await firstValueFrom(facade.save(validatedForm));

    expect(transportCompanyRelationInternalService.createTransportCompanyRelation).toHaveBeenCalledExactlyOnceWith({
      transportCompanyId: 1234,
      sboid: 'SBO-4711',
      validFrom: validatedForm.validFrom.toDate(),
      validTo: validatedForm.validTo.toDate(),
    });
    expect(transportCompanyRelationInternalService.updateTransportCompanyRelation).not.toHaveBeenCalled();
    expect(transportCompanyRelationInternalService.getTransportCompanyBoRelations).toHaveBeenCalledExactlyOnceWith(777);
    expect(facade.transportCompanyRelationsReadonly()).toEqual(reloadedRelations);
    expect(facade.isEditMode()).toBe(false);
    expect(facade.isRelationSelected()).toBe(false);
  });

  it('should update selected relation on save and reset selection after reload', async () => {
    facade.selectRelation(0);

    await firstValueFrom(facade.save(validatedForm));

    expect(transportCompanyRelationInternalService.updateTransportCompanyRelation).toHaveBeenCalledExactlyOnceWith({
      id: 11,
      validFrom: validatedForm.validFrom.toDate(),
      validTo: validatedForm.validTo.toDate(),
    });
    expect(transportCompanyRelationInternalService.createTransportCompanyRelation).not.toHaveBeenCalled();
    expect(transportCompanyRelationInternalService.getTransportCompanyBoRelations).toHaveBeenCalledExactlyOnceWith(777);
    expect(facade.isRelationSelected()).toBe(false);
    expect(facade.selectedRelation()).toBeNull();
  });

  it('should delete selected relation and reload relations', async () => {
    facade.selectRelation(1);

    await firstValueFrom(facade.deleteRelation());

    expect(transportCompanyRelationInternalService.deleteTransportCompanyRelation).toHaveBeenCalledExactlyOnceWith(22);
    expect(transportCompanyRelationInternalService.getTransportCompanyBoRelations).toHaveBeenCalledExactlyOnceWith(777);
    expect(facade.transportCompanyRelationsReadonly()).toEqual(reloadedRelations);
    expect(facade.isRelationSelected()).toBe(false);
  });

  it('should throw when deleting without a selected relation', () => {
    expect(() => facade.deleteRelation()).toThrow('No relation selected');
    expect(transportCompanyRelationInternalService.deleteTransportCompanyRelation).not.toHaveBeenCalled();
  });
});
