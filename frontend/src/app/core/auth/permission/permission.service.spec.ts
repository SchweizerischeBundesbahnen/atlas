import { beforeEach, describe, expect, it } from 'vitest';
import { PermissionService } from './permission.service';
import {
  ApplicationRole,
  ApplicationType,
  CantonPermissionRestrictionModel,
  PermissionRestrictionType,
} from '../../../api';
import { TerminationDecision } from '../../../api/model/terminationDecision';
import { TestBed } from '@angular/core/testing';
import { UserService } from '../user/user.service';
import TerminationDecisionPersonEnum = TerminationDecision.TerminationDecisionPersonEnum;

type UserServiceMock = Pick<
  { -readonly [P in keyof UserService]: UserService[P] },
  'permissions' | 'isAdmin'
>;

describe('PermissionService', () => {
  describe('Permissions for create Button', () => {
    it('Permissions for create Button BODI are set up correctly', () => {
      let result = PermissionService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Bodi,
        [],
        true
      );
      expect(result).toBe(true);

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
      expect(result).toBe(false);

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
      expect(result).toBe(true);

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
      expect(result).toBe(false);

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
      expect(result).toBe(false);
    });

    it('Permissions for create Button LIDI are set up correctly', () => {
      let result = PermissionService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Lidi,
        [],
        true
      );
      expect(result).toBe(true);

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
      expect(result).toBe(true);

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
      expect(result).toBe(true);

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
      expect(result).toBe(true);

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
      expect(result).toBe(false);
    });

    it('Permissions for create Button TTFN are set up correctly', () => {
      let result = PermissionService.hasPermissionsToCreateWithPermissions(
        ApplicationType.Ttfn,
        [],
        true
      );
      expect(result).toBe(true);

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
      expect(result).toBe(true);

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
      expect(result).toBe(true);

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
      expect(result).toBe(true);

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
      expect(result).toBe(false);
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
      expect(result).toBe(true);

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
      expect(result).toBe(true);

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
      expect(result).toBe(true);

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
      expect(result).toBe(false);

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
      expect(result).toBe(true);
    });
  });

  describe('Permission for edit TTH Canton', () => {
    it('should be able to edit Canton if user is for canton enabled', () => {
      const cantonRestriction: CantonPermissionRestrictionModel[] = [];
      cantonRestriction.push({ type: 'CANTON', valueAsString: 'BERN' });
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
      expect(result).toBe(true);
    });

    it('should not be able to edit Canton if user is not for canton enabled', () => {
      const cantonRestriction: CantonPermissionRestrictionModel[] = [];
      cantonRestriction.push({ type: 'CANTON', valueAsString: 'BERN' });
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
      expect(result).toBe(true);
    });
  });

  describe('Available Pages based on permissions', () => {
    let permissionService: PermissionService;

    let userServiceMock: UserServiceMock;

    beforeEach(() => {
      userServiceMock = {
        isAdmin: false,
        permissions: [],
      };

      TestBed.configureTestingModule({
        providers: [
          {
            provide: UserService,
            useValue: userServiceMock,
          },
        ],
      });

      permissionService = TestBed.inject(PermissionService);
    });

    it('should show TTFN if supervisor', () => {
      userServiceMock.permissions = [
        {
          application: ApplicationType.Ttfn,
          role: ApplicationRole.Supervisor,
          permissionRestrictions: [],
        },
      ];

      const mayAccessTtfn = permissionService.mayAccessTtfn();
      expect(mayAccessTtfn).toBe(true);
    });

    it('should not show TTFN if reader', () => {
      userServiceMock.permissions = [
        {
          application: ApplicationType.Ttfn,
          role: ApplicationRole.Reader,
          permissionRestrictions: [],
        },
      ];

      const mayAccessTtfn = permissionService.mayAccessTtfn();
      expect(mayAccessTtfn).toBe(false);
    });

    it('should show Bulk Import if supervisor', () => {
      userServiceMock.permissions = [
        {
          application: ApplicationType.Sepodi,
          role: ApplicationRole.Supervisor,
          permissionRestrictions: [],
        },
      ];

      const mayAccessTtfn = permissionService.mayAccessBulkImport();
      expect(mayAccessTtfn).toBe(true);
    });

    it('should not show Bulk Import if reader', () => {
      userServiceMock.permissions = [
        {
          application: ApplicationType.Sepodi,
          role: ApplicationRole.Reader,
          permissionRestrictions: [],
        },
      ];

      const mayAccessTtfn = permissionService.mayAccessBulkImport();
      expect(mayAccessTtfn).toBe(false);
    });

    it('should show TTH if explicit reader', () => {
      userServiceMock.permissions = [
        {
          application: ApplicationType.TimetableHearing,
          role: ApplicationRole.ExplicitReader,
          permissionRestrictions: [],
        },
      ];

      const mayAccessTth = permissionService.mayAccessTimetableHearing();
      expect(mayAccessTth).toBe(true);
    });

    it('should not show TTH if reader', () => {
      userServiceMock.permissions = [
        {
          application: ApplicationType.TimetableHearing,
          role: ApplicationRole.Reader,
          permissionRestrictions: [],
        },
      ];

      const mayAccessTth = permissionService.mayAccessTimetableHearing();
      expect(mayAccessTth).toBe(false);
    });

    it('should show TTH if reader with special restriction', () => {
      userServiceMock.permissions = [
        {
          application: ApplicationType.TimetableHearing,
          role: ApplicationRole.Reader,
          permissionRestrictions: [
            {
              type: PermissionRestrictionType.TransportCompanyDossierAnswer,
              valueAsString: 'true',
            },
          ],
        },
      ];

      const mayAccessTth = permissionService.mayAccessTimetableHearing();
      expect(mayAccessTth).toBe(true);
    });

    it('should evaluate at least supervisor', () => {
      userServiceMock.permissions = [
        {
          application: ApplicationType.Ttfn,
          role: ApplicationRole.Supervisor,
          permissionRestrictions: [],
        },
      ];

      const ttfnSupervisor = permissionService.isAtLeastSupervisor(
        ApplicationType.Ttfn
      );
      expect(ttfnSupervisor).toBe(true);
    });
  });

  describe('Permissions for termination', () => {
    let permissionService: PermissionService;

    let userServiceMock: UserServiceMock;

    beforeEach(() => {
      userServiceMock = {
        isAdmin: false,
        permissions: [],
      };

      TestBed.configureTestingModule({
        providers: [
          {
            provide: UserService,
            useValue: userServiceMock,
          },
        ],
      });

      permissionService = TestBed.inject(PermissionService);
    });

    it('should have no termination permission', () => {
      userServiceMock.permissions = [];
      const terminationPermission =
        permissionService.getTerminationPermission();
      expect(terminationPermission).toBeUndefined();
    });

    it('should get infoplus termination permission', () => {
      userServiceMock.permissions = [
        {
          application: ApplicationType.Sepodi,
          role: ApplicationRole.Reader,
          permissionRestrictions: [
            {
              type: PermissionRestrictionType.InfoPlusTerminationVote,
              valueAsString: 'true',
            },
          ],
        },
      ];

      const terminationPermission =
        permissionService.getTerminationPermission();
      expect(terminationPermission).toEqual(
        TerminationDecisionPersonEnum.InfoPlus
      );
    });

    it('should get nova termination permission', () => {
      userServiceMock.permissions = [
        {
          application: ApplicationType.Sepodi,
          role: ApplicationRole.Reader,
          permissionRestrictions: [
            {
              type: PermissionRestrictionType.InfoPlusTerminationVote,
              valueAsString: 'false',
            },
            {
              type: PermissionRestrictionType.NovaTerminationVote,
              valueAsString: 'true',
            },
          ],
        },
      ];

      const terminationPermission =
        permissionService.getTerminationPermission();
      expect(terminationPermission).toEqual(TerminationDecisionPersonEnum.Nova);
    });
  });

  describe('Permission for TTH BO', () => {
    let permissionService: PermissionService;
    let userServiceMock: UserServiceMock;

    beforeEach(() => {
      userServiceMock = {
        isAdmin: false,
        permissions: [],
      };

      TestBed.configureTestingModule({
        providers: [
          {
            provide: UserService,
            useValue: userServiceMock,
          },
        ],
      });

      permissionService = TestBed.inject(PermissionService);
    });

    it('should get tth bo permission true', () => {
      //given
      userServiceMock.permissions = [
        {
          application: ApplicationType.TimetableHearing,
          role: ApplicationRole.Reader,
          permissionRestrictions: [
            {
              type: PermissionRestrictionType.TransportCompanyDossierAnswer,
              valueAsString: 'true',
            },
          ],
        },
      ];
      //when
      const isTthBoUser = permissionService.isTthBoUser();
      const isCanton = permissionService.isTthCanton();
      //then
      expect(isTthBoUser).toBe(true);
      expect(isCanton).toBe(false);
    });

    it('should get tth bo permission false', () => {
      //given
      userServiceMock.permissions = [
        {
          application: ApplicationType.TimetableHearing,
          role: ApplicationRole.Reader,
          permissionRestrictions: [
            {
              type: PermissionRestrictionType.TransportCompanyDossierAnswer,
              valueAsString: 'false',
            },
          ],
        },
      ];

      const isTthBoUser = permissionService.isTthBoUser();
      expect(isTthBoUser).toBe(false);
    });

    it('should get tth Canton Application User', () => {
      //given
      userServiceMock.permissions = [
        {
          application: ApplicationType.TimetableHearing,
          role: ApplicationRole.Reader,
          permissionRestrictions: [
            {
              type: PermissionRestrictionType.TransportCompanyDossierAnswer,
              valueAsString: 'true',
            },
          ],
        },
      ];
      //when
      const result = permissionService.getTthApplicationUserType();
      //then
      expect(result).toBe('BO_TTH');
    });

    it('should get tth Canton Application User', () => {
      //given
      userServiceMock.permissions = [
        {
          application: ApplicationType.TimetableHearing,
          role: ApplicationRole.ExplicitReader,
          permissionRestrictions: [
            {
              type: PermissionRestrictionType.TransportCompanyDossierAnswer,
              valueAsString: 'false',
            },
          ],
        },
      ];
      //when
      const result = permissionService.getTthApplicationUserType();
      //then
      expect(result).toBe('CANTON_TTH');
    });
  });
});
