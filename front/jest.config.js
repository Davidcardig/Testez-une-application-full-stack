module.exports = {
  moduleNameMapper: {
    '@core/(.*)': '<rootDir>/src/app/core/$1',
  },
  preset: 'jest-preset-angular',
  setupFilesAfterEnv: ['<rootDir>/setup-jest.ts'],
  bail: false,
  verbose: false,
  collectCoverage: true,
  coverageDirectory: './coverage/jest',
  testPathIgnorePatterns: ['<rootDir>/node_modules/'],
  coveragePathIgnorePatterns: ['<rootDir>/node_modules/'],
  collectCoverageFrom: [
    'src/app/**/*.ts',
    '!src/app/**/*.spec.ts',
    '!src/app/**/**.module.ts',
    '!src/app/main.ts',
    '!src/app/polyfills.ts',
    '!src/app/environments/**',
  ],
  coverageReporters: [
    'html',
    'text',
    'text-summary',
    'lcov',
    'json',
  ],
  coverageThreshold: {
    global: {
      statements: 80,
      branches: 70,
      functions: 80,
      lines: 80,
    },
  },
  roots: [
    "<rootDir>"
  ],
  modulePaths: [
    "<rootDir>"
  ],
  moduleDirectories: [
    "node_modules"
  ],
};
