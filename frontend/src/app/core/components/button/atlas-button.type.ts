export const AtlasButtonType = {
  CREATE: 'create' as AtlasButtonType,
  CREATE_CHECKING_PERMISSION: 'createCheckingPermission' as AtlasButtonType,
  EDIT: 'edit' as AtlasButtonType,
  EDIT_SERVICE_POINT_DEPENDENT: 'edit-service-point-dependent' as AtlasButtonType,
  REVOKE: 'revoke' as AtlasButtonType,
  SKIP_WORKFLOW: 'skipworkflow' as AtlasButtonType,
  SUPERVISOR_BUTTON: 'supervisorButton' as AtlasButtonType,
  DELETE: 'delete' as AtlasButtonType,
  CLOSE_ICON: 'closeIcon' as AtlasButtonType,
  DEFAULT_PRIMARY: 'defaultPrimary' as AtlasButtonType,
  ICON: 'icon' as AtlasButtonType,
  WHITE_FOOTER_NON_EDIT: 'whiteFooterNonEdit' as AtlasButtonType,
  WHITE_FOOTER_EDIT_MODE: 'whiteFooterEdit' as AtlasButtonType,
  CANTON_WRITE_PERMISSION: 'cantonWritePermission' as AtlasButtonType,
  MANAGE_TIMETABLE_HEARING: 'manageTimetableHearing' as AtlasButtonType,
  CANCEL: 'cancel' as AtlasButtonType,
  CONFIRM: 'confirm' as AtlasButtonType,
};

export type AtlasButtonType =
  | 'createCheckingPermission'
  | 'create'
  | 'whiteFooterNonEdit'
  | 'whiteFooterEdit'
  | 'edit'
  | 'edit-service-point-dependent'
  | 'revoke'
  | 'skipworkflow'
  | 'supervisorButton'
  | 'delete'
  | 'defaultPrimary'
  | 'icon'
  | 'cantonWritePermission'
  | 'manageTimetableHearing'
  | 'cancel'
  | 'confirm';
