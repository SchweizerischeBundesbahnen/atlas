import { Map } from 'maplibre-gl';
import { ServicePointIconType } from './service-point-icon-type';

export interface MapIcon {
  id: string;
  icon: HTMLImageElement;
}

export class MapIconsService {
  private static ICONS_BASE_PATH = '../../../../assets/images/service-point-symbols/';

  static getIconsAsImages() {
    const types = Object.keys(ServicePointIconType).map((type) => this.getIconAsImage(type));
    return Promise.all(types);
  }

private static getIconAsImage(type: string) {
  return new Promise<MapIcon>((resolve) => {
    const image = new Image(20, 20);
    image.addEventListener('load', () => resolve({ id: type, icon: image }));
    image.src = this.ICONS_BASE_PATH + type + '.svg';
  });
}
}
