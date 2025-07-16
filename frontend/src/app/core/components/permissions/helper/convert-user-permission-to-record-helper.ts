import moment from 'moment';
import { Permission } from '../../../../api';
import { CreationEditionRecord } from '../../base-detail/user-edit-info/creation-edition-record';

export class ConvertUserPermissionToRecordHelper {
  static convertUserPermissionToRecord(
    permissions: Permission[]
  ): CreationEditionRecord {
    const permissionsArray = Array.from(permissions || []);

    if (permissionsArray.length === 0) {
      return {};
    }

    let firstCreated = permissionsArray[0];
    let lastEdited = permissionsArray[0];

    for (let i = 1; i < permissionsArray.length; i++) {
      const current = permissionsArray[i];

      if (
        moment(new Date(current.creationDate!)).isBefore(
          moment(new Date(firstCreated.creationDate!))
        )
      ) {
        firstCreated = current;
      }

      if (
        moment(new Date(current.editionDate!)).isAfter(
          moment(new Date(lastEdited.editionDate!))
        )
      ) {
        lastEdited = current;
      }
    }

    return {
      editor: lastEdited.editor,
      editionDate: lastEdited.editionDate,
      creator: firstCreated.creator,
      creationDate: firstCreated.creationDate,
    };
  }
}
