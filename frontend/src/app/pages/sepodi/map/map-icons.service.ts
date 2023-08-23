export enum ServicePointType {
  OPERATING_POINT_TECHNICAL = 'OPERATING_POINT_TECHNICAL',
  STOP_POINT_AND_FREIGHT_SERVICE_POINT = 'STOP_POINT_AND_FREIGHT_SERVICE_POINT',
  STOP_POINT = 'STOP_POINT',
  FREIGHT_SERVICE_POINT = 'FREIGHT_SERVICE_POINT',
  SERVICE_POINT = 'SERVICE_POINT',
}

interface MapIcon {
  id: string;
  icon: HTMLImageElement;
}

export class MapIconsService {
  private static ICONS_BASE_PATH = '../../../../assets/images/service-point-symbols/';

  static getIconsAsImages() {
    const types = Object.keys(ServicePointType).map((type) => this.getIconAsImage(type));
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
