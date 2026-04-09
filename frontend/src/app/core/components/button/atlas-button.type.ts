export const AtlasButtonType = {
  CREATE: 'create',
  CREATE_CHECKING_PERMISSION: 'createCheckingPermission',
  EDIT: 'edit',
  EDIT_SERVICE_POINT_DEPENDENT: 'edit-service-point-dependent',
  REVOKE: 'revoke',
  SKIP_WORKFLOW: 'skipworkflow',
  SUPERVISOR_BUTTON: 'supervisorButton',
  DELETE: 'delete',
  CLOSE_ICON: 'closeIcon',
  DEFAULT_PRIMARY: 'defaultPrimary',
  ICON: 'icon',
  WHITE_FOOTER_NON_EDIT: 'whiteFooterNonEdit',
  WHITE_FOOTER_EDIT_MODE: 'whiteFooterEdit',
  CANTON_WRITE_PERMISSION: 'cantonWritePermission',
  MANAGE_TIMETABLE_HEARING: 'manageTimetableHearing',
  CANCEL: 'cancel',
  CONFIRM: 'confirm',
};

export type AtlasButtonType =
  (typeof AtlasButtonType)[keyof typeof AtlasButtonType];
