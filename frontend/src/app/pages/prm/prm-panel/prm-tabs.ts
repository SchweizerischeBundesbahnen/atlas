import { Pages } from '../../pages';

export const PrmTabs = {
  STOP_POINT: {
    link: Pages.PRM_STOP_POINT_TAB.path,
    title: Pages.PRM_STOP_POINT_TAB.title,
  },
  REFERENCE_POINT: {
    link: 'reference-points',
    title: 'PRM.TABS.REFERENCE_POINT',
  },
  PLATFORM: {
    link: 'platforms',
    title: 'PRM.TABS.PLATFORM',
  },

  CONTACT_POINT: {
    link: 'contact-points',
    title: 'PRM.TABS.CONTACT_POINT',
  },

  TOILET: {
    link: 'toilets',
    title: 'PRM.TABS.TOILET',
  },
  PARKING_LOT: {
    link: 'parking-lots',
    title: 'PRM.TABS.PARKING_LOT',
  },
  CONNECTION: {
    link: 'connections',
    title: 'PRM.TABS.CONNECTION',
  },
};
export const PRM_TABS = [
  PrmTabs.STOP_POINT,
  PrmTabs.REFERENCE_POINT,
  PrmTabs.PLATFORM,
  PrmTabs.CONTACT_POINT,
  PrmTabs.TOILET,
  PrmTabs.PARKING_LOT,
  PrmTabs.CONNECTION,
];

export const PRM_REDUCED_TABS = [
  PrmTabs.STOP_POINT,
  PrmTabs.PLATFORM,
  PrmTabs.CONTACT_POINT,
  PrmTabs.TOILET,
  PrmTabs.PARKING_LOT,
];

export const PRM_COMPLETE_TABS = [PrmTabs.REFERENCE_POINT, PrmTabs.CONNECTION];
