// Karma configuration file, see link for more information
// https://karma-runner.github.io/1.0/config/configuration-file.html

// Jenkins has a BUILD_NUMBER environment variable. So when
// this variable is set, we use the chromium browser downloaded
// by puppeteer.
if (process.env.BUILD_NUMBER) {
  process.env.CHROME_BIN = require('puppeteer').executablePath();
}

module.exports = function (config) {
  config.set({
    basePath: '',
    frameworks: ['jasmine', '@angular-devkit/build-angular'],
    plugins: [
      require('karma-jasmine'),
      require('karma-chrome-launcher'),
      require('karma-browserstack-launcher'),
      require('karma-jasmine-html-reporter'),
      require('karma-sonarqube-reporter'),
      require('karma-coverage'),
      require('@angular-devkit/build-angular/plugins/karma'),
    ],
    client: {
      jasmine: {
        // you can add configuration options for Jasmine here
        // the possible options are listed at https://jasmine.github.io/api/edge/Configuration.html
        // for example, you can disable the random execution with `random: false`
        // or set a specific seed with `seed: 4321`
      },
      clearContext: false, // leave Jasmine Spec Runner output visible in browser
    },
    jasmineHtmlReporter: {
      suppressAll: true, // removes the duplicated traces
    },
    sonarqubeReporter: {
      basePath: require('path').join(__dirname, './src'),
      outputFolder: require('path').join(__dirname, './coverage/esta-cloud-angular'),
      reportName: (_metadata) => 'sonarqube.xml',
    },
    coverageReporter: {
      dir: require('path').join(__dirname, './coverage/esta-cloud-angular'),
      subdir: '.',
      reporters: [
        { type: 'html' },
        { type: 'text-summary' },
        { type: 'lcovonly' },
        { type: 'cobertura' },
      ],
    },
    reporters: ['progress', 'kjhtml'],
    port: 9876,
    colors: true,
    logLevel: config.LOG_INFO,
    autoWatch: true,
    browsers: ['Chrome'],
    customLaunchers: {
      // See https://www.browserstack.com/automate/capabilities
      // To use these browsers add the key to angular.json under test => browsers
      // (Look for ChromeHeadless for an example)
      BsChrome: {
        base: 'BrowserStack',
        browser: 'chrome',
        os: 'OS X',
      },
    },
    singleRun: false,
    restartOnFileChange: true,
  });
};
