import { UserPermissionManager } from './user-permission-manager';
import { of } from 'rxjs';
import { fakeAsync, tick } from '@angular/core/testing';
import { PermissionRestrictionType } from '../../../api';

describe('UserPermissionManager', () => {
  let userPermissionManager: UserPermissionManager;
  const businessOrganisationMockService = jasmine.createSpyObj('BusinessOrganisationService', [
    'getAllBusinessOrganisations',
  ]);

  beforeEach(() => {
    userPermissionManager = new UserPermissionManager(businessOrganisationMockService);
    businessOrganisationMockService.getAllBusinessOrganisations.and.returnValue(
      of({ objects: [{ sboid: 'ch:1:sboid:test' }] })
    );
  });

  it('test clearSboidsIfNotWriter, setPermissions and getPermissions', fakeAsync(() => {
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
    ]);
    tick();
    userPermissionManager.clearPermissionRestrictionsIfNotWriter();
    expect(userPermissionManager.userPermission.permissions[0].permissionRestrictions).toEqual([
      { valueAsString: 'ch:1:sboid:writer', type: PermissionRestrictionType.BusinessOrganisation },
    ]);
    expect(userPermissionManager.userPermission.permissions[1].permissionRestrictions).toEqual([]);
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
