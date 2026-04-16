import { describe, expect, it } from 'vitest';
import { ServicePointNavigationHelper } from './service-point-navigation.helper';
import { SloidLocationModel } from '../../api/model/sloidLocationModel';
import { Pages } from '../../pages/pages';

describe('Service Points Navigation Helper', () => {
  interface Navigation {
    sloidType: SloidLocationModel.SloidTypeEnum;
    navigationPath: string;
  }
  describe('PRM Navigation', () => {
    const prmNavigation: Navigation[] = [
      { sloidType: 'TOILET', navigationPath: Pages.TOILET.path },
      { sloidType: 'PARKING_LOT', navigationPath: Pages.PARKING_LOT.path },
      { sloidType: 'CONTACT_POINT', navigationPath: Pages.CONTACT_POINT.path },
      { sloidType: 'PLATFORM', navigationPath: Pages.PLATFORMS.path },
    ];
    it('Should build only root Navigation commands', () => {
      //given
      const location: SloidLocationModel = {
        sloid: 'ch:1:sloid:1',
        sloidType: 'SERVICE_POINT',
      };
      //when
      const result = ServicePointNavigationHelper.buildPrmNavigation(location, Pages.STOP_POINTS.path);
      //then
      expect(result).toEqual(['stop-points', 'ch:1:sloid:1']);
    });

    prmNavigation.forEach((navigation) => {
      it('Should build ' + navigation.sloidType + ' Navigation commands', () => {
        //given
        const location: SloidLocationModel = {
          sloid: 'ch:1:sloid:1:1',
          sloidType: navigation.sloidType,
        };
        //when
        const result = ServicePointNavigationHelper.buildPrmNavigation(location, Pages.STOP_POINTS.path);
        //then
        expect(result).toEqual(['stop-points', 'ch:1:sloid:1', navigation.navigationPath, 'ch:1:sloid:1:1', 'detail']);
      });
    });

    it('Should build referencePoint  Navigation commands', () => {
      //given
      const location: SloidLocationModel = {
        sloid: 'ch:1:sloid:1:1',
        sloidType: 'REFERENCE_POINT',
      };
      //when
      const result = ServicePointNavigationHelper.buildPrmNavigation(location, Pages.STOP_POINTS.path);
      //then
      expect(result).toEqual(['stop-points', 'ch:1:sloid:1', 'reference-points', 'ch:1:sloid:1:1']);
    });
  });

  describe('SePoDi Navigation', () => {
    it('Should build root servicePoint Navigation commands', () => {
      //given
      const location: SloidLocationModel = {
        sloid: 'ch:1:sloid:1',
        sloidType: 'SERVICE_POINT',
      };
      //when
      const result = ServicePointNavigationHelper.buildSepodiNavigation(location, Pages.SERVICE_POINTS.path);
      //then
      expect(result).toEqual(['service-points', '8500001']);
    });
    const sepodiNavigationAreaPlatform: Navigation[] = [
      {
        sloidType: 'AREA',
        navigationPath: Pages.TRAFFIC_POINT_ELEMENTS_AREA.path,
      },
      {
        sloidType: 'PLATFORM',
        navigationPath: Pages.TRAFFIC_POINT_ELEMENTS_PLATFORM.path,
      },
    ];

    sepodiNavigationAreaPlatform.forEach((navigation: Navigation) => {
      it('Should build' + navigation.sloidType + ' Navigation commands', () => {
        //given
        const location: SloidLocationModel = {
          sloid: 'ch:1:sloid:1:1',
          sloidType: navigation.sloidType,
        };
        //when
        const result = ServicePointNavigationHelper.buildSepodiNavigation(location, Pages.SERVICE_POINTS.path);
        //then
        expect(result).toEqual(['service-points', '8500001', navigation.navigationPath, 'ch:1:sloid:1:1']);
      });
    });

    const sepodiNavigationSector: Navigation[] = [
      {
        sloidType: 'SECTOR',
        navigationPath: Pages.SECTORS.path,
      },
      {
        sloidType: 'SECTOR_GROUP',
        navigationPath: Pages.SECTOR_GROUPS.path,
      },
    ];

    sepodiNavigationSector.forEach((navigation: Navigation) => {
      it('Should build area Navigation commands', () => {
        //given
        const location: SloidLocationModel = {
          sloid: 'ch:1:sloid:1:1:1:1',
          sloidType: navigation.sloidType,
        };
        //when
        const result = ServicePointNavigationHelper.buildSepodiNavigation(location, Pages.SERVICE_POINTS.path);
        //then
        expect(result).toEqual([
          'service-points',
          '8500001',
          Pages.TRAFFIC_POINT_ELEMENTS_PLATFORM.path,
          'ch:1:sloid:1:1:1',
          navigation.navigationPath,
          'ch:1:sloid:1:1:1:1',
        ]);
      });
    });
  });
});
