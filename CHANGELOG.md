# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Users can define delegated methods to model complex behaviour
- Models from the SMT solver are recorded and not reused
- Support for arrays
- DEEPSEA now prints a header with version information

### Changed
- Unimplemented instructions now throw an exception
- Path conditions are handled more intelligently, support "additional" conditions

### Fixed
- Issue with build.gradle that caused CI to fail.

## [0.0.1] - 2017-09-09

First version after switching to gradle.
