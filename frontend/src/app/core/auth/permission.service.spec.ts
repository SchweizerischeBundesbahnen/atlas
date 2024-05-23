import {ApplicationRole, ApplicationType, CantonPermissionRestrictionModel, PermissionRestrictionType,} from '../../api';
import {PermissionService} from "./permission.service";

describe('PermissionService', () => {

  describe('Permissions for create Button', () => {
    it('Permissions for create Button BODI are set up correctly', () => {
      let result = PermissionService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Bodi,
        [],
        true
      );
      expect(result).toBeTrue();

      result = PermissionService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Bodi,
        [
          {
            application: ApplicationType.Bodi,
            role: ApplicationRole.SuperUser,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeFalse();

      result = PermissionService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Bodi,
        [
          {
            application: ApplicationType.Bodi,
            role: ApplicationRole.Supervisor,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeTrue();

      result = PermissionService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Bodi,
        [
          {
            application: ApplicationType.Bodi,
            role: ApplicationRole.Writer,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeFalse();

      result = PermissionService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Bodi,
        [
          {
            application: ApplicationType.Bodi,
            role: ApplicationRole.Reader,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeFalse();
    });

    it('Permissions for create Button LIDI are set up correctly', () => {
      let result = PermissionService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Lidi,
        [],
        true
      );
      expect(result).toBeTrue();

      result = PermissionService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Lidi,
        [
          {
            application: ApplicationType.Lidi,
            role: ApplicationRole.SuperUser,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeTrue();

      result = PermissionService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Lidi,
        [
          {
            application: ApplicationType.Lidi,
            role: ApplicationRole.Supervisor,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeTrue();

      result = PermissionService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Lidi,
        [
          {
            application: ApplicationType.Lidi,
            role: ApplicationRole.Writer,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeTrue();

      result = PermissionService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Lidi,
        [
          {
            application: ApplicationType.Lidi,
            role: ApplicationRole.Reader,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeFalse();
    });

    it('Permissions for create Button TTFN are set up correctly', () => {
      let result = PermissionService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Ttfn,
        [],
        true
      );
      expect(result).toBeTrue();

      result = PermissionService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Ttfn,
        [
          {
            application: ApplicationType.Ttfn,
            role: ApplicationRole.SuperUser,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeTrue();

      result = PermissionService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Ttfn,
        [
          {
            application: ApplicationType.Ttfn,
            role: ApplicationRole.Supervisor,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeTrue();

      result = PermissionService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Ttfn,
        [
          {
            application: ApplicationType.Ttfn,
            role: ApplicationRole.Writer,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeTrue();

      result = PermissionService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Ttfn,
        [
          {
            application: ApplicationType.Ttfn,
            role: ApplicationRole.Reader,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeFalse();
    });
  });

  describe('Permissions for edit Button', () => {
    it('LIDI setup correctly', () => {
      let result = PermissionService.hasPermissionsToWriteWithPermissions(
        ApplicationType.Lidi,
        'ch:1:slnid:1000004',
        [],
        true
      );
      expect(result).toBeTrue();

      result = PermissionService.hasPermissionsToWriteWithPermissions(
        ApplicationType.Lidi,
        'ch:1:slnid:1000004',
        [
          {
            application: ApplicationType.Lidi,
            role: ApplicationRole.Supervisor,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeTrue();

      result = PermissionService.hasPermissionsToWriteWithPermissions(
        ApplicationType.Lidi,
        'ch:1:slnid:1000004',
        [
          {
            application: ApplicationType.Lidi,
            role: ApplicationRole.SuperUser,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeTrue();

      result = PermissionService.hasPermissionsToWriteWithPermissions(
        ApplicationType.Lidi,
        'ch:1:slnid:1000004',
        [
          {
            application: ApplicationType.Lidi,
            role: ApplicationRole.Writer,
            permissionRestrictions: [],
          },
        ],
        false
      );
      expect(result).toBeFalse();

      result = PermissionService.hasPermissionsToWriteWithPermissions(
        ApplicationType.Lidi,
        'ch:1:slnid:1000004',
        [
          {
            application: ApplicationType.Lidi,
            role: ApplicationRole.Writer,
            permissionRestrictions: [
              {
                valueAsString: 'ch:1:slnid:1000004',
                type: PermissionRestrictionType.BusinessOrganisation,
              },
            ],
          },
        ],
        false
      );
      expect(result).toBeTrue();
    });
  });

  describe('Permission for edit TTH Canton', () => {
    it('should be able to edit Canton if user is for canton enabled', () => {
      const cantonRestriction: CantonPermissionRestrictionModel[] = [];
      cantonRestriction.push({type: 'CANTON', valueAsString: 'BERN'});
      const result = PermissionService.hasPermissionToWriteOnCanton(
        ApplicationType.TimetableHearing,
        'be',
        [
          {
            application: ApplicationType.TimetableHearing,
            role: ApplicationRole.Writer,
            permissionRestrictions: cantonRestriction,
          },
        ],
        false
      );
      expect(result).toBeTrue();
    });

    it('should not be able to edit Canton if user is not for canton enabled', () => {
      const cantonRestriction: CantonPermissionRestrictionModel[] = [];
      cantonRestriction.push({type: 'CANTON', valueAsString: 'BERN'});
      const result = PermissionService.hasPermissionToWriteOnCanton(
        ApplicationType.TimetableHearing,
        'zh',
        [
          {
            application: ApplicationType.TimetableHearing,
            role: ApplicationRole.Writer,
            permissionRestrictions: cantonRestriction,
          },
        ],
        false
      );
      expect(result).toBeFalsy();
    });

    it('should be able to edit Canton if user is admin', () => {
      const result = PermissionService.hasPermissionToWriteOnCanton(
        ApplicationType.TimetableHearing,
        'be',
        [],
        true
      );
      expect(result).toBeTrue();
    });
  });

  describe('Available Pages based on permissions', () => {

    let permissionService: PermissionService;
    const userServiceMock = jasmine.createSpyObj({}, {isAdmin: false});

    beforeEach(() => {
      permissionService = new PermissionService(userServiceMock);
    });

    it('should show TTFN if supervisor', () => {
      userServiceMock.permissions = [{
        application: ApplicationType.Ttfn,
        role: ApplicationRole.Supervisor,
        permissionRestrictions: []
      }];

      const mayAccessTtfn = permissionService.mayAccessTtfn();
      expect(mayAccessTtfn).toBeTrue();
    });

    it('should not show TTFN if reader', () => {
      userServiceMock.permissions = [{
        application: ApplicationType.Ttfn,
        role: ApplicationRole.Reader,
        permissionRestrictions: []
      }];

      const mayAccessTtfn = permissionService.mayAccessTtfn();
      expect(mayAccessTtfn).toBeFalse();
    });

    it('should show TTH if explicit reader', () => {
      userServiceMock.permissions = [{
        application: ApplicationType.TimetableHearing,
        role: ApplicationRole.ExplicitReader,
        permissionRestrictions: []
      }];

      const mayAccessTth = permissionService.mayAccessTimetableHearing();
      expect(mayAccessTth).toBeTrue();
    });

    it('should not show TTH if reader', () => {
      userServiceMock.permissions = [{
        application: ApplicationType.TimetableHearing,
        role: ApplicationRole.Reader,
        permissionRestrictions: []
      }];

      const mayAccessTth = permissionService.mayAccessTimetableHearing();
      expect(mayAccessTth).toBeFalse();
    });

    it('should evaluate at least supervisor', () => {
      userServiceMock.permissions = [{
        application: ApplicationType.Ttfn,
        role: ApplicationRole.Supervisor,
        permissionRestrictions: []
      }];

      const ttfnSupervisor = permissionService.isAtLeastSupervisor(ApplicationType.Ttfn);
      expect(ttfnSupervisor).toBeTrue();
    });
  });
});
