import { Map } from 'maplibre-gl';
import { ServicePointIconType } from './service-point-icon-type';
import { TrafficPointElementType } from '../../../api';

export interface MapIcon {
  id: string;
  icon: HTMLImageElement;
}

export class MapIconsService {
  private static IMAGES_BASE_PATH = '../../../../assets/images/';
  private static SERVICE_POINT_ICONS_BASE_PATH = this.IMAGES_BASE_PATH + 'service-point-symbols/';
  private static TRAFFIC_POINT_ICONS_BASE_PATH = this.IMAGES_BASE_PATH + 'traffic-point-symbols/';

  static addTrafficPointIconToMap(map: Map) {
    MapIconsService.getTrafficPointIconsAsImages().then((icons) => {
      icons.forEach((icon) => {
        if (!map.hasImage(icon.id)) {
          map.addImage(icon.id, icon.icon);
        }
      });
    });
  }

  static getTrafficPointIconsAsImages() {
    const types = ['BOARDING_PLATFORM', 'BOARDING_AREA', 'SELECTED_TP_INDICATOR'].map((type) =>
      this.getIconAsImage(this.TRAFFIC_POINT_ICONS_BASE_PATH, type),
    );
    return Promise.all(types);
  }

  static getServicePointIconsAsImages() {
    const types = Object.keys(ServicePointIconType).map((type) =>
      this.getIconAsImage(this.SERVICE_POINT_ICONS_BASE_PATH, type),
    );
    return Promise.all(types);
  }

  private static getIconAsImage(basePath: string, type: string) {
    return new Promise<MapIcon>((resolve) => {
      const image = new Image(20, 20);
      image.addEventListener('load', () => resolve({ id: type, icon: image }));
      image.src = basePath + type + '.svg';
    });
  }
}
