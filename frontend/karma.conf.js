// Karma configuration file, see link for more information
// https://karma-runner.github.io/1.0/config/configuration-file.html
process.env.CHROMIUM_BIN = require('@playwright/test').chromium.executablePath();
process.env.CHROME_BIN = require('puppeteer').executablePath();

module.exports = function (config) {
  process.env.CHROMIUM_BIN = require('@playwright/test').chromium.executablePath();
  process.env.CHROME_BIN = require('puppeteer').executablePath();
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
      // leave Jasmine Spec Runner output visible in browser
      clearContext: false,
    },
    jasmineHtmlReporter: {
      // removes the duplicated traces
      suppressAll: true,
    },
    angularCLI: {
      config: './angular.json',
      environment: 'dev',
    },
    sonarqubeReporter: {
      basePath: require('path').join(__dirname, './src'),
      outputFolder: require('path').join(__dirname, './coverage/atlas-frontend'),
      reportName: (_metadata) => 'sonarqube.xml',
    },
    coverageReporter: {
      dir: require('path').join(__dirname, './coverage/atlas-frontend'),
      subdir: '.',
      reporters: [
        {type: 'html'},
        {type: 'text-summary'},
        {type: 'lcovonly'},
        {type: 'cobertura'},
      ],
    },
    browserConsoleLogOptions: {level: 'error'},
    browsers: ['Chrome'],
    customLaunchers: {
      ChromeHeadlessNoSandbox: {
        base: "Chrome",
        flags: ["--no-sandbox"],
        displayName: "ChromeHeadlessNoSandbox"
      }
    },
    reporters: ['progress', 'kjhtml'],
    port: 9876,
    colors: true,
    logLevel: config.LOG_ERROR,
    autoWatch: true,
    singleRun: false,
    restartOnFileChange: true,
  });
};
