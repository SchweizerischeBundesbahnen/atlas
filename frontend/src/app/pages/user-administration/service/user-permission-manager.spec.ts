import { UserPermissionManager } from './user-permission-manager';
import { of } from 'rxjs';
import { fakeAsync, tick } from '@angular/core/testing';
import { Country, PermissionRestrictionType, SwissCanton } from '../../../api';

describe('UserPermissionManager', () => {
  let userPermissionManager: UserPermissionManager;
  const businessOrganisationMockService = jasmine.createSpyObj('BusinessOrganisationService', [
    'getAllBusinessOrganisations',
  ]);

  beforeEach(() => {
    userPermissionManager = new UserPermissionManager(businessOrganisationMockService);
    businessOrganisationMockService.getAllBusinessOrganisations.and.returnValue(
      of({ objects: [{ sboid: 'ch:1:sboid:test' }] }),
    );
  });

  it('test clearPermisRestrIfNotWriterAndRemoveBOPermisRestrIfSepodiAndSuperUser, setPermissions and getPermissions', fakeAsync(() => {
    userPermissionManager.setPermissions([
      {
        application: 'TTFN',
        role: 'WRITER',
        permissionRestrictions: [
          {
            valueAsString: 'ch:1:sboid:writer',
            type: PermissionRestrictionType.BusinessOrganisation,
          },
        ],
      },
      {
        application: 'LIDI',
        role: 'SUPER_USER',
        permissionRestrictions: [
          {
            valueAsString: 'ch:1:sboid:super_user',
            type: PermissionRestrictionType.BusinessOrganisation,
          },
        ],
      },
      {
        application: 'TIMETABLE_HEARING',
        role: 'WRITER',
        permissionRestrictions: [
          {
            valueAsString: SwissCanton.Aargau,
            type: PermissionRestrictionType.Canton,
          },
        ],
      },
      {
        application: 'SEPODI',
        role: 'SUPER_USER',
        permissionRestrictions: [
          {
            valueAsString: 'ch:1:sboid:super_user',
            type: PermissionRestrictionType.BusinessOrganisation,
          },
          {
            valueAsString: Country.Canada,
            type: PermissionRestrictionType.Country,
          },
        ],
      },
      {
        application: 'PRM',
        role: 'WRITER',
        permissionRestrictions: [
          {
            valueAsString: 'ch:5:sboid:writer',
            type: PermissionRestrictionType.BusinessOrganisation,
          },
        ],
      },
    ]);
    tick();
    userPermissionManager.clearPermisRestrIfNotWriterAndRemoveBOPermisRestrIfSepodiAndSuperUser();
    expect(userPermissionManager.userPermission.permissions[0].permissionRestrictions).toEqual([
      { valueAsString: 'ch:1:sboid:writer', type: PermissionRestrictionType.BusinessOrganisation },
    ]);
    expect(userPermissionManager.userPermission.permissions[1].permissionRestrictions).toEqual([]);
    expect(userPermissionManager.userPermission.permissions[3].permissionRestrictions).toEqual([
      { valueAsString: SwissCanton.Aargau, type: PermissionRestrictionType.Canton },
    ]);
    expect(userPermissionManager.userPermission.permissions[4].permissionRestrictions).toEqual([
      { valueAsString: Country.Canada, type: PermissionRestrictionType.Country },
    ]);
    expect(userPermissionManager.userPermission.permissions[5].permissionRestrictions).toEqual([
      { valueAsString: 'ch:5:sboid:writer', type: PermissionRestrictionType.BusinessOrganisation },
    ]);
  }));

  it('test getSbbUserId, setSbbUserId', () => {
    userPermissionManager.setSbbUserId('u236171');
    expect(userPermissionManager.getSbbUserId()).toEqual('u236171');
    expect(userPermissionManager.userPermission.sbbUserId).toEqual('u236171');
  });

  it('test getCurrentRole', () => {
    expect(userPermissionManager.getCurrentRole('TTFN')).toEqual('READER');
  });

  it('test changePermissionRole, getCurrentRole', () => {
    userPermissionManager.changePermissionRole('LIDI', 'SUPER_USER');
    expect(userPermissionManager.getCurrentRole('LIDI')).toEqual('SUPER_USER');
  });

  it('test removeSboidFromPermission', fakeAsync(() => {
    userPermissionManager.setPermissions([
      {
        application: 'TTFN',
        role: 'SUPER_USER',
        permissionRestrictions: [
          {
            valueAsString: 'ch:1:sboid:test',
            type: PermissionRestrictionType.BusinessOrganisation,
          },
        ],
      },
    ]);
    tick();
    userPermissionManager.removeSboidFromPermission('TTFN', 0);
    expect(userPermissionManager.userPermission.permissions[0].permissionRestrictions).toEqual([]);
  }));

  it('test addSboidToPermission', fakeAsync(() => {
    userPermissionManager.addSboidToPermission('TTFN', 'ch:1:sboid:100000');
    tick();
    expect(userPermissionManager.userPermission.permissions[0].permissionRestrictions).toEqual([
      { valueAsString: 'ch:1:sboid:100000', type: PermissionRestrictionType.BusinessOrganisation },
    ]);
  }));
});
