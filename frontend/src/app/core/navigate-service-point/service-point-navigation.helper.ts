import { SloidLocationModel } from '../../api/model/sloidLocationModel';
import { SloidHelper } from '../util/sloidHelper';
import { PRM_NAVIGATION, SEPODI_NAVIAGATION, SloidContainer } from './service-point-navigation';

export class ServicePointNavigationHelper {
  static buildPrmNavigation(model: SloidLocationModel, rootNavigationPath: string): string[] {
    const commands: string[] = [];
    const navigation = PRM_NAVIGATION.get(model.sloidType!);
    const sloidContainer = this.buildSloidContainer(model);

    if (!navigation && model.sloidType === 'SERVICE_POINT') {
      commands.push(rootNavigationPath);
      commands.push(sloidContainer.rootSloid);
      //e.g. {host}/prm-directory/{sloid}
      return commands;
    }
    if (!navigation && model.sloidType !== 'SERVICE_POINT') {
      return commands;
    }

    commands.push(navigation!.rootPath);
    commands.push(sloidContainer.rootSloid);

    commands.push(navigation!.path);
    commands.push(sloidContainer.sloid);
    if (navigation?.suffixDetail) {
      commands.push(navigation.suffixDetail);
    }
    //e.g. {host}/prm-directory/{rootSloid}/{platforms/{sloid}/detail
    return commands;
  }

  static buildSepodiNavigation(model: SloidLocationModel, rootNavigationPath: string): string[] {
    const commands: string[] = [];
    const navigation = SEPODI_NAVIAGATION.get(model.sloidType!);
    const sloidContainer = this.buildSloidContainer(model);

    if (!navigation && model.sloidType === 'SERVICE_POINT') {
      commands.push(rootNavigationPath);
      commands.push(this.buildServicePointNumber(sloidContainer));
      //e.g. {host}/service-point-directory/service-points/{servicePointNumber}/service-point
      return commands;
    }
    if (!navigation && model.sloidType !== 'SERVICE_POINT') {
      return commands;
    }

    commands.push(navigation!.rootPath);
    commands.push(this.buildServicePointNumber(sloidContainer));
    if (model.sloidType === 'PLATFORM' || model.sloidType === 'AREA') {
      commands.push(navigation!.path);
      commands.push(sloidContainer.sloid);
      //e.g. {host}/service-point-directory/service-points/{servicePointNumber}/service-point/areas/{sloid}
    }
    if (model.sloidType === 'SECTOR' || model.sloidType === 'SECTOR_GROUP') {
      commands.push(navigation!.parentPath!);
      commands.push(sloidContainer.parentSloid!);
      commands.push(navigation!.path);
      commands.push(sloidContainer.sloid);
      //e.g. {host}/service-point-directory/service-points/{servicePointNumber}/traffic-point-elements/{parentSloid}/sectors/sloid
    }
    return commands;
  }

  private static buildServicePointNumber(sloidContainer: SloidContainer) {
    return SloidHelper.servicePointSloidToNumber(sloidContainer.rootSloid).toString();
  }

  static getRootSloid(sloidLocationModel: SloidLocationModel) {
    return sloidLocationModel.sloid!.split(':').slice(0, 4).join(':');
  }

  static getParentSloid(sloidLocationModel: SloidLocationModel) {
    return sloidLocationModel.sloid!.split(':').slice(0, 6).join(':');
  }

  static buildSloidContainer(model: SloidLocationModel): SloidContainer {
    return {
      rootSloid: this.getRootSloid(model),
      parentSloid: this.getParentSloid(model),
      sloid: model.sloid!,
    };
  }
}
