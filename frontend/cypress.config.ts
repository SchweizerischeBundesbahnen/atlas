import { defineConfig } from 'cypress';
import cypress_failed_log from 'cypress-failed-log/src/failed';
import cypress_high_resolution from 'cypress-high-resolution';

export default defineConfig({
  videosFolder: 'cypress/test-results/videos',
  screenshotsFolder: 'cypress/test-results/screenshots',
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
  reporter: 'cypress-mochawesome-reporter',
  reporterOptions: {
    reportDir: 'cypress/test-results/reports',
    charts: true,
    reportPageTitle: 'Atlas E2E Tests',
    embeddedScreenshots: true,
    inlineAssets: true,
    saveAllAttempts: false,
    debug: true,
    saveJson: true
  },

  env: {
    resolution: 'high',
  },
  e2e: {
    // We've imported your old cypress plugins here.
    // You may want to clean this up later by importing these.
    async setupNodeEvents(on, config) {
      on('task', {
        failed: cypress_failed_log(),
      });
      cypress_high_resolution(on, config);
      require('cypress-mochawesome-reporter/plugin')(on);
      return config;
    },
    baseUrl: 'http://localhost:4200',
    scrollBehavior: 'center'
  },
});
