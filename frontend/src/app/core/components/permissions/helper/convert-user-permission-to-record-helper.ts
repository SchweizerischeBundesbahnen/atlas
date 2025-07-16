import moment from 'moment';
import { Permission } from '../../../../api';
import { CreationEditionRecord } from '../../base-detail/user-edit-info/creation-edition-record';

export class ConvertUserPermissionToRecordHelper {
  static convertUserPermissionToRecord(
    permissions: Permission[]
  ): CreationEditionRecord {
    const permissionsFromUserModelAsArray = Array.from(permissions!);

    if (permissionsFromUserModelAsArray.length > 0) {
      const firstCreated = permissionsFromUserModelAsArray.reduce(
        (previousValue, currentValue) => {
          return moment(new Date(previousValue.creationDate!)).isBefore(
            moment(new Date(currentValue.creationDate!))
          )
            ? previousValue
            : currentValue;
        }
      );
      const lastEdited = permissionsFromUserModelAsArray.reduce(
        (previousValue, currentValue) => {
          return moment(new Date(previousValue.editionDate!)).isAfter(
            moment(new Date(currentValue.editionDate!))
          )
            ? previousValue
            : currentValue;
        }
      );
      return {
        editor: lastEdited.editor,
        editionDate: lastEdited.editionDate,
        creator: firstCreated.creator,
        creationDate: firstCreated.creationDate,
      };
    }
    return {};
  }
}
