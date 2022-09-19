import { UserPermissionManager } from './user-permission-manager';
import { of } from 'rxjs';

describe('UserPermissionManager', () => {
  let userPermissionManager: UserPermissionManager;
  const businessOrganisationMockService = jasmine.createSpyObj('BusinessOrganisationService', [
    'getAllBusinessOrganisations',
  ]);

  beforeEach(() => {
    userPermissionManager = new UserPermissionManager(businessOrganisationMockService);
  });

  it('test clearSboidsIfNotWriter, setPermissions and getPermissions', () => {
    userPermissionManager.setPermissions([
      {
        application: 'TTFN',
        role: 'WRITER',
        sboids: ['ch:1:sboid:writer'],
      },
      {
        application: 'LIDI',
        role: 'SUPER_USER',
        sboids: ['ch:1:sboid:super_user'],
      },
    ]);

    userPermissionManager.clearSboidsIfNotWriter();

    expect(userPermissionManager.getPermissions()[0].sboids).toEqual(['ch:1:sboid:writer']);
    expect(userPermissionManager.getPermissions()[1].sboids).toEqual([]);
  });

  it('test getSbbUserId, setSbbUserId, getUserPermission', () => {
    userPermissionManager.setSbbUserId('u236171');
    expect(userPermissionManager.getSbbUserId()).toEqual('u236171');
    expect(userPermissionManager.getUserPermission().sbbUserId).toEqual('u236171');
  });

  it('test getCurrentRole', () => {
    expect(userPermissionManager.getCurrentRole('TTFN')).toEqual('WRITER');
  });

  it('test changePermissionRole, getCurrentRole', () => {
    userPermissionManager.changePermissionRole('LIDI', 'SUPER_USER');
    expect(userPermissionManager.getCurrentRole('LIDI')).toEqual('SUPER_USER');
  });

  it('test removeSboidFromPermission', () => {
    userPermissionManager.setPermissions([
      {
        application: 'TTFN',
        role: 'SUPER_USER',
        sboids: ['ch:1:sboid:test'],
      },
    ]);
    userPermissionManager.removeSboidFromPermission('TTFN', 0);
    expect(userPermissionManager.getPermissions()[0].sboids).toEqual([]);
  });

  it('test addSboidToPermission', () => {
    businessOrganisationMockService.getAllBusinessOrganisations.and.returnValue(
      of({ objects: [{ sboid: 'ch:1:sboid:test' }] })
    );

    userPermissionManager.addSboidToPermission('TTFN', 'ch:1:sboid:100000');
    expect(userPermissionManager.getPermissions()[0].sboids).toEqual(['ch:1:sboid:100000']);
  });
});
