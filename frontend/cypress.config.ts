import {defineConfig} from 'cypress';
import cypress_failed_log from 'cypress-failed-log/src/failed';
import cypress_high_resolution from 'cypress-high-resolution';

export default defineConfig({
  videosFolder: 'cypress/videos',
  screenshotsFolder: 'cypress/screenshots',
  fixturesFolder: 'cypress/fixtures',
  viewportWidth: 1920,
  viewportHeight: 1080,
  videoCompression: false,
  defaultCommandTimeout: 60000,
  execTimeout: 60000,
  pageLoadTimeout: 60000,
  requestTimeout: 60000,
  responseTimeout: 60000,
  video: true,
  env: {
    resolution: 'high'
  },
  e2e: {
    // We've imported your old cypress plugins here.
    // You may want to clean this up later by importing these.
    setupNodeEvents(on, config) {
      on('task', {
        failed: cypress_failed_log()
      });
      cypress_high_resolution(on, config);
    },
    baseUrl: 'http://localhost:4200'
  }
});
